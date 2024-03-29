package com.github.jinsedeyuzhou;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.media.AudioManager;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.provider.Settings;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.GestureDetector;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.OrientationEventListener;
import android.view.Surface;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.github.jinsedeyuzhou.media.IjkVideoView;
import com.github.jinsedeyuzhou.utils.MediaNetUtils;
import com.github.jinsedeyuzhou.view.MarqueeTextView;

import java.util.Locale;

import tv.danmaku.ijk.media.player.IMediaPlayer;
import tv.danmaku.ijk.media.player.IjkMediaPlayer;

/**
 * Created by Berkeley on 11/2/16.
 */
public class VPlayPlayer extends FrameLayout {

    private static final String TAG = "VPlayPlayer";
    private Context mContext;
    private Activity activity;
    private View view;
    private View contollerbar;
    private IjkVideoView mVideoView;
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
    private ImageView mVideoFinish;
    private MarqueeTextView mVideoTitle;

    private ProgressBar bottomProgress;
    private LinearLayout gestureTouch;
    private LinearLayout gesture;
    private TextView mTvCurrent;
    private TextView mTvDuration;
    private ImageView mImageTip;
    private ProgressBar mProgressGesture;
    private IntentFilter intentFilter;
    private ImageView mVideoLock;
    private RelativeLayout appVideoPlay;
    private ImageView mVideoShare;
    private LinearLayout container_tools;
    private LinearLayout mVideoStaus;
    private TextView mStatusText;
    private LinearLayout mVideoNetTie;
    private TextView mVideoNetTieConfirm;
    private TextView mVideoNetTieCancel;
    private TextView mVideoDuration;

    //是否展示
    private boolean isShow;
    //是否拖动
    private boolean isDragging;
    //是否显示控制bar
    private boolean isShowContoller;

    //    private boolean PlayerApplication.getInstance().isSound;
    private AudioManager audioManager;
    private int currentPosition;
    //默认超时时间
    private int defaultTimeout = 3000;
    //是否可以使用移动网络播放
    private boolean mobile;
    //是否是竖屏
    private boolean portrait;
    //屏幕宽度
    private int screenWidthPixels;
    public int initHeight;
    //播放状态
    private int status = PlayStateParams.STATE_IDLE;

    private boolean isAutoPause;
    private boolean isNetListener = true;
    private boolean playerSupport;

    //是否允许移动播放
    private boolean isAllowModible;
    private int volume = -1;
    private float brightness = -1;
    private long newPosition = -1;
    private int mMaxVolume;
    private long duration;
    private boolean isLock;
    private boolean isPlay;
    private boolean instantSeeking;
    private String url;


    private boolean mIsLand = false; // 是否是横屏
    private boolean mClick = false; // 是否点击
    private boolean mClickLand = true; // 点击进入横屏
    private boolean mClickPort = true; // 点击进入竖屏

    private OrientationEventListener orientationEventListener;
    private OnClickOrientationListener onClickOrientationListener;
    private NetChangeReceiver changeReceiver;

    private boolean firstPlay;


    private Handler handler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case PlayStateParams.SET_VIEW_HIDE:
                    isShow = false;
                    hide(false);
                    break;
                case PlayStateParams.MESSAGE_SHOW_PROGRESS:
                    setProgress();
                    if (!isDragging) {
                        msg = obtainMessage(PlayStateParams.MESSAGE_SHOW_PROGRESS);
                        sendMessageDelayed(msg, 1000);
                    }

