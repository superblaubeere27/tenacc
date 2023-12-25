package net.ccbluex.tenacc.api.server

import net.minecraft.server.network.ServerPlayerEntity

interface CITServerAdapter {
    fun log(s: String)

    val player: ServerPlayerEntity
}