package com.news.yazhidao.pages;

import android.view.View;

import com.news.yazhidao.R;
import com.news.yazhidao.widget.swipebackactivity.SwipeBackActivity;

/**
 * Created by fengjigang on 16/4/8.
 */
public class MyMessageAty extends SwipeBackActivity implements View.OnClickListener {

    private View mMessagetLeftBack;

    @Override
    protected void setContentView() {
        setContentView(R.layout.aty_my_message);
    }

    @Override
    protected void initializeViews() {
        mMessagetLeftBack = findViewById(R.id.mMessagetLeftBack);
        mMessagetLeftBack.setOnClickListener(this);
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
            case R.id.mMessagetLeftBack:
                finish();
                break;
        }
    }
}
