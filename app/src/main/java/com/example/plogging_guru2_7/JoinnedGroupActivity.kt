package com.example.plogging_guru2_7

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.plogging_guru2_7.databinding.ActivityChangePwBinding
import com.example.plogging_guru2_7.databinding.ActivityJoinnedGroupBinding

class JoinnedGroupActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_joinned_group)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // 뷰 바인딩
        val binding = ActivityJoinnedGroupBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 'back' 버튼 클릭시
        binding.back.setOnClickListener {
            finish() // 현재 액티비티 종료, 마이페이지 화면으로 돌아가기
        }
    }
}