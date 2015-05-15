package com.news.yazhidao.pages;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;

import com.news.yazhidao.R;
import com.news.yazhidao.entity.NewsFeed;
import com.news.yazhidao.utils.helper.ShareSdkHelper;

import cn.sharesdk.douban.Douban;
import cn.sharesdk.renren.Renren;
import cn.sharesdk.sina.weibo.SinaWeibo;
import cn.sharesdk.tencent.weibo.TencentWeibo;
import cn.sharesdk.wechat.friends.Wechat;
import cn.sharesdk.wechat.moments.WechatMoments;

/**
 * Created by fengjigang on 14/11/12.
 */
public class ShareSdkAty extends Activity {
    private static final String TAG = "ShareSdkAty";
    AlertDialog dialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(1);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setItems(R.array.share_list,new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String platform=null;
                switch (which){
                    case 0:
                        platform=SinaWeibo.NAME;
                        break;
                    case 1:
                        platform= Wechat.NAME;
                        break;
                    case 2:
                        platform= WechatMoments.NAME;
                        break;
                    case 3:
                        platform= TencentWeibo.NAME;
                        break;
                    case 4:
                        platform= Renren.NAME;
                        break;
                    case 5:
                        platform= Douban.NAME;
                        break;
                }
                ShareSdkHelper.ShareToPlatform(ShareSdkAty.this, platform,new NewsFeed());
            }
        });
        this.dialog = builder.create();
        this.dialog.setCancelable(true);
        this.dialog.show();
        this.dialog.setOnDismissListener(new DialogInterface.OnDismissListener()
        {
            public final void onDismiss(DialogInterface paramAnonymousDialogInterface)
            {
                ShareSdkAty.this.finish();
            }
        });
    }


}
