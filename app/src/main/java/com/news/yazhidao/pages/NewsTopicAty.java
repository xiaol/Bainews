package com.news.yazhidao.pages;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Handler;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.bumptech.glide.Glide;
import com.google.gson.reflect.TypeToken;
import com.news.yazhidao.R;
import com.news.yazhidao.application.YaZhiDaoApplication;
import com.news.yazhidao.common.CommonConstant;
import com.news.yazhidao.common.HttpConstant;
import com.news.yazhidao.entity.NewsFeed;
import com.news.yazhidao.entity.NewsTopic;
import com.news.yazhidao.entity.TopicBaseInfo;
import com.news.yazhidao.entity.TopicClass;
import com.news.yazhidao.net.volley.NewsTopicRequestGet;
import com.news.yazhidao.utils.DensityUtil;
import com.news.yazhidao.utils.DeviceInfoUtil;
import com.news.yazhidao.utils.NetUtil;
import com.news.yazhidao.utils.TextUtil;
import com.news.yazhidao.utils.manager.SharedPreManager;
import com.news.yazhidao.widget.NewsTopicHeaderView;
import com.news.yazhidao.widget.SharePopupWindow;
import com.news.yazhidao.widget.TextViewExtend;
import com.news.yazhidao.widget.swipebackactivity.SwipeBackActivity;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by fengjigang on 16/4/6.
 */
public class NewsTopicAty extends SwipeBackActivity implements View.OnClickListener, SharePopupWindow.ShareDismiss {

    public static final int REQUEST_CODE = 1006;
    public static final String KEY_NID = "key_nid";
    private RelativeLayout bgLayout;
    private int mtid;
    private long mFirstClickTime;
    private ExpandableSpecialListViewAdapter mAdapter;
    private Context mContext;
    private ImageView mTopicLeftBack, mNewsLoadingImg, mivShareBg;
    private TextView mTopicRightMore;
    private View mNewsDetailLoaddingWrapper;
    private ExpandableListView mlvSpecialNewsFeed;
    //    private ExpandableListView mExpandableListView;
    private boolean isListRefresh;
    private TopicBaseInfo mTopicBaseInfo;
    private ArrayList<TopicClass> marrTopicClass;
    private NewsTopic mNewTopic;
    private Handler mHandler;
    private SharedPreferences mSharedPreferences;
    private int mScreenWidth, mCardWidth, mCardHeight;
    private NewsTopicHeaderView mSpecialNewsHeaderView;
    private TextView mTopicTitle;
    private AlphaAnimation mAlphaAnimationIn, mAlphaAnimationOut;

    @Override
    protected void setContentView() {
        setContentView(R.layout.aty_news_topic);
        mContext = this;
    }

    @Override
    protected void initializeViews() {
        mAlphaAnimationIn = new AlphaAnimation(0, 1.0f);
        mAlphaAnimationIn.setDuration(500);
        mAlphaAnimationOut = new AlphaAnimation(1.0f, 0);
        mAlphaAnimationOut.setDuration(500);
        mtid = getIntent().getIntExtra(KEY_NID, 0);
        mScreenWidth = DeviceInfoUtil.getScreenWidth();
        mSharedPreferences = mContext.getSharedPreferences("showflag", 0);
        mCardWidth = (int) ((mScreenWidth - DensityUtil.dip2px(mContext, 32)) / 3.0f);
        mCardHeight = (int) (mCardWidth * 213 / 326.0f);
        mAdapter = new ExpandableSpecialListViewAdapter(this);
        mHandler = new Handler();
        mSpecialNewsHeaderView = new NewsTopicHeaderView(this);
        mNewsDetailLoaddingWrapper = findViewById(R.id.mNewsDetailLoaddingWrapper);
        bgLayout = (RelativeLayout) findViewById(R.id.bgLayout);
        mTopicTitle = (TextView) findViewById(R.id.mTopicTitle);
        mNewsLoadingImg = (ImageView) findViewById(R.id.mNewsLoadingImg);
        mNewsLoadingImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadData();
            }
        });
        mlvSpecialNewsFeed = (ExpandableListView) findViewById(R.id.news_Topic_listView);
        mlvSpecialNewsFeed.setAdapter(mAdapter);
        mlvSpecialNewsFeed.addHeaderView(mSpecialNewsHeaderView);
        mivShareBg = (ImageView) findViewById(R.id.share_bg_imageView);
