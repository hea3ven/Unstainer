package com.hea3ven.unstainer.api.script

import com.hea3ven.unstainer.api.TestContext
import net.minecraft.util.math.BlockPos
import org.hamcrest.CoreMatchers.`is`
import org.junit.Assert.assertThat
import org.junit.Test

class TickWaitActionTest {

    @Test
    fun finished_beforeExecute_isFalse() {
        val action = TickWaitAction(1)

        assertThat(action.finished, `is`(false))
    }

    @Test
    fun finished_afterOneTick_isFalse() {
        val action = TickWaitAction(2)

        // TODO:       val ctx = Mockito.stub(TestContext::class.java)
        val ctx = TestContext(null, BlockPos.ORIGIN)
        action.onTick(ctx)

        assertThat(action.finished, `is`(false))
    }

    @Test
    fun finished_afterTwoTick_isFalse() {
        val action = TickWaitAction(2)

        // TODO:       val ctx = Mockito.stub(TestContext::class.java)
        val ctx = TestContext(null, BlockPos.ORIGIN)
        action.onTick(ctx)
        action.onTick(ctx)

        assertThat(action.finished, `is`(true))
    }

    @Test
    fun getFinishesTick_constant_isFalse() {
        val action = TickWaitAction(1)

        assertThat(action.finishesTick, `is`(true))
    }

}