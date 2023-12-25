package net.ccbluex.tenacc.impl.network.packets

import net.ccbluex.tenacc.impl.TestIdentifier
import net.ccbluex.tenacc.impl.network.ClientIntegrationTestNetworkingConstants
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.util.Identifier


object StartTestServerPacket {
    val IDENTIFIER = Identifier(ClientIntegrationTestNetworkingConstants.NAMESPACE, "start_test")

    fun send(user: ServerPlayerEntity, identifier: TestIdentifier) {
        val buf = PacketByteBufs.create()

        buf.writeString(identifier.className, 100)
        buf.writeString(identifier.testName, 100)

        ServerPlayNetworking.send(user, IDENTIFIER, buf)
    }

}