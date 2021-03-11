package com.example.dad

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.view.Window
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import com.example.dad.bean.RecordMovie
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.ui.PlayerView
import kotlin.properties.Delegates


class MovieActivity : AppCompatActivity() {
    private var mApp: MainApplication by Delegates.notNull();
    private lateinit var player: SimpleExoPlayer;
    private var now_position = 0;
    private var handler = Handler();
    private var id: Int = 0;


    var runnable: Runnable = Runnable {
        run {
            storageMovieData(id, now_position, player.currentPosition);
            handler.postDelayed(runnable, 1000 * 20);
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
        mApp = application as MainApplication;

        val bundle = intent.extras;
        val urls = bundle?.getStringArrayList("movieurl");
        id = bundle?.getInt("id")!!;
        val intent = Intent(this, MainActivity::class.java);

        val videoView = findViewById<PlayerView>(R.id.videoView);
        player = SimpleExoPlayer.Builder(this).build();
        player.setThrowsWhenUsingWrongThread(false);
        player.addListener(object : Player.EventListener {
            override fun onPlaybackStateChanged(state: Int) {
                super.onPlaybackStateChanged(state);
                if (state == 4) {
                    storageMovieData(id, 0, 0);
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

        var startM = mApp.dbHelper.getItemById(id);
        if (startM.id != 0) {
            player.seekTo(startM.postion, startM.duration);
            storageMovieData(startM.id, startM.postion, startM.duration);
        } else {
            storageMovieData(id, now_position, player.contentDuration);
        }


        player.prepare();
        player.play();

        //存储当前播放视频id


        handler.postDelayed(runnable, 1000 * 20);
    }

    /**
     * @param id 视频id
     * @param postion 视频在列表中的位置
     * @param duration 视频播放进度
     * 存储视频播放的信息
     */
    fun storageMovieData(id: Int, postion: Int, duration: Long) {
        var rm = RecordMovie();
        rm.id = id;
        rm.postion = postion;
        rm.duration = duration;
        if (mApp.dbHelper.getItemById(id).id != 0) {
            mApp.dbHelper.updateItemById(rm);
        } else {
            mApp.dbHelper.insertRecord(rm);
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        player.release();
    }

    override fun onStop() {
        super.onStop();
        player.pause();
        handler.removeCallbacks(runnable);
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

