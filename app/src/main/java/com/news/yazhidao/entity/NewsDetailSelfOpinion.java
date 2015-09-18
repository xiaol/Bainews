package com.news.yazhidao.entity;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by Berkeley on 8/12/15.
 */
public class NewsDetailSelfOpinion implements Serializable {

    private ArrayList<NewsDetail.Article> self_opinion;

    public ArrayList<NewsDetail.Article> getSelf_opinion() {
        return self_opinion;
    }

    public void setSelf_opinion(ArrayList<NewsDetail.Article> self_opinion) {
        this.self_opinion = self_opinion;
    }
}
