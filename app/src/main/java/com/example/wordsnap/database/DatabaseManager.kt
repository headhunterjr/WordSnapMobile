package com.example.wordsnap.database

import android.content.ContentValues
import android.content.Context
import android.util.Log
import java.time.LocalDateTime
import kotlin.random.Random
import com.example.wordsnap.auth.PasswordHelper

class DatabaseManager(context: Context) {
    private val dbHelper: DatabaseHelper = DatabaseHelper(context)

    fun getTableData(tableName: String): List<Map<String, String>> {
        val db = dbHelper.readableDatabase
        val data = mutableListOf<Map<String, String>>()

        try {
            val cursor = db.rawQuery("SELECT * FROM $tableName", null)
            cursor.use {
                while (it.moveToNext()) {
                    val rowData = mutableMapOf<String, String>()
                    for (i in 0 until it.columnCount) {
                        val columnName = it.getColumnName(i)
                        val columnValue = it.getString(i) ?: "NULL"
                        rowData[columnName] = columnValue
                    }
                    data.add(rowData)
                }
            }
        } catch (e: Exception) {
            Log.e("DatabaseManager", "Error retrieving data from $tableName", e)
        } finally {
            db.close()
        }

        return data
    }

    fun populateTablesWithRandomData() {
        val db = dbHelper.writableDatabase
        val userIds = mutableListOf<Long>()
        val cardSetIds = mutableListOf<Long>()

        try {
            for (i in 1..25) {
                val name = "user_${Random.nextInt(1, 1000)}"
                val email = "$name@example.com"
                val passwordHash = "hash_${Random.nextInt(10000, 99999)}"
                val passwordSalt = "passwordsalt_${Random.nextInt(10000, 99999)}_${Random.nextInt(10000, 99999)}"
                val isVerified = Random.nextBoolean()
                val createdAt = LocalDateTime.now().minusDays(Random.nextLong(1, 10)).toString()

                val userValues = ContentValues().apply {
                    put("name", name)
                    put("email", email)
                    put("password_hash", passwordHash)
                    put("password_salt", passwordSalt)
                    put("is_verified", if (isVerified) 1 else 0)
                    put("created_at", createdAt)
                }

                val userId = db.insert("Users", null, userValues)
                if (userId != -1L) {
                    userIds.add(userId)
                }
            }

            userIds.forEach { userId ->
                repeat(2) {
                    val setName = "Set_${Random.nextInt(1, 100)}"
                    val isPublic = Random.nextBoolean()
                    val createdAt = LocalDateTime.now().minusDays(Random.nextLong(1, 100)).toString()

                    val cardSetValues = ContentValues().apply {
                        put("user_ref", userId)
                        put("name", setName)
                        put("is_public", if (isPublic) 1 else 0)
                        put("created_at", createdAt)
                    }

                    val cardSetId = db.insert("CardSets", null, cardSetValues)
                    if (cardSetId != -1L) {
                        cardSetIds.add(cardSetId)
                    }
                }
            }

            cardSetIds.forEach { cardSetId ->
                val wordEn = "Word_EN_${Random.nextInt(1, 100)}"
                val wordUa = "Word_UA_${Random.nextInt(1, 100)}"
                val note = if (Random.nextBoolean()) "Note_${Random.nextInt(1, 100)}" else null

                val cardValues = ContentValues().apply {
                    put("cardset_ref", cardSetId)
                    put("word_en", wordEn)
                    put("word_ua", wordUa)
                    if (note != null) put("note", note)
                }

                db.insert("Cards", null, cardValues)
            }

            userIds.forEachIndexed { index, userId ->
                val firstIndex = index * 2
                if (firstIndex + 1 < cardSetIds.size) {
                    for (j in 0 until 2) {
                        val cardSetId = cardSetIds[firstIndex + j]
                        val successRate = Random.nextDouble() * 100

                        val progressValues = ContentValues().apply {
                            put("user_ref", userId)
                            put("cardset_ref", cardSetId)
                            put("success_rate", successRate)
                        }

                        db.insert("Progress", null, progressValues)
                    }
                }
            }

            Log.d("DatabaseManager", "Random data inserted successfully")
        } catch (e: Exception) {
            Log.e("DatabaseManager", "Error inserting random data", e)
        } finally {
            db.close()
        }
    }

