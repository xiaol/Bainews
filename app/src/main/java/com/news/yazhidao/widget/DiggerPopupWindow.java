package com.news.yazhidao.widget;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.news.yazhidao.R;
import com.news.yazhidao.common.GlobalParams;
import com.news.yazhidao.entity.Album;
import com.news.yazhidao.pages.LengjingFgt;
import com.news.yazhidao.utils.ToastUtil;

import java.util.ArrayList;


/**
 * Created by ariesy on 2015/7/16.
 */
public class DiggerPopupWindow extends PopupWindow implements View.OnClickListener {

    private Activity m_pContext;
    private LengjingFgt mLengJingFgt;
    private View mMenuView;
    private int itemCount;
    private AlbumAdapter adapter;

    private TextView tv_cancel;
    private TextView tv_source_url;
    private TextView tv_confirm;
    private EditText et_content;
    private GridView gv_album;
    private ArrayList<Album> albumList;
    public DiggerPopupWindow(LengjingFgt lengjingFgt, Activity context, String itemCount, ArrayList<Album> list) {
        super(context);
        m_pContext = context;
        this.mLengJingFgt = lengjingFgt;
        if(albumList != null){
            albumList.clear();
        }
        if(itemCount != null) {
            this.itemCount = Integer.parseInt(itemCount);
        }

        albumList = new ArrayList<Album>();

        if(list != null && list.size() > 0) {
            for (int i = 0; i < list.size(); i++) {
                albumList.add(list.get(i));
            }
        }

        findHeadPortraitImageViews();
        loadData();


    }



    private void findHeadPortraitImageViews() {

        mMenuView = View.inflate(m_pContext, R.layout.popup_window_add_digger,null);
        tv_cancel = (TextView) mMenuView.findViewById(R.id.tv_cancel);
        tv_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        tv_source_url = (TextView) mMenuView.findViewById(R.id.tv_source_url);
        tv_confirm = (TextView) mMenuView.findViewById(R.id.tv_confirm);
        tv_confirm.setOnClickListener(this);
        et_content = (EditText) mMenuView.findViewById(R.id.et_content);
        gv_album = (GridView) mMenuView.findViewById(R.id.gv_album);
        gv_album.setColumnWidth((int) (GlobalParams.screenWidth * 0.45));
//        gv_album.setSelector(null);
        adapter = new AlbumAdapter(m_pContext);
        gv_album.setAdapter(adapter);

        //设置SelectPicPopupWindow的View
        this.setContentView(mMenuView);
        //设置SelectPicPopupWindow弹出窗体的宽
        this.setWidth(ViewGroup.LayoutParams.MATCH_PARENT);
        //设置SelectPicPopupWindow弹出窗体的高
        this.setHeight(ViewGroup.LayoutParams.MATCH_PARENT);
        //设置SelectPicPopupWindow弹出窗体可点击
        this.setFocusable(true);
        //设置SelectPicPopupWindow弹出窗体动画效果
//        this.setAnimationStyle(R.style.DialogAnimation);
        //实例化一个ColorDrawable颜色为半透明
        ColorDrawable dw = new ColorDrawable(0xb0000000);
        //设置SelectPicPopupWindow弹出窗体的背景
        this.setBackgroundDrawable(dw);
    }

    @Override
    public void dismiss() {
        super.dismiss();
    }

    private void loadData() {

    }
    public void setDigNewsTitleAndUrl(String title,String url){
        et_content.setText(title);
        et_content.setSelection(title.length());
        tv_source_url.setText(url);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.tv_confirm:
            if(TextUtils.isEmpty(et_content.getText().toString())){
                ToastUtil.toastShort("亲,挖掘内容不能为空!");
            }else{
                mLengJingFgt.updateSpecialList();
                this.dismiss();
            }
                break;
        }
    }

    class AlbumAdapter extends BaseAdapter {
        Context mContext;

        public AlbumAdapter(Context context) {
            mContext = context;
        }

        @Override
        public int getCount() {
            return albumList.size() + 1;
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
        public View getView(final int position, View convertView, ViewGroup parent) {
            final Holder holder;
            if (convertView == null) {
                holder = new Holder();
                convertView = LayoutInflater.from(mContext).inflate(R.layout.item_gridview_album, null, false);
                holder.ivBgIcon = (ImageView) convertView.findViewById(R.id.iv_bg_icon);
                holder.tvName = (LetterSpacingTextView) convertView.findViewById(R.id.tv_name);
                holder.tvName.setFontSpacing(5);
                holder.tvName.setTextSize(16);
                holder.iv_selected = (ImageView) convertView.findViewById(R.id.iv_selected);
                holder.rl_album = (RelativeLayout) convertView.findViewById(R.id.rl_album);
                holder.rl_add_album = (RelativeLayout) convertView.findViewById(R.id.rl_add_album);
                holder.iv_add_album = (ImageView) convertView.findViewById(R.id.iv_add_album);
                convertView.setTag(holder);
            } else {
                holder = (Holder) convertView.getTag();
            }

            holder.rl_album.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Album album = albumList.get(position);

                    boolean isSelected = album.isSelected();
                    if(isSelected){
                        holder.iv_selected.setVisibility(View.GONE);
                        album.setSelected(false);
                    }else{
                        holder.iv_selected.setVisibility(View.VISIBLE);
                        album.setSelected(true);
                    }

                    for(int i = 0;i < albumList.size();i++){
                        if(i != position){
                            albumList.get(i).setSelected(false);
                        }
                    }
                }
            });

            holder.rl_add_album.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AddAlbumPopupWindow window = new AddAlbumPopupWindow(m_pContext,new AddAlbumPopupWindow.AddAlbumListener(){

                        @Override
                        public void add(Album album) {
                            if(album != null){
                                albumList.add(album);
                                adapter.notifyDataSetChanged();
                            }
                        }
                    });
                    window.setFocusable(true);
                    window.showAtLocation(m_pContext.getWindow().getDecorView(), Gravity.CENTER
                            | Gravity.CENTER, 0, 0);
                }
            });


            if(position == albumList.size()){

                holder.rl_album.setVisibility(View.GONE);
                holder.rl_add_album.setVisibility(View.VISIBLE);

            }else {

                holder.rl_album.setVisibility(View.VISIBLE);
                holder.rl_add_album.setVisibility(View.GONE);

                Album album = albumList.get(position);

                if (albumList != null && albumList.size() > 0) {
                    holder.tvName.setText(album.getAlbum());
                }

                if(album.isSelected()){
                    holder.iv_selected.setVisibility(View.VISIBLE);
                }else{
                    holder.iv_selected.setVisibility(View.GONE);
                }
            }
            return convertView;
        }
    }

    class Holder {
        ImageView ivBgIcon;
        LetterSpacingTextView tvName;
        ImageView iv_selected;
        RelativeLayout rl_album;
        RelativeLayout rl_add_album;
        ImageView iv_add_album;
    }
}
