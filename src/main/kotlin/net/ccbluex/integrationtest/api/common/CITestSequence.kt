package net.ccbluex.integrationtest.api.common

import net.ccbluex.integrationtest.api.server.CITServerAdapter

class CITestSequence {

    suspend fun waitTicks(ticks: Int) {
        TODO("KICK")
    }

    suspend fun syncFence(fenceId: Int = 0) {
        TODO("SDF")
    }

    fun permitFencePassage(fenceId: Int = 0) {
        TODO("ASD")
    }

    fun server(fn: CITServerAdapter.() -> Unit) {

    }

}