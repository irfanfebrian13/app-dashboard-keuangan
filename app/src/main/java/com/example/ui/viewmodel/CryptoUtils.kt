package com.example.ui.viewmodel

import android.util.Base64
import java.security.MessageDigest
import javax.crypto.Cipher
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.SecretKeySpec

object CryptoUtils {
    private const val ALGORITHM = "AES/CBC/PKCS5Padding"
    
    private fun deriveKey(passphrase: String): SecretKeySpec {
        val digest = MessageDigest.getInstance("SHA-256")
        val keyBytes = digest.digest(passphrase.toByteArray(Charsets.UTF_8))
        return SecretKeySpec(keyBytes, "AES")
    }

    fun encrypt(plainText: String, passphrase: String): String {
        val keySpec = deriveKey(passphrase)
        val ivBytes = ByteArray(16) { 0 } // Standard zero IV for simplified sync-import
        val ivSpec = IvParameterSpec(ivBytes)
        
        val cipher = Cipher.getInstance(ALGORITHM)
        cipher.init(Cipher.ENCRYPT_MODE, keySpec, ivSpec)
        
        val encryptedBytes = cipher.doFinal(plainText.toByteArray(Charsets.UTF_8))
        return Base64.encodeToString(encryptedBytes, Base64.DEFAULT or Base64.NO_WRAP)
    }

    fun decrypt(cipherText: String, passphrase: String): String {
        val keySpec = deriveKey(passphrase)
        val ivBytes = ByteArray(16) { 0 }
        val ivSpec = IvParameterSpec(ivBytes)
        
        val cipher = Cipher.getInstance(ALGORITHM)
        cipher.init(Cipher.DECRYPT_MODE, keySpec, ivSpec)
        
        val decodedBytes = Base64.decode(cipherText, Base64.DEFAULT)
        val decryptedBytes = cipher.doFinal(decodedBytes)
        return String(decryptedBytes, Charsets.UTF_8)
    }
}
