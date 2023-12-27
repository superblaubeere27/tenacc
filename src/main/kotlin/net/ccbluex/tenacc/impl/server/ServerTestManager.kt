package net.ccbluex.tenacc.impl.server

import net.ccbluex.tenacc.api.common.TACCSequenceAdapter
import net.ccbluex.tenacc.api.runner.ScheduledTest
import net.ccbluex.tenacc.api.runner.TACCTestScheduler
import net.ccbluex.tenacc.api.runner.TestScheduleRequest
import net.ccbluex.tenacc.features.templates.MirrorType
import net.ccbluex.tenacc.features.templates.RotationType
import net.ccbluex.tenacc.features.templates.TemplateLoader
import net.ccbluex.tenacc.features.templates.TemplateTransformation
import net.ccbluex.tenacc.impl.TestManager
import net.ccbluex.tenacc.impl.TestableFunction
import net.ccbluex.tenacc.impl.network.ServerNetworkManager
import net.ccbluex.tenacc.utils.chat
import net.minecraft.server.MinecraftServer
import net.minecraft.util.math.BlockPos
import java.util.*
import kotlin.collections.ArrayList

class ServerTestManager(
    val server: MinecraftServer
) : TestManager(), TACCTestScheduler {

    val networkManager = ServerNetworkManager(this)
    var runningTest: ServerRunningTestContext? = null

    private val testQueue: MutableList<ScheduledTest> = Collections.synchronizedList(ArrayList())

    override val isServer: Boolean
        get() = true

    override fun createCommonAdapter(): TACCSequenceAdapter = ServerCommonAdapter(this)

    override fun reset() {
        super.reset()

        this.runningTest = null
        println("Server reset")
    }

    fun tick(server: MinecraftServer) {
        if (this.runningTest != null || this.testQueue.isEmpty())
            return

        val test = this.testQueue.removeAt(0)

        startTest(server, test.fn, test.mirrorType, test.rotationType)
    }

    override fun failTestError(e: Throwable, reportToOtherSide: Boolean) {
        println("Test failed with error: ")
        e.printStackTrace()

        val runningTest1 = this.runningTest

        runningTest1 ?: return

        testProvider.onTestFail(runningTest1!!.player, ScheduledTest(runningTest1.test, runningTest1.templateInfo.transformation.mirrorType, runningTest1.templateInfo.transformation.rotationType), e)

        checkForQueueEnd()

        try {
            if (reportToOtherSide) {
                networkManager.sendError(e)
            }
        } finally {
            this.reset()
        }

    }

    fun startTest(
        server: MinecraftServer,
        testableFunction: TestableFunction,
        mirror: MirrorType,
        rotationType: RotationType
    ) {
        if (server.playerManager.playerList.size != 1)
            throw IllegalStateException("Tests can only be run if there is only one player online!")

        val player = server.playerManager.playerList.first()

        this.reset()

        val templateInfo = TemplateLoader.placeTemplate(
            player.serverWorld,
            BlockPos(0, 0, 0),
            TemplateTransformation(rotationType, mirror),
            testableFunction.annotation.scenary
        )

        val ctx = ServerRunningTestContext(testableFunction, player, templateInfo)

        this.runningTest = ctx

        networkManager.sendTestStart(ctx)

        this.runTest(testableFunction)
    }

    fun checkForQueueEnd() {
        if (this.testQueue.isEmpty()) {
            testProvider.onTestQueueFinish(this.runningTest!!.player)
        }
    }

    fun endTest() {
        networkManager.sendTestEnd()

        val runningTest1 = this.runningTest

        runningTest1 ?: return

        testProvider.onTestPass(runningTest1!!.player, ScheduledTest(runningTest1.test, runningTest1.templateInfo.transformation.mirrorType, runningTest1.templateInfo.transformation.rotationType))

        checkForQueueEnd()

        reset()
    }

    override fun enqueueTests(vararg tests: TestScheduleRequest) {
        val newEntries = ArrayList<ScheduledTest>()

        for (test in tests) {
            val testableFn = this.findTestById(test.id) ?: throw IllegalArgumentException("Unknown test ${test.id}")

            for (mirrorType in (test.mirrors ?: testableFn.annotation.mirrors)) {
                for (rotationType in (test.rotations ?: testableFn.annotation.rotations)) {
                    newEntries.add(ScheduledTest(testableFn, mirrorType, rotationType))
                }
            }
        }

        this.testQueue.addAll(newEntries)
    }
}