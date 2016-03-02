package com.news.yazhidao.entity;

import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by Ariesymark on 2015/3/25.
 */

@DatabaseTable(tableName = "tb_news_feed")
public class NewsFeed implements Serializable {

    public static final String COLUMN_CHANNEL_ID = "channelId";
    public static final String COLUMN_NEWS_ID = "newsId";
    public static final String COLUMN_UPDATE_TIME = "updateTime";
    @DatabaseField(generatedId = true)
    private int id;
    @DatabaseField
    private String sourceSiteName;
    @DatabaseField
    private String updateTime;//更新时间
    @DatabaseField
    private String sourceUrl;//来源地址
    @DatabaseField
    private String description;
    @DatabaseField
    private String title;
    @DatabaseField(dataType = DataType.SERIALIZABLE)
    private ArrayList<Source> relatePointsList;
    @DatabaseField(dataType = DataType.SERIALIZABLE)
    private ArrayList<String> imgUrls;
    @DatabaseField
    private String commentNum;
    @DatabaseField
    private String newsId;
    @DatabaseField
    private String channelId;
    @DatabaseField
    private String type;
    @DatabaseField
    private String collection;
    @DatabaseField(dataType = DataType.BOOLEAN)
    private boolean isRead;//用户是否看过

    public boolean isRead() {
        return isRead;
    }

    public void setRead(boolean read) {
        isRead = read;
    }

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

    public ArrayList<Source> getRelatePointsList() {
        return relatePointsList;
    }

    public void setRelatePointsList(ArrayList<Source> relatePointsList) {
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
        private String sourceUrl;
        private String sourceSiteName;
        private String compress;
        private String similarity;
        private String user;

        public String getUser() {
            return user;
        }

        public void setUser(String user) {
            this.user = user;
        }

        public String getSimilarity() {
            return similarity;
        }

        public void setSimilarity(String similarity) {
            this.similarity = similarity;
        }

        public String getSourceSiteName() {
            return sourceSiteName;
        }

        public void setSourceSiteName(String sourceSiteName) {
            this.sourceSiteName = sourceSiteName;
        }

        public String getSourceUrl() {
            return sourceUrl;
        }

        public void setSourceUrl(String sourceUrl) {
            this.sourceUrl = sourceUrl;
        }

        public String getCompress() {
            return compress;
        }

        public void setCompress(String compress) {
            this.compress = compress;
        }
    }

}
