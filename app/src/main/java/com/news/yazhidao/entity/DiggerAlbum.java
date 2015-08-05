package com.news.yazhidao.entity;

import java.io.Serializable;

/**
 * 挖掘机界面中得专辑entity
 * Created by fengjigang on 15/7/31.
 */
public class DiggerAlbum implements Serializable {

    /**
     * album_id : 55bae2e42c0e2c01fc2866d2
     * create_time : 2015-07-31 10:52:20
     * album_des :
     * user_id : 3
     * album_title : 默认
     * album_news_count : 0
     * album_img : 2130837689
     */
    //专辑id
    private String album_id;
    //专辑创建的时间
    private String create_time;
    //专辑描述
    private String album_des;
    //用户id
    private String user_id;
    //专辑标题
    private String album_title;
    //专辑中包含挖掘内容的个数
    private String album_news_count;
    //专辑的背景图片
    private String album_img;

    public DiggerAlbum(String album_id, String create_time, String album_des, String user_id, String album_title, String album_news_count, String album_img) {
        this.album_id = album_id;
        this.create_time = create_time;
        this.album_des = album_des;
        this.user_id = user_id;
        this.album_title = album_title;
        this.album_news_count = album_news_count;
        this.album_img = album_img;
    }

    public void setAlbum_id(String album_id) {
        this.album_id = album_id;
    }

    public void setCreate_time(String create_time) {
        this.create_time = create_time;
    }

    public void setAlbum_des(String album_des) {
        this.album_des = album_des;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public void setAlbum_title(String album_title) {
        this.album_title = album_title;
    }

    public void setAlbum_news_count(String album_news_count) {
        this.album_news_count = album_news_count;
    }

    public void setAlbum_img(String album_img) {
        this.album_img = album_img;
    }

    public String getAlbum_id() {
        return album_id;
    }

    public String getCreate_time() {
        return create_time;
    }

    public String getAlbum_des() {
        return album_des;
    }

    public String getUser_id() {
        return user_id;
    }

    public String getAlbum_title() {
        return album_title;
    }

    public String getAlbum_news_count() {
        return album_news_count;
    }

    public String getAlbum_img() {
        return album_img;
    }
}
