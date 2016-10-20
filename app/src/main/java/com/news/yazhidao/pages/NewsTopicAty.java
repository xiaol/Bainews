package com.news.yazhidao.pages;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Handler;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshExpandableListView;
import com.news.yazhidao.R;
import com.news.yazhidao.application.YaZhiDaoApplication;
import com.news.yazhidao.common.BaseActivity;
import com.news.yazhidao.common.CommonConstant;
import com.news.yazhidao.common.HttpConstant;
import com.news.yazhidao.database.ChannelItemDao;
import com.news.yazhidao.entity.ChannelItem;
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
import com.news.yazhidao.widget.TextViewExtend;
import com.umeng.analytics.MobclickAgent;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by fengjigang on 16/4/6.
 */
public class NewsTopicAty extends BaseActivity implements View.OnClickListener {

    public static final int REQUEST_CODE = 1006;
    public static final String KEY_NID = "key_nid";
    private RelativeLayout mHomeRetry, bgLayout;
    private int mtid;
    private long mFirstClickTime;
    private ExpandableSpecialListViewAdapter mAdapter;
    private ArrayList<NewsFeed> mArrNewsTopic = new ArrayList<>();
    private Context mContext;
    private PullToRefreshExpandableListView mlvSpecialNewsFeed;
    private boolean isListRefresh;
    private TopicBaseInfo mTopicBaseInfo;
    private TopicClass mTopicClass;
    private Handler mHandler;
    private SharedPreferences mSharedPreferences;
    private int mScreenWidth, mCardWidth, mCardHeight;
    private NewsTopicHeaderView mSpecialNewsHeaderView;

    @Override
    protected void setContentView() {
        setContentView(R.layout.aty_news_topic);
        mContext = this;
    }

