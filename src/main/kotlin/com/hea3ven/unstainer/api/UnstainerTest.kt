package com.hea3ven.unstainer.api

import java.lang.annotation.Retention
import java.lang.annotation.RetentionPolicy

@Retention(RetentionPolicy.RUNTIME)
annotation class UnstainerTest(val requirement: TestRequirement, val world: TestWorldType = TestWorldType.FLAT,
        val dimension: String = TestDimension.NETHER) // TODO: make overworld the default
