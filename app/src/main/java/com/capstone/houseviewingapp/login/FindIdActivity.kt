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
            val ime = insets.getInsets(WindowInsetsCompat.Type.ime())
            val bottom = maxOf(systemBars.bottom, ime.bottom)
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, bottom)
            insets
        }
        // NOTE: 뒤로가기 버튼 (로그인 화면으로)
        binding.backButton.setOnClickListener {
//            intent = Intent(this, LoginActivity::class.java)
//            startActivity(intent) // NOTE: 이렇게 작성하면 앱 화면이 쌓여서 나중에 오류 생김
            finish()
        }
        binding.confirmButton.setOnClickListener {
            // TODO: [백엔드 연동] 나중에 실제 서버 API를 호출해서 아이디를 받아와야 함!
            // 지금은 테스트용으로 가짜 아이디("capstone123")를 넣어둠
            val id = "capstone123"
            val bottomsheet = FindIDResultFragment(id)
            bottomsheet.show(supportFragmentManager, "FindIDResultTag")
        }
    }
}