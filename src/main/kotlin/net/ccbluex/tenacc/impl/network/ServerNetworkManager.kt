package net.ccbluex.tenacc.impl.network

import net.ccbluex.tenacc.impl.common.NetworkHandler
import net.ccbluex.tenacc.impl.network.packets.FencePermitServerPacket
import net.ccbluex.tenacc.impl.network.packets.StartTestServerPacket
import net.ccbluex.tenacc.impl.server.ServerRunningTestContext
import net.ccbluex.tenacc.impl.server.ServerTestManager
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking

class ServerNetworkManager(
    private val testManager: ServerTestManager
): NetworkHandler {

    init {
        ServerPlayNetworking.registerGlobalReceiver(FencePermitServerPacket.IDENTIFIER, FencePermitServerPacket)
    }

    override fun sendFencePermit(ids: Int) {
        val testContext = testManager.runningTest!!

        FencePermitServerPacket.send(testContext.player, ids)
    }

    fun sendTestStart(testContext: ServerRunningTestContext) {
        StartTestServerPacket.send(testContext.player, testContext.test.identifier)
    }
}