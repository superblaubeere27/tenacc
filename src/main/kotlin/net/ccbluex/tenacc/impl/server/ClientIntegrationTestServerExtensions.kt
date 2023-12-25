package net.ccbluex.tenacc.impl.server

import net.ccbluex.tenacc.interfaces.IMixinMinecraftServer
import net.minecraft.server.MinecraftServer

val MinecraftServer.testManager: ServerTestManager
    get() = (this as IMixinMinecraftServer).testManager