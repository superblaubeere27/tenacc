package net.ccbluex.tenacc.impl.server

import net.ccbluex.tenacc.impl.TestableFunction
import net.minecraft.server.network.ServerPlayerEntity

class ServerRunningTestContext(
    val test: TestableFunction,
    val player: ServerPlayerEntity
    ) {
}