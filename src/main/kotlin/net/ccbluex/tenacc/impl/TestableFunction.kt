package net.ccbluex.tenacc.impl

import net.ccbluex.tenacc.api.CITest
import net.ccbluex.tenacc.api.common.CITCommonAdapter

class TestableFunction(
    val identifier: TestIdentifier,
    val annotation: CITest,
    val testFunction: (CITCommonAdapter) -> Unit
)

data class TestIdentifier(val className: String, val testName: String)