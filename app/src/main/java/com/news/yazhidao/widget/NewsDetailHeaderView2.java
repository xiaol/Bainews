package com.news.yazhidao.widget;

import android.content.Context;
import android.graphics.Color;
import android.graphics.PointF;
import android.graphics.Rect;
import android.net.Uri;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.news.yazhidao.R;
import com.news.yazhidao.entity.NewsDetail;
import com.news.yazhidao.entity.NewsDetailAdd;
import com.news.yazhidao.utils.DensityUtil;
import com.news.yazhidao.utils.DeviceInfoUtil;
import com.news.yazhidao.utils.Logger;
import com.news.yazhidao.utils.TextUtil;

/**
 * Created by fengjigang on 15/9/6.
 */
public class NewsDetailHeaderView2 extends RelativeLayout {

    private final SimpleDraweeView mDetailSpeechCommentUserIcon;
    private final SpeechView mDetailSpeechComment;
    private final LinearLayout mDetailTitleWrapper;
    private int mScreenWidth,mScreenHeight;
    //语音评论父容器,评论父容器
    private View mDetailSpeechCommentWrapper,mDetailCommentWrapper;
    //新闻大头图
    private SimpleDraweeView mDetailHeaderImg;
    //新闻标题,新闻时间,新闻描述
    private TextView mDetailTitle,mDetailDate,mDetailDesc;
    private Context mContext;
    public NewsDetailHeaderView2(Context context) {
        this(context, null);
    }

    public NewsDetailHeaderView2(Context context, AttributeSet attrs) {
        this(context, attrs, -1);
    }

    public NewsDetailHeaderView2(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
        mScreenWidth = DeviceInfoUtil.getScreenWidth(context);
        mScreenHeight = DeviceInfoUtil.getScreenHeight(context);
        View root = View.inflate(context, R.layout.aty_news_detail_header_view2, this);
        mDetailHeaderImg = (SimpleDraweeView)root.findViewById(R.id.mDetailHeaderImg);
        mDetailHeaderImg.getHierarchy().setActualImageFocusPoint(new PointF(.5f,0.35f));
        mDetailHeaderImg.setLayoutParams(new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,(int)(mScreenWidth*520.0f/720)));
        mDetailTitle = (TextView)root.findViewById(R.id.mDetailTitle);
        mDetailDate = (TextView)root.findViewById(R.id.mDetailDate);
        mDetailCommentWrapper = root.findViewById(R.id.mDetailCommentWrapper);
        mDetailSpeechCommentWrapper = root.findViewById(R.id.mDetailSpeechCommentWrapper);
        mDetailSpeechCommentUserIcon = (SimpleDraweeView)root.findViewById(R.id.mDetailSpeechCommentUserIcon);
        mDetailSpeechComment = (SpeechView)root.findViewById(R.id.mDetailSpeechComment);
        mDetailDesc = (TextView)root.findViewById(R.id.mDetailDesc);
        mDetailTitleWrapper = (LinearLayout)root.findViewById(R.id.mDetailTitleWrapper);
    }

    /**
     * 获取数据后刷新界面
     * @param pNewsDetail
     */
    public void updateView(Object pNewsDetail){
        //FIXME 等接口改后,此处就不用这么费劲来写了,可以简化
        if(pNewsDetail instanceof NewsDetail){
            NewsDetail detail = (NewsDetail)pNewsDetail;
            if (!TextUtil.isEmptyString(detail.imgUrl)){
                mDetailHeaderImg.setVisibility(VISIBLE);
                mDetailHeaderImg.setImageURI(Uri.parse(detail.imgUrl));
            }else {
                mDetailHeaderImg.setVisibility(GONE);
            }
            mDetailTitle.setText(detail.title);
            mDetailDate.setText(detail.updateTime);
            /**语音评论和新闻描述有一个不为null*/
            if (!TextUtil.isEmptyString(detail.abs) || detail.isdoc){
                if (!TextUtil.isEmptyString(detail.abs)){
                    mDetailDesc.setText(detail.abs.replace("\n",""));
                    mDetailDesc.setVisibility(VISIBLE);
                }else {
                    mDetailDesc.setVisibility(GONE);
                }
                if (detail.isdoc){
                    mDetailSpeechComment.setUrlAndDuration(detail.docUrl,Integer.valueOf(detail.docTime),true);
                    mDetailSpeechCommentUserIcon.setImageURI(Uri.parse(detail.docUserIcon));
                    mDetailSpeechCommentWrapper.setVisibility(VISIBLE);
                }else{
                    mDetailSpeechCommentWrapper.setVisibility(GONE);
                }
                mDetailCommentWrapper.setVisibility(VISIBLE);
            }else{
                mDetailCommentWrapper.setVisibility(GONE);
            }
        }else if(pNewsDetail instanceof NewsDetailAdd){
            NewsDetailAdd detail = (NewsDetailAdd)pNewsDetail;
            mDetailTitle.setText(detail.title);
            mDetailDate.setText(detail.updateTime);
            if (!TextUtil.isEmptyString(detail.imgUrl)){
                mDetailHeaderImg.setVisibility(VISIBLE);
                mDetailHeaderImg.setImageURI(Uri.parse(detail.imgUrl));
            }else {
                mDetailHeaderImg.setVisibility(INVISIBLE);
                Rect rect = new Rect();
                mDetailTitle.getPaint().getTextBounds(detail.title, 0, detail.title.length(), rect);
                Logger.e("jigang", mDetailTitle.getLineCount() +" >,title h=" +mDetailTitle.getHeight() + ",w=" +mDetailDate.getWidth());
                mDetailHeaderImg.setLayoutParams(new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, DensityUtil.dip2px(mContext, 80) + mDetailTitle.getLineCount() * mDetailTitle.getHeight() + mDetailDate.getHeight()));
                mDetailTitleWrapper.setBackgroundColor(Color.parseColor("#E6E6E6"));
                mDetailTitle.setTextColor(Color.BLACK);
                mDetailDate.setTextColor(Color.BLACK);
            }
            /**语音评论和新闻描述有一个不为null*/
            if (!TextUtil.isEmptyString(detail.abs) || detail.isdoc){
                if (!TextUtil.isEmptyString(detail.abs)){
                    mDetailDesc.setText(detail.abs.replace("\n",""));
                    mDetailDesc.setVisibility(VISIBLE);
                }else {
                    mDetailDesc.setVisibility(GONE);
                }
                if (detail.isdoc){
                    mDetailSpeechComment.setUrlAndDuration(detail.docUrl,Integer.valueOf(detail.docTime),true);
                    mDetailSpeechCommentUserIcon.setImageURI(Uri.parse(detail.docUserIcon));
                    mDetailSpeechCommentWrapper.setVisibility(VISIBLE);
                }else{
                    mDetailSpeechCommentWrapper.setVisibility(GONE);
                }
                mDetailCommentWrapper.setVisibility(VISIBLE);
            }else{
                mDetailCommentWrapper.setVisibility(GONE);
            }
        }
    }
}
