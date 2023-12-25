package net.ccbluex.tenacc.input

import net.ccbluex.tenacc.api.client.InputKey
import net.minecraft.client.MinecraftClient

val KEYBINDING_TO_INPUT_KEY = run {
    val opt = MinecraftClient.getInstance().options

    hashMapOf(
        opt.forwardKey to InputKey.KEY_FORWARDS,
        opt.backKey to InputKey.KEY_BACKWARDS,
        opt.rightKey to InputKey.KEY_RIGHT,
        opt.leftKey to InputKey.KEY_LEFT,
        opt.jumpKey to InputKey.KEY_JUMP,
        opt.attackKey to InputKey.KEY_ATTACK,
        opt.useKey to InputKey.KEY_USE,
    )
}

val INPUT_KEY_TO_KEYBINDING = KEYBINDING_TO_INPUT_KEY.entries.associate{ (k,v)-> v to k }