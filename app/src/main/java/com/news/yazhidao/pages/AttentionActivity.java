package com.news.yazhidao.pages;


import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AbsListView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
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
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.news.yazhidao.R;
import com.news.yazhidao.adapter.NewsFeedAdapter;
import com.news.yazhidao.common.HttpConstant;
import com.news.yazhidao.entity.AttentionListEntity;
import com.news.yazhidao.entity.AttentionPbsEntity;
import com.news.yazhidao.entity.NewsFeed;
import com.news.yazhidao.entity.User;
import com.news.yazhidao.net.volley.DetailOperateRequest;
import com.news.yazhidao.net.volley.NewsDetailRequest;
import com.news.yazhidao.utils.Logger;
import com.news.yazhidao.utils.TextUtil;
import com.news.yazhidao.utils.ToastUtil;
import com.news.yazhidao.utils.manager.SharedPreManager;
import com.news.yazhidao.widget.AttentionDetailDialog;

import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;

public class AttentionActivity extends AppCompatActivity implements View.OnClickListener{

    public static final String KEY_ATTENTION_TITLE = "key_attention_title";
    public static final String KEY_ATTENTION_HEADIMAGE = "key_detail_headimage";
    public static final String KEY_ATTENTION_CONPUBFLAG = "key_detail_conpubflag";

    public static final int REQUEST_CODE = 1040;
    // 控制ToolBar的变量
    private static final float PERCENTAGE_TO_SHOW_TITLE_AT_TOOLBAR = 0.9f;
    private static final float PERCENTAGE_TO_HIDE_TITLE_DETAILS = 0.3f;

    private static final int ALPHA_ANIMATIONS_DURATION = 200;

    private boolean mIsTheTitleVisible = false;
    private boolean mIsTheTitleContainerVisible = true;
    private PullToRefreshListView mAttentionList;


    //    @Bind(R.id.main_iv_placeholder)
//    ImageView mIvPlaceholder; // 大图片

    //    @Bind(R.id.main_ll_title_container)
//    LinearLayout mLlTitleContainer; // Title的LinearLayout

    //    @Bind(R.id.main_fl_title)
//    FrameLayout mFlTitleContainer; // Title的FrameLayout

    //    @Bind(R.id.main_abl_app_bar)
//    AppBarLayout mAblAppBar; // 整个可以滑动的AppBar

//    @Bind(R.id.main_tv_toolbar_title)
//    TextView mTvToolbarTitle; // 标题栏Title

    //    @Bind(R.id.main_tb_toolbar)
//    Toolbar mTbToolbar; // 工具栏

    private TextView mAttentionLeftBack,
            mAttentionRightMore,
            mAttention_btn,
            tv_attention_title,
            mAttentionCenterTitle;

    private String mPName, mPUrl;

    private int conpubflag;

    private SimpleDraweeView iv_attention_headImage;

    private Context mContext;

    private RelativeLayout bgLayout,mAttentionTitleLayout;

    private User user;

    private NewsFeedAdapter mAdapter;
    ArrayList<NewsFeed> newsFeeds = new ArrayList<>();
    private int mPageIndex = 1;

    private boolean ismAttention;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_attention);
//        ButterKnife.bind(this);
        mContext = this;
        user = SharedPreManager.getUser(mContext);
        mPName = getIntent().getStringExtra(KEY_ATTENTION_TITLE);
        mPUrl = getIntent().getStringExtra(KEY_ATTENTION_HEADIMAGE);
        conpubflag = getIntent().getIntExtra(KEY_ATTENTION_CONPUBFLAG, 0);
        ismAttention = conpubflag == 1;
//        Logger.e("bbb", "mPName==" + mPName);
//        Logger.e("bbb", "mPUrl==" + mPUrl);
//        mPName = "蚕豆网";
//        mPUrl = "http://file3.u148.net/2011/4/images/1302139148470.jpg";
// ;

//        mIvPlaceholder = (ImageView) findViewById(R.id.main_iv_placeholder);
//        mLlTitleContainer = (LinearLayout) findViewById(R.id.main_ll_title_container);
//        mFlTitleContainer = (FrameLayout) findViewById(R.id.main_fl_title);
//        mAblAppBar = (AppBarLayout) findViewById(R.id.main_abl_app_bar);
////        mTvToolbarTitle = (TextView) findViewById(R.id.main_tv_toolbar_title);
//        mTbToolbar = (Toolbar) findViewById(R.id.main_tb_toolbar);

        bgLayout = (RelativeLayout) findViewById(R.id.bgLayout);
