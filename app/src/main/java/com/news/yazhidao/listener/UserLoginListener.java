package com.news.yazhidao.listener;

import cn.sharesdk.framework.PlatformDb;

/**
 * Created by fengjigang on 15/5/6.
 * 用户登陆成功后回调
 */
public interface UserLoginListener {
    void userLogin(String platform,PlatformDb platformDb);
}
