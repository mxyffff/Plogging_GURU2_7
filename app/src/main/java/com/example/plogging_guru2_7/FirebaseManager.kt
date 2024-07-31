package com.example.plogging_guru2_7

import android.util.Log
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.GenericTypeIndicator
import com.google.firebase.database.ValueEventListener
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
        val detailPlace: String? = null,
        val participants: List<String>? = null  // 참여 사용자들의 username 리스트
    )

    // 사용자 추가
    fun addUser(user: User, callback: (Boolean) -> Unit) {
        usersRef.child(user.username).setValue(user).addOnCompleteListener { task ->
            callback(task.isSuccessful)
        }
    }

    // 그룹 추가
    fun addGroup(group: Group, callback: (Boolean) -> Unit) {
        val newGroupRef = groupsRef.push()
        val groupId = newGroupRef.key ?: ""
        val participants = mutableListOf(group.userId) // 그룹 생성자를 participants 리스트에 추가
        val groupWithId = group.copy(id = groupId, participants = participants)
        newGroupRef.setValue(groupWithId)
            .addOnSuccessListener {
                Log.d("FirebaseManager", "Group added with participants: $groupWithId")
                callback(true)
            }
            .addOnFailureListener {
                Log.e("FirebaseManager", "Failed to add group", it)
                callback(false)
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

    // groupId로 그룹 정보 가져오는 함수
    fun getGroupById(groupId: String, callback: (Group?) -> Unit) {
        groupsRef.child(groupId).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val group = snapshot.getValue(Group::class.java)
                callback(group)
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("FirebaseManager", "Failed to read group", error.toException())
                callback(null)
            }
        })
    }

    // 모든 그룹 정보 조회 함수
    fun getAllGroups(callback: (List<Group>) -> Unit) {
        groupsRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val groups = mutableListOf<Group>()
                for (childSnapshot in snapshot.children) {
                    val group = childSnapshot.getValue(Group::class.java)
                    if (group != null) {
                        groups.add(group)
                    }
                }
                callback(groups)
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("FirebaseManager", "Failed to read groups", error.toException())
                callback(emptyList())
            }
        })
    }

    // 사용자가 가입한 그룹 정보 조회 함수
    fun getJoinedGroups(username: String, callback: (List<Group>) -> Unit) {
        groupsRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val groups = mutableListOf<Group>()
                for (childSnapshot in snapshot.children) {
                    val group = childSnapshot.getValue(Group::class.java)
                    if (group != null && group.participants?.contains(username) == true) {
                        groups.add(group)
                    }
                }
                callback(groups)
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("FirebaseManager", "Failed to read groups", error.toException())
                callback(emptyList())
            }
        })
    }

    // 사용자가 생성한 그룹 정보 조회 함수
    fun getCreatedGroups(username: String, callback: (List<Group>) -> Unit) {
        groupsRef.orderByChild("userId").equalTo(username)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val groups = mutableListOf<Group>()
                    snapshot.children.forEach { childSnapshot ->
                        val group = childSnapshot.getValue(Group::class.java)
                        if (group != null) {
                            groups.add(group)
                        }
                    }
                    callback(groups)
                }

                override fun onCancelled(error: DatabaseError) {
                    callback(emptyList())
                }
            })
    }

    // 그룹에 사용자 추가
    fun addParticipantToGroup(groupId: String, username: String, callback: (Boolean) -> Unit) {
        val groupRef = groupsRef.child(groupId)
        groupRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val group = snapshot.getValue(Group::class.java)
                val participants = group?.participants?.toMutableList() ?: mutableListOf()
                // 그룹의 인원수가 꽉 차 있는지 검사
                if (group != null && participants.size < group.groupMembers && !participants.contains(username)) {
                    participants.add(username)
                    groupRef.child("participants").setValue(participants)
                        .addOnSuccessListener {
                            callback(true)
                        }
                        .addOnFailureListener {
                            callback(false)
                        }
                } else {
                    callback(false)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                callback(false)
            }
        })
    }

    // 그룹에서 사용자 제거
    fun removeParticipantFromGroup(groupId: String, username: String, callback: (Boolean) -> Unit) {
        val groupRef = groupsRef.child(groupId)
        groupRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val group = snapshot.getValue(Group::class.java)
                val participants = group?.participants?.toMutableList() ?: mutableListOf()
                if (group != null && participants.contains(username)) {
                    participants.remove(username)
                    groupRef.child("participants").setValue(participants)
                        .addOnSuccessListener {
                            callback(true)
                        }
                        .addOnFailureListener {
                            callback(false)
                        }
                } else {
                    callback(false)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                callback(false)
            }
        })
    }

    // 그룹 삭제
    fun deleteGroup(groupId: String, currentUsername: String, callback: (Boolean) -> Unit) {
        val groupRef = groupsRef.child(groupId)
        groupRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val group = snapshot.getValue(Group::class.java)
                if (group != null && group.userId == currentUsername) {
                    groupRef.removeValue()
                        .addOnSuccessListener {
                            callback(true)
                        }
                        .addOnFailureListener {
                            callback(false)
                        }
                } else {
                    callback(false)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                callback(false)
            }
        })
    }
}