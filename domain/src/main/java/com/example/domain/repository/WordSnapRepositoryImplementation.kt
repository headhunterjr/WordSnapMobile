package com.example.domain.repository

import android.content.Context
import com.example.data.database.DatabaseManager
import com.example.data.entities.Cardset
import com.example.data.entities.Card
import com.example.data.entities.User

class WordSnapRepositoryImplementation(context: Context) : WordSnapRepository {
    private val db = DatabaseManager(context)

    override fun getRandomPublicCardsets(limit: Int): List<Cardset> = db.getRandomPublicCardsets(limit)

    override fun searchPublicCardsets(query: String): List<Cardset> = db.searchPublicCardsets(query)

    override fun getSavedCardsets(userId: Long): List<Cardset> = db.getUsersCardsetsLibrary(userId)

    override fun getMyCardsets(userId: Long): List<Cardset> = db.getUsersOwnCardsets(userId)

    override fun getCardsetById(cardsetId: Int): Cardset? = db.getCardsetById(cardsetId)

    override fun getCardsForCardset(cardsetId: Int): List<Card> = db.getCardsForCardset(cardsetId)

    override fun getUserByEmail(email: String): User? = db.getUserByEmail(email)

    override fun registerUser(name: String, email: String, passwordHash: String, salt: String): Boolean = db.registerUser(name, email, passwordHash, salt)

    override fun userExists(username: String, email: String) = db.userExists(username, email)

    override fun addUser(user: User) = db.addUser(user)

    override fun addCardset(cardset: Cardset) = db.addCardset(cardset)

    override fun updateCardset(cardset: Cardset) = db.updateCardset(cardset)

    override fun switchCardsetPrivacy(cardsetId: Int) = db.switchCardsetPrivacy(cardsetId)

    override fun deleteCardset(cardsetId: Int) = db.deleteCardset(cardsetId)

    override fun addCard(card: Card) = db.addCard(card)

    override fun updateCard(card: Card) = db.updateCard(card)

    override fun deleteCard(cardId: Int) = db.deleteCard(cardId)

    override fun addTestProgress(userRef: Int, cardsetRef: Int, successRate: Double) = db.addTestProgress(userRef, cardsetRef, successRate)

    override fun getProgress(userRef: Int, cardsetRef: Int) = db.getProgress(userRef, cardsetRef)

    override fun updateProgress(userRef: Int, cardsetRef: Int, successRate: Double) = db.updateProgress(userRef, cardsetRef, successRate)

    override fun getCard(cardId: Int) = db.getCard(cardId)

    override fun isCardsetOwnedByUser(userId: Int, cardsetId: Int) = db.isCardsetOwnedByUser(userId, cardsetId)

    override fun updateCardsetName(cardsetId: Int, newName: String) =
        db.updateCardsetName(cardsetId, newName)
}