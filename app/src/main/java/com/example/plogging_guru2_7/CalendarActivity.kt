package com.example.plogging_guru2_7

import android.animation.ObjectAnimator
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.CalendarView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.plogging_guru2_7.databinding.ActivityCalendarBinding
import com.google.android.material.floatingactionbutton.FloatingActionButton

class CalendarActivity : AppCompatActivity(), ActivityAdapter.OnItemClickListener {

    private lateinit var binding: ActivityCalendarBinding
    private lateinit var firebaseManager: FirebaseManager
    private lateinit var activityAdapter: ActivityAdapter
    private var selectedDate: String = ""
    private var isFocusable = false

    companion object {
        private const val REQUEST_CODE_EDIT_ACTIVITY = 2
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_calendar)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // binding
        binding = ActivityCalendarBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // FirebaseManager 초기화
        firebaseManager = FirebaseManager()

        // Calendar setup
        setupCalendar()

        // Floating button setup
        binding.fab.setOnClickListener {
            // Show the floating menu
            if(isFocusable) {
                ObjectAnimator.ofFloat(binding.fabAddGroup, "translationY", 0f).apply { start() }
                ObjectAnimator.ofFloat(binding.fabAddPersonal, "translationY", 0f).apply { start() }
            }
            else {
                ObjectAnimator.ofFloat(binding.fabAddGroup, "translationY", -200f).apply { start() }
                ObjectAnimator.ofFloat(binding.fabAddPersonal, "translationY", -400f).apply { start() }
            }

            isFocusable = !isFocusable
        }

        // 개인 플로깅 버튼 클릭 시
        binding.fabAddPersonal.setOnClickListener {
            startActivity(Intent(this, AddPersonalActivity::class.java))
        }

        // 그룹 플로깅 버튼 클릭 시
        binding.fabAddGroup.setOnClickListener {
            startActivity(Intent(this, AddGroupActivity::class.java))
        }

        /// RecyclerView setup for listing activities
        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        activityAdapter = ActivityAdapter(emptyList(), this)
        binding.recyclerView.adapter = activityAdapter


        // 하단바
        //각 버튼에 클릭 리스너 설정 (각 페이지로 이동)
        binding.mapIcon.setOnClickListener {
            val intent = Intent(this, PloggingSpotActivity::class.java)
            startActivity(intent)
        }

        binding.calendarIcon.setOnClickListener {
            //현재 페이지 유지
        }

        binding.communityIcon.setOnClickListener {
            val intent = Intent(this, CommunityActivity::class.java)
            startActivity(intent)
        }

        binding.myPageIcon.setOnClickListener {
            val intent = Intent(this, MyPageActivity::class.java)
            startActivity(intent)
        }
    }

    private fun setupCalendar() {
        val calendarView = findViewById<CalendarView>(R.id.calendarView)
        calendarView.setOnDateChangeListener { _, year, month, dayOfMonth ->
            selectedDate = "$year${String.format("%02d", month + 1)}${String.format("%02d", dayOfMonth)}"
            loadActivitiesForDate(selectedDate)
        }
    }

    private fun loadActivitiesForDate(date: String) {
        firebaseManager.getActivitiesByDate(date) { activities ->
            activityAdapter.updateData(activities)
        }
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE_EDIT_ACTIVITY && resultCode == RESULT_OK) {
            loadActivitiesForDate(selectedDate)
        }
    }

    override fun onItemClick(activity: Any) {
        val intent = when (activity) {
            is FirebaseManager.grecord -> Intent(this, AddGroupActivity::class.java).apply {
                putExtra("activity", activity)
            }
            is FirebaseManager.precord -> Intent(this, AddPersonalActivity::class.java).apply {
                putExtra("activity", activity)
            }
            else -> return
        }
        startActivityForResult(intent, REQUEST_CODE_EDIT_ACTIVITY)
    }

    override fun onDeleteClick(activity: Any) {
        val id = when (activity) {
            is FirebaseManager.grecord -> activity.id
            is FirebaseManager.precord -> activity.id
            else -> return
        }
        firebaseManager.deleteActivity(id) { success ->
            if (success) {
                Toast.makeText(this, "기록이 삭제되었습니다.", Toast.LENGTH_SHORT).show()
                loadActivitiesForDate(selectedDate)  // 삭제 후 해당 날짜의 데이터를 다시 로드
            } else {
                Toast.makeText(this, "기록 삭제에 실패했습니다.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun putExtra(s: String, activity: FirebaseManager.grecord) {
        TODO("Not yet implemented")
    }

    override fun putExtra(s: String, activity: FirebaseManager.precord) {
        TODO("Not yet implemented")
    }
}