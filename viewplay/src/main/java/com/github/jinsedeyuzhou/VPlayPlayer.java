package com.github.jinsedeyuzhou;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.media.AudioManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.GestureDetector;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.OrientationEventListener;
import android.view.Surface;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.github.jinsedeyuzhou.media.IjkVideoView;
import com.github.jinsedeyuzhou.utils.MediaUtils;
import com.github.jinsedeyuzhou.view.PlayStateParams;

import tv.danmaku.ijk.media.player.IMediaPlayer;
import tv.danmaku.ijk.media.player.IjkMediaPlayer;

/**
 * Created by Berkeley on 11/2/16.
 */
public class VPlayPlayer extends RelativeLayout {

    private static final String TAG = "VPlayPlayer";
    private Context mContext;
    private Activity activity;
    private View view;
    private View contollerbar;
    private IjkVideoView mVideoView;

    //是否展示
    private boolean isShow;
    //是否拖动
    private boolean isDragging;
    //是否显示控制bar
    private boolean isShowContoller;

    private boolean isSound;
    private AudioManager audioManager;
    private int currentPosition;
    //默认超时时间
    private int defaultTimeout = 3000;
    //是否可以使用移动网络播放
    private boolean mobile;
    //是否是竖屏
    private boolean portrait;
    private boolean iSportrait;
    //屏幕宽度
    private int screenWidthPixels;
    public static int initHeight;
    //播放状态
    private int status = PlayStateParams.STATE_IDLE;


    //初始化view
    private ProgressBar progressBar;
    private SeekBar seekBar;
    private TextView allTime;
    private TextView time;
    private ImageView full;
    private ImageView sound;
    private ImageView play;
    private ImageView pauseImage;
    private Bitmap bitmap;
    private RelativeLayout top_box;
    private boolean isFixedTool;

    //是否允许移动播放
    private boolean isAllowModible;


    private int volume = -1;
    private float brightness = -1;
    private long newPosition = -1;
    private int mMaxVolume;
    private long duration;
    private boolean isLock;
    private boolean isPlay;
    private ImageView mVideoFinish;
    private TextView mVideoTitle;
    private ConnectionChangeReceiver changeReceiver;
    private ProgressBar bottomProgress;
    private LinearLayout gestureTouch;
    private LinearLayout gesture;
    private TextView mTvCurrent;
    private TextView mTvDuration;
    private ImageView mImageTip;
    private ProgressBar mProgressGesture;
    private RelativeLayout layout;
    private IntentFilter intentFilter;
    private ImageView mLockScreen;
    private OrientationEventListener orientationEventListener;
    private LinearLayout appVideoPlay;
    private boolean isAutoQuitFullScreen=true;

