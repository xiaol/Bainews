package com.news.yazhidao.widget.imagewall;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.FrameLayout;

public class ImageWallView extends FrameLayout {


    public ImageWallView(Context context) {
        this(context, null);
    }

    public ImageWallView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ImageWallView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void addSource(Object source,int styleconstant){
        ViewWall.add(this, source,styleconstant);
    }
}
