package net.ccbluex.tenacc.ui

import net.ccbluex.tenacc.ClientClientTestAdapter.player
import net.ccbluex.tenacc.utils.InventorySerializer
import net.ccbluex.tenacc.utils.TenaccSerializer
import net.ccbluex.tenacc.utils.chat
import net.minecraft.client.MinecraftClient
import net.minecraft.inventory.Inventory
import net.minecraft.item.ItemStack
import net.minecraft.nbt.visitor.StringNbtWriter
import net.minecraft.text.ClickEvent
import net.minecraft.text.Text
import net.minecraft.util.Formatting
import java.io.File
import java.nio.charset.Charset

const val OUTPUT_FOLDER = "saved-inventories"

internal object SaveInventoryContents {

    fun savePlayerInventoryContents() {
        val playerInventory = player.inventory

        val nbtData = InventorySerializer.serializePlayerInventory(playerInventory)

        val fileName = TenaccSerializer.suggestNameForContent(nbtData.toString().toByteArray())

        val data = StringNbtWriter().apply(nbtData).toByteArray(Charset.forName("UTF-8"))

        val outputFile = TenaccSerializer.writeToDataOutput(
            runDirectory = MinecraftClient.getInstance().runDirectory,
            subfolder = OUTPUT_FOLDER,
            fileName = "$fileName.nbttxt"
        ) {
            it.write(data)
        }

        player.chat(Text.literal("Saved player inventory to "), formatFileName(outputFile), Text.literal("."))
    }

    fun saveInventoryContents(contents: Inventory, name: String) {
        val nbtData = InventorySerializer.serializeItems(contents)

        val fileName = TenaccSerializer.suggestNameForContent(nbtData.toString().toByteArray())

        val data = StringNbtWriter().apply(nbtData).toByteArray(Charset.forName("UTF-8"))

        val outputFile = TenaccSerializer.writeToDataOutput(
            runDirectory = MinecraftClient.getInstance().runDirectory,
            subfolder = OUTPUT_FOLDER,
            fileName = "$fileName.nbttxt"
        ) {
            it.write(data)
        }

        player.chat(Text.literal("Saved $name to "), formatFileName(outputFile), Text.literal("."))
    }

    private fun formatFileName(outputFile: File): Text {
        return Text
            .literal(outputFile.absolutePath)
            .formatted(Formatting.UNDERLINE)
            .styled {
                it.withClickEvent(
                    ClickEvent(ClickEvent.Action.OPEN_FILE, outputFile.parentFile.absolutePath)
                )
            }
    }

}