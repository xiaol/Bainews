package com.news.yazhidao.widget;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.PopupWindow;

import com.news.yazhidao.R;
import com.news.yazhidao.entity.NewsDetail;
import com.news.yazhidao.utils.FastBlur;
import com.news.yazhidao.utils.ImageUtils;

import java.util.ArrayList;


//m_ppopupWindow = new TSHeadPortraitPopupWindow(m_pActivity, m_pUserData.getM_strPhotoUrl());
//        m_ppopupWindow.setAnimationStyle(R.style.AnimationAlpha);
//        m_ppopupWindow.showAtLocation(m_pActivity.getWindow().getDecorView(), Gravity.CENTER
//        | Gravity.CENTER, 0, 0);

/**
 * Created by h.yuan on 2015/3/23.
 */
public class TimePopupWindow extends PopupWindow implements Handler.Callback {

    private TextViewExtend mtvHour, mtvMin, mtvSec;
    private ImageView mivBg, mivStatus;
    private View mMenuView;
    private Activity m_pContext;
    private Context mContext;
    private Handler mHandler;
    private RoundedProgressBar mrpbTime;
    private HorizontalListView mhlvDate;
    private DateAdapter mDateAdapter;
    private int miCurrentProgress,miTotalProgress;

    public TimePopupWindow(Activity context, Bitmap bitmap) {
        super(context);
        m_pContext = context;
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mMenuView = inflater.inflate(R.layout.popup_window_time, null);
        mHandler = new Handler(this);
        mDateAdapter = new DateAdapter(m_pContext);
        findHeadPortraitImageViews(bitmap);
    }

    private void findHeadPortraitImageViews(Bitmap bitmap) {
        mrpbTime = (RoundedProgressBar) mMenuView.findViewById(R.id.progress_circle);
        mivBg = (ImageView) mMenuView.findViewById(R.id.bg_imageView);
        blur(bitmap, mivBg);
        mivStatus = (ImageView) mMenuView.findViewById(R.id.iv_date_status);
        mtvHour = (TextViewExtend) mMenuView.findViewById(R.id.tv_hour_num);
        mtvMin = (TextViewExtend) mMenuView.findViewById(R.id.tv_min_num);
        mtvSec = (TextViewExtend) mMenuView.findViewById(R.id.tv_sec_num);

        mhlvDate = (HorizontalListView) mMenuView.findViewById(R.id.lv_date);
        mDateAdapter.notifyDataSetChanged();
        mhlvDate.setAdapter(mDateAdapter);
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                mHandler.sendEmptyMessage(0);
            }
        },1000);

        new CountDownTimer(64000, 1000) {
            public void onTick(long millisUntilFinished) {
                int ss = 1000;
                int mi = ss * 60;
                int hh = mi * 60;
                int dd = hh * 24;

                long day = millisUntilFinished / dd;
                long hour = (millisUntilFinished - day * dd) / hh;
                long minute = (millisUntilFinished - day * dd - hour * hh) / mi;
                long second = (millisUntilFinished - day * dd - hour * hh - minute * mi) / ss;
                long milliSecond = millisUntilFinished - day * dd - hour * hh - minute * mi - second * ss;

                String strDay = day < 10 ? "0" + day : "" + day; //天
                String strHour = hour < 10 ? "0" + hour : "" + hour;//小时
                String strMinute = minute < 10 ? "0" + minute : "" + minute;//分钟
                String strSecond = second < 10 ? "0" + second : "" + second;//秒
                String strMilliSecond = milliSecond < 10 ? "0" + milliSecond : "" + milliSecond;//毫秒
                strMilliSecond = milliSecond < 100 ? "0" + strMilliSecond : "" + strMilliSecond;

                mtvHour.setText("" + strHour);
                mtvMin.setText("" + strMinute);
                mtvSec.setText("" + strSecond);
            }

            public void onFinish() {

            }
        }.start();

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
    }

    private void blur(Bitmap bkg, View view) {
        long startMs = System.currentTimeMillis();
        float scaleFactor = 15;
        float radius = 1;
        int width = m_pContext.getWindowManager().getDefaultDisplay().getWidth();
        int height = m_pContext.getWindowManager().getDefaultDisplay()
                .getHeight();
        Bitmap overlay = Bitmap.createBitmap((int) (width / scaleFactor),
                (int) (height / scaleFactor), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(overlay);
        canvas.translate(-2 / scaleFactor, 0);
        canvas.scale(1 / scaleFactor, 1 / scaleFactor);
        Paint paint = new Paint();
        paint.setFlags(Paint.ANTI_ALIAS_FLAG);//消除锯齿
        if (canvas != null && bkg != null && paint != null) {
            canvas.drawBitmap(bkg, 0, 0, paint);
            canvas.drawColor(new Color().parseColor("#99FFFFFF"));
        }

        overlay = FastBlur.doBlur(overlay, (int) radius, true);
        overlay = ImageUtils.getRoundedCornerBitmap(m_pContext, overlay, 1, false, false, false, true);
        view.setBackgroundDrawable(new BitmapDrawable(m_pContext.getResources(), overlay));

        if (overlay != null) {
            overlay = null;
        }
        Log.e("xxxx", System.currentTimeMillis() - startMs + "ms");
    }
    int porgress=0;
    @Override
    public boolean handleMessage(Message msg) {
        int i = mrpbTime.getProgress();
        if(i<30){
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    porgress = porgress+5;
                    mrpbTime.setProgress(porgress);
                    mHandler.sendEmptyMessage(porgress);
                }
            },100);
        }
        return false;
    }


    class DateAdapter extends BaseAdapter {

        Context mContext;
        ArrayList<NewsDetail.Relate> mArrData;

        DateAdapter(Context context) {
            mContext = context;
        }

        public void setData(ArrayList<NewsDetail.Relate> pArrData) {
            mArrData = pArrData;
        }

        @Override
        public int getCount() {
            return 5;
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
                convertView = LayoutInflater.from(mContext).inflate(R.layout.adapter_list_date, null, false);
                holder.tvDate = (TextViewExtend) convertView.findViewById(R.id.tv_date);
                holder.ivMorning = (ImageView) convertView.findViewById(R.id.iv_date_morning);
                holder.ivNight = (ImageView) convertView.findViewById(R.id.iv_date_night);
                convertView.setTag(holder);
            } else {
                holder = (Holder) convertView.getTag();
            }
            if (position == 0) {
                holder.ivMorning.setPressed(true);
            } else {
                holder.ivMorning.setPressed(false);
            }
            holder.tvDate.setText("4月21日");
            holder.ivMorning.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                }
            });
            holder.ivNight.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                }
            });
            return convertView;
        }
    }


    class Holder {
        TextViewExtend tvDate;
        ImageView ivMorning;
        ImageView ivNight;
    }
}
