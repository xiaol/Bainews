package com.news.yazhidao.pages;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;

import com.google.gson.reflect.TypeToken;
import com.news.yazhidao.R;
import com.news.yazhidao.common.BaseActivity;
import com.news.yazhidao.common.CommonConstant;
import com.news.yazhidao.common.HttpConstant;
import com.news.yazhidao.entity.FeedBackList;
import com.news.yazhidao.net.JsonCallback;
import com.news.yazhidao.net.MyAppException;
import com.news.yazhidao.net.NetworkRequest;
import com.news.yazhidao.widget.RoundedImageView;
import com.news.yazhidao.widget.TextViewExtend;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;


public class ChatAty extends BaseActivity {

    private PrivateChatHistoryAdapter mAdapter;
    private ListView mListView;

    @Override
    protected void setContentView() {
        setContentView(R.layout.aty_chat);
    }

    @Override
    protected void initializeViews() {
        mAdapter = new PrivateChatHistoryAdapter(this);
        mListView = (ListView) findViewById(R.id.chat_list_view);
        mListView.setAdapter(mAdapter);
    }

    @Override
    protected void loadData() {
        NetworkRequest request = new NetworkRequest(HttpConstant.URL_GET_MESSAGE_LIST, NetworkRequest.RequestMethod.GET);
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("jpushId", CommonConstant.JINYU_JPUSH_ID);
        request.getParams = hashMap;
        request.setCallback(new JsonCallback<ArrayList<FeedBackList>>() {

            @Override
            public void success(ArrayList<FeedBackList> result) {
                mAdapter.setData(result);
                Log.i("tag", result.toString());
                mAdapter.notifyDataSetChanged();
            }

            @Override
            public void failed(MyAppException exception) {
            }
        }.setReturnType(new TypeToken<ArrayList<FeedBackList>>() {
        }.getType()));
        request.execute();
    }


    public class PrivateChatHistoryAdapter extends BaseAdapter {

        private Context mContext;
        private ArrayList<FeedBackList> arrayDataList;

        public PrivateChatHistoryAdapter(Context context) {
            mContext = context;
        }

        public void setData(ArrayList<FeedBackList> argDatas) {
            this.arrayDataList = argDatas;
        }

        @Override
        public int getCount() {
            // TODO Auto-generated method stub
            return arrayDataList == null ? 0 : arrayDataList.size();
        }

        @Override
        public Object getItem(int position) {
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        public long getItemId(int position) {
            // TODO Auto-generated method stub
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            final Holder holder;
            if (convertView == null) {
                holder = new Holder();
                convertView = LayoutInflater.from(mContext).inflate(R.layout.adapter_private_chat_history_cell, null, false);
                holder.m_pIconView = (RoundedImageView) convertView.findViewById(R.id.history_icon_view);
                holder.m_pIconView.setScaleType(ImageView.ScaleType.CENTER_CROP);
                holder.m_pNewCount = (RoundedImageView) convertView.findViewById(R.id.new_count);
                holder.m_pUserNameView = (TextViewExtend) convertView.findViewById(R.id.username_view);
                holder.m_pContentHistoryView = (TextViewExtend) convertView.findViewById(R.id.content_history_view);
                holder.m_pDateView = (TextViewExtend) convertView.findViewById(R.id.date_view);
                convertView.setTag(holder);
            } else {
                holder = (Holder) convertView.getTag();
            }
            final FeedBackList data = arrayDataList.get(position);
            convertView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(mContext, FeedBackActivity.class);
                    intent.putExtra("userJpushId",data.jpushId);
                    mContext.startActivity(intent);
                }
            });
            String strPhoto = data.userIcon;
            if (strPhoto != null && !strPhoto.equals(""))
                ImageLoader.getInstance().displayImage(strPhoto, holder.m_pIconView);
//          holder.m_pNewCount.SetValue(pData.getM_iNewChatCount());
            String strUserName = data.userName;
            if (strUserName != null && !strUserName.equals(""))
                holder.m_pUserNameView.setText(data.userName);
            else
                holder.m_pUserNameView.setText("游客");
            SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            holder.m_pContentHistoryView.setText(data.jpushId);
            Date date = new Date(Long.valueOf(data.lastMsgTime));
            holder.m_pDateView.setText(df.format(date));
            return convertView;
        }

        class Holder {
            RoundedImageView m_pIconView;
            RoundedImageView m_pNewCount;
            TextViewExtend m_pUserNameView;
            TextViewExtend m_pContentHistoryView;
            TextViewExtend m_pDateView;
        }

    }
}
