package com.news.yazhidao.utils;

import android.text.TextUtils;
import android.widget.ImageView;

import com.news.yazhidao.R;

/**
 * Created by fengjigang on 15/1/27.
 */
public class TextUtil {
    public static String convertTemp(String origin) {
        if (!TextUtils.isEmpty(origin)) {
            return origin.replace("度", "℃");
        }
        return "";
    }

    public static String trimBlankSpace(String data){
        if(data.contains("  ")){
        int index=data.indexOf("  ");
        StringBuilder before=new StringBuilder(data.replace("  ","").substring(0,index));
        StringBuilder after=new StringBuilder(data.replace("  ","").substring(index));
        return before+"  "+after;
        }
        return data;
    }
    public static void setResourceSiteIcon(ImageView iv_source, String source_name) {


        if ("凤凰网".equals(source_name)) {
            iv_source.setBackgroundResource(R.drawable.fenghuangwang);
        } else if ("网易".equals(source_name)) {
            iv_source.setBackgroundResource(R.drawable.yi);
        } else if ("zhihu".equals(source_name)) {
            iv_source.setBackgroundResource(R.drawable.zhihu);
        } else if ("微博".equals(source_name)) {
            iv_source.setBackgroundResource(R.drawable.weibo);
        } else if ("国际在线".equals(source_name)) {
            iv_source.setBackgroundResource(R.drawable.guojizaixian);
        } else if ("新浪网".equals(source_name)) {
            iv_source.setBackgroundResource(R.drawable.xinlang);
        } else if ("搜狐".equals(source_name)) {
            iv_source.setBackgroundResource(R.drawable.souhu);
        } else if ("腾讯".equals(source_name)) {
            iv_source.setBackgroundResource(R.drawable.tengxun);
        } else if ("中国经济报".equals(source_name)) {
            iv_source.setBackgroundResource(R.drawable.zhongguojingjibao);
        } else if ("中国经济网".equals(source_name)) {
            iv_source.setBackgroundResource(R.drawable.zhongguojingjiwang);
        } else if ("人民网".equals(source_name)) {
            iv_source.setBackgroundResource(R.drawable.renminwang);
        } else if ("经济参考报".equals(source_name)) {
            iv_source.setBackgroundResource(R.drawable.jingjicankaobao);
        } else if ("南方网".equals(source_name)) {
            iv_source.setBackgroundResource(R.drawable.nanfang);
        } else if ("中工网".equals(source_name)) {
            iv_source.setBackgroundResource(R.drawable.zhonggongwang);
        } else if ("央视网".equals(source_name)) {
            iv_source.setBackgroundResource(R.drawable.yangshiwang);
        } else if ("金融街".equals(source_name)) {
            iv_source.setBackgroundResource(R.drawable.jinrongjie);
        } else if ("南海网".equals(source_name)) {
            iv_source.setBackgroundResource(R.drawable.nanhaiwang);
        } else if ("36氪".equals(source_name)) {
            iv_source.setBackgroundResource(R.drawable.thirty_six_ke);
        } else if ("环球网".equals(source_name)) {
            iv_source.setBackgroundResource(R.drawable.huanqiuwang);
        } else if ("解放牛网".equals(source_name)) {
            iv_source.setBackgroundResource(R.drawable.jiefangniuwang);
        } else if ("21CN".equals(source_name)) {
            iv_source.setBackgroundResource(R.drawable.twenty_one_cn);
        } else if ("中金在线".equals(source_name)) {
            iv_source.setBackgroundResource(R.drawable.zhongjinzaixian);
        } else if ("证券之星".equals(source_name)) {
            iv_source.setBackgroundResource(R.drawable.zhengquanzhixing);
        } else if ("太平洋电脑网".equals(source_name)) {
            iv_source.setBackgroundResource(R.drawable.taipingyangdiannaowang);
        } else if ("中关村在线".equals(source_name)) {
            iv_source.setBackgroundResource(R.drawable.zhongguancunzaixian);
        } else if ("红网".equals(source_name)) {
            iv_source.setBackgroundResource(R.drawable.hongwang);
        } else if ("北青网".equals(source_name)) {
            iv_source.setBackgroundResource(R.drawable.beiqingwang);
        } else if ("sports.cn".equals(source_name)) {
            iv_source.setBackgroundResource(R.drawable.sportscn);
        } else if ("新民网".equals(source_name)) {
            iv_source.setBackgroundResource(R.drawable.xinmin);
        } else if ("中国山东网".equals(source_name)) {
            iv_source.setBackgroundResource(R.drawable.zhongguoshandongwang);
        } else {
            iv_source.setBackgroundResource(R.drawable.other);
        }

    }

    public static String trimEnterInContent(String[] split) {
        StringBuilder _StringB=new StringBuilder();
        for(int i=0;i<split.length;i++){
            if(i!=split.length-1){
                _StringB.append(split[i]+"\n");
            }else{
                _StringB.append(split[i]);
            }
        }
        return _StringB.toString();
    }
}
