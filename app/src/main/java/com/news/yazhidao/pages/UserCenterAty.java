package com.news.yazhidao.pages;

import android.content.Intent;
import android.net.Uri;
import android.view.View;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.news.yazhidao.R;
import com.news.yazhidao.common.BaseActivity;
import com.news.yazhidao.entity.User;
import com.news.yazhidao.utils.manager.SharedPreManager;
import com.umeng.analytics.MobclickAgent;

/**
 * Created by fengjigang on 16/4/6.
 */
public class UserCenterAty extends BaseActivity implements View.OnClickListener {

    public static final int REQUEST_CODE = 1008;

    private View mCenterCancel,mCenterComment,mCenterFavorite,mCenterMessage,mCenterDigger,mCenterSetting;
    private SimpleDraweeView mCenterUserIcon;
    private TextView mCenterUserName;

    @Override
    protected void setContentView() {
        setContentView(R.layout.aty_user_center);
    }

    @Override
    protected void initializeViews() {
        mCenterCancel = findViewById(R.id.mCenterCancel);
        mCenterCancel.setOnClickListener(this);
        mCenterUserIcon = (SimpleDraweeView)findViewById(R.id.mCenterUserIcon);
        mCenterUserName = (TextView)findViewById(R.id.mCenterUserName);
        mCenterComment = findViewById(R.id.mCenterComment);
        mCenterComment.setOnClickListener(this);
        mCenterFavorite = findViewById(R.id.mCenterFavorite);
        mCenterFavorite.setOnClickListener(this);
        mCenterMessage = findViewById(R.id.mCenterMessage);
        mCenterMessage.setOnClickListener(this);
        mCenterDigger = findViewById(R.id.mCenterDigger);
        mCenterDigger.setOnClickListener(this);
        mCenterSetting = findViewById(R.id.mCenterSetting);
        mCenterSetting.setOnClickListener(this);
    }

    @Override
    protected boolean isNeedAnimation() {
        return true;
    }

    @Override
    protected boolean translucentStatus() {
        return false;
    }

    @Override
    protected void loadData() {
        User user = SharedPreManager.getUser(this);
        if (user != null){
            mCenterUserIcon.setImageURI(Uri.parse(user.getUserIcon()));
            mCenterUserName.setText(user.getUserName());
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == SettingAty.RESULT_CODE){
            finish();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.mCenterCancel:
                finish();
                break;
            case R.id.mCenterComment:
                Intent myCommentAty = new Intent(this,MyCommentAty.class);
                startActivity(myCommentAty);
                MobclickAgent.onEvent(this,"qidian_user_center_my_comments");
                break;
            case R.id.mCenterFavorite:
                Intent myFavoriteAty = new Intent(this,MyFavoriteAty.class);
                startActivity(myFavoriteAty);
                MobclickAgent.onEvent(this,"qidian_user_center_my_favorite");
                break;
            case R.id.mCenterMessage:
                Intent myMessageAty = new Intent(this, MyMessageAty.class);
                startActivity(myMessageAty);
                MobclickAgent.onEvent(this,"qidian_user_center_my_message");
                break;
            case R.id.mCenterDigger:
                Intent diggerAty = new Intent(this, DiggerAty.class);
                startActivity(diggerAty);
                MobclickAgent.onEvent(this,"qidian_user_center_my_digger");
                break;
            case R.id.mCenterSetting:
                Intent userCenterAty = new Intent(this,SettingAty.class);
                startActivityForResult(userCenterAty,REQUEST_CODE);
                MobclickAgent.onEvent(this,"qidian_user_center_my_setting");
                break;
        }
    }
}
