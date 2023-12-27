package net.ccbluex.tenacc

import net.ccbluex.tenacc.api.client.TACCClientTestAdapter
import net.ccbluex.tenacc.api.client.InputKey
import net.ccbluex.tenacc.features.templates.TemplateInfo
import net.ccbluex.tenacc.input.InputManager
import net.ccbluex.tenacc.utils.chat
import net.minecraft.client.MinecraftClient
import net.minecraft.entity.player.PlayerEntity

object ClientClientTestAdapter: TACCClientTestAdapter {
    override fun sendInputs(vararg keys: InputKey, nTicks: Int?) {
        InputManager.setInput(keys, nTicks)
    }

    override fun clearInputs() {
        InputManager.clearInput()
    }

    override fun log(s: String) {
        player.chat("§7[LOG CLIENT] §7§o$s")
    }

    override val player: PlayerEntity
        get() = MinecraftClient.getInstance().player!!
    override val templateInfo: TemplateInfo
        get() = ClientTestManager.currentTestContext!!.templateInfo
}