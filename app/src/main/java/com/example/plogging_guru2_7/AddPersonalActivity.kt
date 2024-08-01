package com.example.plogging_guru2_7

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.plogging_guru2_7.databinding.ActivityAddPersonalBinding

class AddPersonalActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddPersonalBinding
    private lateinit var firebaseManager: FirebaseManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_add_personal)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        //binding
        binding = ActivityAddPersonalBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // FirebaseManager 초기화
        firebaseManager = FirebaseManager()

        // 'back' 버튼 클릭시
        binding.btnBack.setOnClickListener {
            val intent = Intent(this, CalendarActivity::class.java)
            startActivity(intent)
            finish()
        }

        // EditText에 기존 데이터 표시
        intent.getParcelableExtra<FirebaseManager.precord>("activity")?.let { precord ->
            binding.etTitle.setText(precord.personalName)
            binding.etpDate.setText(precord.date.toString())
            binding.etpLocation.setText(precord.personalPlace)
            binding.etMemo.setText(precord.memo)
            // 사진 처리는 필요에 따라 추가
        }

        // Handle saving data to Firebase
        binding.btnSave.setOnClickListener {
            savePersonalToDatabase()
            // Save data to Firebase
        }
    }

    // add group record
    private fun savePersonalToDatabase() {
        val personalName = binding.etTitle.text.toString()
        val date = binding.etpDate.text.toString().toIntOrNull() ?: 0
        val personalPlace = binding.etpLocation.text.toString()
        val photo = arrayOf(binding.ivPhoto).toString()
        val memo = binding.etMemo.text.toString()


        if (personalName.isNotEmpty() && date > 0 && personalPlace.isNotEmpty() && photo.isNotEmpty() && memo.isNotEmpty()) {

            val precord = FirebaseManager.precord(
                personalName = personalName,
                date = date,
                personalPlace = personalPlace,
                photo = photo,
                memo = memo
            )

            firebaseManager.addPrecord(precord) { success ->
                if (success) {
                    Toast.makeText(this, "기록이 성공적으로 저장되었습니다.", Toast.LENGTH_SHORT).show()
                    // move to CalendarActivity
                    val intent = Intent(this, CalendarActivity::class.java)
                    startActivity(intent)
                    finish()
                } else {
                    Toast.makeText(this, "기록 저장에 실패했습니다.", Toast.LENGTH_SHORT).show()
                }
            }
        } else {
            Toast.makeText(this, "모든 정보를 입력해 주세요.", Toast.LENGTH_SHORT).show()
        }
    }
}