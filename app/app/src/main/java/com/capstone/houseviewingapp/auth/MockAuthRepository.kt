package com.capstone.houseviewingapp.auth

import com.capstone.houseviewingapp.auth.model.FindIdRequest
import com.capstone.houseviewingapp.auth.model.FindIdResponse
import com.capstone.houseviewingapp.auth.model.LoginRequest
import com.capstone.houseviewingapp.auth.model.LoginResponse
import com.capstone.houseviewingapp.auth.model.MeResponse
import com.capstone.houseviewingapp.auth.model.ReissueRequest
import com.capstone.houseviewingapp.auth.model.ReissueResponse
import com.capstone.houseviewingapp.auth.model.RegisterRequest
import com.capstone.houseviewingapp.auth.model.RegisterResponse
import com.capstone.houseviewingapp.auth.model.ResetPasswordRequest
import com.capstone.houseviewingapp.auth.model.VerifyPasswordRequest
import java.util.concurrent.atomic.AtomicLong

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

    override fun register(request: RegisterRequest): Result<RegisterResponse> {
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
        return Result.success(RegisterResponse(userId = newUser.userId))
    }

    override fun isLoginIdAvailable(loginId: String): Result<Boolean> {
        val normalized = loginId.trim()
        if (normalized.isBlank()) return Result.failure(IllegalArgumentException("INVALID_LOGIN_ID"))
        return Result.success(users.none { it.loginId.equals(normalized, ignoreCase = true) })
    }

    override fun isEmailAvailable(email: String): Result<Boolean> {
        val normalized = email.trim()
        if (normalized.isBlank()) return Result.failure(IllegalArgumentException("INVALID_EMAIL"))
        return Result.success(users.none { it.email.equals(normalized, ignoreCase = true) })
    }

    override fun login(request: LoginRequest): Result<LoginResponse> {
        val user = users.firstOrNull { it.loginId == request.loginId && it.password == request.password }
            ?: return Result.failure(IllegalArgumentException("LOGIN_FAILED"))

        val access = "mock_access_${user.userId}_${System.currentTimeMillis()}"
        val refresh = "mock_refresh_${user.userId}_${System.currentTimeMillis()}"
        accessTokenToUserId[access] = user.userId
        refreshTokenToUserId[refresh] = user.userId
        return Result.success(LoginResponse(accessToken = access, refreshToken = refresh))
    }

    override fun logout(accessToken: String): Result<Unit> {
        val userId = accessTokenToUserId.remove(accessToken)
            ?: return Result.failure(IllegalArgumentException("INVALID_HEADER"))
        // 같은 유저의 refresh 토큰도 무효화
        refreshTokenToUserId.entries.removeAll { it.value == userId }
        return Result.success(Unit)
    }

    override fun reissue(accessToken: String, request: ReissueRequest): Result<ReissueResponse> {
        if (!accessTokenToUserId.containsKey(accessToken)) {
            return Result.failure(IllegalArgumentException("INVALID_TOKEN"))
        }
        val userId = refreshTokenToUserId[request.refreshToken]
            ?: return Result.failure(IllegalArgumentException("INVALID_TOKEN"))

        // 기존 토큰 교체
        accessTokenToUserId.entries.removeAll { it.value == userId }
        refreshTokenToUserId.remove(request.refreshToken)

        val newAccess = "mock_access_${userId}_${System.currentTimeMillis()}"
        val newRefresh = "mock_refresh_${userId}_${System.currentTimeMillis()}"
        accessTokenToUserId[newAccess] = userId
        refreshTokenToUserId[newRefresh] = userId

        return Result.success(
            ReissueResponse(
                accessToken = newAccess,
                refreshToken = newRefresh
            )
        )
    }

    override fun findId(request: FindIdRequest): Result<FindIdResponse> {
        val matched = users.firstOrNull {
            it.name == request.name && it.email.equals(request.email, ignoreCase = true)
        } ?: return Result.failure(IllegalArgumentException("FIND_LOGIN_ID_FAILED"))

        return Result.success(FindIdResponse(loginId = matched.loginId))
    }

    override fun issueResetToken(loginId: String, name: String, email: String): Result<String> {
        val user = users.firstOrNull {
            it.loginId.equals(loginId, ignoreCase = true) &&
                it.name == name &&
                it.email.equals(email, ignoreCase = true)
        } ?: return Result.failure(IllegalArgumentException("RESET_AUTH_FAILED"))

        val token = "mock_reset_${user.userId}_${System.currentTimeMillis()}"
        resetTokenToUserId[token] = user.userId
        return Result.success(token)
    }

    override fun verifyPassword(request: VerifyPasswordRequest): Result<Boolean> {
        val matched = users.firstOrNull { it.loginId == request.loginId }
            ?: return Result.failure(IllegalArgumentException("VERIFY_PASSWORD_FAILED"))

        return Result.success(matched.password == request.password)
    }

    override fun resetPassword(request: ResetPasswordRequest): Result<Unit> {
        val userId = resetTokenToUserId.remove(request.resetToken)
            ?: return Result.failure(IllegalStateException("UNAUTHORIZED"))
        if (request.password.isBlank()) {
            return Result.failure(IllegalArgumentException("INVALID_PASSWORD"))
        }
        val user = users.firstOrNull { it.userId == userId }
            ?: return Result.failure(IllegalStateException("USER_NOT_FOUND"))
        user.password = request.password
        return Result.success(Unit)
    }

    override fun me(accessToken: String): Result<MeResponse> {
        val userId = accessTokenToUserId[accessToken]
            ?: return Result.failure(IllegalStateException("UNAUTHORIZED"))
        val me = users.firstOrNull { it.userId == userId }
            ?: return Result.failure(IllegalStateException("USER_NOT_FOUND"))
        return Result.success(
            MeResponse(
                name = me.name,
                email = me.email,
                loginId = me.loginId
            )
        )
    }

    override fun deleteMe(accessToken: String): Result<Unit> {
        val userId = accessTokenToUserId[accessToken]
            ?: return Result.failure(IllegalStateException("UNAUTHORIZED"))
        val removed = users.removeAll { it.userId == userId }
        if (!removed) return Result.failure(IllegalStateException("USER_NOT_FOUND"))

        accessTokenToUserId.entries.removeAll { it.value == userId }
        refreshTokenToUserId.entries.removeAll { it.value == userId }
        return Result.success(Unit)
    }
}

