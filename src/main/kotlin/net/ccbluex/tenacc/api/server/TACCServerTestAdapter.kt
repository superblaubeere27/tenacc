package net.ccbluex.tenacc.api.server

import net.ccbluex.tenacc.api.common.TACCBox
import net.ccbluex.tenacc.api.common.TACCEventListener
import net.ccbluex.tenacc.api.common.TACCTestAdapter
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.util.math.BlockPos

interface TACCServerTestAdapter: TACCTestAdapter {
    /**
     * Resets the template
     */
    fun resetScenery()

    fun registerEventListener(listener: TACCEventListener)

    fun <T> openBox(box: TACCBox.ServerBox<T>): T = box.value

    val player: ServerPlayerEntity
}