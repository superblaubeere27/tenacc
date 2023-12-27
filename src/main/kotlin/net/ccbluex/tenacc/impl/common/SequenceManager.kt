package net.ccbluex.tenacc.impl.common

import net.ccbluex.tenacc.api.common.TACCEvent
import java.util.BitSet
import java.util.concurrent.CopyOnWriteArraySet

class SequenceManager {
    private val registeredSequences = CopyOnWriteArraySet<ManagedSequence>()
    private val fencePermissions = BitSet()

    fun registerSequence(sequence: ManagedSequence) {
        this.registeredSequences.add(sequence)
    }

    fun unregisterSequence(sequence: ManagedSequence) {
        this.registeredSequences.remove(sequence)
    }

    fun onEvent(event: TACCEvent) {
        if (event is FencePermitEvent) {
            synchronized(this.fencePermissions) {
                event.permissions.forEach { this.fencePermissions.set(it) }
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
        synchronized(this.fencePermissions) {
            if (!this.fencePermissions.get(id)) {
                return false
            }

            this.fencePermissions.clear(id)
        }

        return true
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