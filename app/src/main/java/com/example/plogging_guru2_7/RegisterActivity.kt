package com.example.plogging_guru2_7

import android.os.Bundle
import android.util.Patterns
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.plogging_guru2_7.databinding.ActivityMainBinding
import com.example.plogging_guru2_7.databinding.ActivityRegisterBinding

class RegisterActivity : AppCompatActivity() {

    private lateinit var dbManager: DBManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_register)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        // 뷰 바인딩 선언
        val binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        dbManager = DBManager(this, "users", null, 1) // DBManger 초기화

        // 회원가입 버튼 클릭 이벤트 처리
        binding.btnSignup.setOnClickListener {
            val username = binding.username.text.toString()
            val password = binding.password.text.toString()
            val rePassword = binding.rePassword.text.toString() // 비밀번호 확인
            val email = binding.email.text.toString()
            val nickname = binding.nickname.text.toString()
            registerUser(username, password, rePassword, email, nickname) // 사용자 등록
        }
    }

    // 사용자 등록 처리
    private fun registerUser(username: String, password: String, rePassword: String, email: String, nickname: String) {
        // 모든 입력 필드가 채워졌는지 확인
        if (username.isEmpty() || password.isEmpty() || rePassword.isEmpty() || email.isEmpty() || nickname.isEmpty()) {
            Toast.makeText(this, "모든 란을 입력해주세요", Toast.LENGTH_SHORT).show()
            return
        }

        // 이메일 유효성 검사
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            Toast.makeText(this, "이메일 주소가 올바르지 않습니다", Toast.LENGTH_SHORT).show()
            return
        }

        // 비밀번호를 일치하게 입력했는지 확인
        if(password != rePassword) {
            Toast.makeText(this, "비밀번호가 일치하지 않습니다", Toast.LENGTH_SHORT).show()
            return
        }

        // 비밀번호 유효성 검사
        if (!isValidPassword(password)) {
            Toast.makeText(this,
                "비밀번호는 최소 8글자 이상이어야 하고, 영문과 숫자 조합이어야 합니다", Toast.LENGTH_SHORT).show()
            return
        }

        // 아이디 중복 확인
        if (dbManager.getUserByUsername(username)?.moveToFirst() == true) {
            Toast.makeText(this, "이미 존재하는 아이디입니다", Toast.LENGTH_SHORT).show()
            return
        }

        // 새로운 사용자 추가
        val success = dbManager.addUser(username, password, email, nickname)
        if (success) {
            Toast.makeText(this, "회원가입에 성공했습니다", Toast.LENGTH_SHORT).show()
            finish()  // 회원가입 완료 후 로그인 화면으로 돌아가기
        } else {
            Toast.makeText(this, "회원가입에 실패했습니다", Toast.LENGTH_SHORT).show()
        }
    }

    // 비밀번호 유효성 검사 함수
    private fun isValidPassword(password: String): Boolean {
        return password.length >= 8 && password.any { it.isDigit() } && password.any { it.isLetter() }
    }
}