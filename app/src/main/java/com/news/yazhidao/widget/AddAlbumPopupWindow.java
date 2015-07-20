package com.news.yazhidao.widget;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.news.yazhidao.R;
import com.news.yazhidao.entity.Album;

import java.util.ArrayList;


/**
 * Created by ariesy on 2015/7/16.
 */
public class AddAlbumPopupWindow extends PopupWindow {

    private Context m_pContext;
    private View mMenuView;
    private ArrayList<Album> albumList;
    private AddAlbumListener listener;
    private Album album;

    private TextView tv_cancel;
    private TextView tv_add_album;
    private TextView tv_new;
    private EditText et_name;
    private EditText et_des;


    public AddAlbumPopupWindow(Context context,AddAlbumListener listener) {
        super(context);
        this.listener = listener;
        m_pContext = context;
        findHeadPortraitImageViews();
        loadData();


    }

    private void findHeadPortraitImageViews() {

        mMenuView = View.inflate(m_pContext, R.layout.rl_add_album,null);

        tv_cancel = (TextView) mMenuView.findViewById(R.id.tv_cancel);
        tv_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        tv_add_album = (TextView) mMenuView.findViewById(R.id.tv_add_album);
        tv_new = (TextView) mMenuView.findViewById(R.id.tv_new);
        tv_new.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                album = new Album();
                album.setAlbum(et_name.getText().toString());
                album.setDescription(et_des.getText().toString());
                album.setSelected(false);

                dismiss();
            }
        });
        et_name = (EditText) mMenuView.findViewById(R.id.et_name);
        et_des = (EditText) mMenuView.findViewById(R.id.et_des);

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
//        //实例化一个ColorDrawable颜色为半透明
//        ColorDrawable dw = new ColorDrawable(0xffffff);
//        //设置SelectPicPopupWindow弹出窗体的背景
//        this.setBackgroundDrawable(dw);
    }

    @Override
    public void dismiss() {
        super.dismiss();
        if(album != null) {
            listener.add(album);
        }

    }

    private void loadData() {

    }

    public interface AddAlbumListener {
        void add(Album album);
    }
}
