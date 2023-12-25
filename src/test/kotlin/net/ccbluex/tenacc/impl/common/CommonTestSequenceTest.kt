package net.ccbluex.tenacc.impl.common

import net.ccbluex.tenacc.api.client.CITClientAdapter
import net.ccbluex.tenacc.api.common.CITestSequence
import net.ccbluex.tenacc.api.server.CITServerAdapter
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class CommonTestSequenceTest {
    companion object {
        var sequenceManager = SequenceManager()
    }

    @BeforeEach
    fun resetSequenceManager() {
        sequenceManager = SequenceManager()
    }

    @Test
    fun testCoroutineRun() {
        var ran = false

        DummyTestSequence {
            ran = true
        }

        assertTrue(ran)
    }

    @Test
    fun testSync() {
        var nTimesRan = 0

        val seq = DummyTestSequence {
            nTimesRan += 1

            sync()

            nTimesRan += 1

            sync()

            nTimesRan += 1
        }

        assertEquals(1, nTimesRan)
        seq.onEvent(TickEvent())
        assertEquals(2, nTimesRan)
        seq.onEvent(TickEvent())
        assertEquals(3, nTimesRan)
        seq.onEvent(TickEvent())
    }

    @Test
    fun testWaitTicks() {
        var nTimesRan = 0

        val seq = DummyTestSequence {
            nTimesRan += 1

            waitTicks(0)

            nTimesRan += 1

            waitTicks(1)

            nTimesRan += 1

            waitTicks(5)
            nTimesRan += 1
        }

        assertEquals(2, nTimesRan)
        seq.onEvent(TickEvent())

        for (i in 0 until 5) {
            assertEquals(3, nTimesRan)

            seq.onEvent(TickEvent())
        }

        assertEquals(4, nTimesRan)
        seq.onEvent(TickEvent())
    }

    @Test
    fun testWaitTicksBug() {
        var nTimesRan = 0

        val seq = DummyTestSequence {
            nTimesRan += 1

            waitTicks(5)

            nTimesRan += 1
        }

        for (i in 0 until 5) {
            assertEquals(1, nTimesRan)

            seq.onEvent(TickEvent())
        }

        assertEquals(2, nTimesRan)
    }

    @Test
    fun testWaitUntil() {
        var nTimesRan = 0
        var shouldCancel = false
        var cancellationReason: Boolean? = null

        val seq = DummyTestSequence {
            nTimesRan += 1

            cancellationReason = waitUntil { shouldCancel }

            nTimesRan += 1
        }

        for (i in 0 until 100) {
            assertEquals(1, nTimesRan)

            seq.onEvent(TickEvent())
        }

        shouldCancel = true

        seq.onEvent(TickEvent())

        assertEquals(2, nTimesRan)
        assertEquals(true, cancellationReason)
        seq.onEvent(TickEvent())
    }
    @Test
    fun testFenceSelfPermit() {
        var nTimesRan = 0

        val seq = DummyTestSequence {
            nTimesRan += 1

            permitFencePassage(0)

            waitForFencePassage(0)

            nTimesRan += 1

            permitFencePassage(0)
            permitFencePassage(1)

            waitForFencePassage(1, 0)

            nTimesRan += 1
        }

        assertEquals(3, nTimesRan)
    }

    @Test
    fun testFencePassage() {
        var nTimesRan = 0

        val seq = DummyTestSequence {
            nTimesRan += 1

            permitFencePassage(0)

            waitForFencePassage(0)

            nTimesRan += 1

            permitFencePassage(0)

            waitForFencePassage(1, 0)

            nTimesRan += 1

            waitForFencePassage(1)

            nTimesRan += 1
        }

        assertEquals(2, nTimesRan)

        sequenceManager.onEvent(FencePermitEvent(listOf(0)))

        assertEquals(2, nTimesRan)

        seq.onEvent(TickEvent())

        assertEquals(2, nTimesRan)

        sequenceManager.onEvent(FencePermitEvent(listOf(1)))
        assertEquals(2, nTimesRan)
        seq.onEvent(TickEvent())
        assertEquals(3, nTimesRan)
        seq.onEvent(TickEvent())
        assertEquals(3, nTimesRan)
        sequenceManager.onEvent(FencePermitEvent(listOf(1)))
        seq.onEvent(TickEvent())
        assertEquals(4, nTimesRan)
    }

    @Test
    fun testFencePassageMulti() {
        var nTimesRan = 0

        val seq = DummyTestSequence {
            nTimesRan += 1

            waitForFencePassage(0, 1, 2, 3, 4, 5)

            nTimesRan += 1

            waitForFencePassage(3)

            nTimesRan += 1
        }

        val permitDelay = listOf(
            Pair(100, listOf(5, 4)),
            Pair(100, listOf(2)),
            Pair(0, listOf(3)),
            Pair(50, listOf(0, 1)),
            Pair(0, listOf(2, 3)),
        )

        assertEquals(1, nTimesRan)

        for ((delay, fenceIds) in permitDelay) {
            for (i in 0 until delay) {
                seq.onEvent(TickEvent())
            }

            assertEquals(1, nTimesRan)
            sequenceManager.onEvent(FencePermitEvent(fenceIds))
            assertEquals(1, nTimesRan)
        }

        seq.onEvent(TickEvent())

        assertEquals(3, nTimesRan)

    }

    @Test
    fun testWaitUntilMaxTicks() {
        var nTimesRan = 0
        var shouldCancel = false
        var firstCancellationReason: Boolean? = null
        var cancellationReason: Boolean? = null

        val seq = DummyTestSequence {
            nTimesRan += 1

            firstCancellationReason = waitUntil(maxTicks = 0) { shouldCancel }

            nTimesRan += 1

            firstCancellationReason = waitUntil(maxTicks = 1) { shouldCancel }

            nTimesRan += 1

            firstCancellationReason = waitUntil(maxTicks = 1) { shouldCancel }

            nTimesRan += 1

            cancellationReason = waitUntil(maxTicks = 5) { shouldCancel }

            nTimesRan += 1
        }

        assertEquals(false, firstCancellationReason)
        assertEquals(2, nTimesRan)

        seq.onEvent(TickEvent())

        assertEquals(false, firstCancellationReason)
        assertEquals(3, nTimesRan)

        shouldCancel = true

        seq.onEvent(TickEvent())

        assertEquals(true, firstCancellationReason)
        assertEquals(4, nTimesRan)

        shouldCancel = false

        for (i in 0 until 5) {
            assertEquals(4, nTimesRan)

            seq.onEvent(TickEvent())
        }

        assertEquals(5, nTimesRan)
        assertEquals(false, cancellationReason)

        shouldCancel = true

        seq.onEvent(TickEvent())
    }


    object DummyNetworkHandler : NetworkHandler {
        override fun sendFencePermit(ids: Int) {
            
        }
    }
    
    class DummyTestSequence(handler: SuspendableHandler) : CommonTestSequence(sequenceManager, DummyNetworkHandler, handler) {
        override fun server(fn: CITServerAdapter.() -> Unit) {
            TODO("Not yet implemented")
        }

        override suspend fun serverSequence(fn: suspend CITestSequence.(CITServerAdapter) -> Unit) {
            TODO("Not yet implemented")
        }

        override fun client(fn: CITClientAdapter.() -> Unit) {
            TODO("Not yet implemented")
        }

        override suspend fun clientSequence(fn: suspend CITestSequence.(CITClientAdapter) -> Unit) {
            TODO("Not yet implemented")
        }

    }
}