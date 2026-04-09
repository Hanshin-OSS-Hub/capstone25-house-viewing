package com.capstone.houseviewingapp.auth.model

/**
 * NOTE:
 * - 백엔드 스펙의 JSON 키와 1:1로 맞춘 DTO
 * - (name, email, loginId, password, userId ...) 키를 그대로 사용
 */

data class RegisterRequest(
    val name: String,
    val email: String,
    val loginId: String,
    val password: String
)

data class RegisterResponse(
    val userId: Long
)

data class LoginRequest(
    val loginId: String,
    val password: String
)

data class LoginResponse(
    val accessToken: String,
    val refreshToken: String
)

data class FindIdRequest(
    val name: String,
    val email: String
)

data class FindIdResponse(
    val loginId: String
)

data class VerifyPasswordRequest(
    val loginId: String,
    val password: String
)

data class ResetPasswordRequest(
    val resetToken: String,
    val password: String
)

data class ReissueRequest(
    val refreshToken: String
)

data class ReissueResponse(
    val accessToken: String,
    val refreshToken: String
)

data class MeResponse(
    val name: String,
    val email: String,
    val loginId: String
)

/**
 * 백엔드 에러 표준 응답이 정해지면 그 스펙으로 교체
 */
data class ApiErrorResponse(
    val code: String? = null,
    val message: String? = null
)

