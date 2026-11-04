package com.pawverio.platform

import org.junit.Assert.*
import org.junit.Test

class EncryptedBackupTest {
    @Test fun roundTripPreservesUtf8AndUsesRandomSalt() {
        val input = "Bella — medication record 日本語".toByteArray()
        val first = EncryptedBackup.encrypt(input,"a strong backup password".toCharArray())
        val second = EncryptedBackup.encrypt(input,"a strong backup password".toCharArray())
        assertFalse(first.contentEquals(second)); assertArrayEquals(input,EncryptedBackup.decrypt(first,"a strong backup password".toCharArray()))
    }
    @Test fun authenticationRejectsTampering() {
        val bytes = EncryptedBackup.encrypt("private".toByteArray(),"a strong backup password".toCharArray())
        bytes[bytes.lastIndex] = (bytes.last().toInt() xor 1).toByte()
        assertThrows(Exception::class.java) { EncryptedBackup.decrypt(bytes,"a strong backup password".toCharArray()) }
    }
    @Test fun wrongPasswordFails() {
        val bytes = EncryptedBackup.encrypt("private".toByteArray(),"a strong backup password".toCharArray())
        assertThrows(Exception::class.java) { EncryptedBackup.decrypt(bytes,"incorrect password here".toCharArray()) }
    }
}
