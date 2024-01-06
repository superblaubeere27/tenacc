package net.ccbluex.tenacc.utils

import net.ccbluex.tenacc.api.server.TACCServerTestAdapter
import net.minecraft.block.Blocks
import net.minecraft.block.entity.ChestBlockEntity
import net.minecraft.nbt.NbtList
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Vec3d
import net.minecraft.world.GameMode

/**
 *
 */
fun ServerPlayerEntity.loadInventory(fileName: String) {
    val nbt = TenaccResourceManager.readNbt(fileName)

    if (nbt !is NbtList)
        throw IllegalArgumentException("Inventory data $fileName must be an NBT-List.")

    InventorySerializer.deserializePlayerInventory(this.inventory, nbt)

    this.sendInventoryUpdates()
}

fun TACCServerTestAdapter.resetStandardConditions() {
    resetScenery()

    if (player.isDead) {
        player.server.playerManager.respawnPlayer(player, false)
    }

    // If we don't reset the join invulnerability ticks, the player is unable to be damaged for 3s after respawn/spawn
    player.joinInvulnerabilityTicks = -1
    player.serverWorld.resetWeather()
    player.serverWorld.timeOfDay = 1000

    player.isOnFire = false

    player.abilities.apply {
        flying = false
        allowFlying = false
        allowModifyWorld = true
    }
    player.health = 20.0F
    player.markHealthDirty()
    player.isInvisible = false
    player.velocity = Vec3d.ZERO

    player.sendAbilitiesUpdate()
    player.changeGameMode(GameMode.SURVIVAL)

    player.inventory.clear()
    player.sendInventoryUpdates()

    player.closeHandledScreen()
}

fun TACCServerTestAdapter.placeChestWithContents(pos: BlockPos, inventoryResource: String) {
    player.serverWorld.setBlockState(pos, Blocks.CHEST.defaultState)

    val blockEntity = player.serverWorld.getBlockEntity(pos) as ChestBlockEntity

    InventorySerializer.deserializeInventory(
        blockEntity,
        TenaccResourceManager.readNbt(inventoryResource) as NbtList
    )
}

/**
 * Must be called after changing the player's inventory.
 */
fun ServerPlayerEntity.sendInventoryUpdates() {
    this.currentScreenHandler.sendContentUpdates()
    this.playerScreenHandler.onContentChanged(this.inventory)
}