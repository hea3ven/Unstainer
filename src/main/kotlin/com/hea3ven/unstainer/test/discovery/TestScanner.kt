package com.hea3ven.unstainer.test.discovery

import com.hea3ven.unstainer.api.UnstainerTest
import com.hea3ven.unstainer.test.TestInstance

class TestScanner {
	fun scan(testContainers: List<Any>): List<TestInstance> {
		return testContainers .flatMap(::scanTestContainer)
	}

	private fun scanTestContainer(testContainer: Any): List<TestInstance> {
		val klass = testContainer.javaClass
		return klass.methods.filter { it.isAnnotationPresent(UnstainerTest::class.java) }.map {
			val unstainerTest = it.getDeclaredAnnotation(UnstainerTest::class.java)
			TestInstance(it.name, unstainerTest, klass, testContainer, it)
		}

	}

}