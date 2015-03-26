package com.news.yazhidao.pages;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.news.yazhidao.R;
import com.news.yazhidao.widget.NewsDetailHeaderView;
import com.news.yazhidao.widget.StaggeredGridView;


public class DetailAty extends Activity {


    private StaggeredGridView mgvMore;
    private String[] s;
    private MoreAdapter mMoreAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.aty_detail);
        initVars();
        findViews();
        setListener();

    }

    private void initVars() {
        s = new String[] {"dddd","ddd"};
        mMoreAdapter = new MoreAdapter(this);
        mMoreAdapter.setData(s);
    }

    private void findViews() {
        NewsDetailHeaderView headerView = new NewsDetailHeaderView(this);

        mgvMore = (StaggeredGridView) findViewById(R.id.news_detail_staggeredGridView);
        mgvMore.setHeaderView(headerView);
        mgvMore.setAdapter(mMoreAdapter);
        mMoreAdapter.notifyDataSetChanged();
    }

    private void setListener() {
        mgvMore.setOnLoadmoreListener(new StaggeredGridView.OnLoadmoreListener() {
            @Override
            public void onLoadmore() {

            }
        });
    }

    class MoreAdapter extends BaseAdapter {

        Context mContext;
        String[] mStrings;

        MoreAdapter(Context context) {
            mContext = context;
        }


        public void setData(String[] strings) {
            mStrings = strings;
        }

        @Override
        public int getCount() {
            return mStrings.length;
        }

        @Override
        public Object getItem(int position) {
            return mStrings[position];
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            final Holder holder;
            if (convertView == null) {
                holder = new Holder();
                convertView = LayoutInflater.from(mContext).inflate(R.layout.adapter_list_more, null, false);
                holder.tvContent = (TextView) convertView.findViewById(R.id.content_textView);
                convertView.setTag(holder);
            } else {
                holder = (Holder) convertView.getTag();
            }
            holder.tvContent.setText(mStrings[position]);
            return convertView;
        }
    }


    class Holder {
        TextView tvContent;
    }


}
