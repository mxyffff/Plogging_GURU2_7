package com.example.plogging_guru2_7

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.plogging_guru2_7.databinding.ActivityMyGroupPageBinding
import com.example.plogging_guru2_7.databinding.ActivityMyPageBinding

class MyGroupPageActivity : AppCompatActivity() {

    private lateinit var firebaseManager: FirebaseManager
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var editor: SharedPreferences.Editor
    private lateinit var currentUsername: String
    private lateinit var currentNickname: String
    private lateinit var binding: ActivityMyGroupPageBinding
    private lateinit var groupId: String
    private lateinit var commentAdapter: CommentAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_my_group_page)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // 뷰 바인딩 선언
        binding = ActivityMyGroupPageBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // FirebaseManager 및 SharedPreferences 초기화
        firebaseManager = FirebaseManager()
        sharedPreferences = getSharedPreferences("login_prefs", Context.MODE_PRIVATE)
        editor = sharedPreferences.edit()

        // 현재 로그인된 사용자 이름 가져오기
        currentUsername = sharedPreferences.getString("username", "") ?: ""
        firebaseManager.getUserByUsername(currentUsername) { user ->
            currentNickname = user?.nickname ?: currentUsername
        }

        // 그룹 ID 가져오기
        groupId = intent.getStringExtra("groupId") ?: ""

        // 그룹 데이터 가져오기
        loadGroupData()

        // 'back' 버튼 클릭시
        binding.btnBack.setOnClickListener {
            finish() // 현재 액티비티 종료
        }

        // '그룹 삭제' 버튼 클릭시
        binding.btnDelete.setOnClickListener {
            deleteGroup()
        }

        // 댓글 RecyclerView 설정
        commentAdapter = CommentAdapter(currentUsername) { commentId, userId ->
            deleteComment(commentId, userId)
        }
        binding.rvComments.layoutManager = LinearLayoutManager(this)
        binding.rvComments.adapter = commentAdapter
        loadComments()

        // '댓글 전송' 버튼 클릭시
        binding.btnSend.setOnClickListener {
            val commentText = binding.edtComment.text.toString()
            if (commentText.isNotEmpty()) {
                val comment = FirebaseManager.Comment(
                    userId = currentUsername,
                    nickname = currentNickname,
                    text = commentText
                )
                firebaseManager.addComment(groupId, comment) { success ->
                    if (success) {
                        Toast.makeText(this, "댓글이 추가되었습니다", Toast.LENGTH_SHORT).show()
                        binding.edtComment.text.clear()
                        loadComments()
                    } else {
                        Toast.makeText(this, "댓글 추가에 실패했습니다", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }

    private fun loadGroupData() {
        firebaseManager.getGroupById(groupId) { group ->
            group?.let {
                binding.groupName.text = it.groupName
                binding.emoji.text = it.emoji
                binding.participantsState.text = "${it.participants?.size ?: 0} / ${it.groupMembers} 참여중"
                binding.meetingTime.text = it.meetingTime
                binding.meetingPlace.text = it.groupPlace
                binding.detailPlace.text = it.detailPlace
            }
        }
    }

    private fun deleteGroup() {
        firebaseManager.deleteGroup(groupId, currentUsername) { success ->
            if (success) {
                Toast.makeText(this, "모임이 삭제되었습니다", Toast.LENGTH_SHORT).show()
                finish() // 삭제 후 액티비티 종료
            } else {
                Toast.makeText(this, "모임 삭제를 실패했습니다", Toast.LENGTH_SHORT).show()
                Log.e("MyGroupPageActivity", "Failed to delete group")
            }
        }
    }

    private fun loadComments() {
        firebaseManager.getCommentsByGroupId(groupId) { comments ->
            commentAdapter.submitList(comments)
        }
    }

    private fun deleteComment(commentId: String, userId: String) {
        if (userId == currentUsername) {
            firebaseManager.deleteComment(groupId, commentId) { success ->
                if (success) {
                    Toast.makeText(this, "댓글이 삭제되었습니다", Toast.LENGTH_SHORT).show()
                    loadComments()
                } else {
                    Toast.makeText(this, "댓글 삭제에 실패했습니다", Toast.LENGTH_SHORT).show()
                }
            }
        } else {
            Toast.makeText(this, "댓글을 삭제할 권한이 없습니다", Toast.LENGTH_SHORT).show()
        }
    }
}