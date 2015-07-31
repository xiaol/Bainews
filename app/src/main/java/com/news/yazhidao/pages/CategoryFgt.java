package com.news.yazhidao.pages;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;

import com.google.gson.reflect.TypeToken;
import com.news.yazhidao.R;
import com.news.yazhidao.common.GlobalParams;
import com.news.yazhidao.common.HttpConstant;
import com.news.yazhidao.entity.Channel;
import com.news.yazhidao.net.JsonCallback;
import com.news.yazhidao.net.MyAppException;
import com.news.yazhidao.net.NetworkRequest;
import com.news.yazhidao.utils.image.ImageManager;
import com.news.yazhidao.widget.LetterSpacingTextView;

import java.util.ArrayList;


public class CategoryFgt extends Fragment {

    private View rootView;
    private ListView mlvCategory;
    private CategoryAdapter mAdapter;
    private ArrayList<Channel> marrCategory;
    private int mScreenWidth;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.e("jigang", "-----CategoryFgt  onCreateView");

        rootView = inflater.inflate(R.layout.fgt_category, container, false);
        initVars();
        findViews();
        return rootView;
    }

    private void initVars() {
        mAdapter = new CategoryAdapter(getActivity());
        WindowManager wm = (WindowManager) getActivity().getSystemService(Context.WINDOW_SERVICE);
        mScreenWidth = wm.getDefaultDisplay().getWidth();
        loadChannelList();
    }

    private void findViews() {
        mlvCategory = (ListView) rootView.findViewById(R.id.category_listview);
        mlvCategory.setAdapter(mAdapter);
        mlvCategory.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                GlobalParams.pager.setCurrentItem(1);
                GlobalParams.currentCatePos = Integer.parseInt(marrCategory.get(position).getChannel_id());
                Intent intent = new Intent("sendposition");
                GlobalParams.context.sendBroadcast(intent);
                if (GlobalParams.tabs != null) {
                    setTabTitle(marrCategory, position);
                }
            }
        });
    }

    private void setTabTitle(ArrayList<Channel> channelList, int position) {
        GlobalParams.tabs.updateSelection2(1, channelList.get(position).getChannel_name());
    }

    class CategoryAdapter extends BaseAdapter {
        Context mContext;
        ArrayList<Channel> arrCategory;

        public CategoryAdapter(Context context) {
            mContext = context;
        }

        public void setData(ArrayList<Channel> Category) {
            arrCategory = Category;
        }

        @Override
        public int getCount() {
            return marrCategory == null ? 0 : marrCategory.size();
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
            Holder holder;
            if (convertView == null) {
                holder = new Holder();
                convertView = LayoutInflater.from(mContext).inflate(R.layout.adapter_list_category, null, false);
                holder.ivBgIcon = (ImageView) convertView.findViewById(R.id.iv_bg_icon);
                holder.tvName = (LetterSpacingTextView) convertView.findViewById(R.id.tv_name);
                holder.tvName.setFontSpacing(5);
                holder.tvDes = (LetterSpacingTextView) convertView.findViewById(R.id.tv_des);
                holder.tvDes.setFontSpacing(5);
                RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) holder.ivBgIcon.getLayoutParams();
                layoutParams.width = mScreenWidth;
                layoutParams.height = (int) (mScreenWidth * 7 / 27.0f);
                holder.ivBgIcon.setLayoutParams(layoutParams);
                convertView.setTag(holder);
            } else {
                holder = (Holder) convertView.getTag();
            }
            Channel channel = marrCategory.get(position);
            if (channel != null) {
                ImageManager.getInstance(mContext).DisplayImage(channel.getChannel_android_img(), holder.ivBgIcon, false, null);
                holder.tvName.setText(channel.getChannel_name());
                holder.tvDes.setText(channel.getChannel_des());
            }
            return convertView;
        }
    }

    class Holder {
        ImageView ivBgIcon;
        LetterSpacingTextView tvName;
        LetterSpacingTextView tvDes;
    }

    private void loadChannelList() {
        String url = HttpConstant.URL_GET_CHANNEL_LIST;
        final NetworkRequest request = new NetworkRequest(url, NetworkRequest.RequestMethod.GET);
        request.setCallback(new JsonCallback<ArrayList<Channel>>() {

            public void success(ArrayList<Channel> result) {
                marrCategory = result;
                mAdapter.setData(marrCategory);
                mAdapter.notifyDataSetChanged();
            }

            public void failed(MyAppException exception) {

            }
        }.setReturnType(new TypeToken<ArrayList<Channel>>() {
        }.getType()));
        request.execute();
    }


}
