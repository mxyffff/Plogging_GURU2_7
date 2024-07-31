package com.example.plogging_guru2_7

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

//RecyclerView의 Group 어댑터 클래스
class GroupAdapter(val context: Context, val groups: ArrayList<FirebaseManager.Group>) : RecyclerView.Adapter<GroupAdapter.ViewHolder>() {

    // ViewHolder 클래스
    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bindItems(group: FirebaseManager.Group) {
            val groupName = itemView.findViewById<TextView>(R.id.groupName)
            val groupMembers = itemView.findViewById<TextView>(R.id.groupMembers)
            val groupPlace = itemView.findViewById<TextView>(R.id.groupPlace)
            val detailPlace = itemView.findViewById<TextView>(R.id.detailPlace)
            val groupEmoji = itemView.findViewById<TextView>(R.id.groupEmoji)

            groupName.text = group.groupName
            groupMembers.text = "정원: ${group.groupMembers}"
            groupPlace.text = group.groupPlace
            detailPlace.text = group.detailPlace
            groupEmoji.text = group.emoji

            //아이템 클릭 리스너
            itemView.setOnClickListener {
                // 클릭 시 상세 페이지로 이동 (아직 제작 중이라 임시)
                val intent = Intent(context, OtherGroupPageActivity::class.java)
                intent.putExtra("groupId", group.id)
                context.startActivity(intent)
            }
        }
    }

    //ViewHolder 생성
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.groups_item, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return groups.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bindItems(groups[position])
    }
}