//        mlvSpecialNewsFeed.setMode(PullToRefreshBase.Mode.DISABLED);
//        mlvSpecialNewsFeed.setMainFooterView(true);
//        mExpandableListView = mlvSpecialNewsFeed.getRefreshableView();
//        mExpandableListView.setAdapter(mAdapter);
//        mExpandableListView.addHeaderView(mSpecialNewsHeaderView);
//        mlvSpecialNewsFeed.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ExpandableListView>() {
//            @Override
//            public void onPullDownToRefresh(PullToRefreshBase<ExpandableListView> refreshView) {
//                isListRefresh = true;
//                loadData();
//            }
//
//            @Override
//            public void onPullUpToRefresh(PullToRefreshBase<ExpandableListView> refreshView) {
//                isListRefresh = true;
//                loadData();
//            }
//        });
        mlvSpecialNewsFeed.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {

            @Override
            public boolean onGroupClick(ExpandableListView parent, View v,
                                        int groupPosition, long id) {
                // TODO Auto-generated method stub
                return true;
            }
        });
//        mlvSpecialNewsFeed.getRefreshableView().addHeaderView(mSpecialNewsHeaderView);
        mTopicLeftBack = (ImageView) findViewById(R.id.mTopicLeftBack);
        mTopicLeftBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        mTopicRightMore = (TextView) findViewById(R.id.mTopicRightMore);
        mTopicRightMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mivShareBg.startAnimation(mAlphaAnimationIn);
                mivShareBg.setVisibility(View.VISIBLE);
                SharePopupWindow mSharePopupWindow = new SharePopupWindow(NewsTopicAty.this, NewsTopicAty.this);
//                String url = "http://deeporiginalx.com/news.html?type=0" + "&url=" + TextUtil.getBase64(mNewsFeed.getUrl()) + "&interface";
                mSharePopupWindow.setTitleAndNid(mTopicBaseInfo.getName(), mtid, "");
                mSharePopupWindow.setTopic(true);
                mSharePopupWindow.setFavoriteGone();
