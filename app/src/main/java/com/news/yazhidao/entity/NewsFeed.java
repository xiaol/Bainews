package com.news.yazhidao.entity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;

/**
 * Created by Ariesymark on 2015/3/25.
 */
public class NewsFeed implements Serializable {

    private String sourceSiteName;
    private String updateTime;//更新时间
    private String sourceUrl;//来源地址
    private String description;
    private String title;
    private Collection<Source> relatePointsList;
    private ArrayList<String> imgUrls;
    private String commentNum;
    private String newsId;
    private String channelId;
    private String type;
    private String collection;

    public String getSourceSiteName() {
        return sourceSiteName;
    }

    public void setSourceSiteName(String sourceSiteName) {
        this.sourceSiteName = sourceSiteName;
    }

    public String getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(String updateTime) {
        this.updateTime = updateTime;
    }

    public String getSourceUrl() {
        return sourceUrl;
    }

    public void setSourceUrl(String sourceUrl) {
        this.sourceUrl = sourceUrl;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Collection<Source> getRelatePointsList() {
        return relatePointsList;
    }

    public void setRelatePointsList(Collection<Source> relatePointsList) {
        this.relatePointsList = relatePointsList;
    }

    public ArrayList<String> getImgUrls() {
        return imgUrls;
    }

    public void setImgUrls(ArrayList<String> imgUrls) {
        this.imgUrls = imgUrls;
    }

    public String getCommentNum() {
        return commentNum;
    }

    public void setCommentNum(String commentNum) {
        this.commentNum = commentNum;
    }

    public String getNewsId() {
        return newsId;
    }

    public void setNewsId(String newsId) {
        this.newsId = newsId;
    }

    public String getChannelId() {
        return channelId;
    }

    public void setChannelId(String channelId) {
        this.channelId = channelId;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getCollection() {
        return collection;
    }

    public void setCollection(String collection) {
        this.collection = collection;
    }

    public static class Source implements Serializable {

        private String url;
        private String sourceSiteName;
        private String user;
        private String title;
        private String similarity;


        public String getSimilarity() {
            return similarity;
        }

        public void setSimilarity(String similarity) {
            this.similarity = similarity;
        }

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }

        public String getUser() {
            return user;
        }

        public void setUser(String user) {
            this.user = user;
        }

        public String getSourceSiteName() {
            return sourceSiteName;
        }

        public void setSourceSiteName(String sourceSiteName) {
            this.sourceSiteName = sourceSiteName;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        @Override
        public String toString() {
            return url + "," + sourceSiteName + "," + title;
        }
    }

}
