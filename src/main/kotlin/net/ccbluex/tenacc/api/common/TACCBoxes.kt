package net.ccbluex.tenacc.api.common

/**
 * Sometimes we only have specific information on only one side. In order to not interrupt the function flow we can
 * package this information in a box that can only be opened by one side.
 *
 * For example:
 * ```
 * fun server() {
 *     val createdEntity: ServerBox<T> = server {
 *         val entity = createSomeEntityFunction()
 *
 *         entity
 *     }
 *
 *     // on client side ServerBox is empty, on server side it contains the entity.
 *
 *     ... do stuff ...
 *
 *     server {
 *         // We can only open the box on server side in a server/serverSequence {} snippet.
 *         val entity = openBox(createdEntity)
 *     }
 * }
 * ```
 */
sealed class TACCBox<T>(private val valueInternal: T?) {
    val value: T
        get() = valueInternal ?: throw IllegalStateException("tried to access value of empty TACCBox")

    class ServerBox<T> : TACCBox<T> {
        constructor() : super(null)

        internal constructor(value: T) : super(value!!)
    }
    class ClientBox<T> : TACCBox<T> {
        constructor() : super(null)

        constructor(value: T) : super(value!!)
    }
}