//                mSharePopupWindow.setOnFavoritListener(listener);
                mSharePopupWindow.showAtLocation(NewsTopicAty.this.getCurrentFocus(), Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
            }
        });
    }

    @Override
    protected boolean translucentStatus() {
        return false;
    }


    @Override
    protected void loadData() {
        if (!isListRefresh) {
            bgLayout.setVisibility(View.VISIBLE);
        }
        String requestUrl = HttpConstant.URL_NEWS_TOPIC + "tid=" + mtid;
        if (NetUtil.checkNetWork(mContext)) {
            RequestQueue requestQueue = YaZhiDaoApplication.getInstance().getRequestQueue();
            NewsTopicRequestGet<NewsTopic> topicRequestGet = new NewsTopicRequestGet<>(Request.Method.GET, new TypeToken<NewsTopic>() {
            }.getType(), requestUrl, new Response.Listener<NewsTopic>() {

                @Override
                public void onResponse(final NewsTopic result) {
                    mNewsDetailLoaddingWrapper.setVisibility(View.GONE);
                    mNewTopic = result;
                    mTopicBaseInfo = mNewTopic.getTopicBaseInfo();
                    mTopicTitle.setText(mTopicBaseInfo.getName());
                    marrTopicClass = mNewTopic.getTopicClass();
                    for (int i = 0; i < marrTopicClass.size(); i++) {
                        mlvSpecialNewsFeed.expandGroup(i);
                    }
                    mSpecialNewsHeaderView.setHeaderViewData(mTopicBaseInfo, mScreenWidth);
                    mAdapter.setTopicData(marrTopicClass);
                    mAdapter.notifyDataSetChanged();
                    bgLayout.setVisibility(View.GONE);
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    mNewsLoadingImg.setVisibility(View.VISIBLE);
                    bgLayout.setVisibility(View.GONE);
                }
            });
            HashMap<String, String> header = new HashMap<>();
            header.put("Authorization", SharedPreManager.getUser(mContext).getAuthorToken());
            topicRequestGet.setRequestHeader(header);
            topicRequestGet.setRetryPolicy(new DefaultRetryPolicy(15000, 0, 0));
            requestQueue.add(topicRequestGet);
        } else {
            mNewsDetailLoaddingWrapper.setVisibility(View.VISIBLE);
            mNewsLoadingImg.setVisibility(View.VISIBLE);
            setRefreshComplete();
//            ArrayList<NewsFeed> newsFeeds = mNewsFeedDao.queryByChannelId(mstrChannelId);
//            if (TextUtil.isListEmpty(newsFeeds)) {
//                mHomeRetry.setVisibility(View.VISIBLE);
//            } else {
//                mHomeRetry.setVisibility(View.GONE);
//            }
//            mAdapter.setNewsFeed(newsFeeds);
//            mAdapter.notifyDataSetChanged();
            if (bgLayout.getVisibility() == View.VISIBLE) {
                bgLayout.setVisibility(View.GONE);
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    private void setRefreshComplete() {
//        mHandler.postDelayed(new Runnable() {
//            @Override
//            public void run() {
////                mlvSpecialNewsFeed.onRefreshComplete();
////            }
//        }, 500);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
//            case R.id.mLoginWeibo:
//                if (System.currentTimeMillis() - mFirstClickTime < 2000) {
//                    return;
//                }
//                mFirstClickTime = System.currentTimeMillis();
//                break;
//            case R.id.mLoginWeixin:
//                if (System.currentTimeMillis() - mFirstClickTime < 2000) {
//                    return;
//                }
//                mFirstClickTime = System.currentTimeMillis();
//                break;
//            case R.id.mLoginCancel:
//                this.finish();
//                break;
//            case R.id.mLoginSetting:
//                Intent settingAty = new Intent(this, SettingAty.class);
//                startActivity(settingAty);
//                this.finish();
//                break;
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    public void shareDismiss() {
        mivShareBg.startAnimation(mAlphaAnimationOut);
        mivShareBg.setVisibility(View.INVISIBLE);
//        isFavorite = SharedPreManager.myFavoriteisSame(mUrl);
//        if (isFavorite) {
//            mDetailFavorite.setImageResource(R.drawable.btn_detail_favorite_select);
//        } else {
//            mDetailFavorite.setImageResource(R.drawable.btn_detail_favorite_normal);
//        }
    }

    public class ExpandableSpecialListViewAdapter extends BaseExpandableListAdapter {

        private Context mContext;
        private ArrayList<TopicClass> arrTopicClass;
        private int currentType;

        public ExpandableSpecialListViewAdapter(Context context) {
            mContext = context;
        }

        public void setTopicData(ArrayList<TopicClass> topicClass) {
            arrTopicClass = topicClass;
        }

        @Override
        public int getGroupCount() {
            // TODO Auto-generated method stub
            return arrTopicClass == null ? 0 : arrTopicClass.size();
        }

        @Override
        public int getChildrenCount(int groupPosition) {
            // TODO Auto-generated method stub
            return arrTopicClass == null ? 0 : arrTopicClass.get(groupPosition).getNewsFeed().size();
        }

        @Override
        public Object getGroup(int groupPosition) {
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        public Object getChild(int groupPosition, int childPosition) {
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        public long getGroupId(int groupPosition) {
            // TODO Auto-generated method stub
            return groupPosition;
        }

        @Override
        public long getChildId(int groupPosition, int childPosition) {
            // TODO Auto-generated method stub
            return childPosition;
        }

        @Override
        public boolean hasStableIds() {
            // TODO Auto-generated method stub
            return true;
        }

        @Override
        public int getChildType(int groupPosition, int childPosition) {
            NewsFeed feed = arrTopicClass.get(groupPosition).getNewsFeed().get(childPosition);
            int type = feed.getStyle();
            if (0 == type) {
                return NewsFeed.NO_PIC;
            } else if (1 == type || 2 == type) {
                return NewsFeed.ONE_AND_TWO_PIC;
            } else if (3 == type) {
                return NewsFeed.THREE_PIC;
            } else if (11 == type || 12 == type || 13 == type) {
                return NewsFeed.BIG_PIC;
            } else {
                return NewsFeed.EMPTY;
            }
        }

        @Override
        public int getChildTypeCount() {
            return 5;
        }

        @Override
        public boolean isChildSelectable(int groupPosition, int childPosition) {
            // TODO Auto-generated method stub
            return true;
        }

        @Override
        public View getGroupView(final int groupPosition, boolean isExpanded,
                                 View convertView, ViewGroup parent) {
            TopicClass.TopicClassBaseInfo topicClassBaseInfo = arrTopicClass.get(groupPosition).getTopicClassBaseInfo();
            final GroupHolder groupHolder;
            if (convertView == null || convertView.getTag().getClass() != GroupHolder.class) {
                groupHolder = new GroupHolder();
                convertView = LayoutInflater.from(mContext).inflate(R.layout.item_news_topic_group, null, false);
                groupHolder.ivColor = (ImageView) convertView.findViewById(R.id.mGroupColor);
                groupHolder.tvTitle = (TextView) convertView.findViewById(R.id.mGroupTitle);
                convertView.setTag(groupHolder);
            } else {
                groupHolder = (GroupHolder) convertView.getTag();
            }
            groupHolder.tvTitle.setText(topicClassBaseInfo.getName());
            groupHolder.ivColor.setBackgroundColor(getResources().getColor(R.color.new_color2));
            convertView.setOnClickListener(null);
            return convertView;
        }


        @Override
        public View getChildView(final int groupPosition, final int childPosition,
                                 boolean isLastChild, View convertView, ViewGroup parent) {
            NewsFeed feed = arrTopicClass.get(groupPosition).getNewsFeed().get(childPosition);
            ChildNoPicHolder childNoPicHolderHolder;
            final ChildOnePicHolder childOnePicHolder;
            ChildThreePicHolder childThreePicHolder;
            ChildBigPicHolder childBigPicHolder;
            View vNoPic;
            View vOnePic;
            View vThreePic;
            View vBigPic;
            View vEmpty;
            currentType = getChildType(groupPosition, childPosition);
            if (currentType == NewsFeed.NO_PIC) {
                if (convertView == null) {
                    childNoPicHolderHolder = new ChildNoPicHolder();
                    vNoPic = LayoutInflater.from(mContext).inflate(R.layout.ll_news_topic_item_no_pic, null);
                    childNoPicHolderHolder.rlContent = (RelativeLayout) vNoPic.findViewById(R.id.news_content_relativeLayout);
                    childNoPicHolderHolder.tvTitle = (TextView) vNoPic.findViewById(R.id.title_textView);
                    childNoPicHolderHolder.tvSource = (TextViewExtend) vNoPic.findViewById(R.id.news_source_TextView);
                    childNoPicHolderHolder.tvCommentNum = (TextViewExtend) vNoPic.findViewById(R.id.comment_num_textView);
                    childNoPicHolderHolder.tvType = (TextViewExtend) vNoPic.findViewById(R.id.type_textView);
                    childNoPicHolderHolder.ivDelete = (ImageView) vNoPic.findViewById(R.id.delete_imageView);
                    childNoPicHolderHolder.ivBottomLine = (ImageView) vNoPic.findViewById(R.id.line_bottom_imageView);
                    vNoPic.findViewById(R.id.checkFavoriteDelete_image).setVisibility(View.GONE);
                    vNoPic.setTag(childNoPicHolderHolder);
                    convertView = vNoPic;
                } else {
                    childNoPicHolderHolder = (ChildNoPicHolder) convertView.getTag();
                }
                setTitleTextBySpannable(childNoPicHolderHolder.tvTitle, feed.getTitle(), false);
                setSourceViewText(childNoPicHolderHolder.tvSource, feed.getPname());
                setCommentViewText(childNoPicHolderHolder.tvCommentNum, feed.getComment() + "");
                setNewsContentClick(childNoPicHolderHolder.rlContent, feed);
                newsTag(childNoPicHolderHolder.tvType, feed.getRtype());
                childNoPicHolderHolder.ivDelete.setVisibility(View.INVISIBLE);
                setBottomLine(childNoPicHolderHolder.ivBottomLine, getChildrenCount(groupPosition), childPosition);
            } else if (currentType == NewsFeed.ONE_AND_TWO_PIC) {
                if (convertView == null) {
                    childOnePicHolder = new ChildOnePicHolder();
                    vOnePic = LayoutInflater.from(mContext).inflate(R.layout.ll_news_topic_item_one_pic, null);
                    childOnePicHolder.ivPicture = (ImageView) vOnePic.findViewById(R.id.title_img_View);
                    childOnePicHolder.rlContent = (RelativeLayout) vOnePic.findViewById(R.id.news_content_relativeLayout);
                    childOnePicHolder.tvTitle = (TextView) vOnePic.findViewById(R.id.title_textView);
                    childOnePicHolder.tvSource = (TextViewExtend) vOnePic.findViewById(R.id.news_source_TextView);
                    childOnePicHolder.tvCommentNum = (TextViewExtend) vOnePic.findViewById(R.id.comment_num_textView);
                    childOnePicHolder.tvType = (TextViewExtend) vOnePic.findViewById(R.id.type_textView);
                    childOnePicHolder.ivDelete = (ImageView) vOnePic.findViewById(R.id.delete_imageView);
                    childOnePicHolder.llSourceOnePic = (LinearLayout) vOnePic.findViewById(R.id.source_content_linearLayout);
                    childOnePicHolder.ivBottomLine = (ImageView) vOnePic.findViewById(R.id.line_bottom_imageView);
                    vOnePic.findViewById(R.id.checkFavoriteDelete_image).setVisibility(View.GONE);
                    vOnePic.setTag(childOnePicHolder);
                    convertView = vOnePic;
                } else {
                    childOnePicHolder = (ChildOnePicHolder) convertView.getTag();
                }
                RelativeLayout.LayoutParams lpCard = (RelativeLayout.LayoutParams) childOnePicHolder.ivPicture.getLayoutParams();
                lpCard.width = mCardWidth;
                lpCard.height = mCardHeight;
                childOnePicHolder.ivPicture.setLayoutParams(lpCard);
                setImageUri(childOnePicHolder.ivPicture, feed.getImgs().get(0), mCardWidth, mCardHeight, feed.getRtype());
                setTitleTextBySpannable(childOnePicHolder.tvTitle, feed.getTitle(), false);
                setSourceViewText(childOnePicHolder.tvSource, feed.getPname());
                setCommentViewText(childOnePicHolder.tvCommentNum, feed.getComment() + "");
                setNewsContentClick(childOnePicHolder.rlContent, feed);
                newsTag(childOnePicHolder.tvType, feed.getRtype());
                childOnePicHolder.ivDelete.setVisibility(View.INVISIBLE);
                setBottomLine(childOnePicHolder.ivBottomLine, getChildrenCount(groupPosition), childPosition);
            } else if (currentType == NewsFeed.THREE_PIC) {
                if (convertView == null) {
                    childThreePicHolder = new ChildThreePicHolder();
                    vThreePic = LayoutInflater.from(mContext).inflate(R.layout.ll_news_topic_card, null);
                    childThreePicHolder.ivPicture1 = (ImageView) vThreePic.findViewById(R.id.image_card1);
                    childThreePicHolder.ivPicture2 = (ImageView) vThreePic.findViewById(R.id.image_card2);
                    childThreePicHolder.ivPicture3 = (ImageView) vThreePic.findViewById(R.id.image_card3);
                    childThreePicHolder.rlContent = (RelativeLayout) vThreePic.findViewById(R.id.news_content_relativeLayout);
                    childThreePicHolder.tvTitle = (TextView) vThreePic.findViewById(R.id.title_textView);
                    childThreePicHolder.tvSource = (TextViewExtend) vThreePic.findViewById(R.id.news_source_TextView);
                    childThreePicHolder.tvCommentNum = (TextViewExtend) vThreePic.findViewById(R.id.comment_num_textView);
                    childThreePicHolder.tvType = (TextViewExtend) vThreePic.findViewById(R.id.type_textView);
                    childThreePicHolder.ivDelete = (ImageView) vThreePic.findViewById(R.id.delete_imageView);
                    childThreePicHolder.ivBottomLine = (ImageView) vThreePic.findViewById(R.id.line_bottom_imageView);
                    vThreePic.findViewById(R.id.checkFavoriteDelete_image).setVisibility(View.GONE);
                    vThreePic.setTag(childThreePicHolder);
                    convertView = vThreePic;
                } else {
                    childThreePicHolder = (ChildThreePicHolder) convertView.getTag();
                }
                ArrayList<String> strArrImgUrl = feed.getImgs();
                setImageUri(childThreePicHolder.ivPicture1, strArrImgUrl.get(0), mCardWidth, mCardHeight, feed.getRtype());
                setImageUri(childThreePicHolder.ivPicture2, strArrImgUrl.get(1), mCardWidth, mCardHeight, feed.getRtype());
                setImageUri(childThreePicHolder.ivPicture3, strArrImgUrl.get(2), mCardWidth, mCardHeight, feed.getRtype());
                setCardMargin(childThreePicHolder.ivPicture1, 15, 1, 3);
                setCardMargin(childThreePicHolder.ivPicture2, 1, 1, 3);
                setCardMargin(childThreePicHolder.ivPicture3, 1, 15, 3);
                setTitleTextBySpannable(childThreePicHolder.tvTitle, feed.getTitle(), false);
                setSourceViewText(childThreePicHolder.tvSource, feed.getPname());
                setCommentViewText(childThreePicHolder.tvCommentNum, feed.getComment() + "");
                setNewsContentClick(childThreePicHolder.rlContent, feed);
                newsTag(childThreePicHolder.tvType, feed.getRtype());
                childThreePicHolder.ivDelete.setVisibility(View.INVISIBLE);
                setBottomLine(childThreePicHolder.ivBottomLine, getChildrenCount(groupPosition), childPosition);
            } else if (currentType == NewsFeed.BIG_PIC) {
                if (convertView == null) {
                    childBigPicHolder = new ChildBigPicHolder();
                    vBigPic = LayoutInflater.from(mContext).inflate(R.layout.ll_news_topic_big_pic2, null);
                    childBigPicHolder.ivBigPicture = (ImageView) vBigPic.findViewById(R.id.title_img_View);
                    childBigPicHolder.rlContent = (RelativeLayout) vBigPic.findViewById(R.id.news_content_relativeLayout);
                    childBigPicHolder.tvTitle = (TextView) vBigPic.findViewById(R.id.title_textView);
                    childBigPicHolder.tvSource = (TextViewExtend) vBigPic.findViewById(R.id.news_source_TextView);
                    childBigPicHolder.tvCommentNum = (TextViewExtend) vBigPic.findViewById(R.id.comment_num_textView);
                    childBigPicHolder.tvType = (TextViewExtend) vBigPic.findViewById(R.id.type_textView);
                    childBigPicHolder.ivDelete = (ImageView) vBigPic.findViewById(R.id.delete_imageView);
                    vBigPic.findViewById(R.id.checkFavoriteDelete_image).setVisibility(View.GONE);
                    vBigPic.setTag(childBigPicHolder);
                    convertView = vBigPic;
                } else {
                    childBigPicHolder = (ChildBigPicHolder) convertView.getTag();
                }
                ArrayList<String> strArrBigImgUrl = feed.getImgs();
                int with = mScreenWidth - DensityUtil.dip2px(mContext, 30);
                int num = feed.getStyle() - 11;
                RelativeLayout.LayoutParams lpBig = (RelativeLayout.LayoutParams) childBigPicHolder.ivBigPicture.getLayoutParams();
                lpBig.width = with;
                lpBig.height = (int) (with * 9 / 16.0f);
                childBigPicHolder.ivBigPicture.setLayoutParams(lpBig);
                setImageUri(childBigPicHolder.ivBigPicture, strArrBigImgUrl.get(num), with, (int) (with * 9 / 16.0f), feed.getRtype());
                setTitleTextBySpannable(childBigPicHolder.tvTitle, feed.getTitle(), false);
                setSourceViewText(childBigPicHolder.tvSource, feed.getPname());
                setCommentViewText(childBigPicHolder.tvCommentNum, feed.getComment() + "");
                setNewsContentClick(childBigPicHolder.rlContent, feed);
                newsTag(childBigPicHolder.tvType, feed.getRtype());
                childBigPicHolder.ivDelete.setVisibility(View.INVISIBLE);
            } else if (currentType == NewsFeed.EMPTY) {
                if (convertView == null) {
                    childNoPicHolderHolder = new ChildNoPicHolder();
                    vEmpty = LayoutInflater.from(mContext).inflate(R.layout.ll_news_topic_item_empty, null);
                    childNoPicHolderHolder.rlContent = (RelativeLayout) vEmpty.findViewById(R.id.news_content_relativeLayout);
                    vEmpty.findViewById(R.id.checkFavoriteDelete_image).setVisibility(View.GONE);
                    vEmpty.setTag(childNoPicHolderHolder);
                    convertView = vEmpty;
                } else {
                    childNoPicHolderHolder = (ChildNoPicHolder) convertView.getTag();
                }
                childNoPicHolderHolder.rlContent.setVisibility(View.GONE);
            }
            return convertView;
        }

        private void setImageUri(ImageView draweeView, String strImg, int width, int height, int rType) {
            if (!TextUtil.isEmptyString(strImg)) {
                Uri uri;
                if (rType != 3) {
                    String img = strImg.replace("bdp-", "pro-");
                    uri = Uri.parse(img + "@1e_1c_0o_0l_100sh_" + height + "h_" + width + "w_95q.jpg");
                } else {
                    uri = Uri.parse(strImg);
                }
                Glide.with(mContext).load(uri).placeholder(R.drawable.bg_load_default_small).into(draweeView);
            }
        }

        private void setCardMargin(ImageView ivCard, int leftMargin, int rightMargin, int pageNum) {
            LinearLayout.LayoutParams localLayoutParams = (LinearLayout.LayoutParams) ivCard.getLayoutParams();
            localLayoutParams.leftMargin = DensityUtil.dip2px(mContext, leftMargin);
            localLayoutParams.rightMargin = DensityUtil.dip2px(mContext, rightMargin);
            int width = (int) (mScreenWidth / 2.0f - DensityUtil.dip2px(mContext, 15));
            if (pageNum == 2) {
                localLayoutParams.width = width;
                localLayoutParams.height = (int) (width * 74 / 102f);
            } else if (pageNum == 3) {
                localLayoutParams.width = mCardWidth;
                localLayoutParams.height = mCardHeight;
            }
            ivCard.setLayoutParams(localLayoutParams);
        }

        private void setTitleTextBySpannable(TextView tvTitle, String strTitle, boolean isRead) {
            if (strTitle != null && !"".equals(strTitle)) {
                tvTitle.setText(strTitle);
                tvTitle.setLineSpacing(0, 1.1f);
                if (isRead) {
                    tvTitle.setTextColor(mContext.getResources().getColor(R.color.new_color3));
                } else {
                    tvTitle.setTextColor(mContext.getResources().getColor(R.color.new_color1));
                }
                tvTitle.setTextSize(mSharedPreferences.getInt("textSize", CommonConstant.TEXT_SIZE_NORMAL));
            }
        }

        private void setBottomLine(ImageView ivBottom, int count, int position) {
            if (count == position + 1) {//去掉最后一条的线
                ivBottom.setVisibility(View.INVISIBLE);
            } else {
                ivBottom.setVisibility(View.VISIBLE);
            }
        }

        private void setNewsContentClick(RelativeLayout rlNewsContent, final NewsFeed feed) {
            rlNewsContent.setOnClickListener(new View.OnClickListener() {
                long firstClick = 0;

                public void onClick(View paramAnonymousView) {
                    if (System.currentTimeMillis() - firstClick <= 1500L) {
                        firstClick = System.currentTimeMillis();
                        return;
                    }
                    firstClick = System.currentTimeMillis();
                    if (feed.getRtype() == 3) {
                        Intent AdIntent = new Intent(mContext, NewsDetailWebviewAty.class);
                        AdIntent.putExtra(NewsDetailWebviewAty.KEY_URL, feed.getPurl());
                        startActivityForResult(AdIntent, REQUEST_CODE);
                    } else {
                        Intent intent = new Intent(mContext, NewsDetailAty2.class);
                        intent.putExtra(NewsFeedFgt.KEY_NEWS_FEED, feed);
                        ArrayList<String> imageList = feed.getImgs();
                        if (imageList != null && imageList.size() != 0) {
                            intent.putExtra(NewsFeedFgt.KEY_NEWS_IMAGE, imageList.get(0));
                        }
                        startActivityForResult(intent, REQUEST_CODE);
                    }
                }
            });
        }

        private void setSourceViewText(TextViewExtend textView, String strText) {
            if (!TextUtil.isEmptyString(strText)) {
                textView.setText(strText);
            }
        }

        private void setCommentViewText(TextViewExtend textView, String strText) {
            textView.setText(TextUtil.getCommentNum(strText));
        }

        private void newsTag(TextViewExtend tag, int type) {
            String content = "";
            if (type == 1) {
                if (tag.getVisibility() == View.GONE) {
                    tag.setVisibility(View.VISIBLE);
                }
                content = "热点";
                tag.setTextColor(mContext.getResources().getColor(R.color.newsfeed_red));
                tag.setBackgroundResource(R.drawable.newstag_hotspot_shape);
            } else if (type == 2) {
                if (tag.getVisibility() == View.GONE) {
                    tag.setVisibility(View.VISIBLE);
                }
                content = "推送";
                tag.setTextColor(mContext.getResources().getColor(R.color.color1));
                tag.setBackgroundResource(R.drawable.newstag_push_shape);
            } else if (type == 3) {
                if (tag.getVisibility() == View.GONE) {
                    tag.setVisibility(View.VISIBLE);
                }
                content = "广告";
                tag.setTextColor(mContext.getResources().getColor(R.color.theme_color));
                tag.setBackgroundResource(R.drawable.newstag_ad_shape);
            } else {
                if (tag.getVisibility() == View.VISIBLE) {
                    tag.setVisibility(View.GONE);
                }
                return;
            }
            tag.setText(content);
            tag.setGravity(Gravity.CENTER);
            LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) tag.getLayoutParams();
            params.width = DensityUtil.dip2px(mContext, 20);
            params.height = DensityUtil.dip2px(mContext, 11);
            tag.setLayoutParams(params);
        }

        class GroupHolder {
            TextView tvTitle;
            TextViewExtend tvSource;
            ImageView ivColor;
        }

        class ChildNoPicHolder {
            TextView tvTitle;
            TextViewExtend tvSource;
            TextViewExtend tvCommentNum;
            TextViewExtend tvType;
            RelativeLayout rlContent;
            ImageView ivDelete;
            ImageView ivBottomLine;
        }

        class ChildOnePicHolder extends ChildNoPicHolder {
            ImageView ivPicture;
            LinearLayout llSourceOnePic;
            ImageView ivBottomLine;
        }

        class ChildThreePicHolder extends ChildNoPicHolder {
            ImageView ivPicture1;
            ImageView ivPicture2;
            ImageView ivPicture3;
        }

        class ChildBigPicHolder extends ChildNoPicHolder {
            ImageView ivBigPicture;
        }
    }
}