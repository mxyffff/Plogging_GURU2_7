package com.example.plogging_guru2_7

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.plogging_guru2_7.databinding.ActivityChangeEmailBinding
import com.example.plogging_guru2_7.databinding.ActivityChangePwBinding

class ChangeEmailActivity : AppCompatActivity() {

    private lateinit var firebaseManager: FirebaseManager
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var editor: SharedPreferences.Editor
    private lateinit var currentUsername: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_change_email)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // 뷰 바인딩
        val binding = ActivityChangeEmailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // FirebaseManager 및 SharedPreferences 초기화
        firebaseManager = FirebaseManager()
        sharedPreferences = getSharedPreferences("login_prefs", Context.MODE_PRIVATE)
        editor = sharedPreferences.edit()

        // 현재 로그인된 사용자 이름 가져오기
        currentUsername = sharedPreferences.getString("username", "") ?: ""

        // 현재 로그인된 사용자 이메일을 hint로 표시
        firebaseManager.getUserByUsername(currentUsername) { user ->
            if (user != null) {
                binding.chgEmail.hint = user.email
            }
        }

        // 'back' 버튼 클릭시
        binding.back.setOnClickListener {
            finish() // 현재 액티비티 종료, 마이페이지 화면으로 돌아가기
        }

        // 엔터 버튼 클릭 리스너
        binding.btnEnter.setOnClickListener {
            val newEmail = binding.chgEmail.text.toString()
            val password = binding.edtPw.text.toString()

            // 이메일과 비밀번호가 모두 입력되었는지 확인
            if (newEmail.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "모든 칸을 입력해주세요", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // 비밀번호가 올바른지 확인
            firebaseManager.isUserValid(currentUsername, password) { isValid ->
                if (isValid) {
                    // 이메일 변경
                    firebaseManager.updateUserEmail(currentUsername, newEmail) { success ->
                        if (success) {
                            Toast.makeText(this, "이메일이 성공적으로 변경되었습니다", Toast.LENGTH_SHORT).show()
                        } else {
                            Toast.makeText(this, "이메일 변경 실패", Toast.LENGTH_SHORT).show()
                        }
                    }
                } else {
                    Toast.makeText(this, "비밀번호가 올바르지 않습니다", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}