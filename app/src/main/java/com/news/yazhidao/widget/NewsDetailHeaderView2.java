package com.news.yazhidao.widget;

import android.content.Context;
import android.net.Uri;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.news.yazhidao.R;
import com.news.yazhidao.entity.NewsDetail;
import com.news.yazhidao.entity.NewsDetailAdd;
import com.news.yazhidao.utils.DeviceInfoUtil;
import com.news.yazhidao.utils.TextUtil;

/**
 * Created by fengjigang on 15/9/6.
 */
public class NewsDetailHeaderView2 extends RelativeLayout {

    private final SimpleDraweeView mDetailSpeechCommentUserIcon;
    private final SpeechView mDetailSpeechComment;
    private int mScreenWidth,mScreenHeight;
    //返回上一级,全文评论,分享
    private View mDetailLeftBack,mDetailComment,mDetailShare;
    //新闻大头图
    private SimpleDraweeView mDetailHeaderImg;
    //新闻标题,新闻时间,新闻描述
    private TextView mDetailTitle,mDetailDate,mDetailDesc;
    public NewsDetailHeaderView2(Context context) {
        this(context, null);
    }

    public NewsDetailHeaderView2(Context context, AttributeSet attrs) {
        this(context, attrs, -1);
    }

    public NewsDetailHeaderView2(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mScreenWidth = DeviceInfoUtil.getScreenWidth(context);
        mScreenHeight = DeviceInfoUtil.getScreenHeight(context);
        View root = View.inflate(context, R.layout.aty_news_detail_header_view2, this);
        mDetailLeftBack = root.findViewById(R.id.mDetailLeftBack);
        mDetailComment = root.findViewById(R.id.mDetailComment);
        mDetailShare = root.findViewById(R.id.mDetailShare);
        mDetailHeaderImg = (SimpleDraweeView)root.findViewById(R.id.mDetailHeaderImg);
        mDetailHeaderImg.setLayoutParams(new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,(int)(mScreenWidth*520.0f/720)));
        mDetailTitle = (TextView)root.findViewById(R.id.mDetailTitle);
        mDetailDate = (TextView)root.findViewById(R.id.mDetailDate);
        mDetailSpeechCommentUserIcon = (SimpleDraweeView)root.findViewById(R.id.mDetailSpeechCommentUserIcon);
        mDetailSpeechComment = (SpeechView)root.findViewById(R.id.mDetailSpeechComment);
        mDetailDesc = (TextView)root.findViewById(R.id.mDetailDesc);
    }

    /**
     * 获取数据后刷新界面
     * @param pNewsDetail
     */
    public void updateView(Object pNewsDetail){
        if(pNewsDetail instanceof NewsDetail){
            NewsDetail detail = (NewsDetail)pNewsDetail;
            mDetailHeaderImg.setImageURI(Uri.parse(detail.imgUrl));
            mDetailTitle.setText(detail.title);
            mDetailDate.setText(detail.updateTime);
            mDetailDesc.setText(detail.abs);
            if (!TextUtil.isListEmpty(detail.point)){
                NewsDetail.Point comment = detail.point.get(0);
                mDetailSpeechComment.setUrlAndDuration(comment.srcText,comment.srcTextTime,true);
                mDetailSpeechCommentUserIcon.setImageURI(Uri.parse(comment.userIcon));
            }
        }else if(pNewsDetail instanceof NewsDetailAdd){
            NewsDetailAdd detail = (NewsDetailAdd)pNewsDetail;
            mDetailHeaderImg.setImageURI(Uri.parse(detail.imgUrl));
            mDetailTitle.setText(detail.title);
            mDetailDate.setText(detail.updateTime);
            mDetailDesc.setText(detail.abs);
            if (!TextUtil.isListEmpty(detail.point)){
                NewsDetail.Point comment = detail.point.get(0);
                mDetailSpeechComment.setUrlAndDuration(comment.srcText,comment.srcTextTime,true);
                mDetailSpeechCommentUserIcon.setImageURI(Uri.parse(comment.userIcon));
            }
        }
    }

    /**
     * 设置subviews 的点击事件回调
     * @param pSubView
     * @param pListener
     */
    public void setSubViewOnClickListener(View pSubView,View.OnClickListener pListener){
        if (pSubView == null || pListener == null){
            return;
        }
        switch (pSubView.getId()){
            case R.id.mDetailLeftBack:
                mDetailLeftBack.setOnClickListener(pListener);
                break;
            case R.id.mDetailComment:
                mDetailComment.setOnClickListener(pListener);
                break;
            case R.id.mDetailShare:
                mDetailShare.setOnClickListener(pListener);
                break;
        }
    }

    public void setCommentListener(View.OnClickListener pListener){
        mDetailComment.setOnClickListener(pListener);
    }

    public void setShareListener(View.OnClickListener pListener){
        mDetailShare.setOnClickListener(pListener);
    }
}
