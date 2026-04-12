package com.capstone.houseviewingapp.auth

/**
 * 임시 DI 포인트.
 * 현재는 Mock 사용, 추후 Retrofit 구현으로 교체.
 */
object AuthRepositoryProvider {
    val repository: AuthRepository by lazy { MockAuthRepository() }
}

