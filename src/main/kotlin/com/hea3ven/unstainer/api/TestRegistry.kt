package com.hea3ven.unstainer.api

object TestRegistry {

	val containers: List<Any>
		get() = containerList

	private val containerList = mutableListOf<Any>()

	fun register(testContainer: Any) {
		containerList.add(testContainer)
	}
}