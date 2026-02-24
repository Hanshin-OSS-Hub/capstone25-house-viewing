package com.capstone.houseviewingapp.login

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.capstone.houseviewingapp.MainActivity
import com.capstone.houseviewingapp.R
import com.capstone.houseviewingapp.databinding.ActivityLoginBinding

class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding
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

       // NOTE : 회원가입 화면으로 이동
        binding.signupTextView.setOnClickListener {
            intent = Intent(this, SignUpActivity::class.java)
            startActivity(intent)
        }
        // NOTE : 아이디 찾기 화면으로 이동
        binding.findIDTextView.setOnClickListener {
            intent = Intent(this, FindIdActivity::class.java)
            startActivity(intent)
        }
        // NOTE : 비밀번호 찾기 화면으로 이동
        binding.findPWTextView.setOnClickListener {
            intent = Intent(this, FindPasswordActivity::class.java)
            startActivity(intent)
        }
        //NOTE : 홈 화면으로 이동
        binding.loginButton.setOnClickListener {
            //TODO: DB에 있는 아이디 비번이 맞는지 체크해서 로그인 홈화면으로

            intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }
    }
}