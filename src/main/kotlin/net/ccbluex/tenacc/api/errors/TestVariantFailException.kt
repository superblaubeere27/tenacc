package net.ccbluex.tenacc.api.errors

import net.ccbluex.tenacc.api.common.TACCTestVariant

/**
 * Thrown when a party detects a test failure
 */
class TestVariantFailException(
    val variant: TACCTestVariant,
    cause: Throwable
): RuntimeException("Failed to run variant '${variant.name}'", cause) {
}