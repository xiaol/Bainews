package com.news.yazhidao.widget;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;

import com.news.yazhidao.R;
import com.news.yazhidao.common.CommonConstant;
import com.news.yazhidao.listener.UserLoginListener;
import com.news.yazhidao.utils.DeviceInfoUtil;
import com.news.yazhidao.utils.Logger;
import com.news.yazhidao.utils.manager.SharedPreManager;

import java.util.HashMap;

import cn.sharesdk.framework.Platform;
import cn.sharesdk.framework.PlatformActionListener;
import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.sina.weibo.SinaWeibo;
import cn.sharesdk.tencent.weibo.TencentWeibo;
import cn.sharesdk.wechat.friends.Wechat;

/**
 * 登陆界面
 */
public class LoginPopupWindow extends PopupWindow implements PlatformActionListener, OnClickListener {
    public final String TAG = "LoginPopupWindow";
    private View mMenuView;
    private Context mContext;
    RelativeLayout mWeiboLogin, mWeiXinLogin;
    ImageView mCancelLogin;
    private ProgressDialog mProgressDialog;
    private AsyncTask mLoginTask;
    private UserLoginListener mListener;
    private View mHomeAtyLoginBg;
    private int mScreenWidth;
    private int mScreenHeight;
    private View mHomeAtyLoginOut;

    public LoginPopupWindow(Context context,UserLoginListener listener) {
        super(context);
        mContext = context;
        this.mListener=listener;
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mMenuView = inflater.inflate(R.layout.aty_home_login_layout, null);
        mScreenHeight= DeviceInfoUtil.getScreenHeight();
        mScreenWidth=DeviceInfoUtil.getScreenWidth();
        findViews();
    }

    @Override
    public void setOnDismissListener(OnDismissListener onDismissListener) {
        super.setOnDismissListener(onDismissListener);
    }


