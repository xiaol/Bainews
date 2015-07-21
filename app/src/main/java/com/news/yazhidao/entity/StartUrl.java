package com.news.yazhidao.entity;

import java.io.Serializable;

/**
 * Created by Berkeley on 7/8/15.
 */
public class StartUrl implements Serializable{

    private String title;
    private String imgUrl;
    private String updateTime;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }

    public String getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(String updateTime) {
        this.updateTime = updateTime;
    }
}
