package net.ccbluex.tenacc.impl.network.packets

import net.ccbluex.tenacc.impl.common.FencePermitEvent
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


object FencePermitServerPacket: ServerPlayNetworking.PlayChannelHandler {
    val IDENTIFIER = Identifier(ClientIntegrationTestNetworkingConstants.NAMESPACE, "fence_permit")

    fun send(user: ServerPlayerEntity, permission: Int) {
        val buf = PacketByteBufs.create()

        buf.writeVarInt(permission)

        ServerPlayNetworking.send(user, IDENTIFIER, buf)
    }

    override fun receive(
        server: MinecraftServer,
        player: ServerPlayerEntity?,
        handler: ServerPlayNetworkHandler,
        buf: PacketByteBuf,
        responseSender: PacketSender
    ) {
        val perm = buf.readVarInt()

        server.testManager.sequenceManager.onEvent(FencePermitEvent(listOf(perm)))

        println("SERVER PERMIT RECV: $perm (${server.testManager.sequenceManager.fencePermissions})")
    }


}