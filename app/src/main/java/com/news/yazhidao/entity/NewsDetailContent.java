package com.news.yazhidao.entity;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by fengjigang on 15/9/7.
 * 新闻详情页中每一段新闻所对应的POJO
 */
public class NewsDetailContent implements Serializable {
    private String content;
    private ArrayList<NewsDetail.Point> comments;
    public NewsDetailContent(){}

    public NewsDetailContent(String content, ArrayList<NewsDetail.Point> comments) {
        this.content = content;
        this.comments = comments;
    }


    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public ArrayList<NewsDetail.Point> getComments() {
        return comments;
    }

    public void setComments(ArrayList<NewsDetail.Point> comments) {
        this.comments = comments;
    }

}
