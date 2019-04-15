package com.hea3ven.unstainer.test.game

import com.hea3ven.unstainer.test.TestResults
import net.minecraft.client.MinecraftClient

interface GameManager {
    fun startNewWorld(minecraft: MinecraftClient)
    fun stopWorld(minecraft: MinecraftClient)
    fun deleteWorld(minecraft: MinecraftClient, name: String)
    fun showResults(minecraft: MinecraftClient, results: TestResults)
}