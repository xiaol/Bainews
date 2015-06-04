package com.news.yazhidao.widget;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;

import com.news.yazhidao.R;
import com.news.yazhidao.entity.TimeFeed;
import com.news.yazhidao.utils.ToastUtil;
import com.news.yazhidao.widget.InputBar.InputBar;
import com.news.yazhidao.widget.InputBar.InputBarDelegate;
import com.news.yazhidao.widget.InputBar.InputBarType;

import java.util.ArrayList;


//m_ppopupWindow = new TSHeadPortraitPopupWindow(m_pActivity, m_pUserData.getM_strPhotoUrl());
//        m_ppopupWindow.setAnimationStyle(R.style.AnimationAlpha);
//        m_ppopupWindow.showAtLocation(m_pActivity.getWindow().getDecorView(), Gravity.CENTER
//        | Gravity.CENTER, 0, 0);

/**
 * Created by h.yuan on 2015/3/23.
 */
public class CommentPopupWindow extends PopupWindow implements InputBarDelegate, Handler.Callback {

    private ImageView mivClose;
    private View mMenuView;
    private Activity m_pContext;
    //    private Context m_pContext;
    private ListView mlvComment;
    private DateAdapter mCommentAdapter;
    private InputBar mInputBar;
    private RelativeLayout mrlRecord;
    private Handler mHandler;
    private double mRecordVolume;// 麦克风获取的音量值
    private TextViewExtend mtvVoiceTips, mtvVoiceTimes;
    private ImageView mivRecord;

    public CommentPopupWindow(Activity context) {
        super(context);
        m_pContext = context;
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mMenuView = inflater.inflate(R.layout.popup_window_comment, null);
        mCommentAdapter = new DateAdapter(m_pContext);
        mHandler = new Handler(this);
        findHeadPortraitImageViews();
        loadData();
    }

    private void findHeadPortraitImageViews() {
        //录音动画
        mrlRecord = (RelativeLayout) mMenuView.findViewById(R.id.voice_record_layout_wins);
        mtvVoiceTips = (TextViewExtend) mMenuView.findViewById(R.id.tv_voice_tips);
        mtvVoiceTimes = (TextViewExtend) mMenuView.findViewById(R.id.voice_record_time);
        mivRecord = (ImageView) mMenuView.findViewById(R.id.iv_record);
        //输入框
        mInputBar = (InputBar) mMenuView.findViewById(R.id.input_bar_view);
        mInputBar.setActivityAndHandler(m_pContext, mHandler);
        mInputBar.setDelegate(this);

        mivClose = (ImageView) mMenuView.findViewById(R.id.close_imageView);
        mlvComment = (ListView) mMenuView.findViewById(R.id.comment_list_view);
        mlvComment.setAdapter(mCommentAdapter);
        //设置SelectPicPopupWindow的View
        this.setContentView(mMenuView);
        //设置SelectPicPopupWindow弹出窗体的宽
        this.setWidth(ViewGroup.LayoutParams.MATCH_PARENT);
        //设置SelectPicPopupWindow弹出窗体的高
        this.setHeight(ViewGroup.LayoutParams.MATCH_PARENT);
        //设置SelectPicPopupWindow弹出窗体可点击
        this.setFocusable(true);
        //设置SelectPicPopupWindow弹出窗体动画效果
//        this.setAnimationStyle(R.style.DialogAnimation);
        //实例化一个ColorDrawable颜色为半透明
        ColorDrawable dw = new ColorDrawable(0xb0000000);
        //设置SelectPicPopupWindow弹出窗体的背景
        this.setBackgroundDrawable(dw);
        //mMenuView添加OnTouchListener监听判断获取触屏位置如果在选择框外面则销毁弹出框
//        mMenuView.setOnTouchListener(new OnTouchListener() {
//
//            public boolean onTouch(View v, MotionEvent event) {
//                if (event.getAction() == MotionEvent.ACTION_UP) {
//                    dismiss();
//                }
//                return true;
//            }
//        });
        mivClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
    }


    private void loadData() {

    }

