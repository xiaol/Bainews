package com.news.yazhidao.net.request;

import android.content.Context;

import com.news.yazhidao.common.HttpConstant;
import com.news.yazhidao.entity.User;
import com.news.yazhidao.listener.DiggerNewsListener;
import com.news.yazhidao.net.MyAppException;
import com.news.yazhidao.net.NetworkRequest;
import com.news.yazhidao.net.StringCallback;
import com.news.yazhidao.utils.manager.SharedPreManager;

import java.util.HashMap;

/**
 * Created by fengjigang on 15/8/5.
 * 挖掘新闻请求
 */
public class DigNewsRequest {

    /**用户ID*/
    public final static String KEY_UID = "uid";
    /**专辑ID(用户当前搜索新闻所在的专辑ID)*/
    public final static String KEY_ALID = "album";
    /**用户提供的新闻线索(文字，比如文章标题)*/
    public final static String KEY_KEY = "key";
    /**用户提供的新闻线索(URL，比如文章url)*/
    public final static String KEY_URL = "url";

    /**
     * 开始挖掘新闻
     * @param pContext 上下文
     * @param pAlbumId 专辑id
     * @param pTitle 要挖掘的文本
     * @param pUrl 要挖掘的url
     * @param pLisener 挖掘回调接口
     */
    public static void digNews(Context pContext,String pAlbumId,String pTitle,String pUrl, final DiggerNewsListener pLisener){
        NetworkRequest request = new NetworkRequest(HttpConstant.URL_DIGGER_ALBUM, NetworkRequest.RequestMethod.GET);
        HashMap<String,Object> params = new HashMap<>();
        //此处默认用户是已经登录过的
        User user = SharedPreManager.getUser(pContext);
        params.put(KEY_UID,user.getUserId());
        params.put(KEY_ALID,pAlbumId);
        params.put(KEY_KEY,pTitle);
        params.put(KEY_URL,pUrl);
        request.getParams = params;
        request.setCallback(new StringCallback() {
            @Override
            public void success(String result) {
                if (pLisener != null){
                    pLisener.diggerNewsDone(result);
                }
            }

            @Override
            public void failed(MyAppException exception) {
                if (pLisener != null){
                    pLisener.diggerNewsDone(null);
                }
            }
        });
        request.execute();
    }
}
