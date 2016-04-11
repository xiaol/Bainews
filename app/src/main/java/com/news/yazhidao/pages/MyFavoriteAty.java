package com.news.yazhidao.pages;

import android.view.View;

import com.news.yazhidao.R;
import com.news.yazhidao.common.BaseActivity;

/**
 * Created by fengjigang on 16/4/8.
 */
public class MyFavoriteAty extends BaseActivity implements View.OnClickListener {
    private View mFavoriteLeftBack;

    @Override
    protected void setContentView() {
        setContentView(R.layout.aty_my_favorite);
    }

    @Override
    protected void initializeViews() {
        mFavoriteLeftBack = findViewById(R.id.mFavoriteLeftBack);
        mFavoriteLeftBack.setOnClickListener(this);
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
        switch (v.getId()){
            case R.id.mFavoriteLeftBack:
                finish();
                break;
        }
    }
}
