package net.ccbluex.tenacc.impl.server

import net.ccbluex.tenacc.api.common.CITCommonAdapter
import net.ccbluex.tenacc.api.common.CITestSequence
import net.ccbluex.tenacc.api.errors.TestTimeoutException
import net.ccbluex.tenacc.utils.chat

class ServerCommonAdapter(private val serverTestManager: ServerTestManager): CITCommonAdapter {
    override fun logServer(s: String) {
        serverTestManager.runningTest!!.player.chat("§7[LOG SERVER] §r$s")
    }

    override fun startSequence(fn: suspend CITestSequence.() -> Unit) {
        val seq = ServerTestSequence(
            serverTestManager.sequenceManager,
            serverTestManager.networkManager,
            serverTestManager
        ) {
            try {
                fn()
            } catch (e: Exception) {
                serverTestManager.failTestError(e, true)
            } finally {
                serverTestManager.reset()
            }
        }

        seq.run()

        val timeoutTicks = serverTestManager.runningTest!!.test.annotation.timeout

        if (timeoutTicks <= 0)
            return

        val timeoutSeq = ServerTestSequence(
            serverTestManager.sequenceManager,
            serverTestManager.networkManager,
            serverTestManager
        ) {
            waitTicks(timeoutTicks)

            serverTestManager.failTestError(TestTimeoutException(), true)
        }

        timeoutSeq.run()
    }
}