    private Handler handler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
//            Log.d(TAG, "handle");
            switch (msg.what) {
                case PlayStateParams.SET_VIEW_HIDE:
                    isShow = false;
//                    setVisibility(View.GONE);
//                    top_box.setVisibility(View.GONE);
//                    bottomProgress.setVisibility(View.VISIBLE);
                    Log.d(TAG, "handleMessage1");
//                    hideAll();
                    hide(false);
                    break;
                case PlayStateParams.MESSAGE_SHOW_PROGRESS:
//                    Log.d(TAG, "handleMessage  MESSAGE_SHOW_PROGRESS"+newPosition);
                    setProgress();
                    if (!isDragging) {
                        msg = obtainMessage(PlayStateParams.MESSAGE_SHOW_PROGRESS);
                        sendMessageDelayed(msg, 1000);
//                        updatePausePlay();
                    }
//                    Log.v(TAG, "handleMessage  MESSAGE_SHOW_PROGRESS"+newPosition);
                    break;
                case PlayStateParams.PAUSE_IMAGE_HIDE:
                    Log.v(TAG, "handleMessage3");
                    appVideoPlay.setVisibility(View.GONE);
                    break;
                case PlayStateParams.MESSAGE_SEEK_NEW_POSITION:
                    Log.v(TAG, "handleMessage MESSAGE_SEEK_NEW_POSITION" + newPosition);
                    if (newPosition >= 0) {
                        mVideoView.seekTo((int) newPosition);
                        newPosition = -1;
                    }
                    break;
                case PlayStateParams.MESSAGE_HIDE_CONTOLL:
                    Log.v(TAG, "handleMessage4");
                    gestureTouch.setVisibility(View.GONE);
                    if (isShow) {
                        show(PlayStateParams.TIME_OUT);
                    }
                    break;
                case PlayStateParams.MESSAGE_SHOW_DIALOG:
                    showWifiDialog();
                    break;
            }
        }
    };

    private final OnClickListener onClickListener = new OnClickListener() {
        @Override
        public void onClick(View view) {
            if (view.getId() == R.id.player_btn) {

                if (mVideoView.isPlaying()) {
                    pause();
                } else {
                    reStart();
                }

//                if (MediaUtils.isNetworkAvailable(mContext) || MediaUtils.isConnectionAvailable(mContext) && isAllowModible) {
//                    if (mVideoView.isPlaying()) {
//                        pause();
//                    } else {
//                        reStart();
//                    }
//                } else if (!MediaUtils.isConnectionAvailable(mContext) && !isAllowModible) {
////                    handler.sendEmptyMessage(PlayStateParams.MESSAGE_SHOW_DIALOG);
//                }

            } else if (view.getId() == R.id.full) {
                Log.e("full", "full");
                if (getScreenOrientation((Activity) mContext) == ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE) {
                    ((Activity) mContext).setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                    iSportrait = true;
                } else {
                    ((Activity) mContext).setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
                    iSportrait = false;
                }
                updateFullScreenButton();
            } else if (view.getId() == R.id.sound) {
                if (isSound) {
                    //静音
                    sound.setImageResource(R.mipmap.sound_mult_icon);
                    audioManager.setStreamMute(AudioManager.STREAM_MUSIC, true);
                } else {
                    //取消静音
                    sound.setImageResource(R.mipmap.sound_open_icon);
                    audioManager.setStreamMute(AudioManager.STREAM_MUSIC, false);
                }
                isSound = !isSound;
            } else if (view.getId() == R.id.iv_video_finish) {
                quitFullScreen();
            } else if (view.getId() == R.id.iv_video_lockScreen) {
                Log.v(TAG, "isLock:" + isLock);
                if (!isLock) {
                    isLock = true;
                    ((Activity) mContext).setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
                }
                {
                    isLock = false;
                    ((Activity) mContext).setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR);

                }
            } else if (view.getId() == R.id.pause_image) {
                appVideoPlay.setVisibility(View.GONE);
                mVideoView.seekTo(0);
                mVideoView.start();
                updatePausePlay();
            }
        }
    };


    public VPlayPlayer(Context context) {
        this(context, null);

    }

    public VPlayPlayer(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public VPlayPlayer(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.mContext = context;
        activity = (Activity) context;
        initView();
        initAction();
    }

//
//    public VPlayPlayer(Context context, View view) {
//        super(context);
//
//      
//        initView();
//        initAction();
//
//    }

    private void initView() {

        try {
            IjkMediaPlayer.loadLibrariesOnce(null);
            IjkMediaPlayer.native_profileBegin("libijkplayer.so");
        } catch (Throwable e) {
            Log.e(TAG, "loadLibraries error", e);
        }

        view = LayoutInflater.from(mContext).inflate(R.layout.video_player, this, true);
        contollerbar = findViewById(R.id.media_contoller);
        mVideoView = (IjkVideoView) findViewById(R.id.main_video);
        layout = (RelativeLayout) findViewById(R.id.layout);

//        intentFilter = new IntentFilter();
//        intentFilter.addAction("android.net.conn.CONNECTIVITY_CHANGE");
//        changeReceiver = new ConnectionChangeReceiver();
//        mContext.registerReceiver(changeReceiver,intentFilter);
        initHeight = layout.getLayoutParams().height;

        audioManager = (AudioManager) mContext.getSystemService(Context.AUDIO_SERVICE);
        screenWidthPixels = activity.getResources().getDisplayMetrics().widthPixels;


        progressBar = (ProgressBar) findViewById(R.id.loading);
        bottomProgress = (ProgressBar) findViewById(R.id.bottom_progressbar);
        seekBar = (SeekBar) findViewById(R.id.seekbar);
        allTime = (TextView) findViewById(R.id.all_time);
        time = (TextView) findViewById(R.id.time);
        full = (ImageView) findViewById(R.id.full);
        sound = (ImageView) findViewById(R.id.sound);
        play = (ImageView) findViewById(R.id.player_btn);
        pauseImage = (ImageView) findViewById(R.id.pause_image);
        appVideoPlay = (LinearLayout) findViewById(R.id.app_video_replay);

        //触屏
        gestureTouch = (LinearLayout) findViewById(R.id.ll_gesture_touch);
        gesture = (LinearLayout) findViewById(R.id.ll_gesture);
        mTvCurrent = (TextView) findViewById(R.id.tv_current);
        mTvDuration = (TextView) findViewById(R.id.tv_duration);
        mImageTip = (ImageView) findViewById(R.id.image_tip);
        mProgressGesture = (ProgressBar) findViewById(R.id.progressbar_gesture);

        //顶部
        top_box = (RelativeLayout) findViewById(R.id.app_video_top_box);
        mVideoFinish = (ImageView) findViewById(R.id.iv_video_finish);
        mVideoTitle = (TextView) findViewById(R.id.tv_video_title);
        mLockScreen = (ImageView) findViewById(R.id.iv_video_lockScreen);

    }



    private void initAction() {
        isSound = true;
        final GestureDetector detector = new GestureDetector(mContext, new PlayGestureListener());
        mMaxVolume = ((AudioManager) mContext.getSystemService(Context.AUDIO_SERVICE))
                .getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        sound.setOnClickListener(onClickListener);
        play.setOnClickListener(onClickListener);
        mVideoFinish.setOnClickListener(onClickListener);
        full.setOnClickListener(onClickListener);
        mLockScreen.setOnClickListener(onClickListener);
        pauseImage.setOnClickListener(onClickListener);
        seekBar.setMax(1000);
        seekBar.setOnSeekBarChangeListener(mSeekListener);

        layout.setClickable(true);

        layout.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (detector.onTouchEvent(event))
                    return true;

                // 处理手势结束
                switch (event.getAction() & MotionEvent.ACTION_MASK) {
                    case MotionEvent.ACTION_UP:
                        endGesture();
                        break;
                }
                return false;
            }
        });


        mVideoView.setOnInfoListener(new IMediaPlayer.OnInfoListener() {
            @Override
            public boolean onInfo(IMediaPlayer mp, int what, int extra) {

                Log.e("setOnInfoListener", what + "");
                switch (what) {
                    case IMediaPlayer.MEDIA_INFO_BUFFERING_START:
                        //开始缓冲
                        statusChange(PlayStateParams.STATE_PREPARING);
                        break;
                    case IMediaPlayer.MEDIA_INFO_BUFFERING_END:
                        //开始播放
                        statusChange(PlayStateParams.STATE_PLAYING);
                        break;

                    case IMediaPlayer.MEDIA_INFO_VIDEO_RENDERING_START:
                        statusChange(PlayStateParams.STATE_PLAYING);
                        break;

                    case IMediaPlayer.MEDIA_INFO_AUDIO_RENDERING_START:
                        statusChange(PlayStateParams.STATE_PLAYING);
                        break;
                }
                onInfoListener.onInfo(what, extra);
                return false;
            }
        });

        mVideoView.setOnErrorListener(new IMediaPlayer.OnErrorListener() {
            @Override
            public boolean onError(IMediaPlayer iMediaPlayer, int i, int i1) {
                statusChange(PlayStateParams.STATE_ERROR);
                onErrorListener.onError(i, i1);
                return true;
            }
        });

        mVideoView.setOnCompletionListener(new IMediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(IMediaPlayer mp) {
                statusChange(PlayStateParams.STATE_PLAYBACK_COMPLETED);
                if (isAutoQuitFullScreen&&getScreenOrientation((Activity) mContext)
                        == ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE) {
                    //横屏播放完毕，重置
                    ((Activity) mContext).setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                    ViewGroup.LayoutParams layoutParams = mVideoView.getLayoutParams();
                    layoutParams.height = ViewGroup.LayoutParams.MATCH_PARENT;
                    layoutParams.width = ViewGroup.LayoutParams.MATCH_PARENT;
                    mVideoView.setLayoutParams(layoutParams);
                }
                if (completionListener != null)
                    completionListener.completion(mp);
            }
        });
        mVideoView.setOnPreparedListener(new IMediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(IMediaPlayer iMediaPlayer) {

            }
        });
        orientationEventListener = new OrientationEventListener(activity) {
            @Override
            public void onOrientationChanged(int orientation) {

                if (orientation >= 0 && orientation <= 30 || orientation >= 330 || (orientation >= 150 && orientation <= 210)) {
                    //竖屏
                    if (portrait) {
                        activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR);
                        orientationEventListener.disable();
                    }
                } else if ((orientation >= 90 && orientation <= 120) || (orientation >= 240 && orientation <= 300)) {
                    if (!portrait) {
                        activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR);
                        orientationEventListener.disable();
                    }
                }
            }
        };

        hideAll();


    }

    private boolean instantSeeking;
    private final SeekBar.OnSeekBarChangeListener mSeekListener = new SeekBar.OnSeekBarChangeListener() {
        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            if (!fromUser)
                return;
            int position = (int) ((duration * progress * 1.0) / 1000);
            String string = generateTime(position);

            if (instantSeeking) {
                mVideoView.seekTo(position);
            }
            time.setText(string);
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {

            isDragging = true;
            show(3600000);
            handler.removeMessages(PlayStateParams.MESSAGE_SHOW_PROGRESS);
            if (instantSeeking) {
                audioManager.setStreamMute(AudioManager.STREAM_MUSIC, true);
            }
        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {


            if (!instantSeeking) {
                mVideoView.seekTo((int) ((duration * seekBar.getProgress() * 1.0) / 1000));
            }
            show(defaultTimeout);
            handler.removeMessages(PlayStateParams.MESSAGE_SHOW_PROGRESS);
            audioManager.setStreamMute(AudioManager.STREAM_MUSIC, false);
            isDragging = false;
            handler.sendEmptyMessageDelayed(PlayStateParams.MESSAGE_SHOW_PROGRESS, 1000);


        }
    };

    private void statusChange(int newStatus) {
        status = newStatus;
        status = newStatus;
        if (newStatus == PlayStateParams.STATE_PLAYBACK_COMPLETED) {
            Log.d(TAG, "STATE_PLAYBACK_COMPLETED");
            handler.removeMessages(PlayStateParams.MESSAGE_SHOW_PROGRESS);
            hideAll();
            bottomProgress.setProgress(0);
            isShowContoller = false;
            appVideoPlay.setVisibility(View.VISIBLE);

        } else if (newStatus == PlayStateParams.STATE_ERROR) {
            Log.d(TAG, "STATE_ERROR");
            bottomProgress.setProgress(0);
            handler.removeMessages(PlayStateParams.MESSAGE_SHOW_PROGRESS);
            hideAll();
        } else if (newStatus == PlayStateParams.STATE_PREPARING) {
            Log.d(TAG, "STATE_PREPARING");
            hideAll();
            if (progressBar.getVisibility() == View.GONE)
                progressBar.setVisibility(View.VISIBLE);
        } else if (newStatus == PlayStateParams.STATE_PLAYING) {
            Log.d(TAG, "STATE_PLAYING");
            hideAll();
            progressBar.setVisibility(View.GONE);
            isShowContoller = true;
            play.setVisibility(View.VISIBLE);
            if (!MediaUtils.isNetworkAvailable(mContext) && MediaUtils.isConnectionAvailable(mContext) && !isAllowModible) {
                mVideoView.pause();
                handler.removeMessages(PlayStateParams.MESSAGE_SHOW_PROGRESS);
                showWifiDialog();
            }

        }


    }

    /**
     * 退出全屏
     */
    private void quitFullScreen() {
        if (getScreenOrientation(activity) == ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE) {
            activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        } else
            activity.finish();
    }


    /**
     * 展示控制面板
     *
     * @param show
     */
    private void setShowContollerbar(boolean show) {
        setVisibility(show ? View.VISIBLE : View.GONE);

    }

    private void hideAll() {
        top_box.setVisibility(View.GONE);
        showBottomControl(false);
        progressBar.setVisibility(View.GONE);
        appVideoPlay.setVisibility(View.GONE);
        bottomProgress.setVisibility(View.GONE);
    }

    private void showBottomControl(boolean show) {
        contollerbar.setVisibility(show ? View.VISIBLE : View.GONE);
    }


    private void hide(boolean show) {
        if (!isFixedTool) {
            if (!iSportrait)
                top_box.setVisibility(show ? View.VISIBLE : View.GONE);
            else {
                top_box.setVisibility(View.GONE);
            }
        } else {
            top_box.setVisibility(View.VISIBLE);
        }
        showBottomControl(show);
        bottomProgress.setVisibility(show ? View.GONE : View.VISIBLE);
        updatePausePlay();
    }

    private void hide() {
        Log.d(TAG, "hide");
        if (isShow) {
//            handler.removeMessages(PlayStateParams.MESSAGE_SHOW_PROGRESS);
            isShow = false;
            handler.removeMessages(PlayStateParams.SET_VIEW_HIDE);
            hide(false);


        }
    }


    private void show(int timeout) {
        if (!isShowContoller)
            return;
        if (!isShow)
            isShow = true;
        progressBar.setVisibility(View.GONE);
        hide(true);
        handler.sendEmptyMessage(PlayStateParams.MESSAGE_SHOW_PROGRESS);
        handler.removeMessages(PlayStateParams.SET_VIEW_HIDE);
        if (timeout != 0 && mVideoView.isPlaying()) {
            handler.sendMessageDelayed(handler.obtainMessage(PlayStateParams.SET_VIEW_HIDE), timeout);
        }
    }

    private void show() {
//        Log.d(TAG, "show" + isShow + "isShowContoller" + isShowContoller + "position:" + newPosition);
//        if (!isShowContoller)
//            return;
//        if (!isShow)
//            isShow = true;
//        progressBar.setVisibility(View.GONE);
//        hide(true);
//        handler.sendEmptyMessage(PlayStateParams.MESSAGE_SHOW_PROGRESS);

    }


    /**
     * 屏幕旋转判定
     *
     * @param portrait
     */
    private void doOnConfigurationChanged(final boolean portrait) {

        iSportrait = portrait;
        if (mVideoView != null) {
            handler.post(new Runnable() {
                @Override
                public void run() {
                    setFullScreen(!portrait);
                    if (portrait) {
                        top_box.setVisibility(View.GONE);
                        Log.v(TAG, "initHeight" + initHeight);
                        ViewGroup.LayoutParams params = layout.getLayoutParams();
                        params.height = initHeight;
                        layout.setLayoutParams(params);
                        Log.v(TAG, "initHeight" + MediaUtils.dip2px(activity, initHeight));
                        top_box.setVisibility(View.GONE);

                    } else {
                        int heightPixels = activity.getResources().getDisplayMetrics().heightPixels;
                        int widthPixels = activity.getResources().getDisplayMetrics().widthPixels;
                        layout.getLayoutParams().height = Math.min(heightPixels, widthPixels);
                        Log.v(TAG, "initHeight" + 0);
                    }
                    updateFullScreenButton();
                }
            });
        }
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

//    public void showDialog() {
//        final AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
//
//        View view = LayoutInflater.from(mContext).inflate(R.layout.item_dialog, null);
//        builder.setView(view);
//        builder.create();
//       findViewById(R.id.tv_pause).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
////                alertDialog.dismiss();
//                builder.create().dismiss();
//            }
//        });
//       findViewById(R.id.tv_continue_play).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
////                alertDialog.dismiss();
//                builder.create().dismiss();
//                isAllowModible = true;
//            }
//        });
//
//        builder.setView(view);
//
//
//        builder.create().show();
//
//
//    }

    /**
     * 更新全屏按钮
     */
    private void updateFullScreenButton() {
        if (getScreenOrientation((Activity) mContext) == ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE) {
            full.setImageResource(R.mipmap.ic_fullscreen_exit);
        } else {
            full.setImageResource(R.mipmap.ic_fullscreen);
        }
    }

    /**
     * 更新播放按钮
     */
    private void updatePausePlay() {
        if (mVideoView.isPlaying()) {
            play.setImageResource(R.drawable.pause_selector);
        } else {
            play.setImageResource(R.drawable.play_selector);
        }
    }


    public int getScreenOrientation(Activity activity) {
        int rotation = activity.getWindowManager().getDefaultDisplay().getRotation();
        DisplayMetrics dm = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getMetrics(dm);
        int width = dm.widthPixels;
        int height = dm.heightPixels;
        int orientation;
        // if the device's natural orientation is portrait:
        if ((rotation == Surface.ROTATION_0
                || rotation == Surface.ROTATION_180) && height > width ||
                (rotation == Surface.ROTATION_90
                        || rotation == Surface.ROTATION_270) && width > height) {
            switch (rotation) {
                case Surface.ROTATION_0:
                    orientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;
                    break;
                case Surface.ROTATION_90:
                    orientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE;
                    break;
                case Surface.ROTATION_180:
                    orientation =
                            ActivityInfo.SCREEN_ORIENTATION_REVERSE_PORTRAIT;
                    break;
                case Surface.ROTATION_270:
                    orientation =
                            ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE;
                    break;
                default:
                    orientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;
                    break;
            }
        }
        // if the device's natural orientation is landscape or if the device
        // is square:
        else {
            switch (rotation) {
                case Surface.ROTATION_0:
                    orientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE;
                    break;
                case Surface.ROTATION_90:
                    orientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;
                    break;
                case Surface.ROTATION_180:
                    orientation =
                            ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE;
                    break;
                case Surface.ROTATION_270:
                    orientation =
                            ActivityInfo.SCREEN_ORIENTATION_REVERSE_PORTRAIT;
                    break;
                default:
                    orientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE;
                    break;
            }
        }

        return orientation;
    }


    private String generateTime(long time) {
        int totalSeconds = (int) (time / 1000);
        int seconds = totalSeconds % 60;
        int minutes = (totalSeconds / 60) % 60;
        int hours = totalSeconds / 3600;
        return hours > 0 ? String.format("%02d:%02d:%02d", hours, minutes, seconds) : String.format("%02d:%02d", minutes, seconds);
    }


    private long setProgress() {
        if (isDragging) {
            return 0;
        }

        long position = mVideoView.getCurrentPosition();
        long duration = mVideoView.getDuration();
        this.duration = duration;
        if (!generateTime(duration).equals(allTime.getText().toString()))
            allTime.setText(generateTime(duration));
        if (seekBar != null) {
            if (duration > 0) {
                long pos = 1000L * position / duration;
                seekBar.setProgress((int) pos);
                bottomProgress.setProgress((int) pos);
            }
            int percent = mVideoView.getBufferPercentage();
            seekBar.setSecondaryProgress(percent * 10);
            bottomProgress.setSecondaryProgress(percent * 10);
        }
        String string = generateTime((long) (duration * seekBar.getProgress() * 1.0f / 1000));
        time.setText(string);
        return position;
    }


    public class PlayGestureListener extends GestureDetector.SimpleOnGestureListener {

        private boolean firstTouch;
        private boolean volumeControl;
        private boolean seek;

        /**
         * 双击
         */
        @Override
        public boolean onDoubleTap(MotionEvent e) {
            Log.v(TAG, "onDoubleTap");
//            mVideoView.toggleAspectRatio();
            return true;
        }

        @Override
        public boolean onDown(MotionEvent e) {
            firstTouch = true;
            handler.removeMessages(PlayStateParams.SET_VIEW_HIDE);
            //横屏下拦截事件
//            if (getScreenOrientation((Activity) mContext) == ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE) {
//                return true;
//            } else {
//                return false;
//            }
            return super.onDown(e);
        }


        /**
         * 单击
         *
         * @param e
         * @return
         */
        @Override
        public boolean onSingleTapUp(MotionEvent e) {
            Log.d(TAG, "onSingleTapUp" + isShow);
            if (isShow) {
                hide();
            } else {
                show(PlayStateParams.TIME_OUT);
            }
            return true;
        }

        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
            float mOldX = e1.getX(), mOldY = e1.getY();
            float deltaY = mOldY - e2.getY();
            float deltaX = mOldX - e2.getX();
            if (firstTouch) {
                seek = Math.abs(distanceX) >= Math.abs(distanceY);
                volumeControl = mOldX > screenWidthPixels * 0.5f;
                firstTouch = false;
            }
            contollerbar.setVisibility(View.GONE);
            if (seek) {
                onProgressSlide(-deltaX / mVideoView.getWidth());
            } else {
                float percent = deltaY / mVideoView.getHeight();
                if (volumeControl) {
                    onVolumeSlide(percent);
                } else {
                    onBrightnessSlide(percent);
                }


            }

            return super.onScroll(e1, e2, distanceX, distanceY);


        }


    }


    /**
     * 手势结束
     */
    private void endGesture() {
        Log.v(TAG, "endGesture:new Position:" + newPosition);
        volume = -1;
        brightness = -1f;
        if (newPosition >= 0) {
            handler.removeMessages(PlayStateParams.MESSAGE_SEEK_NEW_POSITION);
            handler.sendEmptyMessage(PlayStateParams.MESSAGE_SEEK_NEW_POSITION);
        }
        handler.removeMessages(PlayStateParams.MESSAGE_HIDE_CONTOLL);
        handler.sendEmptyMessageDelayed(PlayStateParams.MESSAGE_HIDE_CONTOLL, 500);

    }


    /**
     * 滑动改变声音大小
     *
     * @param percent
     */
    private void onVolumeSlide(float percent) {
        if (volume == -1) {
            volume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
            if (volume < 0)
                volume = 0;
        }
//        hide();

        int index = (int) (percent * mMaxVolume) + volume;
        if (index > mMaxVolume)
            index = mMaxVolume;
        else if (index < 0)
            index = 0;

        // 变更声音
        audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, index, 0);
        // 变更进度条
        int i = (int) (index * 1.0f / mMaxVolume * 100);
        if (i == 0) {
            sound.setImageResource(R.mipmap.sound_mult_icon);
        } else {
            sound.setImageResource(R.mipmap.sound_open_icon);
        }
        if (i != 0) {
            if (gestureTouch.getVisibility() == View.GONE) {
                gestureTouch.setVisibility(View.VISIBLE);
                gesture.setVisibility(View.GONE);
                mImageTip.setImageResource(R.mipmap.player_video_volume);
            }
        }
        mProgressGesture.setProgress(i);
//        sound_seek.setProgress(i);
    }

    /**
     * 快进或者快退
     *
     * @param percent 移动比例
     * @param
     */
    private void onProgressSlide(float percent) {
        Log.v(TAG, "onprogressSlide:" + newPosition);
        long position = mVideoView.getCurrentPosition();
        long duration = mVideoView.getDuration();
        long deltaMax = Math.min(100 * 1000, duration - position);
        long delta = (long) (deltaMax * percent);

        newPosition = delta + position;
        if (newPosition > duration) {
            newPosition = duration;
        } else if (newPosition <= 0) {
            newPosition = 0;
            delta = -position;
        }
        int showDelta = (int) delta / 1000;
        Log.e("showdelta", ((percent) * 100) + "");
        if (showDelta != 0) {
            if (gestureTouch.getVisibility() == View.GONE) {
                gestureTouch.setVisibility(View.VISIBLE);
                gesture.setVisibility(View.VISIBLE);
            }
            mImageTip.setImageResource(showDelta > 0 ? R.mipmap.forward_icon : R.mipmap.backward_icon);

            String current = generateTime(newPosition);

//            seekTxt.setText(current + "/" + allTime.getText());
            mTvCurrent.setText(current + "/");
            mTvDuration.setText(allTime.getText());
//            mProgressGesture.setProgress((int) newPosition);
            mProgressGesture.setProgress(duration <= 0 ? 0 : (int) (newPosition * 100 / duration));
            Log.v(TAG, "onprogressSlide:" + newPosition);
        }
    }

    /**
     * 滑动改变亮度
     *
     * @param percent
     */
    private void onBrightnessSlide(float percent) {
        if (brightness < 0) {
            brightness = ((Activity) mContext).getWindow().getAttributes().screenBrightness;
            if (brightness <= 0.00f) {
                brightness = 0.50f;
            } else if (brightness < 0.01f) {
                brightness = 0.01f;
            }
        }
        Log.d(this.getClass().getSimpleName(), "brightness:" + brightness + ",percent:" + percent);
        WindowManager.LayoutParams lpa = ((Activity) mContext).getWindow().getAttributes();
        lpa.screenBrightness = brightness + percent;
        if (lpa.screenBrightness > 1.0f) {
            lpa.screenBrightness = 1.0f;
        } else if (lpa.screenBrightness < 0.01f) {
            lpa.screenBrightness = 0.01f;
        }

        if (gestureTouch.getVisibility() == View.GONE) {
            gestureTouch.setVisibility(View.VISIBLE);
            gesture.setVisibility(View.GONE);
            mImageTip.setImageResource(R.mipmap.player_video_light);
        }

        mProgressGesture.setProgress((int) (lpa.screenBrightness * 100));
        ((Activity) mContext).getWindow().setAttributes(lpa);

    }

    private void start() {
//        ViewGroup last = (ViewGroup) this.getParent();//找到videoitemview的父类，然后remove
//        if (last != null) {
//            last.removeAllViews();
//        }
        isShowContoller = false;
        bottomProgress.setProgress(0);
        progressBar.setVisibility(View.VISIBLE);
        hide(false);


    }

    private void pause() {
        play.setImageResource(R.drawable.play_selector);
        mVideoView.pause();
        bitmap = mVideoView.getBitmap();
        if (bitmap != null) {
            pauseImage.setImageBitmap(bitmap);
            appVideoPlay.setVisibility(View.VISIBLE);
        }
    }

    private void reStart() {
        play.setImageResource(R.drawable.pause_selector);
        mVideoView.start();
        if (bitmap != null) {
            handler.sendEmptyMessageDelayed(PlayStateParams.PAUSE_IMAGE_HIDE, 100);
            bitmap.recycle();
            bitmap = null;
        }
    }


    //==========================对外提供接口==============================

    public boolean isPlay() {
        return mVideoView.isPlaying();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (getScreenOrientation((Activity) mContext) == ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE) {
            activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
            return true;
        }
        return false;
    }

    public void setAutoQuitFullScreen(boolean autoQuitFullScreen)
    {
        isAutoQuitFullScreen=autoQuitFullScreen;
    }

    public void showWifiDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setMessage(mContext.getResources().getString(R.string.tips_not_wifi));
        builder.setPositiveButton(mContext.getResources().getString(R.string.tips_not_wifi_confirm), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
//                startPlayLogic();
//                WIFI_TIP_DIALOG_SHOWED = true;
            }
        });
        builder.setNegativeButton(mContext.getResources().getString(R.string.tips_not_wifi_cancel), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.create().show();
    }


    public boolean isShowing() {
        return isShow;
    }

    public void setVisiable() {
        show();
    }
