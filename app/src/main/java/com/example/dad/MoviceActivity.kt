package com.example.dad

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.Window
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import com.devbrackets.android.exomedia.ui.widget.VideoView
import java.util.*

class MoviceActivity : AppCompatActivity() {
    var timer: Timer = Timer();
    lateinit var moviceView: VideoView;
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        supportActionBar?.hide();
        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_movice);

        val sharedPreferences = getSharedPreferences("share", Context.MODE_PRIVATE);
        moviceView = findViewById<VideoView>(R.id.videoView);

        val bundle = intent.extras;
        val url = bundle?.getString("moviceurl");

        moviceView.setVideoURI(Uri.parse(url));
        val intent = Intent(this, MainActivity::class.java);
        moviceView.setOnCompletionListener {
            startActivity(intent);
            println("========end================");
        }

        moviceView.start();

        //上一次看的是否是这个电影，如果是跳转到上次的position
        if (sharedPreferences.getString("movice_url", "1").equals(url)) {
            val getShare = getSharedPreferences("share", Context.MODE_PRIVATE);
            moviceView.seekTo(getShare.getLong("movice_duration", 0));
        }

        //存储当前播放的电影url
        val edit = sharedPreferences.edit();
        edit.putString("movice_url", url);
        edit.commit();

        //加入定时器，记录播放进度
        timer.schedule(object : TimerTask() {
            override fun run() {
                try {
                    val currentPosition = moviceView.currentPosition;
                    edit.putLong("movice_duration", currentPosition);
                    edit.commit();
                } catch (e: Exception) {
                };
            }
        }, Date(), 1000 * 60);
    }

    override fun onDestroy() {
        super.onDestroy();
        timer.cancel();
    }


    override fun onStop() {
        super.onStop();
        moviceView.pause();
        timer.cancel();
    }

    override fun onPause() {
        super.onPause()
        if (moviceView.isPlaying){
            moviceView.pause();
        }
    }

    override fun onResume() {
        super.onResume()
        if (!moviceView.isPlaying){
            moviceView.restart();
        }
    }

    override fun onRestart() {
        super.onRestart()
        moviceView.start();
    }
}