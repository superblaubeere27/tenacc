package net.ccbluex.tenacc.utils

import net.ccbluex.tenacc.api.common.TACCTestAdapter
import net.minecraft.entity.player.PlayerEntity

fun PlayerEntity.isStandingOnMarkerBlock(adapter: TACCTestAdapter, id: String): Boolean {
    val pos = adapter.getMarkerPos(id)

    return this.isOnGround && blockPos == pos
}