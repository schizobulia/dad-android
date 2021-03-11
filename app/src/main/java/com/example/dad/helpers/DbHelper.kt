package com.example.dad.helpers

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log
import com.example.dad.bean.RecordMovie

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
    }

    val DATABASE_CREATE =
            "CREATE TABLE if not exists " + TABLE + " (" +
                    "${ID} integer PRIMARY KEY," +
                    "${POSTION} integer," +
                    "${DURATION} text" +
                    ")"

    fun insertRecord(rm: RecordMovie) {
        val values = ContentValues()
        values.put(ID, rm.id)
        values.put(POSTION, rm.postion)
        values.put(DURATION, rm.duration)
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
        val values = ContentValues()
        values.put(POSTION, rm.postion)
        values.put(DURATION, rm.duration)
        readableDatabase.update(TABLE, values, "id = ?", arrayOf(rm.id.toString()));
    }

    override fun onCreate(db: SQLiteDatabase) {
        Log.d(TAG, "Creating: " + DATABASE_CREATE);
        db.execSQL(DATABASE_CREATE)
    }

    override fun onUpgrade(p0: SQLiteDatabase, p1: Int, p2: Int) {
    }

}