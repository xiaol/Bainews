package com.github.jinsedeyuzhou.utils;

import android.annotation.TargetApi;
import android.content.Context;
import android.media.AudioManager;
import android.os.Build;

/**
 * Created by Berkeley on 1/6/17.
 */

public class ToolsUtils {

    /**
     * @param bMute 值为true时为关闭背景音乐。
     */
    @TargetApi(Build.VERSION_CODES.FROYO)
    public static boolean muteAudioFocus(Context context, boolean bMute) {
        boolean bool = false;
        AudioManager am = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        if (bMute) {
            int result = am.requestAudioFocus(null, AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN_TRANSIENT);
            bool = result == AudioManager.AUDIOFOCUS_REQUEST_GRANTED;
        } else {
            int result = am.abandonAudioFocus(null);
            bool = result == AudioManager.AUDIOFOCUS_REQUEST_GRANTED;
        }
        return bool;
    }
}
