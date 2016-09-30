package com.news.yazhidao.utils.manager;

import android.content.Context;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.google.gson.reflect.TypeToken;
import com.news.yazhidao.application.YaZhiDaoApplication;
import com.news.yazhidao.entity.User;
import com.news.yazhidao.net.volley.RegisterVisitorRequest;
import com.news.yazhidao.net.volley.VisitorLoginRequest;
import com.news.yazhidao.utils.GsonUtil;
import com.news.yazhidao.utils.Logger;
import com.news.yazhidao.utils.TextUtil;
import com.news.yazhidao.utils.helper.ShareSdkHelper;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * 用户管理类
 * Created by fengjigang on 16/5/30.
 */
public class UserManager {

    public interface RegisterVisitorListener {
        void registeSuccess();
    }

    /**
     * 注册游客身份,获取访问所有接口数据的token
     */
    public static void registerVisitor(final Context mContext, final RegisterVisitorListener mListener) {
        final User user = SharedPreManager.getUser(mContext);
        if (user != null)
            Logger.e("jigang","check userJson="+user.toJsonString());
        if (user == null) {
            JSONObject requestBody = new JSONObject();
            try {
                requestBody.put("utype", 2);
                requestBody.put("platform", 2);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            RegisterVisitorRequest jsonObjectRequest = new RegisterVisitorRequest(requestBody.toString(), new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    User user = new User();
                    user.setAuthorToken(response.optString("Authorization"));
                    user.setUtype(response.optString("utype"));
                    int uid = response.optInt("uid");
                    String password = response.optString("password");

                    user.setMuid(uid);
                    user.setPassword(password);

                    /** 用户第一次安装保存的 uid  和 password */
//                    user.setTuid(uid);
//                    user.setTpassword(password);
                    try {
                        ArrayList<String> channels = GsonUtil.deSerializedByType(response.getJSONArray("channel").toString(), new TypeToken<ArrayList<String>>() {
                        }.getType());
                        user.setChannel(channels);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    SharedPreManager.saveUser(user);
                    if (mListener != null){
                        mListener.registeSuccess();
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Logger.e("jigang", "error" + error.getMessage());
                }
            });
            YaZhiDaoApplication.getInstance().getRequestQueue().add(jsonObjectRequest);
        }else {
//            && !user.isOnceLogin()
            if (TextUtil.isEmptyString(user.getUtype()) || user.isVisitor()) {
                JSONObject requestBody = new JSONObject();
                try {
                    requestBody.put("uid", user.getMuid());
                    requestBody.put("password", user.getPassword());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                Logger.e("jigang", "user visitor login body=" + requestBody.toString());
                VisitorLoginRequest loginRequest = new VisitorLoginRequest(requestBody.toString(), new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Logger.e("aaa", "response==" + response.toString());
                        String authorization = response.optString("Authorization");
                        int uid = response.optInt("uid");
                        String password = response.optString("password");
                        if(!TextUtil.isEmptyString(authorization)){
                            user.setAuthorToken(authorization);
                        }
                        if(uid != 0){
                            user.setMuid(uid);
                        }
                        if(!TextUtil.isEmptyString(password)){
                            user.setPassword(password);
                        }

                        /**本来想这样写，但是没有必要，直接把utype 改为2（游客）*/
//                        String utype = response.optString("utype");
//                        if(!"2".equals(utype)){
//                            user.setUtype("2");
//                        }
                        user.setUtype("2");

                        SharedPreManager.saveUser(user);
                        Logger.e("jigang", "user visitor login=" + SharedPreManager.getUser(mContext).toJsonString());
                        if (mListener != null) {
                            mListener.registeSuccess();
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Logger.e("jigang", "error" + error.getMessage());
                    }
                });
                HashMap<String, String> header = new HashMap<>();
                header.put("Authorization", SharedPreManager.getUser(mContext).getAuthorToken());
                header.put("Content-Type", "application/json");
                header.put("X-Requested-With", "*");
                loginRequest.setRequestHeader(header);
                YaZhiDaoApplication.getInstance().getRequestQueue().add(loginRequest);
            } else {
                ShareSdkHelper.mergeThirdUser(null);
            }
        }
    }
}
