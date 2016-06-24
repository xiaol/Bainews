package com.news.yazhidao.adapter;

import android.content.Context;
import android.view.View;
import android.widget.RelativeLayout;

import com.news.yazhidao.R;
import com.news.yazhidao.adapter.abslistview.CommonAdapter;
import com.news.yazhidao.adapter.abslistview.CommonViewHolder;
import com.news.yazhidao.entity.HistoryEntity;
import com.news.yazhidao.entity.NewsFeed;

import java.util.ArrayList;

/**
 * Created by Administrator on 2016/5/11.
 */
public class SearchListViewOpenAdapter extends CommonAdapter<HistoryEntity> {
    public SearchListViewOpenAdapter(Context mContext) {
        super(R.layout.search_listviewopen_item, mContext, null);
    }

    @Override
    public void convert(CommonViewHolder holder, HistoryEntity historyEntity, int position) {
        if(historyEntity.getPosition() == -1){
            holder.getView(R.id.search_listviewopen_itemLayout).setVisibility(View.GONE);
        }else{
            if(holder.getView(R.id.search_listviewopen_itemLayout).getVisibility() == View.GONE){
                holder.getView(R.id.search_listviewopen_itemLayout).setVisibility(View.VISIBLE);
            }
        }
        holder.setTextViewExtendText(R.id.search_listviewopen_item_content,historyEntity.getCotent());
        setItemClick((RelativeLayout) holder.getView(R.id.search_listviewopen_itemLayout),historyEntity.getCotent());

    }
    public void setItemClick(RelativeLayout layout, final String content){
        layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mOnSearchListViewOpenItemClick.listener(content);

            }
        });
    }

    private onSearchListViewOpenItemClick mOnSearchListViewOpenItemClick;
    public void setonSearchListViewOpenItemClick(onSearchListViewOpenItemClick mOnSearchListViewOpenItemClick){
        this.mOnSearchListViewOpenItemClick = mOnSearchListViewOpenItemClick;
    }
    public interface onSearchListViewOpenItemClick{
        public void listener(String content);
    }
}