                    break;
                case PlayStateParams.PAUSE_IMAGE_HIDE:
                    appVideoPlay.setVisibility(View.GONE);
                    break;
                case PlayStateParams.MESSAGE_SEEK_NEW_POSITION:
                    if (newPosition >= 0) {
                        mVideoView.seekTo((int) newPosition);
                        newPosition = -1;
                    }
                    break;
                case PlayStateParams.MESSAGE_HIDE_CONTOLL:
                    gestureTouch.setVisibility(View.GONE);
                    if (isShow) {
                        show(PlayStateParams.TIME_OUT);
                    }
                    break;
                case PlayStateParams.MESSAGE_SHOW_DIALOG:
                    mVideoDuration.setText(generateTimeSeconds(duration));
                    mVideoNetTie.setVisibility(View.VISIBLE);
                    break;
            }
        }
    };

    private final OnClickListener onClickListener = new OnClickListener() {
        @Override
        public void onClick(View view) {
            int id = view.getId();
            if (id == R.id.player_btn) {
                if (isAllowModible && MediaNetUtils.getNetworkType(mContext) == 6 || MediaNetUtils.getNetworkType(mContext) == 3) {
                    doPauseResume();
                } else if (mVideoView.isPlaying() && !isAllowModible && MediaNetUtils.getNetworkType(mContext) == 6) {
//                    showWifiDialog();
                    mVideoDuration.setText(generateTimeSeconds(duration));
                    mVideoNetTie.setVisibility(View.VISIBLE);
                }

            } else if (id == R.id.full) {
                toggleFullScreen();
            } else if (id == R.id.sound) {

                if (!PlayerApplication.getInstance().isSound) {
                    //静音
                    sound.setImageResource(R.mipmap.sound_mult_icon);
//                    audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, 0, 0);
                    audioManager.setStreamMute(AudioManager.STREAM_MUSIC, true);
                } else {
                    //取消静音
                    sound.setImageResource(R.mipmap.sound_open_icon);
                    audioManager.setStreamMute(AudioManager.STREAM_MUSIC, false);
                }
                PlayerApplication.getInstance().isSound = !PlayerApplication.getInstance().isSound;
            } else if (id == R.id.iv_video_finish) {
                if (!onBackPressed())
                    activity.finish();
            } else if (id == R.id.app_video_lock) {
                if (!isLock) {
                    isLock = true;
                    mVideoLock.setImageResource(R.mipmap.video_lock);

                } else {
                    isLock = false;
                    mVideoLock.setImageResource(R.mipmap.video_unlock);
                }
//            } else if (id == R.id.pause_image) {
//                appVideoPlay.setVisibility(View.GONE);
//                mVideoView.seekTo(0);
//                mVideoView.start();
            } else if (id == R.id.app_video_share) {
                if (onShareListener != null)
                    onShareListener.onShare();
            } else if (id == R.id.app_video_netTie_confirm) {
                isAllowModible = true;
                mVideoNetTie.setVisibility(View.GONE);
                if (currentPosition == 0) {
                    play(url);
                } else
                    doPauseResume();

            } else if (id == R.id.app_video_netTie_cancel) {
                mVideoNetTie.setVisibility(View.GONE);
                if (onShareListener != null)
                    onShareListener.onPlayCancel();
            }
        }
    };


    public VPlayPlayer(Context context) {
        super(context);
        init(context);

    }


    public VPlayPlayer(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public VPlayPlayer(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        init(context);

    }


    private void init(Context context) {
        this.mContext = context;
        activity = (Activity) context;
        initView();
        initAction();
        initMediaPlayer();

    }

    private void initView() {


        View.inflate(mContext, R.layout.video_player, this);
        contollerbar = findViewById(R.id.media_contoller);
        mVideoView = (IjkVideoView) findViewById(R.id.main_video);
        RelativeLayout layout = (RelativeLayout) findViewById(R.id.layout);

        progressBar = (ProgressBar) findViewById(R.id.loading);
        bottomProgress = (ProgressBar) findViewById(R.id.bottom_progressbar);
        container_tools = (LinearLayout) findViewById(R.id.ll_container_tools);
        seekBar = (SeekBar) findViewById(R.id.seekbar);
        allTime = (TextView) findViewById(R.id.all_time);
        time = (TextView) findViewById(R.id.time);
        full = (ImageView) findViewById(R.id.full);
        sound = (ImageView) findViewById(R.id.sound);
        play = (ImageView) findViewById(R.id.player_btn);

        pauseImage = (ImageView) findViewById(R.id.pause_image);
        appVideoPlay = (RelativeLayout) findViewById(R.id.app_video_replay);
        appVideoPlay.setClickable(false);


        //status
        mVideoStaus = (LinearLayout) findViewById(R.id.app_video_status);
        mStatusText = (TextView) findViewById(R.id.app_video_status_text);

        //网络提示
        mVideoNetTie = (LinearLayout) findViewById(R.id.app_video_netTie);
        mVideoNetTieConfirm = (TextView) findViewById(R.id.app_video_netTie_confirm);
        mVideoNetTieCancel = (TextView) findViewById(R.id.app_video_netTie_cancel);
        mVideoDuration = (TextView) findViewById(R.id.app_video_network_duration);

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
        mVideoTitle = (MarqueeTextView) findViewById(R.id.tv_video_title);
        mVideoLock = (ImageView) findViewById(R.id.app_video_lock);
        mVideoShare = (ImageView) findViewById(R.id.app_video_share);
        initHeight = layout.getLayoutParams().height;
        screenWidthPixels = activity.getResources().getDisplayMetrics().widthPixels;

    }

//    /**
//     *
//     * 用此当布局的时候才用到其他情况用上面那中方法获取
//     * @param changed
//     * @param left
//     * @param top
//     * @param right
//     * @param bottom
//     */
//    @Override
//    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
//        super.onLayout(changed, left, top, right, bottom);
//        if (initHeight == 0) {
//            initHeight = getHeight();
//            screenWidthPixels = getResources().getDisplayMetrics().widthPixels;
//        }
//    }

    private void initAction() {
        sound.setOnClickListener(onClickListener);
        play.setOnClickListener(onClickListener);
        mVideoFinish.setOnClickListener(onClickListener);
        full.setOnClickListener(onClickListener);
        mVideoLock.setOnClickListener(onClickListener);
        mVideoShare.setOnClickListener(onClickListener);
//        pauseImage.setOnClickListener(onClickListener);
        mVideoNetTieConfirm.setOnClickListener(onClickListener);
        mVideoNetTieCancel.setOnClickListener(onClickListener);
        seekBar.setMax(1000);
        seekBar.setOnSeekBarChangeListener(mSeekListener);
        final GestureDetector detector = new GestureDetector(mContext, new PlayGestureListener());
        setKeepScreenOn(true);
        setClickable(true);

        container_tools.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                Log.e("custommedia", "event");
                Rect seekRect = new Rect();
                seekBar.getHitRect(seekRect);

                if ((event.getY() >= (seekRect.top - 50)) && (event.getY() <= (seekRect.bottom + 50))) {

                    float y = seekRect.top + seekRect.height() / 2;
                    //seekBar only accept relative x
                    float x = event.getX() - seekRect.left;
                    if (x < 0) {
                        x = 0;
                    } else if (x > seekRect.width()) {
                        x = seekRect.width();
                    } else {
                        MotionEvent me = MotionEvent.obtain(event.getDownTime(), event.getEventTime(),
                                event.getAction(), x, y, event.getMetaState());

                        return seekBar.onTouchEvent(me);
                    }

                }
                return false;
            }
        });

        setOnTouchListener(new OnTouchListener() {
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
    }

    private void initMediaPlayer() {

        try {
            IjkMediaPlayer.loadLibrariesOnce(null);
            IjkMediaPlayer.native_profileBegin("libijkplayer.so");
            playerSupport = true;
        } catch (Throwable e) {
            Log.e(TAG, "loadLibraries error", e);
        }
//
//        audioManager = (AudioManager) mContext.getSystemService(Context.AUDIO_SERVICE);
//        mMaxVolume = ((AudioManager) mContext.getSystemService(Context.AUDIO_SERVICE))
//                .getStreamMaxVolume(AudioManager.STREAM_MUSIC);


        audioManager = (AudioManager) PlayerApplication.getAppContext().getSystemService(Context.AUDIO_SERVICE);
        mMaxVolume = ((AudioManager) PlayerApplication.getAppContext().getSystemService(Context.AUDIO_SERVICE))
                .getStreamMaxVolume(AudioManager.STREAM_MUSIC);

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
//                if (getScreenOrientation((Activity) mContext)
//                        == ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE||getScreenOrientation((Activity) mContext) == ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE) {
//                    //横屏播放完毕，重置
//                    ((Activity) mContext).setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
//                    ViewGroup.LayoutParams layoutParams = mVideoView.getLayoutParams();
//                    layoutParams.height = ViewGroup.LayoutParams.MATCH_PARENT;
//                    layoutParams.width = ViewGroup.LayoutParams.MATCH_PARENT;
//                    mVideoView.setLayoutParams(layoutParams);
//                }
                if (completionListener != null)
                    completionListener.completion(mp);
            }
        });
        mVideoView.setOnPreparedListener(new IMediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(IMediaPlayer iMediaPlayer) {

            }
        });
        orientationEventListener = new OrientationEventListener(mContext) {
            @Override
            public void onOrientationChanged(int rotation) {
                // 设置竖屏
                if (((rotation >= 0) && (rotation <= 30)) || (rotation >= 330)) {
                    if (mClick) {
                        if (mIsLand && !mClickLand) {
                        } else {
                            mClickPort = true;
                            mClick = false;
                            mIsLand = false;
                        }
                    } else {
                        if (mIsLand && !isLock) {
                            activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                            mIsLand = false;
                            mClick = false;
                        }
                    }
                }
                // 设置横屏
                else if (((rotation >= 230) && (rotation <= 310)) || (rotation >= 60 && rotation <= 120)) {
                    if (mClick) {
                        if (!mIsLand && !mClickPort) {
                        } else {
                            mClickLand = true;
                            mClick = false;
                            mIsLand = true;
                        }
                    } else {
                        if (!mIsLand) {
                            if ((rotation >= 60 && rotation <= 120))
                                activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE);
                            else if (((rotation >= 230) && (rotation <= 310)))
                                activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
                            mIsLand = true;
                            mClick = false;
                        } else {
                            if ((rotation >= 60 && rotation <= 120))
                                activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE);
                            else if (((rotation >= 230) && (rotation <= 310)))
                                activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
                        }
                    }
                }
            }
        };
