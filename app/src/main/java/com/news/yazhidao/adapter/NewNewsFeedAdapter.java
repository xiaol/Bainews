package com.news.yazhidao.adapter;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.text.Html;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;
import com.bumptech.glide.Glide;
import com.news.yazhidao.R;
import com.news.yazhidao.adapter.abslistview.CommonViewHolder;
import com.news.yazhidao.adapter.abslistview.MultiItemCommonAdapter;
import com.news.yazhidao.adapter.abslistview.MultiItemTypeSupport;
import com.news.yazhidao.application.YaZhiDaoApplication;
import com.news.yazhidao.common.CommonConstant;
import com.news.yazhidao.database.ChannelItemDao;
import com.news.yazhidao.database.NewsFeedDao;
import com.news.yazhidao.database.ReleaseSourceItemDao;
import com.news.yazhidao.entity.AttentionListEntity;
import com.news.yazhidao.entity.ChannelItem;
import com.news.yazhidao.entity.NewsFeed;
import com.news.yazhidao.entity.ReleaseSourceItem;
import com.news.yazhidao.pages.AttentionActivity;
import com.news.yazhidao.pages.MainAty;
import com.news.yazhidao.pages.NewsDetailAty2;
import com.news.yazhidao.pages.NewsDetailVideoAty;
import com.news.yazhidao.pages.NewsDetailWebviewAty;
import com.news.yazhidao.pages.NewsFeedFgt;
import com.news.yazhidao.pages.NewsTopicAty;
import com.news.yazhidao.pages.SubscribeListActivity;
import com.news.yazhidao.utils.DensityUtil;
import com.news.yazhidao.utils.DeviceInfoUtil;
import com.news.yazhidao.utils.FileUtils;
import com.news.yazhidao.utils.TextUtil;
import com.news.yazhidao.utils.ZipperUtil;
import com.news.yazhidao.utils.manager.SharedPreManager;
import com.news.yazhidao.widget.TextViewExtend;
import com.umeng.analytics.MobclickAgent;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Random;

import static com.news.yazhidao.R.id.layout_source;


public class NewNewsFeedAdapter extends MultiItemCommonAdapter<NewsFeed> {

    private final NewsFeedFgt mNewsFeedFgt;
    private String mstrKeyWord;
    private int mScreenHeight;
    private int mScreenWidth;
    private Context mContext;
    public static String KEY_URL = "key_url";
    public static String KEY_NEWS_ID = "key_news_id";
    public static int REQUEST_CODE = 10002;
    private SharedPreferences mSharedPreferences;
    private NewsFeedDao mNewsFeedDao;
    private final int DELETEANIMTIME = 500;
    private File mNewsFile;
    private int mTitleViewWidth;
    private int mCardWidth, mCardHeight;
    private boolean isFavorite;
    private boolean isNeedShowDisLikeIcon = true;
    private boolean isAttention;
    boolean isCkeckVisity;
    private HashMap<String, Integer> mReleaseSourceItem;
    private ReleaseSourceItemDao mReleaseSourceItemDao;
    private String[] mColorArr;
    private TypedArray mTypedArray;
    private int iRandom;

    public NewNewsFeedAdapter(Context context, NewsFeedFgt newsFeedFgt, ArrayList<NewsFeed> datas) {
        super(context, datas, new MultiItemTypeSupport<NewsFeed>() {
            @Override
            public int getLayoutId(int position, NewsFeed newsFeed) {
                switch (newsFeed.getStyle()) {
                    case 0:
                        return R.layout.ll_news_item_no_pic;
                    case 1:
                    case 2:
                        return R.layout.ll_news_item_one_pic;
                    case 3:
                        return R.layout.ll_news_card;
//                        return R.layout.ll_news_big_pic2;
                    case 900:
                        return R.layout.ll_news_item_time_line;
                    case 4://奇点号Item
                        return R.layout.ll_news_search_item;
                    case 5:
                        return R.layout.ll_news_item_topic;

                    //视频播放列表，可以在列表播放
                    case 6:
                        return R.layout.ll_video_item_player;
                    //item视频布局不能在列表播放，可以在其他列表出现
                    case 7:
                        return R.layout.ll_video_item_big;
                    case 8:
                        return R.layout.ll_video_item_small;

                    case 11://大图Item
                    case 12:
                    case 13:
                        return R.layout.ll_news_big_pic2;
                    default:
                        return R.layout.ll_news_item_empty;
                }
            }

            @Override
            public int getViewTypeCount() {
                return 11;
            }

            @Override
            public int getItemViewType(int position, NewsFeed newsFeed) {
                switch (newsFeed.getStyle()) {
                    case 0:
                        return NewsFeed.NO_PIC;
                    case 1:
                    case 2:
                        return NewsFeed.ONE_AND_TWO_PIC;
                    case 3:
                        return NewsFeed.THREE_PIC;
//                        return NewsFeed.BIG_PIC;
                    case 900:
                        return NewsFeed.TIME_LINE;
                    case 4://奇点号Item
                        return NewsFeed.SERRCH_ITEM;
                    case 5:
                        return NewsFeed.TOPIC;
                    case 6:
                        return NewsFeed.VIDEO_PLAYER;
                    case 7:
                        return NewsFeed.VIDEO_BIG;
                    case 8:
                        return NewsFeed.VIDEO_SMALL;
                    case 11://大图Item
                    case 12:
                    case 13:
                        return NewsFeed.BIG_PIC;
                    default:
                        return NewsFeed.EMPTY;
                }
            }
        });
        mContext = context;
        mScreenWidth = DeviceInfoUtil.getScreenWidth();
        mScreenHeight = DeviceInfoUtil.getScreenHeight();
        this.mNewsFeedFgt = newsFeedFgt;
        mSharedPreferences = mContext.getSharedPreferences("showflag", 0);
        mTypedArray = mContext.getResources().obtainTypedArray(R.array.resource_list_image);
        mNewsFeedDao = new NewsFeedDao(mContext);
        mNewsFile = ZipperUtil.getSaveFontPath(context);
        mTitleViewWidth = mScreenWidth - DensityUtil.dip2px(mContext, 147);
        mCardWidth = (int) ((mScreenWidth - DensityUtil.dip2px(mContext, 32)) / 3.0f);
        mCardHeight = (int) (mCardWidth * 213 / 326.0f);
        Random random = new Random();
        iRandom = random.nextInt(50);
    }


    public void setSearchKeyWord(String pKeyWord) {
        mstrKeyWord = pKeyWord;
        mDatas = null;
    }

    public void isFavoriteList() {
        isFavorite = true;
        isNeedShowDisLikeIcon = false;
    }

    public void isAttention() {
        isAttention = true;
        isNeedShowDisLikeIcon = false;
    }


