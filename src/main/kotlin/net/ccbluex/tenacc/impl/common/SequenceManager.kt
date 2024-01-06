package net.ccbluex.tenacc.impl.common

import net.ccbluex.tenacc.api.common.TACCEvent
import java.util.BitSet
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.CopyOnWriteArraySet

class SequenceManager {
    private val registeredSequences = CopyOnWriteArraySet<ManagedSequence>()
    val fencePermissions = ConcurrentHashMap<Int, Int>()

    fun registerSequence(sequence: ManagedSequence) {
        this.registeredSequences.add(sequence)
    }

    fun unregisterSequence(sequence: ManagedSequence) {
        this.registeredSequences.remove(sequence)
    }

    fun onEvent(event: TACCEvent) {
        if (event is FencePermitEvent) {
            event.permissions.forEach {
                this.fencePermissions.compute(it) { k, v -> (v ?: 0) + 1 }
            }
        }

        this.registeredSequences.forEach { it.onEvent(event) }
    }

    /**
     * Tries to pass a fence. If the fence was successfully passed, it is closed.
     *
     * @return true if the fence was passed
     */
    fun tryPassFence(id: Int): Boolean {
        var allowedPassages = 0

        this.fencePermissions.compute(id) { _, passages ->
            allowedPassages = passages ?: 0

            allowedPassages.coerceAtLeast(1) - 1
        }

        return allowedPassages > 0
    }

    fun reset() {
        this.cancelAll()

        this.fencePermissions.clear()
    }

    fun cancelAll() {
        ArrayList(this.registeredSequences).forEach {
            it.cancel()
        }
    }

}

interface ManagedSequence {
    fun onEvent(event: TACCEvent)
    fun cancel()
}