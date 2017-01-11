package com.news.yazhidao.widget;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

/**
 * Created by fengjigang on 16/6/29.
 */
public class SwipeBackViewpager extends ViewPager {
    public SwipeBackViewpager(Context context) {
        super(context);
    }

    public SwipeBackViewpager(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    float x_tmp1 = 0;
    float y_tmp1 = 0;
    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        int currentItem = getCurrentItem();

        switch (event.getAction()){
            case MotionEvent.ACTION_DOWN:
                x_tmp1 = event.getX(0);
                y_tmp1 = event.getY(0);
                if (getCurrentItem() == 1){
                    getParent().requestDisallowInterceptTouchEvent(true);
                }
                break;
            case MotionEvent.ACTION_MOVE:
                if (event.getX(0) - x_tmp1 > 0){//right
                    if (currentItem == 1){
                        getParent().requestDisallowInterceptTouchEvent(true);
                    }else {
                        if (y_tmp1 > 210*4){
                            getParent().requestDisallowInterceptTouchEvent(false);
                        }else {
                            getParent().requestDisallowInterceptTouchEvent(true);
                        }
                    }
                }else {
                    if (currentItem == 0){
                        if (y_tmp1 > 210*4){
                            getParent().requestDisallowInterceptTouchEvent(true);
                        }
                    }else {
                        getParent().requestDisallowInterceptTouchEvent(false);
                    }
                }
                break;
            case MotionEvent.ACTION_UP:
                break;
        }

        return super.dispatchTouchEvent(event);
    }
}
