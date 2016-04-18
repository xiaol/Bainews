package com.news.yazhidao.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.text.Html;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.Transformation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.news.yazhidao.R;
import com.news.yazhidao.adapter.abslistview.CommonViewHolder;
import com.news.yazhidao.adapter.abslistview.MultiItemCommonAdapter;
import com.news.yazhidao.adapter.abslistview.MultiItemTypeSupport;
import com.news.yazhidao.common.CommonConstant;
import com.news.yazhidao.database.NewsFeedDao;
import com.news.yazhidao.entity.NewsFeed;
import com.news.yazhidao.pages.NewsDetailAty2;
import com.news.yazhidao.pages.NewsFeedFgt;
import com.news.yazhidao.utils.DensityUtil;
import com.news.yazhidao.utils.DeviceInfoUtil;
import com.news.yazhidao.utils.FileUtils;
import com.news.yazhidao.utils.Logger;
import com.news.yazhidao.utils.TextUtil;
import com.news.yazhidao.widget.TextViewExtend;
import com.umeng.analytics.MobclickAgent;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;


public class NewsFeedAdapter extends MultiItemCommonAdapter<NewsFeed> {

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
    public static HashSet<Integer> mSaveData = new HashSet<>();

    public NewsFeedAdapter(Context context, NewsFeedFgt newsFeedFgt, ArrayList<NewsFeed> datas) {
        super(context, datas, new MultiItemTypeSupport<NewsFeed>() {
            @Override
            public int getLayoutId(int position, NewsFeed newsFeed) {
                switch (newsFeed.getImgStyle()) {
                    case "0":
                        return R.layout.ll_news_item_no_pic;
                    case "1":
                    case "2":
                        return R.layout.ll_news_item_no_pic;
                    case "3":
                        return R.layout.ll_news_card;
                    default:
                        return 0;
                }
            }

            @Override
            public int getViewTypeCount() {
                return 3;
            }

            @Override
            public int getItemViewType(int position, NewsFeed newsFeed) {
                switch (newsFeed.getImgStyle()) {
                    case "0":
                        return NewsFeed.NO_PIC;
                    case "1":
                    case "2":
                        return NewsFeed.ONE_AND_TWO_PIC;
                    case "3":
                        return NewsFeed.THREE_PIC;
                    default:
                        return 0;
                }
            }
        });
        mContext = context;
        mScreenWidth = DeviceInfoUtil.getScreenWidth();
        mScreenHeight = DeviceInfoUtil.getScreenHeight();
        this.mNewsFeedFgt = newsFeedFgt;
        mSharedPreferences = mContext.getSharedPreferences("showflag", 0);
        mNewsFeedDao = new NewsFeedDao(mContext);
    }

    public void setSearchKeyWord(String pKeyWord) {
        mstrKeyWord = pKeyWord;
        mDatas = null;
    }

