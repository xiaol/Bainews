package com.news.yazhidao.entity;

import com.google.gson.internal.LinkedTreeMap;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * 新闻详情entity
 * Created by fengjigang on 15/1/21.
 */
public class NewsDetailAdd implements Serializable{
    //新闻来源
    public String originsourceSiteName;
    //新闻描述
    public String abs;
    //新闻内容
    public ArrayList<LinkedTreeMap<String,HashMap<String,String>>> content;
    //图片url
    public String imgUrl;
    //标题
    public String title;
    //新闻时间
    public String updateTime;
    //温度
    public String root_class;

    public ArrayList<NewsDetail.BaiDuBaiKe> baike;

    public boolean isdoc;

    public String docUrl;

    public String docTime;

    public String docUserIcon;

    public ArrayList<NewsDetail.ZhiHu> zhihu;

    public ArrayList<NewsDetail.Point> point;

    public ArrayList<ArrayList<String>> douban;  //get(0)  title  get(1) url

    public ArrayList<NewsDetail.Weibo> weibo;
    public ArrayList<HashMap<String,String>> imgWall;
    //相关新闻
    public ArrayList<NewsDetail.Relate> relate;

    public String rc;

    //name entity
    public NewsDetail.Ne ne;

}
