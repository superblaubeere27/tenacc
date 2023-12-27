package net.ccbluex.tenacc.impl

import net.ccbluex.tenacc.api.TACCTest
import net.ccbluex.tenacc.api.TACCTestClass
import kotlin.reflect.KClass
import kotlin.reflect.full.createInstance
import kotlin.reflect.full.functions

object TestParser {
    fun parseClass(clazz: KClass<*>): List<TestableFunction> {
        val testClassAnnotation = clazz.annotations.find { it.annotationClass == TACCTestClass::class } as TACCTestClass?
            ?: return emptyList()

        val functions = ArrayList<TestableFunction>()

        for (function in clazz.functions) {
            val annotation = function.annotations.find { it.annotationClass == TACCTest::class } as TACCTest?

            if (annotation == null)
                continue

            val fn = TestableFunction(
                TestIdentifier(testClassAnnotation.name, annotation.name),
                clazz.qualifiedName!!,
                function.name,
                annotation
            ) {
                val instance = clazz.createInstance()

                function.call(instance, it)
            }

            functions.add(fn)
        }

        return functions
    }
}