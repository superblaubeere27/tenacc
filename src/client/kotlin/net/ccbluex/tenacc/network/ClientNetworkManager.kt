package net.ccbluex.tenacc.network

import net.ccbluex.tenacc.impl.common.NetworkHandler
import net.ccbluex.tenacc.impl.network.packets.FencePermitClientPacket
import net.ccbluex.tenacc.impl.network.packets.FencePermitServerPacket
import net.ccbluex.tenacc.impl.network.packets.StartTestClientPacket
import net.ccbluex.tenacc.impl.network.packets.StartTestServerPacket
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking

object ClientNetworkManager: NetworkHandler {
    init {
        ClientPlayNetworking.registerGlobalReceiver(FencePermitServerPacket.IDENTIFIER, FencePermitClientPacket)
        ClientPlayNetworking.registerGlobalReceiver(StartTestServerPacket.IDENTIFIER, StartTestClientPacket)
    }

    override fun sendFencePermit(ids: Int) {
        FencePermitClientPacket.send(ids)
    }
}