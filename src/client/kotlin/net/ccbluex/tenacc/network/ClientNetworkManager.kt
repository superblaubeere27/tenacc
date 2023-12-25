package net.ccbluex.tenacc.network

import net.ccbluex.tenacc.impl.common.NetworkHandler
import net.ccbluex.tenacc.impl.network.packets.*
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking

object ClientNetworkManager: NetworkHandler {
    init {
        ClientPlayNetworking.registerGlobalReceiver(FencePermitServerPacket.IDENTIFIER, FencePermitClientPacket)
        ClientPlayNetworking.registerGlobalReceiver(StartTestServerPacket.IDENTIFIER, StartTestClientPacket)
        ClientPlayNetworking.registerGlobalReceiver(ResetTestServerPacket.IDENTIFIER, ResetTestClientPacket)
    }

    override fun sendFencePermit(ids: Int) {
        FencePermitClientPacket.send(ids)
    }

    override fun sendError(exception: Throwable) {
        ResetTestClientPacket.send(1, exception.toString())
    }
}