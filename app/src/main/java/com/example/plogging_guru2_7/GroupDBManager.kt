package com.example.plogging_guru2_7

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class GroupDBManager(
    context: Context?,
    name: String?,
    factory: SQLiteDatabase.CursorFactory? = null,
    version: Int = 1
) : SQLiteOpenHelper(context, name, factory, version) {

    companion object {
        private const val DATABASE_VERSION = 1
        private const val DATABASE_NAME = "groupDB.db"
        const val TABLE_GROUPS = "groups"
        const val COLUMN_ID = "id"
        const val COLUMN_GROUP_NAME = "group_name"
        const val COLUMN_GROUP_MEMBERS = "group_members"
        const val COLUMN_MEETING_TIME = "meeting_time"
        const val COLUMN_GROUP_PLACE = "group_place"
        const val COLUMN_USERNAME = "username"  // Foreign key for username
    }

    override fun onCreate(db: SQLiteDatabase?) {
        val createTable = ("CREATE TABLE $TABLE_GROUPS ("
                + "$COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT, "
                + "$COLUMN_GROUP_NAME TEXT, "
                + "$COLUMN_GROUP_MEMBERS INTEGER, "
                + "$COLUMN_MEETING_TIME TEXT, "
                + "$COLUMN_GROUP_PLACE TEXT, "
                + "$COLUMN_USERNAME TEXT, "
                + "FOREIGN KEY ($COLUMN_USERNAME) REFERENCES ${DBManager.TABLE_USERS}(${DBManager.COLUMN_USERNAME}))")
        db!!.execSQL(createTable)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db!!.execSQL("DROP TABLE IF EXISTS $TABLE_GROUPS")
        onCreate(db)
    }

    // 그룹 추가 함수
    fun addGroup(groupName: String, groupMembers: Int, meetingTime: String, groupPlace: String, username: String): Boolean {
        val db = this.writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_GROUP_NAME, groupName)
            put(COLUMN_GROUP_MEMBERS, groupMembers)
            put(COLUMN_MEETING_TIME, meetingTime)
            put(COLUMN_GROUP_PLACE, groupPlace)
            put(COLUMN_USERNAME, username)
        }
        return try {
            val success = db.insert(TABLE_GROUPS, null, values)
            db.close()
            success != -1L
        } catch (e: Exception) {
            db.close()
            false
        }
    }

    // 그룹 정보 조회 함수
    fun getGroupInfo(): List<Group> {
        val db = this.readableDatabase
        val cursor: Cursor = db.rawQuery("SELECT * FROM $TABLE_GROUPS", null)
        val groups = mutableListOf<Group>()
        if (cursor.moveToFirst()) {
            do {
                val id = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID))
                val groupName = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_GROUP_NAME))
                val groupMembers = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_GROUP_MEMBERS))
                val meetingTime = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_MEETING_TIME))
                val groupPlace = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_GROUP_PLACE))
                val username = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_USERNAME))
                groups.add(Group(id, groupName, groupMembers, meetingTime, groupPlace, username))
            } while (cursor.moveToNext())
        }
        cursor.close()
        return groups
    }
}

// 그룹 정보 데이터를 위한 데이터 클래스
data class Group(
    val id: Int,
    val groupName: String,
    val groupMembers: Int,
    val meetingTime: String,
    val groupPlace: String,
    val username: String
)