package com.example.plogging_guru2_7

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide

//RecyclerView의 어댑터 클래스
class RVAdapter(val context : Context, val items : ArrayList<ContentModel>) : RecyclerView.Adapter<RVAdapter.Viewholder>() {

    //ViewHolder 생성
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RVAdapter.Viewholder {
        //아이템 레이아웃 가져옴
        val v = LayoutInflater.from(parent.context).inflate(R.layout.rv_item, parent, false)
        return Viewholder(v)
    }

    //ViewHolder와 데이터 바인딩
    override fun onBindViewHolder(holder: RVAdapter.Viewholder, position: Int) {
        holder.bindItems(items[position])
    }

    // 아이템 개수 반환
    override fun getItemCount(): Int {
        return items.size
    }

    // ViewHolder 클래스
    inner class Viewholder(itemView : View) : RecyclerView.ViewHolder(itemView) {

        //아이템 데이터 설정
        fun bindItems(item : ContentModel) {

            //아이템 클릭 시
            itemView.setOnClickListener {
                Toast.makeText(context, item.title, Toast.LENGTH_LONG).show() //토스트 메시지로 제목 표시
                val intent = Intent(context, WebViewActivity::class.java)
                intent.putExtra("url", item.webUrl) //웹 URL 추가
                itemView.context.startActivity(intent)
            }

            //RecyclerView 아이템의 텍스트뷰와 이미지뷰 찾기
            val contentTitle = itemView.findViewById<TextView>(R.id.textArea)
            val imageViewArea = itemView.findViewById<ImageView>(R.id.imageArea)

            //아이템 제목 설정
            contentTitle.text = item.title

            //Glide 사용해 이미지 로드
            Glide.with(context)
                .load(item.imageUrl)
                .into(imageViewArea)

        }
    }
}