package ru.qqdasdzxc.supremebot.utils

import android.content.Context

object ActivationPreferences {

    private const val ACTIVATION_PREFERENCES = "ACTIVATION_PREFERENCES"
    private const val ACTIVATION_KEY = "ACTIVATION_KEY"

    fun get(context: Context): String? {
        val preferences = context.getSharedPreferences(ACTIVATION_PREFERENCES, Context.MODE_PRIVATE)
        return preferences.getString(ACTIVATION_KEY, null)
    }

    fun save(context: Context, key: String) {
        val preferences = context.getSharedPreferences(ACTIVATION_PREFERENCES, Context.MODE_PRIVATE)
        preferences.edit().putString(ACTIVATION_KEY, key).apply()
    }
}