    @Override
    public void convert(final CommonViewHolder holder, NewsFeed feed) {
        switch (holder.getLayoutId()) {
            case R.layout.ll_news_item_no_pic:
                setTitleTextBySpannable((TextView) holder.getView(R.id.title_textView), feed.getTitle(), feed.isRead());
                setSourceViewText((TextViewExtend) holder.getView(R.id.news_source_TextView), feed.getPubName());
                setCommentViewText((TextViewExtend) holder.getView(R.id.comment_num_textView), feed.getCommentsCount());
                if (feed.getPubTime() != null)
                    setNewsTime((TextViewExtend) holder.getView(R.id.comment_textView), feed.getPubTime());
                setNewsContentClick((RelativeLayout) holder.getView(R.id.news_content_relativeLayout), feed);
                setDeleteClick((ImageView) holder.getView(R.id.delete_imageView), feed, holder.getConvertView());
                break;
            case R.layout.ll_news_item_one_pic:
                holder.setSimpleDraweeViewURI(R.id.title_img_View, feed.getImgList().get(0));
                final String strTitle = feed.getTitle();
                setTitleTextBySpannable((TextView) holder.getView(R.id.title_textView), strTitle, feed.isRead());
                final TextView tvTitle = (TextView) holder.getView(R.id.title_textView);
                final LinearLayout llSourceContent = (LinearLayout) holder.getView(R.id.source_content_linearLayout);
                final ImageView ivBottomLine = (ImageView) holder.getView(R.id.line_bottom_imageView);
                tvTitle.post(new Runnable() {
                    @Override
                    public void run() {
                        RelativeLayout.LayoutParams lpSourceContent = (RelativeLayout.LayoutParams) llSourceContent.getLayoutParams();
                        RelativeLayout.LayoutParams titleLp = (RelativeLayout.LayoutParams) tvTitle.getLayoutParams();
                        RelativeLayout.LayoutParams lpBottomLine = (RelativeLayout.LayoutParams) ivBottomLine.getLayoutParams();
                        int lineCount = tvTitle.getLineCount();
                        if (lineCount >= 3) {
                            titleLp.setMargins(DensityUtil.dip2px(mContext, 15), DensityUtil.dip2px(mContext, 10), DensityUtil.dip2px(mContext, 15), 0);
                            lpSourceContent.rightMargin = DensityUtil.dip2px(mContext, 15);
                            lpBottomLine.addRule(RelativeLayout.BELOW, R.id.source_content_linearLayout);
                        } else if (lineCount == 1) {
                            titleLp.setMargins(DensityUtil.dip2px(mContext, 15), DensityUtil.dip2px(mContext, 21), DensityUtil.dip2px(mContext, 15), 0);
                            lpSourceContent.rightMargin = DensityUtil.dip2px(mContext, 127);
                            lpBottomLine.addRule(RelativeLayout.BELOW, R.id.title_img_View);
                        } else {
                            titleLp.setMargins(DensityUtil.dip2px(mContext, 15), DensityUtil.dip2px(mContext, 10), DensityUtil.dip2px(mContext, 15), 0);
                            lpSourceContent.rightMargin = DensityUtil.dip2px(mContext, 127);
                            lpBottomLine.addRule(RelativeLayout.BELOW, R.id.title_img_View);
                        }
                        llSourceContent.setLayoutParams(lpSourceContent);
                        ivBottomLine.setLayoutParams(lpBottomLine);
                    }
                });

                setSourceViewText((TextViewExtend) holder.getView(R.id.news_source_TextView), feed.getPubName());
                setCommentViewText((TextViewExtend) holder.getView(R.id.comment_num_textView), feed.getCommentsCount());
                if (feed.getPubTime() != null) {
                    setNewsTime((TextViewExtend) holder.getView(R.id.comment_textView), feed.getPubTime());
                }
                setNewsContentClick((RelativeLayout) holder.getView(R.id.news_content_relativeLayout), feed);
                setDeleteClick((ImageView) holder.getView(R.id.delete_imageView), feed, holder.getConvertView());
                break;
            case R.layout.ll_news_card:
                ArrayList<String> strArrImgUrl = feed.getImgList();
                holder.setSimpleDraweeViewURI(R.id.image_card1, strArrImgUrl.get(0));
                holder.setSimpleDraweeViewURI(R.id.image_card2, strArrImgUrl.get(1));
                holder.setSimpleDraweeViewURI(R.id.image_card3, strArrImgUrl.get(2));
                setCardMargin((SimpleDraweeView) holder.getView(R.id.image_card1), 15, 1, 3);
                setCardMargin((SimpleDraweeView) holder.getView(R.id.image_card2), 1, 1, 3);
                setCardMargin((SimpleDraweeView) holder.getView(R.id.image_card3), 1, 15, 3);
                setTitleTextBySpannable((TextView) holder.getView(R.id.title_textView), feed.getTitle(), feed.isRead());
                setSourceViewText((TextViewExtend) holder.getView(R.id.news_source_TextView), feed.getPubName());
                setCommentViewText((TextViewExtend) holder.getView(R.id.comment_num_textView), feed.getCommentsCount());
                if (feed.getPubTime() != null)
                    setNewsTime((TextViewExtend) holder.getView(R.id.comment_textView), feed.getPubTime());
                setNewsContentClick((RelativeLayout) holder.getView(R.id.news_content_relativeLayout), feed);
                setDeleteClick((ImageView) holder.getView(R.id.delete_imageView), feed, holder.getConvertView());
                break;
        }
    }

    private void setCardMargin(SimpleDraweeView ivCard, int leftMargin, int rightMargin, int pageNum) {
        LinearLayout.LayoutParams localLayoutParams = (LinearLayout.LayoutParams) ivCard.getLayoutParams();
        localLayoutParams.leftMargin = DensityUtil.dip2px(mContext, leftMargin);
        localLayoutParams.rightMargin = DensityUtil.dip2px(mContext, rightMargin);
        int width = (int) (mScreenWidth / 2.0f - DensityUtil.dip2px(mContext, 15));
        if (pageNum == 2) {
            localLayoutParams.width = width;
            localLayoutParams.height = (int) (width * 71 / 108.5f);
        } else if (pageNum == 3) {
            width = (int) (mScreenWidth / 3.0f - DensityUtil.dip2px(mContext, 15));
            localLayoutParams.width = width;
            localLayoutParams.height = (int) (width * 71 / 108.5f);
        }
        ivCard.setLayoutParams(localLayoutParams);
    }

