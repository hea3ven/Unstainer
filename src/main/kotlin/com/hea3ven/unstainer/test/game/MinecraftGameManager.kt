package com.hea3ven.unstainer.test.game

import com.hea3ven.unstainer.client.gui.UnstainerResultScreen
import com.hea3ven.unstainer.test.TestResults
import com.mojang.datafixers.Dynamic
import com.mojang.datafixers.types.JsonOps
import net.minecraft.client.MinecraftClient
import net.minecraft.client.gui.screen.ProgressScreen
import net.minecraft.client.gui.screen.SaveLevelScreen
import net.minecraft.datafixers.NbtOps
import net.minecraft.network.chat.TextComponent
import net.minecraft.world.GameMode
import net.minecraft.world.gen.chunk.FlatChunkGeneratorConfig
import net.minecraft.world.level.LevelGeneratorType
import net.minecraft.world.level.LevelInfo
import java.util.*

class MinecraftGameManager : GameManager {

    override fun startNewWorld(minecraft: MinecraftClient) {
        minecraft.openScreen(null)

        val seed = Random().nextLong()
        val levelInfo = LevelInfo(seed, GameMode.CREATIVE, true, false, LevelGeneratorType.FLAT)
        val cfg = FlatChunkGeneratorConfig.getDefaultConfig().toDynamic(NbtOps.INSTANCE).value
        levelInfo.generatorOptions = Dynamic.convert(NbtOps.INSTANCE, JsonOps.INSTANCE, cfg)
        levelInfo.setBonusChest()
        levelInfo.enableCommands()

        minecraft.startIntegratedServer("TestWorld", "Test World", levelInfo)
    }

    override fun stopWorld(minecraft: MinecraftClient) {
        minecraft.world.disconnect()
        minecraft.disconnect(SaveLevelScreen(TextComponent("menu.savingLevel")))
    }

    override fun deleteWorld(minecraft: MinecraftClient, name: String) {
        minecraft.openScreen(ProgressScreen())
        minecraft.levelStorage.deleteLevel(name)
    }

    override fun showResults(minecraft: MinecraftClient, results: TestResults) {
        minecraft.openScreen(UnstainerResultScreen(results))
    }
}