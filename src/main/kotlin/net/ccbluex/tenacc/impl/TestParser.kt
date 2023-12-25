package net.ccbluex.tenacc.impl

import net.ccbluex.tenacc.api.CITest
import net.ccbluex.tenacc.api.CITestClass
import kotlin.reflect.KClass
import kotlin.reflect.full.createInstance
import kotlin.reflect.full.functions

object TestParser {
    fun parseClass(clazz: KClass<*>): List<TestableFunction> {
        val testClassAnnotation = clazz.annotations.find { it.annotationClass == CITestClass::class } as CITestClass?
            ?: return emptyList()

        val functions = ArrayList<TestableFunction>()

        for (function in clazz.functions) {
            val annotation = function.annotations.find { it.annotationClass == CITest::class } as CITest?

            if (annotation == null)
                continue

            val fn = TestableFunction(
                TestIdentifier(testClassAnnotation.name, annotation.name),
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