package net.ccbluex.tenacc

import net.ccbluex.tenacc.api.common.TACCSequenceAdapter
import net.ccbluex.tenacc.api.common.TACCTestSequence
import net.ccbluex.tenacc.api.common.TACCTestVariant
import net.ccbluex.tenacc.api.errors.TestVariantFailException
import net.ccbluex.tenacc.impl.server.*
import net.ccbluex.tenacc.network.ClientNetworkManager
import net.minecraft.client.MinecraftClient

object ClientCommonAdapter: TACCSequenceAdapter {
    override fun logServer(s: String) {
    }

    override fun startSequence(
        variants: Array<TACCTestVariant>,
        fn: suspend TACCTestSequence.() -> Unit
    ) {
        val seq = ClientTestSequence(ClientTestManager.sequenceManager, ClientNetworkManager) {
            if (variants.isEmpty()) {
                sync(CLIENT_SYNC_FENCE_BEFORE_TEST, SERVER_SYNC_FENCE_BEFORE_TEST)

                fn()

                sync(CLIENT_SYNC_FENCE_AFTER_TEST, SERVER_SYNC_FENCE_AFTER_TEST)
            } else {
                for (variant in variants) {
                    waitForPlayerReady()

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

            ClientTestManager.reset()
        }

        seq.run()
    }

    private suspend fun TACCTestSequence.waitForPlayerReady() {
        val mc = MinecraftClient.getInstance()

        // mc.player can change! we have to retrieve it on every loop iteration.
        while (mc.player!!.isDead) {
            waitTicks(1)
        }
    }
}