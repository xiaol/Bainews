package com.news.yazhidao.utils;

import android.content.Context;
import android.content.res.Resources;
import android.os.Handler;
import android.os.Looper;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.news.yazhidao.R;
import com.news.yazhidao.application.YaZhiDaoApplication;

/**
 * Created by fengjigang on 15/2/4.
 */
public class ToastUtil {
    public static void toastLong(String text) {
        Toast.makeText(YaZhiDaoApplication.getAppContext(), text, Toast.LENGTH_LONG).show();
    }

    public static void toastLong(int resId) {
        Resources res = YaZhiDaoApplication.getAppContext().getResources();
        Toast.makeText(YaZhiDaoApplication.getAppContext(), res.getString(resId), Toast.LENGTH_LONG).show();
    }

    public static void toastShort(int resId) {
        Resources res = YaZhiDaoApplication.getAppContext().getResources();
        Toast.makeText(YaZhiDaoApplication.getAppContext(), res.getString(resId), Toast.LENGTH_SHORT).show();
    }

    public static void toastShort(String text) {
        Toast.makeText(YaZhiDaoApplication.getAppContext(), text, Toast.LENGTH_SHORT).show();
    }

    public static void showToastWithIcon(final String text, final int iconResId) {
        if (Looper.getMainLooper() != Looper.myLooper()) {
            return;
        }

        new Handler().post(new Runnable() {
            @Override
            public void run() {
                if (iconResId > 0) {

                    Toast toast = Toast.makeText(YaZhiDaoApplication.getAppContext(), "\t" + text, Toast.LENGTH_SHORT);
                    LinearLayout toastView = (LinearLayout) toast.getView();
//					toastView.setAlpha(.7f);
//					toastView.getBackground().setAlpha(10);
                    toastView.setGravity(Gravity.CENTER_VERTICAL);
                    toastView.setOrientation(LinearLayout.HORIZONTAL);
                    ImageView iconView = new ImageView(YaZhiDaoApplication.getAppContext());
                    iconView.setImageResource(iconResId);
                    toastView.addView(iconView, 0);
                    toast.show();
                } else {
                    Toast.makeText(YaZhiDaoApplication.getAppContext(), text, Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    /**
     * 显示减少此类型推荐
     */
    public static void showReduceRecommendToast(final Context mContext) {
        if (Looper.getMainLooper() != Looper.myLooper()) {
            return;
        }
        new Handler().post(new Runnable() {
            @Override
            public void run() {
                Toast toast = Toast.makeText(mContext, "\t" + "将减少此类推荐", Toast.LENGTH_SHORT);
                toast.setView(LayoutInflater.from(mContext).inflate(R.layout.reduce_recommend_layout,null));
//                LinearLayout toastView = (LinearLayout) toast.getView();
//                toastView.setGravity(Gravity.CENTER_VERTICAL);
//                toastView.setBackgroundResource(R.drawable.bg_reduce_recommend);
//                toastView.setOrientation(LinearLayout.HORIZONTAL);
//                ImageView iconView = new ImageView(YaZhiDaoApplication.getAppContext());
//                iconView.setImageResource(R.drawable.ic_reduce_recommend);
//                toastView.addView(iconView, 0);
                toast.setGravity(Gravity.CENTER, 0, 0);
                toast.show();

            }
        });

    }

    public static void showAttentionSuccessToast(final Context mContext) {
        if (Looper.getMainLooper() != Looper.myLooper()) {
            return;
        }
        new Handler().post(new Runnable() {
            @Override
            public void run() {
                Toast toast = Toast.makeText(mContext, "\t" + "关注成功", Toast.LENGTH_SHORT);
                toast.setView(LayoutInflater.from(mContext).inflate(R.layout.attention_success_layout,null));
                toast.setGravity(Gravity.CENTER, 0, 0);
                toast.show();
            }
        });

    }
}
