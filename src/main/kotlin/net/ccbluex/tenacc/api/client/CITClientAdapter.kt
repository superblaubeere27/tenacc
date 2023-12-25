package net.ccbluex.tenacc.api.client

import net.minecraft.entity.player.PlayerEntity

interface CITClientAdapter {
    fun sendInputs(vararg keys: InputKey, nTicks: Int? = null)
    fun clearInputs()
    fun log(s: String)

    val player: PlayerEntity
}