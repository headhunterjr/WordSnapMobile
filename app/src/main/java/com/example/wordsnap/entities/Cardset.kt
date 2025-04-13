package com.example.wordsnap.entities

data class Cardset(
    val id: Int,
    val userRef: Int,
    val name: String,
    val isPublic: Boolean,
    val createdAt: String
)
