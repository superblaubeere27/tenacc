package net.ccbluex.tenacc.impl.server

import net.ccbluex.tenacc.api.server.CITServerAdapter
import net.ccbluex.tenacc.utils.chat
import net.minecraft.server.network.ServerPlayerEntity

class ServerAdapter(private val testContext: ServerRunningTestContext): CITServerAdapter {
    override fun log(s: String) {
        player.chat("§7[LOG SERVER] §r$s")
    }

    override val player: ServerPlayerEntity
        get() = testContext.player
}