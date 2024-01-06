package net.ccbluex.tenacc.impl.server

import net.ccbluex.tenacc.api.common.TACCEventListener
import net.ccbluex.tenacc.api.server.TACCServerTestAdapter
import net.ccbluex.tenacc.features.templates.TemplateInfo
import net.ccbluex.tenacc.features.templates.TemplateLoader
import net.ccbluex.tenacc.utils.chat
import net.minecraft.server.network.ServerPlayerEntity

class ServerTestAdapter(private val testContext: ServerRunningTestContext): TACCServerTestAdapter {
    override fun log(s: String) {
        player.chat("§7[LOG SERVER] §7§o$s")
    }

    override fun resetScenery() {
        TemplateLoader.resetTemplate(testContext, this.templateInfo)
    }

    override fun registerEventListener(listener: TACCEventListener) {
        testContext.eventHandlers.add(listener)
    }

    override val player: ServerPlayerEntity
        get() = testContext.player

    override val templateInfo: TemplateInfo
        get() = testContext.templateInfo

}