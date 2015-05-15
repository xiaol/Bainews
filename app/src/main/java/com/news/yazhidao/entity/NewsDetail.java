package com.news.yazhidao.entity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * 新闻详情entity
 * Created by fengjigang on 15/1/21.
 */
public class NewsDetail implements Serializable{
    //新闻来源
    public String originsourceSiteName;
    //新闻描述
    public String abs;
    //新闻内容
    public String content;
    //图片url
    public String imgUrl;
    //标题
    public String title;
    //新闻时间
    public String updateTime;
    //温度
    public String root_class;

    public ArrayList<BaiDuBaiKe> baike;

    public ArrayList<ZhiHu> zhihu;

    public ArrayList<ArrayList<String>> douban;  //get(0)  title  get(1) url

    public ArrayList<Weibo> weibo;
    public ArrayList<HashMap<String,String>> imgWall;
    //相关新闻
    public ArrayList<Relate> relate;

    public class Relate {
        public String url;
        public String sourceSitename;
        public String img;
        public String title;
    }

    //name entity
    public Ne ne;

    public class Ne {
        public ArrayList<String> time;
        public ArrayList<String> gpe;
        public ArrayList<String> person;
        public ArrayList<String> loc;
        public ArrayList<String> org;
    }

    public class BaiDuBaiKe {
        public String imgUrl;
        public String title;
        public String url;
        public String abs;
    }

    public class ZhiHu {
        public String url;
        public String user;
        public String title;
    }


    public class Weibo {
        public String sourceSitename;
        public String url;
        public String profileImageUrl;
        public String user;
        public String title;
        public String img;
    }
}
