package net.ccbluex.tenacc.api.runner

import net.ccbluex.tenacc.features.templates.MirrorType
import net.ccbluex.tenacc.features.templates.RotationType
import net.ccbluex.tenacc.impl.TestableFunction

class ScheduledTest(val fn: TestableFunction, val mirrorType: MirrorType, val rotationType: RotationType)