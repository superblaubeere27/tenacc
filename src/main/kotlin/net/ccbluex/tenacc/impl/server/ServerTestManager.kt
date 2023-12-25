package net.ccbluex.tenacc.impl.server

import net.ccbluex.tenacc.api.common.CITCommonAdapter
import net.ccbluex.tenacc.impl.TestManager
import net.ccbluex.tenacc.impl.TestableFunction
import net.ccbluex.tenacc.impl.network.ServerNetworkManager
import net.minecraft.server.MinecraftServer

class ServerTestManager(
    val server: MinecraftServer
) : TestManager() {

    val networkManager = ServerNetworkManager(this)
    var runningTest: ServerRunningTestContext? = null

    override val isServer: Boolean
        get() = true

    override fun createCommonAdapter(): CITCommonAdapter = ServerCommonAdapter(this)

    fun startTest(server: MinecraftServer, testableFunction: TestableFunction) {
        if (server.playerManager.playerList.size != 1)
            throw IllegalStateException("Tests can only be run if there is only one player online!")

        val player = server.playerManager.playerList.first()

        this.runningTest = ServerRunningTestContext(testableFunction, player)

        networkManager.sendTestStart(this.runningTest!!)

        this.runTest(testableFunction)
    }
}