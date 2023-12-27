package net.ccbluex.tenacc.api.common

interface TACCSequenceAdapter {
    fun logServer(s: String)
    fun startSequence(
        variants: Array<TACCTestVariant> = TACCTestVariant.DEFAULT_VARIANTS,
        fn: suspend TACCTestSequence.() -> Unit
    )
}