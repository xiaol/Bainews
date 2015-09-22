package com.news.yazhidao.widget;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Handler;
import android.text.Html;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupWindow;

import com.news.yazhidao.R;
import com.news.yazhidao.common.GlobalParams;
import com.news.yazhidao.common.HttpConstant;
import com.news.yazhidao.entity.User;
import com.news.yazhidao.listener.UserLoginListener;
import com.news.yazhidao.listener.UserLoginPopupStateListener;
import com.news.yazhidao.net.MyAppException;
import com.news.yazhidao.net.NetworkRequest;
import com.news.yazhidao.net.StringCallback;
import com.news.yazhidao.net.UserCallback;
import com.news.yazhidao.net.request.UploadJpushidRequest;
import com.news.yazhidao.pages.HomeAty;
import com.news.yazhidao.utils.DeviceInfoUtil;
import com.news.yazhidao.utils.TextUtil;
import com.news.yazhidao.utils.helper.ShareSdkHelper;
import com.news.yazhidao.utils.manager.SharedPreManager;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import cn.sharesdk.framework.PlatformDb;
import cn.sharesdk.sina.weibo.SinaWeibo;
import cn.sharesdk.wechat.friends.Wechat;
import sdk.meizu.auth.MzAuthenticator;
import sdk.meizu.auth.OAuthError;
import sdk.meizu.auth.callback.CodeCallback;

/**
 * Created by fengjigang on 15/5/13.
 */
public class LoginModePopupWindow extends PopupWindow implements View.OnClickListener {
    private final View mPopupWidow;
    private Context mContext;
    private TextViewExtend mLoginModeWarning;
    private View mLoginModeCancel;
    private View mLoginModeWeibo;
    private View mLoginModeWeiXin;
    private View mLoginModeMeiZu;
    private String CLIENT_ID = "tsGKllOEx2MnUVmBmRey";
    private String REDIRECT_URI = "http://www.deeporiginalx.com/";
    private String CLIENT_SECRET = "gOMuh3824Tx2UKJWvu3Qa3DsUTSvyv";
    private UserLoginListener mUserLoginListener;
    private UserLoginPopupStateListener mUserLoginPopupStateListener;

    public LoginModePopupWindow(Context context, UserLoginListener loginListener, UserLoginPopupStateListener userLoginPopupStateListener) {
        this.mContext = context;
        this.mUserLoginListener = loginListener;
        this.mUserLoginPopupStateListener = userLoginPopupStateListener;
        LayoutInflater inflater = (LayoutInflater) mContext
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mPopupWidow = inflater.inflate(R.layout.aty_home_login_mode, null);
        initConfig();
        initViews();
    }

    private void initViews() {
        mLoginModeWarning = (TextViewExtend) mPopupWidow.findViewById(R.id.mLoginModeWarning);
        mLoginModeWarning.setText(Html.fromHtml(mContext.getResources().getString(R.string.home_login_mode_login_warning)));
        mLoginModeCancel = mPopupWidow.findViewById(R.id.mLoginModeCancel);
        mLoginModeCancel.setOnClickListener(this);
        mLoginModeWeibo = mPopupWidow.findViewById(R.id.mLoginModeWeibo);
        mLoginModeWeibo.setOnClickListener(this);
        mLoginModeWeiXin = mPopupWidow.findViewById(R.id.mLoginModeWeiXin);
        mLoginModeWeiXin.setOnClickListener(this);
        mLoginModeMeiZu = mPopupWidow.findViewById(R.id.mLoginModeMeiZu);
        mLoginModeMeiZu.setOnClickListener(this);
        mLoginModeMeiZu.setVisibility(View.VISIBLE);
        if ("Meizu".equals(android.os.Build.MANUFACTURER)) { //魅族手机
//            mLoginModeMeiZu.setVisibility(View.VISIBLE);
        }
    }

    private void initConfig() {
        //设置SelectPicPopupWindow的View
        this.setContentView(mPopupWidow);
        //设置SelectPicPopupWindow弹出窗体的宽
        this.setWidth(ViewGroup.LayoutParams.MATCH_PARENT);
        //设置SelectPicPopupWindow弹出窗体的高
        this.setHeight(ViewGroup.LayoutParams.MATCH_PARENT);
        //设置SelectPicPopupWindow弹出窗体可点击
        this.setFocusable(true);
        //设置SelectPicPopupWindow弹出窗体动画效果
//        this.setAnimationStyle(R.style.DialogAnimation);
        //实例化一个ColorDrawable颜色为半透明
        ColorDrawable dw = new ColorDrawable(mContext.getResources().getColor(R.color.bg_home_login_mode));
        //设置SelectPicPopupWindow弹出窗体的背景
        this.setBackgroundDrawable(dw);
    }

