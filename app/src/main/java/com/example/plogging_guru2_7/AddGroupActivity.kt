package com.example.plogging_guru2_7

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.bumptech.glide.Glide
import com.example.plogging_guru2_7.databinding.ActivityAddGroupBinding

class AddGroupActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddGroupBinding
    private lateinit var firebaseManager: FirebaseManager
    private var grecord: FirebaseManager.grecord? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_add_group)
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

        grecord = intent.getParcelableExtra("activity")

        grecord?.let {
            binding.etGroupName.setText(it.groupName)
            binding.etDate.setText(it.date.toString())
            binding.etTime.setText(it.meetingTime.toString())
            binding.etLocation.setText(it.groupPlace)
            binding.etSupplies.setText(it.supplies)
            binding.etFeedback.setText(it.feedback)
        }

        binding.btnSave.setOnClickListener {
            saveGroupToDatabase()
        }

        binding.btnBack.setOnClickListener {
            finish()
        }
    }

    private fun saveGroupToDatabase() {
        val groupName = binding.etGroupName.text.toString()
        val date = binding.etDate.text.toString().toIntOrNull() ?: 0
        val meetingTime = binding.etTime.text.toString().toIntOrNull() ?: 0
        val groupPlace = binding.etLocation.text.toString()
        val supplies = binding.etSupplies.text.toString()
        val feedback = binding.etFeedback.text.toString()

        val updatedGrecord = grecord?.copy(
            groupName = groupName,
            date = date,
            meetingTime = meetingTime,
            groupPlace = groupPlace,
            supplies = supplies,
            feedback = feedback
        ) ?: FirebaseManager.grecord(
            groupName = groupName,
            date = date,
            meetingTime = meetingTime,
            groupPlace = groupPlace,
            supplies = supplies,
            feedback = feedback
        )

        if (grecord != null) {
            firebaseManager.updateGrecord(updatedGrecord) { success ->
                if (success) {
                    Toast.makeText(this, "기록이 업데이트되었습니다.", Toast.LENGTH_SHORT).show()
                    finish()
                } else {
                    Toast.makeText(this, "기록 업데이트에 실패했습니다.", Toast.LENGTH_SHORT).show()
                }
            }
        } else {
            firebaseManager.addGrecord(updatedGrecord) { success ->
                if (success) {
                    Toast.makeText(this, "기록이 추가되었습니다.", Toast.LENGTH_SHORT).show()
                    finish()
                } else {
                    Toast.makeText(this, "기록 추가에 실패했습니다.", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}