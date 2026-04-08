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
import com.capstone.houseviewingapp.auth.model.FindIdRequest
import com.capstone.houseviewingapp.R
import com.capstone.houseviewingapp.databinding.ActivityFindPasswordBinding

class FindPasswordActivity : AppCompatActivity() {
    private lateinit var binding: ActivityFindPasswordBinding

    private fun setConfirmEnabled(enabled: Boolean) {
        binding.confirmButton.isEnabled = enabled
        val color = androidx.core.content.ContextCompat.getColor(
            this,
            if (enabled) R.color.blue else R.color.icongray
        )
        binding.confirmButton.backgroundTintList = android.content.res.ColorStateList.valueOf(color)
    }

    private fun validateInputs() {
        val nameOk = binding.nameEditText.text?.toString()?.trim().orEmpty().isNotBlank()
        val idOk = binding.idEditText.text?.toString()?.trim().orEmpty().isNotBlank()
        val emailOk = binding.emailEditText.text?.toString()?.trim().orEmpty().isNotBlank()
        setConfirmEnabled(nameOk && idOk && emailOk)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityFindPasswordBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            val ime = insets.getInsets(WindowInsetsCompat.Type.ime())
            val bottom = maxOf(systemBars.bottom, ime.bottom)
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, bottom)
            insets
        }

        // NOTE: 뒤로가기 버튼 (로그인 화면으로)
        binding.backButton.setOnClickListener {
            finish()
        }
        binding.nameEditText.addTextChangedListener { validateInputs() }
        binding.idEditText.addTextChangedListener { validateInputs() }
        binding.emailEditText.addTextChangedListener { validateInputs() }

        binding.confirmButton.setOnClickListener {
            val request = FindIdRequest(
                name = binding.nameEditText.text?.toString()?.trim().orEmpty(),
                email = binding.emailEditText.text?.toString()?.trim().orEmpty()
            )
            val inputLoginId = binding.idEditText.text?.toString()?.trim().orEmpty()

            val result = AuthRepositoryProvider.repository.findId(request)
            result.onSuccess { res ->
                if (res.loginId == inputLoginId) {
                    startActivity(Intent(this, ResetPasswordActivity::class.java))
                } else {
                    Toast.makeText(this, "입력한 아이디 정보가 일치하지 않습니다.", Toast.LENGTH_SHORT).show()
                }
            }.onFailure {
                Toast.makeText(this, "일치하는 계정을 찾지 못했습니다.", Toast.LENGTH_SHORT).show()
            }
        }
        validateInputs()
    }
}