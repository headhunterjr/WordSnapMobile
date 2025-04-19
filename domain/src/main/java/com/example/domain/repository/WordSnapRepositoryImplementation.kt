package com.example.domain.repository

import android.content.Context
import com.example.data.database.DatabaseManager
import com.example.data.entities.Cardset

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
}