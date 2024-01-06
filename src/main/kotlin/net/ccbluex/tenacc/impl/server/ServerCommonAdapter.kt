package net.ccbluex.tenacc.impl.server

import net.ccbluex.tenacc.api.common.TACCSequenceAdapter
import net.ccbluex.tenacc.api.common.TACCTestSequence
import net.ccbluex.tenacc.api.common.TACCTestVariant
import net.ccbluex.tenacc.api.errors.TestTimeoutException
import net.ccbluex.tenacc.api.errors.TestVariantFailException
import net.ccbluex.tenacc.utils.chat

class ServerCommonAdapter(private val serverTestManager: ServerTestManager): TACCSequenceAdapter {
    override fun logServer(s: String) {
        serverTestManager.runningTest!!.player.chat("§7[LOG SERVER] §7§o$s")
    }

    override fun startSequence(
        variants: Array<TACCTestVariant>,
        fn: suspend TACCTestSequence.() -> Unit
    ) {
        val seq = ServerTestSequence(
            serverTestManager.sequenceManager,
            serverTestManager.networkManager,
            serverTestManager
        ) {
            try {
                if (variants.isEmpty()) {
                    sync(CLIENT_SYNC_FENCE_BEFORE_TEST, SERVER_SYNC_FENCE_BEFORE_TEST)

                    fn()

                    sync(CLIENT_SYNC_FENCE_AFTER_TEST, SERVER_SYNC_FENCE_AFTER_TEST)
                } else {
                    for (variant in variants) {
                        variant.apply(this)

                        try {
                            sync(CLIENT_SYNC_FENCE_BEFORE_TEST, SERVER_SYNC_FENCE_BEFORE_TEST)

                            fn()

                            sync(CLIENT_SYNC_FENCE_AFTER_TEST, SERVER_SYNC_FENCE_AFTER_TEST)
                        } catch (e: Throwable) {
                            throw TestVariantFailException(variant, e)
                        }
                    }
                }

                serverTestManager.endTest()
            } catch (e: Exception) {
                serverTestManager.failTestError(e, true)
            }
        }

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

        seq.run()
        timeoutSeq.run()
    }
}