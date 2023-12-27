package net.ccbluex.tenacc.api.errors

/**
 * Thrown when a party detects a test failure
 */
class TestFailException(message: String): RuntimeException(message) {
}