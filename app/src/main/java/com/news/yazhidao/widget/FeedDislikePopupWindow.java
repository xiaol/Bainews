package com.news.yazhidao.widget;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.PopupWindow;

import com.news.yazhidao.R;

/**
 * Created by fengjigang on 16/4/5.
 */
public class FeedDislikePopupWindow extends PopupWindow {
    private final View rootView;

    public FeedDislikePopupWindow(Context mContext){
        super(mContext);
        LayoutInflater inflater = (LayoutInflater) mContext
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        rootView = inflater.inflate(R.layout.popup_dislike, null);
        setContentView(rootView);
    }
}
