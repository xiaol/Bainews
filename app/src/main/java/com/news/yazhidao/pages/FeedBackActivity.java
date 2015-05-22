package com.news.yazhidao.pages;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.BaseExpandableListAdapter;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.google.gson.reflect.TypeToken;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshExpandableListView;
import com.news.yazhidao.R;
import com.news.yazhidao.common.BaseActivity;
import com.news.yazhidao.common.CommonConstant;
import com.news.yazhidao.common.HttpConstant;
import com.news.yazhidao.entity.FeedBack;
import com.news.yazhidao.entity.Message;
import com.news.yazhidao.entity.User;
import com.news.yazhidao.listener.SendMessageListener;
import com.news.yazhidao.net.JsonCallback;
import com.news.yazhidao.net.MyAppException;
import com.news.yazhidao.net.NetworkRequest;
import com.news.yazhidao.net.request.SendMessageRequest;
import com.news.yazhidao.utils.manager.SharedPreManager;
import com.news.yazhidao.widget.RoundedImageView;
import com.news.yazhidao.widget.TextViewExtend;
import com.nostra13.universalimageloader.core.ImageLoader;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;


public class FeedBackActivity extends BaseActivity implements SendMessageListener {


    private PullToRefreshExpandableListView mlvFeedBack;
    private TSPrivateChatMessageAdapter mAdapter;
    private TextViewExtend mTitleView;
    private RelativeLayout mFeedbackTip;
    private ExpandableListView mlvActual;
    private ArrayList<FeedBack> marrFeedBack;
    private EditText metFeedBack;
    private User mUser;
    private MessageReceiver mReceiver;
    private String mJPushId, mUserId, mUserPlatformType, mReceiverId;
    private boolean mIsSend = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        marrFeedBack = new ArrayList<>();
        mReceiver = new MessageReceiver();
        IntentFilter intentfilter = new IntentFilter();
        intentfilter.addAction("FeedBackMessage");
        registerReceiver(mReceiver, intentfilter);
    }

    @Override
    protected void setContentView() {
        if (getIntent().getStringExtra("userJpushId") != null)
            mReceiverId = getIntent().getStringExtra("userJpushId");
        else
            mReceiverId = CommonConstant.JINYU_JPUSH_ID;
        setContentView(R.layout.aty_private_chat_message_list);
    }

    @Override
    protected void initializeViews() {
        mAdapter = new TSPrivateChatMessageAdapter(this);

        mTitleView = (TextViewExtend) findViewById(R.id.nav_title_view);

        mlvFeedBack = (PullToRefreshExpandableListView) findViewById(R.id.private_chat_message_list_view);//得到ListView对象的引用 /*为ListView设置Adapter来绑定数据*/
        mlvActual = mlvFeedBack.getRefreshableView();
        mlvActual.setAdapter(mAdapter);
        mlvActual.setGroupIndicator(null);
        mlvActual.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {
            @Override
            public boolean onGroupClick(ExpandableListView parent, View v, int groupPosition, long id) {
                return true;
            }
        });
        ImageView pBackButton = (ImageView) findViewById(R.id.back_button);
        pBackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        mlvFeedBack.setMode(PullToRefreshBase.Mode.DISABLED);
