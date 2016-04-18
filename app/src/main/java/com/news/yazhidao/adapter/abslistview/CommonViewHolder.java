package com.news.yazhidao.adapter.abslistview;

import android.content.Context;
import android.graphics.PointF;
import android.net.Uri;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.facebook.drawee.view.SimpleDraweeView;
import com.news.yazhidao.adapter.NewsFeedAdapter;
import com.news.yazhidao.utils.TextUtil;

/**
 * Created by fengjigang on 16/4/14.
 * ListView GridView 通用ViewHolder
 */
public class CommonViewHolder {
    private SparseArray<View> mViews;
    private int mPosition;
    private Context mContext;
    private View mConvertView;
    private int mLayoutId;
    public CommonViewHolder(Context mContext, View mConvertView, ViewGroup mViewGroup, int mPosition) {
        this.mContext = mContext;
        this.mConvertView = mConvertView;
        this.mPosition = mPosition;
        this.mViews = new SparseArray<>();
        mConvertView.setTag(this);
    }

    public static CommonViewHolder get(Context mContext, View mConvertView, ViewGroup mViewGroup,int mLayoutId, int mPosition){
//        View itemView = LayoutInflater.from(mContext).inflate(mLayoutId, mViewGroup, false);
//        CommonViewHolder viewHolder = new CommonViewHolder(mContext, itemView, mViewGroup, mPosition);
//        viewHolder.mLayoutId = mLayoutId;
//        return viewHolder;
        int tempLayout = 0;
        boolean needInflate = false;
        if (mConvertView != null){
            CommonViewHolder viewHolder = (CommonViewHolder) mConvertView.getTag();
            tempLayout = viewHolder.getLayoutId();
        }
        for (Integer layoutId: NewsFeedAdapter.mSaveData){
            if (layoutId == tempLayout){
                needInflate = true;
                break;
            }
        }
        if (mConvertView == null || needInflate){
            View itemView = LayoutInflater.from(mContext).inflate(mLayoutId, mViewGroup, false);
            CommonViewHolder viewHolder = new CommonViewHolder(mContext, itemView, mViewGroup, mPosition);
            viewHolder.mLayoutId = mLayoutId;
            NewsFeedAdapter.mSaveData.clear();
            return viewHolder;
        }else {
            CommonViewHolder viewHolder = (CommonViewHolder) mConvertView.getTag();
            viewHolder.mPosition = mPosition;
            return viewHolder;
        }
    }

    public <T extends View> T getView(int viewId)
    {
        View view = mViews.get(viewId);
        if (view == null)
        {
            view = mConvertView.findViewById(viewId);
            mViews.put(viewId, view);
        }
        return (T) view;
    }

    public View getConvertView()
    {
        return mConvertView;
    }

    public int getLayoutId()
    {
        return mLayoutId;
    }

    public void setSimpleDraweeViewURI(int draweeView, String strImg) {
        SimpleDraweeView imageView = (SimpleDraweeView)getView(draweeView);
        if (!TextUtil.isEmptyString(strImg)) {
            imageView.setImageURI(Uri.parse(strImg));
            imageView.getHierarchy().setActualImageFocusPoint(new PointF(0.5F, 0.4F));
        }
    }
}
