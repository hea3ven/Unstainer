package com.hea3ven.unstainer.test

import com.hea3ven.unstainer.api.TestContext
import com.hea3ven.unstainer.api.script.Script
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World

class TestState(val currentTest: TestInstance, private val chunk: Pair<Int, Int>) {

    private var script: Script = currentTest.testMethod.invoke(currentTest.testObject) as Script
    private var step = 0

    var error: Throwable? = null
        private set

    val finished
        get() = error != null || script.actions.size <= step


    val result: TestResult?
        get() = if (!finished) {
            null
        } else if (error != null) {
            if (error is AssertionError) {
                TestResult.FAIL
            } else {
                TestResult.ERROR
            }
        } else {
            TestResult.SUCCESS
        }

    fun tick(world: World) {
        if (finished) {
            return
        }
        do {
            val action = script.actions[step]
            try {
                action.onTick(TestContext(world, BlockPos(chunk.first * 16.0, 0.0, chunk.second * 16.0)))
                if (action.finished) {
                    step++
                }
            } catch (e: Throwable) {
                error = e
            }
        } while (!action.finishesTick && !finished)
    }

}
