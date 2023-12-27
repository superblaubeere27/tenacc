package net.ccbluex.tenacc.api.runner

import net.ccbluex.tenacc.features.templates.MirrorType
import net.ccbluex.tenacc.features.templates.RotationType
import net.ccbluex.tenacc.impl.TestIdentifier

interface TACCTestScheduler {
    fun enqueueTests(vararg tests: TestScheduleRequest)
}

class TestScheduleRequest(val id: TestIdentifier, val rotations: Array<RotationType>? = null, val mirrors: Array<MirrorType>? = null) {}