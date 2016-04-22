package com.news.yazhidao.entity;

import java.io.Serializable;

/**
 * Created by fengjigang on 16/3/31.
 * 新闻评论
 */
public class NewsDetailComment implements Serializable{

    /**
     * docid : /group/6267906567157874945/
     * comment_id : 6222592828
     * id : 4666198
     * profile : http://p1.pstatp.com/thumb/398/2537495648
     * love : 212
     * content : 凭啥老百姓吃草根？他天天大鱼大肉！这种人不用再为他卖命，真的不值。
     * create_time : 2016-03-31 12:39:00
     * nickname : 你猜23803670
     */

    private String docid;
    private String comment_id;
    private String id;
    private String profile;
    private int love;
    private String content;
    private String create_time;
    private String nickname;
    private boolean isPraise;
    private String uuid;
    public NewsDetailComment(){}

    public NewsDetailComment(String comment_id, String content, String create_time, String docid, String id, int love, String nickname, String profile,String uuid) {
        this.comment_id = comment_id;
        this.content = content;
        this.create_time = create_time;
        this.docid = docid;
        this.id = id;
        this.love = love;
        this.nickname = nickname;
        this.profile = profile;
        this.uuid = uuid;
    }

    @Override
    public String toString() {
        return "NewsDetailComment{" +
                "docid='" + docid + '\'' +
                ", comment_id='" + comment_id + '\'' +
                ", id='" + id + '\'' +
                ", profile='" + profile + '\'' +
                ", love=" + love +
                ", content='" + content + '\'' +
                ", create_time='" + create_time + '\'' +
                ", nickname='" + nickname + '\'' +
                ", isPraise=" + isPraise +
                ", uuid='" + uuid + '\'' +
                '}';
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public boolean isPraise() {
        return isPraise;
    }

    public void setPraise(boolean praise) {
        isPraise = praise;
    }
    public String getDocid() {
        return docid;
    }

    public void setDocid(String docid) {
        this.docid = docid;
    }

    public String getComment_id() {
        return comment_id;
    }

    public void setComment_id(String comment_id) {
        this.comment_id = comment_id;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getProfile() {
        return profile;
    }

    public void setProfile(String profile) {
        this.profile = profile;
    }

    public int getLove() {
        return love;
    }

    public void setLove(int love) {
        this.love = love;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getCreate_time() {
        return create_time;
    }

    public void setCreate_time(String create_time) {
        this.create_time = create_time;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }
}
