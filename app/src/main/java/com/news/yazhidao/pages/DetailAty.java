package com.news.yazhidao.pages;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.news.yazhidao.R;
import com.news.yazhidao.widget.BaiDuBaiKeListView;
import com.news.yazhidao.widget.DouBanGridView;
import com.news.yazhidao.widget.SinaView;
import com.news.yazhidao.widget.StaggeredGridView;
import com.news.yazhidao.widget.ZhiHuListView;


public class DetailAty extends Activity {

    private BaiDuBaiKeListView mlvBaiDuBaiKe;
    private ZhiHuListView mlvZhiHu;
    private DouBanGridView mgvDouBan;
    private SinaView mvSina;
    private StaggeredGridView mgvMore;
    private MoreAdapter mMoreAdapter;
    private String[] s;
    private ImageView mFooter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.aty_detail);
        initVars();
        findViews();
        setListener();

    }

    private void initVars() {
        s = new String[]{"dddddddddddddddddddddddddddddddddddd", "ddd", "ddd", "dddddddddddddddddddddddddddddddddddd", "ddd", "ddd", "ddd", "ddd", "dddddddddddddddddddddddddddddddddddddddddddddddd", "ddd", "ddd", "ddd", "ddd", "ddd", "ddd", "ddd", "dddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddd", "ddd", "ddd", "ddd", "dddddddddddddddddd", "ddd", "ddd", "dddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddd", "ddd", "ddd", "ddd", "ddd", "ddd", "ddd", "ddd", "ddd", "ddd"};
        mMoreAdapter = new MoreAdapter(this);
        mFooter = new ImageView(this);
        mFooter.setBackgroundResource(R.drawable.ic_launcher);
    }

    private void findViews() {
//        mlvZhiHu = (ZhiHuListView) findViewById(R.id.zhihu_listView);
//        mgvDouBan = (DouBanGridView) findViewById(R.id.douban_gridView);
//        mvSina = (SinaView) findViewById(R.id.sina_view);
        LinearLayout relativeLayout = new LinearLayout(this);
        mlvBaiDuBaiKe = new BaiDuBaiKeListView(this);
        mlvZhiHu = new ZhiHuListView(this);
        mgvDouBan = new DouBanGridView(this);
        mvSina = new SinaView(this);
        relativeLayout.setOrientation(LinearLayout.VERTICAL);
        LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
////        layoutParams.width = LinearLayout.LayoutParams.MATCH_PARENT;
////        layoutParams.height = LinearLayout.LayoutParams.WRAP_CONTENT;
        relativeLayout.addView(mlvBaiDuBaiKe);
        relativeLayout.addView(mlvZhiHu);
        relativeLayout.addView(mgvDouBan);
        relativeLayout.addView(mvSina);
        relativeLayout.setLayoutParams(layoutParams);
        mMoreAdapter.setData(s);
        mgvMore = (StaggeredGridView) findViewById(R.id.more_gridView);
//        mgvMore.setMode(PullToRefreshBase.Mode.PULL_FROM_END);
//        final StaggeredGridView staggeredGridView = mgvMore.getRefreshableView();
        mgvMore.setHeaderView(relativeLayout);
        mgvMore.setAdapter(mMoreAdapter);
        mgvMore.setOnLoadmoreListener(new StaggeredGridView.OnLoadmoreListener() {
            @Override
            public void onLoadmore() {
//                        mMoreAdapter.setData();
                mlvBaiDuBaiKe.setZhiHuData();
                mMoreAdapter.notifyDataSetChanged();
//                        if (!mHasRequestedMore) {
//                            mHasRequestedMore = true;
//                            m_pTSDBUserRequests.RequestMoreMessages(20);
//                        }
            }
        });
        mMoreAdapter.notifyDataSetChanged();

//        mgvMore.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener<StaggeredGridView>() {
//
//            @Override
//            public void onRefresh(PullToRefreshBase<StaggeredGridView> refreshView) {
//
//            }
//        });
//        RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) mgvMore.getLayoutParams();
//        layoutParams.width = 1000;
//        layoutParams.height = 2000;
//        mgvMore.setLayoutParams(layoutParams);
    }

    private void setListener() {
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
