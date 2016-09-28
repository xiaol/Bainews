package com.news.yazhidao.pages;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.ncc.sdk.offerwall.NccOfferWallAPI;
import com.ncc.sdk.offerwall.NccOfferWallListener;
import com.news.yazhidao.R;
import com.news.yazhidao.adapter.abslistview.CommonViewHolder;
import com.news.yazhidao.entity.User;
import com.news.yazhidao.utils.Logger;
import com.news.yazhidao.utils.ToastUtil;
import com.news.yazhidao.utils.manager.SharedPreManager;
import com.news.yazhidao.widget.swipebackactivity.SwipeBackActivity;
import com.umeng.analytics.MobclickAgent;

/**
 * Created by fengjigang on 16/4/6.
 */
public class UserCenterAty extends Activity implements View.OnClickListener {

    public static final int REQUEST_CODE = 1008;

    private View mCenterCancel,mCenterComment,mCenterFavorite,mCenterMessage,mCenterDigger,mCenterSetting,mCenterOfferWall;
    private ImageView mCenterUserIcon;
    private TextView mCenterUserName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView();
        initializeViews();
        loadData();
    }

    //    @Override
    protected void setContentView() {
        setContentView(R.layout.aty_user_center);
        NccOfferWallAPI.setPlatformId("4723e8b862a0ad34598189a35cf713b8");
        NccOfferWallAPI.init(this);
        NccOfferWallAPI
                .setOnCloseListener(new NccOfferWallListener<Void>() {
                    @Override
                    public void onSucceed(Void result) {

                        Logger.e("aaa","应用墙关闭了！");
                    }

                    @Override
                    public void onError(int errorCode, String errorMsg) {
                    }
                });

    }

//    @Override
    protected void initializeViews() {

        mCenterCancel = findViewById(R.id.mCenterCancel);
        mCenterCancel.setOnClickListener(this);
        mCenterUserIcon = (ImageView)findViewById(R.id.mCenterUserIcon);
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
        mCenterOfferWall = findViewById(R.id.mCenterOfferWall);

        mCenterOfferWall.setOnClickListener(this);
    }

    protected void loadData() {
        User user = SharedPreManager.getUser(this);
        if (user != null && !user.isVisitor()){
            Glide.with(UserCenterAty.this).load(Uri.parse(user.getUserIcon())).placeholder(R.drawable.ic_user_comment_default).transform(new CommonViewHolder.GlideCircleTransform(UserCenterAty.this, 5, getResources().getColor(R.color.white))).into(mCenterUserIcon);
            mCenterUserName.setText(user.getUserName());
        }else {
            Glide.with(UserCenterAty.this).load(R.drawable.ic_user_comment_default).placeholder(R.drawable.ic_user_comment_default).transform(new CommonViewHolder.GlideCircleTransform(UserCenterAty.this, 5, getResources().getColor(R.color.white))).into(mCenterUserIcon);
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
            case R.id.mCenterOfferWall:
//                Intent userOfferWall = new Intent(this,OfferWallActivity.class);
//                startActivityForResult(userOfferWall,REQUEST_CODE);
//                MobclickAgent.onEvent(this,"qidian_user_center_my_setting");

                NccOfferWallAPI.open(this, new NccOfferWallListener<Void>() {
                    @Override
                    public void onSucceed(Void result) {
                        Logger.e("aaa","打开应用墙成功！");
                    }

                    @Override
                    public void onError(int errorCode, String errorMsg) {
                        Logger.e("aaa","打开应用墙失败 --> " + errorCode + " --> "
                                + errorMsg);
                    }
                });
                break;
        }
    }

    @Override
    protected void onDestroy() {
        NccOfferWallAPI.destroy(this);
        super.onDestroy();
    }
}
