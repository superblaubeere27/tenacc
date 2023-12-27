package net.ccbluex.tenacc.utils

import net.ccbluex.tenacc.api.errors.ClientErrorException
import net.ccbluex.tenacc.api.errors.TestFailException
import net.ccbluex.tenacc.api.errors.TestSectionFailException
import net.ccbluex.tenacc.api.errors.TestVariantFailException
import net.ccbluex.tenacc.impl.TestableFunction

object TestErrorFormatter {
    fun formatError(e: Throwable, testFunction: TestableFunction): String {
        var isClientSide = false
        var variant: String? = null
        var section: String? = null
        var functionLocation: String? = null

        var currThrowable: Throwable? = e

        val causes = ArrayList<Throwable>()

        while (currThrowable != null) {
            var resetCauses = true

            when (currThrowable) {
                is TestVariantFailException -> variant = currThrowable.variant.name
                is TestSectionFailException -> section = currThrowable.sectionName
                is ClientErrorException -> isClientSide = true
                is TestFailException -> {
                    val causeStackTrace = currThrowable.stackTrace.find { it.className.equals(testFunction.className) && it.methodName.equals(testFunction.functionName) }

                    if (causeStackTrace != null)
                        functionLocation = causeStackTrace.toString()

                    causes.add(currThrowable)
                    resetCauses = false
                }
                else -> {
                    causes.add(currThrowable)

                    resetCauses = false
                }
            }

            if (resetCauses) {
                causes.clear()
            }

            currThrowable = currThrowable.cause
        }

        val sb = StringBuilder()

        val additionalInfo = ArrayList<String>()

        if (isClientSide)
            additionalInfo.add("Client side")

        if (variant != null)
            additionalInfo.add("variant '$variant'")

        if (section != null)
            additionalInfo.add("section '$variant'")

        sb.append(additionalInfo.joinToString { it } + ": ")

        sb.append(causes.joinToString(" caused by ") { it.toString() })

        if (functionLocation != null) {
            sb.append(" [at ").append(functionLocation).append("]")
        }

        return sb.toString()
    }
}