//        bgLayout.setVisibility(View.v);

        mAttentionLeftBack = (TextView) findViewById(R.id.mAttentionLeftBack);
        mAttentionRightMore = (TextView) findViewById(R.id.mAttentionRightMore);
        mAttention_btn = (TextView) findViewById(R.id.mAttention_btn);
        mAttentionCenterTitle = (TextView) findViewById(R.id.mAttentionCenterTitle);

        mAttentionCenterTitle.setText(mPName);
        mAttentionTitleLayout = (RelativeLayout) findViewById(R.id.mAttentionTitleLayout);

        mAttentionLeftBack.setOnClickListener(this);
        mAttentionRightMore.setOnClickListener(this);
        mAttention_btn.setOnClickListener(this);

        if(ismAttention){
            mAttention_btn.setText("已关注");
            mAttention_btn.setBackgroundResource(R.drawable.attention_tv_shape);
            mAttention_btn.setTextColor(getResources().getColor(R.color.unattention_line_color));


        }else{
            mAttention_btn.setText("关注");
            mAttention_btn.setBackgroundResource(R.drawable.unattention_tv_shape);
            mAttention_btn.setTextColor(getResources().getColor(R.color.attention_line_color));
        }

        mAttentionList = (PullToRefreshListView) findViewById(R.id.mAttentionList);
        mAttentionList.setMode(PullToRefreshBase.Mode.PULL_FROM_END);
//        mAttentionList.isHaveScrollView(true);
//        mAttentionList.setMainFooterView(true);

        mAttentionList.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ListView>() {
            @Override
            public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {

            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
                loadData();
            }
        });
        mAttentionList.setOnStateListener(new PullToRefreshBase.onStateListener() {
            @Override
            public void getState(PullToRefreshBase.State mState) {
                if (!isBottom) {
                    return;
                }
                boolean isVisisyProgressBar = false;
                switch (mState) {
                    case RESET://初始
                        isVisisyProgressBar = false;
                        footView_tv.setText("上拉获取更多文章");
                        break;
                    case PULL_TO_REFRESH://更多推荐
                        isVisisyProgressBar = false;
                        footView_tv.setText("上拉获取更多文章");
                        break;
                    case RELEASE_TO_REFRESH://松开推荐
                        isVisisyProgressBar = false;
                        footView_tv.setText("松手获取更多文章");
                        break;
                    case REFRESHING:
                    case MANUAL_REFRESHING://推荐中
                        isVisisyProgressBar = true;
                        footView_tv.setText("正在获取更多文章...");
                        break;
                    case OVERSCROLLING:
                        // NO-OP
                        break;
                }
                if (isVisisyProgressBar) {
                    footView_progressbar.setVisibility(View.VISIBLE);
                } else {
                    footView_progressbar.setVisibility(View.GONE);
                }
                mAttentionList.setFooterViewInvisible();
            }
        });

//        final String[] data = getResources().getStringArray(R.array.students);
//        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.text_item, data);


        mAdapter = new NewsFeedAdapter(this, null, newsFeeds);
        mAdapter.isAttention();
        mAttentionList.setAdapter(mAdapter);
        addListOtherView();
