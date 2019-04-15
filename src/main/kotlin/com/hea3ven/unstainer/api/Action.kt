package com.hea3ven.unstainer.api

abstract class Action {
    abstract val finished: Boolean
    abstract val finishesTick: Boolean
    abstract fun onTick(ctx: TestContext)
}
