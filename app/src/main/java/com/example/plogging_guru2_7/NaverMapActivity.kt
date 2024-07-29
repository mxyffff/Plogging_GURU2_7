package com.example.plogging_guru2_7

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.plogging_guru2_7.databinding.FragmentNaverMapBinding

class NaverMapActivity : AppCompatActivity() {

    private lateinit var binding: FragmentNaverMapBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // fragment_naver_map.xml을 레이아웃으로 설정
        binding = FragmentNaverMapBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // NaverMapFragment를 화면에 추가
        supportFragmentManager.beginTransaction()
            .replace(R.id.map, NaverMapFragment())  // map은 fragment_naver_map.xml에서 정의된 MapView의 ID
            .commit()
    }

    fun finishWithResult(address: String, latitude: Double, longitude: Double) {
        val intent = Intent().apply {
            putExtra("address", address)
            putExtra("latitude", latitude)
            putExtra("longitude", longitude)
        }
        setResult(RESULT_OK, intent)
        finish()
    }
}