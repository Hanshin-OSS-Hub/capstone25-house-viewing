package com.capstone.houseviewingapp.login

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.capstone.houseviewingapp.R
import com.capstone.houseviewingapp.databinding.ActivityFindIdBinding

class FindIdActivity : AppCompatActivity() {
    private lateinit var binding : ActivityFindIdBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityFindIdBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        // NOTE: 뒤로가기 버튼 (로그인 화면으로)
        binding.backButton.setOnClickListener {
            intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }
    }
}