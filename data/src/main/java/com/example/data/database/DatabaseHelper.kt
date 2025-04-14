package com.example.data.database

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DatabaseHelper(context: Context) : SQLiteOpenHelper(context,
    DATABASE_NAME, null,
    DATABASE_VERSION
) {

    companion object {
        private const val DATABASE_NAME = "wordsnapdb"
        private const val DATABASE_VERSION = 1
    }

    override fun onCreate(db: SQLiteDatabase) {
        val createUserTableSql = """
        CREATE TABLE Users (
            id INTEGER PRIMARY KEY AUTOINCREMENT,
            name TEXT NOT NULL,
            email TEXT NOT NULL,
            password_hash TEXT NOT NULL,
            password_salt TEXT NOT NULL,
            is_verified INTEGER DEFAULT 0,
            created_at TEXT DEFAULT (datetime('now'))
        )
        """.trimIndent()

        val createCardSetTableSql = """
        CREATE TABLE CardSets (
            id INTEGER PRIMARY KEY AUTOINCREMENT,
            user_ref INTEGER NOT NULL,
            name TEXT NOT NULL,
            is_public INTEGER DEFAULT 0,
            created_at TEXT DEFAULT (datetime('now')),
            FOREIGN KEY (user_ref) REFERENCES Users(id) ON DELETE CASCADE
        )
        """.trimIndent()

        val createCardsTableSql = """
        CREATE TABLE Cards (
            id INTEGER PRIMARY KEY AUTOINCREMENT,
            cardset_ref INTEGER NOT NULL,
            word_en TEXT NOT NULL,
            word_ua TEXT NOT NULL,
            note TEXT,
            FOREIGN KEY (cardset_ref) REFERENCES CardSets(id) ON DELETE CASCADE
        )
        """.trimIndent()

        val createProgressTableSql = """
        CREATE TABLE Progress (
            id INTEGER PRIMARY KEY AUTOINCREMENT,
            user_ref INTEGER NOT NULL,
            cardset_ref INTEGER NOT NULL,
            success_rate REAL DEFAULT 0.0,
            FOREIGN KEY (user_ref) REFERENCES Users(id) ON DELETE CASCADE,
            FOREIGN KEY (cardset_ref) REFERENCES CardSets(id) ON DELETE CASCADE
        )
        """.trimIndent()


        db.execSQL(createUserTableSql)
        db.execSQL(createCardSetTableSql)
        db.execSQL(createCardsTableSql)
        db.execSQL(createProgressTableSql)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS Users")
        db.execSQL("DROP TABLE IF EXISTS CardSets")
        db.execSQL("DROP TABLE IF EXISTS Cards")
        db.execSQL("DROP TABLE IF EXISTS Progress")
        onCreate(db)
    }
}