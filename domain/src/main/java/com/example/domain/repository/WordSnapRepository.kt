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

}
