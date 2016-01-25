package com.news.yazhidao.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.PointF;
import android.net.Uri;
import android.text.Html;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.news.yazhidao.R;
import com.news.yazhidao.common.CommonConstant;
import com.news.yazhidao.entity.NewsFeed;
import com.news.yazhidao.pages.NewsDetailAty2;
import com.news.yazhidao.pages.NewsDetailWebviewAty;
import com.news.yazhidao.pages.NewsFeedFgt;
import com.news.yazhidao.utils.DensityUtil;
import com.news.yazhidao.utils.DeviceInfoUtil;
import com.news.yazhidao.utils.TextUtil;
import com.news.yazhidao.utils.adcoco.AdcocoUtil;
import com.news.yazhidao.widget.TextViewExtend;
import com.umeng.analytics.AnalyticsConfig;
import com.umeng.analytics.MobclickAgent;

import java.util.ArrayList;
import java.util.HashMap;


public class NewsFeedAdapter extends BaseAdapter {

    private ArrayList<NewsFeed> mArrNewsFeed;
    private String mstrKeyWord;
    private int mScreenHeight;
    private int mScreenWidth;
    private Context mContext;
    public static String KEY_URL = "key_url";

    public NewsFeedAdapter(Context context) {
        mContext = context;
        mScreenWidth = DeviceInfoUtil.getScreenWidth();
        mScreenHeight = DeviceInfoUtil.getScreenHeight();
    }

    public void setNewsFeed(ArrayList<NewsFeed> arrNewsFeed) {
        mArrNewsFeed = arrNewsFeed;
    }

