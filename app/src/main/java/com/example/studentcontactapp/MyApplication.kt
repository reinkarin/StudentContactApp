package com.example.studentcontactapp

import android.app.Application
import androidx.appcompat.app.AppCompatDelegate
import com.example.studentcontactapp.utils.SettingsManager

class MyApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        val settingsManager = SettingsManager(this)
        if (settingsManager.isDarkMode()) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        }
    }
}
