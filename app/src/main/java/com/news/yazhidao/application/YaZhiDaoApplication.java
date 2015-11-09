package com.news.yazhidao.application;

import android.app.Application;
import android.content.Context;
import android.content.Intent;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.news.yazhidao.common.CommonConstant;
import com.news.yazhidao.net.request.UploadUmengPushIdRequest;
import com.news.yazhidao.pages.ChatAty;
import com.news.yazhidao.pages.FeedBackActivity;
import com.news.yazhidao.pages.MainAty;
import com.news.yazhidao.pages.NewsDetailAty2;
import com.news.yazhidao.pages.NewsFeedFgt;
import com.news.yazhidao.utils.CrashHandler;
import com.news.yazhidao.utils.DeviceInfoUtil;
import com.news.yazhidao.utils.Logger;
import com.news.yazhidao.utils.TextUtil;
import com.news.yazhidao.utils.helper.NotificationHelper;
import com.umeng.analytics.MobclickAgent;
import com.umeng.message.PushAgent;
import com.umeng.message.UmengNotificationClickHandler;
import com.umeng.message.UmengRegistrar;
import com.umeng.message.entity.UMessage;

/**
 * Created by fengjigang on 15/2/1.
 */
public class YaZhiDaoApplication extends Application {
    private static Context mContext;
    @Override
    public void onCreate() {
        mContext=this;
        //在这里为应用设置异常处理程序，然后我们的程序才能捕获未处理的异常
        CrashHandler crashHandler = CrashHandler.getInstance();
        crashHandler.init(this);
        PushAgent mPushAgent = PushAgent.getInstance(this);
        mPushAgent.setDebugMode(false);
        mPushAgent.setNotificationClickHandler(notificationClickHandler);
        //init fresco
        Fresco.initialize(this);
        super.onCreate();
    }
    public static Context getAppContext(){
        return mContext;
    }
    /**
     * 该Handler是在BroadcastReceiver中被调用，故
     * 如果需启动Activity，需添加Intent.FLAG_ACTIVITY_NEW_TASK
     * */
    UmengNotificationClickHandler notificationClickHandler = new UmengNotificationClickHandler(){

        @Override
        public void dealWithCustomAction(Context context, UMessage msg) {
            //messageKey key
            String messageAction = msg.extra.get("messageKey");
            if ("action_registration_id".equals(messageAction)) {
                String device_token = UmengRegistrar.getRegistrationId(context);
                UploadUmengPushIdRequest.uploadUmengPushId(context, device_token);
            } else if ("action_message_received".equals(messageAction)) {
                String title =msg.extra.get("extra_title");
                String message = msg.extra.get("extra_message");
                String extras = msg.extra.get("extra_extra");
                String type = msg.extra.get("extra_content_type");
                String file = msg.extra.get("extra_richpush_file_path");
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
            }else if("action_notification_received".equals(messageAction)){
                //umeng statistic notification received
                MobclickAgent.onEvent(context, CommonConstant.US_BAINEWS_NOTIFICATION_RECEIVED);
            } else if ("action_notification_opened".equals(messageAction)) {
                //umeng statistic notification received and opened it
                MobclickAgent.onEvent(context,CommonConstant.US_BAINEWS_NOTIFICATION_OPENED);

                String newsid = msg.extra.get("newsid");
                String collection = msg.extra.get("collection");

                Logger.i("jigang", "receive custom newsid=" + newsid + ",collection=" + collection);
                //此处对传过来的json字符串做处理 {"news_url":"www.baidu.com"}
                if (!TextUtil.isEmptyString(newsid) && !TextUtil.isEmptyString(collection)) {
                    Intent detailIntent = new Intent(context, NewsDetailAty2.class);
                    detailIntent.putExtra(NewsFeedFgt.KEY_NEWS_ID, newsid);
                    detailIntent.putExtra(NewsFeedFgt.KEY_NEWS_SOURCE, NewsFeedFgt.VALUE_NEWS_NOTIFICATION);
                    detailIntent.putExtra(NewsFeedFgt.KEY_COLLECTION, collection);
                    detailIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(detailIntent);
                } else {
                    Intent HomeIntent = new Intent(context, MainAty.class);
                    HomeIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(HomeIntent);
                }

            }
        }
    };

}
