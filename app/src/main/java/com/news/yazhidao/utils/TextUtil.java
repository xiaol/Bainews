package com.news.yazhidao.utils;

import android.text.TextUtils;

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
}
