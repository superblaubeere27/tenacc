package net.ccbluex.tenacc.api.common

import net.ccbluex.tenacc.features.templates.BlockMarker
import net.ccbluex.tenacc.features.templates.TemplateInfo
import net.minecraft.util.math.BlockPos

interface TACCTestAdapter {
    val templateInfo: TemplateInfo

    fun getMarkerPos(id: String): BlockPos = this.templateInfo.pos.add(getMarker(id).relativePos)
    fun getMarkerPositions(id: String): List<BlockPos> = getMarkers(id).map { this.templateInfo.pos.add(it.relativePos) }

    fun getMarker(id: String): BlockMarker {
        val markers = getMarkers(id)

        return when (markers.size) {
            1 -> markers[0]
            0 -> throw IllegalArgumentException("Unknown block marker $id")
            else -> throw IllegalArgumentException("Expected single marker $id, found ${markers.size}")
        }
    }

    fun getMarkers(id: String): List<BlockMarker> {
        return this.templateInfo.blockMarkers[id] ?: emptyList()
    }

    fun log(s: String)
}