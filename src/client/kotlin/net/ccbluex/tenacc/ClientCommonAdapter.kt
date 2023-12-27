package net.ccbluex.tenacc

import net.ccbluex.tenacc.api.common.TACCSequenceAdapter
import net.ccbluex.tenacc.api.common.TACCTestSequence
import net.ccbluex.tenacc.api.common.TACCTestVariant
import net.ccbluex.tenacc.api.errors.TestVariantFailException
import net.ccbluex.tenacc.network.ClientNetworkManager

object ClientCommonAdapter: TACCSequenceAdapter {
    override fun logServer(s: String) {
    }

    override fun startSequence(
        variants: Array<TACCTestVariant>,
        fn: suspend TACCTestSequence.() -> Unit
    ) {
        val seq = ClientTestSequence(ClientTestManager.sequenceManager, ClientNetworkManager) {
            try {
                if (variants.isEmpty()) {
                    fn()
                } else {
                    for (variant in variants) {
                        variant.apply(this)

                        try {
                            sync()

                            fn()
                        } catch (e: Throwable) {
                            throw TestVariantFailException(variant, e)
                        }
                    }
                }
            } finally {
                ClientTestManager.reset()
            }
        }

        seq.run()
    }
}