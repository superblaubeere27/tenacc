package net.ccbluex.tenacc.api.common

import net.ccbluex.tenacc.api.client.CITClientAdapter
import net.ccbluex.tenacc.api.server.CITServerAdapter

interface CITestSequence {

    /**
     * Checks [predicate] on every tick until it returns true
     *
     * @param maxTicks if not null, it may suspend for at most [maxTicks] ticks. If `<= 0`, this function immediately
     * returns `false`
     *
     * @return based on the resumption reason.`true` if it is due to the [predicate] returning `true`, `false` if it
     * waited more than [maxTicks]
     */
    suspend fun waitUntil(maxTicks: Int? = null, predicate: () -> Boolean): Boolean
    suspend fun waitTicks(ticks: Int)

    suspend fun waitForFencePassage(vararg fenceIds: Int)

    fun permitFencePassage(fenceId: Int = 0)

    fun server(fn: CITServerAdapter.() -> Unit)
    suspend fun serverSequence(fn: suspend CITestSequence.(CITServerAdapter) -> Unit)

    fun client(fn: CITClientAdapter.() -> Unit)
    suspend fun clientSequence(fn: suspend CITestSequence.(CITClientAdapter) -> Unit)

}