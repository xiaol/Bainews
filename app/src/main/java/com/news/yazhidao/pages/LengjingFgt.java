package com.news.yazhidao.pages;

import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.res.TypedArray;
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
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.news.yazhidao.R;
import com.news.yazhidao.common.GlobalParams;
import com.news.yazhidao.entity.Album;
import com.news.yazhidao.entity.Channel;
import com.news.yazhidao.entity.DigSpecial;
import com.news.yazhidao.entity.DigSpecialItem;
import com.news.yazhidao.utils.DensityUtil;
import com.news.yazhidao.utils.DeviceInfoUtil;
import com.news.yazhidao.widget.DiggerPopupWindow;
import com.oguzdev.circularfloatingactionmenu.library.FloatingActionButton;
import com.oguzdev.circularfloatingactionmenu.library.FloatingActionMenu;
import com.oguzdev.circularfloatingactionmenu.library.SubActionButton;

import java.util.ArrayList;


public class LengjingFgt extends Fragment {

    private View rootView;
    private ListView lv_lecture;
    private ArrayList<Channel> marrCategoryName;
    private TypedArray marrCategoryDrawable;
    private FloatingActionButton leftCenterButton;
    private FloatingActionButton.LayoutParams starParams;
    private ArrayList<Album> albumList = new ArrayList<Album>();
    //个人专辑列表
    private GridView mSpecialGv;
    private MySpecialLvAdatpter mSpecialLvAdatpter;
    private ArrayList<DigSpecial> mSpecialDatas;
    private ArrayList<DigSpecialItem> mSpecialItems;
    private Activity mActivity;
    public LengjingFgt(){}
    @SuppressLint("ValidFragment")
    public LengjingFgt(Activity mActivity){
        this.mActivity = mActivity;
    }
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
        //TODO 此处是测试数据
        mSpecialDatas = new ArrayList();
        ArrayList<DigSpecialItem> items1 = new ArrayList<>();
        items1.add(new DigSpecialItem("这是一个神奇的网站.","htttp://www.baidu.com",1));
        items1.add(new DigSpecialItem("默认","htttp://www.baidu.com",3));
        items1.add(new DigSpecialItem("这是一个神奇的网站.这是一个神奇的网站.这是一个神奇的网站.这是一个神奇的网站.","htttp://www.baidu.comhtttp://www.baidu.comhtttp://www.baidu.com",7));
        mSpecialDatas.add(new DigSpecial("默认","这是一个神奇的app",R.drawable.bg_special_list_item_default,items1));

        ArrayList<DigSpecialItem> items2 = new ArrayList<>();
        items2.add(new DigSpecialItem("这是一个神奇的网站.","htttp://www.baidu.com",2));
        items2.add(new DigSpecialItem("这是一个神奇的网站.","htttp://www.baidu.com",2));
        items2.add(new DigSpecialItem("默认","htttp://www.baidu.com",5));
        items2.add(new DigSpecialItem("这是一个神奇的网站.这是一个神奇的网站.这是一个神奇的网站.这是一个神奇的网站.","htttp://www.baidu.comhtttp://www.baidu.comhtttp://www.baidu.com",6));
        mSpecialDatas.add(new DigSpecial("最爱的", "这是一个神奇的app,这是一个神奇的app,这是一个神奇的app", R.drawable.bg_special_list_item_default,items2));

