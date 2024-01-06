package net.ccbluex.tenacc.utils

import com.mojang.brigadier.StringReader
import net.minecraft.nbt.NbtElement
import net.minecraft.nbt.StringNbtReader
import java.io.InputStream
import java.nio.charset.Charset

object TenaccResourceManager {

    fun <T> readResource(resourceName: String, fn: (InputStream) -> T): T {
        val stream = TenaccResourceManager::class.java.getResourceAsStream(resourceName)
            ?: throw IllegalArgumentException("Resource $resourceName was not found")

        return stream.use(fn)
    }

    fun readResourceToString(resourceName: String): String {
        val bytes = readResource(resourceName) { it.readAllBytes() }

        return String(bytes, Charset.forName("UTF-8"))
    }

    fun readNbt(resourceName: String): NbtElement {
        val text = readResourceToString(resourceName)

        try {
            return StringNbtReader(StringReader(text)).parseElement()
        } catch (e: Exception) {
            throw IllegalArgumentException("Failed to read nbt-text resource $resourceName", e)
        }
    }
}