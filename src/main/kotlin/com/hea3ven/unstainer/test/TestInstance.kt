package com.hea3ven.unstainer.test

import com.hea3ven.unstainer.api.UnstainerTest
import java.lang.reflect.Method

class TestInstance(val name: String, val config: UnstainerTest?, val testClass: Class<out Any>, val testObject: Any,
        val testMethod: Method)