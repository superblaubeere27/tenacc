package net.ccbluex.tenacc.api

@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
annotation class CITestClass(val name: String)
