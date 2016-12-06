package com.news.yazhidao.pages;

import android.app.ProgressDialog;
import android.content.Intent;
import android.view.View;

import com.news.yazhidao.R;
import com.news.yazhidao.common.BaseActivity;
import com.news.yazhidao.entity.User;
import com.news.yazhidao.listener.UserAuthorizeListener;
import com.news.yazhidao.utils.ToastUtil;
import com.news.yazhidao.utils.helper.ShareSdkHelper;
import com.umeng.analytics.MobclickAgent;

import static com.news.yazhidao.utils.helper.ShareSdkHelper.AuthorizePlatform;

/**
 * Created by fengjigang on 16/4/6.
 */
public class GuideLoginAty extends BaseActivity implements View.OnClickListener {
    private View mGuideWeiboLogin;
    private View mGuideWinxinLogin;
    private View mGuideSkip;
    private ProgressDialog progressDialog;
    private long mFirstClickTime;
    private UserAuthorizeListener mAuthorizeListener = new UserAuthorizeListener() {
        @Override
        public void success(User user) {
            Intent mainAty = new Intent(GuideLoginAty.this,MainAty.class);
            startActivity(mainAty);
//            Intent intent = new Intent(MainAty.ACTION_USER_LOGIN);
//            intent.putExtra(MainAty.KEY_INTENT_USER_URL, user.getUserIcon());
//            sendBroadcast(intent);
            GuideLoginAty.this.finish();
        }

        @Override
        public void failure(String message) {
            ToastUtil.toastShort("登录失败");
        }

        @Override
        public void cancel() {
            ToastUtil.toastShort("取消登录");
        }
    };

    @Override
    protected void setContentView() {
        setContentView(R.layout.aty_guide_login);
    }

    @Override
    protected void initializeViews() {
        mGuideWeiboLogin = findViewById(R.id.mGuideWeiboLogin);
        mGuideWeiboLogin.setOnClickListener(this);
        mGuideWinxinLogin = findViewById(R.id.mGuideWinxinLogin);
        mGuideWinxinLogin.setOnClickListener(this);
        mGuideSkip = findViewById(R.id.mGuideSkip);
        mGuideSkip.setOnClickListener(this);
    }

    @Override
    protected boolean isNeedAnimation() {
        return true;
    }

    @Override
    protected void loadData() {

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.mGuideWeiboLogin:
                showLoadingDialog();
                if (System.currentTimeMillis() - mFirstClickTime < 2000) {
                    return;
                }
                mFirstClickTime = System.currentTimeMillis();
                showLoadingDialog();
                ShareSdkHelper.authorize(this, AuthorizePlatform.WEIBO, mAuthorizeListener);
                MobclickAgent.onEvent(this,"qidian_user_first_weibo_login");
                break;
            case R.id.mGuideWinxinLogin:
                showLoadingDialog();
                if (System.currentTimeMillis() - mFirstClickTime < 2000) {
                    return;
                }
                mFirstClickTime = System.currentTimeMillis();
                showLoadingDialog();
                ShareSdkHelper.authorize(this, AuthorizePlatform.WEIXIN, mAuthorizeListener);
                MobclickAgent.onEvent(this,"qidian_user_first_weixin_login");
                break;
            case R.id.mGuideSkip:
                this.finish();
                Intent mainAty = new Intent(this,MainAty.class);
                startActivity(mainAty);
                MobclickAgent.onEvent(this,"qidian_user_first_look_around");
                break;
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (progressDialog != null){
            progressDialog.dismiss();
        }
    }

    /**显示全屏dialog*/
    private void showLoadingDialog(){
        if (progressDialog == null){
            progressDialog = new ProgressDialog(this);
            progressDialog.setIndeterminate(true);
            progressDialog.setCancelable(false);
            progressDialog.setMessage("登录中...");
            progressDialog.show();
        }
    }
}
