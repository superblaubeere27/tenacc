package net.ccbluex.tenacc.impl.server

import net.ccbluex.tenacc.api.client.TACCClientTestAdapter
import net.ccbluex.tenacc.api.common.TACCBox
import net.ccbluex.tenacc.api.common.TACCTestSequence
import net.ccbluex.tenacc.api.server.TACCServerTestAdapter
import net.ccbluex.tenacc.impl.common.*

const val CONTINUE_LOOP_BY_SERVER: Int = 1001
const val BREAK_LOOP_BY_SERVER: Int = 1002
const val CONTINUE_LOOP_BY_CLIENT: Int = 1003
const val BREAK_LOOP_BY_CLIENT: Int = 1004

const val CLIENT_FINISHED_LOOP_ITERATION: Int = 1005
const val SERVER_FINISHED_LOOP_ITERATION: Int = 1006

const val SERVER_SYNC_FENCE: Int = 1007
const val CLIENT_SYNC_FENCE: Int = 1008

class ServerTestSequence(
    sequenceManager: SequenceManager,
    networkHandler: NetworkHandler, override val testManager: ServerTestManager, handler: SuspendableHandler,
) : CommonTestSequence(sequenceManager, networkHandler, handler) {

    override suspend fun <T, L: Iterable<T>> loopByServer(
        data: TACCBox.ServerBox<L>,
        fn: suspend TACCTestSequence.(TACCBox.ServerBox<T>) -> Unit
    ) {
        for (t in data.value) {
            permitFencePassage(CONTINUE_LOOP_BY_SERVER)
            waitForFencePassage(CONTINUE_LOOP_BY_SERVER)

            fn(TACCBox.ServerBox(t))

            waitForFencePassage(CLIENT_FINISHED_LOOP_ITERATION)
        }

        permitFencePassage(BREAK_LOOP_BY_SERVER)
        waitForFencePassage(BREAK_LOOP_BY_SERVER)
    }

    override suspend fun <T, L: Iterable<T>> loopByClient(
        data: TACCBox.ClientBox<L>,
        fn: suspend TACCTestSequence.(TACCBox.ClientBox<T>) -> Unit
    ) {
        while (true) {
            val permit = waitForEitherPassage(CONTINUE_LOOP_BY_CLIENT, BREAK_LOOP_BY_CLIENT)

            if (permit == BREAK_LOOP_BY_CLIENT) {
                break
            }

            fn(TACCBox.ClientBox())

            permitFencePassage(SERVER_FINISHED_LOOP_ITERATION)
        }
    }

    override fun <T> server(fn: TACCServerTestAdapter.() -> T): TACCBox.ServerBox<T> {
        return TACCBox.ServerBox(fn(ServerTestAdapter(testManager.runningTest!!)))
    }

    override suspend fun <T> serverSequence(fn: suspend TACCTestSequence.(TACCServerTestAdapter) -> T): TACCBox.ServerBox<T> {
        return TACCBox.ServerBox(fn(ServerTestAdapter(testManager.runningTest!!)))
    }

    override fun <T> client(fn: TACCClientTestAdapter.() -> T): TACCBox.ClientBox<T> {
        return TACCBox.ClientBox()
    }

    override suspend fun <T> clientSequence(fn: suspend TACCTestSequence.(TACCClientTestAdapter) -> T): TACCBox.ClientBox<T> {
        return TACCBox.ClientBox()
    }

}