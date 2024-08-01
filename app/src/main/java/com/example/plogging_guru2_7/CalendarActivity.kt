package com.example.plogging_guru2_7

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.CalendarView
import android.widget.PopupMenu
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_calendar)
        enableEdgeToEdge()
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
            showFloatingMenu()
        }

        // RecyclerView setup for listing activities
        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        activityAdapter = ActivityAdapter(emptyList(), this)
        binding.recyclerView.adapter = activityAdapter

        // Load activities from Firebase and update RecyclerView
        loadActivitiesFromFirebase()
    }

    private fun setupCalendar() {
        val calendarView = findViewById<CalendarView>(R.id.calendarView)
        calendarView.setOnDateChangeListener { _, year, month, dayOfMonth ->
            // 날짜가 선택되었을 때 처리할 코드를 여기에 작성
            val selectedDate = "$year-${month + 1}-$dayOfMonth"
            // 선택된 날짜에 따라 데이터를 필터링하거나 로드
        }
    }

    private fun showFloatingMenu() {
        val view = layoutInflater.inflate(R.layout.fab_menu, null)
        val personalFab = view.findViewById<FloatingActionButton>(R.id.fab_add_personal)
        val groupFab = view.findViewById<FloatingActionButton>(R.id.fab_add_group)

        val popupWindow = android.widget.PopupWindow(view,
            android.widget.LinearLayout.LayoutParams.WRAP_CONTENT,
            android.widget.LinearLayout.LayoutParams.WRAP_CONTENT)
        popupWindow.isFocusable = true
        popupWindow.update()

        // 개인 플로깅 버튼 클릭 시
        personalFab.setOnClickListener {
            startActivity(Intent(this, AddPersonalActivity::class.java))
            popupWindow.dismiss()
        }

        // 그룹 플로깅 버튼 클릭 시
        groupFab.setOnClickListener {
            startActivity(Intent(this, AddGroupActivity::class.java))
            popupWindow.dismiss()
        }

        // 팝업 창 표시 위치
        popupWindow.showAsDropDown(binding.fab, -60, -200)
    }

    private fun loadActivitiesFromFirebase() {
        firebaseManager.getAllActivities { activities ->
            activityAdapter = ActivityAdapter(activities, this)
            binding.recyclerView.adapter = activityAdapter
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
        startActivity(intent)
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
                loadActivitiesFromFirebase()
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