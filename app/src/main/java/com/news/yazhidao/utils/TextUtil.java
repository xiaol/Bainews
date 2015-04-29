package com.news.yazhidao.utils;

import android.graphics.Color;
import android.text.TextUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.handmark.pulltorefresh.library.internal.LoadingLayout;
import com.news.yazhidao.R;
import com.news.yazhidao.widget.TextViewExtend;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

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

    public static void setTopLineBackground(String category,LinearLayout ll_top_line) {

        if ("焦点".equals(category)) {
            ll_top_line.setBackgroundColor(new Color().parseColor("#ff4341"));
        } else if ("国际".equals(category)) {
            ll_top_line.setBackgroundColor(new Color().parseColor("#007fff"));
        } else if ("港台".equals(category)) {
            ll_top_line.setBackgroundColor(new Color().parseColor("#726bf8"));
        } else if ("内地".equals(category)) {
            ll_top_line.setBackgroundColor(new Color().parseColor("#18a68b"));
        } else if ("财经".equals(category)) {
            ll_top_line.setBackgroundColor(new Color().parseColor("#32bfcd"));
        } else if ("科技".equals(category)) {
            ll_top_line.setBackgroundColor(new Color().parseColor("#007fff"));
        } else if ("体育".equals(category)) {
            ll_top_line.setBackgroundColor(new Color().parseColor("#df8145"));
        } else if ("社会".equals(category)) {
            ll_top_line.setBackgroundColor(new Color().parseColor("#00b285"));
        } else if ("国内".equals(category)) {
            ll_top_line.setBackgroundColor(new Color().parseColor("#726bf8"));
        } else if ("娱乐".equals(category)) {
            ll_top_line.setBackgroundColor(new Color().parseColor("#ff7272"));
        }

    }

    public static void setViewCompatBackground(String category,LoadingLayout loadingLayout) {

        if ("焦点".equals(category)) {
            loadingLayout.setHeaderBackground("#ff4341");
        } else if ("国际".equals(category)) {
            loadingLayout.setHeaderBackground("#007fff");
        } else if ("港台".equals(category)) {
            loadingLayout.setHeaderBackground("#726bf8");
        } else if ("内地".equals(category)) {
            loadingLayout.setHeaderBackground("#18a68b");
        } else if ("财经".equals(category)) {
            loadingLayout.setHeaderBackground("#32bfcd");
        } else if ("科技".equals(category)) {
            loadingLayout.setHeaderBackground("#007fff");
        } else if ("体育".equals(category)) {
            loadingLayout.setHeaderBackground("#df8145");
        } else if ("社会".equals(category)) {
            loadingLayout.setHeaderBackground("#00b285");
        } else if ("国内".equals(category)) {
            loadingLayout.setHeaderBackground("#726bf8");
        } else if ("娱乐".equals(category)) {
            loadingLayout.setHeaderBackground("#ff7272");
        }

    }


    //卡片2类别背景设置
    public static void setNewsBackGround(TextViewExtend tv_news_category, String category) {
        if ("焦点".equals(category)) {
            tv_news_category.setBackgroundResource(R.drawable.bg_category_bottom_jiaodian);
        } else if ("国际".equals(category)) {
            tv_news_category.setBackgroundResource(R.drawable.bg_category_bottom_guoji);
        } else if ("港台".equals(category)) {
            tv_news_category.setBackgroundResource(R.drawable.bg_category_bottom_gangtai);
        } else if ("内地".equals(category)) {
            tv_news_category.setBackgroundResource(R.drawable.bg_category_bottom_neidi);
        } else if ("财经".equals(category)) {
            tv_news_category.setBackgroundResource(R.drawable.bg_category_bottom_caijing);
        } else if ("科技".equals(category)) {
            tv_news_category.setBackgroundResource(R.drawable.bg_category_bottom_keji);
        } else if ("体育".equals(category)) {
            tv_news_category.setBackgroundResource(R.drawable.bg_category_bottom_tiyu);
        } else if ("社会".equals(category)) {
            tv_news_category.setBackgroundResource(R.drawable.bg_category_bottom_shehui);
        } else if ("国内".equals(category)) {
            tv_news_category.setBackgroundResource(R.drawable.bg_category_bottom_guonei);
        } else if ("娱乐".equals(category)) {
            tv_news_category.setBackgroundResource(R.drawable.bg_category_bottom_yule);
        }
    }


    //卡片3类别背景设置
    public static void setNewsBackGroundRight(TextViewExtend tv_news_category, String category) {
        if ("焦点".equals(category)) {
            tv_news_category.setBackgroundResource(R.drawable.bg_category_right_jiaodian);
        } else if ("国际".equals(category)) {
            tv_news_category.setBackgroundResource(R.drawable.bg_category_right_guoji);
        } else if ("港台".equals(category)) {
            tv_news_category.setBackgroundResource(R.drawable.bg_category_right_gangtai);
        } else if ("内地".equals(category)) {
            tv_news_category.setBackgroundResource(R.drawable.bg_category_right_neidi);
        } else if ("财经".equals(category)) {
            tv_news_category.setBackgroundResource(R.drawable.bg_category_right_caijing);
        } else if ("科技".equals(category)) {
            tv_news_category.setBackgroundResource(R.drawable.bg_category_right_keji);
        } else if ("体育".equals(category)) {
            tv_news_category.setBackgroundResource(R.drawable.bg_category_right_tiyu);
        } else if ("社会".equals(category)) {
            tv_news_category.setBackgroundResource(R.drawable.bg_category_right_shehui);
        } else if ("国内".equals(category)) {
            tv_news_category.setBackgroundResource(R.drawable.bg_category_right_guonei);
        } else if ("娱乐".equals(category)) {
            tv_news_category.setBackgroundResource(R.drawable.bg_category_right_yule);
        }
    }

    public static void setTextBackGround(TextView tv_news_category, String category) {

        if ("焦点".equals(category)) {
            tv_news_category.setBackgroundResource(R.drawable.bg_category_jiaodian);
        } else if ("国际".equals(category)) {
            tv_news_category.setBackgroundResource(R.drawable.bg_category_guoji);
        } else if ("港台".equals(category)) {
            tv_news_category.setBackgroundResource(R.drawable.bg_category_gangtai);
        } else if ("内地".equals(category)) {
            tv_news_category.setBackgroundResource(R.drawable.bg_category_neidi);
        } else if ("财经".equals(category)) {
            tv_news_category.setBackgroundResource(R.drawable.bg_category_caijing);
        } else if ("科技".equals(category)) {
            tv_news_category.setBackgroundResource(R.drawable.bg_category_keji);
        } else if ("体育".equals(category)) {
            tv_news_category.setBackgroundResource(R.drawable.bg_category_tiyu);
        } else if ("社会".equals(category)) {
            tv_news_category.setBackgroundResource(R.drawable.bg_category_shehui);
        } else if ("国内".equals(category)) {
            tv_news_category.setBackgroundResource(R.drawable.bg_category_guonei);
        }else if ("娱乐".equals(category)) {
            tv_news_category.setBackgroundResource(R.drawable.bg_category_yule);
        }

    }
    public static void setResourceSiteIcon(ImageView iv_source, String source_name) {


        if ("凤凰网".equals(source_name)) {
            iv_source.setBackgroundResource(R.drawable.fenghuangwang);
        } else if ("网易".equals(source_name)) {
            iv_source.setBackgroundResource(R.drawable.yi);
        } else if ("zhihu".equals(source_name)) {
            iv_source.setBackgroundResource(R.drawable.zhihu);
        } else if ("weibo".equals(source_name)) {
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

    /**
     * 获取inputStream中的数据
     * @param in
     * @return
     */
    public static String getResponseContent(InputStream in) {
        StringBuffer sb = new StringBuffer();
        BufferedReader br = new BufferedReader(new InputStreamReader(in));
        try {
            String line = null;
            while((line=br.readLine())!=null){
                sb.append(line);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }finally{
            try {
                br.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return sb.toString();
    }
}
