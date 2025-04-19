package com.example.domain.repository

import android.content.Context
import com.example.data.database.DatabaseManager
import com.example.data.entities.Cardset
import com.example.data.entities.Card
import com.example.data.entities.User

class WordSnapRepositoryImplementation(context: Context) : WordSnapRepository {
    private val db = DatabaseManager(context)

    override fun getRandomPublicCardsets(limit: Int): List<Cardset> =
        db.getRandomPublicCardsets(limit)

    override fun searchPublicCardsets(query: String): List<Cardset> =
        db.searchPublicCardsets(query)

    override fun getSavedCardsets(userId: Long): List<Cardset> =
        db.getUsersCardsetsLibrary(userId)

    override fun getMyCardsets(userId: Long): List<Cardset> =
        db.getUsersOwnCardsets(userId)

    override fun getCardsetById(cardsetId: Int): Cardset? =
        db.getCardsetById(cardsetId)

    override fun getCardsForCardset(cardsetId: Int): List<Card> =
        db.getCardsForCardset(cardsetId)

    override fun getUserByEmail(email: String): User? =
        db.getUserByEmail(email)

    override fun registerUser(name: String, email: String, passwordHash: String, salt: String): Boolean =
        db.registerUser(name, email, passwordHash, salt)
}