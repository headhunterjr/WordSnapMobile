package com.example.data.database

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Context
import android.util.Log
import java.time.LocalDateTime
import kotlin.random.Random
import com.example.data.entities.Cardset
import com.example.data.entities.User
import com.example.data.entities.Card

class DatabaseManager(context: Context) {
    private val dbHelper: DatabaseHelper =
        DatabaseHelper(context)

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

    fun getRandomPublicCardsets(limit: Int): List<Cardset> {
        val db = dbHelper.readableDatabase
        val list = mutableListOf<Cardset>()

        val sql = """
        SELECT * FROM CardSets
        WHERE is_public = 1
        ORDER BY RANDOM()
        LIMIT ?
    """.trimIndent()

        val cursor = db.rawQuery(sql, arrayOf(limit.toString()))
        if (cursor.moveToFirst()) {
            do {
                val id = cursor.getInt(cursor.getColumnIndexOrThrow("id"))
                val userRef = cursor.getInt(cursor.getColumnIndexOrThrow("user_ref"))
                val name = cursor.getString(cursor.getColumnIndexOrThrow("name"))
                val isPublic = cursor.getInt(cursor.getColumnIndexOrThrow("is_public")) == 1
                val createdAt = cursor.getString(cursor.getColumnIndexOrThrow("created_at"))

                val cardset = Cardset(
                    id = id,
                    userRef = userRef,
                    name = name,
                    isPublic = isPublic,
                    createdAt = createdAt
                )
                list.add(cardset)
            } while (cursor.moveToNext())
        }
        cursor.close()
        db.close()
        return list
    }

    fun searchPublicCardsets(searchQuery: String): List<Cardset> {
        val db = dbHelper.readableDatabase
        val list = mutableListOf<Cardset>()

        // Using LIKE for partial match (case-insensitive).
        // Or you could do a lower(...) comparison.
        val sql = """
        SELECT * FROM CardSets
        WHERE is_public = 1
          AND name LIKE '%' || ? || '%'
        ORDER BY id DESC
    """.trimIndent()

        val cursor = db.rawQuery(sql, arrayOf(searchQuery))
        if (cursor.moveToFirst()) {
            do {
                val id = cursor.getInt(cursor.getColumnIndexOrThrow("id"))
                val userRef = cursor.getInt(cursor.getColumnIndexOrThrow("user_ref"))
                val name = cursor.getString(cursor.getColumnIndexOrThrow("name"))
                val isPublic = cursor.getInt(cursor.getColumnIndexOrThrow("is_public")) == 1
                val createdAt = cursor.getString(cursor.getColumnIndexOrThrow("created_at"))

                val cardset = Cardset(
                    id = id,
                    userRef = userRef,
                    name = name,
                    isPublic = isPublic,
                    createdAt = createdAt
                )
                list.add(cardset)
            } while (cursor.moveToNext())
        }
        cursor.close()
        db.close()
        return list
    }

    fun getUsersOwnCardsets(userId: Long): List<Cardset> {
        val db = dbHelper.readableDatabase
        val list = mutableListOf<Cardset>()
        val sql = "SELECT * FROM CardSets WHERE user_ref = ?"
        Log.d("DBQuery", "getUsersOwnCardsets SQL: $sql | args: [$userId]")
        val cursor = db.rawQuery(sql, arrayOf(userId.toString()))
        Log.d("DBQuery", "getUsersOwnCardsets Cursor.count = ${cursor.count}")
        if (cursor.moveToFirst()) {
            do {
                val id       = cursor.getInt(cursor.getColumnIndexOrThrow("id"))
                val name     = cursor.getString(cursor.getColumnIndexOrThrow("name"))
                val uref     = cursor.getInt(cursor.getColumnIndexOrThrow("user_ref"))
                Log.d("DBQuery", "  own → id=$id, name=$name, user_ref=$uref")
                list += Cardset(
                    id        = id,
                    userRef   = uref,
                    name      = name,
                    isPublic  = cursor.getInt(cursor.getColumnIndexOrThrow("is_public")) == 1,
                    createdAt = cursor.getString(cursor.getColumnIndexOrThrow("created_at"))
                )
            } while (cursor.moveToNext())
        }
        cursor.close()
        db.close()
        return list
    }