    @Override
    public void convert(final CommonViewHolder holder, final NewsFeed feed, int position) {
        //广告
        upLoadAd(feed);
        switch (holder.getLayoutId()) {
            case R.layout.ll_news_item_no_pic:
            case R.layout.ll_news_item_one_pic:
            case R.layout.ll_news_card:
                if (isCkeckVisity) {
                    holder.getView(R.id.checkFavoriteDelete_image).setVisibility(View.VISIBLE);
                } else {
                    holder.getView(R.id.checkFavoriteDelete_image).setVisibility(View.GONE);
                    holder.getImageView(R.id.checkFavoriteDelete_image).setImageResource(R.drawable.favorite_uncheck);

                }
                if (feed.isFavorite()) {
                    holder.getImageView(R.id.checkFavoriteDelete_image).setImageResource(R.drawable.favorite_check);
                } else {
                    holder.getImageView(R.id.checkFavoriteDelete_image).setImageResource(R.drawable.favorite_uncheck);
                }
                if (isFavorite) {
                    holder.getView(R.id.delete_imageView).setVisibility(View.GONE);
                    holder.getView(R.id.comment_num_textView).setVisibility(View.GONE);
                    holder.getView(R.id.line_bottom_imageView).setBackgroundColor(mContext.getResources().getColor(R.color.new_color5));
                    if (getCount() == position + 1) {//去掉最后一条的线
                        holder.getView(R.id.line_bottom_imageView).setVisibility(View.INVISIBLE);
                    }
                    ClickDeleteFavorite((ImageView) holder.getView(R.id.checkFavoriteDelete_image), feed);
                }
                break;
        }
        switch (holder.getLayoutId()) {
            case R.layout.ll_news_item_empty:
                holder.getView(R.id.news_content_relativeLayout).setVisibility(View.GONE);
                break;
            case R.layout.ll_news_item_no_pic:
                if (isFavorite) {
                    setTitleTextBySpannable((TextView) holder.getView(R.id.title_textView), feed.getTitle(), false, feed.getRtype());
                } else {
                    setTitleTextBySpannable((TextView) holder.getView(R.id.title_textView), feed.getTitle(), feed.isRead(), feed.getRtype());
                }
                setFocusBgColor((TextViewExtend) holder.getView(R.id.news_source_TextView), feed.getPname(), (TextViewExtend) holder.getView(R.id.comment_num_textView), (ImageView) holder.getView(R.id.delete_imageView));
                setCommentViewText((TextViewExtend) holder.getView(R.id.comment_num_textView), feed.getComment() + "");
//                if (feed.getPtime() != null)
//                    setNewsTime((TextViewExtend) holder.getView(R.id.comment_textView), feed.getPtime());
                setNewsContentClick((RelativeLayout) holder.getView(R.id.news_content_relativeLayout), feed);
                setDeleteClick((ImageView) holder.getView(R.id.delete_imageView), feed, holder.getConvertView());
                newsTag((TextViewExtend) holder.getView(R.id.type_textView), feed.getRtype());
                holder.getView(R.id.delete_imageView).setVisibility(isNeedShowDisLikeIcon ? View.VISIBLE : View.INVISIBLE);
                if (isAttention) {
                    holder.getView(R.id.news_source_TextView).setVisibility(View.GONE);
                }
                if (feed.getRtype() == 3) {
                    holder.getView(R.id.layout_source).setVisibility(View.GONE);
                } else {
                    holder.getView(R.id.layout_source).setVisibility(View.VISIBLE);
                    setSourceViewText((TextViewExtend) holder.getView(R.id.news_source_TextView), feed.getPname());
                    setSourceImage((ImageView) holder.getView(R.id.news_source_ImageView), feed.getIcon(), position);
                }
                setSourceOnClick(holder.getView(layout_source), feed);
                break;
            case R.layout.ll_news_item_one_pic:
                ImageView ivCard = holder.getView(R.id.title_img_View);
                RelativeLayout.LayoutParams lpCard = (RelativeLayout.LayoutParams) ivCard.getLayoutParams();
                lpCard.width = mCardWidth;
                lpCard.height = mCardHeight;
                ivCard.setLayoutParams(lpCard);
                holder.setIsShowImagesSimpleDraweeViewURI(R.id.title_img_View, feed.getImgs().get(0), mCardWidth, mCardHeight, feed.getRtype());
                final String strTitle = feed.getTitle();
                if (isFavorite) {
                    setTitleTextBySpannable((TextView) holder.getView(R.id.title_textView), feed.getTitle(), false, feed.getRtype());
                } else {
                    setTitleTextBySpannable((TextView) holder.getView(R.id.title_textView), feed.getTitle(), feed.isRead(), feed.getRtype());
                }
                final TextView tvTitle = holder.getView(R.id.title_textView);
                final LinearLayout llSourceOnePic = holder.getView(R.id.source_content_linearLayout);
                final ImageView ivBottomLine = holder.getView(R.id.line_bottom_imageView);
                RelativeLayout.LayoutParams lpSourceContent = (RelativeLayout.LayoutParams) llSourceOnePic.getLayoutParams();
                RelativeLayout.LayoutParams titleLp = (RelativeLayout.LayoutParams) tvTitle.getLayoutParams();
                RelativeLayout.LayoutParams lpBottomLine = (RelativeLayout.LayoutParams) ivBottomLine.getLayoutParams();
                float textRealWidth = tvTitle.getPaint().measureText(strTitle);
//                if (textRealWidth >= 2 * mTitleViewWidth - 5) {
//                    titleLp.setMargins(DensityUtil.dip2px(mContext, 15), DensityUtil.dip2px(mContext, 10), DensityUtil.dip2px(mContext, 15), 0);
//                    lpSourceContent.rightMargin = DensityUtil.dip2px(mContext, 15);
//                    lpBottomLine.addRule(RelativeLayout.BELOW, R.id.source_content_linearLayout);
//                } else if (textRealWidth <= mTitleViewWidth) {
//                    titleLp.setMargins(DensityUtil.dip2px(mContext, 15), DensityUtil.dip2px(mContext, 21), DensityUtil.dip2px(mContext, 15), 0);
//                    lpSourceContent.rightMargin = mCardWidth + DensityUtil.dip2px(mContext, 25);
//                    lpBottomLine.addRule(RelativeLayout.BELOW, R.id.title_img_View);
//                } else {
//                    titleLp.setMargins(DensityUtil.dip2px(mContext, 15), DensityUtil.dip2px(mContext, 10), DensityUtil.dip2px(mContext, 15), 0);
//                    lpSourceContent.rightMargin = mCardWidth + DensityUtil.dip2px(mContext, 25);
//                    lpBottomLine.addRule(RelativeLayout.BELOW, R.id.title_img_View);
//                }
//                llSourceContent.setLayoutParams(lpSourceContent);
//                ivBottomLine.setLayoutParams(lpBottomLine);
                tvTitle.post(new Runnable() {
                    @Override
                    public void run() {
                        RelativeLayout.LayoutParams lpSourceContent = (RelativeLayout.LayoutParams) llSourceOnePic.getLayoutParams();
                        RelativeLayout.LayoutParams titleLp = (RelativeLayout.LayoutParams) tvTitle.getLayoutParams();
                        RelativeLayout.LayoutParams lpBottomLine = (RelativeLayout.LayoutParams) ivBottomLine.getLayoutParams();
                        int lineCount = tvTitle.getLineCount();
                        if (lineCount >= 3) {
//                            titleLp.setMargins(0, DensityUtil.dip2px(mContext, 8), DensityUtil.dip2px(mContext, 30), DensityUtil.dip2px(mContext, 0));
                            lpSourceContent.rightMargin = DensityUtil.dip2px(mContext, 3);
                            lpSourceContent.addRule(RelativeLayout.BELOW, R.id.title_img_View);
                            lpBottomLine.topMargin = DensityUtil.dip2px(mContext, 28);
                        }
//                        else if (lineCount <= 1) {
//                            titleLp.setMargins(0, DensityUtil.dip2px(mContext, 21), DensityUtil.dip2px(mContext, 15), 0);
//                            lpSourceContent.rightMargin = mCardWidth + DensityUtil.dip2px(mContext, 25);
//                            lpBottomLine.addRule(RelativeLayout.BELOW, R.id.title_img_View);
//                        }
                        else {
//                            titleLp.setMargins(0, DensityUtil.dip2px(mContext, 8), DensityUtil.dip2px(mContext, 30), DensityUtil.dip2px(mContext, 0));
                            lpSourceContent.rightMargin = mCardWidth + DensityUtil.dip2px(mContext, 25);
                            lpSourceContent.addRule(RelativeLayout.BELOW, R.id.title_textView);
                            lpBottomLine.topMargin = DensityUtil.dip2px(mContext, 8);
                        }
                        llSourceOnePic.setLayoutParams(lpSourceContent);
                        ivBottomLine.setLayoutParams(lpBottomLine);
                    }
                });
                setFocusBgColor((TextViewExtend) holder.getView(R.id.news_source_TextView), feed.getPname(), (TextViewExtend) holder.getView(R.id.comment_num_textView), (ImageView) holder.getView(R.id.delete_imageView));
                setCommentViewText((TextViewExtend) holder.getView(R.id.comment_num_textView), feed.getComment() + "");
                newsTag((TextViewExtend) holder.getView(R.id.type_textView), feed.getRtype());
                if (feed.getPtime() != null) {
                    setNewsTime((TextViewExtend) holder.getView(R.id.comment_textView), feed.getPtime());
                }
                setNewsContentClick((RelativeLayout) holder.getView(R.id.news_content_relativeLayout), feed);
                setDeleteClick((ImageView) holder.getView(R.id.delete_imageView), feed, holder.getConvertView());
                holder.getView(R.id.delete_imageView).setVisibility(isNeedShowDisLikeIcon ? View.VISIBLE : View.INVISIBLE);
                if (isAttention) {
                    holder.getView(R.id.news_source_TextView).setVisibility(View.GONE);
                }
                if (feed.getRtype() == 3) {
                    holder.getView(R.id.layout_source).setVisibility(View.GONE);
                } else {
                    holder.getView(R.id.layout_source).setVisibility(View.VISIBLE);
                    setSourceViewText((TextViewExtend) holder.getView(R.id.news_source_TextView), feed.getPname());
                    setSourceImage((ImageView) holder.getView(R.id.news_source_ImageView), feed.getIcon(), position);
                }
                setSourceOnClick(holder.getView(layout_source), feed);
                break;
            case R.layout.ll_news_card:
                ArrayList<String> strArrImgUrl = feed.getImgs();
                holder.setIsShowImagesSimpleDraweeViewURI(R.id.image_card1, strArrImgUrl.get(0), mCardWidth, mCardHeight, feed.getRtype());
                holder.setIsShowImagesSimpleDraweeViewURI(R.id.image_card2, strArrImgUrl.get(1), mCardWidth, mCardHeight, feed.getRtype());
                holder.setIsShowImagesSimpleDraweeViewURI(R.id.image_card3, strArrImgUrl.get(2), mCardWidth, mCardHeight, feed.getRtype());
                setCardMargin((ImageView) holder.getView(R.id.image_card1), 15, 1, 3);
                setCardMargin((ImageView) holder.getView(R.id.image_card2), 1, 1, 3);
                setCardMargin((ImageView) holder.getView(R.id.image_card3), 1, 15, 3);
                if (isFavorite) {
                    setTitleTextBySpannable((TextView) holder.getView(R.id.title_textView), feed.getTitle(), false, feed.getRtype());
                } else {
                    setTitleTextBySpannable((TextView) holder.getView(R.id.title_textView), feed.getTitle(), feed.isRead(), feed.getRtype());
                }
                setFocusBgColor((TextViewExtend) holder.getView(R.id.news_source_TextView), feed.getPname(), (TextViewExtend) holder.getView(R.id.comment_num_textView), (ImageView) holder.getView(R.id.delete_imageView));
                setCommentViewText((TextViewExtend) holder.getView(R.id.comment_num_textView), feed.getComment() + "");
                newsTag((TextViewExtend) holder.getView(R.id.type_textView), feed.getRtype());
//                if (feed.getPtime() != null)
//                    setNewsTime((TextViewExtend) holder.getView(R.id.comment_textView), feed.getPtime());
                setNewsContentClick((RelativeLayout) holder.getView(R.id.news_content_relativeLayout), feed);
                setDeleteClick((ImageView) holder.getView(R.id.delete_imageView), feed, holder.getConvertView());
                holder.getView(R.id.delete_imageView).setVisibility(isNeedShowDisLikeIcon ? View.VISIBLE : View.INVISIBLE);
                if (isAttention) {
                    holder.getView(R.id.news_source_TextView).setVisibility(View.GONE);
                }
                if (feed.getRtype() == 3) {
                    holder.getView(R.id.layout_source).setVisibility(View.GONE);
                } else {
                    holder.getView(R.id.layout_source).setVisibility(View.VISIBLE);
                    setSourceViewText((TextViewExtend) holder.getView(R.id.news_source_TextView), feed.getPname());
                    setSourceImage((ImageView) holder.getView(R.id.news_source_ImageView), feed.getIcon(), position);
                }
                setSourceOnClick(holder.getView(layout_source), feed);
                break;
            case R.layout.ll_news_big_pic2:
                ArrayList<String> strArrBigImgUrl = feed.getImgs();
                int width = mScreenWidth - DensityUtil.dip2px(mContext, 30);
                int num = feed.getStyle() - 11;
                ImageView ivBig = holder.getView(R.id.title_img_View);
                RelativeLayout.LayoutParams lpBig = (RelativeLayout.LayoutParams) ivBig.getLayoutParams();
                lpBig.width = width;
                lpBig.height = (int) (width * 185 / 330.0f);
                ivBig.setLayoutParams(lpBig);
                holder.setIsShowImagesSimpleDraweeViewURI(R.id.title_img_View, strArrBigImgUrl.get(num), width, (int) (width * 9 / 16.0f), feed.getRtype());
                if (isFavorite) {
                    setTitleTextBySpannable((TextView) holder.getView(R.id.title_textView), feed.getTitle(), false, feed.getRtype());
                } else {
                    setTitleTextBySpannable((TextView) holder.getView(R.id.title_textView), feed.getTitle(), feed.isRead(), feed.getRtype());
                }
                setFocusBgColor((TextViewExtend) holder.getView(R.id.news_source_TextView), feed.getPname(), (TextViewExtend) holder.getView(R.id.comment_num_textView), (ImageView) holder.getView(R.id.delete_imageView));
                setCommentViewText((TextViewExtend) holder.getView(R.id.comment_num_textView), feed.getComment() + "");
                newsTag((TextViewExtend) holder.getView(R.id.type_textView), feed.getRtype());
//                if (feed.getPtime() != null)
//                    setNewsTime((TextViewExtend) holder.getView(R.id.comment_textView), feed.getPtime());
                setNewsContentClick((RelativeLayout) holder.getView(R.id.news_content_relativeLayout), feed);
                setDeleteClick((ImageView) holder.getView(R.id.delete_imageView), feed, holder.getConvertView());
                holder.getView(R.id.delete_imageView).setVisibility(isNeedShowDisLikeIcon ? View.VISIBLE : View.INVISIBLE);
                if (feed.getRtype() == 3) {
                    holder.getView(R.id.layout_source).setVisibility(View.GONE);
                } else {
                    holder.getView(R.id.layout_source).setVisibility(View.VISIBLE);
                    setSourceViewText((TextViewExtend) holder.getView(R.id.news_source_TextView), feed.getPname());
                    setSourceImage((ImageView) holder.getView(R.id.news_source_ImageView), feed.getIcon(), position);
                }
                setSourceOnClick(holder.getView(layout_source), feed);
                break;
            case R.layout.ll_news_item_topic:
                ImageView ivTopic = holder.getView(R.id.title_img_View);
                int ivWidth = mScreenWidth - DensityUtil.dip2px(mContext, 30);
                RelativeLayout.LayoutParams lpTopic = (RelativeLayout.LayoutParams) ivTopic.getLayoutParams();
                lpTopic.width = ivWidth;
                lpTopic.height = (int) (ivWidth * 80 / 330.0f);
                ivTopic.setLayoutParams(lpTopic);
                holder.setIsShowImagesSimpleDraweeViewURI(R.id.title_img_View, feed.getImgs().get(0), 0, 0, feed.getRtype());
                if (isFavorite) {
                    setTitleTextBySpannable((TextView) holder.getView(R.id.title_textView), feed.getTitle(), false, feed.getRtype());
                } else {
                    setTitleTextBySpannable((TextView) holder.getView(R.id.title_textView), feed.getTitle(), feed.isRead(), feed.getRtype());
                }
                setCommentViewText((TextViewExtend) holder.getView(R.id.comment_num_textView), feed.getComment() + "");
                setNewsContentClick((RelativeLayout) holder.getView(R.id.news_content_relativeLayout), feed);
                setDeleteClick((ImageView) holder.getView(R.id.delete_imageView), feed, holder.getConvertView());
                if (position == 0) {
                    holder.getView(R.id.top_image).setVisibility(View.VISIBLE);
                } else {
                    holder.getView(R.id.top_image).setVisibility(View.GONE);
                }
                newsTag((TextViewExtend) holder.getView(R.id.type_textView), feed.getRtype());
                break;
            case R.layout.ll_news_item_time_line:
                holder.getView(R.id.news_content_relativeLayout).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mNewsFeedFgt.refreshData();
                    }
                });
                break;
            case R.layout.ll_news_search_item://奇点号Item
                final ArrayList<AttentionListEntity> attentionListEntities = feed.getAttentionListEntities();
                int size = attentionListEntities.size();
                if (size == 1) {
                    holder.setSimpleDraweeViewURI(R.id.img_ll_news_search_item_iconOne, attentionListEntities.get(0).getIcon(), 0);
                    holder.setTextViewText(R.id.tv_ll_news_search_item_descrOne, attentionListEntities.get(0).getName());

                    holder.setSimpleDraweeViewResource(R.id.img_ll_news_search_item_iconTwo, R.drawable.search_item_more);
                    holder.setTextViewText(R.id.tv_ll_news_search_item_descrTwo, "更多");

                    holder.getView(R.id.linear_ll_news_search_item_layoutOne).setVisibility(View.VISIBLE);
                    holder.getView(R.id.linear_ll_news_search_item_layoutTwo).setVisibility(View.VISIBLE);
                    holder.getView(R.id.linear_ll_news_search_item_layoutThree).setVisibility(View.INVISIBLE);
                    holder.getView(R.id.linear_ll_news_search_item_layoutFour).setVisibility(View.INVISIBLE);

                    holder.getView(R.id.linear_ll_news_search_item_layoutOne).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            setOpenAttentionPage(attentionListEntities.get(0));
                        }
                    });
                    holder.getView(R.id.linear_ll_news_search_item_layoutOne).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent in = new Intent(mContext, SubscribeListActivity.class);
                            in.putExtra(SubscribeListActivity.KEY_SUBSCRIBE_LIST, attentionListEntities);
                            mContext.startActivity(in);

                        }
                    });
                    holder.getView(R.id.linear_ll_news_search_item_layoutThree).setOnClickListener(null);
                    holder.getView(R.id.linear_ll_news_search_item_layoutFour).setOnClickListener(null);
                } else if (size == 2) {
                    holder.setSimpleDraweeViewURI(R.id.img_ll_news_search_item_iconOne, attentionListEntities.get(0).getIcon(), 0);
                    holder.setTextViewText(R.id.tv_ll_news_search_item_descrOne, attentionListEntities.get(0).getName());
                    holder.setSimpleDraweeViewURI(R.id.img_ll_news_search_item_iconTwo, attentionListEntities.get(1).getIcon(), 1);
                    holder.setTextViewText(R.id.tv_ll_news_search_item_descrTwo, attentionListEntities.get(1).getName());

                    holder.setSimpleDraweeViewResource(R.id.img_ll_news_search_item_iconThree, R.drawable.search_item_more);
                    holder.setTextViewText(R.id.tv_ll_news_search_item_descrThree, "更多");

                    holder.getView(R.id.linear_ll_news_search_item_layoutOne).setVisibility(View.VISIBLE);
                    holder.getView(R.id.linear_ll_news_search_item_layoutTwo).setVisibility(View.VISIBLE);
                    holder.getView(R.id.linear_ll_news_search_item_layoutThree).setVisibility(View.VISIBLE);
                    holder.getView(R.id.linear_ll_news_search_item_layoutFour).setVisibility(View.INVISIBLE);

                    holder.getView(R.id.linear_ll_news_search_item_layoutOne).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            setOpenAttentionPage(attentionListEntities.get(0));
                        }
                    });
                    holder.getView(R.id.linear_ll_news_search_item_layoutTwo).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            setOpenAttentionPage(attentionListEntities.get(1));
                        }
                    });
                    holder.getView(R.id.linear_ll_news_search_item_layoutThree).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent in = new Intent(mContext, SubscribeListActivity.class);
                            in.putExtra(SubscribeListActivity.KEY_SUBSCRIBE_LIST, attentionListEntities);
                            mContext.startActivity(in);
                        }
                    });
                    holder.getView(R.id.linear_ll_news_search_item_layoutFour).setOnClickListener(null);
                } else if (size >= 3) {
                    holder.setSimpleDraweeViewURI(R.id.img_ll_news_search_item_iconOne, attentionListEntities.get(0).getIcon(), 0);
                    holder.setTextViewText(R.id.tv_ll_news_search_item_descrOne, attentionListEntities.get(0).getName());
                    holder.setSimpleDraweeViewURI(R.id.img_ll_news_search_item_iconTwo, attentionListEntities.get(1).getIcon(), 1);
                    holder.setTextViewText(R.id.tv_ll_news_search_item_descrTwo, attentionListEntities.get(1).getName());
                    holder.setSimpleDraweeViewURI(R.id.img_ll_news_search_item_iconThree, attentionListEntities.get(2).getIcon(), 2);
                    holder.setTextViewText(R.id.tv_ll_news_search_item_descrThree, attentionListEntities.get(2).getName());

                    holder.setSimpleDraweeViewResource(R.id.img_ll_news_search_item_iconFour, R.drawable.search_item_more);
                    holder.setTextViewText(R.id.tv_ll_news_search_item_descrFour, "更多");

                    holder.getView(R.id.linear_ll_news_search_item_layoutOne).setVisibility(View.VISIBLE);
                    holder.getView(R.id.linear_ll_news_search_item_layoutTwo).setVisibility(View.VISIBLE);
                    holder.getView(R.id.linear_ll_news_search_item_layoutThree).setVisibility(View.VISIBLE);
                    holder.getView(R.id.linear_ll_news_search_item_layoutFour).setVisibility(View.VISIBLE);

                    holder.getView(R.id.linear_ll_news_search_item_layoutOne).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            setOpenAttentionPage(attentionListEntities.get(0));
                        }
                    });
                    holder.getView(R.id.linear_ll_news_search_item_layoutTwo).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            setOpenAttentionPage(attentionListEntities.get(1));
                        }
                    });
                    holder.getView(R.id.linear_ll_news_search_item_layoutThree).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            setOpenAttentionPage(attentionListEntities.get(2));
                        }
                    });
                    holder.getView(R.id.linear_ll_news_search_item_layoutFour).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent in = new Intent(mContext, SubscribeListActivity.class);
                            in.putExtra(SubscribeListActivity.KEY_SUBSCRIBE_LIST, attentionListEntities);
                            mContext.startActivity(in);
                        }
                    });
                }
                break;
            case R.layout.ll_video_item_player:
                if (isFavorite) {
                    setTitleTextBySpannable((TextView) holder.getView(R.id.tv_video_title), feed.getTitle(), false);
                } else {
                    setTitleTextBySpannable((TextView) holder.getView(R.id.tv_video_title), feed.getTitle(), feed.isRead());
                }
                ImageView ivVideo = holder.getView(R.id.image_bg);
                RelativeLayout.LayoutParams lpVideo = (RelativeLayout.LayoutParams) ivVideo.getLayoutParams();
                lpVideo.width = mScreenWidth;
                lpVideo.height = (int) (mScreenWidth * 203 / 360.0f);
                ivVideo.setLayoutParams(lpVideo);
                holder.getView(R.id.layout_item_video).setLayoutParams(lpVideo);
                holder.setIsShowImagesSimpleDraweeViewURI(R.id.image_bg, feed.getThumbnail(), 0, 0, feed.getRtype());
                setCommentViewText((TextViewExtend) holder.getView(R.id.tv_video_comments), feed.getComment() + "");
                //点击评论跳转
                holder.getView(R.id.tv_video_comments).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        setCommentClick(feed);
                    }
                });
                //视频播放
                setPlayClick((RelativeLayout) holder.getView(R.id.rl_video_show), position, feed);
                //item点击事件跳转到详情页播放
                setNewsContentClick((RelativeLayout) holder.getView(R.id.news_content_relativeLayout), feed);
                setVideoDuration((TextView) holder.getView(R.id.tv_video_duration), feed.getDuration());
                setShareClick((ImageView) holder.getView(R.id.iv_video_share), feed);
                break;
            case R.layout.ll_video_item_small:
                if (isFavorite) {
                    setTitleTextBySpannable((TextView) holder.getView(R.id.tv_video_title), feed.getTitle(), false, feed.getRtype());
                } else {
                    setTitleTextBySpannable((TextView) holder.getView(R.id.tv_video_title), feed.getTitle(), feed.isRead(), feed.getRtype());
                }
                ImageView ivVideoSmall = holder.getView(R.id.image_bg);
                RelativeLayout.LayoutParams lpVideoSmall = (RelativeLayout.LayoutParams) ivVideoSmall.getLayoutParams();
                lpVideoSmall.width = mCardWidth;
                lpVideoSmall.height = mCardHeight;
                ivVideoSmall.setLayoutParams(lpVideoSmall);
                holder.setIsShowImagesSimpleDraweeViewURI(R.id.image_bg, feed.getThumbnail(), 0, 0, feed.getRtype());
                //点击评论跳转
