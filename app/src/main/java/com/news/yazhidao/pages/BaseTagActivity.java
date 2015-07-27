package com.news.yazhidao.pages;

import android.app.Activity;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.news.yazhidao.R;
import com.news.yazhidao.widget.CloudTagManager;

import java.util.Random;

/**
 * Created by Berkeley on 7/20/15.
 */
public class BaseTagActivity extends Activity implements View.OnClickListener {

    public static final String[] keywords = {"下雨啦", "墨迹天气", "豆瓣", "Diablo3",
            "魔兽争霸", "Dota", "我是一只大袋鼠", "崔健"};

    //    , "九月", "十二", "五月天", "夏天的故事", "哈哈",
//            "星巴克", "乐知天命"
    private CloudTagManager keywordsFlow;
    private Button btnchange, btnexperence;
    private GestureDetector gestureDetector;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        setContentView(R.layout.item_tag_cloud);
        btnchange = (Button) findViewById(R.id.btnchange);
        btnexperence = (Button) findViewById(R.id.btnexperence);
        btnchange.setOnClickListener(BaseTagActivity.this);
        btnexperence.setOnClickListener(BaseTagActivity.this);
        keywordsFlow = (CloudTagManager) findViewById(R.id.keywordsFlow);
        keywordsFlow.setDuration(800l);
        keywordsFlow.setOnItemClickListener(BaseTagActivity.this);
        // 添加
        feedKeywordsFlow(keywordsFlow, keywords);
        keywordsFlow.go2Show(CloudTagManager.ANIMATION_IN);

        gestureDetector = new GestureDetector(new DefaultGestureDetector());

        super.onCreate(savedInstanceState);
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
        if (v == btnchange) {
            flyOut();
        } else if (v == btnexperence) {
        } else if (v instanceof TextView) {
            String keyword = ((TextView) v).getText().toString();
            Toast.makeText(this, keyword, Toast.LENGTH_LONG).show();
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

            flyOut();

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
