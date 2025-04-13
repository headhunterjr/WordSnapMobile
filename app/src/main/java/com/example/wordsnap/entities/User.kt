package com.example.wordsnap.entities

data class User(
    val id: Long,
    val name: String,
    val email: String,
    val passwordHash: String,
    val passwordSalt: String,
    val isVerified: Int, // 0 or 1
    val createdAt: String
)