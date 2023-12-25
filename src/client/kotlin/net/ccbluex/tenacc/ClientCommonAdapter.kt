package net.ccbluex.tenacc

import net.ccbluex.tenacc.api.common.CITCommonAdapter
import net.ccbluex.tenacc.api.common.CITestSequence
import net.ccbluex.tenacc.network.ClientNetworkManager

object ClientCommonAdapter: CITCommonAdapter {
    override fun logServer(s: String) {
    }

    override fun startSequence(fn: suspend CITestSequence.() -> Unit) {
        val seq = ClientTestSequence(ClientTestManager.sequenceManager, ClientNetworkManager) {
            fn()

            ClientTestManager.reset()
        }

        seq.run()
    }
}