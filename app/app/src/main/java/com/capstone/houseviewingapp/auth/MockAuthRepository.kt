package com.capstone.houseviewingapp.auth

import com.capstone.houseviewingapp.auth.model.FindIdRequest
import com.capstone.houseviewingapp.auth.model.FindIdResponse
import com.capstone.houseviewingapp.auth.model.LoginRequest
import com.capstone.houseviewingapp.auth.model.LoginResponse
import com.capstone.houseviewingapp.auth.model.MeResponse
import com.capstone.houseviewingapp.auth.model.ReissueRequest
import com.capstone.houseviewingapp.auth.model.ReissueResponse
import com.capstone.houseviewingapp.auth.model.RegisterRequest
import com.capstone.houseviewingapp.auth.model.ResetPasswordRequest
import com.capstone.houseviewingapp.auth.model.SubscriptionMeResponse
import com.capstone.houseviewingapp.auth.model.VerifyPasswordRequest
import java.util.concurrent.atomic.AtomicLong
import kotlinx.coroutines.delay

/**
 * 백엔드 붙이기 전 화면 동작/검증용 Mock 구현
 */
class MockAuthRepository : AuthRepository {
    private data class User(
        val userId: Long,
        var name: String,
        var email: String,
        var loginId: String,
        var password: String
    )

    private val idGenerator = AtomicLong(1L)
    private val users = mutableListOf<User>()
    private val accessTokenToUserId = mutableMapOf<String, Long>()
    private val refreshTokenToUserId = mutableMapOf<String, Long>()
    private val resetTokenToUserId = mutableMapOf<String, Long>()

    override suspend fun register(request: RegisterRequest): Result<Unit> {
        delay(1)
        if (users.any { it.loginId.equals(request.loginId, ignoreCase = true) }) {
            return Result.failure(IllegalStateException("DUPLICATE_LOGIN_ID"))
        }
        if (users.any { it.email.equals(request.email, ignoreCase = true) }) {
            return Result.failure(IllegalStateException("DUPLICATE_EMAIL"))
        }

        val newUser = User(
            userId = idGenerator.getAndIncrement(),
            name = request.name,
            email = request.email,
            loginId = request.loginId,
            password = request.password
        )
        users.add(newUser)
        return Result.success(Unit)
    }

    override suspend fun isLoginIdAvailable(loginId: String): Result<Boolean> {
        delay(1)
        val normalized = loginId.trim()
        if (normalized.isBlank()) return Result.failure(IllegalArgumentException("INVALID_LOGIN_ID"))
        return Result.success(users.none { it.loginId.equals(normalized, ignoreCase = true) })
    }

    override suspend fun isEmailAvailable(email: String): Result<Boolean> {
        delay(1)
        val normalized = email.trim()
        if (normalized.isBlank()) return Result.failure(IllegalArgumentException("INVALID_EMAIL"))
        return Result.success(users.none { it.email.equals(normalized, ignoreCase = true) })
    }

    override suspend fun login(request: LoginRequest): Result<LoginResponse> {
        delay(1)
        val user = users.firstOrNull { it.loginId == request.loginId && it.password == request.password }
            ?: return Result.failure(IllegalArgumentException("LOGIN_FAILED"))

        val access = "mock_access_${user.userId}_${System.currentTimeMillis()}"
        val refresh = "mock_refresh_${user.userId}_${System.currentTimeMillis()}"
        accessTokenToUserId[access] = user.userId
        refreshTokenToUserId[refresh] = user.userId
        return Result.success(
            LoginResponse(
                accessToken = access,
                refreshToken = refresh,
                userId = user.userId,
                loginId = user.loginId,
                name = user.name
            )
        )
    }

    override suspend fun logout(accessToken: String): Result<Unit> {
        delay(1)
        val userId = accessTokenToUserId.remove(accessToken)
            ?: return Result.failure(IllegalArgumentException("INVALID_HEADER"))
        refreshTokenToUserId.entries.removeAll { it.value == userId }
        return Result.success(Unit)
    }

    override suspend fun reissue(request: ReissueRequest): Result<ReissueResponse> {
        delay(1)
        val userId = refreshTokenToUserId[request.refreshToken]
            ?: return Result.failure(IllegalArgumentException("INVALID_TOKEN"))

        accessTokenToUserId.entries.removeAll { it.value == userId }
        refreshTokenToUserId.remove(request.refreshToken)

        val newAccess = "mock_access_${userId}_${System.currentTimeMillis()}"
        val newRefresh = "mock_refresh_${userId}_${System.currentTimeMillis()}"
        accessTokenToUserId[newAccess] = userId
        refreshTokenToUserId[newRefresh] = userId

        return Result.success(ReissueResponse(accessToken = newAccess))
    }

    override suspend fun findId(request: FindIdRequest): Result<FindIdResponse> {
        delay(1)
        val matched = users.firstOrNull {
            it.name == request.name && it.email.equals(request.email, ignoreCase = true)
        } ?: return Result.failure(IllegalArgumentException("FIND_LOGIN_ID_FAILED"))

        return Result.success(FindIdResponse(loginId = matched.loginId))
    }

    override suspend fun verifyPassword(request: VerifyPasswordRequest): Result<String> {
        delay(1)
        val user = users.firstOrNull {
            it.loginId.equals(request.loginId, ignoreCase = true) &&
                it.name == request.name &&
                it.email.equals(request.email, ignoreCase = true)
        } ?: return Result.failure(IllegalArgumentException("VERIFY_PASSWORD_FAILED"))

        val token = "mock_reset_${user.userId}_${System.currentTimeMillis()}"
        resetTokenToUserId[token] = user.userId
        return Result.success(token)
    }

    override suspend fun resetPassword(request: ResetPasswordRequest): Result<Unit> {
        delay(1)
        if (request.newPassword != request.confirmPassword) {
            return Result.failure(IllegalArgumentException("PASSWORD_MISMATCH"))
        }
        if (request.newPassword.isBlank()) {
            return Result.failure(IllegalArgumentException("INVALID_PASSWORD"))
        }
        val userId = resetTokenToUserId.remove(request.refreshToken)
            ?: return Result.failure(IllegalStateException("UNAUTHORIZED"))
        val user = users.firstOrNull { it.userId == userId }
            ?: return Result.failure(IllegalStateException("USER_NOT_FOUND"))
        user.password = request.newPassword
        return Result.success(Unit)
    }

    override suspend fun me(accessToken: String): Result<MeResponse> {
        delay(1)
        val userId = accessTokenToUserId[accessToken]
            ?: return Result.failure(IllegalStateException("UNAUTHORIZED"))
        val me = users.firstOrNull { it.userId == userId }
            ?: return Result.failure(IllegalStateException("USER_NOT_FOUND"))
        return Result.success(
            MeResponse(
                name = me.name,
                email = me.email,
                subscription = SubscriptionMeResponse(planType = "FREE")
            )
        )
    }

    override suspend fun deleteMe(accessToken: String): Result<Unit> {
        delay(1)
        val userId = accessTokenToUserId[accessToken]
            ?: return Result.failure(IllegalStateException("UNAUTHORIZED"))
        val removed = users.removeAll { it.userId == userId }
        if (!removed) return Result.failure(IllegalStateException("USER_NOT_FOUND"))

        accessTokenToUserId.entries.removeAll { it.value == userId }
        refreshTokenToUserId.entries.removeAll { it.value == userId }
        return Result.success(Unit)
    }
}
