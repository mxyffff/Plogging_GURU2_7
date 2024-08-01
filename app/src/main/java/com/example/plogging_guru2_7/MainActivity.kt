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
import com.example.plogging_guru2_7.databinding.ActivityMainBinding
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {

    private lateinit var firebaseManager: FirebaseManager
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var editor: SharedPreferences.Editor


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

        // FirebaseManager 및 SharedPreferences 초기화
        firebaseManager = FirebaseManager()
        sharedPreferences = getSharedPreferences("login_prefs", Context.MODE_PRIVATE)
        editor = sharedPreferences.edit()

        // 로그인 유지 체크 확인
        if (sharedPreferences.getBoolean("stay_logged_in", false)) {
            val savedUsername = sharedPreferences.getString("username", null)
            val savedPassword = sharedPreferences.getString("password", null)
            if (savedUsername != null && savedPassword != null) {
                loginUser(savedUsername, savedPassword, false)
            }
        }

        // 로그인 버튼 클릭 이벤트
        binding.btnSignin.setOnClickListener {
            val username = binding.username.text.toString()
            val password = binding.password.text.toString()
            val stayLoggedIn = binding.chkStayLogin.isChecked
            loginUser(username, password, stayLoggedIn)
        }

        // 회원가입 버튼 클릭 이벤트
        binding.btnSignup.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }

        // 비밀번호 찾기 버튼 클릭 이벤트
        binding.btnFindPW.setOnClickListener {
            val intent = Intent(this, FindPasswordActivity::class.java)
            startActivity(intent)
        }
    }

    // 사용자 로그인 처리
    private fun loginUser(username: String, password: String, stayLoggedIn: Boolean) {
        // 사용자 이름과 비밀번호가 입력되었는지 확인
        if(username.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "아이디와 비밀번호를 모두 입력해주세요", Toast.LENGTH_SHORT).show()
            return
        }

        // Firebase에서 사용자 인증
        firebaseManager.isUserValid(username, password) { isValid ->
            if (isValid) {
                Toast.makeText(this, "로그인 성공", Toast.LENGTH_SHORT).show()

                if (stayLoggedIn) {
                    // 로그인 유지 기능 처리: SharedPreferences를 사용하여 로그인 상태 저장
                    editor.putBoolean("stay_logged_in", true)
                    editor.putString("username", username)
                    editor.putString("password", password)
                    editor.apply()
                    Toast.makeText(this, "로그인 상태가 유지됩니다", Toast.LENGTH_SHORT).show()
                } else {
                    editor.putString("username", username)
                    editor.putString("password", password)
                    editor.apply()
                    Log.d("MainActivity", "Saved username: $username")  // 로그 추가
                }
                // 성공적으로 로그인하면 MyPageActivity로 이동 // 추후 변경
                val intent = Intent(this, CommunityActivity::class.java)
                startActivity(intent)
                finish()
            } else {
                Toast.makeText(this, "아이디 또는 비밀번호가 올바르지 않습니다", Toast.LENGTH_SHORT).show()
            }
        }
    }
}