//        orientationEventListener.enable();
        portrait = getScreenOrientation(activity) == ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;
        hideAll();

        /**
         * 不支持此设备
         */
        if (!playerSupport) {
            showStatus(activity.getResources().getString(R.string.not_support));
        }


    }


    /**
     * 切换全屏
     */
    public void toggleFullScreen() {
        mClick = true;
        if (!mIsLand) {
            if (onClickOrientationListener != null) {
                onClickOrientationListener.landscape();
            }
            activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
            mIsLand = true;
            mClickLand = false;
        } else {
            if (onClickOrientationListener != null) {
                onClickOrientationListener.portrait();
            }
            if (!isLock) {
                activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                mIsLand = false;
                mClickPort = false;
            }
        }
        updateFullScreenButton();
    }


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
//                audioManager.setStreamMute(AudioManager.STREAM_MUSIC, true);
//                PlayerApplication.getInstance().isSound=true;
//                sound.setImageResource(R.mipmap.sound_mult_icon);
            }
        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
            if (!instantSeeking) {
                mVideoView.seekTo((int) ((duration * seekBar.getProgress() * 1.0) / 1000));
            }
            show(defaultTimeout);
            handler.removeMessages(PlayStateParams.MESSAGE_SHOW_PROGRESS);
//            audioManager.setStreamMute(AudioManager.STREAM_MUSIC, false);
//            PlayerApplication.getInstance().isSound=false;
//            sound.setImageResource(R.mipmap.sound_open_icon);
            isDragging = false;
            handler.sendEmptyMessageDelayed(PlayStateParams.MESSAGE_SHOW_PROGRESS, 1000);
        }
    };

    /**
     * 播放错误显示
     *
     * @param statusText
     */
    private void showStatus(String statusText) {
        mVideoStaus.setVisibility(View.VISIBLE);
        mStatusText.setText(statusText);

    }

    private void statusChange(int newStatus) {
        status = newStatus;
        if (newStatus == PlayStateParams.STATE_PLAYBACK_COMPLETED) {
            Log.d(TAG, "STATE_PLAYBACK_COMPLETED");
            orientationEventListener.disable();
            bottomProgress.setProgress(0);
            isShowContoller = false;
            hideAll();
            handler.removeMessages(PlayStateParams.MESSAGE_SHOW_PROGRESS);
            handler.removeCallbacksAndMessages(null);

        } else if (newStatus == PlayStateParams.STATE_ERROR) {
            orientationEventListener.disable();
            Log.d(TAG, "STATE_ERROR");
            bottomProgress.setProgress(0);
            isShowContoller = false;
            hideAll();
            showStatus(activity.getResources().getString(R.string.small_problem));
            handler.removeMessages(PlayStateParams.MESSAGE_SHOW_PROGRESS);
            handler.removeCallbacks(null);
        } else if (newStatus == PlayStateParams.STATE_PREPARING) {
            Log.d(TAG, "STATE_PREPARING");
            if (mVideoStaus.getVisibility() == View.VISIBLE)
                mVideoStaus.setVisibility(View.GONE);
            play.setVisibility(View.GONE);
            isShowContoller = false;
            if (progressBar.getVisibility() == View.GONE)
                progressBar.setVisibility(View.VISIBLE);
        } else if (newStatus == PlayStateParams.STATE_PLAYING) {
            Log.d(TAG, "STATE_PLAYING");
            progressBar.setVisibility(View.GONE);
            mVideoStaus.setVisibility(View.GONE);
            mVideoNetTie.setVisibility(View.GONE);
            isShowContoller = true;
            play.setVisibility(View.VISIBLE);
            handler.removeMessages(PlayStateParams.MESSAGE_SHOW_PROGRESS);
            handler.sendEmptyMessage(PlayStateParams.MESSAGE_SHOW_PROGRESS);
            if (firstPlay && !isAllowModible && MediaNetUtils.getNetworkType(mContext) == 6) {
////            showWifiDialog();
//////            Toast.makeText(mContext.getApplicationContext(),MediaNetUtils.getNetworkType(mContext)+"",Toast.LENGTH_LONG).show();
                mVideoDuration.setText(generateTimeSeconds(duration));
                mVideoNetTie.setVisibility(View.VISIBLE);
                firstPlay = false;
                onPause();

            }

        } else if (newStatus == PlayStateParams.STATE_PAUSED) {
            handler.removeMessages(PlayStateParams.SET_VIEW_HIDE);
            isShowContoller = false;
            showBottomControl(true);
        }


    }

