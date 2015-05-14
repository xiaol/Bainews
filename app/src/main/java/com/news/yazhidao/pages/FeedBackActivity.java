package com.news.yazhidao.pages;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.BaseExpandableListAdapter;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshExpandableListView;
import com.news.yazhidao.R;
import com.news.yazhidao.common.BaseActivity;
import com.news.yazhidao.entity.FeedBack;
import com.news.yazhidao.utils.DensityUtil;
import com.news.yazhidao.widget.RoundedImageView;
import com.news.yazhidao.widget.TextViewExtend;

import java.util.ArrayList;


public class FeedBackActivity extends BaseActivity {


    private PullToRefreshExpandableListView mlvFeedBack;
    private TSPrivateChatMessageAdapter mAdapter;
    private TextViewExtend mTitleView;
    private RelativeLayout mFeedbackTip;
    private ExpandableListView mlvActual;
    private FeedBack mFeedBack;
    private EditText metFeedBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        IntentFilter intentfilter = new IntentFilter();
//        intentfilter.addAction(AppConfigure.NOTIF_RECIEVE_PRIVATE_CHAT);
//        registerReceiver(mReceiver, intentfilter);
    }

    @Override
    protected void setContentView() {
        setContentView(R.layout.aty_private_chat_message_list);

    }

    @Override
    protected void initializeViews() {
        mAdapter = new TSPrivateChatMessageAdapter(this);

        mTitleView = (TextViewExtend) findViewById(R.id.nav_title_view);
        mTitleView.setText("与T对话中");
        mlvFeedBack = (PullToRefreshExpandableListView) findViewById(R.id.private_chat_message_list_view);//得到ListView对象的引用 /*为ListView设置Adapter来绑定数据*/
        mlvActual = mlvFeedBack.getRefreshableView();
//        mlvActual.setPadding(0, 30, 0, 20);
        mlvActual.setAdapter(mAdapter);
        mlvActual.setGroupIndicator(null);
        mlvActual.setDivider(null);
//        int groupCount = mlvActual.getCount();
//        for (int i = 0; i < groupCount; i++) {
//            mlvActual.expandGroup(i);
//        }
        ImageView pBackButton = (ImageView) findViewById(R.id.back_button);
        pBackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        mlvFeedBack.setMode(PullToRefreshBase.Mode.PULL_FROM_START);
        mlvFeedBack.setOnPullEventListener(new PullToRefreshBase.OnPullEventListener<ExpandableListView>() {
            @Override
            public void onPullEvent(PullToRefreshBase<ExpandableListView> refreshView, PullToRefreshBase.State state, PullToRefreshBase.Mode direction) {

            }
        });
        mFeedbackTip = (RelativeLayout) findViewById(R.id.feedback_tip);
        mFeedbackTip.setVisibility(View.GONE);
        mFeedbackTip.setFocusable(false);
        metFeedBack = (EditText) findViewById(R.id.edit_feedback);
        metFeedBack.setCursorVisible(false);//失去光标
        metFeedBack.setImeOptions(EditorInfo.IME_ACTION_SEND);
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(metFeedBack.getWindowToken(), 0);
        metFeedBack.clearFocus();
    }

    @Override
    protected void loadData() {
        mFeedBack = new FeedBack();
        mFeedBack.id = 10;
        mFeedBack.updateTime = "111111";
        ArrayList<FeedBack.Content> contents = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            FeedBack.Content content = mFeedBack.new Content();
            if (i % 2 == 0) {
                content.content = "dasfdsafd";
                content.type = "0";
            } else {
                content.content = "aaaaaaaa";
                content.type = "1";
            }
            contents.add(content);
        }
        mFeedBack.content = contents;
        final ArrayList<FeedBack> arrayList = new ArrayList<>();
        arrayList.add(mFeedBack);
        mAdapter.SetChatData(arrayList);
        mlvActual.post(new Runnable() {
            @Override
            public void run() {
                // Select the last row so it will scroll into view...
                mlvActual.setSelection(arrayList.size() * 2);
            }
        });

        int groupCount = mlvActual.getCount();
        for (int i = 0; i < groupCount; i++) {
            mlvActual.expandGroup(i);
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

//    @Override
//    public void RequestNewChatsSucceeded(TSDBUserToUserChats argUserToUserChats) {
//        final ArrayList<TSDBChatData> arrTempDatas = argUserToUserChats.getM_arrChats();
//        Log.i("---", "arrTempDatas 1= " + arrTempDatas.size());
//        try {
//            this.ProcessingDatas(arrTempDatas);
//        } catch (ParseException e) {
//            e.printStackTrace();
//        }
//        Collections.reverse(m_arrKeys);
//        Log.i("---", "arrTempDatas 2= " + arrTempDatas.size());
//
//        mAdapter.SetChatData(m_hasMessages, m_arrKeys);
//        Log.i("---", "arrTempDatas 3= " + arrTempDatas.size() * 2);
//        mlvActual.post(new Runnable() {
//            @Override
//            public void run() {
//                // Select the last row so it will scroll into view...
//                mlvActual.setSelection(arrTempDatas.size() * 2);
//            }
//        });
//
//        int groupCount = mlvActual.getCount();
//        for (int i = 0; i < groupCount; i++) {
//            mlvActual.expandGroup(i);
//        }
//
//
//        Log.i("---", "arrTempDatas 4= " + arrTempDatas.size() * 2);


//    }


    public void SaveDataArrayToDic() {
//        Collections.reverse(argDatas);
    }


    BroadcastReceiver mReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
        }
    };


    class TSPrivateChatMessageAdapter extends BaseExpandableListAdapter {

        Context mContext;
        ArrayList<FeedBack> marrFeedBack;

        public TSPrivateChatMessageAdapter(Context context) {
            mContext = context;
        }

        public void SetChatData(ArrayList<FeedBack> arrFeedBack) {
            marrFeedBack = arrFeedBack;
            this.notifyDataSetChanged();
        }

        @Override
        public int getGroupCount() {
            // TODO Auto-generated method stub
            return marrFeedBack == null ? 0 : marrFeedBack.size();
        }

        @Override
        public int getChildrenCount(int groupPosition) {
            // TODO Auto-generated method stub
            if (marrFeedBack != null) {
                ArrayList<FeedBack.Content> contents = marrFeedBack.get(groupPosition).content;
                return contents == null ? 0 : contents.size();
            } else
                return 0;
        }

        @Override
        public Object getGroup(int groupPosition) {
            // TODO Auto-generated method stub
            return marrFeedBack.get(groupPosition);
        }

        @Override
        public Object getChild(int groupPosition, int childPosition) {
            // TODO Auto-generated method stub
            return marrFeedBack.get(groupPosition).content.get(childPosition);
        }

        @Override
        public long getGroupId(int groupPosition) {
            // TODO Auto-generated method stub
            return groupPosition;
        }

        @Override
        public long getChildId(int groupPosition, int childPosition) {
            // TODO Auto-generated method stub
            return childPosition;
        }

        @Override
        public boolean hasStableIds() {
            return true;
        }

        @Override
        public View getGroupView(int groupPosition, boolean isExpanded,
                                 View convertView, ViewGroup parent) {
            final GroupHolder groupHolder;
            if (convertView == null) {
                groupHolder = new GroupHolder();
                convertView = LayoutInflater.from(mContext).inflate(
                        R.layout.adapter_group_chat_header, null, false);
                groupHolder.m_pDateView = (TextViewExtend) convertView
                        .findViewById(R.id.date_view);
                convertView.setTag(groupHolder);
            } else {
                groupHolder = (GroupHolder) convertView.getTag();
            }
            FeedBack feedBack = marrFeedBack.get(groupPosition);
            groupHolder.m_pDateView.setText(feedBack.updateTime);
            return convertView;
        }

        @Override
        public View getChildView(int groupPosition, int childPosition,
                                 boolean isLastChild, View convertView, ViewGroup parent) {
            final ChildHolder holder;
            if (convertView == null) {
                holder = new ChildHolder();
                convertView = LayoutInflater.from(mContext).inflate(R.layout.adapter_private_chat_message_cell, null, false);
                //左头像
                holder.m_pLeftIconView = (RoundedImageView) convertView.findViewById(R.id.left_icon_view);
                holder.m_pLeftIconView.setScaleType(ImageView.ScaleType.CENTER_CROP);
                holder.m_pLeftNameView = (TextViewExtend) convertView.findViewById(R.id.name_view_left);
                //左文字框
                holder.m_pLeftContentView = (TextViewExtend) convertView.findViewById(R.id.content_view_left);
                holder.m_pLeftContentView.setTextSize(16.0f);
                holder.m_pLeftContentView.setMaxWidth(DensityUtil.dip2px(mContext, 240));
                //右头像
                holder.m_pRightIconView = (RoundedImageView) convertView.findViewById(R.id.right_icon_view);
                holder.m_pRightIconView.setScaleType(ImageView.ScaleType.CENTER_CROP);
                holder.m_pRightNameView = (TextViewExtend) convertView.findViewById(R.id.name_view_right);
                //右文字框
                holder.m_pRightContentView = (TextViewExtend) convertView.findViewById(R.id.content_view_right);
                holder.m_pRightContentView.setTextSize(16.0f);
                holder.m_pRightContentView.setMaxWidth(DensityUtil.dip2px(mContext, 240));
                convertView.setTag(holder);
            } else {
                holder = (ChildHolder) convertView.getTag();
            }
            FeedBack.Content content = marrFeedBack.get(groupPosition).content.get(childPosition);

            //显示右侧
            if (content.type != null && content.type.equals("0")) {
                holder.m_pLeftIconView.setVisibility(View.GONE);
                holder.m_pRightIconView.setVisibility(View.VISIBLE);
                holder.m_pLeftContentView.setVisibility(View.GONE);
                holder.m_pRightContentView.setVisibility(View.VISIBLE);
                holder.m_pLeftNameView.setVisibility(View.GONE);
                holder.m_pRightNameView.setVisibility(View.VISIBLE);
//                ImageLoader.getInstance().displayImage("url", holder.m_pRightIconView);
                holder.m_pRightContentView.setText(content.content);
            } else {
                holder.m_pLeftIconView.setVisibility(View.VISIBLE);
                holder.m_pRightIconView.setVisibility(View.GONE);
                holder.m_pLeftContentView.setVisibility(View.VISIBLE);
                holder.m_pRightContentView.setVisibility(View.GONE);
                holder.m_pLeftNameView.setVisibility(View.VISIBLE);
                holder.m_pRightNameView.setVisibility(View.GONE);
//                ImageLoader.getInstance().displayImage("url", holder.m_pLeftIconView);
                holder.m_pLeftContentView.setText(content.content);
            }
            return convertView;
        }

        @Override
        public boolean isChildSelectable(int groupPosition, int childPosition) {
            // TODO Auto-generated method stub
            return true;
        }

        class GroupHolder {
            TextViewExtend m_pDateView;
        }

        class ChildHolder {
            //头像
            RoundedImageView m_pLeftIconView;
            RoundedImageView m_pRightIconView;
            TextViewExtend m_pLeftNameView;
            TextViewExtend m_pRightNameView;
            //文字框
            TextViewExtend m_pLeftContentView;
            TextViewExtend m_pRightContentView;
        }

    }


}