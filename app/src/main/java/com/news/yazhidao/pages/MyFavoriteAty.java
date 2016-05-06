package com.news.yazhidao.pages;

import android.content.SharedPreferences;
import android.view.View;
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
        bgLayout.setVisibility(View.GONE);
        mFavoriteLeftBack.setOnClickListener(this);
        mFavoriteListView.setMode(PullToRefreshBase.Mode.DISABLED);

        try {
            newsFeedList = SharedPreManager.myFavoriteGetList();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        mAdapter.setNewsFeed(newsFeedList);
        mFavoriteListView.setAdapter(mAdapter);
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
