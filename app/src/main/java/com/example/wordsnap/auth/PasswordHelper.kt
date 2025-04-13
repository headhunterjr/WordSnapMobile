package com.example.wordsnap.auth

import java.security.MessageDigest
import java.security.SecureRandom
import android.util.Base64

object PasswordHelper {

    // Generates a random salt encoded in Base64.
    fun generateSalt(): String {
        val saltBytes = ByteArray(16)
        SecureRandom().nextBytes(saltBytes)
        return Base64.encodeToString(saltBytes, Base64.NO_WRAP)
    }

    // Hashes the password by first hashing it, concatenating with salt, and hashing again.
    fun hashPassword(password: String, salt: String): String {
        // Create SHA256 instance
        val sha256 = MessageDigest.getInstance("SHA-256")
        // First hash: hash the password bytes
        val firstHashBytes = sha256.digest(password.toByteArray(Charsets.UTF_8))
        val firstHash = Base64.encodeToString(firstHashBytes, Base64.NO_WRAP)

        // Concatenate the first hash with the salt and compute the final hash.
        val saltedInput = firstHash + salt
        val finalHashBytes = sha256.digest(saltedInput.toByteArray(Charsets.UTF_8))
        return Base64.encodeToString(finalHashBytes, Base64.NO_WRAP)
    }

    // Verifies the input password against the stored hash and salt.
    fun verifyPassword(password: String, storedHash: String, storedSalt: String): Boolean {
        val computedHash = hashPassword(password, storedSalt)
        return computedHash == storedHash
    }
}
