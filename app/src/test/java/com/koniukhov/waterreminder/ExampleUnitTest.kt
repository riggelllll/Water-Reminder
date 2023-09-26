package com.koniukhov.waterreminder

import org.hamcrest.CoreMatchers.equalTo
import org.hamcrest.MatcherAssert.assertThat
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.RepeatedTest

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleUnitTest {
    @RepeatedTest(3)
    fun addition_isCorrect() {
        val a = 2
        val b = 2
        assertThat(a, equalTo(b))
    }
}