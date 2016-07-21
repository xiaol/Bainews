package com.news.yazhidao.pages;


import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AbsListView;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.facebook.drawee.view.SimpleDraweeView;
import com.google.gson.reflect.TypeToken;
import com.news.yazhidao.R;
import com.news.yazhidao.common.HttpConstant;
import com.news.yazhidao.entity.NewsFeed;
import com.news.yazhidao.net.volley.NewsLoveRequest;
import com.news.yazhidao.utils.DensityUtil;
import com.news.yazhidao.utils.Logger;
import com.news.yazhidao.utils.manager.SharedPreManager;
import com.news.yazhidao.widget.AvatarImageBehavior;
import com.news.yazhidao.widget.ListViewForScrollView;

import java.util.ArrayList;
import java.util.HashMap;

public class AttentionActivity extends AppCompatActivity implements View.OnClickListener,AppBarLayout.OnOffsetChangedListener{

    public static final String KEY_ATTENTION_TITLE = "key_attention_title";
    public static final String KEY_ATTENTION_HEADIMAGE = "key_detail_headimage";
    // 控制ToolBar的变量
    private static final float PERCENTAGE_TO_SHOW_TITLE_AT_TOOLBAR = 0.9f;
    private static final float PERCENTAGE_TO_HIDE_TITLE_DETAILS = 0.3f;

    private static final int ALPHA_ANIMATIONS_DURATION = 200;

    private boolean mIsTheTitleVisible = false;
    private boolean mIsTheTitleContainerVisible = true;
    private ListViewForScrollView listViewForScrollView;


    //    @Bind(R.id.main_iv_placeholder)
    ImageView mIvPlaceholder; // 大图片

    //    @Bind(R.id.main_ll_title_container)
    LinearLayout mLlTitleContainer; // Title的LinearLayout

    //    @Bind(R.id.main_fl_title)
    FrameLayout mFlTitleContainer; // Title的FrameLayout

    //    @Bind(R.id.main_abl_app_bar)
    AppBarLayout mAblAppBar; // 整个可以滑动的AppBar

//    @Bind(R.id.main_tv_toolbar_title)
//    TextView mTvToolbarTitle; // 标题栏Title

    //    @Bind(R.id.main_tb_toolbar)
    Toolbar mTbToolbar; // 工具栏

    private TextView mAttentionLeftBack,
            mAttentionRightMore,
            mAttention_btn,
            tv_attention_title;

    private String title, headImageUrl;

    private SimpleDraweeView iv_attention_headImage;

    private Context mContext;

    private RelativeLayout bgLayout;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_attention);
//        ButterKnife.bind(this);
        mContext = this;
//        title = getIntent().getStringExtra(KEY_ATTENTION_TITLE);
//        headImageUrl = getIntent().getStringExtra(KEY_ATTENTION_HEADIMAGE);
        title = "音乐风云";
        headImageUrl = "http://www.ld12.com/upimg358/20160201/yd33s1bvzmm1543.jpg";

        mIvPlaceholder = (ImageView) findViewById(R.id.main_iv_placeholder);
        mLlTitleContainer = (LinearLayout) findViewById(R.id.main_ll_title_container);
        mFlTitleContainer = (FrameLayout) findViewById(R.id.main_fl_title);
        mAblAppBar = (AppBarLayout) findViewById(R.id.main_abl_app_bar);
