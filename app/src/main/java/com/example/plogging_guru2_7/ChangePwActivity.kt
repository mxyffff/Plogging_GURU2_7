package com.example.plogging_guru2_7

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.plogging_guru2_7.databinding.ActivityChangePwBinding

class ChangePwActivity : AppCompatActivity() {

    private lateinit var firebaseManager: FirebaseManager
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var editor: SharedPreferences.Editor
    private lateinit var currentUsername: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_change_pw)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // 뷰 바인딩
        val binding = ActivityChangePwBinding.inflate(layoutInflater)
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

        // 엔터 버튼 클릭 이벤트
        binding.btnEnter.setOnClickListener {
            val oldPw = binding.oldPw.text.toString()
            val newPw = binding.newPw.text.toString()
            val reNewPw = binding.reNewPw.text.toString()

            // 비밀번호가 모두 입력되었는지 확인
            if (oldPw.isEmpty() || newPw.isEmpty() || reNewPw.isEmpty()) {
                Toast.makeText(this, "모든 칸을 입력해주세요", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // 새 비밀번호와 비밀번호 확인이 일치하는지 확인
            if (newPw != reNewPw) {
                Toast.makeText(this, "새 비밀번호가 일치하지 않습니다", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // 비밀번호 유효성 검사
            val register = RegisterActivity()
            if (!register.isValidPassword(newPw)) {
                Toast.makeText(this,
                    "비밀번호는 최소 8글자 이상이어야 하고, 영문과 숫자 조합이어야 합니다", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // 현재 비밀번호가 올바른지 확인
            firebaseManager.isUserValid(currentUsername, oldPw) { isValid ->
                if (isValid) {
                    // 비밀번호 변경
                    firebaseManager.updateUserPassword(currentUsername, newPw) { success ->
                        if (success) {
                            Toast.makeText(this, "비밀번호가 성공적으로 변경되었습니다", Toast.LENGTH_SHORT).show()
                        } else {
                            Toast.makeText(this, "비밀번호 변경 실패", Toast.LENGTH_SHORT).show()
                        }
                    }
                } else {
                    Toast.makeText(this, "현재 비밀번호가 올바르지 않습니다", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}