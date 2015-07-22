package com.news.yazhidao.pages;

import android.content.res.TypedArray;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.news.yazhidao.R;
import com.news.yazhidao.common.GlobalParams;
import com.news.yazhidao.entity.Album;
import com.news.yazhidao.entity.Channel;
import com.oguzdev.circularfloatingactionmenu.library.FloatingActionButton;
import com.oguzdev.circularfloatingactionmenu.library.FloatingActionMenu;

import java.util.ArrayList;


public class LengjingFgt extends Fragment {

    private View rootView;
    private ListView lv_lecture;
    private MyAdapter adapter;
    private ArrayList<Channel> marrCategoryName;
    private TypedArray marrCategoryDrawable;
    private FloatingActionButton leftCenterButton;
    private FloatingActionButton.LayoutParams starParams;
    private ArrayList<Album> albumList= new ArrayList<Album>();
    private FloatingActionMenu leftCenterMenu;
    private ImageView fabIconStar;
    private LinearLayout ll_root;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fgt_category_lengjing, container, false);
        initVars();
        findViews();
        return rootView;
    }

    private void initVars() {

    }

    @Override
    public void onStop() {

        super.onStop();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    private void findViews() {
        lv_lecture = (ListView) rootView.findViewById(R.id.lv_lecture);
        adapter = new MyAdapter();
        lv_lecture.setAdapter(adapter);
        ll_root = (LinearLayout) rootView.findViewById(R.id.ll_rootview);
    }

    private class MyAdapter extends BaseAdapter{

        @Override
        public int getCount() {
            return 1;
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

            convertView = View.inflate(getActivity(),R.layout.item_lv_lecture,null);
            ImageView iv_lecture = (ImageView) convertView.findViewById(R.id.iv_lecture);

            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(GlobalParams.screenWidth, (int)(GlobalParams.screenWidth * 0.73));
            params.setMargins(10,10,10,10);

            iv_lecture.setLayoutParams(params);

            switch (position){
                case 0:
                    iv_lecture.setBackgroundResource(R.drawable.img_lecture);
                    break;
            }

            return convertView;
        }

    }
}
