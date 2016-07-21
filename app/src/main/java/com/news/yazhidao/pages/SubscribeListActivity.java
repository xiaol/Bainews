package com.news.yazhidao.pages;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.news.yazhidao.R;
import com.news.yazhidao.adapter.SubscribeListAdapter;
import com.news.yazhidao.common.BaseActivity;
import com.news.yazhidao.entity.AttentionListEntity;
import com.news.yazhidao.entity.NewsFeed;
import com.news.yazhidao.utils.TextUtil;

import java.util.ArrayList;

public class SubscribeListActivity extends BaseActivity {

    private TextView mSubscribeListLeftBack;
    private PullToRefreshListView aty_SubscribeList_PullToRefreshListView;
    private SubscribeListAdapter mAdapter;
    private Context mContext;
    ArrayList<AttentionListEntity> attentionListEntities = new ArrayList<AttentionListEntity>();

    @Override
    protected void setContentView() {
        setContentView(R.layout.activity_subscribe_list);
        mContext = this;

    }

    @Override
    protected void initializeViews() {
        mSubscribeListLeftBack = (TextView) findViewById(R.id.mSubscribeListLeftBack);
        aty_SubscribeList_PullToRefreshListView = (PullToRefreshListView) findViewById(R.id.aty_SubscribeList_PullToRefreshListView);
    }

    @Override
    protected void loadData() {
        setAttentionDate();
        aty_SubscribeList_PullToRefreshListView.setMode(PullToRefreshBase.Mode.DISABLED);

        mAdapter = new SubscribeListAdapter(mContext);
        mAdapter.setNewsFeed(attentionListEntities);
        aty_SubscribeList_PullToRefreshListView.setAdapter(mAdapter);
        mSubscribeListLeftBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
    }
    public void setAttentionDate(){


        for (int i = 0; i < 4; i++) {
            AttentionListEntity attentionListEntity = new AttentionListEntity();

            if (i == 0) {
                attentionListEntity.setIcon("http://file3.u148.net/2011/4/images/1302139148470.jpg");
                attentionListEntity.setDescr("音乐风云1");
                attentionListEntity.setConcern("9987");
            } else if (i == 1) {
                attentionListEntity.setIcon("http://file3.u148.net/2011/4/images/1302139115130.jpg");
                attentionListEntity.setDescr("音乐风云2");
                attentionListEntity.setConcern("10001");
            } else if (i == 2) {
                attentionListEntity.setIcon("http://file3.u148.net/2011/4/images/1302139127832.jpg");
                attentionListEntity.setDescr("音乐风云3");
                attentionListEntity.setConcern("12000");
            } else if (i == 3) {
                attentionListEntity.setIcon("http://file3.u148.net/2011/4/images/1302139144126.jpg");
                attentionListEntity.setDescr("音乐风云4");
                attentionListEntity.setConcern("56");
            }
            attentionListEntities.add(attentionListEntity);
        }
    }
}
