package com.example.plogging_guru2_7

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.plogging_guru2_7.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var dbManager: DBManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        // 뷰 바인딩 선언
        val binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        dbManager = DBManager(this, "users", null, 1) // DBManager 초기화

        // 로그인 버튼 클릭 이벤트
        binding.btnSignin.setOnClickListener {
            val username = binding.username.text.toString()
            val password = binding.password.text.toString()
            loginUser(username, password)
        }

        // 회원가입 버튼 클릭 이벤트
        binding.btnSignup.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }
    }

    // 사용자 로그인 처리
    private fun loginUser(username: String, password: String) {
        // 사용자 이름과 비밀번호가 입력되었는지 확인
        if(username.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "아이디와 비밀번호를 모두 입력해주세요",Toast.LENGTH_SHORT).show()
            return
        }

        // DB에서 사용자 검색
        val cursor = dbManager.getUserByUsername(username)
        if (cursor != null && cursor.moveToFirst()) {
            // 저장된 password 가져오기
            val storedPassword = cursor.getString(cursor.getColumnIndexOrThrow(DBManager.COLUMN_PASSWORD))
            // password 일치 여부 확인
            if (storedPassword == password) {
                Toast.makeText(this, "로그인 성공", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "틀린 비밀번호입니다", Toast.LENGTH_SHORT).show()
            }
        } else {
            Toast.makeText(this, "존재하지 않는 아이디입니다.", Toast.LENGTH_SHORT).show()
        }
        cursor?.close()
    }
}