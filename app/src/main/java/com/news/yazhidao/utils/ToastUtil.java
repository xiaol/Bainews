package com.news.yazhidao.utils;

import android.content.res.Resources;
import android.os.Handler;
import android.os.Looper;
import android.view.Gravity;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

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
        if(Looper.getMainLooper() != Looper.myLooper()){
            return;
        }

        new Handler().post(new Runnable() {
            @Override
            public void run() {
                if(iconResId > 0){

                    Toast toast = Toast.makeText(YaZhiDaoApplication.getAppContext(), "\t"+ text, Toast.LENGTH_SHORT);
                    LinearLayout toastView = (LinearLayout) toast.getView();
//					toastView.setAlpha(.7f);
//					toastView.getBackground().setAlpha(10);
                    toastView.setGravity(Gravity.CENTER_VERTICAL);
                    toastView.setOrientation(LinearLayout.HORIZONTAL);
                    ImageView iconView = new ImageView(YaZhiDaoApplication.getAppContext());
                    iconView.setImageResource(iconResId);
                    toastView.addView(iconView, 0);
                    toast.show();
                }else{
                    Toast.makeText(YaZhiDaoApplication.getAppContext(), text, Toast.LENGTH_SHORT).show();
                }
            }
        });

    }
}