//        mTbToolbar.setTitle("");
        mAttentionList.setOnScrollListener(new AbsListView.OnScrollListener() {
            /**
             * ListView的状态改变时触发
             * @param view
             * @param scrollState
             */
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                switch (scrollState) {
                    // 当不滚动时
                    case AbsListView.OnScrollListener.SCROLL_STATE_IDLE:
                        // 判断滚动到底部
                        if (view.getLastVisiblePosition() == (view.getCount() - 1)) {
                            Log.e("aaa", "滑动到底部");
                            isBottom = true;
                        } else {
                            isBottom = false;
                            Logger.e("aaa", "在33333isBottom ==" + isBottom);
                        }
                        break;
                }
            }

            /**
             * 正在滚动
             * firstVisibleItem第一个Item的位置
             * visibleItemCount 可见的Item的数量
             * totalItemCount item的总数
             */
            @Override
            public void onScroll(AbsListView view, int firstVisibleItem,int visibleItemCount, int totalItemCount) {
//                Logger.e("aaa", "firstVisibleItem==" + firstVisibleItem);
//                Logger.e("aaa", "visibleItemCount==" + visibleItemCount);
//                Logger.e("aaa", "totalItemCount==" + totalItemCount);
//                if(){
//
//                }
                final int[] location = new int[2];
                tv_attention_title.getLocationInWindow(location);
                int mY = location[1];
                Logger.e("aaa", "gY====" + location[1]);


                final int[] locationTitle = new int[2];
                mAttentionCenterTitle.getLocationInWindow(locationTitle);
                int gY = locationTitle[1];
                Logger.e("aaa", "mY====" + locationTitle[1]);

                if(gY>=mY){
                    if(tv_attention_title.getVisibility() == View.VISIBLE){
                        tv_attention_title.setVisibility(View.INVISIBLE);
                    }
                    if(mAttentionCenterTitle.getVisibility() == View.GONE){
                        mAttentionCenterTitle.setVisibility(View.VISIBLE);
                    }
                    mAttentionTitleLayout.setBackgroundColor(getResources().getColor(R.color.bg_share));
                }else{
                    if(tv_attention_title.getVisibility() == View.INVISIBLE){
                        tv_attention_title.setVisibility(View.VISIBLE);
                    }
                    if(mAttentionCenterTitle.getVisibility() == View.VISIBLE){
                        mAttentionCenterTitle.setVisibility(View.GONE);
                    }
                    mAttentionTitleLayout.setBackgroundColor(getResources().getColor(R.color.transparent));
                }
                int margin = mY - gY;
                if(maxMargin<margin){
                    maxMargin = margin;
                }
                float proportion = (float) margin / (float)maxMargin;
                Logger.e("aaa", "proportion==" + proportion);
                mAttention_btn.setAlpha(proportion);
//                mAttentionTitleLayout.setAlpha();
            }
        });


//        initParallaxValues(); // 自动滑动效果
        loadData();
    }

    private int maxMargin;
    private boolean isBottom;
    private TextView attention_headViewItem_Content,
            footView_tv;
    private ImageView img_attention_line;
    private LinearLayout linear_attention_descrLayout;
    private ProgressBar footView_progressbar;
    public void addListOtherView(){
        LinearLayout attentionHeadView = (LinearLayout) getLayoutInflater().inflate(R.layout.attention_headview_item, null);
        ListView lv = mAttentionList.getRefreshableView();

        lv.addHeaderView(attentionHeadView);
        linear_attention_descrLayout = (LinearLayout) attentionHeadView.findViewById(R.id.linear_attention_descrLayout);
        attention_headViewItem_Content = (TextView) attentionHeadView.findViewById(R.id.attention_headViewItem_Content);
        img_attention_line = (ImageView) attentionHeadView.findViewById(R.id.img_attention_line);

        tv_attention_title = (TextView) attentionHeadView.findViewById(R.id.tv_attention_title);
        tv_attention_title.setText(mPName);

        iv_attention_headImage = (SimpleDraweeView) attentionHeadView.findViewById(R.id.iv_attention_headImage);
//        iv_attention_headImage.setImageURI(Uri.parse(mPUrl));



        final LinearLayout footerView = (LinearLayout) getLayoutInflater().inflate(R.layout.footerview_layout, null);
        lv.addFooterView(footerView);
        footView_tv = (TextView) footerView.findViewById(R.id.footerView_tv);
        footView_progressbar = (ProgressBar) footerView.findViewById(R.id.footerView_pb);




    }
    //    @Override
