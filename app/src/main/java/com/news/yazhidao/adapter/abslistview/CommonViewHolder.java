package com.news.yazhidao.adapter.abslistview;

import android.content.Context;
import android.graphics.PointF;
import android.net.Uri;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.facebook.drawee.view.SimpleDraweeView;
import com.news.yazhidao.utils.TextUtil;
import com.news.yazhidao.widget.TextViewExtend;

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
        if (mConvertView == null){
            View itemView = LayoutInflater.from(mContext).inflate(mLayoutId, mViewGroup, false);
            CommonViewHolder viewHolder = new CommonViewHolder(mContext, itemView, mViewGroup, mPosition);
            viewHolder.mLayoutId = mLayoutId;
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

    public void setTextViewExtendText(int ViewID ,String content){
        TextViewExtend text = getView(ViewID);
        text.setText(content);
    }
    public void setTextViewExtendTextColor(int ViewID ,int color){
        TextViewExtend text = getView(ViewID);
        text.setTextColor(mContext.getResources().getColor(color));
    }
    public void setTextViewExtendTextBackground(int ViewID ,int color){
        TextViewExtend text = getView(ViewID);
        text.setBackgroundColor(mContext.getResources().getColor(color));
    }
    public void setTextViewExtendTextBackgroundResource(int ViewID ,int resource){
        TextViewExtend text = getView(ViewID);
        text.setBackgroundResource(resource);
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
