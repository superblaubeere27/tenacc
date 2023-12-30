package net.ccbluex.tenacc.test

import net.ccbluex.tenacc.api.TACCTest
import net.ccbluex.tenacc.api.TACCTestClass
import net.ccbluex.tenacc.api.client.InputKey
import net.ccbluex.tenacc.api.common.TACCBox
import net.ccbluex.tenacc.api.common.TACCSequenceAdapter
import net.ccbluex.tenacc.api.common.TACCTestSequence
import net.ccbluex.tenacc.api.common.TACCTestVariant
import net.ccbluex.tenacc.features.templates.MirrorType
import net.ccbluex.tenacc.features.templates.RotationType
import net.ccbluex.tenacc.utils.Rotation
import net.ccbluex.tenacc.utils.isStandingOnMarkerBlock
import net.ccbluex.tenacc.utils.lookDirection
import net.minecraft.block.Block
import net.minecraft.block.Blocks
import net.minecraft.item.ItemStack
import net.minecraft.item.Items
import net.minecraft.util.math.BlockPos
import org.joml.Vector3f


@TACCTestClass("GeneralTest")
class GeneralTest {

    @TACCTest(
        name = "testBasicFunctionality",
        scenary = "general_test.nbt",
        timeout = 500,
        rotations = [RotationType.NONE, RotationType.MINUS_90_DEGREES, RotationType.PLUS_90_DEGREES, RotationType.PLUS_180_DEGREES],
        mirrors = [MirrorType.MIRROR_NONE, MirrorType.MIRROR_Z, MirrorType.MIRROR_X]
    )
    fun testBasicFunctionality(adapter: TACCSequenceAdapter) {
        var block: Block? = null

        val variants = arrayOf(
            TACCTestVariant.DEFAULT,
//            TACCTestVariant.of("impossible version") {
//                block = Blocks.OBSIDIAN
//            }
        )

        adapter.startSequence(variants) {
            val startPositions = server { getMarkerPositions("start") }

            loopByServer(startPositions) { startPositionBox ->
                server<Unit> {
                    resetScenery()

                    block?.let {
                        player.serverWorld.setBlockState(getMarkerPos("run_left"), it.defaultState)
                    }
                }

                waitTicks(2)
                sync()

                runLoop(startPositionBox)
            }

            adapter.logServer("Test passed.")
        }
    }

    @TACCTest(
        name = "testImpossible",
        scenary = "general_test.nbt",
        timeout = 500
    )
    fun testImpossible(adapter: TACCSequenceAdapter) {
        var block: Block? = null

        val variants = arrayOf(
            TACCTestVariant.DEFAULT,
            TACCTestVariant.of("impossible version") {
                block = Blocks.OBSIDIAN
            }
        )

        adapter.startSequence(variants) {
            val startPositions = server { getMarkerPositions("start") }

            loopByServer(startPositions) { startPositionBox ->
                server<Unit> {
                    resetScenery()

                    block?.let {
                        player.serverWorld.setBlockState(getMarkerPos("turn_left"), it.defaultState)
                    }
                }

                waitTicks(2)
                sync()

                runLoop(startPositionBox)
            }

            adapter.logServer("Test passed.")
        }
    }

    private suspend fun TACCTestSequence.runLoop(serverBox: TACCBox.ServerBox<BlockPos>) {
        server {
            player.inventory.setStack(0, ItemStack(Items.OAK_WOOD, 4))

            val startPosition = openBox(serverBox)

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

        client {
            player.lookDirection(
                Rotation.fromDirection(
                Vector3f(0.0F, 0.0F, -1.0F),
                templateInfo.transformation
            ))

            clearInputs()
            sendInputs(InputKey.KEY_BACKWARDS, nTicks = 1)
            sendInputs(InputKey.KEY_FORWARDS)
        }

        clientSequence { client ->
            waitUntilOrFail(80, "where tf is the fucking run_left marker?") {
                client.player.isStandingOnMarkerBlock(
                    client,
                    "turn_left"
                ) && client.player.velocity.horizontalLength() < 0.01
            }

            client.log("ran into wall")

            client.player.lookDirection(
                Rotation.fromDirection(
                    Vector3f(-1.0F, 0.0F, 0.0F),
                    client.templateInfo.transformation
                ))
            client.sendInputs(InputKey.KEY_USE, nTicks = 2)

            waitUntil {
                client.player.isStandingOnMarkerBlock(
                    client,
                    "begin_scaffold"
                )
            }

            client.log("found scaffold start")

            client.player.lookDirection(
                Rotation.fromDirection(
                    Vector3f(1.0F, 0.0F, 0.0F),
                    client.templateInfo.transformation
                ).pitch(80.0F))

            client.player.inventory.selectedSlot = 0

            client.clearInputs()
            client.sendInputs(InputKey.KEY_USE, InputKey.KEY_BACKWARDS, InputKey.KEY_SNEAK)
        }

        serverSequence { server ->
            waitUntilOrFail(200, "Failed to reach goal in time") {
                server.player.isStandingOnMarkerBlock(server, "end")
            }
        }

        sync()
    }

}