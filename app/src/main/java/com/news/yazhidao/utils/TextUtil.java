package com.news.yazhidao.utils;

import android.content.Context;
import android.graphics.Color;
import android.text.TextUtils;
import android.util.Log;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.handmark.pulltorefresh.library.internal.LoadingLayout;
import com.news.yazhidao.R;
import com.news.yazhidao.widget.TextViewExtend;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
        } else{
            tv_news_category.setBackgroundResource(R.drawable.bg_category_right_keji);
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

        if(TextUtils.isEmpty(source_name)){
            return;
        }
        if (source_name.contains("凤凰网")) {
            iv_source.setBackgroundResource(R.drawable.fenghuangwang);
        } else if( source_name.contains("网易")) {
            iv_source.setBackgroundResource(R.drawable.yi);
        } else if( source_name.contains("zhihu")) {
            iv_source.setBackgroundResource(R.drawable.zhihu);
        } else if( source_name.contains("weibo")) {
            iv_source.setBackgroundResource(R.drawable.weibo);
        } else if( source_name.contains("国际在线")) {
            iv_source.setBackgroundResource(R.drawable.guojizaixian);
        } else if( source_name.contains("新浪网")) {
            iv_source.setBackgroundResource(R.drawable.xinlang);
        } else if( source_name.contains("搜狐")) {
            iv_source.setBackgroundResource(R.drawable.souhu);
        } else if( source_name.contains("腾讯")) {
            iv_source.setBackgroundResource(R.drawable.tengxun);
        } else if( source_name.contains("中国经济报")) {
            iv_source.setBackgroundResource(R.drawable.zhongguojingjibao);
        } else if( source_name.contains("中国经济网")) {
            iv_source.setBackgroundResource(R.drawable.zhongguojingjiwang);
        } else if( source_name.contains("人民网")) {
            iv_source.setBackgroundResource(R.drawable.renminwang);
        } else if( source_name.contains("经济参考报")) {
            iv_source.setBackgroundResource(R.drawable.jingjicankaobao);
        } else if( source_name.contains("南方网")) {
            iv_source.setBackgroundResource(R.drawable.nanfang);
        } else if( source_name.contains("中工网")) {
            iv_source.setBackgroundResource(R.drawable.zhonggongwang);
        } else if( source_name.contains("央视网")) {
            iv_source.setBackgroundResource(R.drawable.yangshiwang);
        } else if( source_name.contains("金融街")) {
            iv_source.setBackgroundResource(R.drawable.jinrongjie);
        } else if( source_name.contains("南海网")) {
            iv_source.setBackgroundResource(R.drawable.nanhaiwang);
        } else if( source_name.contains("36氪")) {
            iv_source.setBackgroundResource(R.drawable.thirty_six_ke);
        } else if( source_name.contains("环球网")) {
            iv_source.setBackgroundResource(R.drawable.huanqiuwang);
        } else if( source_name.contains("解放牛网")) {
            iv_source.setBackgroundResource(R.drawable.jiefangniuwang);
        } else if( source_name.contains("21CN")) {
            iv_source.setBackgroundResource(R.drawable.twenty_one_cn);
        } else if( source_name.contains("中金在线")) {
            iv_source.setBackgroundResource(R.drawable.zhongjinzaixian);
        } else if( source_name.contains("证券之星")) {
            iv_source.setBackgroundResource(R.drawable.zhengquanzhixing);
        } else if( source_name.contains("太平洋电脑网")) {
            iv_source.setBackgroundResource(R.drawable.taipingyangdiannaowang);
        } else if( source_name.contains("中关村在线")) {
            iv_source.setBackgroundResource(R.drawable.zhongguancunzaixian);
        } else if( source_name.contains("红网")) {
            iv_source.setBackgroundResource(R.drawable.hongwang);
        } else if( source_name.contains("北青网")) {
            iv_source.setBackgroundResource(R.drawable.beiqingwang);
        } else if( source_name.contains("sports.cn")) {
            iv_source.setBackgroundResource(R.drawable.sportscn);
        } else if( source_name.contains("新民网")) {
            iv_source.setBackgroundResource(R.drawable.xinmin);
        } else if( source_name.contains("中国山东网")) {
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

    /**
     * InputStream 转换成byte[]
     * @param input
     * @return
     * @throws IOException
     */
    public static byte[] toByteArray(InputStream input) throws IOException {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        byte[] buffer = new byte[4096];
        int n = 0;
        while (-1 != (n = input.read(buffer))) {
            output.write(buffer, 0, n);
        }
        return output.toByteArray();
    }

    //把 所有的半角符号转化为全角符号
    public static String ToDBC(String input) {
        char[] c = input.toCharArray();
        for (int i = 0; i< c.length; i++) {
            if (c[i] == 12288) {
                c[i] = (char) 32;
                continue;
            }if (c[i]> 65280&& c[i]< 65375)
                c[i] = (char) (c[i] - 65248);
        }
        return new String(c);
    }
    /**
     * 随机产生一个颜色值给专辑做背景色
     */
    public static int getRandomColor4Special(Context mContext){
        String[] colorArr = mContext.getResources().getStringArray(R.array.bg_special_colors);
        Random random = new Random();
        int index = random.nextInt(colorArr.length);
        return Color.parseColor(colorArr[index]);
    }

    /**
     * 专辑列表获取对应的背景
     * @return
     */
    public static int getSpecialBgPic(int positon){
        positon = positon % 8;
        int resource = R.drawable.bg_special1;
        switch (positon){
            case 1:
                resource = R.drawable.bg_special1;
                break;
            case 2:
                resource = R.drawable.bg_special2;
                break;
            case 3:
                resource = R.drawable.bg_special3;
                break;
            case 4:
                resource = R.drawable.bg_special4;
                break;
            case 5:
                resource = R.drawable.bg_special5;
                break;
            case 6:
                resource = R.drawable.bg_special6;
                break;
            case 7:
                resource = R.drawable.bg_special7;
                break;
            case 8:
                resource = R.drawable.bg_special8;
                break;
        }
        return resource;
    }
    /**
     * 获取别的app分享进来的新闻链接中的url
     * @param data
     */
    public static String  getNewsTitle(String data) {
        String regEx4Title = "[\\((][\\s\\S]*[\\))]";
        String regEx4Url = "(https://|http://)?([\\w-]+\\.)+[\\w-]+(/[\\w-./?%&=]*)?";
        //去除多余文字
        Pattern patternTitle = Pattern.compile(regEx4Title);
        Matcher matcherTitle = patternTitle.matcher(data);
        while (matcherTitle.find()) {
            data = data.replace(matcherTitle.group(),"");
        }
        data = data.replace("【","").replace("】", "");

        //去除多余的url

        Pattern patternUrl = Pattern.compile(regEx4Url);
        Matcher matcherUrl = patternUrl.matcher(data);
        while (matcherUrl.find()){
            data = data.replace(matcherUrl.group(),"");
        }
        return data.replace("\n","");
    }

    /**
     * 获取别的app分享进来的新闻链接中的标题
     * @param data
     * @return
     */
    public static String getNewsUrl(String data) {
        String regEx4Url = "(https://|http://)?([\\w-]+\\.)+[\\w-]+(/[\\w-./?%&=]*)?";
        Pattern pattern = Pattern.compile(regEx4Url);
        Matcher matcher = pattern.matcher(data);
        if (matcher.find()){
            data = matcher.group();
        }
        return data;
    }
}
