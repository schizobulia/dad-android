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

class MovieActivity : AppCompatActivity() {
    var timer: Timer = Timer();
    lateinit var moviceView: VideoView;
    var now_position = 0;
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        supportActionBar?.hide();
        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_movice);

        val sharedPreferences = getSharedPreferences("share", Context.MODE_PRIVATE);
        val edit = sharedPreferences.edit();
        moviceView = findViewById<VideoView>(R.id.videoView);

        val bundle = intent.extras;
        val urls = bundle?.getStringArrayList("movieurl");
        var title = bundle?.getString("title");


        val intent = Intent(this, MainActivity::class.java);
        moviceView.setOnCompletionListener {
            if (urls?.size == 1){
                startActivity(intent);
            } else {
                if (now_position == urls!!.size - 1) {
                    edit.putInt("now_position", 0);
                    edit.putLong("movice_duration", 0);
                    edit.commit();
                    startActivity(intent);
                } else {
                    now_position += 1;
                    moviceView.reset();
                    moviceView.setVideoURI(Uri.parse(urls?.get(now_position)));
                    moviceView.start();
                }
            }
            println("========end================");
        }

        //上一次看的是否是这个电影，如果是跳转到上次的position
        if (sharedPreferences.getString("title", "1").equals(title)) {
            val getShare = getSharedPreferences("share", Context.MODE_PRIVATE);
            var duration = getShare.getLong("movice_duration", 0);
            now_position = sharedPreferences.getInt("now_position", 0);
            moviceView.setVideoURI(Uri.parse(urls?.get(now_position)));
            moviceView.seekTo(duration);
        } else {
            moviceView.setVideoURI(Uri.parse(urls?.get(now_position)));
        }

        moviceView.start();

        //存储当前播放视频标题
        edit.putString("title", title);
        edit.commit();

        //加入定时器，记录播放进度
        timer.schedule(object : TimerTask() {
            override fun run() {
                try {
                    val currentPosition = moviceView.currentPosition;
                    edit.putInt("now_position", now_position);
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