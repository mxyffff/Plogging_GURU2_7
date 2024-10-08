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
import com.example.plogging_guru2_7.databinding.ActivityMyPageBinding
import com.example.plogging_guru2_7.databinding.ActivityOtherGroupPageBinding

class OtherGroupPageActivity : AppCompatActivity() {

    private lateinit var firebaseManager: FirebaseManager
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var editor: SharedPreferences.Editor
    private lateinit var currentUsername: String
    private lateinit var currentNickname: String
    private lateinit var binding: ActivityOtherGroupPageBinding
    private lateinit var groupId: String
    private var isParticipant: Boolean = false
    private lateinit var commentAdapter: CommentAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_other_group_page)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // 뷰 바인딩 선언
        binding = ActivityOtherGroupPageBinding.inflate(layoutInflater)
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

        // '가입하기/탈퇴하기' 버튼 클릭시
        binding.btnEnter.setOnClickListener {
            if (isParticipant) {
                leaveGroup()
            } else {
                joinGroup()
            }
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

                isParticipant = it.participants?.contains(currentUsername) == true
                updateButtonState(it.participants?.size ?: 0 >= it.groupMembers)
            }
        }
    }

    private fun updateButtonState(isFull: Boolean) {
        binding.btnEnter.text = when {
            isFull && !isParticipant -> "모집 완료"
            isParticipant -> "탈퇴하기"
            else -> "가입하기"
        }
        binding.btnEnter.isEnabled = !isFull || isParticipant
    }

    private fun joinGroup() {
        firebaseManager.addParticipantToGroup(groupId, currentUsername) { success ->
            if (success) {
                isParticipant = true
                loadGroupData() // 그룹 데이터 갱신
            } else {
                Log.e("OtherGroupPageActivity", "Failed to join group")
            }
        }
    }

    private fun leaveGroup() {
        firebaseManager.removeParticipantFromGroup(groupId, currentUsername) { success ->
            if (success) {
                isParticipant = false
                loadGroupData() // 그룹 데이터 갱신
            } else {
                Log.e("OtherGroupPageActivity", "Failed to leave group")
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