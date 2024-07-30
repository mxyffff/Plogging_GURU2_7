package com.example.plogging_guru2_7

import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.getValue

class FirebaseManager {
    private val database: FirebaseDatabase = FirebaseDatabase.getInstance()
    private val usersRef: DatabaseReference = database.getReference("users")
    private val groupsRef: DatabaseReference = database.getReference("groups")

    data class User(
        val username: String = "",
        val password: String = "",
        val email: String = "",
        val nickname: String = ""
    )

    data class Group(
        var id: String = "",
        val groupName: String = "",
        val groupMembers: Int = 0,
        val meetingTime: String = "",
        val groupPlace: String = "",
        val userId: String = "",
        val emoji: String? = null,
        val detailPlace: String? = null
    )

    // 사용자 추가
    fun addUser(user: User, callback: (Boolean) -> Unit) {
        usersRef.child(user.username).setValue(user).addOnCompleteListener { task ->
            callback(task.isSuccessful)
        }
    }

    // 그룹 추가
    fun addGroup(group: Group, callback: (Boolean) -> Unit) {
        val key = groupsRef.push().key ?: return callback(false)
        group.id = key
        groupsRef.child(key).setValue(group).addOnCompleteListener { task ->
            callback(task.isSuccessful)
        }
    }

    // 사용자 인증
    fun isUserValid(username: String, password: String, callback: (Boolean) -> Unit) {
        usersRef.child(username).get().addOnSuccessListener { dataSnapshot ->
            val user = dataSnapshot.getValue<User>()
            callback(user?.password == password)
        }.addOnFailureListener {
            callback(false)
        }
    }

    // 사용자 비밀번호 찾기
    fun getPasswordByEmailAndUsername(email: String, username: String, callback: (String?) -> Unit) {
        usersRef.child(username).get().addOnSuccessListener { dataSnapshot ->
            val user = dataSnapshot.getValue<User>()
            callback(if (user?.email == email) user.password else null)
        }.addOnFailureListener {
            callback(null)
        }
    }

    // 사용자 정보 가져오기
    fun getUserByUsername(username: String, callback: (User?) -> Unit) {
        usersRef.child(username).get().addOnSuccessListener { dataSnapshot ->
            val user = dataSnapshot.getValue<User>()
            callback(user)
        }.addOnFailureListener {
            callback(null)
        }
    }

    // 닉네임으로 사용자 검색
    fun getUserByNickname(nickname: String, callback: (User?) -> Unit) {
        val query = usersRef.orderByChild("nickname").equalTo(nickname)
        query.get().addOnSuccessListener { dataSnapshot ->
            val user = dataSnapshot.children.firstOrNull()?.getValue<User>()
            callback(user)
        }.addOnFailureListener {
            callback(null)
        }
    }

    // 사용자 비밀번호 업데이트
    fun updateUserPassword(username: String, newPassword: String, callback: (Boolean) -> Unit) {
        usersRef.child(username).child("password").setValue(newPassword).addOnCompleteListener { task ->
            callback(task.isSuccessful)
        }
    }

    // 사용자 이메일 업데이트
    fun updateUserEmail(username: String, newEmail: String, callback: (Boolean) -> Unit) {
        usersRef.child(username).child("email").setValue(newEmail).addOnCompleteListener { task ->
            callback(task.isSuccessful)
        }
    }

    // 사용자 삭제
    fun deleteUser(username: String, callback: (Boolean) -> Unit) {
        usersRef.child(username).removeValue().addOnCompleteListener { task ->
            callback(task.isSuccessful)
        }
    }

    // 특정 사용자가 생성한 그룹 정보 조회 함수
    fun getUserGroups(username: String, callback: (List<Group>) -> Unit) {
        val query = groupsRef.orderByChild("userId").equalTo(username)
        query.get().addOnSuccessListener { dataSnapshot ->
            val groups = dataSnapshot.children.mapNotNull { it.getValue<Group>() }
            callback(groups)
        }.addOnFailureListener {
            callback(emptyList())
        }
    }

}