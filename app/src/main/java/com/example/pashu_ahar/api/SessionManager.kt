package com.example.pashu_ahar.api

import android.content.Context
import android.content.SharedPreferences

class SessionManager(context: Context) {
    private var prefs: SharedPreferences = context.getSharedPreferences("pashu_ahar_prefs", Context.MODE_PRIVATE)

    companion object {
        const val USER_TOKEN = "user_token"
        const val USER_NAME = "user_name"
        const val USER_EMAIL = "user_email"
        const val PROFILE_IMAGE = "profile_image"
        const val PHONE_NUMBER = "phone_number"
        const val FARM_NAME = "farm_name"
        const val ADDRESS = "address"
    }

    fun saveAuthToken(token: String) {
        val editor = prefs.edit()
        editor.putString(USER_TOKEN, "Bearer $token")
        editor.apply()
    }

    fun fetchAuthToken(): String? {
        return prefs.getString(USER_TOKEN, null)
    }

    fun saveUserDetail(user: User) {
        val editor = prefs.edit()
        editor.putString(USER_NAME, user.fullName)
        editor.putString(USER_EMAIL, user.email)
        editor.putString(PROFILE_IMAGE, user.profileImage)
        editor.putString(PHONE_NUMBER, user.phoneNumber)
        editor.putString(FARM_NAME, user.farmName)
        editor.putString(ADDRESS, user.address)
        editor.apply()
    }

    fun getUserName(): String? = prefs.getString(USER_NAME, "User")
    fun getUserEmail(): String? = prefs.getString(USER_EMAIL, "")
    fun getProfileImage(): String? = prefs.getString(PROFILE_IMAGE, null)
    fun getPhoneNumber(): String? = prefs.getString(PHONE_NUMBER, "")
    fun getFarmName(): String? = prefs.getString(FARM_NAME, "")
    fun getAddress(): String? = prefs.getString(ADDRESS, "")

    fun logout() {
        val editor = prefs.edit()
        editor.clear()
        editor.apply()
    }
}
