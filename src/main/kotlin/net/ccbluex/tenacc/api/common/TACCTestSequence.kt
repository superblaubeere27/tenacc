package net.ccbluex.tenacc.api.common

import net.ccbluex.tenacc.api.client.TACCClientTestAdapter
import net.ccbluex.tenacc.api.errors.TestFailException
import net.ccbluex.tenacc.api.server.TACCServerTestAdapter
import net.ccbluex.tenacc.impl.server.CLIENT_SYNC_FENCE
import net.ccbluex.tenacc.impl.server.SERVER_SYNC_FENCE

interface TACCTestSequence {

    /**
     * Checks [predicate] on every tick until it returns true
     *
     * @param maxTicks if not null, it may suspend for at most [maxTicks] ticks. If `<= 0`, this function immediately
     * returns `false`
     *
     * @return based on the resumption reason.`true` if it is due to the [predicate] returning `true`, `false` if it
     * waited more than [maxTicks]
     */
    suspend fun waitUntil(maxTicks: Int? = null, predicate: () -> Boolean): Boolean

    suspend fun waitUntilOrFail(maxTicks: Int, message: String, predicate: () -> Boolean) {
        if (!waitUntil(maxTicks, predicate)) {
            failTest(message)
        }
    }

    suspend fun waitTicks(ticks: Int)


    fun failTest(message: String) {
        throw TestFailException(message)
    }

    suspend fun <T, L: Iterable<T>> loopByServer(
        data: TACCBox.ServerBox<L>,
        fn: suspend TACCTestSequence.(TACCBox.ServerBox<T>) -> Unit
    )

    suspend fun <T, L: Iterable<T>> loopByClient(
        data: TACCBox.ClientBox<L>,
        fn: suspend TACCTestSequence.(TACCBox.ClientBox<T>) -> Unit
    )

    /**
     * Ensures that both client and server have reached this point on passage.
     *
     * For example:
     * ```
     * clientSequence { waitTicks(Random.nextInt(100)) }
     * serverSequence { waitTicks(Random.nextInt(100)) }
     *
     * // This point might be reached ealier by a side
     * sync()
     *
     * // At this point we know that both sides have reached the sync point
     * ```
     */
    suspend fun sync() {
        client { permitFencePassage(CLIENT_SYNC_FENCE) }
        server { permitFencePassage(SERVER_SYNC_FENCE) }

        waitForFencePassage(CLIENT_SYNC_FENCE, SERVER_SYNC_FENCE)
    }

    /**
     * Waits until every given fence is passed
     */
    suspend fun waitForFencePassage(vararg fenceIds: Int)

    /**
     * Waits until any of the given fences is passed. The fence that was eventually passed is returned.
     */
    suspend fun waitForEitherPassage(vararg fenceIds: Int): Int

    fun permitFencePassage(fenceId: Int = 0)

    fun <T> server(fn: TACCServerTestAdapter.() -> T): TACCBox.ServerBox<T>
    suspend fun <T> serverSequence(fn: suspend TACCTestSequence.(TACCServerTestAdapter) -> T): TACCBox.ServerBox<T>

    fun <T> client(fn: TACCClientTestAdapter.() -> T): TACCBox.ClientBox<T>
    suspend fun <T> clientSequence(fn: suspend TACCTestSequence.(TACCClientTestAdapter) -> T): TACCBox.ClientBox<T>
}