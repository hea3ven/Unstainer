package com.hea3ven.unstainer.api.script

import com.hea3ven.unstainer.api.Action
import com.hea3ven.unstainer.api.TestContext

class RunAction(private val closure: (TestContext) -> Any) : Action() {

    override var finished = false

    override val finishesTick = false


    override fun onTick(ctx: TestContext) {
        try {
            closure.invoke(ctx)
        } finally {
            finished = true
        }
    }
}