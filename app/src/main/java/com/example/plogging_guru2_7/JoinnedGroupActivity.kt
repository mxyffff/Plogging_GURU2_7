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
import com.example.plogging_guru2_7.databinding.ActivityChangePwBinding
import com.example.plogging_guru2_7.databinding.ActivityCommunityBinding
import com.example.plogging_guru2_7.databinding.ActivityJoinnedGroupBinding

class JoinnedGroupActivity : AppCompatActivity() {

    private lateinit var groupAdapter: GroupAdapter
    private var groupList: ArrayList<FirebaseManager.Group> = ArrayList()
    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_joinned_group)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // 뷰 바인딩
        val binding = ActivityJoinnedGroupBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // SharedPreferences 초기화
        sharedPreferences = getSharedPreferences("login_prefs", Context.MODE_PRIVATE)

        // 현재 로그인한 사용자의 사용자명 가져오기
        val currentUsername = sharedPreferences.getString("username", null)

        // 'back' 버튼 클릭시
        binding.back.setOnClickListener {
            finish() // 현재 액티비티 종료, 마이페이지 화면으로 돌아가기
        }

        // RecyclerView 설정
        binding.rvGroup.layoutManager = LinearLayoutManager(this)
        groupAdapter = GroupAdapter(this, groupList, currentUsername ?: "")
        binding.rvGroup.adapter = groupAdapter

        if (currentUsername != null) {
            // Firebase에서 사용자가 가입한 그룹 데이터 가져오기
            FirebaseManager().getJoinedGroups(currentUsername) { groups ->
                groupList.clear()
                groupList.addAll(groups)
                Log.d("JoinnedGroupActivity", "Group list size: ${groupList.size}") // 데이터 확인
                groupAdapter.notifyDataSetChanged()
            }
        } else {
            Log.e("JoinnedGroupActivity", "Current username is null")
        }
    }
}