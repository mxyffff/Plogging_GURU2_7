package com.example.plogging_guru2_7

import android.os.Parcel
import android.os.Parcelable
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
    private val commentsRef: DatabaseReference = database.getReference("comments")
    private val markersRef: DatabaseReference = database.getReference("markers")
    private val grecordRef: DatabaseReference = database.getReference("grecord")
    private val precordRef: DatabaseReference = database.getReference("precord")


    // 회원 정보
    data class User(
        val username: String = "",
        val password: String = "",
        val email: String = "",
        val nickname: String = ""
    )

    // 모임 정보
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

    // 댓글 정보
    data class Comment(
        val id: String = "",
        val userId: String = "",
        val nickname: String = "",
        val text: String = "",
        val timestamp: Long = System.currentTimeMillis()
    )

    // 마커 정보
    data class Marker(
        val id: String = "",
        val userId: String = "",
        val nickname: String = "",
        val latitude: Double = 0.0,
        val longitude: Double = 0.0,
        val address: String = ""
    )

    // 모임 기록 정보
    data class grecord(
        val id: String = "",
        val groupName: String = "",
        val date: Int = 0,
        val meetingTime: Int = 0,
        val groupPlace: String = "",
        val supplies: String = "",
        val feedback: String = ""
    ) : Parcelable {
        constructor(parcel: Parcel) : this(
            parcel.readString() ?: "",
            parcel.readString() ?: "",
            parcel.readInt(),
            parcel.readInt(),
            parcel.readString() ?: "",
            parcel.readString() ?: "",
            parcel.readString() ?: ""
        )

        override fun writeToParcel(parcel: Parcel, flags: Int) {
            parcel.writeString(id)
            parcel.writeString(groupName)
            parcel.writeInt(date)
            parcel.writeInt(meetingTime)
            parcel.writeString(groupPlace)
            parcel.writeString(supplies)
            parcel.writeString(feedback)
        }

        override fun describeContents(): Int = 0

        companion object CREATOR : Parcelable.Creator<grecord> {
            override fun createFromParcel(parcel: Parcel): grecord = grecord(parcel)
            override fun newArray(size: Int): Array<grecord?> = arrayOfNulls(size)
        }
    }

    // 개인 기록 정보
    data class precord(
        val id: String = "",
        val personalName: String = "",
        val date: Int = 0,
        val personalPlace: String = "",
        val photo: String = "",
        val memo: String = ""
    ) : Parcelable {
        constructor(parcel: Parcel) : this(
            parcel.readString() ?: "",
            parcel.readString() ?: "",
            parcel.readInt(),
            parcel.readString() ?: "",
            parcel.readString() ?: "",
            parcel.readString() ?: ""
        )

        override fun writeToParcel(parcel: Parcel, flags: Int) {
            parcel.writeString(id)
            parcel.writeString(personalName)
            parcel.writeInt(date)
            parcel.writeString(personalPlace)
            parcel.writeString(photo)
            parcel.writeString(memo)
        }
        override fun describeContents(): Int = 0

        companion object CREATOR : Parcelable.Creator<precord> {
            override fun createFromParcel(parcel: Parcel): precord = precord(parcel)
            override fun newArray(size: Int): Array<precord?> = arrayOfNulls(size)
        }
    }


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

    // 댓글 추가
    fun addComment(groupId: String, comment: Comment, callback: (Boolean) -> Unit) {
        val newCommentRef = commentsRef.child(groupId).push()
        val commentWithId = comment.copy(id = newCommentRef.key ?: "")
        newCommentRef.setValue(commentWithId)
            .addOnSuccessListener {
                callback(true)
            }
            .addOnFailureListener {
                callback(false)
            }
    }

    // 마커 추가
    fun addMarker(marker: Marker, callback: (Boolean) -> Unit) {
        val newMarkerRef = markersRef.push()
        val markerWithId = marker.copy(id = newMarkerRef.key ?: "")
        newMarkerRef.setValue(markerWithId)
            .addOnSuccessListener {
                callback(true)
            }
            .addOnFailureListener {
                callback(false)
            }
    }

    // 모임 기록 추가
    fun addGrecord(grecord: grecord, callback: (Boolean) -> Unit) {
        val newGrecordRef = grecordRef.push()
        val grecordId = newGrecordRef.key ?: ""
        val grecordWithId = grecord.copy(id = grecordId)
        newGrecordRef.setValue(grecordWithId)
            .addOnSuccessListener {
                Log.d("FirebaseManager", "Grecord added: $grecordWithId")
                callback(true)
            }
            .addOnFailureListener {
                Log.e("FirebaseManager", "Failed to add grecord", it)
                callback(false)
            }
    }

    // 개인 기록 추가
    fun addPrecord(precord: precord, callback: (Boolean) -> Unit) {
        val newPrecordRef = precordRef.push()
        val precordId = newPrecordRef.key ?: ""
        val precordWithId = precord.copy(id = precordId)
        newPrecordRef.setValue(precordWithId)
            .addOnSuccessListener {
                Log.d("FirebaseManager", "Precord added: $precordWithId")
                callback(true)
            }
            .addOnFailureListener {
                Log.e("FirebaseManager", "Failed to add precord", it)
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
    fun getPasswordByEmailAndUsername(
        email: String,
        username: String,
        callback: (String?) -> Unit
    ) {
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
        usersRef.child(username).child("password").setValue(newPassword)
            .addOnCompleteListener { task ->
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
                if (group != null && participants.size < group.groupMembers && !participants.contains(
                        username
                    )
                ) {
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

    // 그룹의 모든 댓글 가져오기
    fun getCommentsByGroupId(groupId: String, callback: (List<Comment>) -> Unit) {
        commentsRef.child(groupId).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val comments = mutableListOf<Comment>()
                for (childSnapshot in snapshot.children) {
                    val comment = childSnapshot.getValue(Comment::class.java)
                    if (comment != null) {
                        comments.add(comment)
                    }
                }
                callback(comments)
            }

            override fun onCancelled(error: DatabaseError) {
                callback(emptyList())
            }
        })
    }

    // 댓글 삭제
    fun deleteComment(groupId: String, commentId: String, callback: (Boolean) -> Unit) {
        commentsRef.child(groupId).child(commentId).removeValue()
            .addOnCompleteListener { task ->
                callback(task.isSuccessful)
            }
    }

    // 모든 마커 정보 조회
    fun getAllMarkers(callback: (List<Marker>) -> Unit) {
        markersRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val markers = mutableListOf<Marker>()
                for (childSnapshot in snapshot.children) {
                    val marker = childSnapshot.getValue(Marker::class.java)
                    if (marker != null) {
                        markers.add(marker)
                    }
                }
                callback(markers)
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("FirebaseManager", "Failed to read markers", error.toException())
                callback(emptyList())
            }
        })
    }

    // 모든 기록 정보 조회
    fun getAllActivities(callback: (List<Any>) -> Unit) {
        val activitiesRef = database.getReference("activities")
        activitiesRef.get().addOnSuccessListener { dataSnapshot ->
            val activities = mutableListOf<Any>()
            dataSnapshot.children.forEach { childSnapshot ->
                val grecord = childSnapshot.getValue(grecord::class.java)
                val precord = childSnapshot.getValue(precord::class.java)
                grecord?.let { activities.add(it) }
                precord?.let { activities.add(it) }
            }
            callback(activities)
        }.addOnFailureListener {
            callback(emptyList())
        }
    }


    // 특정 날짜의 기록 정보 조회
    fun getActivitiesByDate(date: String, callback: (List<Any>) -> Unit) {
        val activities = mutableListOf<Any>()
        val dateInt = date.toIntOrNull() ?: return callback(emptyList()) // date를 Int로 변환

        precordRef.orderByChild("date").equalTo(dateInt.toDouble())
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    for (childSnapshot in snapshot.children) {
                        val activity = childSnapshot.getValue(precord::class.java)
                        activity?.let { activities.add(it) }
                    }
                    grecordRef.orderByChild("date").equalTo(dateInt.toDouble())
                        .addListenerForSingleValueEvent(object : ValueEventListener {
                            override fun onDataChange(snapshot: DataSnapshot) {
                                for (childSnapshot in snapshot.children) {
                                    val activity = childSnapshot.getValue(grecord::class.java)
                                    activity?.let { activities.add(it) }
                                }
                                callback(activities)
                            }

                            override fun onCancelled(error: DatabaseError) {
                                Log.e("FirebaseManager", "Failed to read activities", error.toException())
                                callback(emptyList())
                            }
                        })
                }

                override fun onCancelled(error: DatabaseError) {
                    callback(emptyList())
                }
            })
    }

    // 기록 정보 삭제
    fun deleteActivity(id: String, callback: (Boolean) -> Unit) {
        val activityRef = database.getReference("activities").child(id)
        activityRef.removeValue().addOnCompleteListener { task ->
            callback(task.isSuccessful)
        }
    }

}