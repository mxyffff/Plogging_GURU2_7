package com.example.plogging_guru2_7


import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.plogging_guru2_7.R
import com.example.plogging_guru2_7.databinding.ActivityAddGroupBinding
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class AddGroupActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddGroupBinding
    private lateinit var firebaseManager: FirebaseManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_group)
        enableEdgeToEdge()
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // binding
        binding = ActivityAddGroupBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // FirebaseManager 초기화
        firebaseManager = FirebaseManager()


        // 'back' 버튼 클릭시
        binding.btnBack.setOnClickListener {
            val intent = Intent(this, CalendarActivity::class.java)
            startActivity(intent)
            finish()
        }

        // Handle saving data to Firebase
        binding.btnSave.setOnClickListener {
            saveGroupToDatabase()
            // Save data to Firebase
        }

        // EditText에 기존 데이터 표시
        intent.getParcelableExtra<FirebaseManager.grecord>("activity")?.let { grecord ->
            binding.etGroupName.setText(grecord.groupName)
            binding.etDate.setText(grecord.date.toString())
            binding.etTime.setText(grecord.meetingTime.toString())
            binding.etLocation.setText(grecord.groupPlace)
            binding.etSupplies.setText(grecord.supplies)
            binding.etFeedback.setText(grecord.feedback)
        }
    }

    // add group record
    private fun saveGroupToDatabase() {
        val groupName = binding.etGroupName.text.toString()
        val date = binding.etDate.text.toString().toIntOrNull() ?: 0
        val meetingTime = binding.etTime.text.toString().toIntOrNull() ?: 0
        val groupPlace = binding.etLocation.text.toString()
        val supplies = binding.etSupplies.text.toString()
        val feedback = binding.etFeedback.text.toString()


        if (groupName.isNotEmpty() && date > 0 && meetingTime > 0 && groupPlace.isNotEmpty() && supplies.isNotEmpty() && feedback.isNotEmpty() ) {

            val grecord = FirebaseManager.grecord(
                groupName = groupName,
                date = date,
                meetingTime = meetingTime,
                groupPlace = groupPlace,
                supplies = supplies,
                feedback = feedback
            )

            firebaseManager.addGrecord(grecord) { success ->
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