package com.news.yazhidao.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.PointF;
import android.net.Uri;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.news.yazhidao.R;
import com.news.yazhidao.entity.NewsDetail;
import com.news.yazhidao.entity.NewsDetailContent;
import com.news.yazhidao.entity.NewsDetailEntry;
import com.news.yazhidao.entity.NewsDetailImageWall;
import com.news.yazhidao.utils.DensityUtil;
import com.news.yazhidao.utils.DeviceInfoUtil;
import com.news.yazhidao.utils.TextUtil;
import com.news.yazhidao.utils.ToastUtil;
import com.news.yazhidao.widget.TextViewExtend;
import com.news.yazhidao.widget.imagewall.WallActivity;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by fengjigang on 15/9/7.
 * 新闻详情页中 ExpandableListView 对应的adapter
 */
public class NewsDetailELVAdapter extends BaseExpandableListAdapter implements View.OnClickListener {
    /**
     * viewholder 的类型
     */
    public enum ViewHolderType{
        CONTENT,IMAGEWALL, DIFFERENT_OPINION, SELECTION_COMMENT,NEWS_ENTRY,RELATE_OPINION,WEIBO,ZHIHU
    }
    private int mSreenWidth,mSreenHeight;
    private Context mContext;
    private ArrayList<ArrayList> mNewsContentDataList;
    private ArrayList<HashMap<String, String>> mImageWallMap;

    private GroupViewHolder mGroupViewHolder;
    private ContentViewHolder mContentViewHolder;
    private ImageWallHolder mImageWallHolder;
    private DifferentOpinionViewHolder mDiffOpinionHolder;
    private SelectionCommentViewHolder mSelCommentHolder;
    private NewsEntryViewHolder mEntryHolder;
    private RelateOpinionViewHolder mRelateHolder;
    private WeiboViewHolder mWeiboViewHolder;
    private ZhiHuViewHolder mZhiHuHolder;
    public NewsDetailELVAdapter(Context pContext, ArrayList<ArrayList> pNewsContentDataList){
        mContext = pContext;
        this.mSreenWidth = DeviceInfoUtil.getScreenWidth(pContext);
        this.mSreenHeight = DeviceInfoUtil.getScreenHeight(pContext);
        this.mNewsContentDataList = pNewsContentDataList;
    }

    public void setmNewsContentDataList(ArrayList<ArrayList> mNewsContentDataList) {
        this.mNewsContentDataList = mNewsContentDataList;
        this.notifyDataSetChanged();
    }

    @Override
    public int getGroupCount() {
        return mNewsContentDataList == null ? 0 : mNewsContentDataList.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return mNewsContentDataList.get(groupPosition).size();
    }

