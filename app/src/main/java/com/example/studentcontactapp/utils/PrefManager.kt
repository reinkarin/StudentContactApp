package com.example.studentcontactapp.utils

import android.content.Context
import android.content.SharedPreferences

class PrefManager(context: Context) {
    private val pref: SharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
    private val editor: SharedPreferences.Editor = pref.edit()

    companion object {
        private const val PREF_NAME = "StudentPref"
        private const val IS_LOGGED_IN = "isLoggedIn"
        private const val USERNAME = "username"
        private const val IS_REMEMBER_ME = "isRememberMe"
    }

    fun setLogin(isLoggedIn: Boolean) {
        editor.putBoolean(IS_LOGGED_IN, isLoggedIn)
        editor.apply()
    }

    fun isLoggedIn(): Boolean = pref.getBoolean(IS_LOGGED_IN, false)

    fun setUsername(username: String) {
        editor.putString(USERNAME, username)
        editor.apply()
    }

    fun getUsername(): String? = pref.getString(USERNAME, "")

    fun setRememberMe(isRememberMe: Boolean) {
        editor.putBoolean(IS_REMEMBER_ME, isRememberMe)
        editor.apply()
    }

    fun isRememberMe(): Boolean = pref.getBoolean(IS_REMEMBER_ME, false)

    fun logout() {
        editor.clear()
        editor.apply()
    }
}
