package com.example.domain.auth

import java.security.MessageDigest
import java.security.SecureRandom
import android.util.Base64

object PasswordHelper {

    fun generateSalt(): String {
        val saltBytes = ByteArray(16)
        SecureRandom().nextBytes(saltBytes)
        return Base64.encodeToString(saltBytes, Base64.NO_WRAP)
    }

    fun hashPassword(password: String, salt: String): String {
        val sha256 = MessageDigest.getInstance("SHA-256")
        val firstHashBytes = sha256.digest(password.toByteArray(Charsets.UTF_8))
        val firstHash = Base64.encodeToString(firstHashBytes, Base64.NO_WRAP)

        val saltedInput = firstHash + salt
        val finalHashBytes = sha256.digest(saltedInput.toByteArray(Charsets.UTF_8))
        return Base64.encodeToString(finalHashBytes, Base64.NO_WRAP)
    }

    fun verifyPassword(password: String, storedHash: String, storedSalt: String): Boolean {
        val computedHash = hashPassword(password, storedSalt)
        return computedHash == storedHash
    }
}
