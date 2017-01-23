package com.news.yazhidao.pages;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.view.View;

import com.news.yazhidao.R;
import com.news.yazhidao.common.BaseActivity;
import com.news.yazhidao.common.CommonConstant;
import com.news.yazhidao.database.ChannelItemDao;
import com.news.yazhidao.entity.User;
import com.news.yazhidao.listener.UserAuthorizeListener;
import com.news.yazhidao.utils.ToastUtil;
import com.news.yazhidao.utils.helper.ShareSdkHelper;
import com.news.yazhidao.utils.manager.SharedPreManager;

import static com.news.yazhidao.utils.helper.ShareSdkHelper.AuthorizePlatform;

/**
 * Created by fengjigang on 16/4/6.
 */
public class LoginAty extends BaseActivity implements View.OnClickListener {

    public static final String KEY_USER_LOGIN = "key_user_login";
    public static final int REQUEST_CODE = 1006;
    private View mLoginWeibo, mLoginWeixin, mLoginCancel, mLoginSetting;
    private ProgressDialog progressDialog;
    private long mFirstClickTime;
    private int mAttentionIndex;
    private Context mContext;
    private boolean misshow;
    private UserAuthorizeListener mAuthorizeListener = new UserAuthorizeListener() {
        @Override
        public void success(User user) {
            if (!misshow) {
                if (SharedPreManager.getBoolean(CommonConstant.FILE_USER, "isusericonlogin", false)) {
                    SharedPreManager.save(CommonConstant.FILE_USER, "isusericonlogin", true);
                    Intent intent = new Intent();
                    intent.putExtra(KEY_USER_LOGIN, user);
                    intent.putExtra(SubscribeListActivity.KEY_ATTENTION_INDEX, mAttentionIndex);
                    setResult(REQUEST_CODE, intent);
                } else {
                    SharedPreManager.save(CommonConstant.FILE_USER, "isusericonlogin", false);
                    int position = new ChannelItemDao(LoginAty.this).ResetSelectedByFocus();
                    Intent intent = new Intent(MainAty.ACTION_FOUCES);
                    intent.putExtra(MainAty.KEY_INTENT_CURRENT_POSITION, position);
                    sendBroadcast(intent);
                }
                LoginAty.this.finish();
                misshow = true;
            }
        }

        @Override
        public void failure(String message) {
            if (!misshow) {
                ToastUtil.toastShort("登录失败");
                misshow = true;
            }
        }

        @Override
        public void cancel() {
            if (!misshow) {
                ToastUtil.toastShort("取消登录");
                misshow = true;
            }
        }
    };

    @Override
    protected void setContentView() {
        setContentView(R.layout.aty_login);
        mContext = this;

    }

    @Override
    protected void initializeViews() {
        mLoginWeibo = findViewById(R.id.mLoginWeibo);
        mLoginWeibo.setOnClickListener(this);
        mLoginWeixin = findViewById(R.id.mLoginWeixin);
        mLoginWeixin.setOnClickListener(this);
        mLoginCancel = findViewById(R.id.mLoginCancel);
        mLoginCancel.setOnClickListener(this);
        mLoginSetting = findViewById(R.id.mLoginSetting);
        mLoginSetting.setOnClickListener(this);
    }

    @Override
    protected boolean translucentStatus() {
        return false;
    }

    @Override
    protected void loadData() {
        mAttentionIndex = getIntent().getIntExtra(SubscribeListActivity.KEY_ATTENTION_INDEX, 0);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (getIntent().getBooleanExtra(SettingAty.KEY_NEED_NOT_SETTING, false)) {
            mLoginSetting.setVisibility(View.GONE);
        } else {
            mLoginSetting.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.mLoginWeibo:
                showLoadingDialog();
                if (System.currentTimeMillis() - mFirstClickTime < 2000) {
                    return;
                }
                mFirstClickTime = System.currentTimeMillis();
                showLoadingDialog();
                ShareSdkHelper.authorize(this, AuthorizePlatform.WEIBO, mAuthorizeListener);
                break;
            case R.id.mLoginWeixin:
                showLoadingDialog();
                if (System.currentTimeMillis() - mFirstClickTime < 2000) {
                    return;
                }
                mFirstClickTime = System.currentTimeMillis();
                showLoadingDialog();
                ShareSdkHelper.authorize(this, AuthorizePlatform.WEIXIN, mAuthorizeListener);
                break;
            case R.id.mLoginCancel:
                this.finish();
                break;
            case R.id.mLoginSetting:
                Intent settingAty = new Intent(this, SettingAty.class);
                startActivity(settingAty);
                this.finish();
                break;
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (progressDialog != null) {
            progressDialog.dismiss();
        }
    }

    /**
     * 显示全屏dialog
     */
    private void showLoadingDialog() {
        if (progressDialog == null) {
            progressDialog = new ProgressDialog(this);
            progressDialog.setIndeterminate(true);
            progressDialog.setCancelable(false);
            progressDialog.setMessage("登录中...");
            progressDialog.show();
        }
    }
}
