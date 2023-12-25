package net.ccbluex.tenacc.test

import net.ccbluex.tenacc.api.CITest
import net.ccbluex.tenacc.api.CITestClass
import net.ccbluex.tenacc.api.client.InputKey
import net.ccbluex.tenacc.api.common.CITCommonAdapter
import net.minecraft.util.math.BlockPos


@CITestClass("TestNo1")
class TestTestTest {

    @CITest(name = "sampleTest", scenary = "test.structure")
    fun runTest(adapter: CITCommonAdapter) {
        adapter.startSequence {
            server {
                player.teleport(player.serverWorld, 3.313046, 56.000000, -2.700000, 0.0F, 20F)

                permitFencePassage(1)
            }

            waitForFencePassage(1)

            client {
                sendInputs(InputKey.KEY_FORWARDS)
            }

            serverSequence { server ->
                waitUntil(100) { server.player.isOnGround && server.player.blockPos == BlockPos(3, 55, -1) }

                server.log("Player fell into pit")

                permitFencePassage(1)
            }

            waitForFencePassage(1)

            client {
                sendInputs(InputKey.KEY_FORWARDS)
                sendInputs(InputKey.KEY_JUMP, nTicks = 3)
            }

            clientSequence { client ->
                waitUntil(100) { client.player.blockPos == BlockPos(3, 56, 1) && client.player.velocity.z < 0.01 }

                client.log("ran into wall")

                client.clearInputs()

                client.sendInputs(InputKey.KEY_BACKWARDS, InputKey.KEY_LEFT)

                waitUntil(100) { client.player.blockPos == BlockPos(6, 56, 1) && client.player.velocity.x < 0.01 }

                client.log("ran into right wall")

                client.player.yaw = 10.0F
                client.player.pitch = 40.0F

                client.clearInputs()
                client.sendInputs(InputKey.KEY_USE, nTicks = 1)

                waitTicks(5)

                client.log("opened gate")

                client.player.yaw = 0.0F
                client.player.pitch = 20.0F

                client.clearInputs()
                client.sendInputs(InputKey.KEY_FORWARDS)
            }

            serverSequence { server ->
                waitUntil(100) { server.player.isOnGround && server.player.blockPos == BlockPos(6, 55, 4) }

                permitFencePassage(1)
            }

            waitForFencePassage(1)

            adapter.logServer("Test passed.")
        }
    }

}