package com.news.yazhidao.pages;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.news.yazhidao.R;
import com.news.yazhidao.database.DiggerAlbumDao;
import com.news.yazhidao.entity.Album;
import com.news.yazhidao.entity.DiggerAlbum;
import com.news.yazhidao.entity.User;
import com.news.yazhidao.listener.FetchAlbumListListener;
import com.news.yazhidao.listener.UserLoginListener;
import com.news.yazhidao.net.request.FetchAlbumListRequest;
import com.news.yazhidao.utils.DensityUtil;
import com.news.yazhidao.utils.DeviceInfoUtil;
import com.news.yazhidao.utils.Logger;
import com.news.yazhidao.utils.TextUtil;
import com.news.yazhidao.utils.ToastUtil;
import com.news.yazhidao.utils.manager.SharedPreManager;
import com.news.yazhidao.widget.DiggerPopupWindow;
import com.news.yazhidao.widget.LoginModePopupWindow;

import java.util.ArrayList;

import cn.sharesdk.framework.PlatformDb;


public class LengjingFgt extends Fragment {

    private static final String TAG = "LengjingFgt";
    /**
     * 用户注销登录广播
     */
    public static final String ACTION_USER_LOGOUTED = "com.news.yazhidao.ACTION_USER_LOGOUTED";
    /**
     * 用户完成热门话题选择
     */
    public static final String ACTION_USER_CHOSE_TOPIC = "com.news.yazhidao.ACTION_USER_CHOSE_TOPIC";
    /**
     * 添加新专辑时,刷新专辑列表
     */
    public static final String ACTION_USER_REFRESH_ALBUM = "com.news.yazhidao.ACTION_USER_REFRESH_ALBUM";

    private View rootView;
    private View fl_lecture;
    private ArrayList<Album> albumList = new ArrayList<Album>();
    private TextView lstv_des;
    //个人专辑列表
    private GridView mSpecialGv;
    private MyAlbumLvAdatpter mAlbumLvAdatpter;
    private ArrayList<DiggerAlbum> mDiggerAlbums;
    private Activity mActivity;
    /**
     * 用户退出登录广播
     */
    private UserLogoutReceiver mUserLogoutReceiver;

