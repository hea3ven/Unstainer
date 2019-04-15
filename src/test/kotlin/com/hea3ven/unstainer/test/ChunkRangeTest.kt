package com.hea3ven.unstainer.test

import org.hamcrest.CoreMatchers.`is`
import org.junit.Assert.*
import org.junit.Test

class ChunkRangeTest{
    @Test
    fun testSequence() {
        val range = ChunkRange()

        assertThat(range.next(), `is`(0 to 0))
        assertThat(range.next(), `is`(1 to 0))
        assertThat(range.next(), `is`(1 to 1))
        assertThat(range.next(), `is`(0 to 1))
        assertThat(range.next(), `is`(-1 to 1))
        assertThat(range.next(), `is`(-1 to 0))
        assertThat(range.next(), `is`(-1 to -1))
        assertThat(range.next(), `is`(0 to -1))
        assertThat(range.next(), `is`(1 to -1))
        assertThat(range.next(), `is`(2 to -1))
        assertThat(range.next(), `is`(2 to 0))
        assertThat(range.next(), `is`(2 to 1))
        assertThat(range.next(), `is`(2 to 2))
        assertThat(range.next(), `is`(1 to 2))
    }
}