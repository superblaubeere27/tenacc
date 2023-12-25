package net.ccbluex.tenacc.utils

import net.minecraft.entity.player.PlayerEntity
import net.minecraft.text.Text

private fun String.asText() = Text.literal(this)

private val clientPrefix = "§f§lte§c§onacc §8▸ §7".asText()

fun PlayerEntity.chat(vararg texts: Text, prefix: Boolean = true) {
    val literalText = if (prefix) clientPrefix.copy() else Text.literal("")
    texts.forEach { literalText.append(it) }

    this.sendMessage(literalText)
}

fun PlayerEntity.chat(text: String) = chat(text.asText())
