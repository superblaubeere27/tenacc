package net.ccbluex.tenacc.test

import net.ccbluex.tenacc.api.TACCTest
import net.ccbluex.tenacc.api.TACCTestClass
import net.ccbluex.tenacc.api.client.InputKey
import net.ccbluex.tenacc.api.common.TACCSequenceAdapter
import net.ccbluex.tenacc.api.common.TACCTestSequence
import net.ccbluex.tenacc.api.common.TACCTestVariant
import net.ccbluex.tenacc.features.templates.MirrorType
import net.ccbluex.tenacc.features.templates.RotationType
import net.ccbluex.tenacc.utils.Rotation
import net.ccbluex.tenacc.utils.isStandingOnMarkerBlock
import net.ccbluex.tenacc.utils.loadInventory
import net.ccbluex.tenacc.utils.lookDirection
import net.minecraft.block.Block
import net.minecraft.block.Blocks
import org.joml.Vector3f


@TACCTestClass("TestNo1")
class TestTestTest {

    @TACCTest(
        name = "sampleTest",
        scenary = "sampletest.nbt",
        timeout = 500,
        rotations = [RotationType.NONE, RotationType.MINUS_90_DEGREES, RotationType.PLUS_90_DEGREES, RotationType.PLUS_180_DEGREES],
        mirrors = [MirrorType.MIRROR_NONE, MirrorType.MIRROR_Z, MirrorType.MIRROR_X]
    )
    fun runTest(adapter: TACCSequenceAdapter) {
        var block: Block? = null

        val variants = arrayOf(
            TACCTestVariant.DEFAULT,
//            TACCTestVariant.of("with obsidian") {
//                block = Blocks.OBSIDIAN
//            }
        )

        adapter.startSequence(variants) {
            val startPositions = server { getMarkerPositions("start") }

            loopByServer(startPositions) { startPositionBox ->
                server {
                    resetScenery()

                    block?.let {
                        player.serverWorld.setBlockState(getMarkerPos("run_left"), it.defaultState)
                    }

                    val startPosition = openBox(startPositionBox)

                    log("Testing sign $startPosition")

                    player.teleport(
                        player.serverWorld,
                        startPosition.x.toDouble() + 0.5,
                        startPosition.y.toDouble(),
                        startPosition.z.toDouble() + 0.5,
                        0.0F,
                        20F
                    )
                }

                waitTicks(2)
                sync()

                runLoop()
            }

            adapter.logServer("Test passed.")
        }
    }

    private suspend fun TACCTestSequence.runLoop() {
        client {
            player.lookDirection(
                Rotation.fromDirection(
                Vector3f(0.0F, 0.0F, 1.0F),
                templateInfo.transformation
            ))

            clearInputs()
            sendInputs(InputKey.KEY_BACKWARDS, nTicks = 1)
            sendInputs(InputKey.KEY_FORWARDS)
        }

        serverSequence { server ->
            waitUntilOrFail(25, "Failed to reach pit in time") {
                server.player.isStandingOnMarkerBlock(server, "pit")
            }

            server.log("Player fell into pit")

            permitFencePassage(1)
        }

        waitForFencePassage(1)

        client {
            println("PERM!")

            sendInputs(InputKey.KEY_FORWARDS)
            sendInputs(InputKey.KEY_JUMP, nTicks = 10)
        }

        clientSequence { client ->
            waitUntilOrFail(40, "where tf is the fucking run_left marker?") {
                client.player.isStandingOnMarkerBlock(
                    client,
                    "run_left"
                ) && client.player.velocity.horizontalLength() < 0.01
            }

            client.log("ran into wall")

            client.player.lookDirection(
                Rotation.fromDirection(
                    Vector3f(1.0F, 0.0F, 0.0F),
                    client.templateInfo.transformation
                ))

            waitUntil {
                client.player.isStandingOnMarkerBlock(
                    client,
                    "run_forward"
                ) && client.player.velocity.horizontalLength() < 0.01
            }

            client.log("ran into right wall")

            client.player.lookDirection(
                Rotation.fromDirection(
                    Vector3f(0.0F, 0.0F, 1.0F),
                    client.templateInfo.transformation
                ).pitch(40.0F))

            client.clearInputs()
            client.sendInputs(InputKey.KEY_USE, nTicks = 1)

            waitTicks(5)

            client.log("opened gate")

            client.player.lookDirection(
                Rotation.fromDirection(
                    Vector3f(0.0F, 0.0F, 1.0F),
                    client.templateInfo.transformation
                ))

            client.clearInputs()
            client.sendInputs(InputKey.KEY_FORWARDS)
        }

        serverSequence { server ->
            waitUntilOrFail(100, "Failed to reach pit in time") {
                server.player.isStandingOnMarkerBlock(server, "target")
            }

            permitFencePassage(1)
        }

        waitForFencePassage(1)
    }

}