    @Override
    public Object getGroup(int groupPosition) {

        return mNewsContentDataList.get(groupPosition);
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return mNewsContentDataList.get(groupPosition).get(childPosition);
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        switch (getViewHolderType(groupPosition)){
            case CONTENT:
            case IMAGEWALL:
            case DIFFERENT_OPINION:
            case SELECTION_COMMENT:
                convertView =  new FrameLayout(mContext);
                break;
            case NEWS_ENTRY:
            case RELATE_OPINION:
            case WEIBO:
            case ZHIHU:
                if (convertView == null || !(convertView.getTag() instanceof GroupViewHolder)){
                    mGroupViewHolder = new GroupViewHolder();
                    convertView = View.inflate(mContext, R.layout.item_news_detail_comment_group, null);
                    mGroupViewHolder.mDetailCommentGroupTitle = (TextView)convertView.findViewById(R.id.mDetailCommentGroupTitle);
                    convertView.setTag(mGroupViewHolder);
                }else {
                    mGroupViewHolder = (GroupViewHolder) convertView.getTag();
                }
                String title = null;
                switch (getViewHolderType(groupPosition)){
                    case NEWS_ENTRY:
                        title = "新闻词条";
                        break;
                    case RELATE_OPINION:
                        title = "相关观点";
                        break;
                    case WEIBO:
                        title = "微博热点";
                        break;
                    case ZHIHU:
                        title = "知乎推荐";
                        break;
                }
                mGroupViewHolder.mDetailCommentGroupTitle.setText(title);
        }
        return convertView;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {

        /**新闻内容组*/
        if (getViewHolderType(groupPosition) == ViewHolderType.CONTENT) {
            ArrayList arrayList = mNewsContentDataList.get(groupPosition);
            if (convertView == null || !ContentViewHolder.class.equals(convertView.getTag().getClass())) {
                mContentViewHolder = new ContentViewHolder();
                convertView = View.inflate(mContext, R.layout.item_news_detail_content, null);
                mContentViewHolder.mDetailContent = (TextView) convertView.findViewById(R.id.mDetailContent);
                mContentViewHolder.mDetailCommentCount = (TextView) convertView.findViewById(R.id.mDetailCommentCount);
                mContentViewHolder.mDetailAddComment = (ImageView) convertView.findViewById(R.id.mDetailAddComment);
                mContentViewHolder.mNewsDetailContentWrapper = convertView.findViewById(R.id.mNewsDetailContentWrapper);
                mContentViewHolder.mDetailGroupDivider = convertView.findViewById(R.id.mDetailGroupDivider);
                convertView.setTag(mContentViewHolder);
            } else {
                mContentViewHolder = (ContentViewHolder) convertView.getTag();
            }
            NewsDetailContent content = (NewsDetailContent) arrayList.get(childPosition);
            /**设置新闻内容每一个item的背景,针对圆角*/
            if (arrayList.size() == 1){
                /**判断是否有只有当前一个组*/
                if (mNewsContentDataList.size() > 1){
                    mContentViewHolder.mNewsDetailContentWrapper.setBackgroundResource(R.drawable.bg_item_news_detail_content_header);
                }else {
                    mContentViewHolder.mNewsDetailContentWrapper.setBackgroundResource(R.drawable.bg_item_news_detail_content);
                }
            }else if(arrayList.size() > 1){
                if (childPosition == 0){
                    mContentViewHolder.mNewsDetailContentWrapper.setBackgroundResource(R.drawable.bg_item_news_detail_content_header);
                }else if(childPosition == arrayList.size() - 1){
                    if (mNewsContentDataList.size() > 1){
                        mContentViewHolder.mNewsDetailContentWrapper.setBackgroundResource(R.drawable.bg_item_news_detail_content_footer);
                    }else {
                        mContentViewHolder.mNewsDetailContentWrapper.setBackgroundResource(R.drawable.bg_item_news_detail_content_header);
                    }
                }else {
                    mContentViewHolder.mNewsDetailContentWrapper.setBackgroundResource(R.drawable.bg_item_news_detail_content_middle);
                }
            }
            mContentViewHolder.mNewsDetailContentWrapper.setPadding(DensityUtil.dip2px(mContext,22),DensityUtil.dip2px(mContext,12),DensityUtil.dip2px(mContext,22),0);
            /**设置分组之间的间隔*/
            mContentViewHolder.mDetailGroupDivider.setVisibility(childPosition == 0 ?View.VISIBLE : View.GONE);
            /**设置新闻详情*/
            mContentViewHolder.mDetailContent.setText(content.getContent());
            /**设置该段落的评论数*/
            if (!TextUtil.isListEmpty(content.getComments())) {
                mContentViewHolder.mDetailCommentCount.setText("" + content.getComments().size());
                mContentViewHolder.mDetailCommentCount.setVisibility(View.VISIBLE);
                mContentViewHolder.mDetailAddComment.setVisibility(View.GONE);
                mContentViewHolder.mDetailCommentCount.setOnClickListener(NewsDetailELVAdapter.this);
            } else {
                mContentViewHolder.mDetailCommentCount.setVisibility(View.GONE);
                mContentViewHolder.mDetailAddComment.setVisibility(View.VISIBLE);
                mContentViewHolder.mDetailAddComment.setOnClickListener(NewsDetailELVAdapter.this);
            }

        } else if(getViewHolderType(groupPosition) == ViewHolderType.IMAGEWALL){
            /**图片墙组*/
            if (convertView == null || !ImageWallHolder.class.getClasses().equals(convertView.getTag().getClass())){
                mImageWallHolder = new ImageWallHolder();
                convertView = View.inflate(mContext,R.layout.item_news_detail_imagewall,null);
                mImageWallHolder.mDetailImageWallImg = (SimpleDraweeView) convertView.findViewById(R.id.mDetailImageWallImg);
                mImageWallHolder.mDetailImageWallImg.getHierarchy().setActualImageFocusPoint(new PointF(.5f,.35f));
                mImageWallHolder.mDetailImageWallImg.setLayoutParams(new RelativeLayout.LayoutParams(mSreenWidth - DensityUtil.dip2px(mContext,20), (int) (370.f / 720 * mSreenWidth)));
                mImageWallHolder.mDetailImageWallCount = (TextView) convertView.findViewById(R.id.mDetailImageWallCount);
                convertView.setTag(mImageWallHolder);
            }else {
                mImageWallHolder = (ImageWallHolder) convertView.getTag();
            }
            ArrayList<NewsDetailImageWall> imageWalls = mNewsContentDataList.get(groupPosition);
            NewsDetailImageWall imageWall = imageWalls.get(0);
            this.mImageWallMap = imageWall.getImgWall();
            HashMap<String, String> imageMap = mImageWallMap.get(0);
            mImageWallHolder.mDetailImageWallImg.setImageURI(Uri.parse(imageMap.get("img")));
            mImageWallHolder.mDetailImageWallCount.setText("" + mImageWallMap.size());
            convertView.setOnClickListener(NewsDetailELVAdapter.this);
        }else if (getViewHolderType(groupPosition) == ViewHolderType.DIFFERENT_OPINION){
            /**差异化观点组*/
            if (convertView == null || !DifferentOpinionViewHolder.class.getClasses().equals(convertView.getTag().getClass())){
                mDiffOpinionHolder = new DifferentOpinionViewHolder();
                convertView = View.inflate(mContext, R.layout.item_news_detail_different_opinion, null);
                mDiffOpinionHolder.mDetailDifferOpinionDivider = convertView.findViewById(R.id.mDetailDifferOpinionDivider);
                mDiffOpinionHolder.mDetailDiffOpinionContent = (TextView) convertView.findViewById(R.id.mDetailDiffOpinionContent);
                mDiffOpinionHolder.mDetailDiffOpinionTitle = (TextView) convertView.findViewById(R.id.mDetailDiffOpinionTitle);
                convertView.setTag(mDiffOpinionHolder);
            }else {
                mDiffOpinionHolder = (DifferentOpinionViewHolder) convertView.getTag();
            }
            ArrayList<NewsDetail.Article> list = mNewsContentDataList.get(groupPosition);
            NewsDetail.Article opinion = list.get(childPosition);
            mDiffOpinionHolder.mDetailDiffOpinionContent.setText(opinion.self_opinion);
            mDiffOpinionHolder.mDetailDiffOpinionTitle.setText(opinion.title);

            //设置点击事件
            convertView.setOnClickListener(NewsDetailELVAdapter.this);
        }else if(getViewHolderType(groupPosition) == ViewHolderType.SELECTION_COMMENT){
            /**精选评论组*/
            if (convertView == null || !SelectionCommentViewHolder.class.getClasses().equals(convertView.getTag().getClass())){
                mSelCommentHolder = new SelectionCommentViewHolder();
                convertView = View.inflate(mContext,R.layout.item_news_detail_comment,null);
                mSelCommentHolder.mDetailCommentDivider = convertView.findViewById(R.id.mDetailCommentDivider);
                mSelCommentHolder.mDetailCommentItemWrapper = convertView.findViewById(R.id.mDetailCommentItemWrapper);
                mSelCommentHolder.mDetailCommentUserIcon = (SimpleDraweeView)convertView.findViewById(R.id.mDetailCommentUserIcon);
                mSelCommentHolder.mDetailCommentUserName = (TextView)convertView.findViewById(R.id.mDetailCommentUserName);
                mSelCommentHolder.mDetailCommentUserSpeech = (TextView)convertView.findViewById(R.id.mDetailCommentUserSpeech);
                mSelCommentHolder.mDetailCommentPraiseWrapper = convertView.findViewById(R.id.mDetailCommentPraiseWrapper);
                mSelCommentHolder.mDetailCommentPraise = (ImageView)convertView.findViewById(R.id.mDetailCommentPraise);
                mSelCommentHolder.mDetailCommentPraiseNum = (TextView)convertView.findViewById(R.id.mDetailCommentPraiseNum);
                mSelCommentHolder.mDetailCommentCheckAll = (TextView)convertView.findViewById(R.id.mDetailCommentCheckAll);
                convertView.setTag(mSelCommentHolder);
            }else {
                mSelCommentHolder = (SelectionCommentViewHolder) convertView.getTag();
            }
            ArrayList<NewsDetail.Point> list = mNewsContentDataList.get(groupPosition);
            NewsDetail.Point point = list.get(childPosition);
            /**设置评论内容每一个item的背景,针对圆角*/
            if (list.size() == 1){
                mSelCommentHolder.mDetailCommentItemWrapper.setBackgroundResource(R.drawable.bg_item_news_detail_content);
            }else {
                if (childPosition == 0){
                    mSelCommentHolder.mDetailCommentItemWrapper.setBackgroundResource(R.drawable.bg_item_news_detail_content_header);
                }else if (childPosition == list.size() - 1){
                    mSelCommentHolder.mDetailCommentItemWrapper.setBackgroundResource(R.drawable.bg_item_news_detail_content_footer);
                }else {
                    mSelCommentHolder.mDetailCommentItemWrapper.setBackgroundResource(R.drawable.bg_item_news_detail_content_middle);
                }
            }
            if (childPosition != list.size() - 1){
                mSelCommentHolder.mDetailCommentUserIcon.setImageURI(Uri.parse(point.userIcon));
                mSelCommentHolder.mDetailCommentUserName.setText(point.userName);
                mSelCommentHolder.mDetailCommentUserSpeech.setText(point.srcText);
                mSelCommentHolder.mDetailCommentPraiseNum.setText(point.up);
                mSelCommentHolder.mDetailCommentCheckAll.setVisibility(View.GONE);
                mSelCommentHolder.mDetailCommentItemWrapper.setVisibility(View.VISIBLE);
            }else {
                mSelCommentHolder.mDetailCommentCheckAll.setVisibility(View.VISIBLE);
                mSelCommentHolder.mDetailCommentItemWrapper.setVisibility(View.GONE);
            }
            mSelCommentHolder.mDetailCommentDivider.setVisibility(childPosition == 0 ? View.VISIBLE : View.GONE);
            mSelCommentHolder.mDetailCommentItemWrapper.setPadding(DensityUtil.dip2px(mContext, 26), 0, DensityUtil.dip2px(mContext, 26), 0);
            mSelCommentHolder.mDetailCommentPraiseWrapper.setOnClickListener(NewsDetailELVAdapter.this);
            mSelCommentHolder.mDetailCommentCheckAll.setOnClickListener(NewsDetailELVAdapter.this);
        }else if (getViewHolderType(groupPosition) == ViewHolderType.NEWS_ENTRY){
            /**新闻词条组*/
            if (convertView == null || !NewsEntryViewHolder.class.getClasses().equals(convertView.getTag().getClass())){
                mEntryHolder = new NewsEntryViewHolder();
                convertView = View.inflate(mContext, R.layout.item_news_detail_entry, null);
                mEntryHolder.mDetailEntryTitle = (TextView)convertView.findViewById(R.id.mDetailEntryTitle);
                mEntryHolder.mDetailEntryIcon = (ImageView)convertView.findViewById(R.id.mDetailEntryIcon);
                convertView.setTag(mEntryHolder);
            }else {
                mEntryHolder = (NewsEntryViewHolder) convertView.getTag();
            }
            ArrayList<NewsDetailEntry> list = mNewsContentDataList.get(groupPosition);
            NewsDetailEntry entry = list.get(childPosition);
            if (list.size() == 1){
                convertView.setBackgroundResource(R.drawable.bg_item_news_detail_content_footer);
            }else {
                if (childPosition != list.size() - 1){
                    convertView.setBackgroundResource(R.drawable.bg_item_news_detail_content_middle);
                }else {
                    convertView.setBackgroundResource(R.drawable.bg_item_news_detail_content_footer);
                }
            }
            mEntryHolder.mDetailEntryTitle.setText(entry.getTitle());
            mEntryHolder.mDetailEntryIcon.setImageResource(entry.getType() == NewsDetailEntry.EntyType.BAIDUBAIKE ? R.drawable.ic_news_detail_entry_baike : R.drawable.ic_news_detail_entry_douban);
            convertView.setPadding(DensityUtil.dip2px(mContext, 22), DensityUtil.dip2px(mContext, 16), 0, 0);
            convertView.setOnClickListener(NewsDetailELVAdapter.this);
        }else if (getViewHolderType(groupPosition) == ViewHolderType.RELATE_OPINION){
            /**相关观点组*/
            if (convertView == null || !RelateOpinionViewHolder.class.getClasses().equals(convertView.getTag().getClass())){
                mRelateHolder = new RelateOpinionViewHolder();
                convertView = View.inflate(mContext,R.layout.item_news_detail_relate_opinion,null);
                mRelateHolder.mDetailRelateOpinionTime = (TextViewExtend) convertView.findViewById(R.id.mDetailRelateOpinionTime);
                mRelateHolder.mDetailRelateOpinionContent = (TextViewExtend) convertView.findViewById(R.id.mDetailRelateOpinionContent);
                mRelateHolder.mDetailRelateOpinionImg = (SimpleDraweeView) convertView.findViewById(R.id.mDetailRelateOpinionImg);
                convertView.setTag(mRelateHolder);
            }else {
                mRelateHolder = (RelateOpinionViewHolder) convertView.getTag();
            }
            ArrayList<NewsDetail.Relate> list = mNewsContentDataList.get(groupPosition);
            NewsDetail.Relate relate = list.get(childPosition);
            mRelateHolder.mDetailRelateOpinionTime.setText(relate.updateTime.substring(5, 10).replace("-", "."));
            mRelateHolder.mDetailRelateOpinionContent.setText(relate.title);
            mRelateHolder.mDetailRelateOpinionImg.setImageURI(Uri.parse(relate.img));
            if (list.size() == 1){
                convertView.setBackgroundResource(R.drawable.bg_item_news_detail_content_footer);
            }else {
                if (childPosition != list.size() - 1){
                    convertView.setBackgroundResource(R.drawable.bg_item_news_detail_content_middle);
                }else {
                    convertView.setBackgroundResource(R.drawable.bg_item_news_detail_content_footer);
                }
            }
            convertView.setPadding(DensityUtil.dip2px(mContext,22),0,DensityUtil.dip2px(mContext,22),0);
            convertView.setOnClickListener(NewsDetailELVAdapter.this);
        }else if (getViewHolderType(groupPosition) == ViewHolderType.WEIBO){
            /**微博组*/
            if (convertView == null || !WeiboViewHolder.class.getClasses().equals(convertView.getTag().getClass())){
                mWeiboViewHolder = new WeiboViewHolder();
                convertView = View.inflate(mContext,R.layout.item_news_detail_weibo,null);
                mWeiboViewHolder.mDetailWeiboUserIcon = (SimpleDraweeView)convertView.findViewById(R.id.mDetailWeiboUserIcon);
                mWeiboViewHolder.mDetailWeiboUserName = (TextView)convertView.findViewById(R.id.mDetailWeiboUserName);
                mWeiboViewHolder.mDetailWeiboUserSpeech = (TextView)convertView.findViewById(R.id.mDetailWeiboUserSpeech);
                mWeiboViewHolder.mDetailWeiboImg = (SimpleDraweeView)convertView.findViewById(R.id.mDetailWeiboImg);
                convertView.setTag(mWeiboViewHolder);
            }else {
                mWeiboViewHolder = (WeiboViewHolder) convertView.getTag();
            }
            ArrayList<NewsDetail.Weibo> list = mNewsContentDataList.get(groupPosition);
            NewsDetail.Weibo weibo = list.get(childPosition);
            if (list.size() == 1){
                convertView.setBackgroundResource(R.drawable.bg_item_news_detail_content_footer);
            }else {
                if (childPosition != list.size() - 1){
                    convertView.setBackgroundResource(R.drawable.bg_item_news_detail_content_middle);
                }else {
                    convertView.setBackgroundResource(R.drawable.bg_item_news_detail_content_footer);
                }
            }
            mWeiboViewHolder.mDetailWeiboUserIcon.setImageURI(Uri.parse(weibo.profileImageUrl));
            mWeiboViewHolder.mDetailWeiboUserName.setText(weibo.user);
            mWeiboViewHolder.mDetailWeiboUserSpeech.setText(weibo.title);
            if (!TextUtil.isEmptyString(weibo.img)){
                mWeiboViewHolder.mDetailWeiboImg.setImageURI(Uri.parse(weibo.img));
                mWeiboViewHolder.mDetailWeiboImg.setVisibility(View.VISIBLE);
            }else {
                mWeiboViewHolder.mDetailWeiboImg.setVisibility(View.GONE);

            }
            convertView.setPadding(DensityUtil.dip2px(mContext,22),0,DensityUtil.dip2px(mContext,22),0);
            convertView.setOnClickListener(NewsDetailELVAdapter.this);
        }else if (getViewHolderType(groupPosition) == ViewHolderType.ZHIHU){
            /**知乎组*/
            if (convertView == null || !ZhiHuViewHolder.class.getClasses().equals(convertView.getTag().getClass())){
                mZhiHuHolder = new ZhiHuViewHolder();
                convertView = View.inflate(mContext,R.layout.item_news_detail_zhihu,null);
                mZhiHuHolder.mDetailZhiHuTitle = (TextView)convertView.findViewById(R.id.mDetailZhiHuTitle);
                mZhiHuHolder.mDetailZhiHuDivider = (View)convertView.findViewById(R.id.mDetailZhiHuDivider);
                convertView.setTag(mZhiHuHolder);
            }else {
                mZhiHuHolder = (ZhiHuViewHolder) convertView.getTag();
            }
            ArrayList<NewsDetail.ZhiHu> list = mNewsContentDataList.get(groupPosition);
            NewsDetail.ZhiHu zhihu = list.get(childPosition);
            mZhiHuHolder.mDetailZhiHuTitle.setText(zhihu.title);
            if (list.size() == 1){
                convertView.setBackgroundResource(R.drawable.bg_item_news_detail_content_footer);
            }else {
                if (childPosition != list.size() - 1){
                    convertView.setBackgroundResource(R.drawable.bg_item_news_detail_content_middle);
                }else {
                    convertView.setBackgroundResource(R.drawable.bg_item_news_detail_content_footer);
                }
            }
            convertView.setPadding(DensityUtil.dip2px(mContext,22),0,DensityUtil.dip2px(mContext,22),0);
            convertView.setOnClickListener(NewsDetailELVAdapter.this);
        }
        return convertView;
    }

    /**
     * 根据分组索引 判断其对应的ViewHolder
     * @param pPosition
     * @return
     */
    public ViewHolderType getViewHolderType(int pPosition){
        ArrayList arrayList = mNewsContentDataList.get(pPosition);
        Object obj = arrayList.get(0);
        if (obj instanceof NewsDetailContent){
            return ViewHolderType.CONTENT;
        }else if (obj instanceof NewsDetailImageWall){
            return ViewHolderType.IMAGEWALL;
        }else if (obj instanceof NewsDetail.Article){
            return ViewHolderType.DIFFERENT_OPINION;
        }else if (obj instanceof NewsDetail.Point){
            return ViewHolderType.SELECTION_COMMENT;
        }else if (obj instanceof NewsDetailEntry){
            return ViewHolderType.NEWS_ENTRY;
        }else if (obj instanceof NewsDetail.Relate){
            return ViewHolderType.RELATE_OPINION;
        }else if (obj instanceof NewsDetail.Weibo){
            return ViewHolderType.WEIBO;
        }else if (obj instanceof NewsDetail.ZhiHu){
            return ViewHolderType.ZHIHU;
        }
        return ViewHolderType.CONTENT;
    }
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.mDetailCommentCount:
                ToastUtil.toastShort("onclik count comment");
                break;
            case R.id.mDetailAddComment:
                ToastUtil.toastShort("onclik add comment");
                break;
            case R.id.mDetailImageWallWrapper:
                Intent imageWallIntent = new Intent(mContext, WallActivity.class);
                imageWallIntent.putExtra(WallActivity.KEY_IMAGE_WALL_DATA,mImageWallMap);
                mContext.startActivity(imageWallIntent);
                break;
            case R.id.mDetailDiffOpinionWrapper:
                ToastUtil.toastShort("onclik different opinoin");
                break;
            case R.id.mDetailCommentPraiseWrapper:
                ToastUtil.toastShort("点赞~~~");
                break;
            case R.id.mDetailCommentCheckAll:
                ToastUtil.toastShort("查看全部评论");
                break;
            case R.id.mDetailEntryWrapper:
                ToastUtil.toastShort("查看新闻词条");
                break;
            case R.id.mDetailRelateOpinionWrapper:
                ToastUtil.toastShort("相关观点");
                break;
            case R.id.mDetailZhiHuWrapper:
                ToastUtil.toastShort("知乎推荐");
                break;
        }
    }

    /**
     * 所有分组通用 ViewHolder
     */
    static class GroupViewHolder {
        TextView mDetailCommentGroupTitle;
    }
    /**
     * 新闻内容 ViewHolder
     */
    static class ContentViewHolder {
        TextView mDetailContent;
        TextView mDetailCommentCount;
        ImageView mDetailAddComment;
        View mNewsDetailContentWrapper;
        View mDetailGroupDivider;
    }

    /**
     * 图片墙 ViewHolder
     */
    static class ImageWallHolder {
        SimpleDraweeView mDetailImageWallImg;
        TextView mDetailImageWallCount;
    }

    /**
     * 差异化观点 ViewHolder
     */
    static class DifferentOpinionViewHolder {
         View mDetailDifferOpinionDivider;
         TextView mDetailDiffOpinionContent;
         TextView mDetailDiffOpinionTitle;
    }

    /**
     * 精选评论 ViewHolder
     */
    static class SelectionCommentViewHolder {
        View mDetailCommentDivider;
        View mDetailCommentItemWrapper;
        SimpleDraweeView mDetailCommentUserIcon;
        TextView mDetailCommentUserName;
        TextView mDetailCommentUserSpeech;
        View mDetailCommentPraiseWrapper;
        ImageView mDetailCommentPraise;
        TextView mDetailCommentPraiseNum;
        TextView mDetailCommentCheckAll;
    }

    /**
     * 新闻词条(百度百科,豆瓣) ViewHolder
     */
    static class NewsEntryViewHolder {
        TextView mDetailEntryTitle;
        ImageView mDetailEntryIcon;
    }

    /**
     * 新闻相关观点  ViewHolder
     */
    static class RelateOpinionViewHolder {
        TextViewExtend mDetailRelateOpinionTime;
        RelativeLayout lineLayout;
        ImageView roundedImageView;
        ImageView topLineImageView;
        ImageView lineBottomImageView;
        SimpleDraweeView mDetailRelateOpinionImg;
        TextViewExtend mDetailRelateOpinionContent;
    }

    /**
     * weibo ViewHolder
     */
    static class WeiboViewHolder {
        SimpleDraweeView mDetailWeiboUserIcon;
        TextView mDetailWeiboUserName;
        TextView mDetailWeiboUserSpeech;
        SimpleDraweeView mDetailWeiboImg;
    }

    /**
     * 知乎ViewHolder
     */
    static class ZhiHuViewHolder {
        ImageView roundedImageView;
        TextView mDetailZhiHuTitle;
        View mDetailZhiHuDivider;

    }
}
