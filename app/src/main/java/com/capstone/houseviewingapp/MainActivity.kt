package com.capstone.houseviewingapp

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.capstone.houseviewingapp.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding : ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityMainBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(binding.root)
        //NOTE : bottom 빼고 여백 남기기
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, 0)
            insets
        }

        // NOTE: BottomNavigationView가 멋대로 패딩 먹는 거 강제 차단
        ViewCompat.setOnApplyWindowInsetsListener(binding.navigationBar) { v, insets ->
            // NOTE : "좌, 우, 위, 아래 패딩 전부 0으로 고정해!"
            v.setPadding(0, 0, 0, 0)
            insets
        }
    }
}