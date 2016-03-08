package com.news.yazhidao.adapter;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.PointF;
import android.net.Uri;
import android.text.Html;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.LinearInterpolator;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.backends.pipeline.PipelineDraweeController;
import com.facebook.drawee.view.SimpleDraweeView;
import com.facebook.imagepipeline.common.ResizeOptions;
import com.facebook.imagepipeline.request.ImageRequest;
import com.facebook.imagepipeline.request.ImageRequestBuilder;
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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;


public class NewsFeedAdapter extends BaseAdapter {

    private final NewsFeedFgt mNewsFeedFgt;
    private ArrayList<NewsFeed> mArrNewsFeed;
    private String mstrKeyWord;
    private int mScreenHeight;
    private int mScreenWidth;
    private Context mContext;
    public static String KEY_URL = "key_url";
    public static String KEY_NEWS_ID = "key_news_id";
    public static int REQUEST_CODE = 10002;

    public NewsFeedAdapter(Context context,NewsFeedFgt newsFeedFgt) {
        mContext = context;
        mScreenWidth = DeviceInfoUtil.getScreenWidth();
        mScreenHeight = DeviceInfoUtil.getScreenHeight();
        this.mNewsFeedFgt = newsFeedFgt;
    }
    public NewsFeedAdapter(Context context) {
        this(context,null);
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
//                holder.valueAnimator = ValueAnimator.ofFloat(0, 360);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            if ("adcoco".equals(platform)) {
                ArrayList localArrayList2 = mArrNewsFeed;
                AdcocoUtil.ad(position, convertView, localArrayList2);
            }
            RelativeLayout.LayoutParams lpTitle = (RelativeLayout.LayoutParams) holder.tvTitle.getLayoutParams();
            if ("no_pic".equals(strType)) {
                lpTitle.topMargin = DensityUtil.dip2px(mContext, 8);
                lpTitle.bottomMargin = DensityUtil.dip2px(mContext, 8);
                lpTitle.leftMargin = DensityUtil.dip2px(mContext, 12);
                holder.tvSource.setPadding(0, 0, 0, 0);
                holder.tvComment.setPadding(0, 0, 0, 0);
            } else {
                lpTitle.topMargin = DensityUtil.dip2px(mContext, 12);
                lpTitle.bottomMargin = DensityUtil.dip2px(mContext, 0);
                lpTitle.leftMargin = DensityUtil.dip2px(mContext, 8);
                holder.tvSource.setPadding(0, 0, 0, DensityUtil.dip2px(mContext, 2));
                holder.tvComment.setPadding(0, 0, 0, DensityUtil.dip2px(mContext, 2));
            }
            holder.tvTitle.setLayoutParams(lpTitle);
            ArrayList<String> strArrImgUrl = feed.getImgUrls();
            String strImg = null;
            if ("one_pic".equals(strType) || "two_pic".equals(strType)) {
                strImg = strArrImgUrl.get(0);
            }
            if (strImg != null && !"".equals(strImg)) {
                ((View)holder.ivTitleImg.getParent()).setVisibility(View.VISIBLE);
                holder.ivTitleImg.setImageURI(Uri.parse(strImg));
                holder.ivTitleImg.getHierarchy().setActualImageFocusPoint(new PointF(0.5F, 0.4F));
            } else {
                ((View)holder.ivTitleImg.getParent()).setVisibility(View.GONE);
            }
            String strTitle = feed.getTitle();
            setTitleTextBySpannable(holder.tvTitle, strTitle,feed.isRead());
            setViewText(holder.tvSource, feed.getSourceSiteName());
            if (feed.getUpdateTime() != null)
                setNewsTime(holder.tvComment, feed.getUpdateTime());
            setNewsContentClick(holder.rlNewsContent, feed);
//            if (holder.valueAnimator.isRunning()) {
//                holder.tvTitle.setTextColor(mContext.getResources().getColor(R.color.color2));
//                if (holder.ivTitleImg != null) {
//                    holder.ivTitleImg.setScaleX(1.0f);
//                    holder.ivTitleImg.setScaleY(1.0f);
//                    holder.valueAnimator.end();
//                }
//            }
//            setContentAnim(holder.rlNewsContent, holder.ivTitleImg, holder.tvTitle,holder.valueAnimator);
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
         else if ("big_pic0".equals(strType)) {
            //是网易的大图新闻
            boolean isNeteaseBigPic = feed.getSourceSiteName().startsWith("网易");
//            if (isNeteaseBigPic) {
//                ViewHolder2 holder2;
//                if (convertView == null || convertView.getTag().getClass() != ViewHolder2.class) {
//                    holder2 = new ViewHolder2();
//                    convertView = View.inflate(mContext, R.layout.ll_news_item_top, null);
//                    holder2.rl_item_content = (RelativeLayout) convertView.findViewById(R.id.rl_item_content);
//                    holder2.iv_title_img = (SimpleDraweeView) convertView.findViewById(R.id.iv_title_img);
//                    holder2.tv_title = (TextViewExtend) convertView.findViewById(R.id.tv_title);
//                    RelativeLayout.LayoutParams lpImg = (RelativeLayout.LayoutParams) holder2.iv_title_img.getLayoutParams();
//                    lpImg.width = mScreenWidth;
//                    lpImg.height = (int) (mScreenWidth * (182 / 320.0f));
//                    holder2.iv_title_img.setLayoutParams(lpImg);
//                    convertView.setTag(holder2);
//                } else {
//                    holder2 = (ViewHolder2) convertView.getTag();
//                }
//                String title_news = feed.getTitle();
//                String title = "";
//                if (title_news != null && title_news.length() > 0) {
//                    title = TextUtil.getNewsTitle(title_news);
//                }
//                if (title != null && !"".equals(title)) {
//                    holder2.tv_title.setText(title);
//                } else {
//                    holder2.tv_title.setText(title_news);
//                }
//                ArrayList<String> strArrImgUrl = feed.getImgUrls();
//                String strImgUrl = strArrImgUrl.get(0);
//                if (strImgUrl != null && !"".equals(strImgUrl)) {
//                    holder2.iv_title_img.setImageURI(Uri.parse(strImgUrl));
//                    holder2.iv_title_img.getHierarchy().setActualImageFocusPoint(new PointF(0.5f, 0.4f));
//                }
//                setNewsContentClick(holder2.rl_item_content, feed);
//            } else {

                if (isNeteaseBigPic){
                ViewHolder4 holder4;
                if (convertView == null || convertView.getTag().getClass() != ViewHolder4.class) {
                    holder4 = new ViewHolder4();
                    convertView = View.inflate(mContext, R.layout.ll_news_big_pic2, null);
                    holder4.mBigPicTitle = (TextViewExtend) convertView.findViewById(R.id.mBigPicTitle);
                    holder4.mBigPicImg = (SimpleDraweeView) convertView.findViewById(R.id.mBigPicImg);
                    holder4.mBigPicSource = (TextViewExtend) convertView.findViewById(R.id.mBigPicSource);
                    holder4.mBigPicTime = (TextViewExtend) convertView.findViewById(R.id.mBigPicTime);
                    holder4.mBigPicWrapper = (RelativeLayout) convertView.findViewById(R.id.mBigPicWrapper);
                    convertView.setTag(holder4);
                } else {
                    holder4 = (ViewHolder4) convertView.getTag();
                }
                holder4.mBigPicTitle.setText(feed.getTitle());
                ArrayList<String> strArrImgUrl = feed.getImgUrls();
                String strImgUrl = strArrImgUrl.get(0);
                if (strImgUrl != null && !"".equals(strImgUrl)) {
                    holder4.mBigPicImg.getHierarchy().setActualImageFocusPoint(new PointF(0.5f, 0.4f));
                    int width = DeviceInfoUtil.getScreenWidth(mContext), height = (int) (DeviceInfoUtil.getScreenWidth(mContext) * 9.0f / 16.0f);
                    ImageRequest request = ImageRequestBuilder.newBuilderWithSource(Uri.parse(strImgUrl))
                            .setResizeOptions(new ResizeOptions(width, height))
                            .build();
                    PipelineDraweeController controller = (PipelineDraweeController) Fresco.newDraweeControllerBuilder()
                            .setOldController(holder4.mBigPicImg.getController())
                            .setImageRequest(request)
                            .build();
                    holder4.mBigPicImg.setController(controller);

                }
                setViewText(holder4.mBigPicSource,feed.getSourceSiteName());
                if (feed.getUpdateTime() != null){
                    setNewsTime(holder4.mBigPicTime, feed.getUpdateTime());
                }
                setNewsContentClick(holder4.mBigPicWrapper, feed);

                }
//            }
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
                setCardMargin(holder3.ivCard1, 12, 6, 3);
                setCardMargin(holder3.ivCard2, 6, 6, 3);
                setCardMargin(holder3.ivCard3, 6, 12, 3);
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

//            }
            String strTitle = feed.getTitle();
            setTitleTextBySpannable(holder3.tvTitle, strTitle, feed.isRead());
            setViewText(holder3.tvSource, feed.getSourceSiteName());
            if (feed.getUpdateTime() != null)
                setNewsTime(holder3.tvComment, feed.getUpdateTime());
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
                        finalText = source.getUser() + ":" + source_title;
                        tvSource.setText(Html.fromHtml(finalText));
                    } else {
//                        source_name_font = "<font color =\"#7d7d7d\">" + "<big>" + source_name + "</big>" + "</font>" + ": ";
                        finalText = source_name + ":" + source_title;
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
//        ivVerticalLine.post(new Runnable() {
//            public void run() {
//                RelativeLayout.LayoutParams lpVerticalLine = (RelativeLayout.LayoutParams) ivVerticalLine.getLayoutParams();
//                lpVerticalLine.height = (int) ivSource.getY();
//                ivVerticalLine.setLayoutParams(lpVerticalLine);
//            }
//        });
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

    private void setNewsTime(TextViewExtend tvComment, String updateTime) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            Date date = dateFormat.parse(updateTime);
            long between = System.currentTimeMillis() - date.getTime();
            if (between >= (24 * 3600000)) {
                tvComment.setText("23小时前");
            } else if (between < (24 * 3600000) && between >= (1 * 3600000)) {
                tvComment.setText(between / 3600000 + "小时前");
            } else {
                if (between / 3600000 / 60 == 0) {
                    tvComment.setText("刚刚");
                } else {
                    tvComment.setText(between / 3600000 / 60 + "分钟前");
                }
            }
        } catch (ParseException e) {
            tvComment.setText("一天前");
            e.printStackTrace();
        }

    }

    private void setTitleTextBySpannable(TextView tvTitle, String strTitle, boolean isRead) {
        if (strTitle != null && !"".equals(strTitle)) {
            if (mstrKeyWord != null && !"".equals(mstrKeyWord)) {
                strTitle = strTitle.replace(mstrKeyWord.toLowerCase(), "<font color =\"#35a6fb\">" + mstrKeyWord.toLowerCase() + "</font>");
                strTitle = strTitle.replace(mstrKeyWord.toUpperCase(), "<font color =\"#35a6fb\">" + mstrKeyWord.toUpperCase() + "</font>");
                tvTitle.setText(Html.fromHtml(strTitle), TextView.BufferType.SPANNABLE);
            } else {
                tvTitle.setText(strTitle);
            }
            if (isRead){
                tvTitle.setTextColor(mContext.getResources().getColor(R.color.title_user_had_read));
            }else {
                tvTitle.setTextColor(mContext.getResources().getColor(R.color.color2));
            }
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
                intent.putExtra(NewsFeedFgt.KEY_CHANNEL_ID, feed.getChannelId());
                intent.putExtra(NewsFeedFgt.KEY_NEWS_IMG_URL,TextUtil.isListEmpty(feed.getImgUrls())?null:feed.getImgUrls().get(0) );
                if (mNewsFeedFgt != null){
                    mNewsFeedFgt.startActivityForResult(intent,REQUEST_CODE);
                }else {
                    ((Activity)mContext).startActivityForResult(intent,REQUEST_CODE);
                }
                MobclickAgent.onEvent(mContext, "bainews_view_head_news");
                MobclickAgent.onEvent(mContext, "user_read_detail");
            }
        });
    }

    private void setContentAnim(RelativeLayout rlNewsContent, final SimpleDraweeView imageView, final TextView tvTitle, final ValueAnimator valueAnimator) {

        rlNewsContent.setOnTouchListener(new View.OnTouchListener() {
            float event1Y = 0;
            float event2Y = 0;

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        event1Y = event.getY(0);
                        if (imageView != null) {
                            imageView.setScaleX(2.0f);
                            imageView.setScaleY(2.0f);
                            final float xx = imageView.getX();
                            final float yy = imageView.getY();
                            valueAnimator.setInterpolator(new LinearInterpolator());
                            valueAnimator.setDuration(200000);
                            valueAnimator.start();
                            valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                                @Override
                                public void onAnimationUpdate(ValueAnimator animation) {
                                    int r = DensityUtil.dip2px(mContext, 10);
                                    float degree = animation.getAnimatedFraction() * 360;
                                    imageView.setX((float) (-r / 4 + r * Math.cos(degree)));
                                    imageView.setY((float) (r * Math.sin(degree)));
                                }
                            });
                            valueAnimator.addListener(new AnimatorListenerAdapter() {
                                @Override
                                public void onAnimationEnd(Animator animation) {
                                    imageView.setX(xx);
                                    imageView.setY(yy);
                                }
                            });
                        }
                        tvTitle.setTextColor(mContext.getResources().getColor(R.color.color8));
                        break;
                    case MotionEvent.ACTION_MOVE:
                        event2Y = event.getY(0);
                        tvTitle.setTextColor(mContext.getResources().getColor(R.color.color2));
                        if (Math.abs(event2Y - event1Y) > 0 && imageView != null) {
                            imageView.setScaleX(1.0f);
                            imageView.setScaleY(1.0f);
                            valueAnimator.end();
                        }
                        break;
                    case MotionEvent.ACTION_UP:
                        tvTitle.setTextColor(mContext.getResources().getColor(R.color.color2));
                        if (imageView != null) {
                            imageView.setScaleX(1.0f);
                            imageView.setScaleY(1.0f);
                            valueAnimator.end();
                        }
                        break;
                }
                return false;
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
        ValueAnimator valueAnimator;
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

    class ViewHolder4 extends BaseHolder {
        TextViewExtend mBigPicTitle;
        SimpleDraweeView mBigPicImg;
        TextViewExtend mBigPicSource;
        TextViewExtend mBigPicTime;
        RelativeLayout mBigPicWrapper;
    }
}