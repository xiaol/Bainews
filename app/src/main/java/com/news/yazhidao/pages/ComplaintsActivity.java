package com.news.yazhidao.pages;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import com.news.yazhidao.R;
import com.news.yazhidao.adapter.ComplaintsAdapter;
import com.news.yazhidao.common.BaseActivity;
import com.news.yazhidao.utils.ToastUtil;

import java.util.ArrayList;

public class ComplaintsActivity extends BaseActivity {
    private TextView Complaints_LeftBack,commitBtn;
    private ListView Complaints_listView;
    private ComplaintsAdapter mAdapter;
    private ArrayList<String> stringArrayList = new ArrayList<String>();
    private Context mContext;


    @Override
    protected boolean translucentStatus() {
        return false;
    }
    @Override
    protected void setContentView() {
        setContentView(R.layout.activity_complaints);
        mContext = this;
    }

    @Override
    protected void initializeViews() {
        Complaints_LeftBack = (TextView) findViewById(R.id.Complaints_LeftBack);
        Complaints_listView = (ListView) findViewById(R.id.Complaints_listView);
      ;
    }

    @Override
    protected void loadData() {

        stringArrayList.add("广告");
        stringArrayList.add("色情低俗");
        stringArrayList.add("反动");
        stringArrayList.add("谣言");
        stringArrayList.add("欺诈或恶意营销");
        stringArrayList.add("标题夸张/文不对题");
        stringArrayList.add("内容过时");
        stringArrayList.add("内容格式有误");
        stringArrayList.add("错别字");
        stringArrayList.add("抄袭");
        addListView();
        Complaints_LeftBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        mAdapter = new ComplaintsAdapter(mContext, stringArrayList, new ComplaintsAdapter.SetItemChangeBackgroundListener() {
            @Override
            public void listener() {
                commitBtn.setBackgroundResource(R.drawable.complaintsfooterview_btnshape);
                commitBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        ToastUtil.toastShort("您的投诉已提交");
                        finish();

                    }
                });
            }
        });
        Complaints_listView.setAdapter(mAdapter);

    }

    private void addListView() {
        View headView = getLayoutInflater().inflate(R.layout.complaints_headview, null);
        Complaints_listView.addHeaderView(headView);

        View footerView = getLayoutInflater().inflate(R.layout.complaints_footerview, null);
        commitBtn = (TextView) footerView.findViewById(R.id.complaintsfooterView_Btn);

        Complaints_listView.addFooterView(footerView);

    }
}
