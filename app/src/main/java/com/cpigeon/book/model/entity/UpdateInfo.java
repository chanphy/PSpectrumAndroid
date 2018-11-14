package com.cpigeon.book.model.entity;

import com.base.util.http.GsonUtil;

import java.io.Serializable;

/**
 * Created by chenshuai on 2017/4/18.
 * 应用更新信息类
 */

public class UpdateInfo implements Serializable {

    /**
     * appName : 中鸽网
     * packageName : com.cpigeon.app
     * url : http://192.168.0.52:8888/Apk/201703100-release.apk
     * verName : 1.1.1
     * verCode : 201703110
     * updateTime : 2017/3/10
     * updateExplain : 1.新增用户个人信息中心，可进行个人信息的设置
     * 2.新增用户签到
     * 3.新增余额充值入口，余额充值更方便
     * 4.新增余额充值记录，充值记录更直观
     * 5.优化界面,修复BUG
     * force : true
     */

    private String appName;//应用名称
    private String packageName;//应用包名
    private String url;//下载url (绝对路径)
    private String verName;//版本名称
    private int verCode;//版本号
    private String updateTime;//更新时间
    private String updateExplain;//更新说明
    private boolean force;//是否强制下载，不下载则退出程序

    private UpdateInfo(Builder builder) {
        setAppName(builder.appName);
        setPackageName(builder.packageName);
        setUrl(builder.url);
        setVerName(builder.verName);
        setVerCode(builder.verCode);
        setUpdateTime(builder.updateTime);
        setUpdateExplain(builder.updateExplain);
        setForce(builder.force);
    }

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getVerName() {
        return verName;
    }

    public void setVerName(String verName) {
        this.verName = verName;
    }

    public int getVerCode() {
        return verCode;
    }

    public void setVerCode(int verCode) {
        this.verCode = verCode;
    }

    public String getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(String updateTime) {
        this.updateTime = updateTime;
    }

    public String getUpdateExplain() {
        return updateExplain;
    }

    public void setUpdateExplain(String updateExplain) {
        this.updateExplain = updateExplain;
    }

    public boolean isForce() {
        return force;
    }

    public void setForce(boolean force) {
        this.force = force;
    }

    public String toJsonString() {
        return GsonUtil.toJson(this);
    }


    public static final class Builder {
        private String appName;
        private String packageName;
        private String url;
        private String verName;
        private int verCode;
        private String updateTime;
        private String updateExplain;
        private boolean force;

        public Builder() {
        }

        public Builder appName(String val) {
            appName = val;
            return this;
        }

        public Builder packageName(String val) {
            packageName = val;
            return this;
        }

        public Builder url(String val) {
            url = val;
            return this;
        }

        public Builder verName(String val) {
            verName = val;
            return this;
        }

        public Builder verCode(int val) {
            verCode = val;
            return this;
        }

        public Builder updateTime(String val) {
            updateTime = val;
            return this;
        }

        public Builder updateExplain(String val) {
            updateExplain = val;
            return this;
        }

        public Builder force(boolean val) {
            force = val;
            return this;
        }

        public UpdateInfo build() {
            return new UpdateInfo(this);
        }
    }
}