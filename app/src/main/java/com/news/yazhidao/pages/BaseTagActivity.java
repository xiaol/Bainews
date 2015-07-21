package com.news.yazhidao.pages;

import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.news.yazhidao.R;
import com.news.yazhidao.common.BaseActivity;
import com.news.yazhidao.widget.CloudTagManager;

import java.util.Random;

/**
 * Created by Berkeley on 7/20/15.
 */
public class BaseTagActivity extends BaseActivity implements View.OnClickListener{

    public static final String[] keywords = { "下雨啦", "墨迹天气", "豆瓣", "Diablo3",
            "魔兽争霸", "Dota", "音乐", "崔健", "九月", "十二", "五月天", "夏天的故事", "我是一只大袋鼠",
            "星巴克", "乐知天命" };
    private CloudTagManager keywordsFlow;
    private Button btnIn, btnOut;
    private GestureDetector gestureDetector;

    @Override
    protected void setContentView() {
        setContentView(R.layout.activity_basetag);
    }

    @Override
    protected void initializeViews() {

        btnIn = (Button) findViewById(R.id.btnIn);
        btnOut = (Button) findViewById(R.id.btnOut);
        btnIn.setOnClickListener(BaseTagActivity.this);
        btnOut.setOnClickListener(BaseTagActivity.this);
        keywordsFlow = (CloudTagManager) findViewById(R.id.keywordsFlow);
        keywordsFlow.setDuration(800l);
        keywordsFlow.setOnItemClickListener(BaseTagActivity.this);
        // 添加
        feedKeywordsFlow(keywordsFlow, keywords);
        keywordsFlow.go2Show(CloudTagManager.ANIMATION_IN);

        gestureDetector = new GestureDetector(new DefaultGestureDetector());

    }

    @Override
    protected void loadData() {

    }

    private static void feedKeywordsFlow(CloudTagManager keywordsFlow,
                                         String[] arr) {
        for (int i = 0; i < arr.length; i++) {
            String tmp = arr[i];
            keywordsFlow.feedKeyword(tmp);
        }
    }

    @Override
    public void onClick(View v) {
        if (v == btnIn) {
            flyIn();
        } else if (v == btnOut) {
            flyOut();
        } else if (v instanceof TextView) {
            String keyword = ((TextView) v).getText().toString();
            Toast.makeText(this, keyword, 0).show();
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return gestureDetector.onTouchEvent(event);
    }

    private class DefaultGestureDetector extends GestureDetector.SimpleOnGestureListener {
        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
                               float velocityY) {
            final int FLING_MIN_DISTANCE = 100;//X或者y轴上移动的距离(像素)
            final int FLING_MIN_VELOCITY = 100;//x或者y轴上的移动速度(像素/秒)
            Random random = new Random();

            if (random.nextInt(2) == 0) {
                flyIn();
            } else {
                flyOut();
            }
            //			if ((e1.getX() - e2.getX()) > FLING_MIN_DISTANCE
            //					&& Math.abs(velocityX) > FLING_MIN_VELOCITY) {
            //			} else if ((e2.getX() - e1.getX()) > FLING_MIN_DISTANCE
            //					&& Math.abs(velocityX) > FLING_MIN_VELOCITY) {
            //			} else if ((e1.getY() - e2.getY()) > FLING_MIN_DISTANCE
            //					&& Math.abs(velocityY) > FLING_MIN_VELOCITY) {
            //			} else if ((e2.getY() - e1.getY()) > FLING_MIN_DISTANCE
            //					&& Math.abs(velocityY) > FLING_MIN_VELOCITY) {
            //			} else {
            //			}
            return false;
        }
    }

    private void flyIn() {
        keywordsFlow.rubKeywords();
        // keywordsFlow.rubAllViews();
        feedKeywordsFlow(keywordsFlow, keywords);
        keywordsFlow.go2Show(CloudTagManager.ANIMATION_IN);
    }

    private void flyOut() {
        keywordsFlow.rubKeywords();
        // keywordsFlow.rubAllViews();
        feedKeywordsFlow(keywordsFlow, keywords);
        keywordsFlow.go2Show(CloudTagManager.ANIMATION_OUT);
    }
}
