package net.ccbluex.tenacc.test

import net.ccbluex.tenacc.api.runner.*
import net.ccbluex.tenacc.utils.TestErrorFormatter
import net.ccbluex.tenacc.utils.chat
import net.minecraft.server.network.ServerPlayerEntity

internal class TestProvider: TACCTestProvider {
    override val structureTemplateBasePath: String
        get() = "C:/Users/David/IdeaProjects/client-integrationtest/run/saves/New World/generated/minecraft/structures/"

    override fun init(scheduler: TACCTestScheduler?) {

    }

    override fun registerTests(registry: TACCTestRegistry) {
        registry.registerTestClass(TestTestTest::class)
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