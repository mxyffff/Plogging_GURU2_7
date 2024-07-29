package com.example.plogging_guru2_7

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import com.example.plogging_guru2_7.databinding.ActivityMyPageBinding
import com.google.android.material.bottomnavigation.BottomNavigationView

class MyPageActivity : AppCompatActivity() {

    private lateinit var dbManager: DBManager
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var editor: SharedPreferences.Editor
    private lateinit var currentUsername: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_my_page)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        // 뷰 바인딩 선언
        val binding = ActivityMyPageBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // dbManger 및 SharedPreferences 초기화
        dbManager = DBManager(this, "users", null, 1)
        sharedPreferences = getSharedPreferences("login_prefs", Context.MODE_PRIVATE)
        editor = sharedPreferences.edit()

        // 현재 로그인된 사용자 이름 가져오기
        currentUsername = sharedPreferences.getString("username", "") ?: ""

        // 사용자 정보를 가져와서 UI에 표시
        val user = dbManager.getUserInfo(currentUsername)
        if (user != null) {
            binding.userId.text = user.username
        }

        // 비밀번호 변경 버튼 클릭 리스너
        binding.changePw.setOnClickListener {
            val intent = Intent(this, ChangePwActivity::class.java)
            startActivity(intent)
        }

        // 이메일 변경 버튼 클릭 리스너
        binding.changeEmail.setOnClickListener {
            val intent = Intent(this, ChangeEmailActivity::class.java)
            startActivity(intent)
        }

        // 회원 탈퇴 버튼 클릭 리스너
        binding.deleteAct.setOnClickListener {
            val intent = Intent(this, DeleteInfoActivity::class.java)
            startActivity(intent)
        }

        // 로그아웃 버튼 클릭 리스너
        binding.logout.setOnClickListener {
            // 로그인 세션 종료
            editor.clear().apply()
            Toast.makeText(this, "로그아웃 되었습니다.", Toast.LENGTH_SHORT).show()
            startActivity(Intent(this, MainActivity::class.java)) // 로그아웃 성공시 로그인 화면으로 이동
            finish()
        }
    }
}