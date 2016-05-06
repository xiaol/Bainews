package com.news.yazhidao.pages;

import android.content.SharedPreferences;
import android.view.View;
import android.widget.AbsListView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.news.yazhidao.R;
import com.news.yazhidao.adapter.MyFavoriteAdapter;
import com.news.yazhidao.common.BaseActivity;
import com.news.yazhidao.entity.NewsFeed;
import com.news.yazhidao.utils.manager.SharedPreManager;

import org.json.JSONException;

import java.util.ArrayList;

/**
 * Created by fengjigang on 16/4/8.
 */
public class MyFavoriteAty extends BaseActivity implements View.OnClickListener {
    private View mFavoriteLeftBack;
    private RelativeLayout bgLayout;
    private PullToRefreshListView mFavoriteListView;
    private MyFavoriteAdapter mAdapter;
    private ArrayList<NewsFeed> newsFeedList = new ArrayList<NewsFeed>();
    private boolean isHaveFooterView;



    @Override

    protected void setContentView() {
        setContentView(R.layout.aty_my_favorite);
    }

    @Override
    protected void initializeViews() {
        mFavoriteLeftBack = findViewById(R.id.mFavoriteLeftBack);
        bgLayout = (RelativeLayout) findViewById(R.id.bgLayout);
        mFavoriteListView = (PullToRefreshListView) findViewById(R.id.aty_myFavorite_PullToRefreshListView);
        mAdapter = new MyFavoriteAdapter(MyFavoriteAty.this);

    }

    @Override
    protected boolean translucentStatus() {
        return false;
    }

    @Override
    protected void loadData() {
        mFavoriteLeftBack.setOnClickListener(this);
        mFavoriteListView.setMode(PullToRefreshBase.Mode.DISABLED);
        mFavoriteListView.setAdapter(mAdapter);

    }

    @Override
    protected void onResume() {
        super.onResume();
        bgLayout.setVisibility(View.GONE);
        try {
            newsFeedList = SharedPreManager.myFavoriteGetList();
        } catch (JSONException e) {
            e.printStackTrace();
        }
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
        }
    }
}
