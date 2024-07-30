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
import com.example.plogging_guru2_7.databinding.ActivityChangeEmailBinding
import com.example.plogging_guru2_7.databinding.ActivityDeleteInfoBinding

class DeleteInfoActivity : AppCompatActivity() {

    private lateinit var firebaseManager: FirebaseManager
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var editor: SharedPreferences.Editor
    private lateinit var currentUsername: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_delete_info)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // 뷰 바인딩
        val binding = ActivityDeleteInfoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // FirebaseManager 및 SharedPreferences 초기화
        firebaseManager = FirebaseManager()
        sharedPreferences = getSharedPreferences("login_prefs", Context.MODE_PRIVATE)
        editor = sharedPreferences.edit()

        // 현재 로그인된 사용자 이름 가져오기
        currentUsername = sharedPreferences.getString("username", "") ?: ""

        // 'back' 버튼 클릭시
        binding.back.setOnClickListener {
            finish() // 현재 액티비티 종료, 마이페이지 화면으로 돌아가기
        }

        // 엔터 버튼 클릭 리스너
        binding.btnEnter.setOnClickListener {
            val email = binding.edtEmail.text.toString()
            val password = binding.edtPw.text.toString()

            // 이메일과 비밀번호가 모두 입력되었는지 확인
            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "모든 칸을 입력해주세요", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            try {
                // 비밀번호와 이메일이 올바른지 확인
                firebaseManager.isUserValid(currentUsername, password) { isValid ->
                    if (isValid) {
                        firebaseManager.getUserByUsername(currentUsername) { user ->
                            if (user?.email == email) {
                                // 사용자 삭제
                                firebaseManager.deleteUser(currentUsername) { success ->
                                    if (success) {
                                        Toast.makeText(this, "회원 탈퇴가 완료되었습니다", Toast.LENGTH_SHORT).show()
                                        // 로그아웃 처리 후 메인 액티비티로 이동
                                        sharedPreferences.edit().clear().apply()
                                        startActivity(Intent(this, MainActivity::class.java))
                                        finish()
                                    } else {
                                        Toast.makeText(this, "회원 탈퇴 실패", Toast.LENGTH_SHORT).show()
                                    }
                                }
                            } else {
                                Toast.makeText(this, "이메일이나 비밀번호가 올바르지 않습니다", Toast.LENGTH_SHORT).show()
                            }
                        }
                    } else {
                        Toast.makeText(this, "이메일이나 비밀번호가 올바르지 않습니다", Toast.LENGTH_SHORT).show()
                    }
                }
            } catch (e: Exception) {
                // 예외 처리
                Toast.makeText(this, "오류가 발생했습니다. 다시 시도해 주세요.", Toast.LENGTH_SHORT).show()
            }
        }
    }
}