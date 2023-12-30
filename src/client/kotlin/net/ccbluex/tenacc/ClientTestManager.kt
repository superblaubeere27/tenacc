package net.ccbluex.tenacc

import net.ccbluex.tenacc.api.common.TACCSequenceAdapter
import net.ccbluex.tenacc.features.templates.TemplateInfo
import net.ccbluex.tenacc.impl.TestIdentifier
import net.ccbluex.tenacc.impl.TestManager
import net.ccbluex.tenacc.input.InputManager
import net.ccbluex.tenacc.network.ClientNetworkManager
import net.minecraft.client.MinecraftClient

object ClientTestManager: TestManager() {
    override val isServer: Boolean
        get() = false

    var currentTestContext: ClientTestContext? = null

    var queuedTestContext: ClientTestContext? = null

    override fun createCommonAdapter(): TACCSequenceAdapter = ClientCommonAdapter

    fun init() {
        ClientNetworkManager
    }

    fun tick() {
        if (MinecraftClient.getInstance().player != null && MinecraftClient.getInstance().world != null) {
            val queuedCtx = queuedTestContext

            if (queuedCtx != null) {
                this.queuedTestContext = null

                startTest(queuedCtx.testIdentifier, queuedCtx.templateInfo)
            }
        }
    }

    override fun reset() {
        super.reset()

        InputManager.clearInput()

        queuedTestContext = null
        currentTestContext = null

        println("Client reset")
    }

    override fun failTestError(e: Throwable, reportToOtherSide: Boolean) {
        if (reportToOtherSide) {
             ClientNetworkManager.sendError(e)
        }

        this.reset()
    }

    fun startTest(testIdentifier: TestIdentifier, templateInfo: TemplateInfo) {
        currentTestContext = ClientTestContext(testIdentifier, templateInfo)

        runTestByIdentifier(testIdentifier)
    }

    fun startTestWhenAvailable(testIdentifier: TestIdentifier, templateInfo: TemplateInfo) {
        queuedTestContext = ClientTestContext(testIdentifier, templateInfo)
    }

}