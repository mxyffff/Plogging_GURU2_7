package com.example.plogging_guru2_7

import android.database.Cursor
import android.os.Bundle
import android.widget.LinearLayout
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.plogging_guru2_7.databinding.ActivityFindPasswordBinding

class FindPasswordActivity : AppCompatActivity() {

    private lateinit var dbManager: DBManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_find_password)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // DBManager 초기화
        dbManager = DBManager(this, "usersDB", null, 1)

        // 뷰 바인딩 선언
        val binding = ActivityFindPasswordBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // enter 버튼 클릭 이벤트 처리
        binding.btnEnter.setOnClickListener {
            val username = binding.edtUsername.text.toString()
            val email = binding.edtEmail.text.toString()

            if (username.isEmpty() || email.isEmpty()) {
                Toast.makeText(this, "아이디와 이메일을 모두 입력해주세요", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            // password 검색
            val password = dbManager.getPasswordByEmailAndUsername(email, username)
            if (password != null) {
                // 비밀번호 정보 보여주기
                binding.inputInfo.visibility = LinearLayout.GONE
                binding.passwordInfo.visibility = LinearLayout.VISIBLE // 레이아웃 교체, 이전 레이아웃 제거
                binding.passwordText.text = password
            } else {
                Toast.makeText(this, "아이디 또는 이메일이 일치하지 않습니다", Toast.LENGTH_SHORT).show()
            }
        }

        // 'Back to Login' 버튼 클릭 이벤트 처리
        binding.backToLogin.setOnClickListener {
            finish() // 현재 액티비티 종료, 로그인 화면으로 돌아가기
        }

        // 'btnBack' 버튼 클릭 이벤트 처리
        binding.btnBack.setOnClickListener {
            finish() // 현재 액티비티 종료, 로그인 화면으로 돌아가기
        }
    }
}