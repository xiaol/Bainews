package com.news.yazhidao.pages;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PointF;
import android.graphics.drawable.AnimationDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.Button;
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
import com.news.yazhidao.net.JsonCallback;
import com.news.yazhidao.net.MyAppException;
import com.news.yazhidao.net.NetworkRequest;
import com.news.yazhidao.utils.DensityUtil;
import com.news.yazhidao.utils.DeviceInfoUtil;
import com.news.yazhidao.utils.NetUtil;
import com.news.yazhidao.utils.TextUtil;
import com.news.yazhidao.utils.ToastUtil;
import com.news.yazhidao.utils.adcoco.AdcocoUtil;
import com.news.yazhidao.widget.TextViewExtend;
import com.news.yazhidao.widget.TextViewVertical;
import com.umeng.analytics.AnalyticsConfig;
import com.umeng.analytics.MobclickAgent;

import java.util.ArrayList;
import java.util.HashMap;

public class NewsFeedFgt extends Fragment {

    public static String KEY_NEWS_SOURCE = "key_news_source";
    public static String KEY_URL = "key_url";
    public static final String VALUE_NEWS_NOTIFICATION = "notification";
    public static String VALUE_NEWS_SOURCE = "other_view";
    private Button btn_reload;
    private NewsFeedAdapter mAdapter;
    private AnimationDrawable mAniNewsLoading;
    private ArrayList<NewsFeed> mArrNewsFeed;
    private Context mContext;
    private View mNewsFeedProgressWheelWrapper;
    private ImageView mNewsLoadingImg;
    NetworkRequest mRequest;
    private int mScreenHeight;
    private int mScreenWidth;
    private LinearLayout mllNoNetwork;
    private PullToRefreshListView mlvNewsFeed;

    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        mContext = getActivity();
        WindowManager localWindowManager = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);
        mScreenWidth = localWindowManager.getDefaultDisplay().getWidth();
        mScreenHeight = localWindowManager.getDefaultDisplay().getHeight();
    }

    public View onCreateView(LayoutInflater paramLayoutInflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = paramLayoutInflater.inflate(R.layout.activity_news, container, false);
        mNewsFeedProgressWheelWrapper = rootView.findViewById(R.id.mNewsFeedProgressWheelWrapper);
        mNewsLoadingImg = (ImageView) rootView.findViewById(R.id.mNewsLoadingImg);
        mNewsLoadingImg.setImageResource(R.drawable.loading_process_new_gif);
        mllNoNetwork = (LinearLayout) rootView.findViewById(R.id.no_network_layout);
        btn_reload = (Button) rootView.findViewById(R.id.btn_reload);
        btn_reload.setOnClickListener(new View.OnClickListener() {
            public void onClick(View paramAnonymousView) {
                if (NetUtil.checkNetWork(mContext)) {
                    mNewsFeedProgressWheelWrapper.setVisibility(View.VISIBLE);
                    mAniNewsLoading = (AnimationDrawable) mNewsLoadingImg.getDrawable();
                    mAniNewsLoading.start();
                } else {
                    mlvNewsFeed.setVisibility(View.GONE);
                    mllNoNetwork.setVisibility(View.VISIBLE);
                    ToastUtil.toastShort("您的网络出现问题，请检查网络设置...");
                }
            }
        });
        mlvNewsFeed = ((PullToRefreshListView) rootView.findViewById(R.id.news_feed_listView));
        mlvNewsFeed.setMode(PullToRefreshBase.Mode.BOTH);
        mlvNewsFeed.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ListView>() {
            @Override
            public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {

            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {

            }
        });
        mAdapter = new NewsFeedAdapter();
        mlvNewsFeed.setAdapter(mAdapter);
        loadData();
        String platform = AnalyticsConfig.getChannel(getActivity());
        if ("adcoco".equals(platform)) {
            AdcocoUtil.setup(getActivity());
            try {
                new AdcocoUtil().insertAdcoco(mArrNewsFeed, mlvNewsFeed.getRefreshableView(), mArrNewsFeed.size(), -1);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return rootView;
    }

    private void loadNewsFeedData(int position1, int position2, final boolean flag) {
        if (flag) {
            if (mNewsFeedProgressWheelWrapper != null)
                mNewsFeedProgressWheelWrapper.setVisibility(View.VISIBLE);
            if (mAniNewsLoading != null)
                mAniNewsLoading.start();
        }
        mllNoNetwork.setVisibility(View.GONE);
        mRequest = new NetworkRequest("http://api.deeporiginalx.com/news/baijia/newsFetchHome?channelId=&page=" + position2 + "&limit=50", NetworkRequest.RequestMethod.GET);
        mRequest.setCallback(new JsonCallback<ArrayList<NewsFeed>>() {
            public void failed(MyAppException paramAnonymousMyAppException) {
                mlvNewsFeed.onRefreshComplete();
                mNewsFeedProgressWheelWrapper.setVisibility(View.GONE);
                mAniNewsLoading.stop();
                mllNoNetwork.setVisibility(View.VISIBLE);
                mlvNewsFeed.setVisibility(View.GONE);
            }

            public void success(ArrayList<NewsFeed> result) {
                if (result != null && result.size() > 0) {
                    if (flag)
                        mArrNewsFeed = result;
                    mlvNewsFeed.setVisibility(View.VISIBLE);
                    mlvNewsFeed.getRefreshableView().setSelection(0);
                    mAdapter.notifyDataSetChanged();
                } else {
                    ToastUtil.toastLong("网络不给力,请检查网络....  size 0");
                    mllNoNetwork.setVisibility(View.VISIBLE);
                }
                mlvNewsFeed.onRefreshComplete();
                mNewsFeedProgressWheelWrapper.setVisibility(View.GONE);
                mAniNewsLoading.stop();
            }
        }.setReturnType(new TypeToken<ArrayList<NewsFeed>>() {
        }.getType()));
        mRequest.execute();
    }

    private void setCardMargin(SimpleDraweeView ivCard, int leftMargin, int rightMargin) {
        LinearLayout.LayoutParams localLayoutParams = (LinearLayout.LayoutParams) ivCard.getLayoutParams();
        localLayoutParams.leftMargin = DensityUtil.dip2px(mContext, leftMargin);
        localLayoutParams.rightMargin = DensityUtil.dip2px(mContext, rightMargin);
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
                    intent.putExtra(KEY_URL, source.getUrl());
                    startActivity(intent);
                    //umeng statistic onclick url below the head news
                    HashMap<String, String> _MobMap = new HashMap<>();
                    _MobMap.put("resource_site_name", source.getSourceSitename());
                    MobclickAgent.onEvent(mContext, CommonConstant.US_BAINEWS_CLICK_URL_BELOW_HEAD_VEWS, _MobMap);
                }
            });
            if (source != null) {
                String source_name = source.getSourceSitename();
                String finalText = "";
                String source_title = source.getTitle();
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
        }else {
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

    public void CancelRequest() {
        if (mRequest != null)
            mRequest.cancel(true);
    }

    public void loadData() {
        if (NetUtil.checkNetWork(mContext)) {
            mllNoNetwork.setVisibility(View.GONE);
            loadNewsFeedData(1, 10, true);
            mNewsFeedProgressWheelWrapper.setVisibility(View.VISIBLE);
            mAniNewsLoading = ((AnimationDrawable) mNewsLoadingImg.getDrawable());
            mAniNewsLoading.start();
        } else {
            mlvNewsFeed.setVisibility(View.GONE);
            mllNoNetwork.setVisibility(View.VISIBLE);
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
            final NewsFeed feed = mArrNewsFeed.get(position);
            ArrayList<NewsFeed.Source> sourceList = (ArrayList<NewsFeed.Source>) feed.getSublist();
            String strSpecial = feed.getSpecial();
            ViewHolder holder;
            //普通卡片
            if (("400".equals(strSpecial)) || (strSpecial == null)) {
                String platform = AnalyticsConfig.getChannel(getActivity());
                if ("adcoco".equals(platform)) {
                    AdcocoUtil.update();
                }
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

                if (DeviceInfoUtil.isFlyme()) {
                    holder.rlNewsContent.setPadding(View.VISIBLE, 0, 0, DensityUtil.dip2px(getActivity(), 15.0F));
                }

                String strImgUrl = feed.getImgUrl();
                if ((strImgUrl != null) && (!"".equals(strImgUrl))) {
                    holder.ivTitleImg.setImageURI(Uri.parse(strImgUrl));
                    holder.ivTitleImg.getHierarchy().setActualImageFocusPoint(new PointF(0.5F, 0.4F));
                }

                String strTitle = feed.getTitle();
                if ((strTitle != null) && (!"".equals(strTitle))) {
                    holder.tvTitle.setText(strTitle, TextView.BufferType.SPANNABLE);
                }

                holder.rlNewsContent.setOnClickListener(new View.OnClickListener() {
                    long firstClick = 0;

                    public void onClick(View paramAnonymousView) {
                        if (System.currentTimeMillis() - firstClick <= 1500L) {
                            firstClick = System.currentTimeMillis();
                            return;
                        }
                        firstClick = System.currentTimeMillis();
                        Intent localIntent = new Intent(mContext, NewsDetailAty2.class);
                        localIntent.putExtra(NewsFeedFgt.KEY_URL, feed.getSourceUrl());
                        localIntent.putExtra("position", position);
                        startActivityForResult(localIntent, 0);
                        MobclickAgent.onEvent(mContext, "bainews_view_head_news");
                    }
                });
                if (sourceList != null && sourceList.size() > 0) {
                    int size = sourceList.size();
                    holder.llSourceContent.setVisibility(View.VISIBLE);
                    holder.ivVerticalLine1.setVisibility(View.VISIBLE);
                    holder.tvBottomLine1.setVisibility(View.VISIBLE);
                    holder.ivVerticalLine2.setVisibility(View.VISIBLE);
                    holder.tvBottomLine2.setVisibility(View.VISIBLE);
                    holder.ivVerticalLine3.setVisibility(View.VISIBLE);
                    holder.tvBottomLine3.setVisibility(View.VISIBLE);
                    if (size == 1) {
                        NewsFeed.Source source = sourceList.get(0);
                        setRelateView(source, holder.rlRelate1, holder.tvRelate1, holder.tvSource1, true);
                        setRelateView(null, holder.rlRelate2, holder.tvRelate2, holder.tvSource2, false);
                        setRelateView(null, holder.rlRelate3, holder.tvRelate3, holder.tvSource3, false);
                        holder.ivVerticalLine1.setVisibility(View.GONE);
                        holder.tvBottomLine1.setVisibility(View.GONE);
                    } else if (size == 2) {
                        NewsFeed.Source source1 = sourceList.get(0);
                        NewsFeed.Source source2 = sourceList.get(1);
                        setRelateView(source1, holder.rlRelate1, holder.tvRelate1, holder.tvSource1, true);
                        setRelateView(source2, holder.rlRelate2, holder.tvRelate2, holder.tvSource2, true);
                        setRelateView(null, holder.rlRelate3, holder.tvRelate3, holder.tvSource3, false);
                        setVerticalLineHeight(holder.rlRelate1, holder.ivVerticalLine1);
                        holder.ivVerticalLine2.setVisibility(View.GONE);
                        holder.tvBottomLine2.setVisibility(View.GONE);
                    } else if (size == 3) {
                        NewsFeed.Source source1 = sourceList.get(0);
                        NewsFeed.Source source2 = sourceList.get(1);
                        NewsFeed.Source source3 = sourceList.get(2);
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
            else if ("1".equals(strSpecial)) {
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

                int textsize = DensityUtil.dip2px(mContext, 18);
                holder2.tv_title.setTextSize(textsize);
                holder2.tv_title.setTextColor(new Color().parseColor("#f7f7f7"));
                holder2.tv_title.setLineWidth(DensityUtil.dip2px(mContext, 20));
                holder2.tv_title.setShadowLayer(4f, 1, 2, new Color().parseColor("#000000"));

                String strImgUrl = feed.getImgUrl();
                if (feed.getImgUrl() != null && !("".equals(strImgUrl))) {
                    holder2.iv_title_img.setImageURI(Uri.parse(strImgUrl));
                    holder2.iv_title_img.getHierarchy().setActualImageFocusPoint(new PointF(.5f, .4f));
                } else {
                    holder2.tv_title.setBackgroundResource(R.drawable.bg_load_default_big);
                }

                holder2.rl_item_content.setOnClickListener(new View.OnClickListener() {
                    long firstClick = 0;

                    @Override
                    public void onClick(View v) {
                        if (System.currentTimeMillis() - firstClick <= 1500) {
                            firstClick = System.currentTimeMillis();
                            return;
                        }
                        firstClick = System.currentTimeMillis();
                        Intent intent = new Intent(mContext, NewsDetailAty2.class);
                        intent.putExtra(KEY_URL, feed.getSourceUrl());
                        intent.putExtra("position", position);
                        startActivityForResult(intent, 0);
                        //uemng statistic view the head news
                        MobclickAgent.onEvent(mContext, CommonConstant.US_BAINEWS_VIEW_HEAD_NEWS);
                    }
                });
            }
            //多图
            else if ("9".equals(feed.getSpecial())) {
                ViewHolder3 holder3;
                if ((convertView == null) || (convertView.getTag().getClass() != ViewHolder3.class)) {
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
                if (DeviceInfoUtil.isFlyme()) {
                    holder3.rlNewsContent.setPadding(View.VISIBLE, 0, 0, DensityUtil.dip2px(getActivity(), 15.0F));
                }
                String[] images = feed.getImgUrl_ex();
                String strImg1 = images[0];
                if ((strImg1 != null) && (!"".equals(strImg1))) {
                    holder3.ivCard1.setImageURI(Uri.parse(strImg1));
                    holder3.ivCard1.getHierarchy().setActualImageFocusPoint(new PointF(0.5F, 0.4F));
                }
                String strImg2 = images[1];
                if ((strImg2 != null) && (!"".equals(strImg2))) {
                    holder3.ivCard2.setImageURI(Uri.parse(strImg2));
                    holder3.ivCard2.getHierarchy().setActualImageFocusPoint(new PointF(0.5F, 0.4F));
                }

                if (images.length == 3) {
                    String strImg3 = images[2];
                    holder3.ivCard3.setVisibility(View.VISIBLE);
                    if (strImg3 != null && !"".equals(strImg3)) {
                        holder3.ivCard3.setImageURI(Uri.parse(strImg3));
                        holder3.ivCard3.getHierarchy().setActualImageFocusPoint(new PointF(0.5F, 0.4F));
                    }
                    setCardMargin(holder3.ivCard1, 8, 4);
                    setCardMargin(holder3.ivCard2, 4, 4);
                    setCardMargin(holder3.ivCard3, 4, 8);
                } else {
                    holder3.ivCard3.setVisibility(View.GONE);
                    setCardMargin(holder3.ivCard1, 8, 4);
                    setCardMargin(holder3.ivCard2, 4, 8);
                }

                String strTitle = feed.getTitle();
                if (strTitle != null && !"".equals(strTitle)) {
                    holder3.tvTitle.setText(strTitle, TextView.BufferType.SPANNABLE);
                }

                holder3.rlNewsContent.setOnClickListener(new View.OnClickListener() {
                    long firstClick = 0L;

                    public void onClick(View paramAnonymousView) {
                        if (System.currentTimeMillis() - firstClick <= 1500L) {
                            firstClick = System.currentTimeMillis();
                            return;
                        }
                        firstClick = System.currentTimeMillis();
                        Intent localIntent = new Intent(mContext, NewsDetailAty2.class);
                        localIntent.putExtra(NewsFeedFgt.KEY_URL, feed.getSourceUrl());
                        localIntent.putExtra("position", position);
                        startActivityForResult(localIntent, 0);
                        MobclickAgent.onEvent(mContext, "bainews_view_head_news");
                    }
                });
                if (sourceList != null && sourceList.size() > 0) {
                    int size = sourceList.size();
                    holder3.llSourceContent.setVisibility(View.VISIBLE);
                    holder3.ivVerticalLine1.setVisibility(View.VISIBLE);
                    holder3.tvBottomLine1.setVisibility(View.VISIBLE);
                    holder3.ivVerticalLine2.setVisibility(View.VISIBLE);
                    holder3.tvBottomLine2.setVisibility(View.VISIBLE);
                    holder3.ivVerticalLine3.setVisibility(View.VISIBLE);
                    holder3.tvBottomLine3.setVisibility(View.VISIBLE);
                    if (size == 1) {
                        NewsFeed.Source source = sourceList.get(0);
                        setRelateView(source, holder3.rlRelate1, holder3.tvRelate1, holder3.tvSource1, true);
                        setRelateView(null, holder3.rlRelate2, holder3.tvRelate2, holder3.tvSource2, false);
                        setRelateView(null, holder3.rlRelate3, holder3.tvRelate3, holder3.tvSource3, false);
                        holder3.ivVerticalLine1.setVisibility(View.GONE);
                        holder3.tvBottomLine1.setVisibility(View.GONE);
                    } else if (size == 2) {
                        NewsFeed.Source source1 = sourceList.get(0);
                        NewsFeed.Source source2 = sourceList.get(1);
                        setRelateView(source1, holder3.rlRelate1, holder3.tvRelate1, holder3.tvSource1, true);
                        setRelateView(source2, holder3.rlRelate2, holder3.tvRelate2, holder3.tvSource2, true);
                        setRelateView(null, holder3.rlRelate3, holder3.tvRelate3, holder3.tvSource3, false);
                        setVerticalLineHeight(holder3.rlRelate1, holder3.ivVerticalLine1);
                        holder3.ivVerticalLine2.setVisibility(View.GONE);
                        holder3.tvBottomLine2.setVisibility(View.GONE);
                    } else if (size == 3) {
                        NewsFeed.Source source1 = sourceList.get(0);
                        NewsFeed.Source source2 = sourceList.get(1);
                        NewsFeed.Source source3 = sourceList.get(2);
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

            return convertView;
        }
    }

    class ViewHolder {
        SimpleDraweeView ivTitleImg;
        ImageView ivVerticalLine1;
        ImageView ivVerticalLine2;
        ImageView ivVerticalLine3;
        LinearLayout llSourceContent;
        RelativeLayout rlNewsContent;
        RelativeLayout rlRelate1;
        RelativeLayout rlRelate2;
        RelativeLayout rlRelate3;
        TextView tvBottomLine1;
        TextView tvBottomLine2;
        TextView tvBottomLine3;
        TextViewExtend tvComment;
        TextView tvRelate1;
        TextView tvRelate2;
        TextView tvRelate3;
        TextViewExtend tvSource;
        TextViewExtend tvSource1;
        TextViewExtend tvSource2;
        TextViewExtend tvSource3;
        TextView tvTitle;
    }

    class ViewHolder2 {
        SimpleDraweeView iv_title_img;
        RelativeLayout rl_item_content;
        TextViewVertical tv_title;
    }

    class ViewHolder3 {
        SimpleDraweeView ivCard1;
        SimpleDraweeView ivCard2;
        SimpleDraweeView ivCard3;
        ImageView ivVerticalLine1;
        ImageView ivVerticalLine2;
        ImageView ivVerticalLine3;
        LinearLayout llImageList;
        LinearLayout llSourceContent;
        RelativeLayout rlNewsContent;
        RelativeLayout rlRelate1;
        RelativeLayout rlRelate2;
        RelativeLayout rlRelate3;
        TextView tvBottomLine1;
        TextView tvBottomLine2;
        TextView tvBottomLine3;
        TextViewExtend tvComment;
        TextView tvRelate1;
        TextView tvRelate2;
        TextView tvRelate3;
        TextViewExtend tvSource;
        TextViewExtend tvSource1;
        TextViewExtend tvSource2;
        TextViewExtend tvSource3;
        TextView tvTitle;
    }
}