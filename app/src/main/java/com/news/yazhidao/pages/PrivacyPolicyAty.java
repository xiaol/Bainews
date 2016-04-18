package com.news.yazhidao.pages;

import android.view.View;
import android.webkit.WebView;

import com.news.yazhidao.R;
import com.news.yazhidao.common.BaseActivity;

/**
 * Created by fengjigang on 16/4/11.
 */
public class PrivacyPolicyAty extends BaseActivity implements View.OnClickListener {
    private View mPrivacyLeftBack;
    private WebView mPrivacyWebView;

    @Override
    protected void setContentView() {
        setContentView(R.layout.aty_privacy_policy);
    }

    @Override
    protected void initializeViews() {
        mPrivacyLeftBack = findViewById(R.id.mPrivacyLeftBack);
        mPrivacyLeftBack.setOnClickListener(this);
        mPrivacyWebView = (WebView) findViewById(R.id.mPrivacyWebView);
        mPrivacyWebView.loadUrl("file:///android_asset/PrivacyPolicy.html");
    }

    @Override
    protected boolean translucentStatus() {
        return false;
    }

    @Override
    protected void loadData() {

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.mPrivacyLeftBack:
                finish();
                break;
        }
    }
}
