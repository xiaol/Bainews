package com.news.yazhidao.pages;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PointF;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.google.gson.reflect.TypeToken;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.news.yazhidao.R;
import com.news.yazhidao.common.CommonConstant;
import com.news.yazhidao.entity.NewsFeed;
import com.news.yazhidao.entity.User;
import com.news.yazhidao.net.JsonCallback;
import com.news.yazhidao.net.MyAppException;
import com.news.yazhidao.net.NetworkRequest;
import com.news.yazhidao.utils.DensityUtil;
import com.news.yazhidao.utils.DeviceInfoUtil;
import com.news.yazhidao.utils.NetUtil;
import com.news.yazhidao.utils.TextUtil;
import com.news.yazhidao.utils.ToastUtil;
import com.news.yazhidao.utils.adcoco.AdcocoUtil;
import com.news.yazhidao.utils.manager.SharedPreManager;
import com.news.yazhidao.widget.TextViewExtend;
import com.news.yazhidao.widget.TextViewVertical;
import com.umeng.analytics.AnalyticsConfig;
import com.umeng.analytics.MobclickAgent;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class NewsFeedFgt extends Fragment {

    /**
     * 当前fragment 所对应的新闻频道
     */
    public static String KEY_CHANNEL_ID = "key_channel_id";
    public static String KEY_WORD = "key_word";
    public static String KEY_NEWS_SOURCE = "key_news_source";
    public static String KEY_URL = "key_url";
    public static String KEY_NEWS_ID = "key_news_id";
    public static String KEY_COLLECTION = "key_collection";
    public static final String VALUE_NEWS_NOTIFICATION = "notification";
    public static String VALUE_NEWS_SOURCE = "other_view";
    public static final int PULL_DOWN_REFRESH = 1;
    private static final int PULL_UP_REFRESH = 2;
    private NewsFeedAdapter mAdapter;
    private ArrayList<NewsFeed> mArrNewsFeed;
    private Context mContext;
    private NetworkRequest mRequest;
    private int mScreenHeight;
    private int mScreenWidth;
    private PullToRefreshListView mlvNewsFeed;
    private View rootView;
    private String mstrDeviceId, mstrUserId, mstrChannelId, mstrKeyWord;
    /**
     * 当前的fragment 是否已经加载过数据
     */
    private boolean isLoadedData;

    public static NewsFeedFgt newInstance(String pChannelId) {
        NewsFeedFgt newsFeedFgt = new NewsFeedFgt();
        Bundle bundle = new Bundle();
        bundle.putString(KEY_CHANNEL_ID, pChannelId);
        newsFeedFgt.setArguments(bundle);
        return newsFeedFgt;
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (rootView != null && isVisibleToUser && !isLoadedData) {
            isLoadedData = true;
            Logger.e("jigang", "setUserVisibleHint  " + isLoadedData);
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    mlvNewsFeed.setRefreshing();
                }
            },500);
        }
    }

    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        mContext = getActivity();
        mScreenWidth = DeviceInfoUtil.getScreenWidth();
        mScreenHeight = DeviceInfoUtil.getScreenHeight();
        mstrDeviceId = DeviceInfoUtil.getUUID();
        User user = SharedPreManager.getUser(mContext);
        if (user != null)
            mstrUserId = user.getUserId();
        else
            mstrUserId = "";
    }

    public View onCreateView(LayoutInflater LayoutInflater, ViewGroup container, Bundle savedInstanceState) {
        Bundle arguments = getArguments();
        if (arguments != null) {
            mstrChannelId = arguments.getString(KEY_CHANNEL_ID);
            mstrKeyWord = arguments.getString(KEY_WORD);
        }
        rootView = LayoutInflater.inflate(R.layout.activity_news, container, false);
        mlvNewsFeed = ((PullToRefreshListView) rootView.findViewById(R.id.news_feed_listView));
        mlvNewsFeed.setMode(PullToRefreshBase.Mode.BOTH);
        mlvNewsFeed.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ListView>() {
            @Override
            public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
                loadData(PULL_DOWN_REFRESH);
            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
                loadData(PULL_UP_REFRESH);
            }
        });
        mAdapter = new NewsFeedAdapter();
        mlvNewsFeed.setAdapter(mAdapter);
        setUserVisibleHint(getUserVisibleHint());
        String platform = AnalyticsConfig.getChannel(getActivity());
        if ("adcoco".equals(platform) && !TextUtil.isListEmpty(mArrNewsFeed)) {
            AdcocoUtil.setup(getActivity());
            try {
                new AdcocoUtil().insertAdcoco(mArrNewsFeed, mlvNewsFeed.getRefreshableView(), mArrNewsFeed.size(), -1);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return rootView;
    }

    /**
     * 设置搜索热词
     *
     * @param pKeyWord
     */
    public void setSearchKeyWord(String pKeyWord) {
        this.mstrKeyWord = pKeyWord;
        loadData(PULL_DOWN_REFRESH);
    }

    /**
     * 当前新闻feed流fragment的父容器是否是MainAty,如果是则进行刷新动画
     */
    public void startTopRefresh() {
        if (MainAty.class.equals(mContext.getClass())) {
            ((MainAty) mContext).startTopRefresh();
        }
    }

    /**
     * 当前新闻feed流fragment的父容器是否是MainAty,如果是则停止刷新动画
     *
     * @return
     */
    public void stopRefresh() {
        if (MainAty.class.equals(mContext.getClass())) {
            ((MainAty) mContext).stopTopRefresh();
        }
    }

    private void loadNewsFeedData(String url, final int flag) {
        List<NameValuePair> nameValuePairList = new ArrayList<>();
        nameValuePairList.add(new BasicNameValuePair("userid", mstrUserId));
        nameValuePairList.add(new BasicNameValuePair("deviceid", mstrDeviceId));
        nameValuePairList.add(new BasicNameValuePair("channelid", mstrChannelId));
        nameValuePairList.add(new BasicNameValuePair("keyword", mstrKeyWord));
        nameValuePairList.add(new BasicNameValuePair("page", "1"));
        mRequest = new NetworkRequest("http://api.deeporiginalx.com/news/baijia/" + url, NetworkRequest.RequestMethod.POST);
        mRequest.setParams(nameValuePairList);
        mRequest.setCallback(new JsonCallback<ArrayList<NewsFeed>>() {
            public void failed(MyAppException paramAnonymousMyAppException) {
                stopRefresh();
                mlvNewsFeed.onRefreshComplete();
            }

            public void success(ArrayList<NewsFeed> result) {
                stopRefresh();
                if (result != null && result.size() > 0) {
                    switch (flag) {
                        case PULL_DOWN_REFRESH:
                            if (mArrNewsFeed == null)
                                mArrNewsFeed = result;
                            else
                                mArrNewsFeed.addAll(0, result);
                            mlvNewsFeed.getRefreshableView().setSelection(0);
                            break;
                        case PULL_UP_REFRESH:
                            mArrNewsFeed.addAll(result);
                            break;
                    }
                    mAdapter.notifyDataSetChanged();
                } else {
                    ToastUtil.toastLong("网络不给力,请检查网络....");
                }
                mlvNewsFeed.onRefreshComplete();
            }
        }.setReturnType(new TypeToken<ArrayList<NewsFeed>>() {
        }.getType()));
        mRequest.execute();
    }

    private void setCardMargin(SimpleDraweeView ivCard, int leftMargin, int rightMargin, int pageNum) {
        LinearLayout.LayoutParams localLayoutParams = (LinearLayout.LayoutParams) ivCard.getLayoutParams();
        localLayoutParams.leftMargin = DensityUtil.dip2px(mContext, leftMargin);
        localLayoutParams.rightMargin = DensityUtil.dip2px(mContext, rightMargin);
        int width = (int) (mScreenWidth / 2.0f - DensityUtil.dip2px(mContext, 12));
        if (pageNum == 2) {
            localLayoutParams.width = width;
            localLayoutParams.height = (int) (width * 3 / 4.0f);
        } else if (pageNum == 3) {
            width = (int) (mScreenWidth / 3.0f - DensityUtil.dip2px(mContext, 12));
            localLayoutParams.width = width;
            localLayoutParams.height = (int) (width * 3 / 4.0f);
        }
        ivCard.setLayoutParams(localLayoutParams);
    }

    private void setRelateView(final NewsFeed.Source source, RelativeLayout rlRelate, TextView tvRelate, TextViewExtend tvSource, boolean IsVisible) {
        if (IsVisible) {
            rlRelate.setVisibility(View.VISIBLE);
            rlRelate.setOnClickListener(new View.OnClickListener() {
                long firstClick = 0;

                @Override
                public void onClick(View v) {
                    if (System.currentTimeMillis() - firstClick <= 1500) {
                        firstClick = System.currentTimeMillis();
                        return;
                    }
                    firstClick = System.currentTimeMillis();
                    Intent intent = new Intent(mContext, NewsDetailWebviewAty.class);
                    intent.putExtra(KEY_URL, source.getSourceUrl());
                    startActivity(intent);
                    //umeng statistic onclick url below the head news
                    HashMap<String, String> _MobMap = new HashMap<>();
                    _MobMap.put("resource_site_name", source.getSourceSiteName());
                    MobclickAgent.onEvent(mContext, CommonConstant.US_BAINEWS_CLICK_URL_BELOW_HEAD_VEWS, _MobMap);
                }
            });
            if (source != null) {
                String source_name = source.getSourceSiteName();
                String finalText = "";
                String source_title = source.getCompress();
                String source_name_font = "";
                source_title = "<font color =\"#888888\">" + "<big>" + source_title + "</big>" + "</font>";
                if (source_name != null) {
                    if (source.getUser() != null && !"".equals(source.getUser())) {
                        source_name_font = "<font color =\"#7d7d7d\">" + "<big>" + source.getUser() + "</big>" + "</font>" + ": ";
                        finalText = source_name_font + source_title;
                        tvSource.setText(Html.fromHtml(finalText));
                    } else {
                        source_name_font = "<font color =\"#7d7d7d\">" + "<big>" + source_name + "</big>" + "</font>" + ": ";
                        finalText = source_name_font + source_title;
                        tvSource.setText(Html.fromHtml(finalText));
                    }
                } else {
                    String anonyStr = "<font color =\"#7d7d7d\">" + "<big>" + "匿名报道:" + "</big>" + "</font>" + ": ";
                    finalText = anonyStr + source_title;
                    tvSource.setText(Html.fromHtml(finalText));
                }
                if (source.getSimilarity() != null && !"".equals(source.getSimilarity())) {
                    if (source.getSimilarity().length() > 4) {
                        String ss = source.getSimilarity().substring(2, 4);
                        tvRelate.setText(ss + "%相关");
                    } else {
                        tvRelate.setVisibility(View.GONE);
                    }
                } else {
                    tvRelate.setVisibility(View.GONE);
                }
            }
        } else {
            rlRelate.setVisibility(View.GONE);
        }
    }

    private void setVerticalLineHeight(final RelativeLayout rlRelate, final ImageView ivVerticalLine) {
        ivVerticalLine.post(new Runnable() {
            public void run() {
                RelativeLayout.LayoutParams lpVerticalLine = (RelativeLayout.LayoutParams) ivVerticalLine.getLayoutParams();
                lpVerticalLine.height = (int) (rlRelate.getHeight() - ivVerticalLine.getY());
                ivVerticalLine.setLayoutParams(lpVerticalLine);
            }
        });
    }

    private void setLoadImage(SimpleDraweeView imageView, String imageUrl) {
        imageView.setVisibility(View.VISIBLE);
        if (imageUrl != null && !"".equals(imageUrl)) {
            imageView.setImageURI(Uri.parse(imageUrl));
            imageView.getHierarchy().setActualImageFocusPoint(new PointF(0.5F, 0.4F));
        }
    }

    private void setTitleTextBySpannable(TextView tvTitle, String strTitle) {
        if (strTitle != null && !"".equals(strTitle)) {
            if (mstrKeyWord != null && !"".equals(mstrKeyWord)) {
                strTitle = strTitle.replace(mstrKeyWord, "<font color =\"#35a6fb\">" + mstrKeyWord + "</font>");
            }
            tvTitle.setText(Html.fromHtml(strTitle), TextView.BufferType.SPANNABLE);
        }
    }

    private void setViewText(TextViewExtend textView, String strText) {
        if (strText != null && !"".equals(strText)) {
            textView.setText(strText);
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
                Intent intent = new Intent(mContext, NewsDetailAty2.class);
                intent.putExtra(NewsFeedFgt.KEY_NEWS_ID, feed.getNewsId());
                intent.putExtra(NewsFeedFgt.KEY_COLLECTION, feed.getCollection());
                startActivity(intent);
                MobclickAgent.onEvent(mContext, "bainews_view_head_news");
            }
        });
    }

    public void CancelRequest() {
        if (mRequest != null)
            mRequest.cancel(true);
    }

    public void loadData(int flag) {
        Logger.e("jigang","loaddata -----");
        if (NetUtil.checkNetWork(mContext)) {
            if (!TextUtil.isEmptyString(mstrKeyWord)) {
                loadNewsFeedData("search", flag);
            } else if (!TextUtil.isEmptyString(mstrChannelId))
                loadNewsFeedData("recommend", flag);
            startTopRefresh();
        } else {
            ToastUtil.toastLong("您的网络有点不给力，请检查网络....");
        }
    }


    private class NewsFeedAdapter extends BaseAdapter {

        public int getCount() {
            return mArrNewsFeed == null ? 0 : mArrNewsFeed.size();
        }

        public Object getItem(int position) {
            return mArrNewsFeed.get(position);
        }

        public long getItemId(int position) {
            return position;
        }
        public View getView(final int position, View convertView, ViewGroup parent) {
        long start = System.currentTimeMillis();
            final NewsFeed feed = mArrNewsFeed.get(position);
            ArrayList<NewsFeed.Source> relatePointsList = (ArrayList<NewsFeed.Source>) feed.getRelatePointsList();
            String strType = feed.getType();
            //普通卡片
            if ("one_pic".equals(strType) || "no_pic".equals(strType)) {
                String platform = AnalyticsConfig.getChannel(getActivity());
                if ("adcoco".equals(platform)) {
                    AdcocoUtil.update();
                }
                ViewHolder holder;
                if (convertView == null || convertView.getTag().getClass() != ViewHolder.class) {
                    holder = new ViewHolder();
                    convertView = View.inflate(mContext, R.layout.ll_news_item3, null);
                    holder.ivTitleImg = (SimpleDraweeView) convertView.findViewById(R.id.title_img_View);
                    holder.tvTitle = (TextView) convertView.findViewById(R.id.title_textView);
                    holder.tvSource = (TextViewExtend) convertView.findViewById(R.id.news_source_TextView);
                    holder.tvComment = (TextViewExtend) convertView.findViewById(R.id.comment_textView);
                    holder.rlNewsContent = (RelativeLayout) convertView.findViewById(R.id.news_content_relativeLayout);
                    holder.rlRelate1 = (RelativeLayout) convertView.findViewById(R.id.relate_layout1);
                    holder.rlRelate2 = (RelativeLayout) convertView.findViewById(R.id.relate_layout2);
                    holder.rlRelate3 = (RelativeLayout) convertView.findViewById(R.id.relate_layout3);
                    holder.ivVerticalLine1 = (ImageView) holder.rlRelate1.findViewById(R.id.iv_combine_line);
                    holder.tvBottomLine1 = (TextView) holder.rlRelate1.findViewById(R.id.tv_devider_line);
                    holder.tvRelate1 = (TextView) holder.rlRelate1.findViewById(R.id.tv_relate);
                    holder.tvSource1 = (TextViewExtend) holder.rlRelate1.findViewById(R.id.tv_news_source);
                    holder.ivVerticalLine2 = (ImageView) holder.rlRelate2.findViewById(R.id.iv_combine_line);
                    holder.tvBottomLine2 = (TextView) holder.rlRelate2.findViewById(R.id.tv_devider_line);
                    holder.tvRelate2 = (TextView) holder.rlRelate2.findViewById(R.id.tv_relate);
                    holder.tvSource2 = (TextViewExtend) holder.rlRelate2.findViewById(R.id.tv_news_source);
                    holder.ivVerticalLine3 = (ImageView) holder.rlRelate3.findViewById(R.id.iv_combine_line);
                    holder.tvBottomLine3 = (TextView) holder.rlRelate3.findViewById(R.id.tv_devider_line);
                    holder.tvRelate3 = (TextView) holder.rlRelate3.findViewById(R.id.tv_relate);
                    holder.tvSource3 = (TextViewExtend) holder.rlRelate3.findViewById(R.id.tv_news_source);
                    holder.llSourceContent = (LinearLayout) convertView.findViewById(R.id.source_content_linearLayout);
                    convertView.setTag(holder);
                } else {
                    holder = (ViewHolder) convertView.getTag();
                }
                if ("adcoco".equals(platform)) {
                    ArrayList localArrayList2 = mArrNewsFeed;
                    AdcocoUtil.ad(position, convertView, localArrayList2);
                }

                ArrayList<String> strArrImgUrl = feed.getImgUrls();
                String strImg = null;
                if ("one_pic".equals(strType)) {
                    strImg = strArrImgUrl.get(0);
                }
                if (strImg != null && !"".equals(strImg)) {
                    holder.ivTitleImg.setVisibility(View.VISIBLE);
                    holder.ivTitleImg.setImageURI(Uri.parse(strImg));
                    holder.ivTitleImg.getHierarchy().setActualImageFocusPoint(new PointF(0.5F, 0.4F));
                } else {
                    holder.ivTitleImg.setVisibility(View.GONE);
                }

                setTitleTextBySpannable(holder.tvTitle, feed.getTitle());
                setViewText(holder.tvSource, feed.getSourceSiteName());
                setViewText(holder.tvComment, feed.getCommentNum() + "评论");
                setNewsContentClick(holder.rlNewsContent, feed);
                if (relatePointsList != null && relatePointsList.size() > 0) {
                    int size = relatePointsList.size();
                    holder.llSourceContent.setVisibility(View.VISIBLE);
                    holder.ivVerticalLine1.setVisibility(View.VISIBLE);
                    holder.tvBottomLine1.setVisibility(View.VISIBLE);
                    holder.ivVerticalLine2.setVisibility(View.VISIBLE);
                    holder.tvBottomLine2.setVisibility(View.VISIBLE);
                    holder.ivVerticalLine3.setVisibility(View.VISIBLE);
                    holder.tvBottomLine3.setVisibility(View.VISIBLE);
                    if (size == 1) {
                        NewsFeed.Source source = relatePointsList.get(0);
                        setRelateView(source, holder.rlRelate1, holder.tvRelate1, holder.tvSource1, true);
                        setRelateView(null, holder.rlRelate2, holder.tvRelate2, holder.tvSource2, false);
                        setRelateView(null, holder.rlRelate3, holder.tvRelate3, holder.tvSource3, false);
                        holder.ivVerticalLine1.setVisibility(View.GONE);
                        holder.tvBottomLine1.setVisibility(View.GONE);
                    } else if (size == 2) {
                        NewsFeed.Source source1 = relatePointsList.get(0);
                        NewsFeed.Source source2 = relatePointsList.get(1);
                        setRelateView(source1, holder.rlRelate1, holder.tvRelate1, holder.tvSource1, true);
                        setRelateView(source2, holder.rlRelate2, holder.tvRelate2, holder.tvSource2, true);
                        setRelateView(null, holder.rlRelate3, holder.tvRelate3, holder.tvSource3, false);
                        setVerticalLineHeight(holder.rlRelate1, holder.ivVerticalLine1);
                        holder.ivVerticalLine2.setVisibility(View.GONE);
                        holder.tvBottomLine2.setVisibility(View.GONE);
                    } else if (size == 3) {
                        NewsFeed.Source source1 = relatePointsList.get(0);
                        NewsFeed.Source source2 = relatePointsList.get(1);
                        NewsFeed.Source source3 = relatePointsList.get(2);
                        setRelateView(source1, holder.rlRelate1, holder.tvRelate1, holder.tvSource1, true);
                        setRelateView(source2, holder.rlRelate2, holder.tvRelate2, holder.tvSource2, true);
                        setRelateView(source3, holder.rlRelate3, holder.tvRelate3, holder.tvSource3, true);
                        setVerticalLineHeight(holder.rlRelate1, holder.ivVerticalLine1);
                        setVerticalLineHeight(holder.rlRelate2, holder.ivVerticalLine2);
                        holder.ivVerticalLine3.setVisibility(View.GONE);
                        holder.tvBottomLine3.setVisibility(View.GONE);
                    }
                } else {
                    holder.llSourceContent.setVisibility(View.GONE);
                }
            }
            //大图
            else if ("big_pic".equals(strType)) {
                ViewHolder2 holder2;
                if (convertView == null || convertView.getTag().getClass() != ViewHolder2.class) {
                    holder2 = new ViewHolder2();
                    convertView = View.inflate(mContext, R.layout.ll_news_item_top, null);
                    holder2.rl_item_content = (RelativeLayout) convertView.findViewById(R.id.rl_item_content);
                    holder2.iv_title_img = (SimpleDraweeView) convertView.findViewById(R.id.iv_title_img);
                    holder2.tv_title = (TextViewVertical) convertView.findViewById(R.id.tv_title);
                    RelativeLayout.LayoutParams lpImg = (RelativeLayout.LayoutParams) holder2.iv_title_img.getLayoutParams();
                    lpImg.width = mScreenWidth;
                    lpImg.height = (int) (mScreenHeight * 0.40);
                    holder2.iv_title_img.setLayoutParams(lpImg);
                    convertView.setTag(holder2);
                } else {
                    holder2 = (ViewHolder2) convertView.getTag();
                }
                String title_news = feed.getTitle();
                String title = "";
                if (title_news != null && title_news.length() > 0) {
                    title = TextUtil.getNewsTitle(title_news);
                }

                if (title != null && !"".equals(title)) {
                    holder2.tv_title.setText(title);
                } else {
                    holder2.tv_title.setText(title_news);
                }

                int textSize = DensityUtil.dip2px(mContext, 18);
                holder2.tv_title.setTextSize(textSize);
                holder2.tv_title.setTextColor(new Color().parseColor("#f7f7f7"));
                holder2.tv_title.setLineWidth(DensityUtil.dip2px(mContext, 20));
                holder2.tv_title.setShadowLayer(4f, 1, 2, new Color().parseColor("#000000"));

                ArrayList<String> strArrImgUrl = feed.getImgUrls();
                String strImgUrl = strArrImgUrl.get(0);
                if (strImgUrl != null && !"".equals(strImgUrl)) {
                    holder2.iv_title_img.setImageURI(Uri.parse(strImgUrl));
                    holder2.iv_title_img.getHierarchy().setActualImageFocusPoint(new PointF(0.5f, 0.4f));
                } else {
                    holder2.tv_title.setBackgroundResource(R.drawable.bg_load_default_big);
                }
                setNewsContentClick(holder2.rl_item_content, feed);
            }
            //多图
            else if ("three_pic".equals(strType) || "two_pic".equals(strType)) {
                ViewHolder3 holder3;
                if (convertView == null || convertView.getTag().getClass() != ViewHolder3.class) {
                    holder3 = new ViewHolder3();
                    convertView = View.inflate(mContext, R.layout.ll_news_card, null);
                    holder3.llImageList = (LinearLayout) convertView.findViewById(R.id.image_list_LinearLayout);
                    holder3.rlNewsContent = (RelativeLayout) convertView.findViewById(R.id.news_content_relativeLayout);
                    holder3.ivCard1 = (SimpleDraweeView) convertView.findViewById(R.id.image_card1);
                    holder3.ivCard2 = (SimpleDraweeView) convertView.findViewById(R.id.image_card2);
                    holder3.ivCard3 = (SimpleDraweeView) convertView.findViewById(R.id.image_card3);
                    holder3.tvTitle = (TextView) convertView.findViewById(R.id.title_textView);
                    holder3.tvSource = (TextViewExtend) convertView.findViewById(R.id.news_source_TextView);
                    holder3.tvComment = (TextViewExtend) convertView.findViewById(R.id.comment_textView);
                    holder3.rlRelate1 = (RelativeLayout) convertView.findViewById(R.id.relate_layout1);
                    holder3.rlRelate2 = (RelativeLayout) convertView.findViewById(R.id.relate_layout2);
                    holder3.rlRelate3 = (RelativeLayout) convertView.findViewById(R.id.relate_layout3);
                    holder3.ivVerticalLine1 = (ImageView) holder3.rlRelate1.findViewById(R.id.iv_combine_line);
                    holder3.tvBottomLine1 = (TextView) holder3.rlRelate1.findViewById(R.id.tv_devider_line);
                    holder3.tvRelate1 = (TextView) holder3.rlRelate1.findViewById(R.id.tv_relate);
                    holder3.tvSource1 = (TextViewExtend) holder3.rlRelate1.findViewById(R.id.tv_news_source);
                    holder3.ivVerticalLine2 = (ImageView) holder3.rlRelate2.findViewById(R.id.iv_combine_line);
                    holder3.tvBottomLine2 = (TextView) holder3.rlRelate2.findViewById(R.id.tv_devider_line);
                    holder3.tvRelate2 = (TextView) holder3.rlRelate2.findViewById(R.id.tv_relate);
                    holder3.tvSource2 = (TextViewExtend) holder3.rlRelate2.findViewById(R.id.tv_news_source);
                    holder3.ivVerticalLine3 = (ImageView) holder3.rlRelate3.findViewById(R.id.iv_combine_line);
                    holder3.tvBottomLine3 = (TextView) holder3.rlRelate3.findViewById(R.id.tv_devider_line);
                    holder3.tvRelate3 = (TextView) holder3.rlRelate3.findViewById(R.id.tv_relate);
                    holder3.tvSource3 = (TextViewExtend) holder3.rlRelate3.findViewById(R.id.tv_news_source);
                    holder3.llSourceContent = (LinearLayout) convertView.findViewById(R.id.source_content_linearLayout);
                    convertView.setTag(holder3);
                } else {
                    holder3 = (ViewHolder3) convertView.getTag();
                }
                ArrayList<String> strArrImgUrl = feed.getImgUrls();
                setLoadImage(holder3.ivCard1, strArrImgUrl.get(0));
                setLoadImage(holder3.ivCard2, strArrImgUrl.get(1));

                if (strArrImgUrl.size() == 3) {
                    setLoadImage(holder3.ivCard3, strArrImgUrl.get(2));
                    setCardMargin(holder3.ivCard1, 8, 4, 3);
                    setCardMargin(holder3.ivCard2, 4, 4, 3);
                    setCardMargin(holder3.ivCard3, 4, 8, 3);
                } else {
                    holder3.ivCard3.setVisibility(View.GONE);
                    setCardMargin(holder3.ivCard1, 8, 4, 2);
                    setCardMargin(holder3.ivCard2, 4, 8, 2);
                }

                setTitleTextBySpannable(holder3.tvTitle, feed.getTitle());
                setViewText(holder3.tvSource, feed.getSourceSiteName());
                setViewText(holder3.tvComment, feed.getCommentNum() + "评论");
                setNewsContentClick(holder3.rlNewsContent, feed);
                if (relatePointsList != null && relatePointsList.size() > 0) {
                    int size = relatePointsList.size();
                    holder3.llSourceContent.setVisibility(View.VISIBLE);
                    holder3.ivVerticalLine1.setVisibility(View.VISIBLE);
                    holder3.tvBottomLine1.setVisibility(View.VISIBLE);
                    holder3.ivVerticalLine2.setVisibility(View.VISIBLE);
                    holder3.tvBottomLine2.setVisibility(View.VISIBLE);
                    holder3.ivVerticalLine3.setVisibility(View.VISIBLE);
                    holder3.tvBottomLine3.setVisibility(View.VISIBLE);
                    if (size == 1) {
                        NewsFeed.Source source = relatePointsList.get(0);
                        setRelateView(source, holder3.rlRelate1, holder3.tvRelate1, holder3.tvSource1, true);
                        setRelateView(null, holder3.rlRelate2, holder3.tvRelate2, holder3.tvSource2, false);
                        setRelateView(null, holder3.rlRelate3, holder3.tvRelate3, holder3.tvSource3, false);
                        holder3.ivVerticalLine1.setVisibility(View.GONE);
                        holder3.tvBottomLine1.setVisibility(View.GONE);
                    } else if (size == 2) {
                        NewsFeed.Source source1 = relatePointsList.get(0);
                        NewsFeed.Source source2 = relatePointsList.get(1);
                        setRelateView(source1, holder3.rlRelate1, holder3.tvRelate1, holder3.tvSource1, true);
                        setRelateView(source2, holder3.rlRelate2, holder3.tvRelate2, holder3.tvSource2, true);
                        setRelateView(null, holder3.rlRelate3, holder3.tvRelate3, holder3.tvSource3, false);
                        setVerticalLineHeight(holder3.rlRelate1, holder3.ivVerticalLine1);
                        holder3.ivVerticalLine2.setVisibility(View.GONE);
                        holder3.tvBottomLine2.setVisibility(View.GONE);
                    } else if (size == 3) {
                        NewsFeed.Source source1 = relatePointsList.get(0);
                        NewsFeed.Source source2 = relatePointsList.get(1);
                        NewsFeed.Source source3 = relatePointsList.get(2);
                        setRelateView(source1, holder3.rlRelate1, holder3.tvRelate1, holder3.tvSource1, true);
                        setRelateView(source2, holder3.rlRelate2, holder3.tvRelate2, holder3.tvSource2, true);
                        setRelateView(source3, holder3.rlRelate3, holder3.tvRelate3, holder3.tvSource3, true);
                        setVerticalLineHeight(holder3.rlRelate1, holder3.ivVerticalLine1);
                        setVerticalLineHeight(holder3.rlRelate2, holder3.ivVerticalLine2);
                        holder3.ivVerticalLine3.setVisibility(View.GONE);
                        holder3.tvBottomLine3.setVisibility(View.GONE);
                    }
                } else {
                    holder3.llSourceContent.setVisibility(View.GONE);
                }
            }
            Logger.e("jigang","getView consume time "+(System.currentTimeMillis() - start));
            return convertView;
        }
    }

    class BaseHolder {
        TextViewExtend tvSource;
        TextViewExtend tvComment;
        TextView tvTitle;
        LinearLayout llSourceContent;
        RelativeLayout rlNewsContent;
        ImageView ivVerticalLine1;
        ImageView ivVerticalLine2;
        ImageView ivVerticalLine3;
        TextView tvBottomLine1;
        TextView tvBottomLine2;
        TextView tvBottomLine3;
        RelativeLayout rlRelate1;
        RelativeLayout rlRelate2;
        RelativeLayout rlRelate3;
        TextView tvRelate1;
        TextView tvRelate2;
        TextView tvRelate3;
        TextViewExtend tvSource1;
        TextViewExtend tvSource2;
        TextViewExtend tvSource3;
    }

    class ViewHolder extends BaseHolder {
        SimpleDraweeView ivTitleImg;
    }

    class ViewHolder2 {
        SimpleDraweeView iv_title_img;
        RelativeLayout rl_item_content;
        TextViewVertical tv_title;
    }

    class ViewHolder3 extends BaseHolder {
        SimpleDraweeView ivCard1;
        SimpleDraweeView ivCard2;
        SimpleDraweeView ivCard3;
        LinearLayout llImageList;
    }
}