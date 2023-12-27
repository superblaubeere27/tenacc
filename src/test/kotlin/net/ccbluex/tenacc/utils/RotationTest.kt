package net.ccbluex.tenacc.utils

import net.ccbluex.tenacc.features.templates.MirrorType
import net.ccbluex.tenacc.features.templates.RotationType
import net.ccbluex.tenacc.features.templates.TemplateTransformation
import org.joml.Vector3f
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import kotlin.math.absoluteValue
import kotlin.math.roundToInt

class RotationTest {
    @Test
    fun testDirectionTransformation() {
        assertEquals(-90, Rotation.fromDirection(Vector3f(1.0F, 0.0F, 0.0F), TemplateTransformation(RotationType.NONE, MirrorType.MIRROR_NONE)).yaw.toInt())
        assertEquals(90, Rotation.fromDirection(Vector3f(-1.0F, 0.0F, 0.0F), TemplateTransformation(RotationType.NONE, MirrorType.MIRROR_NONE)).yaw.toInt())
        assertEquals(0, Rotation.fromDirection(Vector3f(0.0F, 0.0F, 1.0F), TemplateTransformation(RotationType.NONE, MirrorType.MIRROR_NONE)).yaw.toInt())
        assertEquals(-180, Rotation.fromDirection(Vector3f(0.0F, 0.0F, -1.0F), TemplateTransformation(RotationType.NONE, MirrorType.MIRROR_NONE)).yaw.toInt())

        assertEquals(0, Rotation.fromDirection(Vector3f(1.0F, 0.0F, 0.0F), TemplateTransformation(RotationType.PLUS_90_DEGREES, MirrorType.MIRROR_NONE)).yaw.toInt())
        assertEquals(180, Rotation.fromDirection(Vector3f(-1.0F, 0.0F, 0.0F), TemplateTransformation(RotationType.PLUS_90_DEGREES, MirrorType.MIRROR_NONE)).yaw.toInt())
        assertEquals(90, Rotation.fromDirection(Vector3f(0.0F, 0.0F, 1.0F), TemplateTransformation(RotationType.PLUS_90_DEGREES, MirrorType.MIRROR_NONE)).yaw.toInt())
        assertEquals(-90, Rotation.fromDirection(Vector3f(0.0F, 0.0F, -1.0F), TemplateTransformation(RotationType.PLUS_90_DEGREES, MirrorType.MIRROR_NONE)).yaw.toInt())

        assertEquals(90, Rotation.fromDirection(Vector3f(1.0F, 0.0F, 0.0F), TemplateTransformation(RotationType.PLUS_180_DEGREES, MirrorType.MIRROR_NONE)).yaw.toInt())
        assertEquals(-90, Rotation.fromDirection(Vector3f(-1.0F, 0.0F, 0.0F), TemplateTransformation(RotationType.PLUS_180_DEGREES, MirrorType.MIRROR_NONE)).yaw.roundToInt())
        assertEquals(180, Rotation.fromDirection(Vector3f(0.0F, 0.0F, 1.0F), TemplateTransformation(RotationType.PLUS_180_DEGREES, MirrorType.MIRROR_NONE)).yaw.absoluteValue.toInt())
        assertEquals(0, Rotation.fromDirection(Vector3f(0.0F, 0.0F, -1.0F), TemplateTransformation(RotationType.PLUS_180_DEGREES, MirrorType.MIRROR_NONE)).yaw.toInt())

        assertEquals(-180, Rotation.fromDirection(Vector3f(1.0F, 0.0F, 0.0F), TemplateTransformation(RotationType.MINUS_90_DEGREES, MirrorType.MIRROR_NONE)).yaw.toInt())
        assertEquals(0, Rotation.fromDirection(Vector3f(-1.0F, 0.0F, 0.0F), TemplateTransformation(RotationType.MINUS_90_DEGREES, MirrorType.MIRROR_NONE)).yaw.toInt())
        assertEquals(-90, Rotation.fromDirection(Vector3f(0.0F, 0.0F, 1.0F), TemplateTransformation(RotationType.MINUS_90_DEGREES, MirrorType.MIRROR_NONE)).yaw.toInt())
        assertEquals(90, Rotation.fromDirection(Vector3f(0.0F, 0.0F, -1.0F), TemplateTransformation(RotationType.MINUS_90_DEGREES, MirrorType.MIRROR_NONE)).yaw.toInt())
    }
}