    private void setNewsTime(TextViewExtend tvComment, String updateTime) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            Date date = dateFormat.parse(updateTime);
            long between = System.currentTimeMillis() - date.getTime();
            if (between >= (24 * 3600000)) {
                tvComment.setText(updateTime);
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
            tvComment.setText(updateTime);
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

    private void setCommentViewText(TextViewExtend textView, String strText) {
        if (!TextUtil.isEmptyString(strText) && !"0".equals(strText)) {
            textView.setText(strText + "评");
        } else {
            textView.setText("");
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
                intent.putExtra(NewsFeedFgt.KEY_NEWS_ID, feed.getUrl());
                intent.putExtra(NewsFeedFgt.KEY_URL, feed.getPubUrl());
                intent.putExtra(NewsFeedFgt.KEY_CHANNEL_ID, feed.getChannelId());
                intent.putExtra(NewsFeedFgt.KEY_NEWS_IMG_URL, TextUtil.isListEmpty(feed.getImgList()) ? null : feed.getImgList().get(0));
                intent.putExtra(NewsFeedFgt.KEY_NEWS_TYPE, feed.getImgStyle());
                intent.putExtra(NewsFeedFgt.KEY_NEWS_DOCID, feed.getDocid());
                if (mNewsFeedFgt != null) {
                    mNewsFeedFgt.startActivityForResult(intent, REQUEST_CODE);
                } else {
                    ((Activity) mContext).startActivityForResult(intent, REQUEST_CODE);
                }
                //推送人员使用
                if (DeviceInfoUtil.getUUID().equals("3b7976c8c1b8cd372a59b05bfa9ac5b3")) {
                    File file = FileUtils.getSavePushInfoPath(mContext, "push.txt");
                    BufferedWriter bis = null;
                    try {
                        bis = new BufferedWriter(new FileWriter(file));
                        bis.write(feed.getTitle() + ",newsid=" + feed.getUrl());
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
                MobclickAgent.onEvent(mContext, "user_read_detail");
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
//                ToastUtil.toastShort(feed.getPubName());
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

    public void disLikeDeleteItem() {

        deleteCell(DeleteView);

    }

    private void deleteCell(final View v) {
        AnimationListener al = new AnimationListener() {
            @Override
            public void onAnimationEnd(Animation arg0) {
                Logger.d("jigang", "getTitle" + DeleteClickBean.getTitle());
                mNewsFeedDao.deleteOnceDate(DeleteClickBean);
                ArrayList<NewsFeed> arrayList = getNewsFeed();
                for (NewsFeed feed : arrayList) {
                    Logger.e("jigang", "title+++++" + feed.getTitle());
                }
                arrayList.remove(DeleteClickBean);
                for (NewsFeed feed : arrayList) {
                    Logger.e("jigang", "title-----" + feed.getTitle());
                }
//                setNewsFeed(arrayList);
                CommonViewHolder holder = (CommonViewHolder) v.getTag();
                mSaveData.add(holder.getLayoutId());
                notifyDataSetChanged();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }

            @Override
            public void onAnimationStart(Animation animation) {
            }
        };
        collapse(v, al);
    }

    private int mItemHeight;
    private void collapse(final View v, AnimationListener al) {
        mItemHeight = v.getMeasuredHeight();
        Animation anim = new Animation() {
            @Override
            protected void applyTransformation(float interpolatedTime, Transformation t) {
                if (interpolatedTime == 1) {
                    v.setVisibility(View.GONE);
                } else {
                    v.getLayoutParams().height = mItemHeight - (int) (mItemHeight * interpolatedTime);
                    v.requestLayout();
                }
            }

            @Override
            public boolean willChangeBounds() {
                return true;
            }
        };

        if (al != null) {
            anim.setAnimationListener(al);
        }
        anim.setDuration(DELETEANIMTIME);
        v.startAnimation(anim);
    }


    public interface clickShowPopWindow {
        public void showPopWindow(int x, int y, NewsFeed feed);
    }

}