    private long mFirstClickTime;

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.mLoginModeCancel:
                this.dismiss();
                if (mUserLoginPopupStateListener != null) {
                    mUserLoginPopupStateListener.close();
                }
                break;
            case R.id.mLoginModeWeibo:
                if (System.currentTimeMillis() - mFirstClickTime < 2000) {
                    return;
                }
                mFirstClickTime = System.currentTimeMillis();
                ShareSdkHelper.authorize(mContext, SinaWeibo.NAME, mUserLoginListener, new UserLoginPopupStateListener() {

                    @Override
                    public void close() {
                        LoginModePopupWindow.this.dismiss();
                    }
                });
                break;
            case R.id.mLoginModeWeiXin:
                if (System.currentTimeMillis() - mFirstClickTime < 2000) {
                    return;
                }
                mFirstClickTime = System.currentTimeMillis();
                ShareSdkHelper.authorize(mContext, Wechat.NAME, mUserLoginListener, new UserLoginPopupStateListener() {
                    @Override
                    public void close() {
                        LoginModePopupWindow.this.dismiss();
                    }
                });
                break;
            case R.id.mLoginModeMeiZu:
                if (System.currentTimeMillis() - mFirstClickTime < 2000) {
                    return;
                }
                MzAuthenticator mAuthenticator = new MzAuthenticator(CLIENT_ID, REDIRECT_URI);
                mFirstClickTime = System.currentTimeMillis();
                mAuthenticator.requestCodeAuth((HomeAty) mContext, "uc_basic_info", new CodeCallback() {
                    @Override
                    public void onError(OAuthError oAuthError) {
                        LoginModePopupWindow.this.dismiss();
                    }

                    @Override
                    public void onGetCode(String code) {
                        List<NameValuePair> nameValuePairs = new LinkedList<>();
                        nameValuePairs.add(new BasicNameValuePair("grant_type", "authorization_code"));
                        nameValuePairs.add(new BasicNameValuePair("client_id", CLIENT_ID));
                        nameValuePairs.add(new BasicNameValuePair("client_secret", CLIENT_SECRET));
                        nameValuePairs.add(new BasicNameValuePair("code", code));
                        nameValuePairs.add(new BasicNameValuePair("redirect_uri", REDIRECT_URI));
                        nameValuePairs.add(new BasicNameValuePair("state", "11"));
                        NetworkRequest request = new NetworkRequest("https://open-api.flyme.cn/oauth/token", NetworkRequest.RequestMethod.POST);
                        request.setParams(nameValuePairs);
                        request.setTimeOut(10000);
                        request.setCallback(new StringCallback() {
                            @Override
                            public void success(String result) {
                                JSONObject dataJson;
                                String strToken = null;
                                try {
                                    dataJson = new JSONObject(result);
                                    strToken = dataJson.getString("access_token");
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                                final String token = strToken;
                                NetworkRequest request = new NetworkRequest("https://open-api.flyme.cn/v2/me?access_token=" + strToken);
                                request.setTimeOut(10000);
                                request.setCallback(new StringCallback() {

                                    @Override
                                    public void success(String result) {
                                        JSONObject resultJson;
                                        try {
                                            resultJson = new JSONObject(result);
                                            JSONObject jsonvalue = resultJson.getJSONObject("value");
                                            Log.i("eva", jsonvalue.toString());
                                            final String strIcon = jsonvalue.getString("icon");
                                            final String strNickname = jsonvalue.getString("nickname");
                                            Log.i("eva", strIcon + "strIcon");
                                            Log.i("eva", strNickname + "strNickname");
                                            HashMap<String, Object> params = new HashMap<>();
                                            params.put("uuid", DeviceInfoUtil.getUUID());
                                            params.put("userId", TextUtil.getDatabaseId());
                                            params.put("expiresIn", System.currentTimeMillis()+1000*60*60*24*3);
                                            params.put("expiresTime",System.currentTimeMillis()+1000*60*60*24*3);
                                            params.put("token", token);
                                            params.put("userGender", "1");
                                            params.put("userIcon", strIcon);
                                            params.put("userName", strNickname);
                                            params.put("platformType", "meizu");
                                            NetworkRequest request = new NetworkRequest(HttpConstant.URL_USER_LOGIN, NetworkRequest.RequestMethod.GET);
                                            request.getParams = params;
                                            request.setCallback(new UserCallback<User>() {
                                                @Override
                                                public void success(User user) {
                                                    SharedPreManager.saveUser(user);
                                                    String jPushId = SharedPreManager.getJPushId();
                                                    if (!TextUtils.isEmpty(jPushId)) {
                                                        UploadJpushidRequest.uploadJpushId(mContext, jPushId);
                                                    }

                                                    Intent intent = new Intent("saveuser");
                                                    intent.putExtra("url", user.getUserIcon());
                                                    GlobalParams.context.sendBroadcast(intent);

                                                    if (mUserLoginListener != null) {
                                                        new Handler().post(new Runnable() {
                                                            @Override
                                                            public void run() {
                                                                LoginModePopupWindow.this.dismiss();
                                                                mUserLoginPopupStateListener.close();
                                                                PlatformDb platformDb = new PlatformDb(mContext, "platformNname", 1);
                                                                platformDb.put("nickname", strNickname);
                                                                platformDb.put("icon", strIcon);
                                                                mUserLoginListener.userLogin(strNickname, platformDb);
                                                            }
                                                        });
                                                    }
                                                }

                                                @Override
                                                public void failed(MyAppException exception) {
                                                }
                                            }.setReturnClass(User.class));
                                            request.execute();
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }
                                    }

                                    @Override
                                    public void failed(MyAppException exception) {
                                    }
                                });
                                request.execute();
                            }

                            @Override
                            public void failed(MyAppException exception) {

                            }
                        });
                        request.execute();
                    }
                });
                break;

        }
    }
}
