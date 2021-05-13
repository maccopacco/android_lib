package com.maxdreher

import org.junit.Test

class CheckIterableExtensionFunctions {
    @Test
    fun checkOnlyOne_OneEven() {
        assert(listOf(1, 2, 3).onlyOne { it % 2 == 0 })
    }

    @Test
    fun checkOnlyOne_TwoEvens() {
        assert(!listOf(1, 2, 2, 3).onlyOne { it % 2 == 0 })
    }

    @Test
    fun checkOnlyOne_NotExcessive() {
        var checks = 0
        val predicate: (Int) -> Boolean = { num ->
            checks++
            num > 4
        }
        val result = listOf(1, 2, 3, 4, 5, 6, 7, 8, 9, 10).onlyOne(predicate)
        assert(!result) { "Expected result != false" }
        assert(checks == 6) { "Too many checks" }
    }

    @Test
    fun checkOnlyOne_NoMatches() {
        assert(!listOf(1,2,3).onlyOne { it > 10 })
    }
}