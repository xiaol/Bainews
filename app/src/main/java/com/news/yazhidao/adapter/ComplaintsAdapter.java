package com.news.yazhidao.adapter;

import android.content.Context;
import android.view.View;
import android.widget.RelativeLayout;

import com.news.yazhidao.R;
import com.news.yazhidao.adapter.abslistview.CommonAdapter;
import com.news.yazhidao.adapter.abslistview.CommonViewHolder;

import java.util.ArrayList;

/**
 * Created by Administrator on 2016/6/16.
 */

public class ComplaintsAdapter extends CommonAdapter<String> {
    private Context mContext;
    private int isCheck =-1;
    private SetItemChangeBackgroundListener setItemChangeBackgroundListener;
    public ComplaintsAdapter(Context context, ArrayList<String> datas,SetItemChangeBackgroundListener setItemChangeBackgroundListener) {
        super(R.layout.complaints_item,context, datas);
        mContext = context;
        this.setItemChangeBackgroundListener = setItemChangeBackgroundListener;
    }




    @Override
    public void convert(CommonViewHolder holder, String str, int position) {
        holder.setTextViewExtendTextandTextSice(R.id.complaintsItem_Name, str);
        if(isCheck == position ){
            holder.getView(R.id.complaintsItem_checkImage).setVisibility(View.VISIBLE);
        }else{
            holder.getView(R.id.complaintsItem_checkImage).setVisibility(View.GONE);
        }
        if(getCount()-1 == position){
            holder.getView(R.id.complaintsItem_line).setVisibility(View.GONE);
        }else{
            holder.getView(R.id.complaintsItem_line).setVisibility(View.VISIBLE);
        }
        onComplaintsItemClick((RelativeLayout) holder.getView(R.id.complaintsItem_layout), position);
    }
    public void onComplaintsItemClick(RelativeLayout ComplaintsItemLayout,final int position){
        ComplaintsItemLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(isCheck == -1){//保证他只调用一次
                    setItemChangeBackgroundListener.listener();
                }
                isCheck = position;
                notifyDataSetChanged();
            }
        });
    }
    public interface SetItemChangeBackgroundListener{
        public void listener();
    }
}
