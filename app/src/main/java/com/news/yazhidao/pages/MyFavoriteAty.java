package com.news.yazhidao.pages;

import android.content.SharedPreferences;
import android.os.Handler;
import android.view.KeyEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.AbsListView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.news.yazhidao.R;
import com.news.yazhidao.adapter.NewsFeedAdapter;
import com.news.yazhidao.adapter.NewsFeedAdapter.introductionNewsFeed;
import com.news.yazhidao.common.BaseActivity;
import com.news.yazhidao.entity.NewsFeed;
import com.news.yazhidao.utils.DensityUtil;
import com.news.yazhidao.utils.Logger;
import com.news.yazhidao.utils.manager.SharedPreManager;

import org.json.JSONException;

import java.util.ArrayList;

/**
 * Created by fengjigang on 16/4/8.
 */
public class MyFavoriteAty extends BaseActivity implements View.OnClickListener {
    private View mFavoriteLeftBack;
//    private RelativeLayout bgLayout;
    private PullToRefreshListView mFavoriteListView;
    private NewsFeedAdapter mAdapter;
    private ArrayList<NewsFeed> newsFeedList = new ArrayList<NewsFeed>();
    private ArrayList<NewsFeed> deleteFeedList = new ArrayList<NewsFeed>();
    private boolean isHaveFooterView;
    private TextView mFavoriteRightManage,aty_myFavorite_number;
    private LinearLayout aty_myFavorite_Deletelayout;
    private boolean isDeleteyFavorite;




    @Override

    protected void setContentView() {
        setContentView(R.layout.aty_my_favorite);
    }

    @Override
    protected void initializeViews() {
        mFavoriteLeftBack = findViewById(R.id.mFavoriteLeftBack);

        mFavoriteRightManage = (TextView) findViewById(R.id.mFavoriteRightManage);
        mFavoriteRightManage.setOnClickListener(this);
        aty_myFavorite_number = (TextView) findViewById(R.id.aty_myFavorite_number);
        aty_myFavorite_Deletelayout = (LinearLayout) findViewById(R.id.aty_myFavorite_Deletelayout);
        aty_myFavorite_Deletelayout.setOnClickListener(this);

//        bgLayout = (RelativeLayout) findViewById(R.id.bgLayout);
        mFavoriteListView = (PullToRefreshListView) findViewById(R.id.aty_myFavorite_PullToRefreshListView);
        mAdapter = new NewsFeedAdapter(this, null, null);
        mAdapter.setIntroductionNewsFeed(mIntroductionNewsFeed);

    }

    introductionNewsFeed mIntroductionNewsFeed = new introductionNewsFeed() {
        @Override
        public void getDate(NewsFeed feed, boolean isCheck) {

            if(isCheck){
//                for (NewsFeed bean:deleteFeedList) {
//                    if(bean.getUrl().equals(feed.getUrl())){
//                        return;
//                    }
//                }
                deleteFeedList.add(feed);

                Logger.e("aaa"," deleteFeedList.size()===添加==="+ deleteFeedList.size());
            }else{
//                for (NewsFeed bean:deleteFeedList) {
//                    if(bean.getUrl().equals(feed.getUrl())){
//
//                        return;
//                    }
//                }
                deleteFeedList.remove(feed);
                Logger.e("aaa"," deleteFeedList.size()===删除==="+ deleteFeedList.size());
            }
            aty_myFavorite_number.setText("(" + deleteFeedList.size() + ")");
        }
    };
    @Override
    protected boolean translucentStatus() {
        return false;
    }

    @Override
    protected void loadData() {
        aty_myFavorite_Deletelayout.setVisibility(View.GONE);
        mFavoriteLeftBack.setOnClickListener(this);
        mFavoriteListView.setMode(PullToRefreshBase.Mode.DISABLED);
        mFavoriteListView.setAdapter(mAdapter);

    }

    @Override
    protected void onResume() {
        super.onResume();
//        bgLayout.setVisibility(View.GONE);
        try {
            newsFeedList = SharedPreManager.myFavoriteGetList();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Logger.e("aaa", "newsFeedList==" + newsFeedList);
        mAdapter.setNewsFeed(newsFeedList);
        mAdapter.notifyDataSetChanged();

        if(newsFeedList.size() == 0){
            mFavoriteListView.setVisibility(View.GONE);
        }else{
            if(!isHaveFooterView){
                isHaveFooterView = true;
                AbsListView.LayoutParams layoutParams = new AbsListView.LayoutParams(AbsListView.LayoutParams.MATCH_PARENT, AbsListView.LayoutParams.WRAP_CONTENT);
                ListView lv = mFavoriteListView.getRefreshableView();
                LinearLayout mNewsDetailFootView = (LinearLayout) getLayoutInflater().inflate(R.layout.detail_footview_layout, null);
                mNewsDetailFootView.setLayoutParams(layoutParams);
                lv.addFooterView(mNewsDetailFootView);
            }
            mFavoriteListView.setVisibility(View.VISIBLE);

        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.mFavoriteLeftBack:
                finish();
                break;
            case R.id.mFavoriteRightManage:
                if(isDeleteyFavorite){
                    for (int i = 0; i < deleteFeedList.size(); i++) {
                        deleteFeedList.get(i).setFavorite(false);
                    }
                    setDeleteType(false);
                }else{
                    setDeleteType(true);

                }
                break;
            case R.id.aty_myFavorite_Deletelayout:
                for (int i = 0; i < deleteFeedList.size(); i++) {
                    deleteFeedList.get(i).setFavorite(false);
                }
                setDeleteType(false);
                break;
        }
    }

    public void setDeleteType(boolean isType){
        if(isType){
            isDeleteyFavorite = true;
            deleteLayoutAnimcation(isDeleteyFavorite);
            mAdapter.setVisitycheckFavoriteDeleteLayout(isDeleteyFavorite);
            mFavoriteRightManage.setText("取消");
        }else{
            isDeleteyFavorite = false;
            deleteLayoutAnimcation(isDeleteyFavorite);
            mAdapter.setNewsFeed(newsFeedList);
            mAdapter.setVisitycheckFavoriteDeleteLayout(isDeleteyFavorite);
            mFavoriteRightManage.setText("管理");
            deleteFeedList = new ArrayList<NewsFeed>();
            aty_myFavorite_number.setText("");
        }
    }
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (isDeleteyFavorite) {
                setDeleteType(false);
                return true;
            } else {
                finish();
            }
        }
        return super.onKeyDown(keyCode, event);

    }

    private void deleteLayoutAnimcation(boolean isStart){
        if(isStart){
            aty_myFavorite_Deletelayout.setVisibility(View.VISIBLE);
            //初始化
            Animation translateAnimation = new TranslateAnimation(0, 0, DensityUtil.dip2px(this, 47), 0);
            //设置动画时间
            translateAnimation.setDuration(100);
            aty_myFavorite_Deletelayout.startAnimation(translateAnimation);
            translateAnimation.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {

                }

                @Override
                public void onAnimationEnd(Animation animation) {
//
                }

                @Override
                public void onAnimationRepeat(Animation animation) {

                }
            });
        }else{
            //初始化
            Animation translateAnimation = new TranslateAnimation(0, 0, 0, DensityUtil.dip2px(this, 47));
            //设置动画时间
            translateAnimation.setDuration(100);
            aty_myFavorite_Deletelayout.startAnimation(translateAnimation);
            translateAnimation.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {

                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    aty_myFavorite_Deletelayout.setVisibility(View.GONE);
                }

                @Override
                public void onAnimationRepeat(Animation animation) {

                }
            });
        }


    }
}
