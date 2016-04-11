package com.news.yazhidao.adapter;

import android.content.Context;
import android.graphics.PointF;
import android.net.Uri;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
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
import com.news.yazhidao.entity.NewsFeed;
import com.news.yazhidao.pages.NewsFeedFgt;
import com.news.yazhidao.utils.DensityUtil;
import com.news.yazhidao.utils.DeviceInfoUtil;
import com.news.yazhidao.utils.ToastUtil;
import com.news.yazhidao.utils.adcoco.AdcocoUtil;
import com.news.yazhidao.widget.FeedDislikePopupWindow;
import com.news.yazhidao.widget.TextViewExtend;
import com.umeng.analytics.AnalyticsConfig;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;


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

    public NewsFeedAdapter(Context context, NewsFeedFgt newsFeedFgt) {
        mContext = context;
        mScreenWidth = DeviceInfoUtil.getScreenWidth();
        mScreenHeight = DeviceInfoUtil.getScreenHeight();
        this.mNewsFeedFgt = newsFeedFgt;
    }

    public NewsFeedAdapter(Context context) {
        this(context, null);
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
        String strType = feed.getImgStyle();
        //没图
        if ("0".equals(strType)) {
            BaseHolder holder;
            if (convertView == null || convertView.getTag().getClass() != BaseHolder.class) {
                holder = new BaseHolder();
                convertView = View.inflate(mContext, R.layout.ll_news_item_no_pic, null);
                holder.tvTitle = (TextView) convertView.findViewById(R.id.title_textView);
                holder.tvSource = (TextViewExtend) convertView.findViewById(R.id.news_source_TextView);
                holder.tvComment = (TextViewExtend) convertView.findViewById(R.id.comment_textView);
                holder.rlNewsContent = (RelativeLayout) convertView.findViewById(R.id.news_content_relativeLayout);
                holder.ivDelete = (ImageView) convertView.findViewById(R.id.delete_imageView);
                holder.tvCommentNum = (TextViewExtend) convertView.findViewById(R.id.comment_num_textView);
                convertView.setTag(holder);
            } else {
                holder = (BaseHolder) convertView.getTag();
            }
            String strTitle = feed.getTitle();
            setTitleTextBySpannable(holder.tvTitle, strTitle, feed.isRead());
            setViewText(holder.tvSource, feed.getPubName());
            if (feed.getPubTime() != null)
                setNewsTime(holder.tvComment, feed.getPubTime());
            setNewsContentClick(holder.rlNewsContent, feed);
            setDeleteClick(holder.ivDelete, feed);
        }
        //普通卡片
        if ("1".equals(strType) || "2".equals(strType)) {
            String platform = AnalyticsConfig.getChannel(mContext);
            if ("adcoco".equals(platform)) {
                AdcocoUtil.update();
            }
            final ViewHolder holder;
            if (convertView == null || convertView.getTag().getClass() != ViewHolder.class) {
                holder = new ViewHolder();
                convertView = View.inflate(mContext, R.layout.ll_news_item_one_pic, null);
                holder.ivTitleImg = (SimpleDraweeView) convertView.findViewById(R.id.title_img_View);
                holder.tvTitle = (TextView) convertView.findViewById(R.id.title_textView);
                holder.tvSource = (TextViewExtend) convertView.findViewById(R.id.news_source_TextView);
                holder.tvComment = (TextViewExtend) convertView.findViewById(R.id.comment_textView);
                holder.rlNewsContent = (RelativeLayout) convertView.findViewById(R.id.news_content_relativeLayout);
                holder.ivDelete = (ImageView) convertView.findViewById(R.id.delete_imageView);
                holder.tvCommentNum = (TextViewExtend) convertView.findViewById(R.id.comment_num_textView);
                holder.llSourceContent = (LinearLayout) convertView.findViewById(R.id.source_content_linearLayout);
                holder.ivBottomLine = (ImageView) convertView.findViewById(R.id.line_bottom_imageView);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            if ("adcoco".equals(platform)) {
                ArrayList localArrayList2 = mArrNewsFeed;
                AdcocoUtil.ad(position, convertView, localArrayList2);
            }
            ArrayList<String> strArrImgUrl = feed.getImgList();
            String strImg = null;
            if ("1".equals(strType) || "2".equals(strType)) {
                strImg = strArrImgUrl.get(0);
                if (strImg != null && !"".equals(strImg)) {
                    holder.ivTitleImg.setImageURI(Uri.parse(strImg));
                }
            }
            final String strTitle = feed.getTitle();
            setTitleTextBySpannable(holder.tvTitle, strTitle, feed.isRead());
            holder.tvTitle.post(new Runnable() {
                @Override
                public void run() {
                    RelativeLayout.LayoutParams lpSourceContent = (RelativeLayout.LayoutParams) holder.llSourceContent.getLayoutParams();
                    RelativeLayout.LayoutParams lpBottomLine = (RelativeLayout.LayoutParams) holder.ivBottomLine.getLayoutParams();
                    if (holder.tvTitle.getLineCount() >= 3) {
                        lpSourceContent.rightMargin = DensityUtil.dip2px(mContext, 15);
                        lpBottomLine.addRule(RelativeLayout.BELOW, R.id.source_content_linearLayout);
                    } else {
                        lpSourceContent.rightMargin = DensityUtil.dip2px(mContext, 127);
                        lpBottomLine.addRule(RelativeLayout.BELOW, R.id.title_img_View);
                    }
                    holder.llSourceContent.setLayoutParams(lpSourceContent);
                    holder.ivBottomLine.setLayoutParams(lpBottomLine);
                }
            });

            setViewText(holder.tvSource, feed.getPubName());
            if (feed.getPubTime() != null)
                setNewsTime(holder.tvComment, feed.getPubTime());
            setNewsContentClick(holder.rlNewsContent, feed);
            setDeleteClick(holder.ivDelete, feed);
        }
        //大图
        else if ("big_pic".equals(strType)) {
//            ||"1".equals(strType)
            //是网易的大图新闻
            boolean isNeteaseBigPic = feed.getPubName().startsWith("网易");
//            isNeteaseBigPic = true;
            if (isNeteaseBigPic) {
                ViewHolder2 holder;
                if (convertView == null || convertView.getTag().getClass() != ViewHolder2.class) {
                    holder = new ViewHolder2();
                    convertView = View.inflate(mContext, R.layout.ll_news_big_pic2, null);
                    holder.ivTitleImg = (SimpleDraweeView) convertView.findViewById(R.id.title_img_View);
                    holder.tvTitle = (TextView) convertView.findViewById(R.id.title_textView);
                    holder.tvSource = (TextViewExtend) convertView.findViewById(R.id.news_source_TextView);
                    holder.tvComment = (TextViewExtend) convertView.findViewById(R.id.comment_textView);
                    holder.rlNewsContent = (RelativeLayout) convertView.findViewById(R.id.news_content_relativeLayout);
                    holder.ivDelete = (ImageView) convertView.findViewById(R.id.delete_imageView);
                    holder.tvCommentNum = (TextViewExtend) convertView.findViewById(R.id.comment_num_textView);
                    convertView.setTag(holder);
                } else {
                    holder = (ViewHolder2) convertView.getTag();
                }
                String strTitle = feed.getTitle();
                setTitleTextBySpannable(holder.tvTitle, strTitle, feed.isRead());
                ArrayList<String> strArrImgUrl = feed.getImgList();
                String strImgUrl = strArrImgUrl.get(0);
                if (strImgUrl != null && !"".equals(strImgUrl)) {
                    holder.ivTitleImg.getHierarchy().setActualImageFocusPoint(new PointF(0.5f, 0.4f));
                    int width = DeviceInfoUtil.getScreenWidth(mContext), height = (int) (DeviceInfoUtil.getScreenWidth(mContext) * 9.0f / 16.0f);
                    ImageRequest request = ImageRequestBuilder.newBuilderWithSource(Uri.parse(strImgUrl))
                            .setResizeOptions(new ResizeOptions(width, height))
                            .build();
                    PipelineDraweeController controller = (PipelineDraweeController) Fresco.newDraweeControllerBuilder()
                            .setOldController(holder.ivTitleImg.getController())
                            .setImageRequest(request)
                            .build();
                    holder.ivTitleImg.setController(controller);
                }
                setViewText(holder.tvSource, feed.getPubName());
                if (feed.getPubTime() != null)
                    setNewsTime(holder.tvComment, feed.getPubTime());
                setNewsContentClick(holder.rlNewsContent, feed);
                setDeleteClick(holder.ivDelete, feed);
            }
        }
        //多图
        else if ("3".equals(strType)) {
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
                holder3.tvCommentNum = (TextViewExtend) convertView.findViewById(R.id.comment_num_textView);
                holder3.ivDelete = (ImageView) convertView.findViewById(R.id.delete_imageView);
                setCardMargin(holder3.ivCard1, 15, 1, 3);
                setCardMargin(holder3.ivCard2, 1, 1, 3);
                setCardMargin(holder3.ivCard3, 1, 15, 3);
                convertView.setTag(holder3);
            } else {
                holder3 = (ViewHolder3) convertView.getTag();
            }
            ArrayList<String> strArrImgUrl = feed.getImgList();
            setLoadImage(holder3.ivCard1, strArrImgUrl.get(0));
            setLoadImage(holder3.ivCard2, strArrImgUrl.get(1));
            setLoadImage(holder3.ivCard3, strArrImgUrl.get(2));
            String strTitle = feed.getTitle();
            setTitleTextBySpannable(holder3.tvTitle, strTitle, feed.isRead());
            setViewText(holder3.tvSource, feed.getPubName());
            if (feed.getPubTime() != null)
                setNewsTime(holder3.tvComment, feed.getPubTime());
            setNewsContentClick(holder3.rlNewsContent, feed);
            setDeleteClick(holder3.ivDelete, feed);
        }
        return convertView;
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

    private void setLoadImage(SimpleDraweeView imageView, String imageUrl) {
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
                tvTitle.setTextColor(mContext.getResources().getColor(R.color.color3));
            } else {
                tvTitle.setTextColor(mContext.getResources().getColor(R.color.color1));
            }
        }
    }

    private void setViewText(TextViewExtend textView, String strText) {
        if (strText != null && !"".equals(strText)) {
            textView.setText(strText);
        }
    }

    private void setNewsContentClick(RelativeLayout rlNewsContent, final NewsFeed feed) {
//        rlNewsContent.setOnClickListener(new View.OnClickListener() {
//            long firstClick = 0;
//
//            public void onClick(View paramAnonymousView) {
//                if (System.currentTimeMillis() - firstClick <= 1500L) {
//                    firstClick = System.currentTimeMillis();
//                    return;
//                }
//                firstClick = System.currentTimeMillis();
//                Intent intent = new Intent(mContext, NewsDetailAty2.class);
//                intent.putExtra(NewsFeedFgt.KEY_NEWS_ID, feed.getUrl());
//                intent.putExtra(NewsFeedFgt.KEY_URL, feed.getPubUrl());
//                intent.putExtra(NewsFeedFgt.KEY_CHANNEL_ID, feed.getChannelId());
//                intent.putExtra(NewsFeedFgt.KEY_NEWS_IMG_URL, TextUtil.isListEmpty(feed.getImgList()) ? null : feed.getImgList().get(0));
//                intent.putExtra(NewsFeedFgt.KEY_NEWS_TYPE, feed.getImgStyle());
//                intent.putExtra(NewsFeedFgt.KEY_NEWS_DOCID, feed.getDocid());
//                if (mNewsFeedFgt != null) {
//                    mNewsFeedFgt.startActivityForResult(intent, REQUEST_CODE);
//                } else {
//                    ((Activity) mContext).startActivityForResult(intent, REQUEST_CODE);
//                }
//                //推送人员使用
//                if (DeviceInfoUtil.getUUID().equals("3b7976c8c1b8cd372a59b05bfa9ac5b3")) {
//                    File file = FileUtils.getSavePushInfoPath(mContext, "push.txt");
//                    BufferedWriter bis = null;
//                    try {
//                        bis = new BufferedWriter(new FileWriter(file));
//                        bis.write(feed.getTitle() + ",newsid=" + feed.getUrl());
//                        bis.newLine();
//                        bis.flush();
//                    } catch (Exception e) {
//                        e.printStackTrace();
//                    } finally {
//                        if (bis != null) {
//                            try {
//                                bis.close();
//                            } catch (IOException e) {
//                                e.printStackTrace();
//                            }
//                        }
//                    }
//
//                }
//                MobclickAgent.onEvent(mContext, "bainews_view_head_news");
//                MobclickAgent.onEvent(mContext, "user_read_detail");
//            }
//        });
    }

    clickShowPopWindow mClickShowPopWindow;
    public void setClickShowPopWindow(clickShowPopWindow mClickShowPopWindow){
        this.mClickShowPopWindow = mClickShowPopWindow;
    }

    private void setDeleteClick(final ImageView imageView, final NewsFeed feed) {
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ToastUtil.toastShort(feed.getPubName());

//
                int[] LocationInWindow = new int[2];
                int[] LocationOnScreen = new int[2];
                imageView.getLocationInWindow(LocationInWindow);

                mClickShowPopWindow.showPopWindow(LocationInWindow[0] + imageView.getWidth()/2, LocationInWindow[1] + imageView.getHeight()/2);

            }
        });
    }


    class BaseHolder {
        TextViewExtend tvSource;
        TextViewExtend tvComment;
        TextView tvTitle;
        RelativeLayout rlNewsContent;
        ImageView ivDelete;
        TextViewExtend tvCommentNum;
        LinearLayout llSourceContent;
    }

    class ViewHolder extends BaseHolder {
        SimpleDraweeView ivTitleImg;
        ImageView ivBottomLine;
    }

    class ViewHolder2 extends BaseHolder {
        SimpleDraweeView ivTitleImg;
    }

    class ViewHolder3 extends BaseHolder {
        SimpleDraweeView ivCard1;
        SimpleDraweeView ivCard2;
        SimpleDraweeView ivCard3;
        LinearLayout llImageList;
    }
    public interface clickShowPopWindow{
        public void showPopWindow(int x,int y);
    }

}