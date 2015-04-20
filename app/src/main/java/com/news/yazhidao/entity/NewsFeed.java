package com.news.yazhidao.entity;

import java.io.Serializable;
import java.util.Collection;

/**
 * Created by Ariesymark on 2015/3/25.
 */
public class NewsFeed implements Serializable{

    private String originsourceSiteName; //来源网站
    private String updateTime;//更新时间
    private String sourceUrl;//来源地址
    private String description;
    private String title;
    private Collection<String> urls_response;
    private Collection<Source> sublist;
    private String imgUrl;
    private String otherNum;
    private String _id;
    private String sourceSiteName;
    private String channel;
    private String root_class;
    private String category;
    private String special;
    private boolean flag;

    public boolean isFlag() {
        return flag;
    }

    public void setFlag(boolean flag) {
        this.flag = flag;
    }

    public String getSpecial() {
        return special;
    }

    public void setSpecial(String special) {
        this.special = special;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public Collection<Source> getSublist() {
        return sublist;
    }

    public void setSublist(Collection<Source> sublist) {
        this.sublist = sublist;
    }

    public String getOtherNum() {
        return otherNum;
    }

    public void setOtherNum(String otherNum) {
        this.otherNum = otherNum;
    }

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    public String getSourceSiteName() {
        return sourceSiteName;
    }

    public void setSourceSiteName(String sourceSiteName) {
        this.sourceSiteName = sourceSiteName;
    }

    public String getChannel() {
        return channel;
    }

    public void setChannel(String channel) {
        this.channel = channel;
    }

    public String getRoot_class() {
        return root_class;
    }

    public void setRoot_class(String root_class) {
        this.root_class = root_class;
    }

    public Collection<String> getUrls_response() {
        return urls_response;
    }

    public void setUrls_response(Collection<String> urls_response) {
        this.urls_response = urls_response;
    }

    public String getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(String updateTime) {
        this.updateTime = updateTime;
    }

    public String getOriginsourceSiteName() {
        return originsourceSiteName;
    }

    public void setOriginsourceSiteName(String originsourceSiteName) {
        this.originsourceSiteName = originsourceSiteName;
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

    public String getImgUrl() {
        return imgUrl;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }

    public static class Source implements Serializable {

        private String url;
        private String sourceSitename;
        private String user;
        private String title;

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

        public String getSourceSitename() {
            return sourceSitename;
        }

        public void setSourceSitename(String sourceSitename) {
            this.sourceSitename = sourceSitename;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        @Override
        public String toString() {
            return url + "," + sourceSitename + "," + title;
        }
    }

}
