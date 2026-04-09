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

/**
 * 백엔드 연동 전/후를 동일하게 쓰기 위한 추상화 인터페이스
 *
 * 현재:
 * - MockAuthRepository 로컬 구현 사용 가능
 *
 * 추후:
 * - RetrofitAuthRepository 구현 추가 후 갈아끼우면 됨
 */
interface AuthRepository {
    fun register(request: RegisterRequest): Result<RegisterResponse>
    fun isLoginIdAvailable(loginId: String): Result<Boolean>
    fun isEmailAvailable(email: String): Result<Boolean>
    fun login(request: LoginRequest): Result<LoginResponse>
    fun logout(accessToken: String): Result<Unit>
    fun reissue(accessToken: String, request: ReissueRequest): Result<ReissueResponse>
    fun findId(request: FindIdRequest): Result<FindIdResponse>
    fun issueResetToken(loginId: String, name: String, email: String): Result<String>
    fun verifyPassword(request: VerifyPasswordRequest): Result<Boolean>
    fun resetPassword(request: ResetPasswordRequest): Result<Unit>
    fun me(accessToken: String): Result<MeResponse>
    fun deleteMe(accessToken: String): Result<Unit>
}