    private class UserLogoutReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (ACTION_USER_LOGOUTED.equals(action)) {
                /**用户退出登录后,要清空挖掘机中显示的数据并且隐藏专辑列表和显示教程页*/
                mDiggerAlbums = null;
                mAlbumLvAdatpter.notifyDataSetChanged();
                fl_lecture.setVisibility(View.VISIBLE);
                mSpecialGv.setVisibility(View.GONE);
            } else if (ACTION_USER_CHOSE_TOPIC.equals(action)) {
                String hotTopic = intent.getStringExtra(BaseTagActivity.KEY_HOT_TOPIC);
                if (!TextUtils.isEmpty(hotTopic)) {
                    hotTopic = hotTopic.replace("#", "");
                }
                openEditWindow(hotTopic, "");
            } else if (ACTION_USER_REFRESH_ALBUM.equals(action)){
                refreshAlbumList();
            }
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
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mUserLogoutReceiver = new UserLogoutReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction(ACTION_USER_LOGOUTED);
        filter.addAction(ACTION_USER_CHOSE_TOPIC);
        filter.addAction(ACTION_USER_REFRESH_ALBUM);
        activity.registerReceiver(mUserLogoutReceiver, filter);
    }

    @Override
    public void onResume() {
        super.onResume();
        Bundle bundle = getArguments();
        if (bundle != null && bundle.size() >0){
            final String title = bundle.getString(DiggerAty.KEY_TITLE);
            final String url = bundle.getString(DiggerAty.KEY_URL);
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    openEditWindow(title, url);
                }
            }, 800);
            bundle.clear();
        }
    }
    @Override
    public void onDetach() {
        super.onDetach();
        getActivity().unregisterReceiver(mUserLogoutReceiver);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fgt_category_lengjing, container, false);
        initVars();
        findViews();
        Logger.e("jigang", "---lengjing  onCreateView");
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
        fl_lecture = rootView.findViewById(R.id.fl_lecture);
        lstv_des = (TextView)rootView.findViewById(R.id.lstv_des);

        //专辑显示列表
        mSpecialGv = (GridView) rootView.findViewById(R.id.mSpecialLv);
        mSpecialGv.setAdapter(mAlbumLvAdatpter);
        mSpecialGv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                openAlbumListAty(position, false, getActivity());
            }
        });

        /**判断用户是否登录,如果没有登录则只显示教程页面*/
        User user = SharedPreManager.getUser(getActivity());
        if (user == null) {
            fl_lecture.setVisibility(View.VISIBLE);
            mSpecialGv.setVisibility(View.GONE);
        } else {
            fl_lecture.setVisibility(View.GONE);
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
                    Logger.e(TAG,"获取专辑失败!");
                    setDiggerAlbums(queryAlbumsFromDB());
                }
            });
        }
    }
    /**
     * 打开新闻挖掘的标题和url编辑界面
     * 此方法主要用于从其他app分享进来或则从热门话题选择标签进来后挖掘
     *
     * @param newsTitle
     * @param newsUrl
     */
    public void openEditWindow(final String newsTitle, final String newsUrl) {
        /**判断用户是否登录,如果没有登录则只显示教程页面*/
        User user = SharedPreManager.getUser(getActivity());
        if (user == null) {
            final LoginModePopupWindow window = new LoginModePopupWindow(getActivity(), new UserLoginListener() {
                @Override
                public void userLogin(String platform, PlatformDb platformDb) {
                    /**获取专辑列表数据*/
                    FetchAlbumListRequest.obtainAlbumList(getActivity(), new FetchAlbumListListener() {
                        @Override
                        public void success(ArrayList<DiggerAlbum> resultList) {
                            handleAlbumsData(newsTitle, newsUrl, resultList);
                        }

                        @Override
                        public void failure() {
                            ToastUtil.toastShort("获取专辑失败!");
                        }
                    });
                }

                @Override
                public void userLogout() {

                }
            }, null);
            window.showAtLocation(getActivity().getWindow().getDecorView(), Gravity.CENTER
                    | Gravity.CENTER, 0, 0);
        } else {
            //查看数据库中是否已经专辑数据,如果没有则联网获取
            ArrayList<DiggerAlbum> resultList = queryAlbumsFromDB();
            if (!TextUtil.isListEmpty(resultList)) {
                handleAlbumsData(newsTitle, newsUrl, resultList);
            } else {
                /**获取专辑列表数据*/
                FetchAlbumListRequest.obtainAlbumList(getActivity(), new FetchAlbumListListener() {
                    @Override
                    public void success(ArrayList<DiggerAlbum> resultList) {
                        handleAlbumsData(newsTitle, newsUrl, resultList);
                    }

                    @Override
                    public void failure() {
                        ToastUtil.toastShort("获取专辑失败!");
                    }
                });

            }
        }

    }

    /**
     * 从数据库获取专辑数据
     *
     * @return
     */
    private ArrayList<DiggerAlbum> queryAlbumsFromDB() {
        DiggerAlbumDao diggerAlbumDao = new DiggerAlbumDao(getActivity());
        return diggerAlbumDao.querForAll();
    }

    /**
     * 处理获取到的专辑列表数据
     *
     * @param resultList
     */
    private void handleAlbumsData(final String newsTitle, final String newsUrl, ArrayList<DiggerAlbum> resultList) {
        Logger.e("jigang","--handleAlbumsData--"+resultList.size());
        if (!TextUtil.isListEmpty(resultList)) {
            albumList.clear();
            for (int i = 0; i < resultList.size(); i++) {
                Album album = new Album();
                album.setSelected(i == 0);
                album.setAlbum(resultList.get(i).getAlbum_title());
                album.setAlbumId(resultList.get(i).getAlbum_id());
                album.setId(resultList.get(i).getAlbum_img());
                albumList.add(album);
            }
            setDiggerAlbums(resultList);
            DiggerPopupWindow window = new DiggerPopupWindow(LengjingFgt.this, mActivity, 1 + "", albumList, 1, false, !TextUtils.isEmpty(newsUrl));
            window.setDigNewsTitleAndUrl(newsTitle, newsUrl);
            window.setFocusable(true);
            window.showAtLocation(getActivity().getWindow().getDecorView(), Gravity.CENTER
                    | Gravity.CENTER, 0, 0);

        }
    }


    /**
     * 打开专辑列表详情页
     *
     * @param position 当前的专辑索引
     */
    private void openAlbumListAty(int position, boolean isNewAdd, Activity activity) {
        DiggerAlbum diggerAlbum = mDiggerAlbums.get(position);
        Logger.e("jigang", "update open =" + diggerAlbum.getAlbum_id());
        Intent specialAty = new Intent(activity, AlbumListAty.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable(AlbumListAty.KEY_DIG_SPECIAL_BUNDLE, diggerAlbum);
        bundle.putBoolean(AlbumListAty.KEY_DIG_IS_NEW_ADD, isNewAdd);
        specialAty.putExtra(AlbumListAty.KEY_DIG_SPECIAL_INTENT, bundle);
        activity.startActivity(specialAty);
    }

    /**
     * 更新专辑列表数据
     *
     * @param pAlbumIndex  专辑索引,有可能为新建专辑索引
     * @param pDiggerAlbum
     */
    public void updateAlbumList(int pAlbumIndex, DiggerAlbum pDiggerAlbum) {
        Logger.e("jigang", "update 11 =" + pDiggerAlbum.getAlbum_id());
        //判断是否是新添加的专辑
        if (pAlbumIndex >= mDiggerAlbums.size()) {
            //添加一个新的专辑
            mDiggerAlbums.add(pDiggerAlbum);
        }
        mAlbumLvAdatpter.notifyDataSetChanged();
        openAlbumListAty(pAlbumIndex, true, getActivity());
    }

    /**
     * 刷新列表
     */
    public void refreshAlbumList(){
        ArrayList<DiggerAlbum> diggerAlbums = queryAlbumsFromDB();
        mDiggerAlbums = diggerAlbums;
        mAlbumLvAdatpter.notifyDataSetChanged();
    }
    public void setDiggerAlbums(ArrayList<DiggerAlbum> mDiggerAlbums) {
        this.mDiggerAlbums = mDiggerAlbums;
        if (fl_lecture!=null){
            fl_lecture.setVisibility(View.GONE);
        }
        mSpecialGv.setVisibility(View.VISIBLE);
        mAlbumLvAdatpter.notifyDataSetChanged();
    }

    public ArrayList<DiggerAlbum> getDiggerAlbums() {
        return mDiggerAlbums;
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
