package com.news.yazhidao.entity;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by fengjigang on 16/3/30.
 * 新闻详情页
 */
public class TopicClass implements Serializable {

    private TopicClassBaseInfo topicClassBaseInfo;
    private ArrayList<NewsFeed> newsFeedArrayList;

    public TopicClassBaseInfo getTopicClassBaseInfo() {
        return topicClassBaseInfo;
    }

    public void setTopicClassBaseInfo(TopicClassBaseInfo topicClassBaseInfo) {
        this.topicClassBaseInfo = topicClassBaseInfo;
    }

    public ArrayList<NewsFeed> getNewsFeedArrayList() {
        return newsFeedArrayList;
    }

    public void setNewsFeedArrayList(ArrayList<NewsFeed> newsFeedArrayList) {
        this.newsFeedArrayList = newsFeedArrayList;
    }

    class TopicClassBaseInfo implements Serializable {
        private int id;
        private String name;
        private int topic;
        private int puorderrl;

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public int getTopic() {
            return topic;
        }

        public void setTopic(int topic) {
            this.topic = topic;
        }

        public int getPuorderrl() {
            return puorderrl;
        }

        public void setPuorderrl(int puorderrl) {
            this.puorderrl = puorderrl;
        }
    }
}
