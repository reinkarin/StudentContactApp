package com.example.studentcontactapp.utils

import android.content.Context
import android.content.SharedPreferences

class SettingsManager(context: Context) {
    private val pref: SharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
    private val editor: SharedPreferences.Editor = pref.edit()

    companion object {
        private const val PREF_NAME = "SettingsPref"
        private const val DARK_MODE = "darkMode"
        private const val FONT_SIZE = "fontSize"
        private const val NOTIFICATION = "notification"
    }

    fun setDarkMode(enabled: Boolean) {
        editor.putBoolean(DARK_MODE, enabled)
        editor.apply()
    }

    fun isDarkMode(): Boolean = pref.getBoolean(DARK_MODE, false)

    fun setFontSizeLarge(enabled: Boolean) {
        editor.putBoolean(FONT_SIZE, enabled)
        editor.apply()
    }

    fun isFontSizeLarge(): Boolean = pref.getBoolean(FONT_SIZE, false)

    fun setNotificationEnabled(enabled: Boolean) {
        editor.putBoolean(NOTIFICATION, enabled)
        editor.apply()
    }

    fun isNotificationEnabled(): Boolean = pref.getBoolean(NOTIFICATION, true)
}
