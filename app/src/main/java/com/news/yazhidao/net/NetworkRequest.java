package com.news.yazhidao.net;

import org.apache.http.HttpEntity;

import java.util.HashMap;

/**
 * Created by fengjigang on 15/1/5.
 */
public class NetworkRequest {

    public IUpdateProgressListener updateProgressListener;
    public HashMap<String, Object> getParams;

    public NetworkRequest(String url,RequestMethod method){
    this.url=url;
    this.method=method;
}
public NetworkRequest(String url){
    this.url=url;
    this.method= RequestMethod.GET;
}
    public enum RequestMethod{
        GET,POST,PUT,DELETE
    }
    public RequestMethod method;

    public String url;
    public HttpEntity params;
    public HashMap<String, String> headers;
    public AbstractCallBack callback;
    private NetworkRequestTask nrTask;

    public void setCallback(AbstractCallBack callback){
        this.callback=callback;
    }
    public void setUpdateProgressListener(IUpdateProgressListener listener){
        this.updateProgressListener=listener;
    }
    public void execute(){
        nrTask = new NetworkRequestTask(this);
        nrTask.execute();
    }
    public void addHeader(String key,String value){
        if(headers==null){
            headers=new HashMap<String, String>();
            headers.put(key, value);
        }
    }
    public void cancel(boolean force){
        if(force&&nrTask!=null){
            nrTask.cancel(true);
        }
        if(this.callback!=null){
            this.callback.cancel(force);
        }
    }
}
