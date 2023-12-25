package net.ccbluex.tenacc

import net.ccbluex.tenacc.api.client.CITClientAdapter
import net.ccbluex.tenacc.api.common.CITestSequence
import net.ccbluex.tenacc.api.server.CITServerAdapter
import net.ccbluex.tenacc.impl.common.CommonTestSequence
import net.ccbluex.tenacc.impl.common.NetworkHandler
import net.ccbluex.tenacc.impl.common.SequenceManager
import net.ccbluex.tenacc.impl.common.SuspendableHandler

class ClientTestSequence(
    sequenceManager: SequenceManager,
    networkHandler: NetworkHandler, handler: SuspendableHandler
) : CommonTestSequence(sequenceManager, networkHandler, handler) {

    override fun server(fn: CITServerAdapter.() -> Unit) {
    }

    override suspend fun serverSequence(fn: suspend CITestSequence.(CITServerAdapter) -> Unit) {
    }

    override fun client(fn: CITClientAdapter.() -> Unit) {
        fn(ClientAdapter)
    }

    override suspend fun clientSequence(fn: suspend CITestSequence.(CITClientAdapter) -> Unit) {
        fn(ClientAdapter)
    }

}