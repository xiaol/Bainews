package com.github.jinsedeyuzhou.view;

import android.app.Activity;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.github.jinsedeyuzhou.R;
import com.github.jinsedeyuzhou.media.IjkVideoView;

import tv.danmaku.ijk.media.player.IMediaPlayer;

/**
 * Created by Berkeley on 11/2/16.
 */
public class VPlayerView extends RelativeLayout implements IMediaPlayer.OnInfoListener, IMediaPlayer.OnBufferingUpdateListener {


    private View rootView;
    private View controlbar;
    private IjkVideoView mVideoView;
    private CustomMediaContoller mediaController;
    private Context mContext;
    private Activity activity;
    private Handler handler=new Handler();
    private boolean isPause;
    private boolean portrait;
    private RelativeLayout toolbar;
    private ImageView finish;
    private TextView mTitle;
//    private ImageView mLockScreen;
    //    private ConnectionChangeReceiver changeReceiver;

    public VPlayerView(Context context) {
        super(context);
        mContext = context;
        activity= (Activity) mContext;
        initData();
        initView();
        initAction();

    }

    private void initAction() {


    }

    private void initView() {

        rootView = LayoutInflater.from(mContext).inflate(R.layout.video_player, this, true);
        controlbar = findViewById(R.id.media_contoller);
        /**
         * toolsbar
         */
        toolbar = (RelativeLayout) findViewById(R.id.app_video_top_box);
        mTitle = (TextView) findViewById(R.id.tv_video_title);
        finish = (ImageView) findViewById(R.id.iv_video_finish);
//        mLockScreen = (ImageView) findViewById(R.id.iv_video_lockScreen);


//        IntentFilter intentFilter=new IntentFilter();
//        intentFilter.addAction("android.net.conn.CONNECTIVITY_CHANGE");
//        changeReceiver=new ConnectionChangeReceiver();
//        mContext.registerReceiver(changeReceiver,intentFilter);

        mVideoView = (IjkVideoView) findViewById(R.id.main_video);
        mediaController = new CustomMediaContoller(mContext, rootView);
        mVideoView.setMediaController(mediaController);
        mediaController.setCompletionListener(new CustomMediaContoller.CompletionListener() {
            @Override
            public void completion(IMediaPlayer mp) {
                if (completionListener != null)
                    completionListener.completion(mp);
            }
        });


//        mVideoView.setOnCompletionListener(new IMediaPlayer.OnCompletionListener() {
//            @Override
//            public void onCompletion(IMediaPlayer mp) {
//                controlbar.setVisibility(View.VISIBLE);
//
//
////                toolbar.setVisibility(View.GONE);
////                mVideoView.release(true);
//
//                if (mediaController.getScreenOrientation((Activity) mContext)
//                        == ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE) {
//                    //横屏播放完毕，重置
//                    ((Activity) mContext).setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
//                    ViewGroup.LayoutParams layoutParams = getLayoutParams();
//                    layoutParams.height = ViewGroup.LayoutParams.MATCH_PARENT;
//                    layoutParams.width = ViewGroup.LayoutParams.MATCH_PARENT;
//                    setLayoutParams(layoutParams);
//                }
//                if (completionListener != null)
//                    completionListener.completion(mp);
//            }
//        });


    }

    private void initData() {

    }

    public VPlayerView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public VPlayerView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public boolean isPlay() {
        return mVideoView.isPlaying();
    }

    public void pause() {
        if (mVideoView.isPlaying()) {
            mVideoView.pause();
        } else {
            mVideoView.start();
        }
    }

    public void start(String path) {
        Uri uri = Uri.parse(path);
        if (mediaController != null)
            mediaController.start();

        if (!mVideoView.isPlaying()) {
            mVideoView.setVideoURI(uri);
            mVideoView.start();
        } else {
            mVideoView.stopPlayback();
            mVideoView.setVideoURI(uri);
            mVideoView.start();
        }
    }

    public void start(){
        if (mVideoView.isPlaying()){
            mVideoView.start();
        }
    }

    public void onPause()
    {
       mediaController.onPause();
    }
    public void onResume()
    {
        mediaController.onResume();
    }

