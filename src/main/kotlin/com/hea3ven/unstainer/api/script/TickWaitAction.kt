package com.hea3ven.unstainer.api.script

import com.hea3ven.unstainer.api.Action
import com.hea3ven.unstainer.api.TestContext

class TickWaitAction(private var count: Int) : Action() {
    override val finished: Boolean
        get() = count <= 0

    override val finishesTick = true

    override fun onTick(ctx: TestContext) {
        count--
    }
}