package com.news.yazhidao.entity;

import com.j256.ormlite.table.DatabaseTable;

import java.io.Serializable;

/**
 * Created by fengjigang on 15/8/5.
 * 专辑中所包含的挖掘items
 */
@DatabaseTable(tableName = "tb_album_item")
public class AlbumSubItem implements Serializable {

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

    @Override
    public String toString() {
        return "<"+this.getClass().getSimpleName()+">:"+"status="+status+",search_url="+search_url+",search_key="+search_key+",inserteid="+inserteId;
    }
}
