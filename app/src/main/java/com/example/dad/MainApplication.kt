package com.example.dad

import android.app.Application
import com.example.dad.helpers.DbHelper
import kotlin.properties.Delegates

class MainApplication : Application() {
    //database helper
    var dbHelper: DbHelper by Delegates.notNull();

    override fun onCreate() {
        super.onCreate();
        dbHelper = DbHelper(this);
        dbHelper.dbMovieCountCheck();
    }
}