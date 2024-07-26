package com.example.plogging_guru2_7

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import java.sql.SQLException

class DBManager(
    context: Context?,
    name: String?,
    factory: SQLiteDatabase.CursorFactory?,
    version: Int
) : SQLiteOpenHelper(context, name, factory, version) {

    companion object {
        private const val DATABASE_VERSION = 1
        private const val DATABASE_NAME = "userDB.db"
        const val TABLE_USERS = "users"
        const val COLUMN_USERNAME = "username" // ID 역할을 하는 username
        const val COLUMN_PASSWORD = "password"
        const val COLUMN_EMAIL = "email"
        const val COLUMN_NICKNAME = "nickname"
    }

    // DB 테이블 생성
    override fun onCreate(db: SQLiteDatabase?) {
        val createTable = ("CREATE TABLE $TABLE_USERS ("
                + "$COLUMN_USERNAME TEXT PRIMARY KEY, " // username을 PRIMARY KEY로 설정
                + "$COLUMN_PASSWORD TEXT, "
                + "$COLUMN_EMAIL TEXT, "
                + "$COLUMN_NICKNAME TEXT)")
        db!!.execSQL(createTable)
        }

    // DB 업그레이드
    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db!!.execSQL("DROP TABLE IF EXISTS $TABLE_USERS")
        onCreate(db)
    }

    // 새로운 사용자 추가
    fun addUser (username: String, password: String, email: String, nickname: String): Boolean {
        val db = this.writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_USERNAME, username)
            put(COLUMN_PASSWORD, password) // 평문 패스워드 저장
            put(COLUMN_EMAIL, email)
            put(COLUMN_NICKNAME, nickname)
        }
        return try {
            val success = db.insert(TABLE_USERS, null, values)
            db.close()
            Integer.parseInt("$success") != -1
        } catch (e: SQLException) {
            db.close()
            false
        }
    }

    // username과 password로 사용자 인증
    fun isUserValid(username: String, password: String): Boolean {
        val db = this.readableDatabase
        val cursor = db.rawQuery(
            "SELECT * FROM $TABLE_USERS WHERE $COLUMN_USERNAME = ? AND $COLUMN_PASSWORD = ?",
            arrayOf(username, password)
        )
        val result = cursor.moveToFirst()
        cursor.close()
        db.close()
        return result
    }

    // 이메일과 username으로 비밀번호 검색
    fun getPasswordByEmailAndUsername(email: String, username: String): String? {
        val db = this.readableDatabase
        val cursor = db.rawQuery(
            "SELECT $COLUMN_PASSWORD FROM $TABLE_USERS WHERE $COLUMN_EMAIL = ? AND $COLUMN_USERNAME = ?",
            arrayOf(email, username)
        )
        val password = if (cursor.moveToFirst()) {
            cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PASSWORD))
        } else {
            null
        }
        cursor.close()
        db.close()
        return password
    }

    // username으로 사용자 검색
    fun getUserByUsername(username: String): Cursor? {
        val db = this.readableDatabase
        return try {
            db.query(TABLE_USERS, null, "$COLUMN_USERNAME=?", arrayOf(username), null, null, null)
        } catch (e: SQLException) {
            db.close()
            null
        }
    }

    // nickname 검색
    fun getNicknameByNickname(nicknmae: String): Cursor? {
        val db = this.readableDatabase
        return try {
            db.query(TABLE_USERS, null, "$COLUMN_NICKNAME=?", arrayOf(nicknmae), null, null, null)
        } catch (e: SQLException) {
            db.close()
            null
        }
    }
}