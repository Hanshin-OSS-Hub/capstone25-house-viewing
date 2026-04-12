package com.capstone.houseviewingapp.data.local

import android.content.Context

object BillingLocalStore {
    private const val PREF_NAME = "billing_local_pref"
    private const val KEY_IS_PREMIUM = "is_premium"

    private fun prefs(context: Context) =
        context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)

    fun isPremium(context: Context): Boolean =
        prefs(context).getBoolean(KEY_IS_PREMIUM, false)

    fun setPremium(context: Context, isPremium: Boolean) {
        prefs(context).edit().putBoolean(KEY_IS_PREMIUM, isPremium).apply()
    }
}

