package net.ccbluex.tenacc.impl.common

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import net.ccbluex.tenacc.Clientintegrationtest
import net.ccbluex.tenacc.api.common.CIEvent
import net.ccbluex.tenacc.api.common.CITestSequence
import kotlin.coroutines.suspendCoroutine

typealias SuspendableHandler = suspend CommonTestSequence.() -> Unit

abstract class CommonTestSequence(
    private val sequenceManager: SequenceManager,
    private val networkHandler: NetworkHandler,
    private val handler: SuspendableHandler
) : CITestSequence, ManagedSequence {
    private lateinit var coroutine: Job

    open fun cancel() {
        coroutine.cancel()
        continuation = null

        sequenceManager.unregisterSequence(this@CommonTestSequence)
    }

    private var continuation: ConditionalContinuation<*>? = null

    fun run() {
        coroutine = GlobalScope.launch(Dispatchers.Unconfined) {
            val sequence = this@CommonTestSequence

            sequenceManager.registerSequence(sequence)

            try {
                runCoroutine()
            } finally {
                continuation = null
                sequenceManager.unregisterSequence(sequence)
            }
        }
    }

    internal open suspend fun runCoroutine() {
        runCatching {
            handler()
        }.onFailure {
            Clientintegrationtest.logger.error("Failed to execute test coroutine", it)

            TODO("Fail tests on errors")
        }
    }

    override fun onEvent(event: CIEvent) {
        val currContinuation = this.continuation ?: return

        if (currContinuation.filteredEvent == event.javaClass) {
            currContinuation.tick()
        }
    }

    /**
     * @param maxTicks if `<= 0` this function will not wait at all
     */
    override suspend fun waitUntil(maxTicks: Int?, predicate: () -> Boolean): Boolean {
        if (maxTicks != null && maxTicks <= 0) {
            return predicate()
        }

        val conditionalContinuation = ConditionalContinuation.WaitConditionContinuation(
            TickEvent::class.java,
            condition = predicate,
            maxTicks
        )

        return this.wait(conditionalContinuation)
    }

    /**
     * Waits a fixed amount of ticks before continuing.
     * Re-entry at the game tick.
     */
    override suspend fun waitTicks(ticks: Int) {
        // Don't wait if ticks is 0
        if (ticks == 0) {
            return
        }

        this.wait(ConditionalContinuation.WaitTickContinuation(TickEvent::class.java, ticks))
    }

    private suspend fun <R> wait(conditionalContinuation: ConditionalContinuation<R>): R {
        return suspendCoroutine {
            conditionalContinuation.onSuspend(it)

            this.continuation = conditionalContinuation
        }
    }

    /**
     * Syncs the coroutine to the game tick.
     * It does not matter if we wait 0 or 1 ticks, it will always sync to the next tick.
     */
    internal suspend fun sync() {
        this.wait(ConditionalContinuation.WaitTickContinuation(TickEvent::class.java, 0))
    }


    override suspend fun waitForFencePassage(vararg fenceIds: Int) {
        val passagesLeft = fenceIds.filter { fenceId -> !this.sequenceManager.tryPassFence(fenceId) }

        if (passagesLeft.isEmpty()) {
            return
        }

        val conditionalContinuation = ConditionalContinuation.WaitForFencePassageContinuation(
            TickEvent::class.java,
            this.sequenceManager,
            passagesLeft
        )

        this.wait(conditionalContinuation)
    }

    override fun permitFencePassage(fenceId: Int) {
        this.networkHandler.sendFencePermit(fenceId)

        this.sequenceManager.onEvent(FencePermitEvent(listOf(fenceId)))
    }

}