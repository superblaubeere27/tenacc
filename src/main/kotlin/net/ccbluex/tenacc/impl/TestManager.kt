package net.ccbluex.tenacc.impl

import net.ccbluex.tenacc.api.common.CITCommonAdapter
import net.ccbluex.tenacc.impl.common.SequenceManager
import net.ccbluex.tenacc.test.TestTestTest
import kotlin.reflect.KClass

abstract class TestManager {
    val sequenceManager = SequenceManager()
    abstract val isServer: Boolean
    private val registeredTests = ArrayList<TestableFunction>()

    init {
        registerTestClass(TestTestTest::class)
    }

    abstract fun createCommonAdapter(): CITCommonAdapter

    fun runTest(testableFunction: TestableFunction) {
        testableFunction.testFunction(createCommonAdapter())
    }

    fun registerTestClass(testClass: KClass<*>) {
        this.registeredTests.addAll(TestParser.parseClass(testClass))
    }

    fun runTestByIdentifier(id: TestIdentifier) {
        val test = findTestById(id) ?: throw IllegalStateException("Unknown test $id")

        this.runTest(test)
    }

    fun findTestById(id: TestIdentifier) = this.registeredTests.find { it.identifier == id }
}