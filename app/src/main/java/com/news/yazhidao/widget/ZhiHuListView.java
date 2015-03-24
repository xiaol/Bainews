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
public class ZhiHuListView extends FrameLayout {

    private View mRootView;
    private Context mContext;
    private NewsListView mlvZhiHu;
    private ZhiHuAdapter mAdapter;
    int i =3;


    public ZhiHuListView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mRootView = inflater.inflate(R.layout.list_zhihu, null);
        addView(mRootView);
        initVars();
        findViews();
    }

    public ZhiHuListView(Context context) {
        super(context);
        mContext = context;
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mRootView = inflater.inflate(R.layout.list_zhihu, null);
        addView(mRootView);
        initVars();
        findViews();
    }

    private void initVars() {
        mAdapter = new ZhiHuAdapter();
    }

    private void findViews() {
        mlvZhiHu = (NewsListView) mRootView.findViewById(R.id.zhihu_listView);
        mlvZhiHu.setAdapter(mAdapter);
    }

    public void setZhiHuData(){
        i =4;
        mAdapter.notifyDataSetChanged();
    }


    class ZhiHuAdapter extends BaseAdapter {

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
                convertView = LayoutInflater.from(mContext).inflate(R.layout.adapter_list_zhihu, null, false);
                holder.tvContent = (TextView) convertView.findViewById(R.id.content_textView);
                convertView.setTag(holder);
            } else {
                holder = (Holder) convertView.getTag();
            }
            holder.tvContent.setText("dfasdfsadfsadfdfasdfsadfsadfdfasdfsadfsadfdfasdfsadfsadfdfasdfsadfsadfdfasdfsadfsadf");
            return convertView;
        }
    }


    class Holder {
        TextView tvContent;
    }
}
