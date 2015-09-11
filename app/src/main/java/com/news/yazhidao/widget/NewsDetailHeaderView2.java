package com.news.yazhidao.widget;

import android.content.Context;
import android.graphics.PointF;
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
    //返回上一级,全文评论,分享,语音评论父容器,评论父容器
    private View mDetailLeftBack,mDetailComment,mDetailShare,mDetailSpeechCommentWrapper,mDetailCommentWrapper;
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
        mDetailHeaderImg.getHierarchy().setActualImageFocusPoint(new PointF(.5f,0.35f));
        mDetailHeaderImg.setLayoutParams(new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,(int)(mScreenWidth*520.0f/720)));
        mDetailTitle = (TextView)root.findViewById(R.id.mDetailTitle);
        mDetailDate = (TextView)root.findViewById(R.id.mDetailDate);
        mDetailCommentWrapper = root.findViewById(R.id.mDetailCommentWrapper);
        mDetailSpeechCommentWrapper = root.findViewById(R.id.mDetailSpeechCommentWrapper);
        mDetailSpeechCommentUserIcon = (SimpleDraweeView)root.findViewById(R.id.mDetailSpeechCommentUserIcon);
        mDetailSpeechComment = (SpeechView)root.findViewById(R.id.mDetailSpeechComment);
        mDetailDesc = (TextView)root.findViewById(R.id.mDetailDesc);
    }

    /**
     * 获取数据后刷新界面
     * @param pNewsDetail
     */
    public void updateView(Object pNewsDetail){
        //FIXME 等接口改后,此处就不用这么费劲来写了,可以简化
        if(pNewsDetail instanceof NewsDetail){
            NewsDetail detail = (NewsDetail)pNewsDetail;
            mDetailHeaderImg.setImageURI(Uri.parse(detail.imgUrl));
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
            if (!TextUtil.isEmptyString(detail.imgUrl)){
                mDetailHeaderImg.setImageURI(Uri.parse(detail.imgUrl));
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
    public void setLeftBackListener(View.OnClickListener pListener){
        mDetailLeftBack.setOnClickListener(pListener);
    }
}