//    /**
//     * 退出全屏
//     */
//    private void quitFullScreen() {
//        if (getScreenOrientation(activity) == ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE) {
//            activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
//        } else
//            activity.finish();
//    }


    /**
     * 展示控制面板
     *
     * @param show
     */
    public void setShowContollerbar(boolean show) {
        setVisibility(show ? View.VISIBLE : View.GONE);

    }

    private void hideAll() {
        top_box.setVisibility(View.GONE);
        showBottomControl(false);
        progressBar.setVisibility(View.GONE);
        appVideoPlay.setVisibility(View.GONE);
        mVideoStaus.setVisibility(View.GONE);
        mVideoNetTie.setVisibility(View.GONE);
    }

    public void showBottomControl(boolean show) {
        contollerbar.setVisibility(show ? View.VISIBLE : View.GONE);
        bottomProgress.setVisibility(show ? View.GONE : View.VISIBLE);
    }


    private void hide(boolean show) {
        if (!portrait)
            top_box.setVisibility(show ? View.VISIBLE : View.GONE);

        showBottomControl(show);
        bottomProgress.setVisibility(show ? View.GONE : View.VISIBLE);

    }

    private void hide() {
        Log.d(TAG, "hide");
        if (!isShowContoller)
            return;
        if (isShow) {
            isShow = false;
            handler.removeMessages(PlayStateParams.SET_VIEW_HIDE);
            handler.sendEmptyMessage(PlayStateParams.SET_VIEW_HIDE);
        }
    }


    private void show(int timeout) {
        if (!isShowContoller)
            return;
        if (!isShow)
            isShow = true;
        progressBar.setVisibility(View.GONE);
        play.setVisibility(View.VISIBLE);
        hide(true);
        handler.removeMessages(PlayStateParams.SET_VIEW_HIDE);
        if (timeout != 0) {
            handler.sendMessageDelayed(handler.obtainMessage(PlayStateParams.SET_VIEW_HIDE), timeout);
        }
    }

    private void show() {

    }