//        mTvToolbarTitle = (TextView) findViewById(R.id.main_tv_toolbar_title);
        mTbToolbar = (Toolbar) findViewById(R.id.main_tb_toolbar);

        bgLayout = (RelativeLayout) findViewById(R.id.bgLayout);
        bgLayout.setVisibility(View.GONE);

        tv_attention_title = (TextView) findViewById(R.id.tv_attention_title);
        tv_attention_title.setText(title);
        iv_attention_headImage = (SimpleDraweeView) findViewById(R.id.iv_attention_headImage);
        AvatarImageBehavior fancyBehavior = new AvatarImageBehavior(mContext);
        CoordinatorLayout.LayoutParams params =
                (CoordinatorLayout.LayoutParams) iv_attention_headImage.getLayoutParams();
        params.setBehavior(fancyBehavior);


        mAttentionLeftBack = (TextView) findViewById(R.id.mAttentionLeftBack);
        mAttentionRightMore = (TextView) findViewById(R.id.mAttentionRightMore);
        mAttention_btn = (TextView) findViewById(R.id.mAttention_btn);
        mAttentionLeftBack.setOnClickListener(this);
        mAttentionRightMore.setOnClickListener(this);
        mAttention_btn.setOnClickListener(this);



        listViewForScrollView = (ListViewForScrollView) findViewById(R.id.listView);
        final String[] data = getResources().getStringArray(R.array.students);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.text_item, data);
        listViewForScrollView.setAdapter(adapter);


        mTbToolbar.setTitle("");



        initParallaxValues(); // 自动滑动效果
        loadData();
    }

    private void loadData() {
//        RequestQueue requestQueue = Volley.newRequestQueue(mContext);
//        Logger.e("aaa", "URL=======" + HttpConstant.URL_SELECT_FAVORITELIST + "uid=" + SharedPreManager.getUser(mContext).getMuid());
//        NewsLoveRequest<ArrayList<NewsFeed>> request = new NewsLoveRequest<ArrayList<NewsFeed>>(Request.Method.GET,
//                new TypeToken<ArrayList<NewsFeed>>() {
//                }.getType(), HttpConstant.URL_SELECT_FAVORITELIST + "uid=" +user.getMuid()
//                , new Response.Listener<ArrayList<NewsFeed>>() {
//            @Override
//            public void onResponse(ArrayList<NewsFeed> response) {
//                Logger.e("aaa", "收藏内容======" + response.toString());
//                newsFeedList = response;
//                mAdapter.setNewsFeed(newsFeedList);
//                mAdapter.notifyDataSetChanged();
//
//                if(newsFeedList.size() == 0){
//                    mFavoriteListView.setVisibility(View.GONE);
//                }else{
//                    if(!isHaveFooterView){
//                        isHaveFooterView = true;
//                        AbsListView.LayoutParams layoutParams = new AbsListView.LayoutParams(AbsListView.LayoutParams.MATCH_PARENT, AbsListView.LayoutParams.WRAP_CONTENT);
//                        ListView lv = mFavoriteListView.getRefreshableView();
//                        LinearLayout mNewsDetailFootView = (LinearLayout) getLayoutInflater().inflate(R.layout.detail_footview_layout, null);
//                        mNewsDetailFootView.setLayoutParams(layoutParams);
//                        lv.addFooterView(mNewsDetailFootView);
//                    }
//                    mFavoriteListView.setVisibility(View.VISIBLE);
//
//                }
//                showBGLayout(false);
//
//
//            }
//        }, new Response.ErrorListener() {
//            @Override
//            public void onErrorResponse(VolleyError error) {
//                //这里有问题，比如点击进入详情页，后关闭网络回来会没有数据（实际是有数据的）
//
//                mFavoriteListView.setVisibility(View.GONE);
//                newsFeedList = new ArrayList<NewsFeed>();
//                mAdapter.setNewsFeed(newsFeedList);
//                mAdapter.notifyDataSetChanged();
//
//
//                showBGLayout(false);
//            }
//        });
//        HashMap<String, String> header = new HashMap<>();
//        header.put("Authorization", SharedPreManager.getUser(mContext).getAuthorToken());
//        header.put("Content-Type", "application/json");
//        header.put("X-Requested-With", "*");
//        request.setRequestHeader(header);
//        request.setRetryPolicy(new DefaultRetryPolicy(15000, 0, 0));
//        requestQueue.add(request);

    }

    @Override
    protected void onResume() {
        super.onResume();
        // AppBar的监听
        mAblAppBar.addOnOffsetChangedListener(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        // AppBar的监听
        mAblAppBar.removeOnOffsetChangedListener(this);
    }

    // 设置自动滑动的动画效果
    private void initParallaxValues() {
        CollapsingToolbarLayout.LayoutParams petDetailsLp =
                (CollapsingToolbarLayout.LayoutParams) mIvPlaceholder.getLayoutParams();
//
        CollapsingToolbarLayout.LayoutParams petBackgroundLp =
                (CollapsingToolbarLayout.LayoutParams) mFlTitleContainer.getLayoutParams();
//
        petDetailsLp.setParallaxMultiplier(0.9f);
        petBackgroundLp.setParallaxMultiplier(0.3f);

        mIvPlaceholder.setLayoutParams(petDetailsLp);
        mFlTitleContainer.setLayoutParams(petBackgroundLp);
    }





    @Override
    public void onClick(View view) {
//        mAttentionLeftBack,mAttentionRightMore, mAttention_btn
        switch (view.getId()) {
            case R.id.mAttentionLeftBack:
                finish();
                break;
            case R.id.mAttentionRightMore:

                break;
            case R.id.mAttention_btn:

                break;
        }

    }

    @Override
    public void onOffsetChanged(AppBarLayout appBarLayout, int i) {
        int maxScroll = appBarLayout.getTotalScrollRange();
        float percentage = (float) Math.abs(i) / (float) (maxScroll-DensityUtil.dip2px(this, 48));
        Logger.e("bbb", "i==" + i);
        Logger.e("bbb", "percentage==" + percentage);

        if(percentage<=1){
            mAttention_btn.setAlpha(1 - percentage);

            mAttention_btn.invalidate();

        }
    }

//

}

