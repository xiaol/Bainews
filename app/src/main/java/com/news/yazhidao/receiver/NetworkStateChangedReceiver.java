package com.news.yazhidao.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;

import com.news.yazhidao.utils.DeviceInfoUtil;
import com.news.yazhidao.utils.Logger;

/**
 * Created by fengjigang on 15/8/11.
 * 监听网络状态变化
 */
public class NetworkStateChangedReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if(ConnectivityManager.CONNECTIVITY_ACTION.equals(intent.getAction())){
            Logger.e("jigang","-----网络状态发生变化了~~");
            if(DeviceInfoUtil.isNetworkConnected(context)){
                Logger.e("jigang","-----当前手机有网络~~");
                if(DeviceInfoUtil.isWifiConnected(context)){
                    Logger.e("jigang","-----当前手机是wifi网络~~");
                }
            }
        }
    }
}