    fun getUsersCardsetsLibrary(userId: Long): List<Cardset> {
        val db = dbHelper.readableDatabase
        val list = mutableListOf<Cardset>()
        val sql = """
        SELECT cs.*
          FROM CardSets cs
          JOIN Progress p ON cs.id = p.cardset_ref
         WHERE p.user_ref = ?
    """.trimIndent()
        Log.d("DBQuery", "getUsersCardsetsLibrary SQL: $sql | args: [$userId]")
        val cursor = db.rawQuery(sql, arrayOf(userId.toString()))
        Log.d("DBQuery", "getUsersCardsetsLibrary Cursor.count = ${cursor.count}")
        if (cursor.moveToFirst()) {
            do {
                val id       = cursor.getInt(cursor.getColumnIndexOrThrow("id"))
                val name     = cursor.getString(cursor.getColumnIndexOrThrow("name"))
                val uref     = cursor.getInt(cursor.getColumnIndexOrThrow("user_ref"))
                Log.d("DBQuery", "  saved → id=$id, name=$name, user_ref=$uref")
                list += Cardset(
                    id        = id,
                    userRef   = uref,
                    name      = name,
                    isPublic  = cursor.getInt(cursor.getColumnIndexOrThrow("is_public")) == 1,
                    createdAt = cursor.getString(cursor.getColumnIndexOrThrow("created_at"))
                )
            } while (cursor.moveToNext())
        }
        cursor.close()
        db.close()
        return list
    }

    fun getCardsetById(cardsetId: Int): Cardset? {
        val db = dbHelper.readableDatabase
        var cardset: Cardset? = null

        val query = "SELECT * FROM CardSets WHERE id = ?"
        val cursor = db.rawQuery(query, arrayOf(cardsetId.toString()))

        if (cursor.moveToFirst()) {
            cardset = Cardset(
                id = cursor.getInt(cursor.getColumnIndexOrThrow("id")),
                userRef = cursor.getInt(cursor.getColumnIndexOrThrow("user_ref")),
                name = cursor.getString(cursor.getColumnIndexOrThrow("name")),
                isPublic = cursor.getInt(cursor.getColumnIndexOrThrow("is_public")) == 1,
                createdAt = cursor.getString(cursor.getColumnIndexOrThrow("created_at"))
            )
        }

        cursor.close()
        db.close()
        return cardset
    }

    @SuppressLint("Range")
    fun getCardsForCardset(cardsetId: Int): List<Card> {
        val cards = mutableListOf<Card>()
        val db = dbHelper.readableDatabase

        val cursor = db.rawQuery(
            "SELECT id, cardset_ref, word_en, word_ua, note FROM Cards WHERE cardset_ref = ?",
            arrayOf(cardsetId.toString())
        )

        cursor.use { c ->
            while (c.moveToNext()) {
                cards += Card(
                    id         = c.getInt(c.getColumnIndexOrThrow("id")),
                    cardsetRef = c.getInt(c.getColumnIndexOrThrow("cardset_ref")),
                    wordEn     = c.getString(c.getColumnIndexOrThrow("word_en")),
                    wordUa     = c.getString(c.getColumnIndexOrThrow("word_ua")),
                    note       = c.getString(c.getColumnIndexOrThrow("note")) ?: ""                )
            }
        }

        db.close()
        return cards
    }

    fun userExists(username: String, email: String): Boolean {
        val db = dbHelper.readableDatabase
        val c = db.rawQuery(
            "SELECT 1 FROM Users WHERE name = ? OR email = ? LIMIT 1",
            arrayOf(username, email)
        )
        val exists = c.count > 0
        c.close(); db.close()
        return exists
    }

    fun addUser(user: User): Int {
        val db = dbHelper.writableDatabase
        val v = ContentValues().apply {
            put("name", user.name)
            put("email", user.email)
            put("password_hash", user.passwordHash)
            put("password_salt", user.passwordSalt)
            put("is_verified", user.isVerified)
            put("created_at", user.createdAt)
        }
        val id = db.insert("Users", null, v).toInt()
        db.close()
        return id
    }

    fun addCardset(cs: Cardset): Int {
        val db = dbHelper.writableDatabase
        val v = ContentValues().apply {
            put("user_ref", cs.userRef)
            put("name", cs.name)
            put("is_public", if (cs.isPublic) 1 else 0)
            put("created_at", cs.createdAt)
        }
        val id = db.insert("CardSets", null, v).toInt()
        db.close()
        return id
    }

    fun updateCardset(cs: Cardset): Int {
        val db = dbHelper.writableDatabase
        val v = ContentValues().apply {
            put("name", cs.name)
            put("is_public", if (cs.isPublic) 1 else 0)
        }
        val count = db.update("CardSets", v, "id = ?", arrayOf(cs.id.toString()))
        db.close()
        return count
    }

    fun switchCardsetPrivacy(cardsetId: Int): Boolean {
        val db = dbHelper.writableDatabase
        db.execSQL(
            "UPDATE CardSets SET is_public = 1 - is_public WHERE id = ?",
            arrayOf(cardsetId)
        )
        db.close()
        return true
    }

