package net.ccbluex.tenacc.api.errors

/**
 * Thrown when a party detects a test failure
 */
class TestSectionFailException(
    val sectionName: String,
    cause: Throwable
): RuntimeException("Failed to run variant '${sectionName}'", cause) {
}