package com.example.dad

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.os.Handler
import android.view.Window
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.ui.PlayerView


class MovieActivity : AppCompatActivity() {
    lateinit var player: SimpleExoPlayer;
    var now_position = 0;
    var handler = Handler();
    var title: String = "";

    var runnable: Runnable = Runnable {
        run {
            storageMovieData(title!!, now_position, player.currentPosition);
            handler.postDelayed(runnable, 1000 * 10);
        }
    };
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        supportActionBar?.hide();
        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        );
        setContentView(R.layout.activity_movie);

        val bundle = intent.extras;
        val urls = bundle?.getStringArrayList("movieurl");
        title = bundle?.getString("title")!!;
        val intent = Intent(this, MainActivity::class.java);
        val sharedPreferences: SharedPreferences = getSharedPreferences("share", Context.MODE_PRIVATE);

        val videoView = findViewById<PlayerView>(R.id.videoView);
        player = SimpleExoPlayer.Builder(this).build();
        player.setThrowsWhenUsingWrongThread(false);
        player.addListener(object : Player.EventListener {
            override fun onPlaybackStateChanged(state: Int) {
                super.onPlaybackStateChanged(state);
                if (state == 4) {
                    storageMovieData(title!!, 0, 0);
                    startActivity(intent);
                }
            }

            override fun onMediaItemTransition(mediaItem: MediaItem?, reason: Int) {
                super.onMediaItemTransition(mediaItem, reason);
                now_position = mediaItem?.mediaId!!.toInt();
            }
        });


        for ((index, url) in urls!!.withIndex()!!) {
            player.addMediaItem(
                MediaItem.Builder().setUri(url).setMediaId(index.toString()).build()
            )
        }

        videoView.player = player;
        if (sharedPreferences.getString("title", "1").equals(title)) {
            player.seekTo(sharedPreferences.getInt("postion", 0),
                sharedPreferences.getLong("duration", 0));
        }


        player.prepare();
        player.play();

        //存储当前播放视频标题
        storageMovieData(title!!, now_position, player.contentDuration);

        handler.postDelayed(runnable, 1000 * 60);
    }

    /**
     * @param title 视频标题
     * @param postion 视频在列表中的位置
     * @param duration 视频播放进度
     * 存储视频播放的信息
     */
    fun storageMovieData(title: String, postion: Int, duration: Long) {
        val getShare = getSharedPreferences("share", Context.MODE_PRIVATE);
        val edit = getShare.edit();
        edit.putString("title", title);
        edit.putInt("postion", postion);
        edit.putLong("duration", duration);
        edit.commit();
    }

    override fun onDestroy() {
        super.onDestroy()
        player.release();
        now_position = 0;
    }

    override fun onStop() {
        super.onStop();
        player.pause();
    }

    override fun onPause() {
        super.onPause()
        if (player.isPlaying) {
            player.pause();
        }
    }

    override fun onResume() {
        super.onResume()
        if (!player.isPlaying) {
            player.play();
        }
    }

    override fun onRestart() {
        super.onRestart()
        player.play();
    }
}

