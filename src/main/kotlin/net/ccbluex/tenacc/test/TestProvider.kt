package net.ccbluex.tenacc.test

import net.ccbluex.tenacc.api.runner.*
import net.ccbluex.tenacc.features.templates.MirrorType
import net.ccbluex.tenacc.features.templates.RotationType
import net.ccbluex.tenacc.impl.TestIdentifier
import net.ccbluex.tenacc.utils.TestErrorFormatter
import net.ccbluex.tenacc.utils.chat
import net.minecraft.server.network.ServerPlayerEntity

internal class TestProvider: TACCTestProvider {
    override val structureTemplateBasePath: String
        get() = "/testresources/"
    override val startIntoTestWorldOnStartup: Boolean
        get() = true

    override fun init(scheduler: TACCTestScheduler?) {
        scheduler!!.enqueueTests(TestScheduleRequest(TestIdentifier("GeneralTest", "testBasicFunctionality"), rotations = arrayOf(RotationType.NONE), mirrors = arrayOf(MirrorType.MIRROR_NONE)))
        scheduler!!.enqueueTests(TestScheduleRequest(TestIdentifier("GeneralTest", "testImpossible"), rotations = arrayOf(RotationType.NONE), mirrors = arrayOf(MirrorType.MIRROR_NONE)))
    }

    override fun registerTests(registry: TACCTestRegistry) {
        registry.registerTestClass(TestTestTest::class)
        registry.registerTestClass(GeneralTest::class)
    }

    override fun onTestFail(player: ServerPlayerEntity, schedulerInfo: ScheduledTest, error: Throwable) {
        player.chat("§cTest §l'${schedulerInfo.fn.identifier}' (${schedulerInfo.mirrorType}/${schedulerInfo.rotationType})§r§c failed: ${TestErrorFormatter.formatError(error, schedulerInfo.fn)}")
    }

    override fun onTestPass(player: ServerPlayerEntity, schedulerInfo: ScheduledTest) {
        player.chat("§aTest §l'${schedulerInfo.fn.identifier}' (${schedulerInfo.mirrorType}/${schedulerInfo.rotationType})§r§a passed.")
    }

    override fun onTestQueueFinish(player: ServerPlayerEntity) {
        player.chat("§aTest queue passed.")
    }
}