package com.news.yazhidao.widget;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.text.Html;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.news.yazhidao.R;
import com.news.yazhidao.entity.User;
import com.news.yazhidao.listener.UserLoginListener;
import com.news.yazhidao.listener.UserLoginPopupStateListener;
import com.news.yazhidao.pages.ChatAty;
import com.news.yazhidao.utils.helper.ShareSdkHelper;
import com.news.yazhidao.utils.image.ImageManager;
import com.news.yazhidao.utils.manager.SharedPreManager;
import com.news.yazhidao.widget.customdialog.Effectstype;
import com.news.yazhidao.widget.customdialog.SuperDialogBuilder;

import cn.sharesdk.framework.PlatformDb;

/**
 * Created by fengjigang on 15/5/12.
 */
public class LoginPopupWindow extends PopupWindow implements View.OnClickListener, UserLoginListener {
    private final View mPopupWidow;
    private Context mContext;
    private RoundedImageView mHomeUserIcon;
    private TextView mHomeLogin;
    private View mHomeChatWrapper;
    private View mHomeLoginCancel;
    private UserLoginListener mUserLoginListener;
    private View mHomeLogout;

    public LoginPopupWindow(Context mContext) {
        this.mContext = mContext;
        LayoutInflater inflater = (LayoutInflater) mContext
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mPopupWidow = inflater.inflate(R.layout.aty_home_login, null);
        initConfig();
        initViews();
    }

    private void initViews() {
        mHomeUserIcon = (RoundedImageView) mPopupWidow.findViewById(R.id.mHomeUserIcon);
        mHomeLogin = (TextView) mPopupWidow.findViewById(R.id.mHomeLogin);
        mHomeLogin.setText(Html.fromHtml(mContext.getResources().getString(R.string.home_login_text)));
        mHomeChatWrapper = mPopupWidow.findViewById(R.id.mHomeChatWrapper);
        mHomeLoginCancel = mPopupWidow.findViewById(R.id.mHomeLoginCancel);
        mHomeLogout = mPopupWidow.findViewById(R.id.mHomeLogout);
        mHomeLogout.setOnClickListener(this);
        mHomeLoginCancel.setOnClickListener(this);
        mHomeLogin.setOnClickListener(this);
        mHomeChatWrapper.setOnClickListener(this);
        //判断用户是否登录，并且登录有效
        User user = SharedPreManager.getUser();
        if (user != null) {
            mHomeLogin.setOnClickListener(null);
            mHomeLogin.setText(user.getUserName());
            mHomeLogin.setTextSize(TypedValue.COMPLEX_UNIT_SP, 15);
            mHomeLogout.setVisibility(View.VISIBLE);
            mHomeChatWrapper.setBackgroundResource(R.drawable.bg_login_footer_default);
            ImageManager.getInstance(mContext).DisplayImage(user.getUserIcon(), mHomeUserIcon, false);
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
        ColorDrawable dw = new ColorDrawable(0xb0000000);
        //设置SelectPicPopupWindow弹出窗体的背景
        this.setBackgroundDrawable(dw);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.mHomeLoginCancel:
                this.dismiss();
                break;
            case R.id.mHomeLogin:
                openLoginModeWindow();
                break;
            case R.id.mHomeLogout:
                logout();
                break;
            case R.id.mHomeChatWrapper:
                String strJPushId = SharedPreManager.getJPushId();
                Intent intent;
//                if (CommonConstant.JINYU_JPUSH_ID.equals(strJPushId))
                    intent = new Intent(mContext, ChatAty.class);
//                else
//                    intent = new Intent(mContext, FeedBackActivity.class);
                mContext.startActivity(intent);
                dismiss();
                break;
        }
    }

    /**
     * 用户退出登录
     */
    private void logout() {
        final SuperDialogBuilder _DialogBuilder = SuperDialogBuilder.getInstance(mContext);
        _DialogBuilder.withMessage("退出后，就不能参与评论了")
                .withDuration(400)
                .withIcon(R.drawable.app_icon)
                .withTitle("退出登录")
                .withEffect(Effectstype.Sidefill)
                .withButton1Text("OK")
                .withButton2Text("Cancel")
                .setButton1Click(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        _DialogBuilder.dismiss();
                        ShareSdkHelper.logout(mContext);
                        mHomeUserIcon.setImageResource(R.drawable.ic_user_login_default);
                        mHomeLogin.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12);
                        mHomeLogin.setText(Html.fromHtml(mContext.getResources().getString(R.string.home_login_text)));
                        mHomeLogin.setOnClickListener(LoginPopupWindow.this);
                        mHomeLogout.setVisibility(View.GONE);
                        mHomeChatWrapper.setBackgroundResource(R.drawable.bg_login_footer);
                    }
                }).setButton2Click(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                _DialogBuilder.dismiss();
            }
        }).show();


    }

    private void openLoginModeWindow() {
        LoginModePopupWindow window = new LoginModePopupWindow(mContext, this, new UserLoginPopupStateListener() {

            @Override
            public void close() {
                LoginPopupWindow.this.dismiss();
            }
        });
        window.showAtLocation(((Activity) mContext).getWindow().getDecorView(), Gravity.CENTER
                | Gravity.CENTER, 0, 0);
    }


    @Override
    public void userLogin(String platform, PlatformDb platformDb) {
        mHomeLogin.setOnClickListener(null);
        mHomeLogin.setText(platformDb.getUserName());
        mHomeLogin.setTextSize(TypedValue.COMPLEX_UNIT_SP, 15);
        mHomeChatWrapper.setBackgroundResource(R.drawable.bg_login_footer_default);
        mHomeLogout.setVisibility(View.VISIBLE);
        ImageManager.getInstance(mContext).DisplayImage(platformDb.getUserIcon(), mHomeUserIcon, false);
    }

    @Override
    public void userLogout() {
        ShareSdkHelper.logout(mContext);
        mHomeUserIcon.setImageResource(R.drawable.ic_user_login_default);
        mHomeLogin.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12);
        mHomeLogin.setText(Html.fromHtml(mContext.getResources().getString(R.string.home_login_text)));
        mHomeLogin.setOnClickListener(this);
    }
}