//    public void onWindowFocusChanged(boolean hasFocus) {
//        super.onWindowFocusChanged(hasFocus);
////        Logger.e("aaa","tv_attention_title=="+tv_attention_title.getWidth());
//        int textWidth = tv_attention_title.getWidth();
//        int ScreenWidth = DeviceInfoUtil.getScreenWidth(mContext);
//        int imageWidth = mContext.getResources().getDimensionPixelOffset(R.dimen.image_final_width);
//        int imageMargin = mContext.getResources().getDimensionPixelOffset(R.dimen.image_final_margin);
//
//        Logger.e("bbb","textWidth=="+textWidth);
//        Logger.e("bbb","ScreenWidth=="+ScreenWidth);
//        Logger.e("bbb","imageWidth=="+imageWidth);
//        Logger.e("bbb","imageMargin=="+imageMargin);
//
//        int widthSum = imageWidth + imageMargin + textWidth;
//
//        Logger.e("bbb","widthSum=="+widthSum);
//
//        int centerX = (ScreenWidth - widthSum) / 2;
//
//        Logger.e("bbb","centerX=="+centerX);
//
//        int imageX = centerX + imageWidth / 2;
//
//        Logger.e("bbb","imageX=="+imageX);
//
//        int textX = centerX + imageWidth + imageMargin + textWidth / 2;
//
//        Logger.e("bbb","textX=="+textX);
//        if(avatarImageBehavior == null){
//            avatarImageBehavior = new AvatarImageBehavior(mContext,imageX,imageWidth);
//            CoordinatorLayout.LayoutParams paramsAvatarImageBehavior =
//                    (CoordinatorLayout.LayoutParams) iv_attention_headImage.getLayoutParams();
//            paramsAvatarImageBehavior.setBehavior(avatarImageBehavior);
//        }
//        if(avatarTextBehavior == null){
//            avatarTextBehavior = new AvatarTextBehavior(mContext,textX,imageWidth);
//            CoordinatorLayout.LayoutParams paramsAvatarTextBehavior =
//                    (CoordinatorLayout.LayoutParams) tv_attention_title.getLayoutParams();
//            paramsAvatarTextBehavior.setBehavior(avatarTextBehavior);
//        }
//
//
//
//
//
//
//    }
//    AvatarImageBehavior avatarImageBehavior;
//    AvatarTextBehavior avatarTextBehavior;
    private void loadData() {
        Logger.e("jigang", "attention url=" + HttpConstant.URL_GETLIST_ATTENTION + "pname=" + mPName+"&info=1");
        RequestQueue requestQueue =  Volley.newRequestQueue(mContext);
        String pname = null;
        String  tstart = System.currentTimeMillis() - 1000 * 60 * 60 * 12 + "";
        try {
            pname = URLEncoder.encode(mPName, "utf-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        NewsDetailRequest<AttentionPbsEntity> feedRequest = new NewsDetailRequest<AttentionPbsEntity>(Request.Method.GET,
                new TypeToken<AttentionPbsEntity>() {
                }.getType(),
                HttpConstant.URL_GETLIST_ATTENTION + "pname=" + pname + "&info=1" + "&tcr=" + tstart + "&p=" + (mPageIndex++),
                new Response.Listener<AttentionPbsEntity>() {

                    @Override
                    public void onResponse(AttentionPbsEntity result) {
                        if(bgLayout.getVisibility() == View.VISIBLE){
                            bgLayout.setVisibility(View.GONE);
                        }
                        Logger.e("jigang", "result===" + result.toString());
//                        mAttentionList.onRefreshComplete();
                        AttentionListEntity attentionListEntity = result.getInfo();
                        String descr = attentionListEntity.getDescr();
                        if(!TextUtil.isEmptyString(descr)){
                            linear_attention_descrLayout.setVisibility(View.VISIBLE);
                            img_attention_line.setVisibility(View.VISIBLE);
                            attention_headViewItem_Content.setText(descr);
                        }else{
                            linear_attention_descrLayout.setVisibility(View.GONE);
                            img_attention_line.setVisibility(View.GONE);
                        }
                        String icon = attentionListEntity.getIcon();
                        if(!TextUtil.isEmptyString(icon)){
                            iv_attention_headImage.setImageURI(Uri.parse(icon));
                        }

                        if (!TextUtil.isListEmpty(result.getNews())) {
                            newsFeeds.addAll(result.getNews());
                            mAdapter.setNewsFeed(newsFeeds);
                            mAdapter.notifyDataSetChanged();
                            mAttentionList.onRefreshComplete();
//                            ToastUtil.toastShort("添加数据！");
                        } else {
//                            ToastUtil.toastShort("暂无相关数据！");
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
//                mAttentionList.onRefreshComplete();
                Logger.e("jigang", "network fail");
            }
        });

        feedRequest.setRetryPolicy(new DefaultRetryPolicy(15000, 0, 0));
        requestQueue.add(feedRequest);

    }

    boolean isNetWork;
    public void addordeleteAttention(final boolean isAttention){
        if(isNetWork){
            return;
        }
        isNetWork = true;
        String pname = null;

        try {
            pname = URLEncoder.encode(mPName, "utf-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        RequestQueue requestQueue = Volley.newRequestQueue(mContext);
        Logger.e("jigang", "attention url=" + HttpConstant.URL_ADDORDELETE_LOVE_COMMENT + "uid=" + user.getMuid() + "&pname="+pname);
        JSONObject json = new JSONObject();
        Logger.e("aaa","json+++++++++++++++++++++++"+json.toString());

        DetailOperateRequest request = new DetailOperateRequest( isAttention ? Request.Method.DELETE : Request.Method.POST,
                HttpConstant.URL_ADDORDELETE_ATTENTION+ "uid=" + user.getMuid() + "&pname="+pname
                , json.toString(), new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                String data = response.optString("data");
                Logger.e("aaa","json+++++++++++++++++++++++"+data);
                if(isAttention){
                    ismAttention = false;
                    mAttention_btn.setText("关注");
                    mAttention_btn.setBackgroundResource(R.drawable.unattention_tv_shape);
                    mAttention_btn.setTextColor(getResources().getColor(R.color.attention_line_color));
                }else{
                    ismAttention = true;
                    AttentionDetailDialog attentionDetailDialog = new AttentionDetailDialog(mContext,mPName );
                    attentionDetailDialog.show();
                    mAttention_btn.setText("已关注");
                    mAttention_btn.setBackgroundResource(R.drawable.attention_tv_shape);
                    mAttention_btn.setTextColor(getResources().getColor(R.color.unattention_line_color));
                }

//                ToastUtil.toastShort(isAttention ? "取消关注！！！！" : "添加关注");

                isNetWork = false;

//                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
//                mNewsDetailList.onRefreshComplete();
                Logger.e("jigang", "network fail");
                isNetWork = false;
            }
        });
        HashMap<String, String> header = new HashMap<>();
        header.put("Authorization", SharedPreManager.getUser(mContext).getAuthorToken());
        header.put("Content-Type", "application/json");
        header.put("X-Requested-With", "*");
        request.setRequestHeader(header);
        request.setRetryPolicy(new DefaultRetryPolicy(15000, 0, 0));
        requestQueue.add(request);



    }

    @Override
    protected void onResume() {
        super.onResume();
        // AppBar的监听
//        mAblAppBar.addOnOffsetChangedListener(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        // AppBar的监听
//        mAblAppBar.removeOnOffsetChangedListener(this);
    }

//    // 设置自动滑动的动画效果
//    private void initParallaxValues() {
//        CollapsingToolbarLayout.LayoutParams petDetailsLp =
//                (CollapsingToolbarLayout.LayoutParams) mIvPlaceholder.getLayoutParams();
////
//        CollapsingToolbarLayout.LayoutParams petBackgroundLp =
//                (CollapsingToolbarLayout.LayoutParams) mFlTitleContainer.getLayoutParams();
////
//        petDetailsLp.setParallaxMultiplier(0.9f);
//        petBackgroundLp.setParallaxMultiplier(0.3f);
//
//        mIvPlaceholder.setLayoutParams(petDetailsLp);
//        mFlTitleContainer.setLayoutParams(petBackgroundLp);
//    }





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
                if (user != null && user.isVisitor()) {
                    Intent loginAty = new Intent(mContext, LoginAty.class);
                    startActivityForResult(loginAty, REQUEST_CODE);
                } else {
                    addordeleteAttention(ismAttention);
                }
                break;
        }

    }

    @Override
    public void finish() {
        Intent in = new Intent();
        in.putExtra(KEY_ATTENTION_CONPUBFLAG, ismAttention);
        setResult(1234,in);
        super.finish();
    }

//    @Override
//    public void onOffsetChanged(AppBarLayout appBarLayout, int i) {
//        int maxScroll = appBarLayout.getTotalScrollRange();
//        float percentage = (float) Math.abs(i) / (float) (maxScroll-DensityUtil.dip2px(this, 48));
////        Logger.e("bbb", "i==" + i);
////        Logger.e("bbb", "percentage==" + percentage);
//
//        if(percentage<=1){
//            mAttention_btn.setAlpha(1 - percentage);
//            if(mAttention_btn.getVisibility() == View.GONE){
//                mAttention_btn.setVisibility(View.VISIBLE);
//            }
//        }else{
//            mAttention_btn.setVisibility(View.GONE);
//        }
//    }

//

}

