package net.ccbluex.tenacc.features.templates

import net.ccbluex.tenacc.utils.outputString
import net.minecraft.block.*
import net.minecraft.block.entity.SignBlockEntity
import net.minecraft.structure.StructurePlacementData
import net.minecraft.structure.StructureTemplate
import net.minecraft.structure.processor.StructureProcessor
import net.minecraft.structure.processor.StructureProcessorType
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.RotationPropertyHelper
import net.minecraft.world.WorldView
import org.joml.Vector3f

/**
 * Run when a structure is placed. Detects block markers and removes them from the final structure.
 * Possible markers:
 * - Signs: If the front text of a sign/wall sign is not empty (after processing) it may be used as a marker
 *      - ID: The front text is processed (remove line breaks, trim both ends) and used as id
 *      - Direction: The direction the front side of the sign. Note that freestanding signs may have
 *        more than one diagonal direction.
 *      - Pos: **Wall signs mark the block they hang on (!!!)** while freestanding signs mark their own block pos
 *      - Only wall signs and signs can be used as markers
 */
class TestMarkerTemplateProcessor : StructureProcessor() {
    val markers = hashMapOf<String, MutableList<BlockMarker>>()

    override fun getType(): StructureProcessorType<*> = throw NotImplementedError()

    override fun process(
        world: WorldView,
        pos: BlockPos,
        pivot: BlockPos,
        originalBlockInfo: StructureTemplate.StructureBlockInfo,
        currentBlockInfo: StructureTemplate.StructureBlockInfo?,
        data: StructurePlacementData
    ): StructureTemplate.StructureBlockInfo? {
        if (currentBlockInfo == null)
            return null

        // Check if the block is a sign block, otherwise no-op
        val currentBlockState = currentBlockInfo.state
        val signBlock = currentBlockState.block as? AbstractSignBlock ?: return currentBlockInfo

        val signEntity = SignBlockEntity.createFromNbt(
            currentBlockInfo.pos,
            currentBlockState,
            currentBlockInfo.nbt
        ) as SignBlockEntity

        val frontText = signEntity.frontText ?: return currentBlockInfo

        // Parse the marker ID from the sign text
        val id = frontText.getMessages(false).joinToString(separator = "") { it.outputString().trim() }

        // We don't want non-empty identifiers
        if (id.isEmpty())
            return currentBlockInfo

        val blockMarker = when (signBlock) {
            is SignBlock -> {
                val degrees = RotationPropertyHelper.toDegrees(currentBlockState.get(SignBlock.ROTATION))

                BlockMarker(
                    currentBlockInfo.pos,
                    Vector3f(0.0F, 0.0F, 1.0F).rotateY(-Math.toRadians(degrees.toDouble()).toFloat())
                )
            }

            is WallSignBlock -> {
                val direction = currentBlockState.get(WallSignBlock.FACING).vector

                BlockMarker(
                    currentBlockInfo.pos.subtract(direction),
                    Vector3f(direction.x.toFloat(), direction.y.toFloat(), direction.z.toFloat())
                )
            }

            else -> return currentBlockInfo
        }

        val markerList = this.markers.computeIfAbsent(id) { arrayListOf() }

        if (markerList.isNotEmpty() && signBlock.woodType != WoodType.CHERRY) {
            throw IllegalStateException(
                "Unique marker (oak sign) with id '$id' was found more than once in the template " +
                        "(at relative positions" +
                        "${markerList.joinToString { it.relativePos.toString() }} and ${blockMarker.relativePos}" +
                        "). Use cherry signs (minecraft:cherry_sign) for marker groups"
            )
        }

        markerList.add(blockMarker)

        return StructureTemplate.StructureBlockInfo(currentBlockInfo.pos, Blocks.AIR.defaultState, null)
    }
}