package com.news.yazhidao.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.news.yazhidao.R;


/**
 * Created by h.yuan on 2015/3/23.
 */
public class DouBanGridView extends FrameLayout {

    private View mRootView;
    private Context mContext;
    private NewsGridView mgvDouBan;
    private DouBanAdapter mAdapter;
    int i =9;


    public DouBanGridView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mRootView = inflater.inflate(R.layout.grid_douban, null);
        addView(mRootView);
        initVars();
        findViews();
    }

    public DouBanGridView(Context context) {
        super(context);
        mContext = context;
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mRootView = inflater.inflate(R.layout.grid_douban, null);
        addView(mRootView);
        initVars();
        findViews();
    }

    private void initVars() {
        mAdapter = new DouBanAdapter();
    }

    private void findViews() {
        mgvDouBan = (NewsGridView) mRootView.findViewById(R.id.douban_gridView);
        mgvDouBan.setAdapter(mAdapter);
    }

    public void setDouBanData(){
        i =4;
        mAdapter.notifyDataSetChanged();
    }


    class DouBanAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return i;
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            final Holder holder;
            if (convertView == null) {
                holder = new Holder();
                convertView = LayoutInflater.from(mContext).inflate(R.layout.adapter_grid_douban, null, false);
                holder.tvContent = (TextView) convertView.findViewById(R.id.content_textView);
                convertView.setTag(holder);
            } else {
                holder = (Holder) convertView.getTag();
            }
            holder.tvContent.setText("北京西");
            return convertView;
        }
    }


    class Holder {
        TextView tvContent;
    }
}
