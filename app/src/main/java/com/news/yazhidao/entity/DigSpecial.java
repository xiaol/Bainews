package com.news.yazhidao.entity;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by fengjigang on 15/7/21.
 * 挖掘机功能中的个人专辑实体
 */
public class DigSpecial implements Serializable {
    //专辑标题
    private String title;
    //专辑对应的描述
    private String desc;
    //专辑对应的背景
    private int bgDrawable;
    //专辑包含的item
    private ArrayList<DigSpecialItem> mSpecialItems;

    public DigSpecial(){}


    public DigSpecial(String title, String desc, int bgDrawable,ArrayList<DigSpecialItem> specialItems) {
        this.title = title;
        this.desc = desc;
        this.bgDrawable = bgDrawable;
        this.mSpecialItems = specialItems;
    }

    public int getBgDrawable() {
        return bgDrawable;
    }

    public void setBgDrawable(int bgDrawable) {
        this.bgDrawable = bgDrawable;
    }
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }
    public ArrayList<DigSpecialItem> getSpecialItems() {
        return mSpecialItems;
    }

    public void setSpecialItems(ArrayList<DigSpecialItem> specialItems) {
        this.mSpecialItems = specialItems;
    }
    @Override
    public String toString() {
        return "<"+this.getClass().getSimpleName()+"> "+"title="+title+",desc="+desc+",specialItems ="+mSpecialItems;
    }
}
