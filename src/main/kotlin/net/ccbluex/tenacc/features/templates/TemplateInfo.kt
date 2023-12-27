package net.ccbluex.tenacc.features.templates

import net.minecraft.network.PacketByteBuf
import net.minecraft.util.math.BlockPos
import org.joml.Vector3f

/**
 * Contains information about the placed template like markers.
 *
 * @param path the path of the structure-block nbt file
 * @param pos the point the template was placed relative to.
 */
data class TemplateInfo(
    val path: String,
    val pos: BlockPos,
    val transformation: TemplateTransformation,
    val blockMarkers: HashMap<String, List<BlockMarker>>
) {

    fun writeToBuf(buf: PacketByteBuf) {
        buf.writeString(this.path,100)
        buf.writeBlockPos(this.pos)

        buf.writeEnumConstant(this.transformation.rotationType)
        buf.writeEnumConstant(this.transformation.mirrorType)

        buf.writeVarInt(this.blockMarkers.size)

        for (markerEntry in this.blockMarkers) {
            val (id, markers) = markerEntry

            buf.writeString(id, 100)

            buf.writeVarInt(markers.size)

            for (marker in markers) {
                buf.writeBlockPos(marker.relativePos)
                buf.writeVector3f(marker.direction)
            }

        }
    }

    companion object {
        fun readFromBuf(buf: PacketByteBuf): TemplateInfo {
            val path = buf.readString(100)
            val basePos = buf.readBlockPos()

            val rotationType = buf.readEnumConstant(RotationType::class.java)
            val mirror = buf.readEnumConstant(MirrorType::class.java)

            val nGroups = buf.readVarInt()

            val markers = hashMapOf<String, List<BlockMarker>>()

            for (ignored in 0 until nGroups) {
                val id = buf.readString(100)
                val nMarkers = buf.readVarInt()

                markers[id] = (0 until nMarkers).map { BlockMarker(buf.readBlockPos(), buf.readVector3f()) }
            }

            return TemplateInfo(path, basePos, TemplateTransformation(rotationType, mirror), markers)
        }
    }
}

/**
 * A marker within a structure (i.e. a sign).
 *
 * @param relativePos position relative to the placement (*"pivot"*) location
 * @param direction direction the points to. For signs that is the direction of the text. Note that freestanding signs
 * may also have diagonal directions
 */
data class BlockMarker(
    val relativePos: BlockPos,
    val direction: Vector3f
)

data class TemplateTransformation(
    val rotationType: RotationType,
    val mirrorType: MirrorType,
) {
    companion object {
        val NEUTRAL = TemplateTransformation(RotationType.NONE, MirrorType.MIRROR_NONE)
    }
}

enum class RotationType {
    NONE,
    PLUS_90_DEGREES,
    PLUS_180_DEGREES,
    MINUS_90_DEGREES,
}

enum class MirrorType {
    MIRROR_NONE,
    MIRROR_X,
    MIRROR_Z
}