    @Override
    public void submitThisMessage(InputBarType argType, String argContent) {
        mrlRecord.setVisibility(View.INVISIBLE);
    }

    @Override
    public void recordDidBegin(InputBar argView) {
        mtvVoiceTips.setText("手指上滑,取消发送");
        mtvVoiceTips.setTextColor(Color.WHITE);
        mrlRecord.setVisibility(View.VISIBLE);
    }

    @Override
    public void recordDidCancel(InputBar argView) {
        mrlRecord.setVisibility(View.INVISIBLE);
    }

    @Override
    public void cancelVoiceTipsType1() {
        mtvVoiceTips.setText("松开手指，取消发送");
        mtvVoiceTips.setTextColor(Color.RED);
    }

    @Override
    public void cancelVoiceTipsType2() {
        mtvVoiceTips.setText("手指上滑,取消发送");
        mtvVoiceTips.setTextColor(Color.WHITE);
    }

    @Override
    public boolean handleMessage(Message msg) {
        switch (msg.what) {
            case InputBar.RECORD_NO:// 不在录音
                if (mInputBar.getRecordState() == InputBar.RECORD_ING) {
                    // 停止动画效果
                    // stopRecordLightAnimation();
                    // 修改录音状态
//                        mRecordState = RECORD_ED;

                    // 停止录音
                    mInputBar.finishRecord();
                    // 初始化录音音量
                    mRecordVolume = 0;
                    // 话筒隐藏
                    mrlRecord.setVisibility(View.INVISIBLE);
                    // 录音达到最大时长，结束录音并发送语音
                    ToastUtil.toastShort("录音时间不能超过30秒");
                }

                break;
            case InputBar.RECORD_ING:// 正在录音
                // 显示录音时间
                mtvVoiceTimes.setText((int) mInputBar.getCurDuration() + "/" + InputBar.MAX_TIME + "″");
                // 音量大小的动画
                mRecordVolume = mInputBar.getRecordVolume();
                if (mInputBar.isLong()) {
                    mivRecord.setBackgroundResource(R.drawable.voice_cancle);
                } else if (mRecordVolume < 500.0) {
                    mivRecord.setBackgroundResource(R.drawable.voice_1);
                } else if (mRecordVolume >= 500.0 && mRecordVolume < 2000) {
                    mivRecord.setBackgroundResource(R.drawable.voice_2);
                } else if (mRecordVolume >= 2000.0 && mRecordVolume < 8000) {
                    mivRecord.setBackgroundResource(R.drawable.voice_3);
                } else if (mRecordVolume >= 8000.0) {
                    mivRecord.setBackgroundResource(R.drawable.voice_4);
                }
                break;
        }
        return false;
    }

    class DateAdapter extends BaseAdapter {

        Context mContext;
        ArrayList<String> marrStrHistoryDate;

        DateAdapter(Context context) {
            mContext = context;
        }

        public void setData(TimeFeed timeFeed) {
            if (timeFeed != null)
                marrStrHistoryDate = timeFeed.getHistory_date();
        }

        @Override
        public int getCount() {
            return marrStrHistoryDate == null ? 0 : marrStrHistoryDate.size();
        }

        @Override
        public Object getItem(int position) {
            return position;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            final Holder holder;
            if (convertView == null) {
                holder = new Holder();
//                if (Integer.valueOf(mCurrentTimeFeed.getNext_update_type()) == 0)
//                    convertView = LayoutInflater.from(mContext).inflate(R.layout.adapter_list_date1, null, false);
//                else
//                    convertView = LayoutInflater.from(mContext).inflate(R.layout.adapter_list_date2, null, false);
//                holder.tvDate = (TextViewExtend) convertView.findViewById(R.id.tv_date);
//                holder.ivMorning = (ImageView) convertView.findViewById(R.id.iv_date_morning);
//                holder.ivNight = (ImageView) convertView.findViewById(R.id.iv_date_night);
//                convertView.setTag(holder);
            } else {
                holder = (Holder) convertView.getTag();
            }
            return convertView;
        }
    }


    class Holder {
        TextViewExtend tvDate;
        ImageView ivMorning;
        ImageView ivNight;
    }
}