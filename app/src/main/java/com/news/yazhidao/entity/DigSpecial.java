package com.news.yazhidao.entity;

/**
 * Created by fengjigang on 15/7/21.
 * 挖掘机功能中的个人专辑实体
 */
public class DigSpecial {
    //专辑标题
    private String title;
    //专辑所包含的个数
    private String count;
    //专辑对应的描述
    private String desc;
    public DigSpecial(){}

    public DigSpecial(String title, String count, String desc) {
        this.title = title;
        this.count = count;
        this.desc = desc;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getCount() {
        return count;
    }

    public void setCount(String count) {
        this.count = count;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    @Override
    public String toString() {
        return "<"+this.getClass().getSimpleName()+"> "+"title="+title+",count="+count+",desc="+desc;
    }
}
