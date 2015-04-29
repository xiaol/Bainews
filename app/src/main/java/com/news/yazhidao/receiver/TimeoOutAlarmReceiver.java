package com.news.yazhidao.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.news.yazhidao.pages.TimeOutAlarmUpdateListener;

/**
 * Created by fiocca on 15/4/29.
 */
public class TimeoOutAlarmReceiver extends BroadcastReceiver {
    private TimeOutAlarmUpdateListener mListener;
    public void setListener(TimeOutAlarmUpdateListener listener){
        this.mListener=listener;
    }
    @Override
    public void onReceive(Context context, Intent intent) {
        if(mListener!=null){
            mListener.updateUI(intent);
        }
    }
}
