package net.ccbluex.integrationtest.api

annotation class CITest(
    /**
     * Must be unique in a [CITestClass].
     */
    val name: String,
    /**
     * The structure block file name used as the test scenary.
     */
    val scenary: String
)
