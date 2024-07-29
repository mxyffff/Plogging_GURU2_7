package com.example.plogging_guru2_7

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.RecyclerView

class CommunityActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_community)

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

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }
}