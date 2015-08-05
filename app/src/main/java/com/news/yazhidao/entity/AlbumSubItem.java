package com.news.yazhidao.entity;

/**
 * Created by fengjigang on 15/8/5.
 */
public class AlbumSubItem {

    /**
     * status : 0
     * search_url : None
     * inserteId : 55c1e1670d00006101f8569f
     * search_key : 啊啊啊啊啊
     */
    private String status;
    private String search_url;
    private String inserteId;
    private String search_key;

    public void setStatus(String status) {
        this.status = status;
    }

    public void setSearch_url(String search_url) {
        this.search_url = search_url;
    }

    public void setInserteId(String inserteId) {
        this.inserteId = inserteId;
    }

    public void setSearch_key(String search_key) {
        this.search_key = search_key;
    }

    public String getStatus() {
        return status;
    }

    public String getSearch_url() {
        return search_url;
    }

    public String getInserteId() {
        return inserteId;
    }

    public String getSearch_key() {
        return search_key;
    }
}
