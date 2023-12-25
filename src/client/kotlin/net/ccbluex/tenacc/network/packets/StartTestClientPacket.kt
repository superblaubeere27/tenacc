package net.ccbluex.tenacc.impl.network.packets

import net.ccbluex.tenacc.ClientTestManager
import net.ccbluex.tenacc.impl.TestIdentifier
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking
import net.fabricmc.fabric.api.networking.v1.PacketSender
import net.minecraft.client.MinecraftClient
import net.minecraft.client.network.ClientPlayNetworkHandler
import net.minecraft.network.PacketByteBuf


object StartTestClientPacket: ClientPlayNetworking.PlayChannelHandler {
    override fun receive(
        client: MinecraftClient?,
        handler: ClientPlayNetworkHandler?,
        buf: PacketByteBuf,
        responseSender: PacketSender?
    ) {
        val testNamespace = buf.readString(100)
        val testName = buf.readString(100)

        ClientTestManager.runTestByIdentifier(TestIdentifier(testNamespace, testName))
    }

}