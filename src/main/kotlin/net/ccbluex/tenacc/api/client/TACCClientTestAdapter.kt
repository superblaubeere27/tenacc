package net.ccbluex.tenacc.api.client

import net.ccbluex.tenacc.api.common.TACCBox
import net.ccbluex.tenacc.api.common.TACCTestAdapter
import net.minecraft.entity.player.PlayerEntity

interface TACCClientTestAdapter: TACCTestAdapter {
    fun sendInputs(vararg keys: InputKey, nTicks: Int? = null)
    fun clearInputs()

    fun <T> openBox(box: TACCBox.ClientBox<T>): T = box.value

    val player: PlayerEntity
}