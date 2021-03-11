package com.example.dad.helpers

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log
import com.example.dad.bean.RecordMovie
import java.util.*

/**
 * sqlite工具类
 */
class DbHelper(context: Context) : SQLiteOpenHelper(context, "recordmovie.db", null, 4) {
    val TAG = "DbHelper"
    val TABLE = "recordmovie"

    companion object {
        val ID: String = "id"
        val POSTION: String = "postion"
        val DURATION: String = "duration"
        val TIMESTAMP: String = "TIMESTAMP"
    }

    val DATABASE_CREATE =
        "CREATE TABLE if not exists " + TABLE + " (" +
                "${ID} integer PRIMARY KEY," +
                "${POSTION} integer," +
                "${TIMESTAMP} integer," +
                "${DURATION} text" +
                ")"

    fun insertRecord(rm: RecordMovie) {
        val values = ContentValues();
        values.put(ID, rm.id);
        values.put(POSTION, rm.postion);
        values.put(DURATION, rm.duration);
        values.put(TIMESTAMP, Date().time);
        writableDatabase.insert(TABLE, null, values);
    }

    fun getData(): Cursor {
        return readableDatabase
            .query(TABLE, arrayOf(ID, POSTION, DURATION), null, null, null, null, ID + " DESC");
    }

    fun getItemById(id: Int): RecordMovie {
        var args = arrayOf(id.toString());
        var raw = readableDatabase.rawQuery("SELECT * FROM " + TABLE + " WHERE id = ?", args);
        val recordMovie = RecordMovie();
        if (raw.moveToFirst()) {
            recordMovie.id = raw.getInt(raw.getColumnIndex("id"));
            recordMovie.postion = raw.getInt(raw.getColumnIndex("postion"));
            recordMovie.duration = raw.getString(raw.getColumnIndex("duration")).toLong();
        }

        raw.close();
        return recordMovie;
    }

    fun updateItemById(rm: RecordMovie) {
        val values = ContentValues();
        values.put(POSTION, rm.postion);
        values.put(DURATION, rm.duration);
        values.put(TIMESTAMP, Date().time);
        readableDatabase.update(TABLE, values, "id = ?", arrayOf(rm.id.toString()));
    }

    fun deleteItemById(id: Int) {
        readableDatabase.delete(TABLE, "id = ?", arrayOf(id.toString()));
    }

    override fun onCreate(db: SQLiteDatabase) {
        Log.d(TAG, "Creating: " + DATABASE_CREATE);
        db.execSQL(DATABASE_CREATE)
    }

    override fun onUpgrade(p0: SQLiteDatabase, p1: Int, p2: Int) {
    }

    //检查本地电影记录的存储
    fun dbMovieCountCheck() {
        val cursor = readableDatabase
            .query(TABLE, arrayOf(ID), null, null, null, null, TIMESTAMP + " ASC");
        var tagI = 20;
        if (cursor.count > tagI) {
            var i = 0;
            while (cursor.moveToNext()) {
                var id = cursor.getInt(cursor.getColumnIndex("id"));
                if (i > tagI / 2) {
                    return;
                } else {
                    deleteItemById(id);
                }
                i++;
            }
        }
    }
}