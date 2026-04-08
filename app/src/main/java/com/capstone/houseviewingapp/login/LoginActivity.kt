package com.capstone.houseviewingapp.login

import android.content.ComponentName
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.text.TextUtils
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.widget.addTextChangedListener
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.capstone.houseviewingapp.MainActivity
import com.capstone.houseviewingapp.R
import com.capstone.houseviewingapp.auth.AuthRepositoryProvider
import com.capstone.houseviewingapp.auth.model.LoginRequest
import com.capstone.houseviewingapp.data.local.AuthTokenLocalStore
import com.capstone.houseviewingapp.data.local.UserProfileLocalStore
import com.capstone.houseviewingapp.databinding.ActivityLoginBinding

class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding

    private val notificationPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        gotoMain()
    }

    private fun setLoginEnabled(enabled: Boolean) {
        binding.loginButton.isEnabled = enabled
        val color = ContextCompat.getColor(this, if (enabled) R.color.blue else R.color.icongray)
        binding.loginButton.backgroundTintList = android.content.res.ColorStateList.valueOf(color)
    }

    private fun validateInputs() {
        val idOk = binding.idEditText.text?.toString()?.trim().orEmpty().isNotBlank()
        val pwOk = binding.passwordEditText.text?.toString().orEmpty().isNotBlank()
        setLoginEnabled(idOk && pwOk)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityLoginBinding.inflate(layoutInflater)
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

        binding.signupTextView.setOnClickListener {
            startActivity(Intent(this, SignUpActivity::class.java))
        }
        binding.findIDTextView.setOnClickListener {
            startActivity(Intent(this, FindIdActivity::class.java))
        }
        binding.findPWTextView.setOnClickListener {
            startActivity(Intent(this, FindPasswordActivity::class.java))
        }
        binding.idEditText.addTextChangedListener { validateInputs() }
        binding.passwordEditText.addTextChangedListener { validateInputs() }
        binding.loginButton.setOnClickListener {
            val request = LoginRequest(
                loginId = binding.idEditText.text?.toString()?.trim().orEmpty(),
                password = binding.passwordEditText.text?.toString().orEmpty()
            )
            val result = AuthRepositoryProvider.repository.login(request)
            val token = result.getOrElse {
                Toast.makeText(this, "로그인에 실패했습니다.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            AuthTokenLocalStore.saveTokens(this, token.accessToken, token.refreshToken)
            AuthTokenLocalStore.saveLoginId(this, request.loginId)
            AuthRepositoryProvider.repository.me(token.accessToken).getOrNull()?.let { me ->
                UserProfileLocalStore.save(this, me.name, me.email, me.loginId)
            }

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.POST_NOTIFICATIONS)
                    != PackageManager.PERMISSION_GRANTED
                ) {
                    notificationPermissionLauncher.launch(android.Manifest.permission.POST_NOTIFICATIONS)
                    return@setOnClickListener
                }
            }
            gotoMain()
        }
        validateInputs()
    }

    private fun gotoMain() {
        if(isNotificationListenerEnabled()) {
            startActivity(Intent(this, MainActivity::class.java))
        } else {
            startActivity(Intent(this, MainActivity::class.java).apply {
                putExtra(MainActivity.EXTRA_SHOW_NOTIFICATION_ACCESS_GUIDE, true)
            })
        }
        finish()
    }

    private fun isNotificationListenerEnabled(): Boolean {
        val pkg = packageName
        val flat = Settings.Secure.getString(contentResolver, "enabled_notification_listeners") ?: return false
        for (name in flat.split(":")) {
            if (TextUtils.isEmpty(name)) continue
            val cn = ComponentName.unflattenFromString(name) ?: continue
            if (cn.packageName == pkg) return true
        }
        return false
    }
}