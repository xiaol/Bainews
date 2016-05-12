package com.news.yazhidao.pages;

import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import com.news.yazhidao.R;
import com.news.yazhidao.common.BaseActivity;
import com.news.yazhidao.entity.User;
import com.news.yazhidao.utils.manager.SharedPreManager;

/**
 * Created by xiao on 2016/5/5.
 */
public class MyTestActivity extends BaseActivity implements View.OnClickListener{
    private EditText et;
    private Button bt1,bt2;
    private TextView textView;
    private User user;
    @Override
    protected void setContentView() {
        setContentView(R.layout.xzj_test_activity);
    }

    @Override
    protected void initializeViews() {
        et = (EditText) this.findViewById(R.id.et_content);
        bt1 = (Button)this.findViewById(R.id.button);
        bt2 = (Button)this.findViewById(R.id.button3);
        textView = (TextView)this.findViewById(R.id.textView);
        bt1.setOnClickListener(this);
        bt2.setOnClickListener(this);
    }

    @Override
    protected void loadData() {

        textView.setText(SharedPreManager.getUUID());
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.button:
                break;
            case R.id.button3:
                break;
        }
    }
}