    fun deleteCardset(cardsetId: Int): Boolean {
        val db = dbHelper.writableDatabase
        val count = db.delete("CardSets", "id = ?", arrayOf(cardsetId.toString()))
        db.close()
        return count > 0
    }

    fun addCard(card: Card): Int {
        val db = dbHelper.writableDatabase
        val v = ContentValues().apply {
            put("cardset_ref", card.cardsetRef)
            put("word_en", card.wordEn)
            put("word_ua", card.wordUa)
            put("note", card.note)
        }
        val id = db.insert("Cards", null, v).toInt()
        db.close()
        return id
    }

    fun updateCardsetName(cardsetId: Int, newName: String): Int {
        val db = dbHelper.writableDatabase
        val cv = ContentValues().apply { put("name", newName) }
        val count = db.update("CardSets", cv, "id = ?", arrayOf(cardsetId.toString()))
        db.close()
        return count
    }

    fun updateCard(card: Card): Int {
        val db = dbHelper.writableDatabase
        val v = ContentValues().apply {
            put("word_en", card.wordEn)
            put("word_ua", card.wordUa)
            put("note", card.note)
        }
        val count = db.update("Cards", v, "id = ?", arrayOf(card.id.toString()))
        db.close()
        return count
    }

    fun deleteCard(cardId: Int): Boolean {
        val db = dbHelper.writableDatabase
        val count = db.delete("Cards", "id = ?", arrayOf(cardId.toString()))
        db.close()
        return count > 0
    }

    fun addTestProgress(userRef: Int, cardsetRef: Int, successRate: Double): Int {
        val db = dbHelper.writableDatabase
        val v = ContentValues().apply {
            put("user_ref", userRef)
            put("cardset_ref", cardsetRef)
            put("success_rate", successRate)
        }
        val id = db.insert("Progress", null, v).toInt()
        db.close()
        return id
    }

    fun getProgress(userRef: Int, cardsetRef: Int): Double? {
        val db = dbHelper.readableDatabase
        val c = db.rawQuery(
            "SELECT success_rate FROM Progress WHERE user_ref = ? AND cardset_ref = ?",
            arrayOf(userRef.toString(), cardsetRef.toString())
        )
        val rate = if (c.moveToFirst()) c.getDouble(0) else null
        c.close(); db.close()
        return rate
    }

    fun updateProgress(userRef: Int, cardsetRef: Int, successRate: Double): Int {
        val db = dbHelper.writableDatabase
        val v = ContentValues().apply {
            put("success_rate", successRate)
        }
        val count = db.update(
            "Progress", v,
            "user_ref = ? AND cardset_ref = ?",
            arrayOf(userRef.toString(), cardsetRef.toString())
        )
        db.close()
        return count
    }

    fun getCard(cardId: Int): Card? {
        val db = dbHelper.readableDatabase
        val c = db.rawQuery(
            "SELECT id, cardset_ref, word_en, word_ua, note FROM Cards WHERE id = ?",
            arrayOf(cardId.toString())
        )
        val card = if (c.moveToFirst()) Card(
            id         = c.getInt(0),
            cardsetRef = c.getInt(1),
            wordEn     = c.getString(2),
            wordUa     = c.getString(3),
            note       = c.getString(4) ?: ""
        ) else null
        c.close(); db.close()
        return card
    }

    fun isCardsetOwnedByUser(userId: Int, cardsetId: Int): Boolean {
        val db = dbHelper.readableDatabase
        val c = db.rawQuery(
            "SELECT 1 FROM CardSets WHERE id = ? AND user_ref = ? LIMIT 1",
            arrayOf(cardsetId.toString(), userId.toString())
        )
        val owned = c.count > 0
        c.close(); db.close()
        return owned
    }

    fun saveCardsetToLibrary(userId: Long, cardsetId: Int): Int {
        val db = dbHelper.writableDatabase
        val v = ContentValues().apply {
            put("user_ref", userId)
            put("cardset_ref", cardsetId)
            put("success_rate", 0.0)
        }
        val id = db.insert("Progress", null, v).toInt()
        db.close()
        return id
    }

    fun removeCardsetFromLibrary(userId: Long, cardsetId: Int): Int {
        val db = dbHelper.writableDatabase
        val count = db.delete(
            "Progress",
            "user_ref = ? AND cardset_ref = ?",
            arrayOf(userId.toString(), cardsetId.toString())
        )
        db.close()
        return count
    }

    fun isCardsetInLibrary(userId: Long, cardsetId: Int): Boolean {
        val db = dbHelper.readableDatabase
        val cursor = db.rawQuery(
            "SELECT 1 FROM Progress WHERE user_ref = ? AND cardset_ref = ?",
            arrayOf(userId.toString(), cardsetId.toString())
        )
        val exists = cursor.count > 0
        cursor.close()
        db.close()
        return exists
    }

}
