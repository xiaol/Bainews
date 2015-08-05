package com.news.yazhidao.pages;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.news.yazhidao.R;
import com.news.yazhidao.common.GlobalParams;
import com.news.yazhidao.entity.Album;
import com.news.yazhidao.entity.DiggerAlbum;
import com.news.yazhidao.entity.User;
import com.news.yazhidao.listener.FetchAlbumListListener;
import com.news.yazhidao.listener.UserLoginListener;
import com.news.yazhidao.net.request.FetchAlbumListRequest;
import com.news.yazhidao.utils.DensityUtil;
import com.news.yazhidao.utils.DeviceInfoUtil;
import com.news.yazhidao.utils.Logger;
import com.news.yazhidao.utils.ToastUtil;
import com.news.yazhidao.utils.manager.SharedPreManager;
import com.news.yazhidao.widget.DiggerPopupWindow;
import com.news.yazhidao.widget.LoginModePopupWindow;

import java.util.ArrayList;

import cn.sharesdk.framework.PlatformDb;


public class LengjingFgt extends Fragment {

    private View rootView;
    private ListView lv_lecture;
    private ArrayList<Album> albumList = new ArrayList<Album>();
    //个人专辑列表
    private GridView mSpecialGv;
    private MyAlbumLvAdatpter mAlbumLvAdatpter;
    private ArrayList<DiggerAlbum> mDiggerAlbums;
    private Activity mActivity;

    private class LogoutReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {

        }
    }
    public LengjingFgt() {
        mDiggerAlbums = new ArrayList<>();
    }

    @SuppressLint("ValidFragment")
    public LengjingFgt(Activity mActivity) {
        this.mActivity = mActivity;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fgt_category_lengjing, container, false);
        initVars();
        findViews();
        Logger.e("jigang","---lengjing  onCreateView");
        return rootView;
    }

    private void initVars() {
        mAlbumLvAdatpter = new MyAlbumLvAdatpter();
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    private void findViews() {
        lv_lecture = (ListView) rootView.findViewById(R.id.lv_lecture);
        lv_lecture.setAdapter(new MyAdapter());

        //专辑显示列表
        mSpecialGv = (GridView) rootView.findViewById(R.id.mSpecialLv);
        mSpecialGv.setAdapter(mAlbumLvAdatpter);
        mSpecialGv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                openAlbumListAty(position,false);
            }
        });

        /**判断用户是否登录,如果没有登录则只显示教程页面*/
        User user = SharedPreManager.getUser(getActivity());
        if (user == null){
            lv_lecture.setVisibility(View.VISIBLE);
            mSpecialGv.setVisibility(View.GONE);
        }else{
            lv_lecture.setVisibility(View.GONE);
            mSpecialGv.setVisibility(View.VISIBLE);
            /**获取专辑列表数据*/
            FetchAlbumListRequest.obtainAlbumList(getActivity(), new FetchAlbumListListener() {
                @Override
                public void success(ArrayList<DiggerAlbum> resultList) {
                    Logger.e("jigang", "--- " + resultList.size());
                    if (resultList != null && resultList.size() > 0) {
                        setDiggerAlbums(resultList);
                    }
                }

                @Override
                public void failure() {
                    ToastUtil.toastShort("获取专辑失败!");
                }
            });
        }
    }

    /**
     * 打开新闻挖掘的标题和url编辑界面
     *
     * @param newsTitle
     * @param newsUrl
     */
    public void openEditWindow(final String newsTitle, final String newsUrl) {
        /**判断用户是否登录,如果没有登录则只显示教程页面*/
        User user = SharedPreManager.getUser(getActivity());
        if (user == null){
            final LoginModePopupWindow window = new LoginModePopupWindow(getActivity(), new UserLoginListener() {
                @Override
                public void userLogin(String platform, PlatformDb platformDb) {
                    fetchAlbumListData(newsTitle,newsUrl);
                }

                @Override
                public void userLogout() {

                }
            }, null);
            window.showAtLocation(getActivity().getWindow().getDecorView(), Gravity.CENTER
                    | Gravity.CENTER, 0, 0);
        }else{
           fetchAlbumListData(newsTitle, newsUrl);
        }

    }

    /***
     * 获取专辑列表
     * @param newsTitle
     * @param newsUrl
     */
