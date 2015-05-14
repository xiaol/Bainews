package com.news.yazhidao.widget;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupWindow;

import com.news.yazhidao.R;
import com.news.yazhidao.listener.UserLoginListener;
import com.news.yazhidao.listener.UserLoginPopupStateListener;
import com.news.yazhidao.utils.helper.ShareSdkHelper;

import cn.sharesdk.sina.weibo.SinaWeibo;
import cn.sharesdk.wechat.friends.Wechat;

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
    private UserLoginListener mUserLoginListener;
    private UserLoginPopupStateListener mUserLoginPopupStateListener;
    public LoginModePopupWindow(Context mContext, UserLoginListener loginListener, UserLoginPopupStateListener userLoginPopupStateListener){
        this.mContext=mContext;
        this.mUserLoginListener=loginListener;
        this.mUserLoginPopupStateListener=userLoginPopupStateListener;
        LayoutInflater inflater = (LayoutInflater) mContext
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mPopupWidow = inflater.inflate(R.layout.aty_home_login_mode, null);
        initConfig();
        initViews();
    }

    private void initViews() {
        mLoginModeWarning=(TextViewExtend)mPopupWidow.findViewById(R.id.mLoginModeWarning);
        mLoginModeWarning.setText(Html.fromHtml(mContext.getResources().getString(R.string.home_login_mode_login_warning)));
        mLoginModeCancel=mPopupWidow.findViewById(R.id.mLoginModeCancel);
        mLoginModeCancel.setOnClickListener(this);
        mLoginModeWeibo=mPopupWidow.findViewById(R.id.mLoginModeWeibo);
        mLoginModeWeibo.setOnClickListener(this);
        mLoginModeWeiXin=mPopupWidow.findViewById(R.id.mLoginModeWeiXin);
        mLoginModeWeiXin.setOnClickListener(this);
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
        ColorDrawable dw = new ColorDrawable(R.color.bg_home_login_mode);
        //设置SelectPicPopupWindow弹出窗体的背景
        this.setBackgroundDrawable(dw);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.mLoginModeCancel:
                this.dismiss();
                mUserLoginPopupStateListener.close();
                break;
            case R.id.mLoginModeWeibo:
                ShareSdkHelper.authorize(mContext, SinaWeibo.NAME,mUserLoginListener,new UserLoginPopupStateListener(){

                    @Override
                    public void close() {
                        LoginModePopupWindow.this.dismiss();
                    }
                });
                break;
            case R.id.mLoginModeWeiXin:
                ShareSdkHelper.authorize(mContext, Wechat.NAME,mUserLoginListener, new UserLoginPopupStateListener() {
                    @Override
                    public void close() {
                        LoginModePopupWindow.this.dismiss();
                    }
                });
                break;
        }
    }
}