//        mlvFeedBack.setOnPullEventListener(new PullToRefreshBase.OnPullEventListener<ExpandableListView>() {
//            @Override
//            public void onPullEvent(PullToRefreshBase<ExpandableListView> refreshView, PullToRefreshBase.State state, PullToRefreshBase.Mode direction) {
//
//            }
//        });
        mFeedbackTip = (RelativeLayout) findViewById(R.id.feedback_tip);
        mFeedbackTip.setVisibility(View.GONE);
        mFeedbackTip.requestFocus();

        metFeedBack = (EditText) findViewById(R.id.edit_feedback);
        metFeedBack.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (mIsSend && keyCode == KeyEvent.KEYCODE_ENTER) {
                    /*隐藏软键盘*/
                    InputMethodManager imm = (InputMethodManager) v
                            .getContext().getSystemService(
                                    Context.INPUT_METHOD_SERVICE);
                    if (imm.isActive()) {
                        imm.hideSoftInputFromWindow(
                                v.getApplicationWindowToken(), 0);
                    }
                    mIsSend = false;
                    Message message;
                    message = new Message(mReceiverId, mJPushId, metFeedBack.getText().toString(), "text");
                    SendMessageRequest.sendMessage(message, FeedBackActivity.this);
                    int size = marrFeedBack.size();
                    String millis;
                    if (size > 0)
                        millis = marrFeedBack.get(size - 1).updateTime;
                    else
                        millis = String.valueOf(System.currentTimeMillis());
                    SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    Long lastTime = 0l;
                    try {
                        lastTime = df.parse(millis).getTime();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    if ((System.currentTimeMillis() - lastTime) > 15 * 60 * 1000) {
                        FeedBack mFeedBack = new FeedBack();
                        try {
                            Date date = new Date();
                            String strDate = df.format(date);
                            mFeedBack.updateTime = strDate;
                            Log.i("tag", strDate);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        ArrayList<FeedBack.Content> contents = new ArrayList<>();
                        FeedBack.Content content = mFeedBack.new Content();
                        content.content = metFeedBack.getText().toString();
                        content.type = "0";
                        contents.add(content);
                        mFeedBack.content = contents;
                        marrFeedBack.add(mFeedBack);
                    } else {
                        FeedBack.Content content = marrFeedBack.get(size - 1).new Content();
                        content.content = metFeedBack.getText().toString();
                        content.type = "0";
                        marrFeedBack.get(size - 1).content.add(content);
                    }
                    refreshUI();
                    return true;
                }
                return false;
            }
        });
    }

    @Override
    protected void loadData() {
        mJPushId = SharedPreManager.getJPushId();
        mUser = SharedPreManager.getUser();
        if (mUser != null) {
            mUserId = mUser.getUserId();
            mUserPlatformType = mUser.getPlatformType();
            mTitleView.setText("与 " + mUser.getUserName() + " 对话中");
        }
        NetworkRequest request = new NetworkRequest(HttpConstant.URL_GET_HISTORY_MESSAGE, NetworkRequest.RequestMethod.GET);
        HashMap<String, Object> hashMap = new HashMap<>();
        if (getIntent().getStringExtra("userJpushId") != null)
            hashMap.put("jpushId", getIntent().getStringExtra("userJpushId"));
        else
            hashMap.put("jpushId", mJPushId);
        hashMap.put("userId", mUserId == null ? "" : mUserId);
        hashMap.put("platformType", mUserPlatformType == null ? "" : mUserPlatformType);
        request.getParams = hashMap;
        request.setCallback(new JsonCallback<ArrayList<FeedBack>>() {
            @Override
            public void success(ArrayList<FeedBack> result) {
                if (result != null) {
                    marrFeedBack = result;
                    for (int i = 0; i < marrFeedBack.size(); i++) {
                        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                        Date date = new Date(Long.valueOf(marrFeedBack.get(i).updateTime));
                        marrFeedBack.get(i).updateTime = df.format(date);
//                        mReceiverId = marrFeedBack.get(i).serviceId;
                    }
                    refreshUI();
                }
            }

            @Override
            public void failed(MyAppException exception) {

            }
        }.setReturnType(new TypeToken<ArrayList<FeedBack>>() {
        }.getType()));
        request.execute();
    }


    @Override
    protected void onDestroy() {
        unregisterReceiver(mReceiver);
        super.onDestroy();
    }

    private void refreshUI() {
        mAdapter.SetChatData(marrFeedBack);
        int groupCount = mAdapter.getGroupCount();
        for (int i = 0; i < groupCount; i++) {
            mlvActual.expandGroup(i);
        }
        mlvActual.post(new Runnable() {
            @Override
            public void run() {
                // Select the last row so it will scroll into view...
                mlvActual.setSelection(marrFeedBack.size() * 2000);
            }
        });
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

    class MessageReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            int size = marrFeedBack.size();
            String millis = marrFeedBack.get(size - 1).updateTime;
            SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Long lastTime = 0l;
            try {
                lastTime = df.parse(millis).getTime();
            } catch (Exception e) {
                e.printStackTrace();
            }
            JSONObject object = null;
            try {
                object = new JSONObject(intent.getStringExtra("message"));
                if (CommonConstant.JINYU_JPUSH_ID.equals(SharedPreManager.getJPushId()))
                    mReceiverId = object.getString("senderId");
            } catch (JSONException e) {
                e.printStackTrace();
            }
            if ((System.currentTimeMillis() - lastTime) > 15 * 60 * 1000) {
                FeedBack mFeedBack = new FeedBack();
                try {
                    Date date = new Date();
                    String strDate = df.format(date);
                    mFeedBack.updateTime = strDate;
                    Log.i("tag", strDate);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                ArrayList<FeedBack.Content> contents = new ArrayList<>();
                FeedBack.Content content = mFeedBack.new Content();
                try {
                    content.content = object.getString("content");
                    content.type = "1";
                    contents.add(content);
                    mFeedBack.content = contents;
                    marrFeedBack.add(mFeedBack);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else {
                FeedBack.Content content = marrFeedBack.get(size - 1).new Content();
                try {
                    content.content = object.getString("content");
                    content.type = "1";
                    marrFeedBack.get(size - 1).content.add(content);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            refreshUI();
        }
    }


    @Override
    public void success(String result) {
        mIsSend = true;
        metFeedBack.setText("");
    }

    @Override
    public void failed(MyAppException exception) {
        mIsSend = true;
        metFeedBack.setText("");
    }

    class TSPrivateChatMessageAdapter extends BaseExpandableListAdapter {

        Context mContext;
        ArrayList<FeedBack> arrFeedBack;

        public TSPrivateChatMessageAdapter(Context context) {
            mContext = context;
        }

        public void SetChatData(ArrayList<FeedBack> feedBack) {
            arrFeedBack = feedBack;
            this.notifyDataSetChanged();
        }

        @Override
        public int getGroupCount() {
            // TODO Auto-generated method stub
            return arrFeedBack == null ? 0 : arrFeedBack.size();
        }

        @Override
        public int getChildrenCount(int groupPosition) {
            // TODO Auto-generated method stub
            if (arrFeedBack != null) {
                ArrayList<FeedBack.Content> contents = arrFeedBack.get(groupPosition).content;
                return contents == null ? 0 : contents.size();
            } else
                return 0;
        }

        @Override
        public Object getGroup(int groupPosition) {
            // TODO Auto-generated method stub
            return arrFeedBack.get(groupPosition);
        }

        @Override
        public Object getChild(int groupPosition, int childPosition) {
            // TODO Auto-generated method stub
            return arrFeedBack.get(groupPosition).content.get(childPosition);
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
            FeedBack feedBack = arrFeedBack.get(groupPosition);
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
                //右头像
                holder.m_pRightIconView = (RoundedImageView) convertView.findViewById(R.id.right_icon_view);
                holder.m_pRightIconView.setScaleType(ImageView.ScaleType.CENTER_CROP);
                holder.m_pRightNameView = (TextViewExtend) convertView.findViewById(R.id.name_view_right);
                //右文字框
                holder.m_pRightContentView = (TextViewExtend) convertView.findViewById(R.id.content_view_right);
                convertView.setTag(holder);
            } else {
                holder = (ChildHolder) convertView.getTag();
            }
            FeedBack.Content content = arrFeedBack.get(groupPosition).content.get(childPosition);

            //显示右侧
            if (content.type != null && content.type.equals("0")) {
                holder.m_pLeftIconView.setVisibility(View.GONE);
                holder.m_pRightIconView.setVisibility(View.VISIBLE);
                holder.m_pLeftContentView.setVisibility(View.GONE);
                holder.m_pRightContentView.setVisibility(View.VISIBLE);
                holder.m_pLeftNameView.setVisibility(View.GONE);
                holder.m_pRightNameView.setVisibility(View.VISIBLE);
                if (mUser != null) {
                    ImageLoader.getInstance().displayImage(mUser.getUserIcon(), holder.m_pRightIconView);
                    holder.m_pRightNameView.setText(mUser.getUserName());
                }
                holder.m_pRightContentView.setText(content.content);
            } else {
                holder.m_pLeftIconView.setVisibility(View.VISIBLE);
                holder.m_pRightIconView.setVisibility(View.GONE);
                holder.m_pLeftContentView.setVisibility(View.VISIBLE);
                holder.m_pRightContentView.setVisibility(View.GONE);
                holder.m_pLeftNameView.setVisibility(View.VISIBLE);
                holder.m_pRightNameView.setVisibility(View.GONE);
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