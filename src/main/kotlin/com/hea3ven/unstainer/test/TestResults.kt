package com.hea3ven.unstainer.test

class TestResults(tests: List<TestInstance>) {

    val results: MutableMap<TestInstance, TestResult?> = tests.map { it to null }.toMap().toMutableMap()

    val progress: Float
        get() = results.values.filterNotNull().size.toFloat() / results.size

    fun set(test: TestInstance, success: TestResult?) {
        results[test] = success
    }

}