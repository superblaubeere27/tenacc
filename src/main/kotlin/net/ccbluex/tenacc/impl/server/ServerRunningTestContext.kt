package net.ccbluex.tenacc.impl.server

import net.ccbluex.tenacc.api.common.TACCEventListener
import net.ccbluex.tenacc.features.templates.TemplateInfo
import net.ccbluex.tenacc.impl.TestableFunction
import net.minecraft.server.network.ServerPlayerEntity
import java.util.concurrent.CopyOnWriteArrayList

class ServerRunningTestContext(
    val test: TestableFunction,
    val player: ServerPlayerEntity,
    val templateInfo: TemplateInfo
) {
    val eventHandlers = CopyOnWriteArrayList<TACCEventListener>()
}