package com.news.yazhidao.entity;

/**
 * 专辑展示中的实体
 * Created by fengjigang on 15/7/23.
 */
public class DigSpecialItem {
    private String title;
    private String url;
    private int progress;

    public DigSpecialItem(String title, String url, int progress) {
        this.title = title;
        this.url = url;
        this.progress = progress;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public int getProgress() {
        return progress;
    }

    public void setProgress(int progress) {
        this.progress = progress;
    }
}
