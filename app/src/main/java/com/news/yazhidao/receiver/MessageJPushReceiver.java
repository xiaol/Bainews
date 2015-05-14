package com.news.yazhidao.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.news.yazhidao.net.request.UploadJpushidRequest;
import com.news.yazhidao.utils.Logger;

import cn.jpush.android.api.JPushInterface;

/**
 * Created by fengjigang on 15/5/14.
 */
public class MessageJPushReceiver extends BroadcastReceiver {
    private static final String TAG = "MessageJPushReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {
        String _Action = intent.getAction();
        Bundle bundle=intent.getExtras();
        if (JPushInterface.ACTION_REGISTRATION_ID.equals(_Action)) {
            String jpushId = bundle.getString(JPushInterface.EXTRA_REGISTRATION_ID);
            Logger.i("jigang", "----title---" + jpushId);
            UploadJpushidRequest.uploadJpushId(jpushId);
        }else if(JPushInterface.ACTION_MESSAGE_RECEIVED.equals(_Action)){
            String title = bundle.getString(JPushInterface.EXTRA_TITLE);
            String message = bundle.getString(JPushInterface.EXTRA_MESSAGE);
            String extras = bundle.getString(JPushInterface.EXTRA_EXTRA);
            String type = bundle.getString(JPushInterface.EXTRA_CONTENT_TYPE);
            String file = bundle.getString(JPushInterface.EXTRA_RICHPUSH_FILE_PATH);

            Logger.i("jigang","receive custom title="+title);
            Logger.i("jigang","receive custom message="+message);
            Logger.i("jigang","receive custom extras="+extras);
            Logger.i("jigang","receive custom type="+type);
            Logger.i("jigang","receive custom file="+file);
        }
    }
}
