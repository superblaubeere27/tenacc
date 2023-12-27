package net.ccbluex.tenacc.api

import net.ccbluex.tenacc.features.templates.MirrorType
import net.ccbluex.tenacc.features.templates.RotationType

/**
 * Used on functions to mark them as a client integration test function.
 *
 * @param scenary the structure block-structure file this test uses as scenary
 * @param timeout the test will automatically fail after this time
 * @param maxLatency if the client to server latency is greater than this threshold, this test cannot run, because it might
 * produce invalid results
 */
@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
annotation class TACCTest(
    val name: String,
    val scenary: String,
    val timeout: Int = 100,
    val maxLatency: Int = -1,
    val mirrors: Array<MirrorType> = [MirrorType.MIRROR_NONE],
    val rotations: Array<RotationType> = [RotationType.NONE],
)
