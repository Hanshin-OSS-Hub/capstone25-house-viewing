package com.capstone.houseviewingapp.login

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.addTextChangedListener
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.capstone.houseviewingapp.auth.AuthRepositoryProvider
import com.capstone.houseviewingapp.auth.model.ResetPasswordRequest
import com.capstone.houseviewingapp.R
import com.capstone.houseviewingapp.databinding.ActivityResetPasswordBinding

class ResetPasswordActivity : AppCompatActivity() {
    companion object {
        const val EXTRA_RESET_TOKEN = "extra_reset_token"
    }

    private lateinit var binding: ActivityResetPasswordBinding
    private var resetToken: String = ""

    private fun setConfirmEnabled(enabled: Boolean) {
        binding.confirmButton.isEnabled = enabled
        val color = androidx.core.content.ContextCompat.getColor(
            this,
            if (enabled) R.color.blue else R.color.icongray
        )
        binding.confirmButton.backgroundTintList = android.content.res.ColorStateList.valueOf(color)
    }

    private fun validateInputs() {
        val password = binding.passwordEditText.text?.toString().orEmpty()
        val passwordCheck = binding.passwordCheckEditText.text?.toString().orEmpty()
        val passwordOk = password.length >= 8
        val passwordMatch = password == passwordCheck && passwordCheck.isNotBlank()

        if (passwordCheck.isNotBlank() && !passwordMatch) {
            binding.passwordCheckTextInputLayout.error = "비밀번호가 일치하지 않습니다."
        } else {
            binding.passwordCheckTextInputLayout.error = null
        }
        setConfirmEnabled(passwordOk && passwordMatch)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityResetPasswordBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        resetToken = intent.getStringExtra(EXTRA_RESET_TOKEN).orEmpty()
        if (resetToken.isBlank()) {
            Toast.makeText(this, "인증 정보가 없습니다. 비밀번호 찾기부터 다시 진행해 주세요.", Toast.LENGTH_SHORT).show()
            finish()
            return
        }
        enableEdgeToEdge()
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            val ime = insets.getInsets(WindowInsetsCompat.Type.ime())
            val bottom = maxOf(systemBars.bottom, ime.bottom)
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, bottom)
            insets
        }
        binding.backButton.setOnClickListener { finish() }
        binding.passwordEditText.addTextChangedListener { validateInputs() }
        binding.passwordCheckEditText.addTextChangedListener { validateInputs() }

        //NOTE : 비밀번호 변경 완료 버튼
        binding.confirmButton.setOnClickListener {
            val request = ResetPasswordRequest(
                resetToken = resetToken,
                password = binding.passwordEditText.text?.toString().orEmpty()
            )
            val result = AuthRepositoryProvider.repository.resetPassword(request)
            result.onSuccess {
                val intent = Intent(this, LoginActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_CLEAR_TOP
                startActivity(intent)
                Toast.makeText(this, "비밀번호 변경이 완료되었습니다.", Toast.LENGTH_SHORT).show()
            }.onFailure { e ->
                val msg = when (e.message) {
                    "UNAUTHORIZED" -> "인증이 필요합니다. 다시 로그인해 주세요."
                    "INVALID_PASSWORD" -> "유효하지 않은 비밀번호입니다."
                    else -> "비밀번호 변경에 실패했습니다."
                }
                Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
            }
        }
        validateInputs()
    }
}