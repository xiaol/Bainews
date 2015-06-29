package com.news.yazhidao.pages;

import android.content.Context;
import android.content.Intent;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import com.google.gson.reflect.TypeToken;
import com.news.yazhidao.R;
import com.news.yazhidao.common.GlobalParams;
import com.news.yazhidao.common.HttpConstant;
import com.news.yazhidao.entity.Channel;
import com.news.yazhidao.net.JsonCallback;
import com.news.yazhidao.net.MyAppException;
import com.news.yazhidao.net.NetworkRequest;
import com.news.yazhidao.widget.LetterSpacingTextView;

import java.util.ArrayList;


public class CategoryFgt extends Fragment {

    private View rootView;
    private GridView mgvCategory;
    private CategoryAdapter mAdapter;
    private ArrayList<Channel> marrCategoryName;
    private TypedArray marrCategoryDrawable;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fgt_category, container, false);
        initVars();
        findViews();
        return rootView;
    }

    private void initVars() {
//        marrCategoryName = getResources().getStringArray(R.array.category_list_name);

        loadChannelList();

    }

    private void findViews() {
        mgvCategory = (GridView) rootView.findViewById(R.id.category_gridview);
//        mgvCategory.setAdapter(mAdapter);
        mgvCategory.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                GlobalParams.pager.setCurrentItem(1);
                GlobalParams.currentCatePos = position;
                Intent intent = new Intent("sendposition");
                GlobalParams.context.sendBroadcast(intent);
                if (GlobalParams.tabs != null) {
                    setTabTitle(position);
                }
            }
        });
    }

    private void setTabTitle(int position) {

        switch (position) {
            case 0:
                GlobalParams.tabs.updateSelection2(1, "谷歌今日焦点");
                break;
            case 1:
                GlobalParams.tabs.updateSelection2(1, "热门专题");
                break;
            case 2:
                GlobalParams.tabs.updateSelection2(1, "重口味");
                break;
            case 3:
                GlobalParams.tabs.updateSelection2(1, "贵圈乱不乱");
                break;
            case 4:
                GlobalParams.tabs.updateSelection2(1, "反正我信了");
                break;
            case 5:
                GlobalParams.tabs.updateSelection2(1, "Take Ground Gas");
                break;
            case 6:
                GlobalParams.tabs.updateSelection2(1, "直男常备");
                break;
            case 7:
                GlobalParams.tabs.updateSelection2(1, "股往金来");
                break;
            case 8:
                GlobalParams.tabs.updateSelection2(1, "科学嗨起来");
                break;
            case 9:
                GlobalParams.tabs.updateSelection2(1, "高逼格get√");
                break;
            case 10:
                GlobalParams.tabs.updateSelection2(1, "追剧看片schedule");
                break;
            case 11:
                GlobalParams.tabs.updateSelection2(1, "音痴恐惧症");
                break;
            case 12:
                GlobalParams.tabs.updateSelection2(1, "萌师强化班");
                break;
            case 13:
                GlobalParams.tabs.updateSelection2(1, "X星人沦陷区");
                break;
        }

    }

    class CategoryAdapter extends BaseAdapter {

        Context mContext;

        public CategoryAdapter(Context context) {
            mContext = context;
        }

        @Override
        public int getCount() {
            return marrCategoryName.size();
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
                holder.tvName.setTextSize(16);
                convertView.setTag(holder);
            } else {
                holder = (Holder) convertView.getTag();
            }
            holder.ivBgIcon.setBackgroundResource(marrCategoryDrawable.getResourceId(position, 0));
            holder.tvName.setText(marrCategoryName.get(position).getChannel_name());
            return convertView;
        }
    }

    class Holder {
        ImageView ivBgIcon;
        LetterSpacingTextView tvName;
    }

    private void loadChannelList() {

        String url = HttpConstant.URL_GET_CHANNEL_LIST;
        final long start = System.currentTimeMillis();
        final NetworkRequest request = new NetworkRequest(url, NetworkRequest.RequestMethod.GET);
        request.setCallback(new JsonCallback<ArrayList<Channel>>() {

            public void success(ArrayList<Channel> result) {
                marrCategoryName = result;
                marrCategoryDrawable = getResources().obtainTypedArray(R.array.bg_category_list);
                mAdapter = new CategoryAdapter(getActivity());
                mgvCategory.setAdapter(mAdapter);
            }

            public void failed(MyAppException exception) {

            }
        }.setReturnType(new TypeToken<ArrayList<Channel>>() {
        }.getType()));
        request.execute();
    }


}
