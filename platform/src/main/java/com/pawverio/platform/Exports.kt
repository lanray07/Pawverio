package com.pawverio.platform

import android.content.Context
import android.graphics.Paint
import android.graphics.pdf.PdfDocument
import android.net.Uri
import android.text.Layout
import android.text.StaticLayout
import android.text.TextDirectionHeuristics
import android.text.TextPaint
import java.io.File
import java.security.SecureRandom
import javax.crypto.Cipher
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.GCMParameterSpec
import javax.crypto.spec.PBEKeySpec
import javax.crypto.spec.SecretKeySpec

/**
 * Caller must show a sensitive-information confirmation before invoking the SAF create-document
 * picker.
 */
class ExportService(private val context: Context) {
    fun writeText(uri: Uri, text: String) {
        require(uri.scheme == "content")
        requireNotNull(context.contentResolver.openOutputStream(uri, "wt"))
            .bufferedWriter(Charsets.UTF_8)
            .use { it.write(text) }
    }

    fun writePdf(uri: Uri, title: String, lines: List<String>) {
        require(uri.scheme == "content")
        val document = PdfDocument()
        val paint =
            TextPaint(Paint.ANTI_ALIAS_FLAG).apply {
                textSize = 12f
                color = android.graphics.Color.rgb(43, 51, 53)
            }
        val heading =
            TextPaint(paint).apply {
                textSize = 22f
                isFakeBoldText = true
            }
        var pageNumber = 0
        var page = document.startPage(PdfDocument.PageInfo.Builder(595, 842, ++pageNumber).create())
        var y = 54f
        fun addBlock(text: String, style: TextPaint = paint) {
            val content = text.ifEmpty { " " }
            val layout =
                StaticLayout.Builder.obtain(content, 0, content.length, style, 515)
                    .setAlignment(Layout.Alignment.ALIGN_NORMAL)
                    .setTextDirection(TextDirectionHeuristics.FIRSTSTRONG_LTR)
                    .setLineSpacing(5f, 1f)
                    .setIncludePad(true)
                    .build()
            for (line in 0 until layout.lineCount) {
                val top = layout.getLineTop(line)
                val height = layout.getLineBottom(line) - top
                if (y + height > 790f) {
                    document.finishPage(page)
                    page =
                        document.startPage(
                            PdfDocument.PageInfo.Builder(595, 842, ++pageNumber).create()
                        )
                    y = 54f
                }
                val canvas = page.canvas
                canvas.save()
                canvas.clipRect(40f, y, 555f, y + height)
                canvas.translate(40f, y - top)
                layout.draw(canvas)
                canvas.restore()
                y += height
            }
        }
        try {
            addBlock(title, heading)
            addBlock("")
            lines.forEach { addBlock(it) }
            document.finishPage(page)
            requireNotNull(context.contentResolver.openOutputStream(uri, "wt"))
                .use(document::writeTo)
        } finally {
            document.close()
        }
    }

    /** Copy attachment into app-private storage, never retain untrusted external file paths. */
    fun importAttachment(uri: Uri): String {
        require(uri.scheme == "content")
        val directory = File(context.filesDir, "attachments").apply { mkdirs() }
        val output = File(directory, java.util.UUID.randomUUID().toString())
        try {
            requireNotNull(context.contentResolver.openInputStream(uri)).use { input ->
                output.outputStream().use { destination ->
                    val buffer = ByteArray(8192)
                    var total = 0L
                    while (true) {
                        val count = input.read(buffer)
                        if (count < 0) break
                        total += count
                        require(total <= 20L * 1024 * 1024) { "Attachment exceeds 20 MB" }
                        destination.write(buffer, 0, count)
                    }
                }
            }
        } catch (error: Exception) {
            output.delete()
            throw error
        }
        return output.name
    }
}

/** Portable password-encrypted format: magic(4), salt(16), nonce(12), authenticated ciphertext. */
object EncryptedBackup {
    private val magic = byteArrayOf(80, 65, 87, 49)

    private fun key(password: CharArray, salt: ByteArray): SecretKeySpec {
        require(password.size >= 12) { "Use at least 12 characters" }
        val spec = PBEKeySpec(password, salt, 210_000, 256)
        return try {
            SecretKeySpec(
                SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256").generateSecret(spec).encoded,
                "AES",
            )
        } finally {
            spec.clearPassword()
        }
    }

    fun encrypt(data: ByteArray, password: CharArray): ByteArray {
        val random = SecureRandom()
        val salt = ByteArray(16).also(random::nextBytes)
        val iv = ByteArray(12).also(random::nextBytes)
        val cipher = Cipher.getInstance("AES/GCM/NoPadding")
        cipher.init(Cipher.ENCRYPT_MODE, key(password, salt), GCMParameterSpec(128, iv))
        cipher.updateAAD(magic)
        return magic + salt + iv + cipher.doFinal(data)
    }

    fun decrypt(data: ByteArray, password: CharArray): ByteArray {
        require(data.size >= 48 && data.take(4).toByteArray().contentEquals(magic)) {
            "Invalid backup"
        }
        val cipher = Cipher.getInstance("AES/GCM/NoPadding")
        cipher.init(
            Cipher.DECRYPT_MODE,
            key(password, data.copyOfRange(4, 20)),
            GCMParameterSpec(128, data.copyOfRange(20, 32)),
        )
        cipher.updateAAD(magic)
        return cipher.doFinal(data.copyOfRange(32, data.size))
    }
}
