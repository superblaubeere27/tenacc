package net.ccbluex.tenacc.impl.common

import kotlinx.coroutines.*
import net.ccbluex.tenacc.Clientintegrationtest
import net.ccbluex.tenacc.api.common.TACCEvent
import net.ccbluex.tenacc.api.common.TACCTestSequence
import net.ccbluex.tenacc.impl.TestManager
import kotlin.coroutines.suspendCoroutine

typealias SuspendableHandler = suspend CommonTestSequence.() -> Unit

abstract class CommonTestSequence(
    private val sequenceManager: SequenceManager,
    private val networkHandler: NetworkHandler,
    private val handler: SuspendableHandler
) : TACCTestSequence, ManagedSequence {
    private lateinit var coroutine: Job

    override fun cancel() {
        coroutine.cancel()
        continuation = null

        sequenceManager.unregisterSequence(this@CommonTestSequence)
    }

    private var continuation: ConditionalContinuation<*>? = null

    abstract val testManager: TestManager

    fun run() {
        coroutine = GlobalScope.launch(Dispatchers.Unconfined, start = CoroutineStart.LAZY) {
            val sequence = this@CommonTestSequence

            sequenceManager.registerSequence(sequence)

            try {
                runCoroutine()
            } catch (e: Exception) {
                this.cancel("Failed to run coroutine", e)
            } finally {
                continuation = null
                sequenceManager.unregisterSequence(sequence)
            }
        }

        coroutine.start()
    }

    internal open suspend fun runCoroutine() {
        runCatching {
            handler()
        }.onFailure {
            Clientintegrationtest.logger.error("Failed to execute test coroutine", it)

            testManager.failTestError(it, true)
        }
    }

    override fun onEvent(event: TACCEvent) {
        runCatching {
            val currContinuation = this.continuation ?: return

            if (currContinuation.filteredEvent == event.javaClass) {
                currContinuation.tick()
            }
        }.onFailure {
            testManager.failTestError(it, true)
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
    internal suspend fun waitUntilNextTick() {
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

    override suspend fun waitForEitherPassage(vararg fenceIds: Int): Int {
        for (fenceId in fenceIds) {
            if (this.sequenceManager.tryPassFence(fenceId)) {
                return fenceId
            }
        }

        val conditionalContinuation = ConditionalContinuation.WaitForAnyFencePassage(
            TickEvent::class.java,
            this.sequenceManager,
            fenceIds
        )

        return this.wait(conditionalContinuation)
    }

    override fun permitFencePassage(fenceId: Int) {
        this.networkHandler.sendFencePermit(fenceId)

        this.sequenceManager.onEvent(FencePermitEvent(listOf(fenceId)))
    }

}