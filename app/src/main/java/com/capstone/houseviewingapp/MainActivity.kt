package com.capstone.houseviewingapp

import android.os.Bundle
import android.view.View
import android.widget.Toast
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

        val items = emptyList<HouseCardItem>()

        // NOTE: ViewPager2에 카드 아이템들 연결하기 (나중에 API에서 받아온 데이터로 교체 예정)
//        val items = listOf<HouseCardItem>(
//            HouseCardItem("서초 그랑자이 104동", "서울특별시 서초구 서초동 1234-56", 50),
//            HouseCardItem("마포 래미안 102동", "서울특별시 마포구 공덕동 11-22", 72),
//            HouseCardItem("강남 아이파크 203동", "서울특별시 강남구 역삼동 33-44", 66)
//        )

        binding.viewpager.adapter = HouseCardAdapter(items)

        updateEmptyState(items)

        //NOTE: 빈 화면에서 집 정보를 직접 입력하고 추가하는 방식
        binding.emptylayout.plusHouseButton.setOnClickListener {
            Toast.makeText(this, "살려줘....", Toast.LENGTH_SHORT).show()
        }

    }

    private fun updateEmptyState(items: List<HouseCardItem>) {
        if (items.isEmpty()) {
            binding.viewpager.visibility = View.GONE
            binding.emptylayout.root.visibility = View.VISIBLE // NOTE: include 부분 -> root 추가 이유 : include 전체를 가리키기 위해 (다른 xml 파일이여서 root 필요)
        } else {
            binding.viewpager.visibility = View.VISIBLE
            binding.emptylayout.root.visibility = View.GONE
        }
    }
}