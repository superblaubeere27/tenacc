package net.ccbluex.tenacc.features.templates

import net.ccbluex.tenacc.impl.server.ServerRunningTestContext
import net.ccbluex.tenacc.impl.server.testManager
import net.minecraft.block.Block
import net.minecraft.nbt.NbtIo
import net.minecraft.nbt.NbtSizeTracker
import net.minecraft.server.MinecraftServer
import net.minecraft.server.world.ServerWorld
import net.minecraft.structure.StructurePlacementData
import net.minecraft.structure.StructureTemplate
import net.minecraft.util.BlockMirror
import net.minecraft.util.BlockRotation
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.random.Random
import java.io.FileInputStream

internal object TemplateLoader {
    private val templateCache = hashMapOf<String, StructureTemplate>()

    /**
     * Loads a structure template or reads it from cache.
     */
    private fun loadOrGetTemplate(testManager: MinecraftServer, resourcePath: String): StructureTemplate {
        // TODO Remove this line
        this.templateCache.clear()

        return this.templateCache.computeIfAbsent(resourcePath) { this.loadTemplate(testManager, resourcePath) }
    }

    private fun loadTemplate(server: MinecraftServer, resourcePath: String): StructureTemplate {
        val structureManager = server.structureTemplateManager

        // Find and open resource
        val fullResourcePath = "/testresources/$resourcePath"

        val stream = TemplateLoader.javaClass.getResourceAsStream(fullResourcePath)
            ?: throw IllegalStateException("Cannot find resource $fullResourcePath")

//         val stream = FileInputStream("${server.testManager.testProvider.structureTemplateBasePath}$resourcePath")

        // Read resource to NBT
        val nbtCompound = stream.use { NbtIo.readCompressed(it, NbtSizeTracker.ofUnlimitedBytes()) }

        // Create a template from the resource
        val template = structureManager.createTemplate(nbtCompound)

        return template
    }

    fun resetTemplate(context: ServerRunningTestContext, templateInfo: TemplateInfo) {
        val newTemplateInfo = placeTemplate(
            world = context.player.serverWorld,
            pos = templateInfo.pos,
            path = templateInfo.path,
            transformation = templateInfo.transformation
        )

        if (newTemplateInfo != templateInfo) {
            throw IllegalStateException("After resetting the template info changed. This should definitely not happen.")
        }
    }

    fun placeTemplate(world: ServerWorld, pos: BlockPos, transformation: TemplateTransformation, path: String): TemplateInfo {
        val markerProcessor = TestMarkerTemplateProcessor()

        val mirror = when (transformation.mirrorType) {
            MirrorType.MIRROR_NONE -> BlockMirror.NONE
            MirrorType.MIRROR_X -> BlockMirror.FRONT_BACK
            MirrorType.MIRROR_Z -> BlockMirror.LEFT_RIGHT
        }
        val rotationType = when (transformation.rotationType) {
            RotationType.NONE -> BlockRotation.NONE
            RotationType.PLUS_90_DEGREES -> BlockRotation.CLOCKWISE_90
            RotationType.PLUS_180_DEGREES -> BlockRotation.CLOCKWISE_180
            RotationType.MINUS_90_DEGREES -> BlockRotation.COUNTERCLOCKWISE_90
        }

        val data = StructurePlacementData()
            .clearProcessors()
            .addProcessor(markerProcessor)
            .setPlaceFluids(true)
            .setMirror(mirror)
            .setRotation(rotationType)
//            .setPosition(pos)

        val template = loadOrGetTemplate(world.server, path)

        template.place(
            world,
            pos,
            BlockPos.ORIGIN,
            data,
            Random.create(),
            Block.NOTIFY_LISTENERS or Block.SKIP_DROPS or Block.FORCE_STATE
        )

        return TemplateInfo(
            path,
            pos,
            transformation,
            markerProcessor.markers as HashMap<String, List<BlockMarker>>
        )
    }

}