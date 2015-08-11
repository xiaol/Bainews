package com.news.yazhidao.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * Created by fengjigang on 15/5/14.
 */
public class MessageJPushReceiver extends BroadcastReceiver {
    private static final String TAG = "MessageJPushReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {
        /**
        String _Action = intent.getAction();
        Bundle bundle = intent.getExtras();
        if (JPushInterface.ACTION_REGISTRATION_ID.equals(_Action)) {
            String device_token = UmengRegistrar.getRegistrationId(context);
            UploadJpushidRequest.uploadJpushId(context,jpushId);
        } else if (JPushInterface.ACTION_MESSAGE_RECEIVED.equals(_Action)) {
            String title = bundle.getString(JPushInterface.EXTRA_TITLE);
            String message = bundle.getString(JPushInterface.EXTRA_MESSAGE);
            String extras = bundle.getString(JPushInterface.EXTRA_EXTRA);
            String type = bundle.getString(JPushInterface.EXTRA_CONTENT_TYPE);
            String file = bundle.getString(JPushInterface.EXTRA_RICHPUSH_FILE_PATH);
            //判断反馈界面是否在前台
            boolean isFeedBackForeground = DeviceInfoUtil.isRunningForeground(context, FeedBackActivity.class.getSimpleName());
            //判断会话列表是否在前台
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
        }else if(JPushInterface.ACTION_NOTIFICATION_RECEIVED.equals(_Action)){
            Logger.e("jigang", "ACTION_NOTIFICATION_RECEIVED");
            //umeng statistic notification received
            MobclickAgent.onEvent(context, CommonConstant.US_BAINEWS_NOTIFICATION_RECEIVED);
        } else if (JPushInterface.ACTION_NOTIFICATION_OPENED.equals(_Action)) {
            //umeng statistic notification received and opened it
            MobclickAgent.onEvent(context,CommonConstant.US_BAINEWS_NOTIFICATION_OPENED);

                String extras = bundle.getString(JPushInterface.EXTRA_EXTRA);
                Logger.i("jigang", "receive custom extras=" + extras);
                //此处对传过来的json字符串做处理 {"news_url":"www.baidu.com"}
                if (extras == null||"{}".equals(extras)) {
                    Intent HomeIntent = new Intent(context, HomeAty.class);
                    HomeIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(HomeIntent);
                } else {
                    try {
                        JSONObject urlObject = new JSONObject(extras);
                        String news_url = urlObject.getString("news_url");
                        if (!TextUtils.isEmpty(news_url)) {
                            Intent detailIntent = new Intent(context, NewsDetailAty.class);
                            detailIntent.putExtra(NewsFeedFgt.KEY_URL, news_url);
                            detailIntent.putExtra(NewsFeedFgt.KEY_NEWS_SOURCE, NewsFeedFgt.VALUE_NEWS_NOTIFICATION);
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
            }**/
        }

}
