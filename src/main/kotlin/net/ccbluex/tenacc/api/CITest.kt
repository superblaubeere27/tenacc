package net.ccbluex.tenacc.api

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
annotation class CITest(
    val name: String,
    val scenary: String,
    val timeout: Int = 100,
    val maxLatency: Int = -1,
)
