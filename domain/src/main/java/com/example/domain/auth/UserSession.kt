package com.example.domain.auth

import com.example.data.entities.User

object UserSession {
    var currentUser: User? = null
        private set

    val isLoggedIn: Boolean
        get() = currentUser != null

    val userId: Long?
        get() = currentUser?.id

    fun login(user: User) {
        currentUser = user
    }

    fun logout() {
        currentUser = null
    }
}