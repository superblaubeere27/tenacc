package net.ccbluex.tenacc.utils

import java.io.DataOutput
import java.io.DataOutputStream
import java.io.File
import java.io.FileOutputStream
import java.security.MessageDigest
import java.util.HexFormat
import kotlin.io.path.Path

private const val TENACC_FOLDER = "tenacc"

object TenaccSerializer {

    fun suggestNameForContent(content: ByteArray): String {
        val hash = MessageDigest.getInstance("SHA-256").digest(content)

        return HexFormat.of().formatHex(hash).substring(0..15)
    }

    /**
     * Creates a file with the given filename in the folder, opens it
     * and calls [writer] with a data output to it.
     *
     * @return the output file
     */
    fun writeToDataOutput(
        runDirectory: File,
        subfolder: String,
        fileName: String,
        writer: (DataOutput) -> Unit
    ): File {
        val outFolder = Path(runDirectory.absolutePath, TENACC_FOLDER, subfolder).toFile()

        outFolder.mkdirs()

        val outputFile = File(outFolder, fileName)

        val stream = DataOutputStream(FileOutputStream(outputFile))

        stream.use {
            writer(it)
        }

        return outputFile
    }
}