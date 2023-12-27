package net.ccbluex.tenacc

import net.ccbluex.tenacc.api.common.TACCSequenceAdapter
import net.ccbluex.tenacc.features.templates.TemplateInfo
import net.ccbluex.tenacc.impl.TestIdentifier
import net.ccbluex.tenacc.impl.TestManager
import net.ccbluex.tenacc.input.InputManager
import net.ccbluex.tenacc.network.ClientNetworkManager

object ClientTestManager: TestManager() {
    override val isServer: Boolean
        get() = false

    var currentTestContext: ClientTestContext? = null

    override fun createCommonAdapter(): TACCSequenceAdapter = ClientCommonAdapter

    fun init() {
        ClientNetworkManager
    }

    override fun reset() {
        super.reset()

        InputManager.clearInput()

        currentTestContext = null

        println("Client reset")
    }

    override fun failTestError(e: Throwable, reportToOtherSide: Boolean) {
        this.reset()

        if (reportToOtherSide) {
            ClientNetworkManager.sendError(e)
        }
    }

    fun startTest(testIdentifier: TestIdentifier, templateInfo: TemplateInfo) {
        currentTestContext = ClientTestContext(testIdentifier, templateInfo)

        runTestByIdentifier(testIdentifier)
    }

}