    private void findViews() {
        mWeiboLogin = (RelativeLayout) mMenuView.findViewById(R.id.mWeiboLogin);
        mCancelLogin = (ImageView) mMenuView.findViewById(R.id.mCancelLogin);
        mWeiXinLogin = (RelativeLayout) mMenuView.findViewById(R.id.mWeiXinLogin);
        mHomeAtyLoginBg=mMenuView.findViewById(R.id.mHomeAtyLoginBg);
        mHomeAtyLoginOut=mMenuView.findViewById(R.id.mHomeAtyLogOut);
        mHomeAtyLoginOut.setOnClickListener(this);
        mWeiboLogin.setOnClickListener(this);
        mCancelLogin.setOnClickListener(this);
        mWeiXinLogin.setOnClickListener(this);

        mProgressDialog = new ProgressDialog(mContext);
        mProgressDialog.setMessage("请稍候");
        mProgressDialog.setCancelable(true);// 设置是否可以通过点击Back键取消
        mProgressDialog.setCanceledOnTouchOutside(false);// 设置在点击Dialog外是否取消Dialog进度条

        //设置SelectPicPopupWindow的View
        this.setContentView(mMenuView);
        //设置SelectPicPopupWindow弹出窗体的宽
        this.setWidth(LayoutParams.MATCH_PARENT);
        //设置SelectPicPopupWindow弹出窗体的高
        this.setHeight(LayoutParams.WRAP_CONTENT);
        //设置SelectPicPopupWindow弹出窗体可点击
        this.setFocusable(true);
        //设置SelectPicPopupWindow弹出窗体动画效果
//        this.setAnimationStyle(R.style.DialogAnimation);
        //实例化一个ColorDrawable颜色为半透明
        ColorDrawable dw = new ColorDrawable(0xb0000000);
        //设置SelectPicPopupWindow弹出窗体的背景
        this.setBackgroundDrawable(dw);
        //mMenuView添加OnTouchListener监听判断获取触屏位置如果在选择框外面则销毁弹出框
        mMenuView.setOnTouchListener(new OnTouchListener() {

            public boolean onTouch(View v, MotionEvent event) {

                int _Width=mHomeAtyLoginBg.getWidth();
                int _Height=mHomeAtyLoginBg.getHeight();
                Rect rect=new Rect(mScreenWidth/2-_Width/2,mScreenHeight/2-_Height/2,mScreenWidth-(mScreenWidth/2-_Width/2),mScreenHeight-(mScreenHeight/2-_Height/2));
                int y = (int) event.getY();
                int x=(int)event.getX();
                if (event.getAction() == MotionEvent.ACTION_UP) {
                   if(!rect.contains(x,y)){
                       dismiss();
                   }
                }
                return true;
            }
        });
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.mCancelLogin:
                dismiss();
                break;
            case R.id.mWeiboLogin:
                authorize(mContext, SinaWeibo.NAME, this);
                break;
            case R.id.mWeiXinLogin:
                authorize(mContext, Wechat.NAME, this);
                break;
            case R.id.mHomeAtyLogOut:
                logout();

                break;
        }
    }

    /**
     * 用户注销登陆
     */
    private void logout() {
        String[] data = SharedPreManager.getUserIdAndPlatform(CommonConstant.FILE_USER_INFO, CommonConstant.KEY_USER_ID_AND_PLATFORM);
        if(data!=null){
            ShareSDK.getPlatform(mContext,data[1]).removeAccount();
        }
        SharedPreManager.remove(CommonConstant.FILE_USER_INFO,CommonConstant.KEY_USER_ID_AND_PLATFORM);
    }

    /**
     * sharesdk 授权认证
     *
     * @param mContext
     * @param platform
     * @param listener
     */
    private void authorize(Context mContext, String platform, PlatformActionListener listener) {
//        mProgressDialog.show();
        Platform _Plateform = ShareSDK.getPlatform(mContext, platform);
        //判断指定平台是否已经完成授权
        if (_Plateform.isValid()) {
            String userId = _Plateform.getDb().getUserId();
            if (userId != null) {
                if(mListener!=null){
                    mListener.userLogin(platform,_Plateform.getDb());
                }
                return;
            }
        }
        if (SinaWeibo.NAME.equals(platform) || TencentWeibo.NAME.equals(platform)) {
            //关注指定的微博
            _Plateform.followFriend("火花无线");
        }
        _Plateform.SSOSetting(false);
        _Plateform.setPlatformActionListener(listener);
        _Plateform.authorize();
    }


    @Override
    public void onComplete(Platform platform, int i, HashMap<String, Object> stringObjectHashMap) {
        Logger.i(TAG, "onComplete-----");
        String userId = platform.getDb().getUserId();
        if (userId != null) {
            String nickName = platform.getDb().getUserName();
            String gender = platform.getDb().getUserGender();
            String iconURL = platform.getDb().getUserIcon();
            String token = platform.getDb().getToken();
            Log.i(TAG, "nickName=" + nickName + ",gender=" + gender + ",iconURL=" + iconURL + ",token=" + token);
            //保存用户登陆信息到sp中
            SharedPreManager.saveUserIdAndPlatform(CommonConstant.FILE_USER_INFO, CommonConstant.KEY_USER_ID_AND_PLATFORM, userId, platform.getName());
            if(mListener!=null){
                mListener.userLogin(platform.getName(),platform.getDb());
            }
        }
        if (mProgressDialog != null) {
            mProgressDialog.dismiss();
        }
    }

    @Override
    public void onError(Platform platform, int i, Throwable throwable) {
        Logger.e(TAG, "authorize error-----");
        if (mProgressDialog != null) {
            mProgressDialog.dismiss();
        }
    }

    @Override
    public void onCancel(Platform platform, int i) {
        Logger.e(TAG, "authorize cancel-----");
        if (mProgressDialog != null) {
            mProgressDialog.dismiss();
        }
    }

}
