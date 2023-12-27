package net.ccbluex.tenacc.utils

import net.ccbluex.tenacc.features.templates.MirrorType
import net.ccbluex.tenacc.features.templates.RotationType
import net.ccbluex.tenacc.features.templates.TemplateTransformation
import org.joml.Vector3f
import kotlin.math.atan2

data class Rotation(
    val yaw: Float,
    val pitch: Float,
) {
    companion object {
        fun fromDirection(
            direction: Vector3f,
            transformation: TemplateTransformation = TemplateTransformation.NEUTRAL
        ): Rotation {
            val mirroredDirection = when (transformation.mirrorType) {
                MirrorType.MIRROR_X -> direction.mul(-1.0F, 1.0F, 1.0F)
                MirrorType.MIRROR_Z -> direction.mul(1.0F, 1.0F, -1.0F)
                MirrorType.MIRROR_NONE -> direction
            }

            val rotatedDirection = when (transformation.rotationType) {
                RotationType.NONE -> mirroredDirection
                RotationType.PLUS_90_DEGREES -> mirroredDirection.rotateY(Math.toRadians(-90.0).toFloat())
                RotationType.PLUS_180_DEGREES -> mirroredDirection.rotateY(Math.toRadians(-180.0).toFloat())
                RotationType.MINUS_90_DEGREES -> mirroredDirection.rotateY(Math.toRadians(90.0).toFloat())
            }

            return Rotation(Math.toDegrees(atan2(-rotatedDirection.x.toDouble(), rotatedDirection.z.toDouble())).toFloat(), 0.0F)
        }
    }

    fun yaw(v: Float) = Rotation(v, pitch)
    fun pitch(v: Float) = Rotation(yaw, v)

}