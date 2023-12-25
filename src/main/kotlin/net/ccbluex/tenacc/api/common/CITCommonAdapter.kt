package net.ccbluex.tenacc.api.common

interface CITCommonAdapter {
    fun logServer(s: String)
    fun startSequence(fn: suspend CITestSequence.() -> Unit)
}