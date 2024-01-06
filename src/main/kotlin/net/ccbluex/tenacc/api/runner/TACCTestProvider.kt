package net.ccbluex.tenacc.api.runner

import net.minecraft.server.network.ServerPlayerEntity

interface TACCTestProvider {
    val structureTemplateBasePath: String

    /**
     * Enables headless features. Currently, that is
     * - Automatic world joining or generation
     * - Automatic respawning
     */
    val headlessMode: Boolean

    fun init(scheduler: TACCTestScheduler?)
    fun registerTests(registry: TACCTestRegistry)

    fun onTestFail(player: ServerPlayerEntity, schedulerInfo: ScheduledTest, error: Throwable)
    fun onTestPass(player: ServerPlayerEntity,schedulerInfo: ScheduledTest)
    fun onTestQueueFinish(player: ServerPlayerEntity)
}