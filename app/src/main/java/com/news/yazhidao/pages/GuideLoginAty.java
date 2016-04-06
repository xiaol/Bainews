package com.news.yazhidao.pages;

import android.content.Intent;
import android.view.View;

import com.news.yazhidao.R;
import com.news.yazhidao.common.BaseActivity;

/**
 * Created by fengjigang on 16/4/6.
 */
public class GuideLoginAty extends BaseActivity implements View.OnClickListener {
    private View mGuideWeiboLogin;
    private View mGuideWinxinLogin;
    private View mGuideSkip;

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
    protected void loadData() {

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.mGuideWeiboLogin:

                break;
            case R.id.mGuideWinxinLogin:

                break;
            case R.id.mGuideSkip:
                this.finish();
                Intent mainAty = new Intent(this,MainAty.class);
                startActivity(mainAty);
                break;
        }
    }
}
