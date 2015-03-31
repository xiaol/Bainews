package com.news.yazhidao.entity;

import java.util.ArrayList;

/**
 * 新闻详情entity
 * Created by fengjigang on 15/1/21.
 */
public class NewsDetail {
    public String originsourceSiteName;
    public String abs;
    public String content;
    public String imgUrl;

    public class Weibo {
        public String sourceName;
        public String url;
        public String user;
        public String title;
    }

    public ArrayList<Relate> relate;

    public class Relate {
        public String url;
        public String sourceSitename;
        public String img;
        public String title;
    }

    public ArrayList<ArrayList<String>> douban;
    public Zhihu zhihu;
    public Ne ne;

    public class Ne {
        public ArrayList<String> time;
        public ArrayList<String> gpe;
        public ArrayList<String> person;
        public ArrayList<String> loc;
        public ArrayList<String> org;
    }

    public class Zhihu {
        public String url;
        public String user;
        public String title;
    }
}
