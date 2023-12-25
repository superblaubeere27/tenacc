package net.ccbluex.tenacc.impl.server

import net.ccbluex.tenacc.api.client.CITClientAdapter
import net.ccbluex.tenacc.api.common.CITestSequence
import net.ccbluex.tenacc.api.server.CITServerAdapter
import net.ccbluex.tenacc.impl.TestManager
import net.ccbluex.tenacc.impl.common.*

class ServerTestSequence(
    sequenceManager: SequenceManager,
    networkHandler: NetworkHandler, override val testManager: ServerTestManager, handler: SuspendableHandler,
) : CommonTestSequence(sequenceManager, networkHandler, handler) {

    override fun server(fn: CITServerAdapter.() -> Unit) {
        fn(ServerAdapter(testManager.runningTest!!))
    }

    override suspend fun serverSequence(fn: suspend CITestSequence.(CITServerAdapter) -> Unit) {
        fn(ServerAdapter(testManager.runningTest!!))
    }

    override fun client(fn: CITClientAdapter.() -> Unit) {}

    override suspend fun clientSequence(fn: suspend CITestSequence.(CITClientAdapter) -> Unit) {}

}