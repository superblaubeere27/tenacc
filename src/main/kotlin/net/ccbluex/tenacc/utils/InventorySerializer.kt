package net.ccbluex.tenacc.utils

import net.minecraft.entity.player.PlayerInventory
import net.minecraft.inventory.Inventory
import net.minecraft.item.ItemStack
import net.minecraft.nbt.NbtCompound
import net.minecraft.nbt.NbtList

object InventorySerializer {

    fun serializeItems(inv: Inventory): NbtList {
        val serializedItems = (0 until inv.size())
            .map {
                val stack = inv.getStack(it)

                stack.writeNbt(NbtCompound())
            }

        val nbtList = NbtList()

        nbtList.addAll(serializedItems)

        return nbtList
    }

    fun serializePlayerInventory(playerInventory: PlayerInventory): NbtList {
        return playerInventory.writeNbt(NbtList())
    }

}