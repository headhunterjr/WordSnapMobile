package com.example.domain.repository

import com.example.data.entities.Cardset
import com.example.data.entities.Card
import com.example.data.entities.User


interface WordSnapRepository {
    fun getRandomPublicCardsets(limit: Int): List<Cardset>
    fun searchPublicCardsets(query: String): List<Cardset>
    fun getSavedCardsets(userId: Long): List<Cardset>
    fun getMyCardsets(userId: Long): List<Cardset>
    fun getCardsetById(cardsetId: Int): Cardset?
    fun getCardsForCardset(cardsetId: Int): List<Card>
    fun getUserByEmail(email: String): User?
    fun registerUser(name: String, email: String, passwordHash: String, salt: String): Boolean
    fun userExists(username: String, email: String): Boolean
    fun addUser(user: User): Int
    fun addCardset(cardset: Cardset): Int
    fun updateCardset(cardset: Cardset): Int
    fun switchCardsetPrivacy(cardsetId: Int): Boolean
    fun deleteCardset(cardsetId: Int): Boolean
    fun addCard(card: Card): Int
    fun updateCard(card: Card): Int
    fun deleteCard(cardId: Int): Boolean
    fun addTestProgress(userRef: Int, cardsetRef: Int, successRate: Double): Int
    fun getProgress(userRef: Int, cardsetRef: Int): Double?
    fun updateProgress(userRef: Int, cardsetRef: Int, successRate: Double): Int
    fun getCard(cardId: Int): Card?
    fun isCardsetOwnedByUser(userId: Int, cardsetId: Int): Boolean
    fun updateCardsetName(cardsetId: Int, newName: String): Int
}
