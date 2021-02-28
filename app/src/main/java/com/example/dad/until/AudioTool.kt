package com.example.dad.until

import android.content.Context
import android.content.res.AssetFileDescriptor
import android.media.MediaPlayer

//视频播放工具
class AudioTool {
    //播放音频文件
    fun playerAudio(context: Context, filePath: String){
        var fd: AssetFileDescriptor? = null;
        var mediaPlayer = MediaPlayer();
        fd = context.applicationContext.assets.openFd(filePath);
        mediaPlayer.setDataSource(fd.fileDescriptor, fd.startOffset, fd.length);
        mediaPlayer.prepare();
        mediaPlayer.start();
        println("===========");
    }
}