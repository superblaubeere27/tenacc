package net.ccbluex.tenacc.api.runner

import kotlin.reflect.KClass

interface TACCTestRegistry {
    fun registerTestClass(clazz: KClass<*>)
}