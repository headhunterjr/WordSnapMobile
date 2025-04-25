package com.example.domain.auth

import android.content.Context
import com.example.data.entities.User

object UserSession {
    var userId: Long? = null
        private set

    private const val PREFS = "WordSnapPrefs"
    private const val KEY_USER_ID = "key_user_id"

    fun init(context: Context) {
        val prefs = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
        val saved = prefs.getLong(KEY_USER_ID, -1L)
        if (saved != -1L) userId = saved
    }

    fun login(user: User, context: Context) {
        userId = user.id
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
            .edit()
            .putLong(KEY_USER_ID, user.id)
            .apply()
    }

    fun logout(context: Context) {
        userId = null
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
            .edit()
            .remove(KEY_USER_ID)
            .apply()
    }

    val isLoggedIn get() = userId != null
}
