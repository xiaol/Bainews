package com.news.yazhidao.utils.manager;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;

import com.news.yazhidao.application.YaZhiDaoApplication;
import com.news.yazhidao.common.CommonConstant;
import com.news.yazhidao.entity.User;
import com.news.yazhidao.utils.Logger;

import cn.sharesdk.framework.ShareSDK;

/**
 * Created by fengjigang on 15/5/6.
 */
public class SharedPreManager {
    public static SharedPreferences getSettings(String name, int mode){
        return YaZhiDaoApplication.getAppContext().getSharedPreferences(name, mode);
    }

    public static void saveUserIdAndPlatform(String spName, String keyUserIdAndPlatform, String userId, String platform) {
        SharedPreferences.Editor e = getSettings(spName, Context.MODE_PRIVATE).edit();
        e.putString(keyUserIdAndPlatform, userId+","+platform);
        e.commit();
    }
    public static void save(String spName, String key, String value){
        SharedPreferences.Editor e = getSettings(spName, Context.MODE_PRIVATE).edit();
        e.putString(key, value);
        e.commit();
    }

    public static void saveJPushId(String value){
        SharedPreferences.Editor e = getSettings(CommonConstant.FILE_JPUSH, Context.MODE_PRIVATE).edit();
        e.putString(CommonConstant.KEY_JPUSH_ID, value);
        e.commit();
    }

    public static String getJPushId(){
        return get(CommonConstant.FILE_JPUSH,CommonConstant.KEY_JPUSH_ID);
    }

    /**
     * 保存User json
     * @param user
     */
    public static void saveUser(User user){
        save(CommonConstant.FILE_USER,CommonConstant.KEY_USER_INFO,user.toJsonString());
    }

    /**
     * 删除保存的user json
     */
    public static void deleteUser(Context mContext){
        User user = getUser();
        if(user!=null){
            ShareSDK.getPlatform(mContext, user.getPlatformType()).removeAccount();
        }
        remove(CommonConstant.FILE_USER, CommonConstant.KEY_USER_INFO);
    }
    /**
     * 从 sp 中获取用户对象
     * @return
     */
    public static User getUser(){
        String userJson = get(CommonConstant.FILE_USER, CommonConstant.KEY_USER_INFO);
        if(TextUtils.isEmpty(userJson)){
            return null;
        }
        return User.parseUser(userJson);
    }
    public static void save(String spName, String key, long value){
        Logger.d("SettingsManager", "SettingsManager : " + spName + ":" + "key : " + key + "value : " + value);
        SharedPreferences.Editor e = getSettings(spName, Context.MODE_PRIVATE).edit();
        e.putLong(key, value);
        e.commit();
    }

    public static void save(String spName, String key, boolean value){
        SharedPreferences.Editor e = getSettings(spName, Context.MODE_PRIVATE).edit();
        e.putBoolean(key, value);
        e.commit();
    }


    public static void clear(String spName){
        SharedPreferences.Editor editor = getSettings(spName, Context.MODE_PRIVATE).edit();
        editor.clear();
        editor.commit();
    }

    public static String[] getUserIdAndPlatform(String spName, String key){
        String data = getSettings(spName, Context.MODE_PRIVATE).getString(key, "");
        return TextUtils.isEmpty(data)?null:data.split(",");
    }
    public static String get(String spName, String key){
        return getSettings(spName, Context.MODE_PRIVATE).getString(key, "");
    }

    public static boolean getBoolean(String spName, String key){
        return getSettings(spName, Context.MODE_PRIVATE).getBoolean(key, false);
    }

    public static boolean getBoolean(String spName, String key, boolean defaultValue){
        return getSettings(spName, Context.MODE_PRIVATE).getBoolean(key, defaultValue);
    }

    public static long getLong(String spName, String key){
        return getSettings(spName, Context.MODE_PRIVATE).getLong(key, 0);
    }

    public static void remove(String spName, String...keys){
        if(keys == null) return;
        SharedPreferences.Editor e = getSettings(spName, Context.MODE_PRIVATE).edit();
        for(String key : keys){
            e.remove(key);
        }
        e.commit();
    }

}
