package com.example.recipe_manager.util

import android.content.SharedPreferences
import androidx.core.content.edit
import org.json.JSONObject

object PreferencesHelper {

    private const val USER_CREDENTIALS_KEY = "user_credentials"

    // Save or update credentials using KTX edit lambda for concise syntax
    fun saveCredentials(sharedPref: SharedPreferences, email: String, password: String) {
        sharedPref.edit {
            val jsonString = sharedPref.getString(USER_CREDENTIALS_KEY, "{}")
            val json = JSONObject(jsonString ?: "{}")
            json.put(email, password)
            putString(USER_CREDENTIALS_KEY, json.toString())
        }
    }

    // Remove credentials for specific email
    fun removeCredentials(sharedPref: SharedPreferences, email: String) {
        sharedPref.edit {
            val jsonString = sharedPref.getString(USER_CREDENTIALS_KEY, "{}")
            val json = JSONObject(jsonString ?: "{}")
            json.remove(email)
            putString(USER_CREDENTIALS_KEY, json.toString())
        }
    }

    // Retrieve password for given email
    fun getPassword(sharedPref: SharedPreferences, email: String): String? {
        val jsonString = sharedPref.getString(USER_CREDENTIALS_KEY, "{}")
        val json = JSONObject(jsonString ?: "{}")
        return if (json.has(email)) json.getString(email) else null
    }
}
