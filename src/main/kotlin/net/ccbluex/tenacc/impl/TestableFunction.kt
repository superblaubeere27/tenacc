package net.ccbluex.tenacc.impl

import net.ccbluex.tenacc.api.TACCTest
import net.ccbluex.tenacc.api.common.TACCSequenceAdapter

class TestableFunction(
    val identifier: TestIdentifier,
    val className: String,
    val functionName: String,
    val annotation: TACCTest,
    val testFunction: (TACCSequenceAdapter) -> Unit
)

data class TestIdentifier(val className: String, val testName: String) {
    override fun toString(): String {
        return "$className::$testName"
    }
}