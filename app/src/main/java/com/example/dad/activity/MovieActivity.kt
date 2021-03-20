package com.example.dad.activity

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.view.View
import android.view.Window
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import com.example.dad.MainApplication
import com.example.dad.R
import com.example.dad.bean.RecordMovie
import com.example.dad.until.AudioTool
import com.github.ybq.android.spinkit.SpinKitView
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.ui.PlayerView
import java.lang.ref.WeakReference
import java.util.*
import kotlin.properties.Delegates


class MovieActivity : AppCompatActivity() {
    private var mApp: MainApplication by Delegates.notNull();
    private lateinit var player: SimpleExoPlayer;
    private var now_position = 0;
    private var handler = Handler();
    private var id: Int = 0;
    private var mHandler = MyHandler(this);
    private var spinKitView: SpinKitView? = null;
    private var showloadingTimer: Timer = Timer();


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
        spinKitView = findViewById<SpinKitView>(R.id.spin_kit);

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
                if (state == 2) {
                    loadingCheck();
                    showLoading();
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

    //长时间加载之后出现提示音
    private fun loadingCheck() {
        var loadingTimer: Timer = Timer();
        loadingTimer.schedule(object : TimerTask() {
            override fun run() {
                var msg = Message();
                msg.what = 1;
                mHandler.sendMessage(msg);
                loadingTimer.cancel();
            }
        }, 1000 * 60 * 2);
    }

    //显示视频加载动画
    private fun showLoading() {
        showloadingTimer.schedule(object : TimerTask() {
            override fun run() {
                var msg = Message();
                msg.what = 2;
                mHandler.sendMessage(msg);
            }
        }, Date(), 1000 * 3);
    }

    override fun onDestroy() {
        super.onDestroy()
        player.release();
        showloadingTimer.cancel();
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

    private class MyHandler(activity: MovieActivity) : Handler() {
        private val mWeakReference = WeakReference<MovieActivity>(activity)
        override fun handleMessage(msg: Message) {
            val movieActivity = mWeakReference.get()
            when (msg.what) {
                1 -> {
                    if (!movieActivity!!.player.isPlaying) {
                        movieActivity.player.release();
                        AudioTool().playerAudio(movieActivity!!, "loading_video.mp3");
                        movieActivity.finish();
                    }
                }
                2 -> {
                    if (movieActivity!!.player.isPlaying) {
                        movieActivity.spinKitView!!.visibility = View.GONE;
                    } else {
                        movieActivity.spinKitView!!.visibility = View.VISIBLE;
                    }
                }
            }
        }
    }
}