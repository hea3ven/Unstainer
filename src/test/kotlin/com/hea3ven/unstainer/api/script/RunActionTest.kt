package com.hea3ven.unstainer.api.script

import com.hea3ven.unstainer.api.TestContext
import net.minecraft.util.math.BlockPos
import org.hamcrest.CoreMatchers.`is`
import org.junit.Assert.assertThat
import org.junit.Test

class RunActionTest {

    @Test
    fun finished_beforeExecute_isFalse() {
        val action = RunAction({})

        assertThat(action.finished, `is`(false))
    }

    @Test
    fun finished_afterExecute_isTrue() {
        val action = RunAction({})

        // TODO:       val ctx = Mockito.stub(TestContext::class.java)
        val ctx = TestContext(null, BlockPos.ORIGIN)
        action.onTick(ctx)

        assertThat(action.finished, `is`(true))
    }

    @Test
    fun getFinishesTick_constant_isFalse() {
        val action = RunAction({})

        assertThat(action.finishesTick, `is`(false))
    }

    @Test
    fun onTick_aClosure_runsTheClosure() {
        var wasRun = false
        val action = RunAction({ ctx ->
            wasRun = true
            print("ASD")
        })

        val ctx = TestContext(null, BlockPos.ORIGIN)
        action.onTick(ctx)
        assertThat(wasRun, `is`(true))
    }
}