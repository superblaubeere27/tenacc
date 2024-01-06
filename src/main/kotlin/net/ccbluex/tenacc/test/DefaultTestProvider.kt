package net.ccbluex.tenacc.test

import net.ccbluex.tenacc.api.runner.ScheduledTest
import net.ccbluex.tenacc.api.runner.TACCTestProvider
import net.ccbluex.tenacc.api.runner.TACCTestRegistry
import net.ccbluex.tenacc.api.runner.TACCTestScheduler
import net.minecraft.server.network.ServerPlayerEntity

object DefaultTestProvider : TACCTestProvider {
    override val structureTemplateBasePath: String
        get() = "/testresources"
    override val headlessMode: Boolean
        get() = false

    override fun init(scheduler: TACCTestScheduler?) {

    }

    override fun registerTests(registry: TACCTestRegistry) {

    }

    override fun onTestFail(player: ServerPlayerEntity, schedulerInfo: ScheduledTest, error: Throwable) {

    }

    override fun onTestPass(player: ServerPlayerEntity, schedulerInfo: ScheduledTest) {

    }

    override fun onTestQueueFinish(player: ServerPlayerEntity) {

    }
}