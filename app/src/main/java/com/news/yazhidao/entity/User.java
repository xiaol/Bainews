package com.news.yazhidao.entity;

import com.news.yazhidao.utils.GsonUtil;

import java.io.Serializable;

/**
 * Created by fengjigang on 15/5/13.
 */
public class User implements Serializable {
    private String uuid;
    private String userId;
    //token的有效时常（单位：秒）
    private long expiresIn;
    //token的有效截止时间
    private String expiresTime;
    private String token;
    private String userGender;
    private String userIcon;
    private String userName;
    private String platformType;
    private long firstLoginTime;
    private long lastLoginTime;
    private String authorToken;//访问接口时的token
    @Override
    public String toString() {
        return "User{" +
                "uuid='" + uuid + '\'' +
                ", userId='" + userId + '\'' +
                ", expiresIn=" + expiresIn +
                ", expiresTime=" + expiresTime +
                ", token='" + token + '\'' +
                ", userGender='" + userGender + '\'' +
                ", userIcon='" + userIcon + '\'' +
                ", userName='" + userName + '\'' +
                ", platformType='" + platformType + '\'' +
                ", firstLoginTime=" + firstLoginTime +
                ", lastLoginTime=" + lastLoginTime +
                '}';
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getUserId() {
        return userId+"";
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public long getExpiresIn() {
        return expiresIn;
    }

    public void setExpiresIn(long expiresIn) {
        this.expiresIn = expiresIn;
    }

    public String getExpiresTime() {
        return expiresTime;
    }

    public void setExpiresTime(String expiresTime) {
        this.expiresTime = expiresTime;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getUserGender() {
        return userGender;
    }

    public void setUserGender(String userGender) {
        this.userGender = userGender;
    }

    public String getUserIcon() {
        return userIcon;
    }

    public void setUserIcon(String userIcon) {
        this.userIcon = userIcon;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPlatformType() {
        return platformType;
    }

    public void setPlatformType(String platformType) {
        this.platformType = platformType;
    }

    public long getFirstLoginTime() {
        return firstLoginTime;
    }

    public void setFirstLoginTime(long firstLoginTime) {
        this.firstLoginTime = firstLoginTime;
    }

    public long getLastLoginTime() {
        return lastLoginTime;
    }

    public void setLastLoginTime(long lastLoginTime) {
        this.lastLoginTime = lastLoginTime;
    }

    public String getAuthorToken() {
        return authorToken;
    }

    public void setAuthorToken(String authorToken) {
        this.authorToken = authorToken;
    }

    /**
     * 把json 反序列化为 User 对象
     * @param userStr
     * @return
     */
    public static User parseUser(String userStr) {
        return GsonUtil.deSerializedByType(userStr, User.class);
    }

    /**
     * 把User 对象序列化 json
     * @return
     */
    public String toJsonString(){
        return GsonUtil.serialized(this);
    }
}
