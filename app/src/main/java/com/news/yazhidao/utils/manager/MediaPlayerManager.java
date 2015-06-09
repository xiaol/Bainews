package com.news.yazhidao.utils.manager;

import android.media.MediaPlayer;
import android.util.Log;

import java.io.IOException;

/**
 * Created by fengjigang on 15/6/4.
 * 语音评论播放器管理
 */
public class MediaPlayerManager {
    private static MediaPlayer mMediaPlayer=new MediaPlayer();
    public static void setData(String path,MediaPlayer.OnCompletionListener listener){
        try {
            if(mMediaPlayer!=null){
                mMediaPlayer.reset();
                mMediaPlayer.stop();
            }
            mMediaPlayer.reset();
            mMediaPlayer.setDataSource(path);
            mMediaPlayer.prepare();
            if(listener!=null){
                mMediaPlayer.setOnCompletionListener(listener);
            }
            mMediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mp) {
                    Log.e("jigang","prepared---");
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public static void pause(){
        if(mMediaPlayer.isPlaying()){
            mMediaPlayer.pause();
        }
    }
    public static void resume(){
        mMediaPlayer.start();
    }
    public static void stop(){
        mMediaPlayer.stop();
    }
    public static void start(){
        mMediaPlayer.start();
    }
    public static boolean isPlaying(){
        return mMediaPlayer.isPlaying();
    }
}