    @Override
    protected void initializeViews() {
        mtid = getIntent().getIntExtra(KEY_NID, 0);
        mScreenWidth = DeviceInfoUtil.getScreenWidth();
        mSharedPreferences = mContext.getSharedPreferences("showflag", 0);
        mCardWidth = (int) ((mScreenWidth - DensityUtil.dip2px(mContext, 32)) / 3.0f);
        mCardHeight = (int) (mCardWidth * 213 / 326.0f);
        mAdapter = new ExpandableSpecialListViewAdapter(this);
        mHandler = new Handler();
        mSpecialNewsHeaderView = new NewsTopicHeaderView(this);
        bgLayout = (RelativeLayout) findViewById(R.id.bgLayout);
        mlvSpecialNewsFeed = (PullToRefreshExpandableListView) findViewById(R.id.news_Topic_listView);
        mlvSpecialNewsFeed.setMode(PullToRefreshBase.Mode.DISABLED);
        mlvSpecialNewsFeed.setMainFooterView(true);
        mlvSpecialNewsFeed.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ExpandableListView>() {
            @Override
            public void onPullDownToRefresh(PullToRefreshBase<ExpandableListView> refreshView) {
                isListRefresh = true;
                loadData();
            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<ExpandableListView> refreshView) {
                isListRefresh = true;
                loadData();
            }
        });
        mlvSpecialNewsFeed.getRefreshableView().addHeaderView(mSpecialNewsHeaderView);
        mHomeRetry = (RelativeLayout) findViewById(R.id.mHomeRetry);
        mHomeRetry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mlvSpecialNewsFeed.setRefreshing();
                mHomeRetry.setVisibility(View.GONE);
            }
        });
    }

    @Override
    protected boolean translucentStatus() {
        return false;
    }

    @Override
    protected void loadData() {
        String requestUrl = HttpConstant.URL_NEWS_TOPIC + "tid=" + mtid;
        if (NetUtil.checkNetWork(mContext)) {
            RequestQueue requestQueue = YaZhiDaoApplication.getInstance().getRequestQueue();
            NewsTopicRequestGet<NewsTopic> topicRequestGet = new NewsTopicRequestGet<>(Request.Method.GET, new TypeToken<NewsTopic>() {
            }.getType(), requestUrl, new Response.Listener<NewsTopic>() {

                @Override
                public void onResponse(final NewsTopic result) {
                    Log.i("tag", result.toString());

                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.i("tag", error.toString());
                }
            });
            HashMap<String, String> header = new HashMap<>();
            header.put("Authorization", SharedPreManager.getUser(mContext).getAuthorToken());
            topicRequestGet.setRequestHeader(header);
            topicRequestGet.setRetryPolicy(new DefaultRetryPolicy(15000, 0, 0));
            requestQueue.add(topicRequestGet);
        } else {
            setRefreshComplete();
//            ArrayList<NewsFeed> newsFeeds = mNewsFeedDao.queryByChannelId(mstrChannelId);
//            if (TextUtil.isListEmpty(newsFeeds)) {
//                mHomeRetry.setVisibility(View.VISIBLE);
//            } else {
//                mHomeRetry.setVisibility(View.GONE);
//            }
//            mAdapter.setNewsFeed(newsFeeds);
//            mAdapter.notifyDataSetChanged();
//            if (bgLayout.getVisibility() == View.VISIBLE) {
//                bgLayout.setVisibility(View.GONE);
//            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    private void setRefreshComplete() {
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                mlvSpecialNewsFeed.onRefreshComplete();
            }
        }, 500);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.mLoginWeibo:
                if (System.currentTimeMillis() - mFirstClickTime < 2000) {
                    return;
                }
                mFirstClickTime = System.currentTimeMillis();
                break;
            case R.id.mLoginWeixin:
                if (System.currentTimeMillis() - mFirstClickTime < 2000) {
                    return;
                }
                mFirstClickTime = System.currentTimeMillis();
                break;
            case R.id.mLoginCancel:
                this.finish();
                break;
            case R.id.mLoginSetting:
                Intent settingAty = new Intent(this, SettingAty.class);
                startActivity(settingAty);
                this.finish();
                break;
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    public class ExpandableSpecialListViewAdapter extends BaseExpandableListAdapter {

        private Context mContext;

        public ExpandableSpecialListViewAdapter(Context context) {
            mContext = context;
        }

        @Override
        public int getGroupCount() {
            // TODO Auto-generated method stub
            return 2;
        }

        @Override
        public int getChildrenCount(int groupPosition) {
            // TODO Auto-generated method stub
            int iCount = 0;
            // In each group, we need to display the tip at the first child view
            if (groupPosition == 0)
                iCount = mArrNewsTopic == null ? 0 : mArrNewsTopic.size() + 1;
            if (groupPosition == 1)
                iCount = mArrNewsTopic == null ? 0 : mArrNewsTopic.size() + 1;
            return iCount;
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
        public boolean isChildSelectable(int groupPosition, int childPosition) {
            // TODO Auto-generated method stub
            return true;
        }

        @Override
        public View getGroupView(final int groupPosition, boolean isExpanded,
                                 View convertView, ViewGroup parent) {
            final GroupHolder groupHolder;
            if (convertView == null) {
                groupHolder = new GroupHolder();
                convertView = LayoutInflater.from(mContext).inflate(R.layout.item_special_news_group, null, false);
                groupHolder.ivColor = (ImageView) convertView.findViewById(R.id.mGroupColor);
                groupHolder.tvTitle = (TextView) convertView.findViewById(R.id.mGroupTitle);
                convertView.setTag(groupHolder);
            } else {
                groupHolder = (GroupHolder) convertView.getTag();
            }
            if (groupPosition == 0) {
                groupHolder.tvTitle.setText("焦点新闻");
                groupHolder.ivColor.setBackgroundColor(getResources().getColor(R.color.new_color2));
            } else {
                groupHolder.tvTitle.setText("评论观点");
                groupHolder.ivColor.setBackgroundColor(getResources().getColor(R.color.red));
            }
            convertView.setOnClickListener(null);
            return convertView;
        }


        @Override
        public View getChildView(final int groupPosition, final int childPosition,
                                 boolean isLastChild, View convertView, ViewGroup parent) {
            ChildNoPicHolder childNoPicHolderHolder;
            ChildOnePicHolder childOnePicHolder;
            ChildThreePicHolder childThreePicHolder;
            ChildBigPicHolder childBigPicHolder;
            NewsFeed feed = mArrNewsTopic.get(childPosition);
            int type = feed.getStyle();
            if (NewsFeed.NO_PIC == type) {
                if (convertView == null) {
                    childNoPicHolderHolder = new ChildNoPicHolder();
                    convertView = LayoutInflater.from(mContext).inflate(R.layout.ll_news_item_no_pic, null);
                    childNoPicHolderHolder.rlContent = (RelativeLayout) convertView.findViewById(R.id.news_content_relativeLayout);
                    childNoPicHolderHolder.tvTitle = (TextView) convertView.findViewById(R.id.title_textView);
                    childNoPicHolderHolder.tvSource = (TextViewExtend) convertView.findViewById(R.id.news_source_TextView);
                    childNoPicHolderHolder.tvCommentNum = (TextViewExtend) convertView.findViewById(R.id.comment_num_textView);
                    childNoPicHolderHolder.tvType = (TextViewExtend) convertView.findViewById(R.id.type_textView);
                    childNoPicHolderHolder.ivDelete = (ImageView) convertView.findViewById(R.id.delete_imageView);
                } else {
                    childNoPicHolderHolder = (ChildNoPicHolder) convertView.getTag();
                }
                setTitleTextBySpannable(childNoPicHolderHolder.tvTitle, feed.getTitle(), false);
                setSourceViewText(childNoPicHolderHolder.tvSource, feed.getPname());
                setCommentViewText(childNoPicHolderHolder.tvCommentNum, feed.getComment() + "");
                setNewsContentClick(childNoPicHolderHolder.rlContent, feed);
                newsTag(childNoPicHolderHolder.tvType, feed.getRtype());
                childNoPicHolderHolder.ivDelete.setVisibility(View.INVISIBLE);
            } else if (NewsFeed.ONE_AND_TWO_PIC == type) {
                if (convertView == null) {
                    childOnePicHolder = new ChildOnePicHolder();
                    convertView = LayoutInflater.from(mContext).inflate(R.layout.ll_news_item_one_pic, null);
                    childOnePicHolder.ivPicture = (ImageView) convertView.findViewById(R.id.title_img_View);
                    childOnePicHolder.rlContent = (RelativeLayout) convertView.findViewById(R.id.news_content_relativeLayout);
                    childOnePicHolder.tvTitle = (TextView) convertView.findViewById(R.id.title_textView);
                    childOnePicHolder.tvSource = (TextViewExtend) convertView.findViewById(R.id.news_source_TextView);
                    childOnePicHolder.tvCommentNum = (TextViewExtend) convertView.findViewById(R.id.comment_num_textView);
                    childOnePicHolder.tvType = (TextViewExtend) convertView.findViewById(R.id.type_textView);
                    childOnePicHolder.ivDelete = (ImageView) convertView.findViewById(R.id.delete_imageView);
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
            } else if (NewsFeed.THREE_PIC == type) {
                if (convertView == null) {
                    childThreePicHolder = new ChildThreePicHolder();
                    convertView = LayoutInflater.from(mContext).inflate(R.layout.ll_news_card, null);
                    childThreePicHolder.ivPicture1 = (ImageView) convertView.findViewById(R.id.image_card1);
                    childThreePicHolder.ivPicture2 = (ImageView) convertView.findViewById(R.id.image_card2);
                    childThreePicHolder.ivPicture3 = (ImageView) convertView.findViewById(R.id.image_card3);
                    childThreePicHolder.rlContent = (RelativeLayout) convertView.findViewById(R.id.news_content_relativeLayout);
                    childThreePicHolder.tvTitle = (TextView) convertView.findViewById(R.id.title_textView);
                    childThreePicHolder.tvSource = (TextViewExtend) convertView.findViewById(R.id.news_source_TextView);
                    childThreePicHolder.tvCommentNum = (TextViewExtend) convertView.findViewById(R.id.comment_num_textView);
                    childThreePicHolder.tvType = (TextViewExtend) convertView.findViewById(R.id.type_textView);
                    childThreePicHolder.ivDelete = (ImageView) convertView.findViewById(R.id.delete_imageView);
                } else {
                    childThreePicHolder = (ChildThreePicHolder) convertView.getTag();
                }
                ArrayList<String> strArrImgUrl = feed.getImgs();
                setImageUri(childThreePicHolder.ivPicture1, strArrImgUrl.get(0), mCardWidth, mCardHeight, feed.getRtype());
                setImageUri(childThreePicHolder.ivPicture2, strArrImgUrl.get(1), mCardWidth, mCardHeight, feed.getRtype());
                setImageUri(childThreePicHolder.ivPicture3, strArrImgUrl.get(2), mCardWidth, mCardHeight, feed.getRtype());
                setCardMargin(childThreePicHolder.ivPicture1, 15, 1, 3);
                setCardMargin(childThreePicHolder.ivPicture1, 1, 1, 3);
                setCardMargin(childThreePicHolder.ivPicture1, 1, 15, 3);
                setTitleTextBySpannable(childThreePicHolder.tvTitle, feed.getTitle(), false);
                setSourceViewText(childThreePicHolder.tvSource, feed.getPname());
                setCommentViewText(childThreePicHolder.tvCommentNum, feed.getComment() + "");
                setNewsContentClick(childThreePicHolder.rlContent, feed);
                newsTag(childThreePicHolder.tvType, feed.getRtype());
                childThreePicHolder.ivDelete.setVisibility(View.INVISIBLE);
            } else if (NewsFeed.BIG_PIC == type) {
                if (convertView == null) {
                    childBigPicHolder = new ChildBigPicHolder();
                    convertView = LayoutInflater.from(mContext).inflate(R.layout.ll_news_big_pic2, null);
                    childBigPicHolder.ivBigPicture = (ImageView) convertView.findViewById(R.id.title_img_View);
                    childBigPicHolder.rlContent = (RelativeLayout) convertView.findViewById(R.id.news_content_relativeLayout);
                    childBigPicHolder.tvTitle = (TextView) convertView.findViewById(R.id.title_textView);
                    childBigPicHolder.tvSource = (TextViewExtend) convertView.findViewById(R.id.news_source_TextView);
                    childBigPicHolder.tvCommentNum = (TextViewExtend) convertView.findViewById(R.id.comment_num_textView);
                    childBigPicHolder.tvType = (TextViewExtend) convertView.findViewById(R.id.type_textView);
                    childBigPicHolder.ivDelete = (ImageView) convertView.findViewById(R.id.delete_imageView);
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
            }
            return convertView;
        }

        private void setImageUri(ImageView draweeView, String strImg, int width, int height, int rType) {
            if (!TextUtil.isEmptyString(strImg)) {
                if (SharedPreManager.getBoolean(CommonConstant.FILE_USER, CommonConstant.TYPE_SHOWIMAGES)) {
                    draweeView.setImageResource(R.drawable.bg_load_default_small);
                } else {
                    Uri uri;
                    if (rType != 3) {
                        String img = strImg.replace("bdp-", "pro-");
                        uri = Uri.parse(img + "@1e_1c_0o_0l_100sh_" + height + "h_" + width + "w_95q.jpg");
                    } else {
                        uri = Uri.parse(strImg);
                    }
                    Glide.with(mContext).load(uri).centerCrop().placeholder(R.drawable.bg_load_default_small).into(draweeView);
                }
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
                    MobclickAgent.onEvent(mContext, "bainews_view_head_news");
                    /**点击查看详情时,统计当前的频道名称*/
                    String channelName = "";
                    ChannelItemDao dao = new ChannelItemDao(mContext);
                    ArrayList<ChannelItem> channelItems = dao.queryForAll();
                    for (ChannelItem item : channelItems) {
                        if (Integer.valueOf(feed.getChannel()).equals(item.getId())) {
                            channelName = item.getName();
                            break;
                        }
                    }
                    dao.setFocusOnline();
                    HashMap<String, String> hashmap = new HashMap();
                    hashmap.put("channel", channelName);
                    MobclickAgent.onEvent(mContext, "user_read_detail", hashmap);
                }
            });
        }

        private void setSourceViewText(TextViewExtend textView, String strText) {
            if (!TextUtil.isEmptyString(strText)) {
                textView.setText(strText);
            }
        }

        private void setCommentViewText(TextViewExtend textView, String strText) {
            if (!TextUtil.isEmptyString(strText) && !"0".equals(strText)) {
                textView.setText(strText + "评");
            } else {
                textView.setText("");
            }
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
        }

        class ChildOnePicHolder extends ChildNoPicHolder {
            ImageView ivPicture;
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
