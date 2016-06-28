package com.news.yazhidao.entity;

import android.graphics.drawable.Drawable;

import java.io.Serializable;

/**
 * Created by Administrator on 2016/6/27.
 */
public class AppsItemInfo implements Serializable{
//    private Drawable icon; // 存放图片
    private String label; // 存放应用程序名
    private String packageName; // 存放应用程序包名

    @Override
    public String toString() {
        return "AppsItemInfo{" +
//                "icon=" + icon +
                ", label='" + label + '\'' +
                ", packageName='" + packageName + '\'' +
                '}';
    }

//    public Drawable getIcon() {
//        return icon;
//    }
//
//    public void setIcon(Drawable icon) {
//        this.icon = icon;
//    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof AppsItemInfo)) return false;

        AppsItemInfo that = (AppsItemInfo) o;

        if (!label.equals(that.label)) return false;
        return packageName.equals(that.packageName);

    }

    @Override
    public int hashCode() {
        int result = label.hashCode();
        result = 31 * result + packageName.hashCode();
        return result;
    }
}
