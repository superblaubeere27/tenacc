package net.ccbluex.tenacc.impl.server

import net.ccbluex.tenacc.api.common.TACCEventListener
import net.ccbluex.tenacc.features.templates.TemplateInfo
import net.ccbluex.tenacc.impl.TestableFunction
import net.minecraft.server.MinecraftServer
import net.minecraft.server.network.ServerPlayerEntity
import java.util.UUID
import java.util.concurrent.CopyOnWriteArrayList

class ServerRunningTestContext(
    val test: TestableFunction,
    val server: MinecraftServer,
    val playerUUID: UUID,
    val templateInfo: TemplateInfo
) {
    val eventHandlers = CopyOnWriteArrayList<TACCEventListener>()

    val player: ServerPlayerEntity
        get() {
            val cached = cachedPlayer

            return if (cached == null || cached.isRemoved) {
                val newPlayer = this.server.playerManager.getPlayer(this.playerUUID) as ServerPlayerEntity

                cachedPlayer = newPlayer

                newPlayer
            } else {
                cached
            }
        }

    private var cachedPlayer: ServerPlayerEntity? = null
}