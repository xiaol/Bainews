package com.news.yazhidao.widget;

import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.media.MediaPlayer;
import android.os.Environment;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.alibaba.sdk.android.oss.callback.GetBytesCallback;
import com.alibaba.sdk.android.oss.model.OSSException;
import com.news.yazhidao.R;
import com.news.yazhidao.utils.DensityUtil;
import com.news.yazhidao.utils.manager.AliYunOssManager;
import com.news.yazhidao.utils.manager.MediaPlayerManager;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Created by fengjigang on 15/6/5.
 */
public class SpeechView extends LinearLayout implements View.OnClickListener {
    private boolean isPlay;
    private Context mContext;
    private AnimationDrawable aniSpeech;
    private ImageView mWave;
    //语音url
    private String mUrl;
    //语音的时长
    private int mDuration;

    public SpeechView(Context context) {
        this(context, null);
    }

    public SpeechView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SpeechView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.mContext = context;
        View view = View.inflate(context, R.layout.speech_layout, this);
        mWave= (ImageView) view.findViewById(R.id.mWave);
        setOnClickListener(this);
    }

    public void setUrl(String url) {
        this.mUrl = url;
    }

    public void setDuration(int duration) {
        int screenWidth = getScreenWidth();
        int newWidth = (int) (screenWidth * 0.7 * duration / 30);
        LayoutParams params = new LayoutParams(newWidth, DensityUtil.dip2px(mContext, 30));
        this.setLayoutParams(params);
    }


    @Override
    public void onClick(View v) {
        //TODO 重复点击的的逻辑处理
        Log.e("jigang", "--onclick-");
        if (TextUtils.isEmpty(mUrl)) {
            return;
        }
        //如果已经存在改语音文件，则直接播放
        boolean existFile = isExistFile(mContext, mUrl);
        if(existFile){
            String filePath = getFilePath(mContext, mUrl);
            Log.e("jigang", "----filepath--" + filePath + ",,");
            if(MediaPlayerManager.isPlaying()){
                MediaPlayerManager.pause();
                if(aniSpeech!=null){
                    mWave.setImageResource(R.drawable.ic_speech_wave3);
                    aniSpeech.stop();
                }
                return;
            }
            if(!isPlay){
                isPlay=true;

                MediaPlayerManager.setData(filePath,new MediaPlayer.OnCompletionListener(){

                    @Override
                    public void onCompletion(MediaPlayer mp) {
                        Log.e("jigang","----play oncompletion----");
                        mp.release();
                        if(aniSpeech!=null){
                            mWave.setImageResource(R.drawable.ic_speech_wave3);
                            aniSpeech.stop();
                        }
                    }
                });
            }
            MediaPlayerManager.start();
            mWave.setImageResource(R.drawable.ani_speech);
            aniSpeech = (AnimationDrawable) mWave.getDrawable();
            aniSpeech.start();

            return;
        }
        //下载语音文件到sd 卡
        AliYunOssManager.getInstance(mContext).downloadSpeechFile(mUrl, new GetBytesCallback() {
            @Override
            public void onSuccess(String s, byte[] bytes) {
                Log.e("jigang", "---onSuccess- success");
                //把文件写入到cache中
                File path = writeFile2SDCard(md5(mUrl), bytes);
            }

            @Override
            public void onProgress(String s, int i, int i1) {
                Log.e("jigang", "---onProgress-");
            }

            @Override
            public void onFailure(String s, OSSException e) {
                Log.e("jigang", "---onFailure- " + e.getMessage());
            }
        });
    }

    public String md5(String key) {
        String cacheKey;
        try {
            final MessageDigest mDigest = MessageDigest.getInstance("MD5");
            mDigest.update(key.getBytes());
            cacheKey = bytesToHexString(mDigest.digest());
        } catch (NoSuchAlgorithmException e) {
            cacheKey = String.valueOf(key.hashCode());
        }
        return cacheKey;
    }

    private String bytesToHexString(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < bytes.length; i++) {
            String hex = Integer.toHexString(0xFF & bytes[i]);
            if (hex.length() == 1) {
                sb.append('0');
            }
            sb.append(hex);
        }
        return sb.toString();
    }

    public File writeFile2SDCard(String name, byte[] bytes) {
        File path = null;
        try {
            OutputStream o = new FileOutputStream(getSavePath(mContext, name));
            BufferedOutputStream out = new BufferedOutputStream(o);
            out.write(bytes);
            out.close();
            path = getSavePath(mContext, name);
            Log.e("jigang", "---writeFile2SDCard- success");
        } catch (IOException e) {
            e.printStackTrace();
        }
        return path;
    }

    public static File getSavePath(Context mContext, String name) {
        File file = null;
        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())
                || !Environment.isExternalStorageRemovable()) {
            file = new File(Environment.getExternalStorageDirectory().toString());
        } else {
            file = new File(mContext.getFilesDir().toString());
        }
        if (file != null) {
            file = new File(file + File.separator + "yazhidao" + File.separator + "speech");
            if (!file.exists()) {
                try {
                    file.mkdirs();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        return new File(file, name + ".mp3");
    }
    public  boolean isExistFile(Context mContext, String mUrl) {
        File file = null;
        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())
                || !Environment.isExternalStorageRemovable()) {
            file = new File(Environment.getExternalStorageDirectory().toString());
        } else {
            file = new File(mContext.getFilesDir().toString());
        }
        if (file != null) {
            file = new File(file + File.separator + "yazhidao" + File.separator + "speech"+File.separator+md5(mUrl)+".mp3");
            return file.exists();
        }
        return false;
    }
    public String getFilePath(Context mContext,String mUrl){
        File file = null;
        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())
                || !Environment.isExternalStorageRemovable()) {
            file = new File(Environment.getExternalStorageDirectory().toString());
        } else {
            file = new File(mContext.getFilesDir().toString());
        }
        if (file != null) {
            file = new File(file + File.separator + "yazhidao" + File.separator + "speech"+File.separator+md5(mUrl)+".mp3");
        }
        return file.getPath();
    }
    public int getScreenWidth() {
        return obtainDisMetri().widthPixels;
    }

    public int getScreenHeight() {
        return obtainDisMetri().heightPixels;
    }

    private DisplayMetrics obtainDisMetri() {
        WindowManager wm = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics outMetrics = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(outMetrics);
        return outMetrics;
    }
}
