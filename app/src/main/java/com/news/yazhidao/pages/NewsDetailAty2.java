package com.news.yazhidao.pages;

import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.Html;
import android.text.method.ScrollingMovementMethod;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.widget.AbsListView;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.google.gson.reflect.TypeToken;
import com.news.yazhidao.R;
import com.news.yazhidao.adapter.NewsDetailELVAdapter;
import com.news.yazhidao.adapter.NewsFeedAdapter;
import com.news.yazhidao.common.BaseActivity;
import com.news.yazhidao.common.CommonConstant;
import com.news.yazhidao.common.HttpConstant;
import com.news.yazhidao.entity.AlbumSubItem;
import com.news.yazhidao.entity.NewsDetailAdd;
import com.news.yazhidao.entity.User;
import com.news.yazhidao.net.HttpClientUtil;
import com.news.yazhidao.net.JsonCallback;
import com.news.yazhidao.net.MyAppException;
import com.news.yazhidao.net.NetworkRequest;
import com.news.yazhidao.utils.DateUtil;
import com.news.yazhidao.utils.DensityUtil;
import com.news.yazhidao.utils.DeviceInfoUtil;
import com.news.yazhidao.utils.Logger;
import com.news.yazhidao.utils.TextUtil;
import com.news.yazhidao.utils.ToastUtil;
import com.news.yazhidao.utils.manager.SharedPreManager;
import com.news.yazhidao.widget.CommentPopupWindow;
import com.news.yazhidao.widget.NewsDetailHeaderView2;
import com.news.yazhidao.widget.SharePopupWindow;
import com.news.yazhidao.widget.UserCommentDialog;
import com.news.yazhidao.widget.swipebackactivity.SwipeBackLayout;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by fengjigang on 15/9/6.
 * 新闻展示详情页
 */
public class NewsDetailAty2 extends BaseActivity implements View.OnClickListener, CommentPopupWindow.IUpdateCommentCount, CommentPopupWindow.IUpdatePraiseCount, SharePopupWindow.ShareDismiss {

    public static final String KEY_IMAGE_WALL_INFO = "key_image_wall_info";

    private int mScreenWidth, mScreenHeight;
    //滑动关闭当前activity布局
    private SwipeBackLayout mSwipeBackLayout;
    private String mUserId = "";
    private String mPlatformType = "";
    private String uuid;
    private String mNewsDetailUrl;
    private ImageView mivShareBg;
    //新闻内容POJO
    private NewsDetailAdd mNewsDetailAdd;
    private ArrayList<ArrayList> mNewsContentDataList;
    private ArrayList<View> mImageViews;
    private ArrayList<HashMap<String, String>> mImages;
    private NewsDetailELVAdapter mNewsDetailELVAdapter;
    private NewsDetailHeaderView2 mDetailHeaderView;
    private ExpandableListView mDetailContentListView;
    private AlphaAnimation mAlphaAnimationIn, mAlphaAnimationOut;
    /**
     * 返回上一级,全文评论,分享
     */
    private View mDetailComment,mDetailHeader, mNewsDetailLoaddingWrapper;
    private ImageView mDetailLeftBack,mDetailShare;
    private ImageView mNewsLoadingImg;
    private AnimationDrawable mAniNewsLoading;
    private View mDetailView;
    private SharePopupWindow mSharePopupWindow;
    private ProgressBar mNewsDetailProgress;

    private float startY;
    private String mSource;
    private String newsId = null;
    private String newsType = null;
    private long mDurationStart;//统计用户读此条新闻时话费的时间
    private boolean isReadOver;//是否看完了全文,此处指的是翻到最下面
    private String channelId;
    private String mImgUrl;
    private View mDetailAddComment;
    private TextView mDetailCommentNum;
    private String mNewsType;
    private View mImageWallWrapper;
    private ViewPager mImageWallVPager;
    private TextView mImageWallDesc;
    private View mDetailBottomBanner;
    private ImageView mDetailCommentPic;

    @Override
    protected boolean translucentStatus() {
        return false;
    }