    public void setContorllerVisiable(){
        mediaController.setVisiable();
    }

    public void seekTo(int msec){
        mVideoView.seekTo(msec);
    }

    public void onChanged(Configuration configuration) {
        portrait = configuration.orientation == Configuration.ORIENTATION_PORTRAIT;
        mediaController.doOnConfigurationChanged(portrait);

    }

    public void doOnConfigurationChanged(final boolean portrait) {
        if (mVideoView != null) {
            handler.post(new Runnable() {
                @Override
                public void run() {
                    setFullScreen(!portrait);
                    if (portrait) {
                        ViewGroup.LayoutParams layoutParams = getLayoutParams();
                        layoutParams.height = ViewGroup.LayoutParams.MATCH_PARENT;
                        layoutParams.width = ViewGroup.LayoutParams.MATCH_PARENT;
                        Log.e("handler", "400");
                        setLayoutParams(layoutParams);
                        requestLayout();
                    } else {
                        int heightPixels = ((Activity) mContext).getResources().getDisplayMetrics().heightPixels;
                        int widthPixels = ((Activity) mContext).getResources().getDisplayMetrics().widthPixels;
                        ViewGroup.LayoutParams layoutParams = getLayoutParams();
                        layoutParams.height = heightPixels;
                        layoutParams.width = widthPixels;
                        Log.e("handler", "height==" + heightPixels + "\nwidth==" + widthPixels);
                        setLayoutParams(layoutParams);
                    }

                    mediaController.updateFullScreenButton();
                }
            });
//            orientationEventListener.enable();
        }
    }

    public void stop() {
        if (mVideoView.isPlaying()) {
            mVideoView.stopPlayback();
        }
    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (mediaController.getScreenOrientation((Activity) mContext) == ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE) {
            activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
            return true;
        }
        return false;
    }

    public void showToolbar(boolean isShow)
    {
        mediaController.setFixed(true);
        toolbar.setVisibility(isShow?View.VISIBLE:View.GONE);
    }
    public void onDestroy() {
        handler.removeCallbacksAndMessages(null);
        mediaController.onDestory();
//        orientationEventListener.disable();
//        mContext.unregisterReceiver(changeReceiver);
    }

    private void setFullScreen(boolean fullScreen) {
        if (mContext != null && mContext instanceof Activity) {
            WindowManager.LayoutParams attrs = ((Activity) mContext).getWindow().getAttributes();
            if (fullScreen) {
                attrs.flags |= WindowManager.LayoutParams.FLAG_FULLSCREEN;
                ((Activity) mContext).getWindow().setAttributes(attrs);
                ((Activity) mContext).getWindow().addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
            } else {
                attrs.flags &= (~WindowManager.LayoutParams.FLAG_FULLSCREEN);
                ((Activity) mContext).getWindow().setAttributes(attrs);
                ((Activity) mContext).getWindow().clearFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
            }
        }

    }

    /**
     * 设置标题
     * @param str
     */
    public void setTitle(String str)
    {
        if (mVideoView==null)
            return ;
        mTitle.setText(str);

    }

    /**
     * 是否显示左上导航图标(一般有actionbar or appToolbar时需要隐藏)
     *
     * @param show
     */
    public void setShowNavIcon(boolean show) {
        finish.setVisibility(show ? View.VISIBLE : View.GONE);
    }


    public void setShowContoller(boolean isShowContoller) {
        mediaController.setShowContoller(isShowContoller);
    }


    public long getPalyPostion() {
        return mVideoView.getCurrentPosition();
    }

    public void release() {
        mVideoView.release(true);
    }

    public int VideoStatus() {
        return mVideoView.getCurrentStatue();
    }




    private CompletionListener completionListener;

    public void setCompletionListener(CompletionListener completionListener) {
        this.completionListener = completionListener;
    }

    @Override
    public void onBufferingUpdate(IMediaPlayer iMediaPlayer, int i) {

    }

    @Override
    public boolean onInfo(IMediaPlayer iMediaPlayer, int i, int i1) {
        return false;
    }

    public interface CompletionListener {
        void completion(IMediaPlayer mp);
    }


}
