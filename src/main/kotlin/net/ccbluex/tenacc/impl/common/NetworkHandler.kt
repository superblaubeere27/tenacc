package net.ccbluex.tenacc.impl.common

interface NetworkHandler {
    fun sendFencePermit(ids: Int)
    fun sendError(e: Throwable)
}