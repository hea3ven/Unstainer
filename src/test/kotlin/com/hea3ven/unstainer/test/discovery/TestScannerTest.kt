package com.hea3ven.unstainer.test.discovery

import com.hea3ven.unstainer.api.TestRequirement
import com.hea3ven.unstainer.api.UnstainerTest
import com.hea3ven.unstainer.api.script.Script
import org.hamcrest.Matchers.*
import org.junit.Assert.assertThat
import org.junit.Test

class TestScannerTest {
    class OneTest {
        @UnstainerTest(requirement = TestRequirement.CHUNK)
        fun testName(): Script = Script(listOf())
    }

    class MultipleTest {
        @UnstainerTest(requirement = TestRequirement.CHUNK)
        fun multipleTests1(): Script = Script(
                listOf())

        @UnstainerTest(requirement = TestRequirement.SAVE)
        fun multipleTests2(): Script = Script(
                listOf())
    }

    @Test
    fun scan_oneTest_findsOneTest() {
        val scanner = TestScanner()

        val result = scanner.scan(listOf(OneTest()))

        assertThat(result, hasSize(1))
        assertThat(result, hasItem(hasProperty("name", `is`("testName"))))
    }

    @Test
    fun scan_multipleTests_findsTwoTests() {
        val scanner = TestScanner()

        val result = scanner.scan(listOf(MultipleTest()))

        assertThat(result, hasSize(2))
        assertThat(result, hasItem(hasProperty("name", `is`("multipleTests1"))))
        assertThat(result, hasItem(hasProperty("name", `is`("multipleTests2"))))
    }

    @Test
    fun scan_multipleContainers_findsThreeTests() {
        val scanner = TestScanner()

        val result = scanner.scan(listOf(OneTest(), MultipleTest()))

        assertThat(result, hasSize(3))
        assertThat(result, hasItem(hasProperty("name", `is`("testName"))))
        assertThat(result, hasItem(hasProperty("name", `is`("multipleTests1"))))
        assertThat(result, hasItem(hasProperty("name", `is`("multipleTests2"))))
    }

}