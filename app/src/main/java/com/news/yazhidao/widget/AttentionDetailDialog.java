package com.news.yazhidao.widget;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.news.yazhidao.R;

/**
 * Created by Administrator on 2016/7/18.
 */
public class AttentionDetailDialog extends Dialog{
    private Context mContext;
    private String attentionName;
    private TextView tv_attentionDetailDialog_content2,
            tv_attentionDetailDialog_know;

    public AttentionDetailDialog(Context context,String contentString) {
        super(context, R.style.dialogsuccess);
        mContext = context;
        this.attentionName = contentString;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.attention_detail_dialog);
        tv_attentionDetailDialog_content2 = (TextView) findViewById(R.id.tv_attentionDetailDialog_content2);
        tv_attentionDetailDialog_know = (TextView) findViewById(R.id.tv_attentionDetailDialog_know);
        tv_attentionDetailDialog_content2.setText("查看\""+attentionName+"\"的内容");
        tv_attentionDetailDialog_know.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });
    }
}
