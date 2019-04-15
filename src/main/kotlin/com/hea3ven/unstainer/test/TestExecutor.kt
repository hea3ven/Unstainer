package com.hea3ven.unstainer.test

import com.hea3ven.unstainer.test.game.GameManager
import net.minecraft.client.MinecraftClient
import net.minecraft.client.network.packet.PlayerPositionLookS2CPacket
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.world.GameMode
import net.minecraft.world.World
import net.minecraft.world.dimension.DimensionType
import org.apache.logging.log4j.LogManager
import java.util.*

class TestExecutor(tests: List<TestInstance>, val gameManager: GameManager) {

    private val log = LogManager.getLogger("tests")

    private val tests: MutableList<TestInstance> = tests.toMutableList()

    val results = TestResults(tests)

    private val chunkConsumer = ChunkRange()

    private var testState: TestState? = null

    private var asd = true
    fun onTick(world: World?) {
        if (world != null && world.dimension.type == DimensionType.OVERWORLD) {
            if (!world.isClient && world.players.isNotEmpty()) {
                val state = testState
                if (state == null || state.finished) {
                    if (state != null) {
                        results.set(state.currentTest, state.result)
                        log.info("Test {} finished: {}", state.currentTest.name, state.result)
                        if(state.error != null){
                            log.error("Test error", state.error)
                        }
                    }
                    if (tests.size > 0) {
                        val chunk = chunkConsumer.next()
                        testState = TestState(tests.removeAt(0), chunk)
                        log.info("Starting test {} {}/{}", testState?.currentTest?.name,
                                results.results.count { it.value != null } + 1, results.results.count())
                        val player = world.players[0] as ServerPlayerEntity
                        player.interactionManager.gameMode = GameMode.SPECTATOR
                        player.networkHandler.teleportRequest(chunk.first * 16 - 2.0, 7.0, chunk.second * 16 - 2.0,
                                -45.0f, 55.0f, EnumSet.allOf(PlayerPositionLookS2CPacket.Flag::class.java))
                        //                        player.setPositionAnglesAndUpdate(-5.0, 8.0, -5.0, 30.0f, -45.0f)
                    } else {
                        testState = null
                    }
                } else {
                    state.tick(world)
                }
                if (asd && results.progress == 1.0f) {
                    log.info("Finished tests: ${results.results.values.count { it == TestResult.SUCCESS }} successful, ${results.results.values.count { it == TestResult.FAIL }} failed, ${results.results.values.count { it == TestResult.ERROR }} error")
                    asd = false
                    closeWorld = true
                }
            }
        }

    }

    var createWorld = false
    var closeWorld = false

    fun onClientTick(minecraft: MinecraftClient) {
        if (createWorld) {
            createWorld = false
            gameManager.startNewWorld(minecraft)
        }
        if (closeWorld) {
            closeWorld = false
            gameManager.stopWorld(minecraft)
            gameManager.deleteWorld(minecraft, "TestWorld")
            gameManager.showResults(minecraft, results)
        }

    }

    fun start() {
        createWorld = true
    }

}
