package com.example.domain.repository

import com.example.data.entities.Cardset

interface WordSnapRepository {
    fun getRandomPublicCardsets(limit: Int): List<Cardset>
    fun searchPublicCardsets(query: String): List<Cardset>
    fun getSavedCardsets(userId: Long): List<Cardset>
    fun getMyCardsets(userId: Long): List<Cardset>
}
