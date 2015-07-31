package com.news.yazhidao.net.request;

import android.content.Context;

import com.news.yazhidao.common.HttpConstant;
import com.news.yazhidao.entity.DiggerAlbum;
import com.news.yazhidao.entity.User;
import com.news.yazhidao.listener.CreateDiggerAlbumListener;
import com.news.yazhidao.net.MyAppException;
import com.news.yazhidao.net.NetworkRequest;
import com.news.yazhidao.net.StringCallback;
import com.news.yazhidao.utils.manager.SharedPreManager;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by fengjigang on 15/7/31.
 * 创建挖掘机种用户专辑的网络请求
 */
public class CreateDiggerAlbumRequest {
    /**登录后用户id*/
    public final static String USER_ID = "user_id";
    /**专辑标题*/
    public final static String ALBUM_TITLE = "album_title";
    /**专辑描述*/
    public final static String ALBUM_DES = "album_des";
    /**专辑背景图片*/
    public final static String ALBUM_IMG = "album_img";
    /**专辑中包含的挖掘内容总数*/
    public final static String ALBUM_NEWS_COUNT = "album_news_count";
    /**创建成功后,返回过来的专辑id*/
    public final static String ALBUM_ID = "album_id";

    /**
     * 创建个人专辑接口
     * @param pContext context 上下文
     * @param pDiggerAlbum 专辑对象
     * @param pListener 网络接口回调
     */
    public static void createDiggerAlbum(Context pContext,DiggerAlbum pDiggerAlbum, final CreateDiggerAlbumListener pListener){
        NetworkRequest request = new NetworkRequest(HttpConstant.URL_CREATE_DIGGER_ALBUM, NetworkRequest.RequestMethod.POST);
        ArrayList<NameValuePair> params = new ArrayList<>();
        //此处默认用户是已经登录过的
        User user = SharedPreManager.getUser(pContext);
        params.add(new BasicNameValuePair(USER_ID,user.getUserId()));
        params.add(new BasicNameValuePair(ALBUM_TITLE,pDiggerAlbum.getAlbum_title()));
        params.add(new BasicNameValuePair(ALBUM_DES,pDiggerAlbum.getAlbum_des()));
        params.add(new BasicNameValuePair(ALBUM_IMG,pDiggerAlbum.getAlbum_img()));
        params.add(new BasicNameValuePair(ALBUM_NEWS_COUNT,pDiggerAlbum.getAlbum_news_count()));
        request.setParams(params);
        request.setCallback(new StringCallback() {
            @Override
            public void success(String result) {
                String albumId = null;
                try {
                    JSONObject jsonObj = new JSONObject(result);
                    albumId = jsonObj.optString(ALBUM_ID);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                if (pListener != null) {
                    pListener.createDiggerAlbumDone(albumId);
                }
            }

            @Override
            public void failed(MyAppException exception) {
                if (pListener != null) {
                    pListener.createDiggerAlbumDone(null);
                }
            }
        });
        request.execute();
    }

}
