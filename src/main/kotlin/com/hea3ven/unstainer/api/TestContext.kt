package com.hea3ven.unstainer.api

import net.minecraft.util.math.BlockPos
import net.minecraft.world.World

data class TestContext(private val worldI: World?, val origin: BlockPos) {
    val world: World
        get() = worldI ?: throw RuntimeException("ASD")
}