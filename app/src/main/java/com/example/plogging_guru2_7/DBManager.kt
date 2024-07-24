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
        const val COLUMN_GENDER = "gender"
    }

    // DB 테이블 생성
    override fun onCreate(db: SQLiteDatabase?) {
        val createTable = ("CREATE TABLE $TABLE_USERS ("
                + "$COLUMN_USERNAME TEXT PRIMARY KEY, " // username을 PRIMARY KEY로 설정
                + "$COLUMN_PASSWORD TEXT, "
                + "$COLUMN_EMAIL TEXT, "
                + "$COLUMN_NICKNAME TEXT, "
                + "$COLUMN_GENDER TEXT)")
        db!!.execSQL(createTable)
        }

    // DB 업그레이드
    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db!!.execSQL("DROP TABLE IF EXISTS $TABLE_USERS")
        onCreate(db)
    }

    // 새로운 사용자 추가
    fun addUser (username: String, password: String, email: String, nickname: String, gender: String): Boolean {
        val db = this.writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_USERNAME, username)
            put(COLUMN_PASSWORD, password) // 평문 패스워드 저장
            put(COLUMN_EMAIL, email)
            put(COLUMN_NICKNAME, nickname)
            put(COLUMN_GENDER, gender)
        } return try {
            val success = db.insert(TABLE_USERS, null, values)
            db.close()
            Integer.parseInt("$success") != -1
        } catch (e: SQLException) {
            db.close()
            false
        }
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
}