package net.ccbluex.tenacc.api.runner

import net.minecraft.server.network.ServerPlayerEntity

interface TACCTestProvider {
    val structureTemplateBasePath: String
    val startIntoTestWorldOnStartup: Boolean

    fun init(scheduler: TACCTestScheduler?)
    fun registerTests(registry: TACCTestRegistry)

    fun onTestFail(player: ServerPlayerEntity, schedulerInfo: ScheduledTest, error: Throwable)
    fun onTestPass(player: ServerPlayerEntity,schedulerInfo: ScheduledTest)
    fun onTestQueueFinish(player: ServerPlayerEntity)
}