    @Override
    protected void setContentView() {
        setContentView(R.layout.aty_news_detail_layout);
        mScreenWidth = DeviceInfoUtil.getScreenWidth(this);
        mScreenHeight = DeviceInfoUtil.getScreenHeight(this);
        mNewsContentDataList = new ArrayList<>();
        mImageViews = new ArrayList<>();
        mNewsDetailELVAdapter = new NewsDetailELVAdapter(this, mNewsContentDataList);
        mAlphaAnimationIn = new AlphaAnimation(0, 1.0f);
        mAlphaAnimationIn.setDuration(500);
        mAlphaAnimationOut = new AlphaAnimation(1.0f, 0);
        mAlphaAnimationOut.setDuration(500);
    }

    @Override
    protected void initializeViews() {
//        mSwipeBackLayout = getSwipeBackLayout();
//        mSwipeBackLayout.setEdgeTrackingEnabled(SwipeBackLayout.EDGE_LEFT);
        mSource = getIntent().getStringExtra(NewsFeedFgt.KEY_NEWS_SOURCE);
        mDetailView = findViewById(R.id.mDetailWrapper);
        mDetailHeaderView = new NewsDetailHeaderView2(this);
        mNewsDetailLoaddingWrapper = findViewById(R.id.mNewsDetailLoaddingWrapper);
        mNewsLoadingImg = (ImageView) findViewById(R.id.mNewsLoadingImg);
        mNewsLoadingImg.setOnClickListener(this);
//        mNewsLoadingImg.setImageResource(R.drawable.loading_process_new_gif);
        mNewsDetailProgress = (ProgressBar) findViewById(R.id.mNewsDetailProgress);
        mivShareBg = (ImageView) findViewById(R.id.share_bg_imageView);
        mDetailHeader = findViewById(R.id.mDetailHeader);
        mDetailLeftBack = (ImageView) findViewById(R.id.mDetailLeftBack);
        mDetailLeftBack.setOnClickListener(this);
        mDetailComment =  findViewById(R.id.mDetailComment);
        mDetailCommentPic =  (ImageView)findViewById(R.id.mDetailCommentPic);
        mDetailComment.setOnClickListener(this);
        mDetailShare = (ImageView) findViewById(R.id.mDetailShare);
        mDetailShare.setOnClickListener(this);
        mDetailAddComment = findViewById(R.id.mDetailAddComment);
        mDetailAddComment.setOnClickListener(this);
        mDetailCommentNum = (TextView) findViewById(R.id.mDetailCommentNum);
        mDetailBottomBanner = findViewById(R.id.mDetailBottomBanner);
        mImageWallWrapper =  findViewById(R.id.mImageWallWrapper);
        mImageWallVPager = (ViewPager)findViewById(R.id.mImageWallVPager);
        mImageWallDesc = (TextView)findViewById(R.id.mImageWallDesc);
        mImageWallDesc.setMovementMethod(ScrollingMovementMethod.getInstance());

        mDetailContentListView = (ExpandableListView) findViewById(R.id.mDetailContentListView);
        mDetailContentListView.addHeaderView(mDetailHeaderView);
        mDetailContentListView.setAdapter(mNewsDetailELVAdapter);
        mDetailContentListView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                if (scrollState == SCROLL_STATE_IDLE){
                    if(view.getLastVisiblePosition() == view.getCount() - 1){
                        isReadOver = true;
                    }
                }
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
            }
        });
        //设置Listview默认展开
        expandedChildViews();
        //去掉Listview左边箭头
        mDetailContentListView.setGroupIndicator(null);
        //取消groupview 点击事件
        mDetailContentListView.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {
            @Override
            public boolean onGroupClick(ExpandableListView parent, View v, int groupPosition, long id) {
                return true;
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        mDurationStart = System.currentTimeMillis();
    }

    @Override
    protected void onStop() {
        super.onStop();
        long readDuration = System.currentTimeMillis() - mDurationStart;
        Logger.e("jigang","time = "+ DateUtil.getDate()+",read duration = " + readDuration + ",readOver = " + isReadOver + ",newsid ="+newsId+",type="+newsType +",channelId =" +channelId+ ",uuid="+uuid+",userid="+mUserId+",location="+SharedPreManager.get(CommonConstant.FILE_USER_LOCATION,CommonConstant.KEY_USER_LOCATION));
    }


    @Override
    protected void loadData() {
//        mNewsLoadingImg.setImageResource(R.drawable.loading_process_new_gif);
//        mAniNewsLoading = (AnimationDrawable) mNewsLoadingImg.getDrawable();
//        mAniNewsLoading.start();
        mNewsLoadingImg.setVisibility(View.GONE);
        mNewsDetailProgress.setVisibility(View.VISIBLE);
        final Bundle bundle = getIntent().getBundleExtra(AlbumListAty.KEY_BUNDLE);
        boolean isDigger = false;
        AlbumSubItem albumSubItem;
        if (bundle != null) {
            isDigger = bundle.getBoolean(AlbumListAty.KEY_IS_DIGGER);
            albumSubItem = (AlbumSubItem) bundle.getSerializable(AlbumListAty.KEY_ALBUMSUBITEM);
            newsId = albumSubItem.getInserteId();

        }else {
            newsId = getIntent().getStringExtra(NewsFeedFgt.KEY_NEWS_ID);
            newsType = getIntent().getStringExtra(NewsFeedFgt.KEY_COLLECTION);
            channelId = getIntent().getStringExtra(NewsFeedFgt.KEY_CHANNEL_ID);
            mImgUrl = getIntent().getStringExtra(NewsFeedFgt.KEY_NEWS_IMG_URL);
            mNewsType = getIntent().getStringExtra(NewsFeedFgt.KEY_NEWS_TYPE);
//            mNewsType = "big_pic";
            mNewsDetailELVAdapter.setNewsImgUrl(mImgUrl);
//            newsId = "ebc3a57f2c3a5153103a1b1875f25a96";
//            newsType = "googleNewsItem";
            Logger.e("jigang","newsid ="+newsId+",type="+newsType);
        }
        User user = SharedPreManager.getUser(NewsDetailAty2.this);
        if (user != null) {
            mUserId = user.getUserId();
            mPlatformType = user.getPlatformType();
        }
        mNewsDetailUrl = getIntent().getStringExtra(NewsFeedFgt.KEY_URL);
        mNewsDetailELVAdapter.setNewsUrl(mNewsDetailUrl);
        uuid = DeviceInfoUtil.getUUID();

        NetworkRequest _Request = new NetworkRequest(HttpConstant.URL_POST_NEWS_DETAIL, NetworkRequest.RequestMethod.POST);
        List<NameValuePair> pairs = new ArrayList<>();
        /**是否是挖掘的新闻*/
        if (isDigger) {
            pairs.add(new BasicNameValuePair("news_id", newsId));
        } else {
            pairs.add(new BasicNameValuePair("newsid", newsId));
            pairs.add(new BasicNameValuePair("devicetype", "android"));
            pairs.add(new BasicNameValuePair("collection", newsType));
        }
        pairs.add(new BasicNameValuePair("userid", mUserId));
        pairs.add(new BasicNameValuePair("deviceid", uuid));
        pairs.add(new BasicNameValuePair("platformtype", mPlatformType));
        _Request.setParams(pairs);
        _Request.setCallback(new JsonCallback<NewsDetailAdd>() {

            @Override
            public void success(final NewsDetailAdd result) {
                mNewsDetailLoaddingWrapper.setVisibility(View.GONE);
//                    StringBuilder builder = new StringBuilder();
//                    try {
//                    InputStream open = NewsDetailAty2.this.getAssets().open("test.json");
//                        byte[] buffer = new byte[1024 * 8];
//                        int length = 0;
//                        while ((length = open.read(buffer)) != -1) {
//                            builder.append(new String(buffer, 0, length));
//                        }
//                    } catch (IOException e) {
//                        e.printStackTrace();
//                    }
//                    ArrayList<LinkedTreeMap<String,HashMap<String,String>>> content  = GsonUtil.deSerializedByType(builder.toString(), new TypeToken<ArrayList<LinkedTreeMap<String,HashMap<String,String>>>>() {
//                    }.getType());
//                    Logger.e("jigang","--content="+content);
//                    result.content = content;
                mNewsDetailAdd = result;
                mNewsDetailELVAdapter.setNewsDetail(result);
                if (result != null) {
                    //此处判断是否是图片新闻
                    if ("big_pic".equals(mNewsType)){
                        if (!TextUtil.isListEmpty(result.imgWall)){
                            mImages = result.imgWall;
                            //隐藏listview 展示imagewall fragment
                            mDetailContentListView.setVisibility(View.GONE);
                            mImageWallWrapper.setVisibility(View.VISIBLE);
                            configViewPagerViews();
                        }else {
                            mDetailContentListView.setVisibility(View.VISIBLE);
                            mImageWallWrapper.setVisibility(View.GONE);
                        }

                    }
                    mDetailHeaderView.updateView(result);
                    if(!TextUtil.isListEmpty(result.point)){
                        mDetailCommentNum.setVisibility(View.VISIBLE);
                        mDetailCommentNum.setText(result.point.size()+"");
                    }
                    TextUtil.parseNewsDetail(mNewsContentDataList,result,mImgUrl);
                    //设置Listview默认展开
                    for (int i = 0; i < mNewsDetailELVAdapter.getGroupCount(); i++) {
                        mDetailContentListView.expandGroup(i);
                    }
                }else {
                    ToastUtil.toastShort("此新闻暂时无法查看!");
                    NewsDetailAty2.this.finish();
                }
            }

            @Override
            public void failed(MyAppException exception) {
                Logger.e("jigang", "network fail");
                mNewsLoadingImg.setVisibility(View.VISIBLE);
                mNewsDetailProgress.setVisibility(View.GONE);
            }
        }.setReturnType(new TypeToken<NewsDetailAdd>() {
        }.getType()));
        _Request.execute();
    }

    @Override
    public void finish() {
        Intent intent = new Intent();
        intent.putExtra(NewsFeedAdapter.KEY_NEWS_ID,newsId);
        setResult(NewsFeedAdapter.REQUEST_CODE,intent);
        super.finish();
        //如果是后台推送新闻消息过来的话，关闭新闻详情页的时候，就会打开主页面
        if (NewsFeedFgt.VALUE_NEWS_NOTIFICATION.equals(mSource)) {
            Intent main = new Intent(this, MainAty.class);
            startActivity(main);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.mDetailLeftBack:
                onBackPressed();
                break;
            case R.id.mDetailAddComment:
                UserCommentDialog commentDialog = new UserCommentDialog(this);
                commentDialog.show(this.getSupportFragmentManager(), "UserCommentDialog");
                break;
            case R.id.mDetailComment:
                ArrayList<NewsDetailAdd.Point> points;
                if (mNewsDetailAdd != null && !TextUtil.isListEmpty(mNewsDetailAdd.point)) {
                    points = mNewsDetailAdd.point;
                } else {
                    points = null;
                }
                mivShareBg.startAnimation(mAlphaAnimationIn);
                mivShareBg.setVisibility(View.VISIBLE);
                CommentPopupWindow window = new CommentPopupWindow(this, points, mNewsDetailUrl, this, -1, this, this);
                window.setFocusable(true);
                //防止虚拟软键盘被弹出菜单遮住
                window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
                window.showAtLocation(mDetailView, Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
                break;
            case R.id.mDetailShare:
                mivShareBg.startAnimation(mAlphaAnimationIn);
                mivShareBg.setVisibility(View.VISIBLE);
                mSharePopupWindow = new SharePopupWindow(this, this);
                // FIXME: 15/11/5 有可能以后分享的时候有问题,遇到问题后改之
                String type = "1", remark = "1";
                HashMap<String, Object> hashMap = new HashMap<>();
                hashMap.put("newsid", newsId);
                hashMap.put("type", type);
                hashMap.put("collection", newsType);
                String url = HttpClientUtil.addParamsToUrl("http://deeporiginalx.com/news.html?", hashMap);
                mSharePopupWindow.setTitleAndUrl(mNewsDetailAdd.title, url,remark);
                mSharePopupWindow.showAtLocation(mDetailView, Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
                break;
            case R.id.mNewsLoadingImg:
                loadData();
                break;
        }
    }

    /**
     * 展开所有的childview
     */
    private void expandedChildViews() {
        for (int i = 0; i < mNewsDetailELVAdapter.getGroupCount(); i++) {
            mDetailContentListView.expandGroup(i);
        }
    }

    @Override
    public void updateCommentCount(NewsDetailAdd.Point point) {
        if (mNewsDetailAdd != null) {
            if (mNewsDetailAdd.point == null) {
                ArrayList<NewsDetailAdd.Point> list = new ArrayList<>();
                list.add(point);
                mNewsDetailAdd.point = list;
            } else {
                mNewsDetailAdd.point.add(point);
            }
            mNewsContentDataList = TextUtil.parseNewsDetail(mNewsContentDataList,mNewsDetailAdd,mImgUrl);
        }
        //更新评论显示数字
        mDetailCommentNum.setText(mNewsDetailAdd.point.size() + "");
        mDetailCommentNum.setVisibility(View.VISIBLE);
        mNewsDetailELVAdapter.notifyDataSetChanged();
        //设置Listview默认展开
        expandedChildViews();
    }


    @Override
    public void updataPraise() {

    }

    @Override
    public void shareDismiss() {
        mivShareBg.startAnimation(mAlphaAnimationOut);
        mivShareBg.setVisibility(View.INVISIBLE);
    }

    private void configViewPagerViews(){
        mDetailHeader.setBackgroundColor(getResources().getColor(R.color.black));
        mDetailBottomBanner.setBackgroundColor(getResources().getColor(R.color.black));
        mDetailAddComment.setBackgroundResource(R.drawable.user_add_comment_black);
        int padding = DensityUtil.dip2px(this,8);
        mDetailLeftBack.setImageResource(R.drawable.btn_detail_left_white);
        mDetailCommentPic.setImageResource(R.drawable.btn_detail_comment_white);
        mDetailShare.setImageResource(R.drawable.btn_detail_share_white);
        mDetailAddComment.setPadding(padding,padding,padding,padding);
        for (int i = 0; i < mImages.size(); i++) {
            final SimpleDraweeView imageView = new SimpleDraweeView(this);
            ViewGroup.LayoutParams  params = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            imageView.setLayoutParams(params);
            mImageViews.add(imageView);
            imageView.setImageURI(Uri.parse(mImages.get(i).get("img")));
        }
        mImageWallVPager.setAdapter(new ImagePagerAdapter(mImageViews));
        mImageWallDesc.setText(Html.fromHtml(1 + "<small>" + "/" + mImages.size() + "</small>" + "&nbsp;&nbsp;&nbsp;"+mImages.get(0).get("note")));
        mImageWallVPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener(){
            @Override
            public void onPageSelected(int position) {
                mImageWallDesc.setText(Html.fromHtml(position + 1 + "<small>" + "/" + mImages.size() + "</small>" + "&nbsp;&nbsp;&nbsp;"+mImages.get(position).get("note")));
            }
        });
    }

    class ImagePagerAdapter extends PagerAdapter {


        private List<View> views = new ArrayList<View>();

        public ImagePagerAdapter(List<View> views) {
            this.views = views;
        }

        @Override
        public boolean isViewFromObject(View arg0, Object arg1) {
            return arg0 == arg1;
        }

        @Override
        public int getCount() {
            return views.size();
        }

        @Override
        public void destroyItem(View container, int position, Object object) {
            ((ViewPager) container).removeView(views.get(position));
        }

        @Override
        public Object instantiateItem(View container, int position) {
            ((ViewPager) container).addView(views.get(position));
            return views.get(position);
        }
    }
}
