package com.capstone.houseviewingapp.auth

import com.capstone.houseviewingapp.BuildConfig

/**
 * [BuildConfig.USE_MOCK_API] 가 true 이면 Mock, false 이면 Retrofit + Bearer JWT
 */
object AuthRepositoryProvider {
    val repository: AuthRepository by lazy {
        if (BuildConfig.USE_MOCK_API) {
            MockAuthRepository()
        } else {
            RemoteAuthRepository()
        }
    }
}
