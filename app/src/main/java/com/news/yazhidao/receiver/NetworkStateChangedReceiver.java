package com.news.yazhidao.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.text.TextUtils;

import com.news.yazhidao.database.DiggerAlbumDao;
import com.news.yazhidao.entity.DiggerAlbum;
import com.news.yazhidao.net.MyAppException;
import com.news.yazhidao.net.StringCallback;
import com.news.yazhidao.net.request.CreateDiggerAlbumRequest;
import com.news.yazhidao.utils.DeviceInfoUtil;
import com.news.yazhidao.utils.Logger;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by fengjigang on 15/8/11.
 * 监听网络状态变化
 */
public class NetworkStateChangedReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if(ConnectivityManager.CONNECTIVITY_ACTION.equals(intent.getAction())){
            if(DeviceInfoUtil.isNetworkConnected(context)){
                //查询数据库,看是否有新建的专辑没有上传成功,如果有则上传
                reUploadAlbum(context);
            }
        }
    }
    public void reUploadAlbum(Context pContext){
        final DiggerAlbumDao dao = new DiggerAlbumDao(pContext);
        ArrayList<DiggerAlbum> albums = dao.queryNotUpload();
        for(final DiggerAlbum pDiggerAlbum: albums){
            /**把新创建好的专辑存入数据库*/
            CreateDiggerAlbumRequest.createDiggerAlbum(pContext, pDiggerAlbum, new StringCallback() {
                @Override
                public int retryCount() {
                    return 3;
                }

                @Override
                public void success(String result) {
                    String albumId = null;
                    if (!TextUtils.isEmpty(result)) {
                        try {
                            JSONObject jsonObj = new JSONObject(result);
                            albumId = jsonObj.optString(CreateDiggerAlbumRequest.ALBUM_ID);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        if (!TextUtils.isEmpty(albumId)) {
                            pDiggerAlbum.setIs_uploaded("1");
                            dao.update(pDiggerAlbum);
                            Logger.e("jigang", "---重新上传新建专辑成功!");
                        } else {
                            Logger.e("jigang", "---重新上传新建专辑失败!");
                        }

                    }
                }

                @Override
                public void failed(MyAppException exception) {
                    Logger.e("jigang", "---重新上传新建专辑失败," + exception.getMessage());
                }
            });
        }

    }
}
