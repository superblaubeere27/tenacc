package net.ccbluex.tenacc.impl

import net.ccbluex.tenacc.Clientintegrationtest
import net.ccbluex.tenacc.api.common.TACCSequenceAdapter
import net.ccbluex.tenacc.api.runner.TACCTestRegistry
import net.ccbluex.tenacc.api.runner.TACCTestProvider
import net.ccbluex.tenacc.impl.common.SequenceManager
import net.ccbluex.tenacc.test.DefaultTestProvider
import kotlin.reflect.KClass

abstract class TestManager: TACCTestRegistry {
    val sequenceManager = SequenceManager()
    abstract val isServer: Boolean
    private val registeredTests = ArrayList<TestableFunction>()

    val testProvider: TACCTestProvider

    init {
        this.testProvider = try {
            val clazz = Class.forName(System.getenv("TENACC_TEST_PROVIDER"))

             clazz.getDeclaredConstructor().newInstance() as TACCTestProvider
        } catch (e: Throwable) {
            Clientintegrationtest.logger.error("Failed to load TACTestProvider in TENACC_TEST_PROVIDER env variable: $e")

            DefaultTestProvider
        }

        this.testProvider.registerTests(this)
    }

    abstract fun createCommonAdapter(): TACCSequenceAdapter

    fun runTest(testableFunction: TestableFunction) {
        runCatching {
            testableFunction.testFunction(createCommonAdapter())
        }.onFailure {
            val ex = IllegalStateException("Exception in test launching", it)

            failTestError(ex, true)
        }
    }

    override fun registerTestClass(clazz: KClass<*>) {
        this.registeredTests.addAll(TestParser.parseClass(clazz))
    }

    fun runTestByIdentifier(id: TestIdentifier) {
        val test = findTestById(id) ?: throw IllegalStateException("Unknown test $id")

        this.runTest(test)
    }

    fun findTestById(id: TestIdentifier) = this.registeredTests.find { it.identifier == id }

    open fun reset() {
        this.sequenceManager.reset()
    }

    abstract fun failTestError(e: Throwable, reportToOtherSide: Boolean)
}