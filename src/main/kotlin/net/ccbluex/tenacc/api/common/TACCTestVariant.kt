package net.ccbluex.tenacc.api.common

/**
 * A variant of a Test. For example Setting A = mode A, Setting B = mode C
 *
 */
abstract class TACCTestVariant(val name: String) {
    companion object {
        val DEFAULT: TACCTestVariant = DefaultTACCTestVariant
        val DEFAULT_VARIANTS: Array<TACCTestVariant> = arrayOf(DEFAULT)

        fun of(name: String, applyFn: suspend TACCTestSequence.() -> Unit): TACCTestVariant = FunctionTACCTestVariant(name, applyFn)

        /**
         * Creates combinations of the given variants. For example if we put in `[[a, b]. [c, d]]`, this function would
         * return `[ac, ad, bc, bd]`
         *
         */
        fun combine(vararg variantCollections: Array<TACCTestVariant>): Array<TACCTestVariant> {
            val combinations = createCombinations(variantCollections)

            return combinations.map { CombinedTACCTestVariant(it) }.toTypedArray()
        }

        internal fun <T> createCombinations(types: Array<out Array<T>>): List<List<T>> {
            val first = types[0].map { listOf(it) }

            return createCombinationsRecursive(first, 1, types)
        }

        private fun <T> createCombinationsRecursive(currentVariants: List<List<T>>, currentIndex: Int, types: Array<out Array<T>>): List<List<T>> {
            if (currentIndex >= types.size)
                return currentVariants

            val newVariants = ArrayList<List<T>>()

            for (t in types[currentIndex]) {
                for (currentVariant in currentVariants) {
                    val list = ArrayList<T>(currentVariant.size + 1)

                    list.addAll(currentVariant)
                    list.add(t)

                    newVariants.add(list)
                }
            }

            return createCombinationsRecursive(newVariants, currentIndex + 1, types)
        }

    }

    abstract suspend fun apply(adapter: TACCTestSequence)
}

private class CombinedTACCTestVariant(
    private val combinedVariants: List<TACCTestVariant>
): TACCTestVariant(nameForVariants(combinedVariants)) {

    override suspend fun apply(adapter: TACCTestSequence) {
        for (combinedVariant in this.combinedVariants) {
            combinedVariant.apply(adapter)
        }
    }

    companion object {
        /**
         * Creates a name for the combined variants. `[a, c]` as variants will be `"a + b"`
         */
        private fun nameForVariants(variants: List<TACCTestVariant>): String {
            return variants.joinToString(" + ") { it.name }
        }
    }
}

private class FunctionTACCTestVariant(name: String, private val fn: suspend TACCTestSequence.() -> Unit): TACCTestVariant(name) {
    override suspend fun apply(sequence: TACCTestSequence) {
        fn(sequence)
    }

}

private object DefaultTACCTestVariant: TACCTestVariant("default") {
    override suspend fun apply(sequence: TACCTestSequence) {
        // Noop.
    }

}