private void fetchAlbumListData(final String newsTitle, final String newsUrl){
    /**获取专辑列表数据*/
    FetchAlbumListRequest.obtainAlbumList(getActivity(), new FetchAlbumListListener() {
        @Override
        public void success(ArrayList<DiggerAlbum> resultList) {
            Logger.e("jigang", "--- " + resultList.size());
            if (resultList != null && resultList.size() > 0) {
                setDiggerAlbums(resultList);
                for(int i = 0;i < mDiggerAlbums.size();i ++){
                    DiggerAlbum diggerAlbum = mDiggerAlbums.get(i);

                    Album album = new Album();
                    album.setAlbum(diggerAlbum.getAlbum_title());
                    album.setDescription(diggerAlbum.getAlbum_des());
                    album.setId(String.valueOf(diggerAlbum.getAlbum_img()));
                    album.setAlbumId(diggerAlbum.getAlbum_id());
                    album.setSelected(i==0);

                    albumList.add(album);
                }

                DiggerPopupWindow window = new DiggerPopupWindow(LengjingFgt.this, mActivity, 1 + "", albumList, 1,false);
                window.setDigNewsTitleAndUrl(newsTitle, newsUrl);
                window.setFocusable(true);
                window.showAtLocation(mActivity.getWindow().getDecorView(), Gravity.CENTER
                        | Gravity.CENTER, 0, 0);
            }
        }

        @Override
        public void failure() {
            ToastUtil.toastShort("获取专辑失败!");
        }
    });
}
    /**
     * 打开专辑列表详情页
     * @param position 当前的专辑索引
     */
    private void openAlbumListAty(int position,boolean isNewAdd){
        DiggerAlbum diggerAlbum = mDiggerAlbums.get(position);
        Logger.e("jigang","update open ="+diggerAlbum.getAlbum_id());
        Intent specialAty = new Intent(getActivity(), AlbumListAty.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable(AlbumListAty.KEY_DIG_SPECIAL_BUNDLE, diggerAlbum);
        bundle.putBoolean(AlbumListAty.KEY_DIG_IS_NEW_ADD, isNewAdd);
        specialAty.putExtra(AlbumListAty.KEY_DIG_SPECIAL_INTENT, bundle);
        startActivity(specialAty);
    }
    /**
     * 更新专辑列表数据
     * @param pAlbumIndex   专辑索引,有可能为新建专辑索引
     * @param pInputTitle 挖掘内容的标题
     * @param pInputUrl  挖掘内容的url,可能为null
     * @param pNewAddAlbum 新建的专辑entity类
     * @param pDiggerAlbum
     */
    public void updateAlbumList(int pAlbumIndex, String pInputTitle, String pInputUrl, Album pNewAddAlbum, DiggerAlbum pDiggerAlbum) {
        Log.e("jigang", "update gaga");
        Logger.e("jigang", "update 11 =" + pDiggerAlbum.getAlbum_id());
        //判断是否是新添加的专辑
        if (pAlbumIndex >= mDiggerAlbums.size()) {
            //添加一个新的专辑
            mDiggerAlbums.add(pDiggerAlbum);
            mAlbumLvAdatpter.notifyDataSetChanged();
        } else {
            //修改专辑数据
            DiggerAlbum diggerAlbum = mDiggerAlbums.get(pAlbumIndex);
            diggerAlbum.setAlbum_news_count((Integer.valueOf(diggerAlbum.getAlbum_news_count())+1)+"");
            mAlbumLvAdatpter.notifyDataSetChanged();

        }
        openAlbumListAty(pAlbumIndex, true);
    }


    public void setDiggerAlbums(ArrayList<DiggerAlbum> mDiggerAlbums) {
        this.mDiggerAlbums = mDiggerAlbums;
        lv_lecture.setVisibility(View.GONE);
        mSpecialGv.setVisibility(View.VISIBLE);
        mAlbumLvAdatpter.notifyDataSetChanged();
    }
    public ArrayList<DiggerAlbum>  getDiggerAlbums(){
        return mDiggerAlbums;
    }
    /**
     * viewpager适配器
     */
    private class MyAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return 3;
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

            convertView = View.inflate(getActivity(), R.layout.item_lv_lecture, null);
            ImageView iv_lecture = (ImageView) convertView.findViewById(R.id.iv_lecture);

            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(GlobalParams.screenWidth - DensityUtil.dip2px(getActivity(),8), (int) (GlobalParams.screenWidth * 0.73));
            iv_lecture.setLayoutParams(params);

            switch (position) {
                case 0:
                    iv_lecture.setBackgroundResource(R.drawable.img_lecture);
                    break;

                case 1:
                    iv_lecture.setBackgroundResource(R.drawable.img_lecture2);
                    break;

                case 2:
                    iv_lecture.setBackgroundResource(R.drawable.img_lecture3);
                    break;
            }

            return convertView;
        }

    }

    /**
     * 个人专辑列表适配器
     */
    public class MyAlbumLvAdatpter extends BaseAdapter {

        @Override
        public int getCount() {
            return mDiggerAlbums == null ? 0 : mDiggerAlbums.size();
        }

        @Override
        public Object getItem(int position) {
            return mDiggerAlbums.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            MyAlbumHolder holder;
            if (convertView == null) {
                holder = new MyAlbumHolder();
                convertView = View.inflate(getActivity(), R.layout.fgt_special_listview_item, null);
                convertView.setLayoutParams(new AbsListView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, (int) (426.0f / 1280 * DeviceInfoUtil.getScreenHeight())));
                holder.mSpecialTitleContainer = convertView.findViewById(R.id.mSpecialTitleContainer);
                holder.mSpecialTitle = (TextView) convertView.findViewById(R.id.mSpecialTitle);
                holder.mSpecialCount = (TextView) convertView.findViewById(R.id.mSpecialCount);
                holder.mSpecialDesc = (TextView) convertView.findViewById(R.id.mSpecialDesc);
                convertView.setTag(holder);
            } else {
                holder = (MyAlbumHolder) convertView.getTag();
            }
            DiggerAlbum diggerAlbum = mDiggerAlbums.get(position);
            holder.mSpecialTitle.setText(diggerAlbum.getAlbum_title());
            holder.mSpecialCount.setText(diggerAlbum.getAlbum_news_count());
            holder.mSpecialDesc.setText(diggerAlbum.getAlbum_des());

            //设置title父容器的宽度
            Rect rect = new Rect();
            holder.mSpecialTitle.getPaint().getTextBounds(diggerAlbum.getAlbum_title(), 0, diggerAlbum.getAlbum_title().length(), rect);
            holder.mSpecialTitleContainer.setLayoutParams(new LinearLayout.LayoutParams(rect.width() + DensityUtil.dip2px(getActivity(), 40), ViewGroup.LayoutParams.WRAP_CONTENT));


            //设置专辑背景图片
            convertView.setBackgroundResource(Integer.valueOf(diggerAlbum.getAlbum_img()));
            return convertView;
        }
    }

    static class MyAlbumHolder {
        View mSpecialTitleContainer;
        TextView mSpecialTitle;
        TextView mSpecialCount;
        TextView mSpecialDesc;
    }


}
