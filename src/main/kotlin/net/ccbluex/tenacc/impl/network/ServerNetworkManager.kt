package net.ccbluex.tenacc.impl.network

import net.ccbluex.tenacc.impl.common.NetworkHandler
import net.ccbluex.tenacc.impl.network.packets.FencePermitServerPacket
import net.ccbluex.tenacc.impl.network.packets.ResetTestServerPacket
import net.ccbluex.tenacc.impl.network.packets.StartTestServerPacket
import net.ccbluex.tenacc.impl.server.ServerRunningTestContext
import net.ccbluex.tenacc.impl.server.ServerTestManager
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking

class ServerNetworkManager(
    private val testManager: ServerTestManager
): NetworkHandler {

    init {
        ServerPlayNetworking.registerGlobalReceiver(FencePermitServerPacket.IDENTIFIER, FencePermitServerPacket)
        ServerPlayNetworking.registerGlobalReceiver(ResetTestServerPacket.IDENTIFIER, ResetTestServerPacket)
    }

    override fun sendFencePermit(ids: Int) {
        val testContext = testManager.runningTest!!

        FencePermitServerPacket.send(testContext.player, ids)
    }

    override fun sendError(e: Throwable) {
        val testContext = testManager.runningTest!!

        ResetTestServerPacket.send(testContext.player, 1, e.toString())
    }

    fun sendTestStart(testContext: ServerRunningTestContext) {
        StartTestServerPacket.send(testContext.player, testContext.test.identifier, testContext.templateInfo)
    }

    fun sendTestEnd() {
        val testContext = testManager.runningTest!!

        ResetTestServerPacket.send(testContext.player, 0, "Test finished.")
    }
}