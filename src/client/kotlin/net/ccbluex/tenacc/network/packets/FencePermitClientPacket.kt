package net.ccbluex.tenacc.impl.network.packets

import net.ccbluex.tenacc.ClientTestManager
import net.ccbluex.tenacc.impl.common.FencePermitEvent
import net.ccbluex.tenacc.impl.server.testManager
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking.PlayChannelHandler
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs
import net.fabricmc.fabric.api.networking.v1.PacketSender
import net.minecraft.client.MinecraftClient
import net.minecraft.client.network.ClientPlayNetworkHandler
import net.minecraft.network.PacketByteBuf


object FencePermitClientPacket: PlayChannelHandler {
    fun send(permission: Int) {
        val buf = PacketByteBufs.create()

        buf.writeVarInt(permission)

        ClientPlayNetworking.send(FencePermitServerPacket.IDENTIFIER, buf)
    }

    override fun receive(
        client: MinecraftClient?,
        handler: ClientPlayNetworkHandler?,
        buf: PacketByteBuf,
        responseSender: PacketSender?
    ) {
        val perm = buf.readVarInt()

        ClientTestManager.sequenceManager.onEvent(FencePermitEvent(listOf(perm)))

        println("CLIENT PERMIT RECV: $perm (${ClientTestManager.sequenceManager.fencePermissions})")
    }


}