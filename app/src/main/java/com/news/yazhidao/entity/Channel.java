package com.news.yazhidao.entity;

import java.io.Serializable;

/**
 * Created by Berkeley on 6/18/15.
 */
public class Channel implements Serializable {

    private String channel_id;
    private String channel_name;
    private String channel_des;
    private String channel_android_img;

    public String getChannel_id() {
        return channel_id;
    }

    public void setChannel_id(String channel_id) {
        this.channel_id = channel_id;
    }

    public String getChannel_name() {
        return channel_name;
    }

    public void setChannel_name(String channel_name) {
        this.channel_name = channel_name;
    }

    public String getChannel_des() {
        return channel_des;
    }

    public void setChannel_des(String channel_des) {
        this.channel_des = channel_des;
    }

    public String getChannel_android_img() {
        return channel_android_img;
    }

    public void setChannel_android_img(String channel_android_img) {
        this.channel_android_img = channel_android_img;
    }
}
