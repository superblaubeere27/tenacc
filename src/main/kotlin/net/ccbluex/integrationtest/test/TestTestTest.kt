package net.ccbluex.integrationtest.test

import kotlinx.coroutines.delay
import net.ccbluex.integrationtest.api.CITest
import net.ccbluex.integrationtest.api.CITestClass
import net.ccbluex.integrationtest.api.common.CITCommonAdapter


@CITestClass("TestNo1")
object TestTestTest {

    @CITest(name = "sampleTest", scenary = "test.structure")
    fun runTest(adapter: CITCommonAdapter) {
        adapter.startSequence {
            println("Start!")

            server {
                permitFencePassage(5)
            }

            syncFence(5)

            println("10 ticks have passed")
        }
    }

    suspend fun doShit() {
        println("a")
        delay(1000L)
        println("b")
    }

}