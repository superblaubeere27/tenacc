package net.ccbluex.tenacc.impl.common

import net.ccbluex.tenacc.api.common.CIEvent
import kotlin.coroutines.Continuation
import kotlin.coroutines.resume

typealias EventClass = Class<out CIEvent>

/**
 * @param R the return value a continuation will give upon continuation
 */
sealed class ConditionalContinuation<R>(
    val filteredEvent: EventClass
) {
    private var continuation: Continuation<R>? = null

    /**
     * This function is called every time an event occurs that should cause a
     */
    abstract fun tick()

    /**
     * Called after the coroutine was suspended with produced [continuation]
     */
    fun onSuspend(continuation: Continuation<R>) {
        this.continuation = continuation
    }

    protected fun resume(returnValue: R) {
        val continuation = this.continuation ?: throw IllegalStateException("onSuspend() not called yet")

        continuation.resume(returnValue)
    }

    /**
     * @param nTicks waits for that amount of ticks. if `<= 1` it will wait for one tick.
     */
    class WaitTickContinuation(
        filteredEvent: EventClass,
        private val nTicks: Int,
    ) : ConditionalContinuation<Unit>(filteredEvent) {
        private var elapsedTicks = 0

        override fun tick() {
            if (++this.elapsedTicks >= this.nTicks) {
                resume(Unit)
            }
        }
    }

    /**
     * Waits for the condition to pass. Returns `true` if the reason of the continuation was the condition passing.
     *
     * @param nMaxTicks if not null, it will only wait at most the specified amount of ticks
     */
    class WaitConditionContinuation(
        filteredEvent: EventClass,
        private val condition: () -> Boolean,
        private val nMaxTicks: Int?,
    ) : ConditionalContinuation<Boolean>(filteredEvent) {
        private var elapsedTicks = 0

        override fun tick() {
            if (this.condition()) {
                resume(true)

                return
            }

            if (this.nMaxTicks != null && ++this.elapsedTicks >= this.nMaxTicks) {
                resume(false)
            }
        }
    }

    /**
     * Waits until all fence ids are passed.
     */
    class WaitForFencePassageContinuation(
        filteredEvent: EventClass,
        private val sequenceManager: SequenceManager,
        ids: List<Int>,
    ) : ConditionalContinuation<Unit>(filteredEvent) {
        private val passagesLeft = ArrayList<Int>(ids)

        override fun tick() {
            val successfulPassages = ArrayList<Int>()

            passagesLeft.forEach { id ->
                if (this.sequenceManager.tryPassFence(id)) {
                    successfulPassages.add(id)
                }
            }

            this.passagesLeft.removeAll(successfulPassages)

            if (passagesLeft.isEmpty()) {
                resume(Unit)
            }
        }
    }
}