    public void setSearchKeyWord(String pKeyWord) {
        mstrKeyWord = pKeyWord;
        mArrNewsFeed = null;
    }

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
        ArrayList<NewsFeed.Source> relatePointsList = (ArrayList<NewsFeed.Source>) feed.getRelatePointsList();
        String strType = feed.getType();
        //普通卡片
        if ("one_pic".equals(strType) || "no_pic".equals(strType) || "two_pic".equals(strType)) {
            String platform = AnalyticsConfig.getChannel(mContext);
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
                holder.ivSource1 = (ImageView) holder.rlRelate1.findViewById(R.id.iv_source);
                holder.ivVerticalTopLine1 = (ImageView) holder.rlRelate1.findViewById(R.id.iv_combine_line_top);
                holder.tvRelate1 = (TextView) holder.rlRelate1.findViewById(R.id.tv_relate);
                holder.tvSource1 = (TextViewExtend) holder.rlRelate1.findViewById(R.id.tv_news_source);
                holder.ivVerticalLine2 = (ImageView) holder.rlRelate2.findViewById(R.id.iv_combine_line);
                holder.ivSource2 = (ImageView) holder.rlRelate2.findViewById(R.id.iv_source);
                holder.ivVerticalTopLine2 = (ImageView) holder.rlRelate2.findViewById(R.id.iv_combine_line_top);
                holder.tvRelate2 = (TextView) holder.rlRelate2.findViewById(R.id.tv_relate);
                holder.tvSource2 = (TextViewExtend) holder.rlRelate2.findViewById(R.id.tv_news_source);
                holder.ivVerticalLine3 = (ImageView) holder.rlRelate3.findViewById(R.id.iv_combine_line);
                holder.ivSource3 = (ImageView) holder.rlRelate3.findViewById(R.id.iv_source);
                holder.ivVerticalTopLine3 = (ImageView) holder.rlRelate3.findViewById(R.id.iv_combine_line_top);
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
            if ("one_pic".equals(strType) || "two_pic".equals(strType)) {
                strImg = strArrImgUrl.get(0);
            }
            if (strImg != null && !"".equals(strImg)) {
                holder.ivTitleImg.setVisibility(View.VISIBLE);
                holder.ivTitleImg.setImageURI(Uri.parse(strImg));
                holder.ivTitleImg.getHierarchy().setActualImageFocusPoint(new PointF(0.5F, 0.4F));
            } else {
                holder.ivTitleImg.setVisibility(View.GONE);
            }

            String strTitle = feed.getTitle();
            setTitleTextBySpannable(holder.tvTitle, strTitle);
            setViewText(holder.tvSource, feed.getSourceSiteName());
            setViewText(holder.tvComment, feed.getCommentNum() + "评论");
            setNewsContentClick(holder.rlNewsContent, feed);
            if (relatePointsList != null && relatePointsList.size() > 0) {
                int size = relatePointsList.size();
                holder.llSourceContent.setVisibility(View.VISIBLE);
                holder.ivVerticalLine1.setVisibility(View.VISIBLE);
//                holder.tvBottomLine1.setVisibility(View.VISIBLE);
                holder.ivVerticalLine2.setVisibility(View.VISIBLE);
//                holder.tvBottomLine2.setVisibility(View.VISIBLE);
                holder.ivVerticalLine3.setVisibility(View.VISIBLE);
//                holder.tvBottomLine3.setVisibility(View.VISIBLE);
                if (size == 1) {
                    NewsFeed.Source source = relatePointsList.get(0);
                    setRelateView(source, holder.rlRelate1, holder.tvRelate1, holder.tvSource1, true, strTitle);
                    setRelateView(null, holder.rlRelate2, holder.tvRelate2, holder.tvSource2, false, strTitle);
                    setRelateView(null, holder.rlRelate3, holder.tvRelate3, holder.tvSource3, false, strTitle);
                    setVerticalTopLineHeight(holder.ivSource1, holder.ivVerticalTopLine1);
                    holder.ivVerticalLine1.setVisibility(View.GONE);
//                    holder.tvBottomLine1.setVisibility(View.GONE);
                } else if (size == 2) {
                    NewsFeed.Source source1 = relatePointsList.get(0);
                    NewsFeed.Source source2 = relatePointsList.get(1);
                    setRelateView(source1, holder.rlRelate1, holder.tvRelate1, holder.tvSource1, true, strTitle);
                    setRelateView(source2, holder.rlRelate2, holder.tvRelate2, holder.tvSource2, true, strTitle);
                    setRelateView(null, holder.rlRelate3, holder.tvRelate3, holder.tvSource3, false, strTitle);
                    setVerticalLineHeight(holder.rlRelate1, holder.ivVerticalLine1);
                    setVerticalTopLineHeight(holder.ivSource1, holder.ivVerticalTopLine1);
                    setVerticalTopLineHeight(holder.ivSource2, holder.ivVerticalTopLine2);
                    holder.ivVerticalLine2.setVisibility(View.GONE);
//                    holder.tvBottomLine2.setVisibility(View.GONE);
                } else {
                    NewsFeed.Source source1 = relatePointsList.get(0);
                    NewsFeed.Source source2 = relatePointsList.get(1);
                    NewsFeed.Source source3 = relatePointsList.get(2);
                    setRelateView(source1, holder.rlRelate1, holder.tvRelate1, holder.tvSource1, true, strTitle);
                    setRelateView(source2, holder.rlRelate2, holder.tvRelate2, holder.tvSource2, true, strTitle);
                    setRelateView(source3, holder.rlRelate3, holder.tvRelate3, holder.tvSource3, true, strTitle);
                    setVerticalLineHeight(holder.rlRelate1, holder.ivVerticalLine1);
                    setVerticalLineHeight(holder.rlRelate2, holder.ivVerticalLine2);
                    setVerticalTopLineHeight(holder.ivSource1, holder.ivVerticalTopLine1);
                    setVerticalTopLineHeight(holder.ivSource2, holder.ivVerticalTopLine2);
                    setVerticalTopLineHeight(holder.ivSource3, holder.ivVerticalTopLine3);
                    holder.ivVerticalLine3.setVisibility(View.GONE);
//                    holder.tvBottomLine3.setVisibility(View.GONE);
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
                holder2.tv_title = (TextViewExtend) convertView.findViewById(R.id.tv_title);
                RelativeLayout.LayoutParams lpImg = (RelativeLayout.LayoutParams) holder2.iv_title_img.getLayoutParams();
                lpImg.width = mScreenWidth;
                lpImg.height = (int) (mScreenWidth * (182 / 320.0f));
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
//            if (title != null && !"".equals(title)) {
//                holder2.tv_title.setText(title, mstrKeyWord);
//            } else {
//                holder2.tv_title.setText(title_news, mstrKeyWord);
//            }
//
//            int textSize = DensityUtil.dip2px(mContext, 18);
//            holder2.tv_title.setTextSize(textSize);
//            holder2.tv_title.setTextColor(new Color().parseColor("#f7f7f7"));
//            holder2.tv_title.setLineWidth(DensityUtil.dip2px(mContext, 20));
//            holder2.tv_title.setShadowLayer(4f, 1, 2, new Color().parseColor("#000000"));

            ArrayList<String> strArrImgUrl = feed.getImgUrls();
            String strImgUrl = strArrImgUrl.get(0);
            if (strImgUrl != null && !"".equals(strImgUrl)) {
                holder2.iv_title_img.setImageURI(Uri.parse(strImgUrl));
                holder2.iv_title_img.getHierarchy().setActualImageFocusPoint(new PointF(0.5f, 0.4f));
            }
            setNewsContentClick(holder2.rl_item_content, feed);
        }
        //多图
        else if ("three_pic".equals(strType)) {
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
                holder3.ivSource1 = (ImageView) holder3.rlRelate1.findViewById(R.id.iv_source);
                holder3.ivVerticalTopLine1 = (ImageView) holder3.rlRelate1.findViewById(R.id.iv_combine_line_top);
                holder3.tvRelate1 = (TextView) holder3.rlRelate1.findViewById(R.id.tv_relate);
                holder3.tvSource1 = (TextViewExtend) holder3.rlRelate1.findViewById(R.id.tv_news_source);
                holder3.ivVerticalLine2 = (ImageView) holder3.rlRelate2.findViewById(R.id.iv_combine_line);
                holder3.ivSource2 = (ImageView) holder3.rlRelate2.findViewById(R.id.iv_source);
                holder3.ivVerticalTopLine2 = (ImageView) holder3.rlRelate2.findViewById(R.id.iv_combine_line_top);
                holder3.tvRelate2 = (TextView) holder3.rlRelate2.findViewById(R.id.tv_relate);
                holder3.tvSource2 = (TextViewExtend) holder3.rlRelate2.findViewById(R.id.tv_news_source);
                holder3.ivVerticalLine3 = (ImageView) holder3.rlRelate3.findViewById(R.id.iv_combine_line);
                holder3.ivSource3 = (ImageView) holder3.rlRelate3.findViewById(R.id.iv_source);
                holder3.ivVerticalTopLine3 = (ImageView) holder3.rlRelate3.findViewById(R.id.iv_combine_line_top);
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

//            if (strArrImgUrl.size() == 2) {
//                holder3.ivCard3.setVisibility(View.GONE);
//                setCardMargin(holder3.ivCard1, 8, 4, 2);
//                setCardMargin(holder3.ivCard2, 4, 8, 2);
//            } else {
            holder3.ivCard3.setVisibility(View.VISIBLE);
            setLoadImage(holder3.ivCard3, strArrImgUrl.get(2));
            setCardMargin(holder3.ivCard1, 8, 4, 3);
            setCardMargin(holder3.ivCard2, 4, 4, 3);
            setCardMargin(holder3.ivCard3, 4, 8, 3);
//            }
            String strTitle = feed.getTitle();
            setTitleTextBySpannable(holder3.tvTitle, strTitle);
            setViewText(holder3.tvSource, feed.getSourceSiteName());
            setViewText(holder3.tvComment, feed.getCommentNum() + "评论");
            setNewsContentClick(holder3.rlNewsContent, feed);
            if (relatePointsList != null && relatePointsList.size() > 0) {
                int size = relatePointsList.size();
                holder3.llSourceContent.setVisibility(View.VISIBLE);
                holder3.ivVerticalLine1.setVisibility(View.VISIBLE);
//                holder3.tvBottomLine1.setVisibility(View.VISIBLE);
                holder3.ivVerticalLine2.setVisibility(View.VISIBLE);
//                holder3.tvBottomLine2.setVisibility(View.VISIBLE);
                holder3.ivVerticalLine3.setVisibility(View.VISIBLE);
//                holder3.tvBottomLine3.setVisibility(View.VISIBLE);
                if (size == 1) {
                    NewsFeed.Source source = relatePointsList.get(0);
                    setRelateView(source, holder3.rlRelate1, holder3.tvRelate1, holder3.tvSource1, true, strTitle);
                    setRelateView(null, holder3.rlRelate2, holder3.tvRelate2, holder3.tvSource2, false, strTitle);
                    setRelateView(null, holder3.rlRelate3, holder3.tvRelate3, holder3.tvSource3, false, strTitle);
                    setVerticalTopLineHeight(holder3.ivSource1, holder3.ivVerticalTopLine1);
                    holder3.ivVerticalLine1.setVisibility(View.GONE);
//                    holder3.tvBottomLine1.setVisibility(View.GONE);
                } else if (size == 2) {
                    NewsFeed.Source source1 = relatePointsList.get(0);
                    NewsFeed.Source source2 = relatePointsList.get(1);
                    setRelateView(source1, holder3.rlRelate1, holder3.tvRelate1, holder3.tvSource1, true, strTitle);
                    setRelateView(source2, holder3.rlRelate2, holder3.tvRelate2, holder3.tvSource2, true, strTitle);
                    setRelateView(null, holder3.rlRelate3, holder3.tvRelate3, holder3.tvSource3, false, strTitle);
                    setVerticalLineHeight(holder3.rlRelate1, holder3.ivVerticalLine1);
                    setVerticalTopLineHeight(holder3.ivSource1, holder3.ivVerticalTopLine1);
                    setVerticalTopLineHeight(holder3.ivSource2, holder3.ivVerticalTopLine2);
                    holder3.ivVerticalLine2.setVisibility(View.GONE);
//                    holder3.tvBottomLine2.setVisibility(View.GONE);
                } else {
                    NewsFeed.Source source1 = relatePointsList.get(0);
                    NewsFeed.Source source2 = relatePointsList.get(1);
                    NewsFeed.Source source3 = relatePointsList.get(2);
                    setRelateView(source1, holder3.rlRelate1, holder3.tvRelate1, holder3.tvSource1, true, strTitle);
                    setRelateView(source2, holder3.rlRelate2, holder3.tvRelate2, holder3.tvSource2, true, strTitle);
                    setRelateView(source3, holder3.rlRelate3, holder3.tvRelate3, holder3.tvSource3, true, strTitle);
                    setVerticalLineHeight(holder3.rlRelate1, holder3.ivVerticalLine1);
                    setVerticalLineHeight(holder3.rlRelate2, holder3.ivVerticalLine2);
                    setVerticalTopLineHeight(holder3.ivSource1, holder3.ivVerticalTopLine1);
                    setVerticalTopLineHeight(holder3.ivSource2, holder3.ivVerticalTopLine2);
                    setVerticalTopLineHeight(holder3.ivSource3, holder3.ivVerticalTopLine3);
                    holder3.ivVerticalLine3.setVisibility(View.GONE);
//                    holder3.tvBottomLine3.setVisibility(View.GONE);
                }
            } else {
                holder3.llSourceContent.setVisibility(View.GONE);
            }
        }
        return convertView;
    }

    private void setCardMargin(SimpleDraweeView ivCard, int leftMargin, int rightMargin, int pageNum) {
        LinearLayout.LayoutParams localLayoutParams = (LinearLayout.LayoutParams) ivCard.getLayoutParams();
        localLayoutParams.leftMargin = DensityUtil.dip2px(mContext, leftMargin);
        localLayoutParams.rightMargin = DensityUtil.dip2px(mContext, rightMargin);
        int width = (int) (mScreenWidth / 2.0f - DensityUtil.dip2px(mContext, 12));
        if (pageNum == 2) {
            localLayoutParams.width = width;
            localLayoutParams.height = (int) (width * 74 / 102.0f);
        } else if (pageNum == 3) {
            width = (int) (mScreenWidth / 3.0f - DensityUtil.dip2px(mContext, 12));
            localLayoutParams.width = width;
            localLayoutParams.height = (int) (width * 74 / 102.0f);
        }
        ivCard.setLayoutParams(localLayoutParams);
    }

    private void setRelateView(final NewsFeed.Source source, RelativeLayout rlRelate, TextView tvRelate, TextViewExtend tvSource, boolean IsVisible, String strTitle) {
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
                    mContext.startActivity(intent);
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
                if (TextUtil.isEmptyString(source_title))
                    source_title = strTitle;
                String source_name_font = "";
//                source_title = "<font color =\"#888888\">" + "<big>" + source_title + "</big>" + "</font>";
                if (source_name != null) {
                    if (source.getUser() != null && !"".equals(source.getUser())) {
//                        source_name_font = "<font color =\"#7d7d7d\">" + "<big>" + source.getUser() + "</big>" + "</font>" + ": ";
                        finalText = source.getUser() + source_title;
                        tvSource.setText(Html.fromHtml(finalText));
                    } else {
//                        source_name_font = "<font color =\"#7d7d7d\">" + "<big>" + source_name + "</big>" + "</font>" + ": ";
                        finalText = source_name + source_title;
                        tvSource.setText(Html.fromHtml(finalText));
                    }
                } else {
//                    String anonyStr = "<font color =\"#7d7d7d\">" + "<big>" + "匿名报道:" + "</big>" + "</font>" + ": ";
                    finalText = "匿名报道:" + source_title;
                    tvSource.setText(Html.fromHtml(finalText));
                }
                if (source.getSimilarity() != null && !"".equals(source.getSimilarity())) {
                    if (source.getSimilarity().length() > 4) {
                        String ss = source.getSimilarity().substring(2, 4);
                        tvRelate.setText(ss + "%");
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

    private void setVerticalTopLineHeight(final ImageView ivSource, final ImageView ivVerticalLine) {
        ivVerticalLine.post(new Runnable() {
            public void run() {
                RelativeLayout.LayoutParams lpVerticalLine = (RelativeLayout.LayoutParams) ivVerticalLine.getLayoutParams();
                lpVerticalLine.height = (int) ivSource.getY();
                ivVerticalLine.setLayoutParams(lpVerticalLine);
            }
        });
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
                strTitle = strTitle.replace(mstrKeyWord.toLowerCase(), "<font color =\"#35a6fb\">" + mstrKeyWord.toLowerCase() + "</font>");
                strTitle = strTitle.replace(mstrKeyWord.toUpperCase(), "<font color =\"#35a6fb\">" + mstrKeyWord.toUpperCase() + "</font>");
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
                intent.putExtra(NewsFeedFgt.KEY_URL, feed.getSourceUrl());
                mContext.startActivity(intent);
                MobclickAgent.onEvent(mContext, "bainews_view_head_news");
            }
        });
    }

    class BaseHolder {
        TextViewExtend tvSource;
        TextViewExtend tvComment;
        TextView tvTitle;
        LinearLayout llSourceContent;
        RelativeLayout rlNewsContent;
        ImageView ivSource1;
        ImageView ivSource2;
        ImageView ivSource3;
        ImageView ivVerticalTopLine1;
        ImageView ivVerticalTopLine2;
        ImageView ivVerticalTopLine3;
        ImageView ivVerticalLine1;
        ImageView ivVerticalLine2;
        ImageView ivVerticalLine3;
        //        TextView tvBottomLine1;
//        TextView tvBottomLine2;
//        TextView tvBottomLine3;
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
        TextViewExtend tv_title;
    }

    class ViewHolder3 extends BaseHolder {
        SimpleDraweeView ivCard1;
        SimpleDraweeView ivCard2;
        SimpleDraweeView ivCard3;
        LinearLayout llImageList;
    }
}