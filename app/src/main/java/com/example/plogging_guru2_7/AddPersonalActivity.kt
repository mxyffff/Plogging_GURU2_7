package com.example.plogging_guru2_7

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.bumptech.glide.Glide
import com.example.plogging_guru2_7.databinding.ActivityAddPersonalBinding

class AddPersonalActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddPersonalBinding
    private lateinit var firebaseManager: FirebaseManager
    private var precord: FirebaseManager.precord? = null
    private var selectedPhotoUri: Uri? = null

    companion object {
        private const val REQUEST_CODE_SELECT_PHOTO = 1
    }

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
        precord = intent.getParcelableExtra("activity")

        precord?.let {
            binding.etTitle.setText(it.personalName)
            binding.etpDate.setText(it.date.toString())
            binding.etpLocation.setText(it.personalPlace)
            binding.etMemo.setText(it.memo)
            if (it.photo.isNotEmpty()) {
                Glide.with(this).load(it.photo).into(binding.ivPhoto)
            }
        }

        binding.btnSave.setOnClickListener {
            savePersonalRecord()
        }

        binding.ivPhoto.setOnClickListener {
            selectPhoto()
        }

        binding.btnBack.setOnClickListener {
            finish()
        }


        // Handle saving data to Firebase
        binding.btnSave.setOnClickListener {
            savePersonalRecord()
        }
    }

    private fun selectPhoto() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        startActivityForResult(intent, REQUEST_CODE_SELECT_PHOTO)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE_SELECT_PHOTO && resultCode == Activity.RESULT_OK) {
            selectedPhotoUri = data?.data
            binding.ivPhoto.setImageURI(selectedPhotoUri)
        }
    }

    private fun savePersonalRecord() {
        val personalName = binding.etTitle.text.toString()
        val personalPlace = binding.etpLocation.text.toString()
        val memo = binding.etMemo.text.toString()
        val date = binding.etpDate.text.toString().toIntOrNull() ?: 0

        val saveRecord: (String) -> Unit = { photoUrl ->
            val updatedPrecord = precord?.copy(
                personalName = personalName,
                personalPlace = personalPlace,
                memo = memo,
                photo = photoUrl,
                date = date
            ) ?: FirebaseManager.precord(
                personalName = personalName,
                personalPlace = personalPlace,
                memo = memo,
                photo = photoUrl,
                date = date
            )

            if (precord != null) {
                // 기존 기록 업데이트
                firebaseManager.updatePrecord(updatedPrecord) { success ->
                    if (success) {
                        // 업데이트된 기록을 반환
                        setResult(Activity.RESULT_OK, Intent().putExtra("updatedActivity", updatedPrecord))
                        finish()
                    } else {
                        Toast.makeText(this, "기록 업데이트에 실패했습니다.", Toast.LENGTH_SHORT).show()
                    }
                }
            } else {
                // 새로운 기록 추가
                firebaseManager.addPrecord(updatedPrecord) { success ->
                    if (success) {
                        // 추가된 기록을 반환
                        setResult(Activity.RESULT_OK, Intent().putExtra("updatedActivity", updatedPrecord))
                        finish()
                    } else {
                        Toast.makeText(this, "기록 추가에 실패했습니다.", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }

        if (selectedPhotoUri != null) {
            firebaseManager.uploadPhoto(selectedPhotoUri!!) { photoUrl ->
                if (photoUrl != null) {
                    saveRecord(photoUrl)
                    Toast.makeText(this, "사진과 함께 업로드 성공!.", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this, "사진 업로드에 실패했습니다.", Toast.LENGTH_SHORT).show()
                }
            }
        } else {
            saveRecord(precord?.photo ?: "")
        }
    }
}