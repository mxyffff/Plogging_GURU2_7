package com.example.plogging_guru2_7

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.plogging_guru2_7.databinding.ActivityCommunityBinding
import com.example.plogging_guru2_7.databinding.ActivityJoinnedGroupBinding
import com.example.plogging_guru2_7.databinding.ActivityMadeGroupBinding

class MadeGroupActivity : AppCompatActivity() {

    private lateinit var groupAdapter: GroupAdapter
    private var groupList: ArrayList<FirebaseManager.Group> = ArrayList()
    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_made_group)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // 뷰 바인딩
        val binding = ActivityMadeGroupBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 'back' 버튼 클릭시
        binding.back.setOnClickListener {
            finish() // 현재 액티비티 종료, 마이페이지 화면으로 돌아가기
        }

        // RecyclerView 설정
        binding.rvGroup.layoutManager = LinearLayoutManager(this)
        groupAdapter = GroupAdapter(this, groupList)
        binding.rvGroup.adapter = groupAdapter

        // SharedPreferences 초기화
        sharedPreferences = getSharedPreferences("login_prefs", Context.MODE_PRIVATE)

        // 현재 로그인한 사용자의 사용자명 가져오기
        val currentUsername = sharedPreferences.getString("username", null)

        if (currentUsername != null) {
            // Firebase에서 사용자가 생성한 그룹 데이터 가져오기
            FirebaseManager().getCreatedGroups(currentUsername) { groups ->
                groupList.clear()
                groupList.addAll(groups)
                Log.d("MadeGroupActivity", "Group list size: ${groupList.size}") // 데이터 확인
                groupAdapter.notifyDataSetChanged()
            }
        } else {
            Log.e("MadeGroupActivity", "Current username is null")
        }
    }
}