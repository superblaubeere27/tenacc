package net.ccbluex.tenacc.utils

import net.minecraft.entity.player.PlayerEntity

fun PlayerEntity.lookDirection(rotation: Rotation) {
    this.yaw = rotation.yaw
    this.pitch = rotation.pitch
}