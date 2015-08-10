package com.news.yazhidao.pages;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.reflect.TypeToken;
import com.news.yazhidao.R;
import com.news.yazhidao.common.HttpConstant;
import com.news.yazhidao.entity.DiggerAlbum;
import com.news.yazhidao.entity.Element;
import com.news.yazhidao.entity.User;
import com.news.yazhidao.listener.UserLoginListener;
import com.news.yazhidao.net.JsonCallback;
import com.news.yazhidao.net.MyAppException;
import com.news.yazhidao.net.NetworkRequest;
import com.news.yazhidao.utils.ToastUtil;
import com.news.yazhidao.utils.manager.SharedPreManager;
import com.news.yazhidao.widget.CloudTagManager;
import com.news.yazhidao.widget.LoginModePopupWindow;

import org.apache.http.NameValuePair;

import java.util.ArrayList;
import java.util.List;

import cn.sharesdk.framework.PlatformDb;

/**
 * Created by Berkeley on 7/20/15.
 */
public class BaseTagActivity extends Activity implements View.OnClickListener {
    /**热门话题key*/
    public final static String KEY_HOT_TOPIC = "key_hot_topic";
    public ArrayList<String> keywords;
    public ArrayList<String> hotwords;
    private int[] array = new int[8];
    private ArrayList<DiggerAlbum> mAlbumDatas;

    private CloudTagManager keywordsFlow;
    private Button btnchange, btnexperence;
    private ImageView back_imageView;
    private LengjingFgt fgt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        setContentView(R.layout.item_tag_cloud);
        btnchange = (Button) findViewById(R.id.btnchange);
        btnexperence = (Button) findViewById(R.id.btnexperence);
        back_imageView = (ImageView) findViewById(R.id.back_imageView);
        back_imageView.setOnClickListener(BaseTagActivity.this);
        btnchange.setOnClickListener(BaseTagActivity.this);
        btnexperence.setOnClickListener(BaseTagActivity.this);
        keywords = new ArrayList<String>();
        hotwords = new ArrayList<String>();
        fgt = new LengjingFgt(BaseTagActivity.this);

        mAlbumDatas = new ArrayList();

        loadElements();

        super.onCreate(savedInstanceState);
    }

    private static void feedKeywordsFlow(CloudTagManager keywordsFlow,
                                         ArrayList<String> arr) {
        keywordsFlow.rubKeywords();

        for (int i = 0; i < arr.size(); i++) {
            String tmp = arr.get(i);
            keywordsFlow.feedKeyword(tmp);
        }
    }

    @Override
    public void onClick(View v) {
        if (v == btnchange) {
            if(keywordsFlow != null && keywords != null) {
                keywords.clear();

                array = createRandomArray(hotwords.size());
                for (int a = 0; a < array.length; a++) {
                    keywords.add(hotwords.get(array[a]));
                }

                // 添加
                feedKeywordsFlow(keywordsFlow, keywords);
                keywordsFlow.go2Show(CloudTagManager.ANIMATION_OUT);
            }

        } else if (v == btnexperence) {
        } else if (v instanceof TextView) {
            final String keyword = ((TextView) v).getText().toString();
            /**首先判断用户是否登录,如果没有登录的话,则弹出登录框*/
            User user = SharedPreManager.getUser(this);
            if (user == null){
                final LoginModePopupWindow window = new LoginModePopupWindow(this, new UserLoginListener() {
                    @Override
                    public void userLogin(String platform, PlatformDb platformDb) {
                    /**通知棱镜界面界面进行挖掘处理*/
                    Intent intent = new Intent(LengjingFgt.ACTION_USER_CHOSE_TOPIC);
                    intent.putExtra(KEY_HOT_TOPIC,keyword);
                    sendBroadcast(intent);
                    BaseTagActivity.this.finish();
                    }

                    @Override
                    public void userLogout() {

                    }
                }, null);
                window.showAtLocation(this.getWindow().getDecorView(), Gravity.CENTER
                        | Gravity.CENTER, 0, 0);
                return;
            }
        }else if(v == back_imageView){
            BaseTagActivity.this.finish();
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


    private void loadElements() {
        final NetworkRequest request = new NetworkRequest(HttpConstant.URL_FETCH_ELEMENTS, NetworkRequest.RequestMethod.POST);
        List<NameValuePair> pairs = new ArrayList<>();

        request.setParams(pairs);
        request.setCallback(new JsonCallback<ArrayList<Element>>() {

            public void success(ArrayList<Element> result) {

                if (result != null) {
                    for (int i = 0; i < result.size(); i++) {
                        hotwords.add(result.get(i).getTitle());
                    }

                    array = createRandomArray(hotwords.size());

                    for (int a = 0; a < array.length; a++) {
                        keywords.add(hotwords.get(array[a]));
                    }

                    keywordsFlow = (CloudTagManager) findViewById(R.id.keywordsFlow);
                    keywordsFlow.setDuration(800l);
                    keywordsFlow.setOnItemClickListener(BaseTagActivity.this);


                    // 添加
                    feedKeywordsFlow(keywordsFlow, keywords);
                    keywordsFlow.go2Show(CloudTagManager.ANIMATION_OUT);
                }

            }

            public void failed(MyAppException exception) {
                ToastUtil.toastLong("fail");
            }
        }.setReturnType(new TypeToken<ArrayList<Element>>() {
        }.getType()));
        request.execute();
    }

    private int[] createRandomArray(int size) {
        int[] list = new int[7];
        int count = 0; //计数
        int num = 0;  //随机数
        int i;
        //填充数组元素
        //判断元素是否存在数组中
        for (i = 0; i < 7; i++) {
            boolean flag = false;
            num = (int) (Math.random() * size);
            list[i] = num;
//            for(int a = 0; a < list.length; a ++){
//                if(list[a] == num){
//                    flag = true;
//                }
//            }
//
//            if(!flag) {
//                list[i] = num;
//            }
        }

        return list;
    }
}
