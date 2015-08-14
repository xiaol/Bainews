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

    public SelfOpinion relate_opinion;

    public boolean isdoc;

    public String docUrl;

    public String docTime;

    public String docUserIcon;

    public ArrayList<ZhiHu> zhihu;

    public ArrayList<Point> point;

    public ArrayList<ArrayList<String>> douban;  //get(0)  title  get(1) url

    public ArrayList<Weibo> weibo;
    public ArrayList<HashMap<String,String>> imgWall;
    //相关新闻
    public ArrayList<Relate> relate;

    public String rc;

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

    public class Point {
        public String userName;
        public String srcText;
        public String desText;
        public String paragraphIndex;
        public String type;
        public String up;
        public String down;
        public String comments_count;
        public String uuid;
        public String userIcon;
        public String sourceUrl;
        public String isPraiseFlag;
        public String commentId;
        //语音评论的时长
        public int srcTextTime;
    }


    public class Weibo {
//        public sourceSitename sourceSitename;
        public String url;
        public String profileImageUrl;
        public String user;
        public String title;
        public String img;
        public String isCommentFlag;
    }

    public class Article{
        public String url;
        public String title;
        public String self_opinion;
    }

    public class sourceSitename{
        public String reposts_count;
        public String updateTime;
        public String source_name;
        public String url;
        public ArrayList<String> img_urls;
        public String profile_image_url;
        public String content;
        public String like_count;
        public String comments_count;
        public String img_url;
    }
}
