package com.news.yazhidao.entity;

import com.j256.ormlite.field.DatabaseField;

import java.io.Serializable;

/**
 * Created by xiao on 2016/5/6.
 */
public class NewsDetailCommentItem implements Serializable {
    private static final long serialVersionUID = 1572058492024089176L;
    /**
     * docid : /group/6267906567157874945/ comment_id : 6222592828 id : 4666198
     * profile : http://p1.pstatp.com/thumb/398/2537495648 love : 212 content :
     * 凭啥老百姓吃草根？他天天大鱼大肉！这种人不用再为他卖命，真的不值。 create_time : 2016-03-31 12:39:00
     * nickname : 你猜23803670
     */

    @DatabaseField(columnName="docid")
    private String docid;
    @DatabaseField(columnName="comment_id")
    private String comment_id;
    @DatabaseField(generatedId = true)
    private int id;
    @DatabaseField(columnName="love")
    private int love;
    @DatabaseField(columnName="content")
    private String content;
    @DatabaseField(columnName="create_time")
    private long create_time;
    @DatabaseField(columnName="isPraise")
    private boolean isPraise;
    @DatabaseField(columnName = "original")
    private String original;

    private User user;
    @DatabaseField(canBeNull = true, foreign = true, columnName = "url", foreignAutoRefresh = true)
    private NewsFeed newsFeed;

    public NewsFeed getNewsFeed() {
        return newsFeed;
    }

    public void setNewsFeed(NewsFeed newsFeed) {
        this.newsFeed = newsFeed;
    }

    public NewsDetailCommentItem() {
    }

    public NewsDetailCommentItem(String comment_id, String content,
                                 long create_time, String docid, int love,
                                 boolean isPraise,String original,
                                 User user) {
        this.comment_id = comment_id;
        this.content = content;
        this.create_time = create_time;
        this.docid = docid;
        this.love = love;
        this.isPraise = isPraise;
        this.original= original;
        this.user = user;
    }

    @Override
    public String toString() {
        return "NewsDetailComment{" + "docid='" + docid + '\''
                + ", comment_id='" + comment_id + '\'' + ", id='" + id + '\''
                +", love=" + love
                + ", content='" + content + '\'' + ", create_time='"
                + create_time + '\'' +  '\''
                + ", isPraise=" + isPraise + '}';
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

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
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

    public long getCreate_time() {
        return create_time;
    }

    public void setCreate_time(long create_time) {
        this.create_time = create_time;
    }

    public String getOriginal() {
        return original;
    }

    public void setOriginal(String original) {
        this.original = original;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