    fun printTableContents() {
        val db = dbHelper.readableDatabase
        val tables = listOf("Users", "CardSets", "Cards", "Progress")

        tables.forEach { tableName ->
            val cursor = db.rawQuery("SELECT * FROM $tableName", null)
            Log.d("DatabaseManager", "\nTable: $tableName")
            cursor.use {
                val columnNames = it.columnNames
                while (it.moveToNext()) {
                    val rowData = StringBuilder()
                    columnNames.forEach { columnName ->
                        val columnIndex = it.getColumnIndex(columnName)
                        rowData.append("$columnName: ${it.getString(columnIndex)}\t")
                    }
                    Log.d("DatabaseManager", rowData.toString())
                }
            }
        }
        db.close()
    }
    fun registerUser(name: String, email: String, password: String): Boolean {
        val db = dbHelper.writableDatabase
        // Check if user exists already
        val cursor = db.rawQuery("SELECT * FROM Users WHERE email = ?", arrayOf(email))
        val userExists = cursor.count > 0
        cursor.close()
        if (userExists) {
            db.close()
            return false
        }
        // Insert new user. Here we store the password in plain text for demo purposes.
        // In a real application, you must securely hash & salt the password.
        val values = ContentValues().apply {
            put("name", name)
            put("email", email)
            put("password_hash", password)
            put("password_salt", "dummySalt")  // Placeholder value
            put("is_verified", 1) // Mark as verified for demonstration
        }
        val id = db.insert("Users", null, values)
        db.close()
        return id != -1L
    }
    fun getUserByEmail(email: String): User? {
        val db = dbHelper.readableDatabase
        var user: User? = null
        val query = "SELECT * FROM Users WHERE email = ?"
        val cursor = db.rawQuery(query, arrayOf(email))
        if (cursor.moveToFirst()) {
            user = User(
                id = cursor.getLong(cursor.getColumnIndexOrThrow("id")),
                name = cursor.getString(cursor.getColumnIndexOrThrow("name")),
                email = cursor.getString(cursor.getColumnIndexOrThrow("email")),
                passwordHash = cursor.getString(cursor.getColumnIndexOrThrow("password_hash")),
                passwordSalt = cursor.getString(cursor.getColumnIndexOrThrow("password_salt")),
                isVerified = cursor.getInt(cursor.getColumnIndexOrThrow("is_verified")),
                createdAt = cursor.getString(cursor.getColumnIndexOrThrow("created_at"))
            )
        }
        cursor.close()
        db.close()
        return user
    }
    fun registerUser(name: String, email: String, passwordHash: String, salt: String): Boolean {
        val db = dbHelper.writableDatabase
        // Check if user already exists
        val cursor = db.rawQuery("SELECT * FROM Users WHERE email = ?", arrayOf(email))
        if (cursor.count > 0) {
            cursor.close()
            db.close()
            return false
        }
        cursor.close()

        // Prepare values and insert.
        val values = ContentValues().apply {
            put("name", name)
            put("email", email)
            put("password_hash", passwordHash)
            put("password_salt", salt)
            put("is_verified", 1) // For demo, mark as verified.
        }
        val id = db.insert("Users", null, values)
        db.close()
        return id != -1L
    }
    fun loginUser(email: String, password: String): Boolean {
        val user = getUserByEmail(email)
        return if (user != null) {
            PasswordHelper.verifyPassword(password, user.passwordHash, user.passwordSalt)
        } else {
            false
        }
    }
}
