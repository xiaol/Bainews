package com.news.yazhidao.pages;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;

import com.news.yazhidao.R;
import com.news.yazhidao.listener.UserLoginListener;
import com.news.yazhidao.listener.UserLoginPopupStateListener;
import com.news.yazhidao.utils.DeviceInfoUtil;
import com.news.yazhidao.utils.Logger;
import com.news.yazhidao.utils.helper.ShareSdkHelper;
import com.umeng.analytics.AnalyticsConfig;

import cn.sharesdk.sina.weibo.SinaWeibo;
import cn.sharesdk.wechat.friends.Wechat;

/**
 * Created by fengjigang on 15/11/11.
 * 用户登录方式界面的fragment
 */
public class LoginModeFgt extends DialogFragment implements View.OnClickListener {

    private enum AuthorizeType {
        WEIXIN,WEIBO,MEIZU
    }
    private  UserLoginListener mUserLoginListener;
    private  UserLoginPopupStateListener mUserLoginPopupStateListener;
    private Context mContext;
    private View mLoginModeCancel;
    private View mLoginModeWeibo;
    private View mLoginModeWeiXin;
    private View mLoginModeMeiZu;
    private ProgressDialog progressDialog;

    private long mFirstClickTime;
    private AuthorizeType mAuthorizeType;

    public LoginModeFgt(){}

    public LoginModeFgt(Context context, UserLoginListener loginListener, UserLoginPopupStateListener userLoginPopupStateListener) {
        this.mContext = context;
        this.mUserLoginListener = loginListener;
        this.mUserLoginPopupStateListener = userLoginPopupStateListener;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        /**让dialogfragment 全屏*/
        setStyle(DialogFragment.STYLE_NORMAL, android.R.style.Theme);
        setCancelable(false);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);

        View rootView = inflater.inflate(R.layout.aty_home_login_mode, null);
        mLoginModeCancel = rootView.findViewById(R.id.mLoginModeCancel);
        mLoginModeCancel.setOnClickListener(this);
        mLoginModeWeibo = rootView.findViewById(R.id.mLoginModeWeibo);
        mLoginModeWeibo.setOnClickListener(this);
        mLoginModeWeiXin = rootView.findViewById(R.id.mLoginModeWeiXin);
        mLoginModeWeiXin.setOnClickListener(this);
        mLoginModeMeiZu = rootView.findViewById(R.id.mLoginModeMeiZu);
        mLoginModeMeiZu.setOnClickListener(this);
        if (DeviceInfoUtil.isFlyme() || "meizu".equals(AnalyticsConfig.getChannel(mContext))) { //魅族手机
            mLoginModeMeiZu.setVisibility(View.VISIBLE);
        }
        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        Logger.e("jigang", "loginModeFgt onPause");
        if (mAuthorizeType == AuthorizeType.WEIXIN || mAuthorizeType == AuthorizeType.MEIZU){
            if (progressDialog != null){
                progressDialog.dismiss();
            }
        }
    }

    @Override
    public void dismiss() {
        super.dismiss();
        if (progressDialog != null){
            progressDialog.dismiss();
        }
    }

    /**显示全屏dialog*/
    private void showLoadingDialog(){
        if (progressDialog == null){
            progressDialog = new ProgressDialog(getActivity());
            progressDialog.setIndeterminate(true);
            progressDialog.setCancelable(false);
            progressDialog.setMessage("登录中...");
            progressDialog.show();
        }
    }

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
                mAuthorizeType = AuthorizeType.WEIBO;
                if (System.currentTimeMillis() - mFirstClickTime < 2000) {
                    return;
                }
                mFirstClickTime = System.currentTimeMillis();
                showLoadingDialog();
                ShareSdkHelper.authorize(mContext, SinaWeibo.NAME, mUserLoginListener, new UserLoginPopupStateListener() {

                    @Override
                    public void close() {
                        LoginModeFgt.this.dismiss();
                    }
                });
                break;
            case R.id.mLoginModeWeiXin:
                mAuthorizeType = AuthorizeType.WEIXIN;
                if (System.currentTimeMillis() - mFirstClickTime < 2000) {
                    return;
                }
                mFirstClickTime = System.currentTimeMillis();
                showLoadingDialog();
                ShareSdkHelper.authorize(mContext, Wechat.NAME, mUserLoginListener, new UserLoginPopupStateListener() {
                    @Override
                    public void close() {
                        LoginModeFgt.this.dismiss();
                    }
                });
                break;
            case R.id.mLoginModeMeiZu:
                mAuthorizeType = AuthorizeType.MEIZU;
                if (System.currentTimeMillis() - mFirstClickTime < 2000) {
                    return;
                }
                mFirstClickTime = System.currentTimeMillis();
                showLoadingDialog();
                ShareSdkHelper.authorize(mContext, "meizu", mUserLoginListener, new UserLoginPopupStateListener() {
                    @Override
                    public void close() {

                    }
                });
                break;
        }
    }
}
