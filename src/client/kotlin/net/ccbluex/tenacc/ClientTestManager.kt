package net.ccbluex.tenacc

import net.ccbluex.tenacc.api.common.CITCommonAdapter
import net.ccbluex.tenacc.impl.TestManager
import net.ccbluex.tenacc.input.InputManager
import net.ccbluex.tenacc.network.ClientNetworkManager

object ClientTestManager: TestManager() {
    override val isServer: Boolean
        get() = false

    override fun createCommonAdapter(): CITCommonAdapter = ClientCommonAdapter

    fun init() {
        ClientNetworkManager
    }

    fun reset() {
        InputManager.clearInput()
    }

}