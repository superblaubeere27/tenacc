package net.ccbluex.tenacc.api.common

import net.minecraft.network.packet.Packet

interface TACCEventListener {
    fun onPacket(packet: Packet<*>)
    fun onTick()
}