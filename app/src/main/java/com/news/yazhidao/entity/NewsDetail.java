package com.news.yazhidao.entity;

import java.util.ArrayList;

/**
 * 新闻详情entity
 * Created by fengjigang on 15/1/21.
 */
public class NewsDetail {
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

    public ArrayList<BaiDuBaiKe> arrBaiDuBaiKe;

    public ArrayList<ZhiHu> arrZhihu;

    public ArrayList<DouBan> arrDouBan;

    public ArrayList<Weibo> arrWeibo;

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
        public String content;
    }

    public class ZhiHu {
        public String url;
        public String user;
        public String title;
    }

    public class DouBan {
        public String url;
        public String title;
    }

    public class Weibo {
        public String content;
        public String url;
        public String user;
        public String title;
    }
}