        mSpecialLvAdatpter = new MySpecialLvAdatpter();
    }

    @Override
    public void onStop() {
        if (leftCenterButton != null) {
            leftCenterButton.setVisibility(View.GONE);
        }
        super.onStop();
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
        mSpecialGv.setAdapter(mSpecialLvAdatpter);
        mSpecialGv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                DigSpecial digSpecial = mSpecialDatas.get(position);
                Intent specialAty = new Intent(getActivity(),SpecialListAty.class);
                Bundle bundle = new Bundle();
                bundle.putSerializable(SpecialListAty.KEY_DIG_SPECIAL_BUNDLE,digSpecial);
                specialAty.putExtra(SpecialListAty.KEY_DIG_SPECIAL_INTENT,bundle);
                startActivity(specialAty);
            }
        });
        // Set up the large red button on the center right side
        // With custom button and content sizes and margins
        int redActionButtonSize = getResources().getDimensionPixelSize(R.dimen.red_action_button_size);
        int imgSize = getResources().getDimensionPixelSize(R.dimen.img_size);
        int redActionButtonMargin = getResources().getDimensionPixelOffset(R.dimen.action_button_margin);
        int redActionButtonContentSize = getResources().getDimensionPixelSize(R.dimen.red_action_button_content_size);
        int redActionButtonContentMargin = getResources().getDimensionPixelSize(R.dimen.red_action_button_content_margin);
        int redActionMenuRadius = getResources().getDimensionPixelSize(R.dimen.red_action_menu_radius);
        int blueSubActionButtonSize = getResources().getDimensionPixelSize(R.dimen.blue_sub_action_button_size);
        int blueSubActionButtonContentMargin = getResources().getDimensionPixelSize(R.dimen.blue_sub_action_button_content_margin);

        final ImageView fabIconStar = new ImageView(getActivity());
        fabIconStar.setImageDrawable(getResources().getDrawable(R.drawable.icon_lengjing_digger));

        starParams = new FloatingActionButton.LayoutParams(redActionButtonSize, redActionButtonSize);
        starParams.setMargins(redActionButtonMargin,
                redActionButtonMargin,
                redActionButtonMargin,
                redActionButtonMargin);
        fabIconStar.setLayoutParams(starParams);

        FloatingActionButton.LayoutParams fabIconStarParams = new FloatingActionButton.LayoutParams(redActionButtonContentSize, redActionButtonContentSize);
        fabIconStarParams.setMargins(redActionButtonContentMargin,
                redActionButtonContentMargin,
                redActionButtonContentMargin,
                redActionButtonContentMargin);

        leftCenterButton = new FloatingActionButton.Builder(getActivity())
                .setContentView(fabIconStar, fabIconStarParams)
                .setPosition(FloatingActionButton.POSITION_BOTTOM_CENTER)
                .setLayoutParams(starParams)
                .build();

        // Set up customized SubActionButtons for the right center menu
        SubActionButton.Builder lCSubBuilder = new SubActionButton.Builder(getActivity());

        int buttonContentSize = getResources().getDimensionPixelSize(R.dimen.sub_action_button_content_size);

        FrameLayout.LayoutParams blueContentParams = new FrameLayout.LayoutParams(buttonContentSize, buttonContentSize);
        blueContentParams.setMargins(blueSubActionButtonContentMargin,
                blueSubActionButtonContentMargin,
                blueSubActionButtonContentMargin,
                blueSubActionButtonContentMargin);
        lCSubBuilder.setLayoutParams(blueContentParams);
        // Set custom layout params
        FrameLayout.LayoutParams blueParams = new FrameLayout.LayoutParams(blueSubActionButtonSize, blueSubActionButtonSize);
        lCSubBuilder.setLayoutParams(blueParams);

        ImageView lcIcon1 = new ImageView(getActivity());
        ImageView lcIcon2 = new ImageView(getActivity());
        ImageView lcIcon3 = new ImageView(getActivity());


        lcIcon1.setImageResource(R.drawable.icon_lengjing_text);
        lcIcon2.setImageResource(R.drawable.icon_lengjing_url);
        lcIcon3.setImageResource(R.drawable.icon_lengjing_base);

        SubActionButton button1 = lCSubBuilder.setContentView(lcIcon1, blueContentParams).build();
        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Album album = new Album();
                album.setSelected(false);
                album.setAlbum("默认");
                albumList.add(album);
                Log.e("jigang","----2albumList size ="+albumList.size());
                DiggerPopupWindow window = new DiggerPopupWindow(LengjingFgt.this, getActivity(), 1 + "", albumList,1);
                window.setFocusable(true);
                window.showAtLocation(getActivity().getWindow().getDecorView(), Gravity.CENTER
                        | Gravity.CENTER, 0, 0);
            }
        });
        SubActionButton button2 = lCSubBuilder.setContentView(lcIcon2, blueContentParams).build();

        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Album album = new Album();
                album.setSelected(false);
                album.setAlbum("默认");
                albumList.add(album);
                Log.e("jigang", "----2albumList size =" + albumList.size());
                DiggerPopupWindow window = new DiggerPopupWindow(LengjingFgt.this, getActivity(), 1 + "", albumList,2);
                window.setFocusable(true);
                window.showAtLocation(getActivity().getWindow().getDecorView(), Gravity.CENTER
                        | Gravity.CENTER, 0, 0);
            }
        });

        SubActionButton button3 = lCSubBuilder.setContentView(lcIcon3, blueContentParams).build();
        button3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), BaseTagActivity.class);
                startActivity(intent);
            }
        });

        // Build another menu with custom options
        final FloatingActionMenu leftCenterMenu = new FloatingActionMenu.Builder(getActivity())
                .addSubActionView(button1)
                .addSubActionView(button2)
                .addSubActionView(button3)
                .setRadius(redActionMenuRadius)
                .setStartAngle(-140)
                .setEndAngle(-40)
                .attachTo(leftCenterButton)
                .build();

        leftCenterMenu.setStateChangeListener(new FloatingActionMenu.MenuStateChangeListener() {
            @Override
            public void onMenuOpened(FloatingActionMenu menu) {
                fabIconStar.setRotation(0);
                PropertyValuesHolder pvhR = PropertyValuesHolder.ofFloat(View.ROTATION, 135);
                ObjectAnimator animation = ObjectAnimator.ofPropertyValuesHolder(fabIconStar, pvhR);
                animation.start();
            }

            @Override
            public void onMenuClosed(FloatingActionMenu menu) {
                fabIconStar.setRotation(135);
                PropertyValuesHolder pvhR = PropertyValuesHolder.ofFloat(View.ROTATION, 0);
                ObjectAnimator animation = ObjectAnimator.ofPropertyValuesHolder(fabIconStar, pvhR);
                animation.start();
            }
        });

    }

    /**
     * 打开新闻挖掘的标题和url编辑界面
     * @param newsTitle
     * @param newsUrl
     */
    public void openEditWindow(String newsTitle,String newsUrl){
        Album album = new Album();
        album.setSelected(true);
        album.setAlbum("默认");
        albumList.add(album);
        Log.e("jigang","++++"+mActivity);
        DiggerPopupWindow window = new DiggerPopupWindow(this,mActivity, 1 + "", albumList,1);
        window.setDigNewsTitleAndUrl(newsTitle,newsUrl);
        window.setFocusable(true);
        window.showAtLocation(mActivity.getWindow().getDecorView(), Gravity.CENTER
                | Gravity.CENTER, 0, 0);
    }

    /**
     * 更新专辑列表数据
     * @param specialIndex 专辑索引
     * @param inputTitle
     * @param mNewAddSpecial
     */
    public void updateSpecialList(int specialIndex, String inputTitle, Album mNewAddSpecial){
        Log.e("jigang", "update gaga");
        //判断是否是心添加的专辑
        if(specialIndex >= mSpecialDatas.size()){
            //添加一个新的专辑
            ArrayList<DigSpecialItem> items1 = new ArrayList<>();
            items1.add(new DigSpecialItem(inputTitle,"",1));
            mSpecialDatas.add(new DigSpecial(mNewAddSpecial.getAlbum(),mNewAddSpecial.getDescription(),Integer.valueOf(mNewAddSpecial.getId()),items1));
            mSpecialLvAdatpter.notifyDataSetChanged();
        }else {
            //修改专辑数据
            DigSpecial digSpecial = mSpecialDatas.get(specialIndex);
            ArrayList<DigSpecialItem> items = digSpecial.getSpecialItems();
            items.add(new DigSpecialItem(inputTitle, "", 1));
            digSpecial.setSpecialItems(items);
            mSpecialLvAdatpter.notifyDataSetChanged();

        }
    }

    /**
     * 获取专辑列表数据
     * @return
     */
    public ArrayList<DigSpecial> getSpecialDatas(){
        return mSpecialDatas;
    }
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

            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(GlobalParams.screenWidth, (int) (GlobalParams.screenWidth * 0.73));
            params.setMargins(10, 10, 10, 10);

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
    public class MySpecialLvAdatpter extends BaseAdapter {

        @Override
        public int getCount() {
            return mSpecialDatas == null ? 0 : mSpecialDatas.size();
        }

        @Override
        public Object getItem(int position) {
            return mSpecialDatas.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            MySpecialHolder holder;
            if (convertView == null) {
                holder = new MySpecialHolder();
                convertView = View.inflate(getActivity(), R.layout.fgt_special_listview_item, null);
                convertView.setLayoutParams(new AbsListView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, (int) (426.0f / 1280 * DeviceInfoUtil.getScreenHeight())));
                holder.mSpecialTitleContainer = convertView.findViewById(R.id.mSpecialTitleContainer);
                holder.mSpecialTitle = (TextView) convertView.findViewById(R.id.mSpecialTitle);
                holder.mSpecialCount = (TextView) convertView.findViewById(R.id.mSpecialCount);
                holder.mSpecialDesc = (TextView) convertView.findViewById(R.id.mSpecialDesc);
                convertView.setTag(holder);
            } else {
                holder = (MySpecialHolder) convertView.getTag();
            }
            DigSpecial digSpecial = mSpecialDatas.get(position);
            holder.mSpecialTitle.setText(digSpecial.getTitle());
            holder.mSpecialCount.setText(digSpecial.getSpecialItems().size()+"");
            holder.mSpecialDesc.setText(digSpecial.getDesc());

            //设置title父容器的宽度
            Rect rect = new Rect();
            holder.mSpecialTitle.getPaint().getTextBounds(digSpecial.getTitle(), 0, digSpecial.getTitle().length(), rect);
            holder.mSpecialTitleContainer.setLayoutParams(new LinearLayout.LayoutParams(rect.width() + DensityUtil.dip2px(getActivity(), 40), ViewGroup.LayoutParams.WRAP_CONTENT));

            return convertView;
        }
    }

    static class MySpecialHolder {
        View mSpecialTitleContainer;
        TextView mSpecialTitle;
        TextView mSpecialCount;
        TextView mSpecialDesc;
    }


}
