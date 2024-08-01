package com.example.plogging_guru2_7

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView

class CommentAdapter(
    private val currentUserId: String,
    private val onDeleteClick: (String, String) -> Unit
) : ListAdapter<FirebaseManager.Comment, CommentAdapter.CommentViewHolder>(CommentDiffCallback()) {

    // ViewHolder 클래스
    class CommentViewHolder(
        itemView: View,
        private val currentUserId: String,
        private val onDeleteClick: (String, String) -> Unit
    ) : RecyclerView.ViewHolder(itemView) {

        private val textViewComment: TextView = itemView.findViewById(R.id.tvComment)
        private val textViewNickname: TextView = itemView.findViewById(R.id.tvNickname)
        private val btnDeleteComment: TextView = itemView.findViewById(R.id.btnDelComment)

        fun bind(comment: FirebaseManager.Comment) {
            textViewComment.text = comment.text
            textViewNickname.text = comment.nickname

            // 댓글 작성자와 현재 사용자가 동일한 경우에만 삭제 버튼을 보이도록 설정
            btnDeleteComment.visibility =
                if (comment.userId == currentUserId) View.VISIBLE else View.GONE

            btnDeleteComment.setOnClickListener {
                onDeleteClick(comment.id, comment.userId)
            }
        }
    }

    class CommentDiffCallback : DiffUtil.ItemCallback<FirebaseManager.Comment>() {
        override fun areItemsTheSame(
            oldItem: FirebaseManager.Comment,
            newItem: FirebaseManager.Comment
        ): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(
            oldItem: FirebaseManager.Comment,
            newItem: FirebaseManager.Comment
        ): Boolean {
            return oldItem == newItem
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CommentViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.comments_item, parent, false)
        return CommentViewHolder(view, currentUserId, onDeleteClick)
    }

    override fun onBindViewHolder(holder: CommentViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
}