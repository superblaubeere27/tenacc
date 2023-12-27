package net.ccbluex.tenacc

import net.ccbluex.tenacc.api.client.TACCClientTestAdapter
import net.ccbluex.tenacc.api.common.TACCBox
import net.ccbluex.tenacc.api.common.TACCTestSequence
import net.ccbluex.tenacc.api.server.TACCServerTestAdapter
import net.ccbluex.tenacc.impl.TestManager
import net.ccbluex.tenacc.impl.common.CommonTestSequence
import net.ccbluex.tenacc.impl.common.NetworkHandler
import net.ccbluex.tenacc.impl.common.SequenceManager
import net.ccbluex.tenacc.impl.common.SuspendableHandler
import net.ccbluex.tenacc.impl.server.*

class ClientTestSequence(
    sequenceManager: SequenceManager,
    networkHandler: NetworkHandler, handler: SuspendableHandler
) : CommonTestSequence(sequenceManager, networkHandler, handler) {
    override val testManager: TestManager
        get() = ClientTestManager

    override suspend fun <T, L: Iterable<T>> loopByServer(
        data: TACCBox.ServerBox<L>,
        fn: suspend TACCTestSequence.(TACCBox.ServerBox<T>) -> Unit
    ) {
        while (true) {
            val permit = waitForEitherPassage(CONTINUE_LOOP_BY_SERVER, BREAK_LOOP_BY_SERVER)

            if (permit == BREAK_LOOP_BY_SERVER) {
                break
            }

            fn(TACCBox.ServerBox())

            permitFencePassage(CLIENT_FINISHED_LOOP_ITERATION)
        }
    }

    override suspend fun <T, L: Iterable<T>> loopByClient(
        data: TACCBox.ClientBox<L>,
        fn: suspend TACCTestSequence.(TACCBox.ClientBox<T>) -> Unit
    ) {
        for (t in data.value) {
            permitFencePassage(CONTINUE_LOOP_BY_CLIENT)
            waitForFencePassage(CONTINUE_LOOP_BY_CLIENT)

            fn(TACCBox.ClientBox(t))

            waitForFencePassage(SERVER_FINISHED_LOOP_ITERATION)
        }

        permitFencePassage(BREAK_LOOP_BY_CLIENT)
        waitForFencePassage(BREAK_LOOP_BY_CLIENT)
    }

    override fun <T> server(fn: TACCServerTestAdapter.() -> T): TACCBox.ServerBox<T> {
        return TACCBox.ServerBox()
    }

    override suspend fun <T> serverSequence(fn: suspend TACCTestSequence.(TACCServerTestAdapter) -> T): TACCBox.ServerBox<T> {
        return TACCBox.ServerBox()
    }

    override fun <T> client(fn: TACCClientTestAdapter.() -> T): TACCBox.ClientBox<T> {
        return TACCBox.ClientBox(fn(ClientClientTestAdapter))
    }

    override suspend fun <T> clientSequence(fn: suspend TACCTestSequence.(TACCClientTestAdapter) -> T): TACCBox.ClientBox<T> {
        return TACCBox.ClientBox(fn(ClientClientTestAdapter))
    }

}