//
//    private VedioIsPause vedioIsPause;
//
//    public interface VedioIsPause {
//        void pause(boolean pause);
//    }

    public void stop() {
        if (mVideoView.isPlaying()) {
            mVideoView.stopPlayback();
        }
    }

    public void release() {
        if (mVideoView != null)
            mVideoView.release(true);
    }

    public int getStatus() {
        return status;
    }

    public void setPauseImageHide() {
        appVideoPlay.setVisibility(View.GONE);
    }

    public void onDestory() {
        orientationEventListener.disable();
        handler.removeCallbacksAndMessages(null);
        mVideoView.stopPlayback();
    }

    public void onResume() {
        orientationEventListener.enable();
//        mVideoView.resume();
        if (status == PlayStateParams.STATE_PAUSED) {
            if (currentPosition > 0) {
                mVideoView.seekTo((int) currentPosition);
            }
            mVideoView.start();
            statusChange(PlayStateParams.STATE_PLAYING);
        }
    }

    public void onPause() {
        show(0);//把系统状态栏显示出来
        if (status == PlayStateParams.STATE_PLAYING) {
            mVideoView.pause();
            currentPosition = mVideoView.getCurrentPosition();
            statusChange(PlayStateParams.STATE_PAUSED);
        }
    }


    public void setShowContoller(boolean isShowContoller) {
        this.isShowContoller = isShowContoller;
        handler.removeMessages(PlayStateParams.SET_VIEW_HIDE);
    }


    public void setFixed(boolean isFixedTool) {
        this.isFixedTool = isFixedTool;
    }

    public void start(String path) {
        Uri uri = Uri.parse(path);
        start();
        if (!mVideoView.isPlaying()) {
            mVideoView.setVideoURI(uri);
            mVideoView.start();
        } else {
            mVideoView.stopPlayback();
            mVideoView.setVideoURI(uri);
            mVideoView.start();
        }
    }


    /**
     * 设置标题
     *
     * @param str
     */
    public void setTitle(String str) {
        if (mVideoView == null)
            return;
        mVideoTitle.setText(str);

    }

    /**
     * 是否显示左上导航图标(一般有actionbar or appToolbar时需要隐藏)
     *
     * @param show
     */
    public void setShowNavIcon(boolean show) {
        mVideoFinish.setVisibility(show ? View.VISIBLE : View.GONE);
    }

    public void onChanged(Configuration configuration) {
        portrait = configuration.orientation == Configuration.ORIENTATION_PORTRAIT;
        doOnConfigurationChanged(portrait);

    }

    //============================网络监听================================

    class ConnectionChangeReceiver extends BroadcastReceiver {
        private final String TAG = ConnectionChangeReceiver.class.getSimpleName();
        private boolean isWifi;
        private boolean isMobile;

        @Override
        public void onReceive(Context context, Intent intent) {
            Log.e(TAG, "网络状态改变");
            //获得网络连接服务
            ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(context.CONNECTIVITY_SERVICE);
            //获取wifi连接状态
            NetworkInfo.State wifi = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState();
            //判断是否正在使用wifi网络
            if (wifi == NetworkInfo.State.CONNECTED) {
                isWifi = true;
            } else
                isWifi = false;
            //获取GPRS状态
            NetworkInfo.State state = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState();
            //判断是否在使用GPRS网络
            if (state == NetworkInfo.State.CONNECTED) {
                isMobile = true;
            } else
                isMobile = false;
            //如果没有连接成功
            if (!isWifi && isMobile) {

                pause();
//                show();

            } else if (!isWifi && !isMobile) {
                pause();
//                show();
//                handler.sendEmptyMessage(PlayStateParams.MESSAGE_SHOW_DIALOG);
                Toast.makeText(context, "当前网络无连接", Toast.LENGTH_SHORT).show();

            }


        }

    }

    //============================外部接口====================================

    public interface OnErrorListener {
        void onError(int what, int extra);
    }

    private OnErrorListener onErrorListener = new OnErrorListener() {
        @Override
        public void onError(int what, int extra) {

        }
    };


    public interface OnInfoListener {
        void onInfo(int what, int extra);
    }

    private OnInfoListener onInfoListener = new OnInfoListener() {
        @Override
        public void onInfo(int what, int extra) {

        }
    };


    private CompletionListener completionListener;

    public void setCompletionListener(CompletionListener completionListener) {
        this.completionListener = completionListener;
    }

    public interface CompletionListener {
        void completion(IMediaPlayer mp);
    }


}
