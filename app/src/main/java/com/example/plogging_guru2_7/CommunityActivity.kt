package com.example.plogging_guru2_7

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.plogging_guru2_7.databinding.ActivityCommunityBinding
import com.example.plogging_guru2_7.databinding.ActivityNaverMapBinding

class CommunityActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCommunityBinding
    private lateinit var groupAdapter: GroupAdapter
    private var groupList: ArrayList<FirebaseManager.Group> = ArrayList()
    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_community)

        // 바인딩 및 초기화
        binding = ActivityCommunityBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //버튼 초기화
        val myPageIcon: Button = findViewById(R.id.myPageIcon)
        val calendarIcon: Button = findViewById(R.id.calendarIcon)
        val communityIcon: Button = findViewById(R.id.communityIcon)
        val mapIcon: Button = findViewById(R.id.mapIcon)

        // SharedPreferences 초기화
        sharedPreferences = getSharedPreferences("login_prefs", Context.MODE_PRIVATE)

        // 현재 로그인한 사용자의 사용자명 가져오기
        val currentUsername = sharedPreferences.getString("username", null)

        //RecyclerView 초기화
        val rv : RecyclerView = findViewById(R.id.rvContent)

        //아이템 목록 생성
        val items = ArrayList<ContentModel>()
        items.add(ContentModel("서울숲 모닝 플로깅", "https://cdn.imweb.me/thumbnail/20240705/c0b61f75ea91d.png", "https://www.kindlyy.kr/category-hometown-2"))
        items.add(ContentModel("제주 해양 정화 플로깅", "https://cdn.imweb.me/thumbnail/20240425/07656317dfe36.png", "https://www.kindlyy.kr/category-nature-2"))
        items.add(ContentModel("우리동네 산책미화, 워컵픽업", "https://cdn.imweb.me/thumbnail/20240417/232653782d2d2.png", "https://www.kindlyy.kr/category-hometown-1"))
        items.add(ContentModel("CQR 남산 클린하이킹", "https://play-lh.googleusercontent.com/FjlomxHLzDsYqGC29h_Al9WzxPJQWRScQEgtI_Pym4RQZsujhfU-9z5kDGhaU5K0Lz5Z", "https://cqr.imweb.me/community/?q=YToxOntzOjEyOiJrZXl3b3JkX3R5cGUiO3M6MzoiYWxsIjt9&bmode=view&idx=29953911&t=board&category=01ZQ280254"))
        items.add(ContentModel("CQR 조찬회: 플로깅", "https://play-lh.googleusercontent.com/FjlomxHLzDsYqGC29h_Al9WzxPJQWRScQEgtI_Pym4RQZsujhfU-9z5kDGhaU5K0Lz5Z", "https://cqr.imweb.me/community/?q=YToxOntzOjEyOiJrZXl3b3JkX3R5cGUiO3M6MzoiYWxsIjt9&bmode=view&idx=30802201&t=board&category=01ZQ280254"))
        items.add(ContentModel("클린하이커스", "https://cleanhikers.com/_next/static/media/banner.c45d7063.png", "https://cleanhikers.com/"))

        //RecyclerView 어댑터 설정
        val rvAdapter = RVAdapter(baseContext, items)
        rv.adapter = rvAdapter

        // RecyclerView 설정
        binding.rvGroup.layoutManager = LinearLayoutManager(this)
        groupAdapter = GroupAdapter(this, groupList, currentUsername ?: "")
        binding.rvGroup.adapter = groupAdapter

        // Firebase에서 모든 그룹 데이터 가져오기
        FirebaseManager().getAllGroups { groups ->
            groupList.clear()
            groupList.addAll(groups)
            Log.d("CommunityActivity", "Group list size: ${groupList.size}") // 데이터 잘 가져오는지 확인
            groupAdapter.notifyDataSetChanged()
        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // addFab 버튼 클릭 리스너
        binding.addFab.setOnClickListener {
            val intent = Intent(this, MakingGroupActivity::class.java)
            startActivity(intent)
            finish()
        }

        // 데이터 초기 로드
        loadGroupData()

        //각 버튼에 클릭 리스너 설정 (각 페이지로 이동)
        mapIcon.setOnClickListener {
            val intent = Intent(this, PloggingSpotActivity::class.java)
            startActivity(intent)
        }

        calendarIcon.setOnClickListener {
            val intent = Intent(this, CalendarActivity::class.java)
            startActivity(intent)
        }

        communityIcon.setOnClickListener {
            //현재 페이지 유지
        }

        myPageIcon.setOnClickListener {
            val intent = Intent(this, MyPageActivity::class.java)
            startActivity(intent)
        }
    }

    override fun onResume() {
        super.onResume()
        // 액티비티가 다시 활성화될 때 데이터 갱신
        loadGroupData()
    }

    private fun loadGroupData() {
        FirebaseManager().getAllGroups { groups ->
            groupList.clear()
            groupList.addAll(groups)
            groupAdapter.notifyDataSetChanged()
        }
    }
}