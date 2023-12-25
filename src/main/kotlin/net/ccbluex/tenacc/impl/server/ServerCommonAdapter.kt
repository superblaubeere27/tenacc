package net.ccbluex.tenacc.impl.server

import net.ccbluex.tenacc.api.common.CITCommonAdapter
import net.ccbluex.tenacc.api.common.CITestSequence
import net.ccbluex.tenacc.utils.chat

class ServerCommonAdapter(private val serverTestManager: ServerTestManager): CITCommonAdapter {
    override fun logServer(s: String) {
        serverTestManager.runningTest!!.player.chat("§7[LOG SERVER] §r$s")
    }

    override fun startSequence(fn: suspend CITestSequence.() -> Unit) {
        ServerTestSequence(serverTestManager.sequenceManager, serverTestManager.networkManager, serverTestManager, fn).run()
    }
}