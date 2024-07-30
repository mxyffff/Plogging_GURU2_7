package com.example.plogging_guru2_7

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.icu.util.Calendar
import android.os.Bundle
import android.widget.DatePicker
import android.widget.TimePicker
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.plogging_guru2_7.databinding.ActivityMakingGroupBinding

class MakingGroupActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMakingGroupBinding
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var firebaseManager: FirebaseManager

    private var selectedDate: String = ""
    private var selectedTime: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_making_group)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // 뷰 바인딩
        binding = ActivityMakingGroupBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // SharedPreferences 초기화
        sharedPreferences = getSharedPreferences("login_prefs", Context.MODE_PRIVATE)

        // FirebaseManager 초기화
        firebaseManager = FirebaseManager()

        // 날짜 선택 버튼 클릭 리스너
        binding.btnSelectDate.setOnClickListener {
            showDatePickerDialog()
        }

        // 시간 선택 버튼 클릭 리스너
        binding.btnSelectTime.setOnClickListener {
            showTimePickerDialog()
        }

        // 위치 선택 버튼 클릭 리스너
        binding.btnSelectPlace.setOnClickListener {
            val intent = Intent(this, NaverMapActivity::class.java)
            startActivityForResult(intent, REQUEST_CODE_SELECT_PLACE)
        }

        // 모임 만들기 버튼 클릭 리스너
        binding.btnEnter.setOnClickListener {
            createGroup()
        }

        // 뒤로가기 버튼 클릭 리스너
        binding.btnBack.setOnClickListener {
            val intent = Intent(this, CommunityActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE_SELECT_PLACE && resultCode == RESULT_OK) {
            val selectedAddress = data?.getStringExtra("address")
            val latitude = data?.getDoubleExtra("latitude", 0.0)
            val longitude = data?.getDoubleExtra("longitude", 0.0)
            binding.placeResult.text = selectedAddress
            // 필요시 위도와 경도도 처리 가능
        }
    }

    // 날짜 선택 다이얼로그 표시
    private fun showDatePickerDialog() {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog = DatePickerDialog(this,
            { _: DatePicker, selectedYear: Int, selectedMonth: Int, selectedDay: Int ->
                selectedDate = "$selectedYear/${selectedMonth + 1}/$selectedDay"
                updateDateTimeResult()
            }, year, month, day)
        datePickerDialog.show()
    }

    // 시간 선택 다이얼로그 표시
    private fun showTimePickerDialog() {
        val calendar = Calendar.getInstance()
        val hour = calendar.get(Calendar.HOUR_OF_DAY)
        val minute = calendar.get(Calendar.MINUTE)

        val timePickerDialog = TimePickerDialog(this,
            { _: TimePicker, selectedHour: Int, selectedMinute: Int ->
                selectedTime = "$selectedHour:$selectedMinute"
                updateDateTimeResult()
            }, hour, minute, true)
        timePickerDialog.show()
    }

    // 날짜 및 시간 결과 업데이트
    private fun updateDateTimeResult() {
        binding.datetimeResult.text = "$selectedDate $selectedTime"
    }


    // 모임을 생성하고 데이터베이스에 저장
    private fun createGroup() {
        val groupName = binding.groupName.text.toString()
        val groupMembers = binding.groupMembers.text.toString().toIntOrNull() ?: 0
        val meetingTime = binding.datetimeResult.text.toString()
        val groupPlace = binding.placeResult.text.toString()
        val emoji = binding.emoji.text.toString()
        val detailPlace = binding.detailPlace.text.toString()

        // 로그인된 사용자 이름 가져오기
        val username = sharedPreferences.getString("username", null)

        if (groupName.isNotEmpty() && groupMembers > 0 && meetingTime.isNotEmpty() && groupPlace.isNotEmpty() && username != null) {

            val group = FirebaseManager.Group(
                groupName = groupName,
                groupMembers = groupMembers,
                meetingTime = meetingTime,
                groupPlace = groupPlace,
                userId = username,
                emoji = emoji,
                detailPlace = detailPlace
            )

            firebaseManager.addGroup(group) { success ->
                if (success) {
                    Toast.makeText(this, "모임이 성공적으로 생성되었습니다.", Toast.LENGTH_SHORT).show()
                    // 성공적으로 그룹이 생성되면 CommunityActivity로 이동
                    val intent = Intent(this, CommunityActivity::class.java)
                    startActivity(intent)
                    finish()
                } else {
                    Toast.makeText(this, "모임 생성에 실패했습니다.", Toast.LENGTH_SHORT).show()
                }
            }
        } else {
            Toast.makeText(this, "모든 정보를 입력해 주세요.", Toast.LENGTH_SHORT).show()
        }
    }

    companion object {
        private const val REQUEST_CODE_SELECT_PLACE = 1001
    }
}