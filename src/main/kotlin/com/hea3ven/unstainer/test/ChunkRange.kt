package com.hea3ven.unstainer.test

import net.minecraft.util.math.Direction

class ChunkRange {

    private var direction = Direction.EAST
    private var step = 0
    private var length = 0
    private var first = true
    private var pos = 0 to 0

    fun next() : Pair<Int, Int>{
        if(length == 0) {
            length = 1
            return pos
        }else {
            pos = pos.first + direction.vector.x to pos.second + direction.vector.z
            step++
            if (step >= length) {
                direction = direction.rotateClockwise(Direction.Axis.Y)
                if (first) {
                    first = false
                }else {
                    length++
                    first = true
                }
                step= 0
            }
            return pos
        }

    }

}
