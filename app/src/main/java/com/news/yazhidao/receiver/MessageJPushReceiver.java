package com.news.yazhidao.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;

import com.news.yazhidao.net.request.UploadJpushidRequest;
import com.news.yazhidao.pages.ChatAty;
import com.news.yazhidao.pages.FeedBackActivity;
import com.news.yazhidao.pages.HomeAty;
import com.news.yazhidao.pages.NewsDetailAty;
import com.news.yazhidao.pages.NewsFeedFragment;
import com.news.yazhidao.utils.DeviceInfoUtil;
import com.news.yazhidao.utils.Logger;
import com.news.yazhidao.utils.helper.NotificationHelper;
import com.umeng.update.UmengUpdateAgent;

import org.json.JSONException;
import org.json.JSONObject;

import cn.jpush.android.api.JPushInterface;

/**
 * Created by fengjigang on 15/5/14.
 */
public class MessageJPushReceiver extends BroadcastReceiver {
    private static final String TAG = "MessageJPushReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {
        String _Action = intent.getAction();
        Bundle bundle = intent.getExtras();
        if (JPushInterface.ACTION_REGISTRATION_ID.equals(_Action)) {
            String jpushId = bundle.getString(JPushInterface.EXTRA_REGISTRATION_ID);
            Logger.i("jigang", "----title---" + jpushId);
            UploadJpushidRequest.uploadJpushId(jpushId);
        } else if (JPushInterface.ACTION_MESSAGE_RECEIVED.equals(_Action)) {
            String title = bundle.getString(JPushInterface.EXTRA_TITLE);
            String message = bundle.getString(JPushInterface.EXTRA_MESSAGE);
            String extras = bundle.getString(JPushInterface.EXTRA_EXTRA);
            String type = bundle.getString(JPushInterface.EXTRA_CONTENT_TYPE);
            String file = bundle.getString(JPushInterface.EXTRA_RICHPUSH_FILE_PATH);
            //判断反馈界面是否在前台
            boolean isFeedBackForeground = DeviceInfoUtil.isRunningForeground(context, FeedBackActivity.class.getSimpleName());
            //判断会话列表是否在前台
            //TODO 修改当前传入的activity name
            boolean isMessageListForeground = DeviceInfoUtil.isRunningForeground(context, ChatAty.class.getSimpleName());
            Intent intent1;
            if (isFeedBackForeground) {
                intent1 = new Intent("FeedBackMessage");
                intent1.putExtra("message", message);
                context.sendBroadcast(intent1);
            } else if (isMessageListForeground) {
                intent1 = new Intent("FeedBackMessageList");
                intent1.putExtra("message", message);
                context.sendBroadcast(intent1);
            } else {
                intent1 = new Intent(context, FeedBackActivity.class);
                NotificationHelper.sendNotification(context, "测试title", message, intent1);
            }
            Logger.i("jigang", "receive custom title=" + title);
            Logger.i("jigang", "receive custom message=" + message);
            Logger.i("jigang", "receive custom extras=" + extras);
            Logger.i("jigang", "receive custom type=" + type);
            Logger.i("jigang", "receive custom file=" + file);
        } else {
            if (JPushInterface.ACTION_NOTIFICATION_OPENED.equals(_Action)) {
                String extras = bundle.getString(JPushInterface.EXTRA_EXTRA);
                Logger.i("jigang", "receive custom extras=" + extras);
                //此处对传过来的json字符串做处理 {"news_url":"www.baidu.com"}
                if (extras == null) {
                    Intent HomeIntent = new Intent(context, HomeAty.class);
                    HomeIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(HomeIntent);
                } else {
                    try {
                        JSONObject urlObject = new JSONObject(extras);
                        String news_url = urlObject.getString("news_url");
                        if (!TextUtils.isEmpty(news_url)) {
                            Intent detailIntent = new Intent(context, NewsDetailAty.class);
                            detailIntent.putExtra(NewsFeedFragment.KEY_URL, news_url);
                            detailIntent.putExtra(NewsFeedFragment.KEY_NEWS_SOURCE, NewsFeedFragment.VALUE_NEWS_NOTIFICATION);
                            detailIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            context.startActivity(detailIntent);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    try {
                        JSONObject versionObject = new JSONObject(extras);
                        String new_version = versionObject.getString("new_version");
                        if (!TextUtils.isEmpty(new_version)) {
                            UmengUpdateAgent.silentUpdate(context);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }
}