//
//    /**
//     * 屏幕旋转判定
//     *
//     * @param portrait
//     */
//    private void doOnConfigurationChanged(final boolean portrait) {
//
//        if (mVideoView != null) {
//            handler.post(new Runnable() {
//                @Override
//                public void run() {
//                    tryFullScreen(!portrait);
//                    ViewGroup.LayoutParams params = getLayoutParams();
//                    if (null == params)
//                        return;
//                    if (portrait) {
//                        top_box.setVisibility(View.GONE);
//                        Log.v(TAG, "initHeight" + initHeight);
//                        params.height = initHeight;
//                        setLayoutParams(params);
//                        requestLayout();
//                        Log.v(TAG, "initHeight" + MediaNetUtils.dip2px(activity, initHeight));
//
//                    } else {
//                        int heightPixels = activity.getResources().getDisplayMetrics().heightPixels;
//                        int widthPixels = activity.getResources().getDisplayMetrics().widthPixels;
//                        params.height = Math.min(heightPixels, widthPixels);
//                        setLayoutParams(params);
//                        requestLayout();
//                        Log.v(TAG, "initHeight" + 0);
//                    }
//                    updateFullScreenButton();
//                }
//            });
//
//        }
//    }

    /**
     * 屏幕旋转判定
     *
     * @param portrait
     */
    private void doOnConfigurationChanged(final boolean portrait) {

        if (mVideoView != null) {
//            handler.post(new Runnable() {
//                @Override
//                public void run() {
            tryFullScreen(!portrait);
            ViewGroup.LayoutParams params = getLayoutParams();
            if (null == params)
                return;
            if (portrait) {
                top_box.setVisibility(View.GONE);
                Log.v(TAG, "initHeight" + initHeight);
                params.height = initHeight;
                setLayoutParams(params);
                requestLayout();
                Log.v(TAG, "initHeight" + MediaNetUtils.dip2px(activity, initHeight));

            } else {
                params.height = ViewGroup.LayoutParams.MATCH_PARENT;
                params.width = ViewGroup.LayoutParams.MATCH_PARENT;
                setLayoutParams(params);
                requestLayout();
                Log.v(TAG, "initHeight" + 0);
            }
            updateFullScreenButton();
        }
//            });

//        }
    }

    private void tryFullScreen(boolean fullScreen) {
        if (activity instanceof AppCompatActivity) {
            ActionBar supportActionBar = ((AppCompatActivity) activity).getSupportActionBar();
            if (supportActionBar != null) {
                if (fullScreen) {
                    supportActionBar.setShowHideAnimationEnabled(false);
                    supportActionBar.hide();
                } else {
                    supportActionBar.setShowHideAnimationEnabled(false);
                    supportActionBar.show();
                }
            }
        }
        setFullScreen(fullScreen);
    }


    private void setFullScreen(boolean fullScreen) {
        if (activity != null) {
            WindowManager.LayoutParams attrs = activity.getWindow().getAttributes();
            if (fullScreen) {
                attrs.flags |= WindowManager.LayoutParams.FLAG_FULLSCREEN;
                activity.getWindow().setAttributes(attrs);
//                activity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
//                setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE |
//                        View.SYSTEM_UI_FLAG_FULLSCREEN |
//                        View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN |
//                        View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION |
//                        View.SYSTEM_UI_FLAG_HIDE_NAVIGATION |
//                        View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
            } else {
                attrs.flags &= (~WindowManager.LayoutParams.FLAG_FULLSCREEN);
                activity.getWindow().setAttributes(attrs);
//                activity.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);

//                setSystemUiVisibility(View.SYSTEM_UI_FLAG_VISIBLE);
            }
        }

    }


    /**
     * 更新全屏按钮
     */
    private void updateFullScreenButton() {
        if (getScreenOrientation((Activity) mContext) == ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE || getScreenOrientation((Activity) mContext) == ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE) {
            full.setImageResource(R.mipmap.ic_fullscreen_exit);
        } else {
            full.setImageResource(R.mipmap.ic_fullscreen);
        }
    }


    private int getScreenOrientation(Activity activity) {
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
        return hours > 0 ? String.format(Locale.getDefault(), "%02d:%02d:%02d", hours, minutes, seconds) : String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds);
    }


    private String generateTimeSeconds(long time) {
        int totalSeconds = (int) (time);
        int seconds = totalSeconds % 60;
        int minutes = (totalSeconds / 60) % 60;
        int hours = totalSeconds / 3600;
        return hours > 0 ? String.format(Locale.getDefault(), "%02d:%02d:%02d", hours, minutes, seconds) : String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds);
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
        if (seekBar != null) {
            String string = generateTime((long) (duration * seekBar.getProgress() * 1.0f / 1000));
            time.setText(string);
        }
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
//                return super.onDown(e);
//            }

            return getScreenOrientation((Activity) mContext) == ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE || getScreenOrientation((Activity) mContext) == ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE || super.onDown(e);
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
            audioManager.setStreamMute(AudioManager.STREAM_MUSIC, true);
            sound.setImageResource(R.mipmap.sound_mult_icon);
            PlayerApplication.getInstance().isSound = true;
        } else {
            sound.setImageResource(R.mipmap.sound_open_icon);
            audioManager.setStreamMute(AudioManager.STREAM_MUSIC, false);
            PlayerApplication.getInstance().isSound = false;
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

            mTvCurrent.setText(current + "/");
            mTvDuration.setText(allTime.getText());
            mProgressGesture.setProgress(duration <= 0 ? 0 : (int) (newPosition * 100 / duration));
        }
    }

    /**
     * 处理音量键，避免外部按音量键后导航栏和状态栏显示出来退不回去的状态
     *
     * @param keyCode
     * @return
     */
    public boolean handleVolumeKey(int keyCode) {
        if (keyCode == KeyEvent.KEYCODE_VOLUME_UP) {
            setVolume(true);
            return true;
        } else if (keyCode == KeyEvent.KEYCODE_VOLUME_DOWN) {
            setVolume(false);
            return true;
        } else {
            return false;
        }
    }

    /**
     * 递增或递减音量，量度按最大音量的 1/15
     *
     * @param isIncrease 递增或递减
     */
    private void setVolume(boolean isIncrease) {
        int curVolume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
        if (isIncrease) {
            curVolume += mMaxVolume / 15;
        } else {
            curVolume -= mMaxVolume / 15;
        }
        if (curVolume > mMaxVolume) {
            curVolume = mMaxVolume;
        } else if (curVolume < 0) {
            curVolume = 0;
            sound.setImageResource(R.mipmap.sound_mult_icon);
//            audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, 0, 0);
            audioManager.setStreamMute(AudioManager.STREAM_MUSIC, true);
            PlayerApplication.getInstance().isSound = true;

        } else {
            sound.setImageResource(R.mipmap.sound_open_icon);
            audioManager.setStreamMute(AudioManager.STREAM_MUSIC, false);
            PlayerApplication.getInstance().isSound = false;
        }
        // 变更声音
        audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, curVolume, 0);
        hide();
        // 变更进度条
        if (gestureTouch.getVisibility() == View.GONE) {
            gestureTouch.setVisibility(View.VISIBLE);
            gesture.setVisibility(View.GONE);
            mImageTip.setImageResource(R.mipmap.player_video_volume);
        }
        mProgressGesture.setProgress((curVolume * 100 / mMaxVolume));
        endGesture();
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
        int flag = Settings.System.getInt(mContext.getContentResolver(),
                Settings.System.ACCELEROMETER_ROTATION, 0);
        if (flag == 1)
            orientationEventListener.enable();
        bottomProgress.setProgress(0);
        mVideoStaus.setVisibility(View.GONE);
        mVideoNetTie.setVisibility(View.GONE);
        progressBar.setVisibility(View.VISIBLE);
        if (PlayerApplication.getInstance().isSound)
            sound.setImageResource(R.mipmap.sound_mult_icon);
        else
            sound.setImageResource(R.mipmap.sound_open_icon);
        hide(false);


    }

    private void doPauseResume() {
        if (status == PlayStateParams.STATE_PLAYBACK_COMPLETED) {
            appVideoPlay.setVisibility(View.GONE);
            mVideoView.seekTo(0);
            mVideoView.start();
            play.setSelected(true);
        } else if (mVideoView.isPlaying()) {
            statusChange(PlayStateParams.STATE_PAUSED);
            mVideoView.pause();
            play.setSelected(false);
//            bitmap = mVideoView.getBitmap();
//            if (bitmap != null) {
//                pauseImage.setImageBitmap(bitmap);
//                appVideoPlay.setVisibility(View.VISIBLE);
//            }
            snapshotsBitmap();

        } else {
            statusChange(PlayStateParams.STATE_PLAYING);
            releaseBitmap();
            mVideoView.start();
            play.setSelected(true);
            handler.sendMessageDelayed(handler.obtainMessage(PlayStateParams.SET_VIEW_HIDE), PlayStateParams.TIME_OUT);
        }
    }


    private void pause() {
        play.setSelected(false);
        mVideoView.pause();
        statusChange(PlayStateParams.STATE_PAUSED);
        bitmap = mVideoView.getBitmap();
        snapshotsBitmap();
    }

    private void reStart() {
        play.setSelected(true);
        mVideoView.start();
        statusChange(PlayStateParams.STATE_PLAYING);
        releaseBitmap();
    }

    public void snapshotsBitmap() {
        bitmap = mVideoView.getBitmap();
        if (bitmap != null) {
            pauseImage.setImageBitmap(bitmap);
            appVideoPlay.setVisibility(View.VISIBLE);
        }
    }

    public void releaseBitmap() {
        if (bitmap != null) {
            handler.sendEmptyMessageDelayed(PlayStateParams.PAUSE_IMAGE_HIDE, 100);
            bitmap.recycle();
            bitmap = null;
        }
    }

    //==========================对外提供方法==============================

    public void isOpenOrientation(boolean isOpen) {
        if (isOpen)
            orientationEventListener.enable();
        else
            orientationEventListener.disable();
    }

    public int getCurrentPosition() {

        return mVideoView.getCurrentPosition();
    }

    public boolean getAllowModible() {
        return isAllowModible;
    }

    public void setAllowModible(boolean isAllowModible) {
        this.isAllowModible = isAllowModible;
    }

    public boolean isPlay() {
        return mVideoView.isPlaying();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (getScreenOrientation(activity) == ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE || getScreenOrientation((Activity) mContext) == ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE) {

            if (!isLock) {
                mIsLand = false; // 是否是横屏
                mClick = false; // 是否点击
//                mClickLand = true; // 点击进入横屏
                mClickPort = false; // 点击进入竖屏
                activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                return true;
            }
            return true;
//            toggleFullScreen();

        }
        return super.onKeyDown(keyCode, event);
    }

    public boolean onBackPressed() {
        if (getScreenOrientation(activity) == ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE || getScreenOrientation(activity) == ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE) {
            Log.v(TAG, "onBackPressed" + mClick + "" + mIsLand + "" + mClickLand);
            if (!isLock) {
                mClick = true; // 是否点击
                mIsLand = false;
                mClickPort = false;
                activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                return true;
            }
            return true;

        }
        return false;
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
        bottomProgress.setProgress(0);
        seekBar.setProgress(0);
        status = PlayStateParams.STATE_IDLE;
    }

    public void release() {
        orientationEventListener.disable();
        if (mVideoView != null)
            mVideoView.release(true);
        bottomProgress.setProgress(0);
        seekBar.setProgress(0);
        if (bitmap != null) {
            bitmap.recycle();
            bitmap = null;
            appVideoPlay.setVisibility(View.GONE);
        }
        status = PlayStateParams.STATE_IDLE;
    }

    public int getStatus() {
        return status;
    }

    public void setPauseImageHide() {
        appVideoPlay.setVisibility(View.GONE);
    }

    public void onDestory() {
        unregisterNetReceiver();
        handler.removeCallbacksAndMessages(null);
        mVideoView.stopPlayback();
    }

    public void onResume() {
//        orientationEventListener.enable();
        if (status == PlayStateParams.STATE_PAUSED) {
            if (isAutoPause) {
//                if (currentPosition > 0) {
//                    mVideoView.seekTo((int) currentPosition);
//                }
                mVideoView.start();
                play.setSelected(true);
                isAutoPause = false;
                releaseBitmap();
                statusChange(PlayStateParams.STATE_PLAYING);
            }
        }
    }

    public void onPause() {
//        orientationEventListener.disable();
        //把系统状态栏显示出来
        if (status == PlayStateParams.STATE_PLAYING) {
            mVideoView.pause();
            play.setSelected(false);
            currentPosition = mVideoView.getCurrentPosition();
            isAutoPause = true;
            snapshotsBitmap();
            statusChange(PlayStateParams.STATE_PAUSED);
        }
    }


    public void setShowContoller(boolean isShowContoller) {
        this.isShowContoller = isShowContoller;
        handler.removeMessages(PlayStateParams.SET_VIEW_HIDE);
        showBottomControl(isShowContoller);
    }

    public void play(String url) {
        this.url = url;
        firstPlay = true;
        play(url, 0);
    }

    public void play(String url, int position) {
        this.url = url;
        if (PlayerApplication.getInstance().isSound)
            sound.setImageResource(R.mipmap.sound_mult_icon);
        else
            sound.setImageResource(R.mipmap.sound_open_icon);
        status = PlayStateParams.STATE_PREPARE;
        if (!isNetListener) {// 如果设置不监听网络的变化，则取消监听网络变化的广播
            unregisterNetReceiver();
        } else {
            // 注册网路变化的监听
            registerNetReceiver();
        }
//        if (!isAllowModible && MediaNetUtils.getNetworkType(mContext) == 6) {
////            showWifiDialog();
//////            Toast.makeText(mContext.getApplicationContext(),MediaNetUtils.getNetworkType(mContext)+"",Toast.LENGTH_LONG).show();
////            mVideoDuration.setText(generateTimeSeconds(duration));
////            mVideoNetTie.setVisibility(View.VISIBLE);
//        } else {
        if (playerSupport) {
            start();
            progressBar.setVisibility(View.VISIBLE);
            releaseBitmap();
            mVideoView.setVideoPath(url);
            mVideoView.seekTo(position);
            mVideoView.start();
            play.setSelected(true);
        }
//        }

    }


    public void start(String path) {
        Uri uri = Uri.parse(path);
        status = PlayStateParams.STATE_PREPARE;
        start();
        if (!mVideoView.isPlaying()) {
            mVideoView.setVideoURI(uri);
            mVideoView.start();
        } else {
            mVideoView.stopPlayback();
            mVideoView.setVideoURI(uri);
            mVideoView.start();
        }
        play.setSelected(true);

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

    public void setDuration(long duration) {
        this.duration = duration;
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

    public interface OnClickOrientationListener {
        void landscape();

        void portrait();
    }

    /**
     * 获得某个控件
     *
     * @param ViewId
     * @return
     */
    public View getView(int ViewId) {
        return activity.findViewById(ViewId);
    }

    /**
     * 注册网络监听器
     */
    private void registerNetReceiver() {
        if (changeReceiver == null) {
            IntentFilter filter = new IntentFilter(
                    ConnectivityManager.CONNECTIVITY_ACTION);
            changeReceiver = new NetChangeReceiver();
            mContext.registerReceiver(changeReceiver, filter);
        }
    }

    /**
     * 销毁网络监听器
     */
    private void unregisterNetReceiver() {
        if (changeReceiver != null) {
            mContext.unregisterReceiver(changeReceiver);
            changeReceiver = null;
        }
    }

    private class NetChangeReceiver extends BroadcastReceiver {
        private final String TAG = NetChangeReceiver.class.getSimpleName();

        @Override
        public void onReceive(Context context, Intent intent) {
            Log.e(TAG, "网络状态改变");
            if (MediaNetUtils.getNetworkType(activity) == 3) {// 网络是WIFI
//                onNetChangeListener.onWifi();
            } else if (mVideoView.isPlaying() && !isAllowModible && MediaNetUtils.getNetworkType(activity) == 6
                    ) {// 网络不是手机网络或者是以太网
                // TODO 更新状态是暂停状态

                currentPosition = mVideoView.getCurrentPosition();
                progressBar.setVisibility(View.GONE);
                onPause();
                show(0);
//                onNetChangeListener.onMobile();
//                showWifiDialog();
                mVideoDuration.setText(generateTimeSeconds(duration));
                mVideoNetTie.setVisibility(View.VISIBLE);

            } else if (MediaNetUtils.getNetworkType(activity) == 1) {// 网络链接断开
//                Toast.makeText(mContext, "网路已断开", Toast.LENGTH_SHORT).show();
                onPause();
//                onNetChangeListener.onDisConnect();
            } else {
                onPause();
//                Toast.makeText(mContext, "未知网络", Toast.LENGTH_SHORT).show();
//                onNetChangeListener.onNoAvailable();
            }


        }

    }

    //============================外部接口====================================
    private OnShareListener onShareListener;

    public interface OnShareListener {
        void onShare();

        void onPlayCancel();
    }

    public void setOnShareListener(OnShareListener onShareListener) {
        this.onShareListener = onShareListener;
    }

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