//                holder.getView(R.id.item_bottom_video).setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        Intent intent = new Intent(mContext, NewsDetailVideoAty.class);
//                        intent.putExtra(NewsFeedFgt.KEY_NEWS_FEED, feed);
//                        intent.putExtra(NewsFeedFgt.KEY_SHOW_COMMENT, true);
//                        if (mNewsFeedFgt != null) {
//                            mNewsFeedFgt.startActivityForResult(intent, REQUEST_CODE);
//                        } else {
//                            ((Activity) mContext).startActivityForResult(intent, REQUEST_CODE);
//                        }
//                    }
//                });
                setFocusBgColor((TextViewExtend) holder.getView(R.id.news_source_TextView), feed.getPname(), (TextViewExtend) holder.getView(R.id.comment_num_textView), (ImageView) holder.getView(R.id.delete_imageView));
                //item点击事件跳转到详情页播放
                setNewsContentClick((RelativeLayout) holder.getView(R.id.news_content_relativeLayout), feed);
                setVideoDuration((TextView) holder.getView(R.id.tv_video_duration), feed.getDuration());
                setCommentViewText((TextViewExtend) holder.getView(R.id.comment_num_textView), feed.getComment() + "");
                holder.getView(R.id.comment_num_textView).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        setCommentClick(feed);
                    }
                });
                if (feed.getPtime() != null) {
                    setNewsTime((TextViewExtend) holder.getView(R.id.comment_textView), feed.getPtime());
                }
                setSourceViewText((TextViewExtend) holder.getView(R.id.news_source_TextView), feed.getPname());
                setSourceImage((ImageView) holder.getView(R.id.news_source_ImageView), feed.getIcon(), position);
                setNewsContentClick((RelativeLayout) holder.getView(R.id.news_content_relativeLayout), feed);
                setDeleteClick((ImageView) holder.getView(R.id.delete_imageView), feed, holder.getConvertView());
                newsTag((TextViewExtend) holder.getView(R.id.type_textView), feed.getRtype());
                break;
        }
    }

    private void setPlayClick(final RelativeLayout view, final int position, final NewsFeed feed) {
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onPlayClickListener != null) {
                    onPlayClickListener.onPlayClick(view, feed);
                }
            }
        });
    }

    private void setCommentClick(NewsFeed newsFeed) {
        Intent intent = new Intent(mContext, NewsDetailVideoAty.class);
        intent.putExtra(NewsFeedFgt.KEY_NEWS_FEED, newsFeed);
        intent.putExtra(NewsFeedFgt.KEY_SHOW_COMMENT, true);
        if (mNewsFeedFgt != null) {
            mNewsFeedFgt.startActivityForResult(intent, REQUEST_CODE);
        } else {
            ((Activity) mContext).startActivityForResult(intent, REQUEST_CODE);
        }
    }

    private void setShareClick(final ImageView imageView, final NewsFeed newsFeed) {
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setAction(MainAty.ACTION_SHOW_SHARE);
                intent.putExtra("newsfeed", newsFeed);
                mContext.sendBroadcast(intent);
            }
        });
    }


    private void upLoadAd(NewsFeed feed) {
        if (feed.getRtype() == 3) {
            String url = null;
            ArrayList<String> arrUrl = feed.getAdimpression();
            if (!TextUtil.isListEmpty(arrUrl)) {
                url = arrUrl.get(0);
            }
            //广告
            if (!TextUtil.isEmptyString(url) && !feed.isUpload()) {
                feed.setUpload(true);
                String lat = SharedPreManager.get(CommonConstant.FILE_USER_LOCATION, CommonConstant.KEY_LOCATION_LATITUDE);
                String lon = SharedPreManager.get(CommonConstant.FILE_USER_LOCATION, CommonConstant.KEY_LOCATION_LONGITUDE);
                String[] realUrl = url.split("&lon");
                final String requestUrl = realUrl[0] + "&lon=" + lon + "&lat=" + lat;
                RequestQueue requestQueue = YaZhiDaoApplication.getInstance().getRequestQueue();
                StringRequest request = new StringRequest(Request.Method.GET, requestUrl, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                    }
                }, null);
                requestQueue.add(request);
            }
        }
    }


    private void setOpenAttentionPage(AttentionListEntity attention) {
        Intent attentionAty = new Intent(mContext, AttentionActivity.class);
        attentionAty.putExtra(AttentionActivity.KEY_ATTENTION_CONPUBFLAG, attention.getFlag());
        attentionAty.putExtra(AttentionActivity.KEY_ATTENTION_HEADIMAGE, attention.getIcon());
        attentionAty.putExtra(AttentionActivity.KEY_ATTENTION_TITLE, attention.getName());
        mContext.startActivity(attentionAty);
    }

    private void setCardMargin(ImageView ivCard, int leftMargin, int rightMargin, int pageNum) {
        LinearLayout.LayoutParams localLayoutParams = (LinearLayout.LayoutParams) ivCard.getLayoutParams();
        localLayoutParams.leftMargin = DensityUtil.dip2px(mContext, leftMargin);
        localLayoutParams.rightMargin = DensityUtil.dip2px(mContext, rightMargin);
        int width = (int) (mScreenWidth / 2.0f - DensityUtil.dip2px(mContext, 15));
        if (pageNum == 2) {
            localLayoutParams.width = width;
            localLayoutParams.height = (int) (width * 71 / 108f);
        } else if (pageNum == 3) {
            localLayoutParams.width = mCardWidth;
            localLayoutParams.height = mCardHeight;
        }
        ivCard.setLayoutParams(localLayoutParams);
    }

    private void setNewsTime(TextViewExtend tvComment, String updateTime) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Calendar calendar = dateFormat.getCalendar();
        try {
            Date date = dateFormat.parse(updateTime);
            long between = System.currentTimeMillis() - date.getTime();
            int month = calendar.get(Calendar.MONTH) + 1;
            int day = calendar.get(Calendar.DAY_OF_MONTH);
            if (between >= (24 * 3600000)) {
//                tvComment.setText(updateTime);
                if (isAttention) {
//                    tvComment.setText(between / 3600000 + "小时前");
                    tvComment.setText(month + "月" + day + "日");
                } else {
                    tvComment.setText("");
                }
            } else if (between < (24 * 3600000) && between >= (1 * 3600000)) {
                if (isAttention) {
                    tvComment.setText(between / 3600000 + "小时前");
                } else {
                    tvComment.setText("");
                }
            } else {
                int time = (int) (between * 60 / 3600000);
                if (time > 0) {
                    tvComment.setText(between * 60 / 3600000 + "分钟前");
                } else if (time <= 0) {
                    tvComment.setText("");
                } else {
                    tvComment.setText(between * 60 * 60 / 3600000 + "秒前");
                }
//                if (between / 3600000 == 0) {
//                    tvComment.setText( between *60/ 3600000+"分钟前");
//                } else {
//                    tvComment.setText(between / 3600000 / 60 + "分钟前");
//                }
            }
        } catch (ParseException e) {
            tvComment.setText(updateTime);
            e.printStackTrace();
        }

    }

    private void setTitleTextBySpannable(TextView tvTitle, String strTitle, boolean isRead, int type) {
        if (strTitle != null && !"".equals(strTitle)) {
            if (mstrKeyWord != null && !"".equals(mstrKeyWord)) {
//                strTitle = strTitle.replace(mstrKeyWord.toLowerCase(), "<font color =\"#35a6fb\">" + mstrKeyWord.toLowerCase() + "</font>");
                tvTitle.setText(Html.fromHtml(strTitle), TextView.BufferType.SPANNABLE);
            } else {
                if (type != 1 && type != 2 && type != 3 && type != 4 && type != 6) {
                    tvTitle.setText(strTitle);
                } else {
                    tvTitle.setText("        " + strTitle);
                }
                tvTitle.setLineSpacing(0, 1.1f);
            }
            if (isRead) {
                tvTitle.setTextColor(mContext.getResources().getColor(R.color.new_color3));
            } else {
                tvTitle.setTextColor(mContext.getResources().getColor(R.color.new_color1));
            }
            tvTitle.setTextSize(mSharedPreferences.getInt("textSize", CommonConstant.TEXT_SIZE_NORMAL));
        }
    }

    private void setTitleTextBySpannable(TextView tvTitle, String strTitle, boolean isRead) {
        if (strTitle != null && !"".equals(strTitle)) {
            if (mstrKeyWord != null && !"".equals(mstrKeyWord)) {
//                strTitle = strTitle.replace(mstrKeyWord.toLowerCase(), "<font color =\"#35a6fb\">" + mstrKeyWord.toLowerCase() + "</font>");
                tvTitle.setText(Html.fromHtml(strTitle), TextView.BufferType.SPANNABLE);
            } else {
                tvTitle.setText(strTitle);
                tvTitle.setLineSpacing(0, 1.1f);
            }
            if (isRead) {
                tvTitle.setTextColor(mContext.getResources().getColor(R.color.new_color3));
            } else {
                tvTitle.setTextColor(mContext.getResources().getColor(R.color.new_color1));
            }
            tvTitle.setTextSize(mSharedPreferences.getInt("textSize", CommonConstant.TEXT_SIZE_NORMAL));
        }
    }

    private void setSourceViewText(TextViewExtend textView, String strText) {
        if (strText != null && !"".equals(strText)) {
            textView.setText(strText);
        }
    }

    private void setSourceImage(ImageView imageView, String url, int position) {
//        imageView.setBackgroundResource(mTypedArray.getResourceId((Math.abs(position + iRandom)) % 7, 0));
        if (mReleaseSourceItem == null) {
            imageView.setVisibility(View.VISIBLE);
            if (!TextUtil.isEmptyString(url)) {
//                .placeholder(mTypedArray.getResourceId(random.nextInt(6), 0))
                Glide.with(mContext).load(Uri.parse(url)).placeholder(mTypedArray.getResourceId((Math.abs(position + iRandom)) % 7, 0)).transform(new CommonViewHolder.GlideCircleTransform(mContext, 1, mContext.getResources().getColor(R.color.news_source_bg))).into(imageView);
            } else {
                Glide.with(mContext).load("").placeholder(mTypedArray.getResourceId((Math.abs(position + iRandom)) % 7, 0)).transform(new CommonViewHolder.GlideCircleTransform(mContext, 1, mContext.getResources().getColor(R.color.news_source_bg))).into(imageView);
            }
        } else {
            imageView.setVisibility(View.GONE);
        }
    }

    private void setSourceOnClick(View linearLayout, final NewsFeed newsFeed) {
        linearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, AttentionActivity.class);
                intent.putExtra(AttentionActivity.KEY_ATTENTION_HEADIMAGE, newsFeed.getPurl());
                intent.putExtra(AttentionActivity.KEY_ATTENTION_TITLE, newsFeed.getPname());
                intent.putExtra(AttentionActivity.KEY_ATTENTION_CONPUBFLAG, newsFeed.getConpubflag());
                mContext.startActivity(intent);
            }
        });
    }

    private void setFocusBgColor(TextViewExtend textView, String pName, TextViewExtend tvCommentNum, ImageView ivDelete) {
        if (mReleaseSourceItem != null) {
            textView.setBackgroundResource(R.drawable.bg_feed_item_comment);
            tvCommentNum.setVisibility(View.GONE);
            ivDelete.setVisibility(View.GONE);
            Integer color = mReleaseSourceItem.get(pName);
            GradientDrawable myGrad = (GradientDrawable) textView.getBackground();
            textView.setTextColor(Color.WHITE);
            if (color != null) {
                myGrad.setColor(color);
            } else {
                Random random = new Random();
                int index = random.nextInt(mColorArr.length);
                int bgColor = Color.parseColor(mColorArr[index]);
                myGrad.setColor(bgColor);
                ReleaseSourceItem item = new ReleaseSourceItem();
                item.setBackground(bgColor);
                item.setpName(pName);
                mReleaseSourceItem.put(pName, bgColor);
                mReleaseSourceItemDao.insertOrUpdate(item);
            }
        }
    }

    public void setReleaseSourceItems(ReleaseSourceItemDao releaseSourceItemDao, String[] colorArr) {
        mReleaseSourceItemDao = releaseSourceItemDao;
        mReleaseSourceItem = mReleaseSourceItemDao.queryReleaseSourceItem();
        mColorArr = colorArr;
    }

    private void setCommentViewText(TextViewExtend textView, String strText) {
        textView.setText(TextUtil.getCommentNum(strText));
    }

    public void newsTag(TextViewExtend tag, int type) {
        GradientDrawable drawable = (GradientDrawable) tag.getBackground();
        String content = "";
        if (type == 1) {
            if (tag.getVisibility() == View.GONE) {
                tag.setVisibility(View.VISIBLE);
            }
            content = "热点";
            drawable.setColor(mContext.getResources().getColor(R.color.news_type_color1));
        } else if (type == 2) {
            if (tag.getVisibility() == View.GONE) {
                tag.setVisibility(View.VISIBLE);
            }
            content = "推送";
            drawable.setColor(mContext.getResources().getColor(R.color.news_type_color2));
        } else if (type == 3) {
            if (tag.getVisibility() == View.GONE) {
                tag.setVisibility(View.VISIBLE);
            }
            content = "广告";
            drawable.setColor(mContext.getResources().getColor(R.color.news_type_color3));
        } else if (type == 4) {
            if (tag.getVisibility() == View.GONE) {
                tag.setVisibility(View.VISIBLE);
            }
            content = "专题";
            drawable.setColor(mContext.getResources().getColor(R.color.news_type_color4));
        } else if (type == 5) {
            if (tag.getVisibility() == View.GONE) {
                tag.setVisibility(View.VISIBLE);
            }
            content = "多图";
            drawable.setColor(mContext.getResources().getColor(R.color.news_type_color5));
        } else if (type == 6) {
            if (tag.getVisibility() == View.GONE) {
                tag.setVisibility(View.VISIBLE);
            }
            content = "视频";
            drawable.setColor(mContext.getResources().getColor(R.color.news_type_color6));
        } else if (type == 7) {
            if (tag.getVisibility() == View.GONE) {
                tag.setVisibility(View.VISIBLE);
            }
            content = "本地";
            drawable.setColor(mContext.getResources().getColor(R.color.news_type_color7));
        } else {
            if (tag.getVisibility() == View.VISIBLE) {
                tag.setVisibility(View.GONE);
            }
            return;
        }
        tag.setText(content);
//        tag.setGravity(Gravity.CENTER);
//        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) tag.getLayoutParams();
//        params.width = DensityUtil.dip2px(mContext, 20);
//        params.height = DensityUtil.dip2px(mContext, 11);
//        tag.setLayoutParams(params);
    }

    /**
     * item的点击事件
     *
     * @param rlNewsContent
     * @param feed
     */
    private void setNewsContentClick(final RelativeLayout rlNewsContent, final NewsFeed feed) {
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
                    if (mNewsFeedFgt != null) {
                        mNewsFeedFgt.startActivityForResult(AdIntent, REQUEST_CODE);
                    } else {
                        ((Activity) mContext).startActivityForResult(AdIntent, REQUEST_CODE);
                    }
                } else if (feed.getRtype() == 4) {
                    Intent AdIntent = new Intent(mContext, NewsTopicAty.class);
                    AdIntent.putExtra(NewsTopicAty.KEY_NID, feed.getNid());
                    if (mNewsFeedFgt != null) {
                        mNewsFeedFgt.startActivityForResult(AdIntent, REQUEST_CODE);
                    } else {
                        ((Activity) mContext).startActivityForResult(AdIntent, REQUEST_CODE);
                    }
                } else if (feed.getRtype() == 6) {
                    if (onPlayClickListener!=null)
                    {
                     onPlayClickListener.onItemClick(rlNewsContent,feed);
                    }
//                    Intent intent = new Intent(mContext, NewsDetailVideoAty.class);
//                    intent.putExtra(NewsFeedFgt.KEY_NEWS_FEED, feed);
//                    if (mNewsFeedFgt != null) {
//                        mNewsFeedFgt.startActivityForResult(intent, REQUEST_CODE);
//                    } else {
//                        ((Activity) mContext).startActivityForResult(intent, REQUEST_CODE);
//                    }
                } else {
                    Intent intent = new Intent(mContext, NewsDetailAty2.class);
                    intent.putExtra(NewsFeedFgt.KEY_NEWS_FEED, feed);
                    ArrayList<String> imageList = feed.getImgs();
                    if (imageList != null && imageList.size() != 0) {
                        intent.putExtra(NewsFeedFgt.KEY_NEWS_IMAGE, imageList.get(0));
                    }
                    if (mNewsFeedFgt != null) {
                        mNewsFeedFgt.startActivityForResult(intent, REQUEST_CODE);
                    } else {
                        ((Activity) mContext).startActivityForResult(intent, REQUEST_CODE);
                    }
                }
                //推送人员使用
                if (DeviceInfoUtil.getUUID().equals("3b7976c8c1b8cd372a59b05bfa9ac5b3")) {
                    File file = FileUtils.getSavePushInfoPath(mContext, "push.txt");
                    BufferedWriter bis = null;
                    try {
                        bis = new BufferedWriter(new FileWriter(file));
                        bis.write(feed.getTitle() + ",newsid=" + feed.getNid());
                        bis.newLine();
                        bis.flush();
                    } catch (Exception e) {
                        e.printStackTrace();
                    } finally {
                        if (bis != null) {
                            try {
                                bis.close();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }

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

    clickShowPopWindow mClickShowPopWindow;

    public void setClickShowPopWindow(clickShowPopWindow mClickShowPopWindow) {
        this.mClickShowPopWindow = mClickShowPopWindow;
    }

    NewsFeed DeleteClickBean;
    View DeleteView;

    private void setDeleteClick(final ImageView imageView, final NewsFeed feed, final View view) {
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                ToastUtil.toastShort(feed.getPname());
                MobclickAgent.onEvent(mContext, "qidian_feed_delete_dislike");
                DeleteView = view;
                DeleteClickBean = feed;
                int[] LocationInWindow = new int[2];
                int[] LocationOnScreen = new int[2];
                imageView.getLocationInWindow(LocationInWindow);

                mClickShowPopWindow.showPopWindow(LocationInWindow[0] + imageView.getWidth() / 2, LocationInWindow[1] + imageView.getHeight() / 2,
                        feed);


            }
        });
    }

    int height;

    public void disLikeDeleteItem() {
        final ViewWrapper wrapper = new ViewWrapper(DeleteView);
        height = DeleteView.getHeight();
        ObjectAnimator changeH = ObjectAnimator.ofInt(wrapper, "height", DeleteView.getHeight(), 0).setDuration(550);
        changeH.start();
        changeH.setInterpolator(new AccelerateInterpolator());
        changeH.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                mNewsFeedDao.deleteOnceDate(DeleteClickBean);
//                ObjectAnimator.ofFloat(wrapper, "height", 0, deleteViewHeight).setDuration(0).start();
                ArrayList<NewsFeed> arrayList = getNewsFeed();
                arrayList.remove(DeleteClickBean);
                notifyDataSetChanged();
            }
        });

    }

    class ViewWrapper {
        private View mTarget;

        public ViewWrapper(View mTarget) {
            this.mTarget = mTarget;
        }

        public int getHeight() {
            int height = mTarget.getLayoutParams().height;
            return height;
        }

        public void setHeight(int height) {
            mTarget.getLayoutParams().height = height;
            mTarget.requestLayout();
        }
    }

    private introductionNewsFeed mIntroductionNewsFeed;

    public void setIntroductionNewsFeed(introductionNewsFeed mIntroductionNewsFeed) {
        this.mIntroductionNewsFeed = mIntroductionNewsFeed;
    }

    public void ClickDeleteFavorite(final ImageView checkDelete, final NewsFeed feed) {
        checkDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (feed.isFavorite()) {
                    feed.setFavorite(false);
                    mIntroductionNewsFeed.getDate(feed, false);
                    checkDelete.setImageResource(R.drawable.favorite_uncheck);
                } else {
                    mIntroductionNewsFeed.getDate(feed, true);
                    feed.setFavorite(true);
                    checkDelete.setImageResource(R.drawable.favorite_check);
                }
            }
        });
    }

    public void setVisitycheckFavoriteDeleteLayout(boolean isVisity) {
        isCkeckVisity = isVisity;
        notifyDataSetChanged();
    }

    public void setVideoDuration(TextView durationView, int duration) {
        if (duration != 0) {
            String time = TextUtil.secToTime(duration);
            durationView.setText(time);
        } else {
            durationView.setText("");
        }
    }

    /**
     * 接口回调传入数据的添加与删除
     * <p/>
     * isCheck true：添加   false：删除
     */
    public interface introductionNewsFeed {
        public void getDate(NewsFeed feed, boolean isCheck);
    }

    public interface clickShowPopWindow {
        public void showPopWindow(int x, int y, NewsFeed feed);
    }

    //视频播放接口
    private OnPlayClickListener onPlayClickListener;

    public void setOnPlayClickListener(OnPlayClickListener onPlayClickListener) {
        this.onPlayClickListener = onPlayClickListener;

    }

    public interface OnPlayClickListener {
        void onPlayClick(RelativeLayout relativeLayout, NewsFeed feed);
        void onItemClick(RelativeLayout rlNewsContent, NewsFeed feed);
    }
}