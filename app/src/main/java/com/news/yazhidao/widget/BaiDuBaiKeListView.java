package com.news.yazhidao.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.news.yazhidao.R;


/**
 * Created by h.yuan on 2015/3/23.
 */
public class BaiDuBaiKeListView extends FrameLayout {

    private View mRootView;
    private Context mContext;
    private NewsListView mlvBaiDuBaiKe;
    private ZhiHuAdapter mAdapter;
    int i =3;


    public BaiDuBaiKeListView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mRootView = inflater.inflate(R.layout.list_baidubaike, null);
        addView(mRootView);
        initVars();
        findViews();
    }

    public BaiDuBaiKeListView(Context context) {
        super(context);
        mContext = context;
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mRootView = inflater.inflate(R.layout.list_baidubaike, null);
        addView(mRootView);
        initVars();
        findViews();
    }

    private void initVars() {
        mAdapter = new ZhiHuAdapter();
    }

    private void findViews() {
        mlvBaiDuBaiKe = (NewsListView) mRootView.findViewById(R.id.baidubaike_listView);
        mlvBaiDuBaiKe.setAdapter(mAdapter);
    }

    public void setZhiHuData(){
        i +=4;
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
                convertView = LayoutInflater.from(mContext).inflate(R.layout.adapter_list_baidubaike, null, false);
                holder.tvTitle = (TextView) convertView.findViewById(R.id.title_textView);
                holder.tvContent = (TextView) convertView.findViewById(R.id.content_textView);
                holder.ivPicture = (ImageView) convertView.findViewById(R.id.picture_imageView);
                convertView.setTag(holder);
            } else {
                holder = (Holder) convertView.getTag();
            }
            holder.tvTitle.setText("北京站-百度百科");
            holder.tvContent.setText("dfasdfsadfsadfdfasdfsadfsadfdfasdfsadfsadfdfasdfsadfsadfdfasdfsadfsadfdfasdfsadfsadf");
            return convertView;
        }
    }


    class Holder {
        TextView tvTitle;
        TextView tvContent;
        ImageView ivPicture;
    }
}
