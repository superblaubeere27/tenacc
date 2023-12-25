package net.ccbluex.tenacc.impl.network.packets

import net.ccbluex.tenacc.api.errors.ClientErrorException
import net.ccbluex.tenacc.impl.network.ClientIntegrationTestNetworkingConstants
import net.ccbluex.tenacc.impl.server.testManager
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs
import net.fabricmc.fabric.api.networking.v1.PacketSender
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking
import net.minecraft.network.PacketByteBuf
import net.minecraft.server.MinecraftServer
import net.minecraft.server.network.ServerPlayNetworkHandler
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.util.Identifier


object ResetTestServerPacket: ServerPlayNetworking.PlayChannelHandler {
    val IDENTIFIER = Identifier(ClientIntegrationTestNetworkingConstants.NAMESPACE, "reset_test")

    fun send(user: ServerPlayerEntity, exitCode: Int = 0, msg: String = "") {
        val buf = PacketByteBufs.create()

        buf.writeInt(exitCode)
        buf.writeString(msg)

        ServerPlayNetworking.send(user, IDENTIFIER, buf)
    }

    override fun receive(
        server: MinecraftServer,
        player: ServerPlayerEntity?,
        handler: ServerPlayNetworkHandler,
        buf: PacketByteBuf,
        responseSender: PacketSender
    ) {
        val exitCode = buf.readInt()
        val message = buf.readString()

        if (exitCode == 0) {
            server.testManager.reset()
        } else {
            server.testManager.failTestError(ClientErrorException(message), false)
        }
    }

}