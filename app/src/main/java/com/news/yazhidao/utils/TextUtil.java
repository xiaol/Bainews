package com.news.yazhidao.utils;

import android.text.TextUtils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by fengjigang on 15/1/27.
 */
public class TextUtil {
    public static String convertTemp(String origin) {
        if (!TextUtils.isEmpty(origin)) {
            return origin.replace("度", "°C");
        }
        return "";
    }
    public static int parsePraiseNumber(String praise){
        String digit_reg="\\d+";

        Pattern p = Pattern.compile(digit_reg);
        Matcher m = p.matcher(praise);
        if(null != m) {
            if (m.find()) {
                return Integer.valueOf(m.group());
            }
        }
        return 0;
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
}
