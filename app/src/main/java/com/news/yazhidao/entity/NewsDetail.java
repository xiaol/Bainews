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
    /**差异化观点*/
    public NewsDetailSelfOpinion relate_opinion;
    /**是否有语音评论*/
    public boolean isdoc;
    /**语音评论的url*/
    public String docUrl;
    /**语音评论时长*/
    public String docTime;
    /**语音评论用户图像的url*/
    public String docUserIcon;

    public ArrayList<ZhiHu> zhihu;

    public ArrayList<Point> point;

    public ArrayList<ArrayList<String>> douban;  //get(0)  title  get(1) url

    public ArrayList<Weibo> weibo;
    /** e.g:"note": "7日上午8时许，广州市区下起暴雨。在海珠区新滘东路绿道，一年约45岁的女子在绿道上撑伞行走时遭雷电击中。主治医生称，伤者暂无生命危险，但还需留院观察。",
     "img": "http://img3.cache.netease.com/photo/0001/2015-09-08/B2VV22KN00AP0001.jpg"*/
    public ArrayList<HashMap<String,String>> imgWall;
    //相关新闻
    public ArrayList<Relate> relate;

    public String rc;

    public class Relate {
        public String url;
        public String sourceSitename;
        public String img;
        public String title;
        public String updateTime;
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

    public static class Point implements Comparable {
        public String userName;
        /**用户评论内容*/
        public String srcText;
        public String desText;
        public String paragraphIndex;
        /**
         * @see com.news.yazhidao.net.request.UploadCommentRequest#TEXT_DOC
         * @see com.news.yazhidao.net.request.UploadCommentRequest#TEXT_PARAGRAPH
         * @see com.news.yazhidao.net.request.UploadCommentRequest#SPEECH_DOC
         * @see com.news.yazhidao.net.request.UploadCommentRequest#SPEECH_PARAGRAPH
         * */
        public String type;//
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

        @Override
        public int compareTo(Object another) {
            if (this.up == null){
                this.up = "0";
            }
            if (((Point)another).up == null){
                ((Point)another).up = "";
            }
            int first = Integer.valueOf(this.up);
            int second = Integer.valueOf(((Point)another).up);
            if (first < second){
                return 1;
            }
            if (first == second){
                return 0;
            }
            return -1;
        }
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
