package net.ccbluex.tenacc

import net.ccbluex.tenacc.api.client.CITClientAdapter
import net.ccbluex.tenacc.api.client.InputKey
import net.ccbluex.tenacc.input.InputManager
import net.ccbluex.tenacc.utils.chat
import net.minecraft.client.MinecraftClient
import net.minecraft.entity.player.PlayerEntity

object ClientAdapter: CITClientAdapter {
    override fun sendInputs(vararg keys: InputKey, nTicks: Int?) {
        InputManager.setInput(keys, nTicks)
    }

    override fun clearInputs() {
        InputManager.clearInput()
    }

    override fun log(s: String) {
        player.chat("§7[LOG CLIENT] §r$s")
    }

    override val player: PlayerEntity
        get() = MinecraftClient.getInstance().player!!

}