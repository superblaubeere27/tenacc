package net.ccbluex.tenacc.api.common

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

class TACCTestVariantTest {

    private fun <T> List<List<T>>.toStableString(): String {
        return this.joinToString(",") { it.joinToString("") { inner -> inner.toString() } }
    }

    @Test
    fun testCreateCombinations() {
        assertEquals("a,b,c", TACCTestVariant.createCombinations(arrayOf(arrayOf("a", "b", "c"))).toStableString())
        assertEquals("aa,ba,ca,ab,bb,cb,ac,bc,cc", TACCTestVariant.createCombinations(arrayOf(arrayOf("a", "b", "c"), arrayOf("a", "b", "c"))).toStableString())
    }
}