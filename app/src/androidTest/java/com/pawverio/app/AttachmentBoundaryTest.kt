package com.pawverio.app

import android.net.Uri
import androidx.test.platform.app.InstrumentationRegistry
import org.junit.Assert.*
import org.junit.Test

class AttachmentBoundaryTest {
    @Test fun canonicalAliasesPreserveReferencedMediaWhileOrphansArePruned(){
        val context=InstrumentationRegistry.getInstrumentation().targetContext
        val temporary=java.io.File(context.cacheDir,"media-prune-${java.util.UUID.randomUUID()}").apply{mkdirs()}
        val isolated=object:android.content.ContextWrapper(context){override fun getFilesDir()=temporary}
        val directory=java.io.File(temporary,"attachments").apply{mkdirs()}
        val kept=java.io.File(directory,"referenced.jpg").apply{writeText("Fictional image bytes")}
        val orphan=java.io.File(directory,"orphan.pdf").apply{writeText("Fictional document")}
        val outside=java.io.File(temporary,"outside.txt").apply{writeText("Outside attachment boundary")}
        try{
            pruneAttachmentFiles(isolated,listOf(java.io.File(directory,"../attachments/referenced.jpg").absolutePath))
            assertTrue(kept.exists());assertFalse(orphan.exists());assertTrue(outside.exists())
            pruneAttachmentFiles(isolated,emptyList())
            assertFalse(kept.exists());assertTrue(outside.exists())
        }finally{temporary.deleteRecursively()}
    }
    @Test
    fun documentResultCannotReadAnAppPrivateFileThroughFileUri() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        val sentinel = java.io.File(context.filesDir, "attachment-boundary-sentinel.txt")
        sentinel.writeText("Fictional private record")
        try {
            val folder = java.io.File(context.filesDir, "attachments")
            val before = folder.listFiles()?.map { it.name }?.toSet().orEmpty()
            assertThrows(IllegalArgumentException::class.java) {
                copyPrivate(context, Uri.fromFile(sentinel), "record")
            }
            assertEquals(before, folder.listFiles()?.map { it.name }?.toSet().orEmpty())
            assertEquals("Fictional private record", sentinel.readText())
        } finally {
            sentinel.delete()
        }
    }
}
