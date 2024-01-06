package net.ccbluex.tenacc.network

import net.ccbluex.tenacc.ClientTestManager
import net.ccbluex.tenacc.impl.common.NetworkHandler
import net.ccbluex.tenacc.impl.network.packets.*
import net.ccbluex.tenacc.impl.server.testManager
import net.ccbluex.tenacc.utils.TestErrorFormatter
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking

object ClientNetworkManager: NetworkHandler {
    init {
        ClientPlayNetworking.registerGlobalReceiver(FencePermitServerPacket.IDENTIFIER, FencePermitClientPacket)
        ClientPlayNetworking.registerGlobalReceiver(StartTestServerPacket.IDENTIFIER, StartTestClientPacket)
        ClientPlayNetworking.registerGlobalReceiver(ResetTestServerPacket.IDENTIFIER, ResetTestClientPacket)
    }

    override fun sendFencePermit(ids: Int) {
        println("CLIENT PERMIT SENT: $ids")

        FencePermitClientPacket.send(ids)
    }

    override fun sendError(exception: Throwable) {
        val testFn = ClientTestManager.currentTestContext

        testFn!!

        ResetTestClientPacket.send(1, TestErrorFormatter.formatError(exception, ClientTestManager.findTestById(testFn.testIdentifier)!!))
    }
}