package net.ccbluex.tenacc.impl.network.packets

import net.ccbluex.tenacc.ClientTestManager
import net.ccbluex.tenacc.impl.TestIdentifier
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs
import net.fabricmc.fabric.api.networking.v1.PacketSender
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking
import net.minecraft.client.MinecraftClient
import net.minecraft.client.network.ClientPlayNetworkHandler
import net.minecraft.network.PacketByteBuf
import net.minecraft.server.network.ServerPlayerEntity


object ResetTestClientPacket: ClientPlayNetworking.PlayChannelHandler {

    fun send(exitCode: Int, msg: String) {
        val buf = PacketByteBufs.create()

        buf.writeInt(exitCode)
        buf.writeString(msg)

        ClientPlayNetworking.send(ResetTestServerPacket.IDENTIFIER, buf)
    }


    override fun receive(
        client: MinecraftClient?,
        handler: ClientPlayNetworkHandler?,
        buf: PacketByteBuf,
        responseSender: PacketSender?
    ) {
        val exitCode = buf.readInt()
        val message = buf.readString()

        ClientTestManager.reset()
    }

}