package com.jaydenxiao.androidfire.bean;

import org.xutils.db.annotation.Column;
import org.xutils.db.annotation.Table;

/**
 * Created by xtt on 2017/9/20.
 */

@Table(name="detectoffline")
public class DetectOffline {

    @Column(name="id",isId = true)
    private int id;

    @Column(name = "usernumber")
    private String usernumber;

    @Column(name="client")
    private String client;

    @Column(name="similarity")
    private String similarity;

    @Column(name="checktime")
    private long checktime;

    @Column(name="isupload")
    private int isupload;//0未上传，1上传过

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUsernumber() {
        return usernumber;
    }

    public void setUsernumber(String usernumber) {
        this.usernumber = usernumber;
    }

    public String getClient() {
        return client;
    }

    public void setClient(String client) {
        this.client = client;
    }

    public String getSimilarity() {
        return similarity;
    }

    public void setSimilarity(String similarity) {
        this.similarity = similarity;
    }

    public long getChecktime() {
        return checktime;
    }

    public void setChecktime(long checktime) {
        this.checktime = checktime;
    }

    public int getIsupload() {
        return isupload;
    }

    public void setIsupload(int isupload) {
        this.isupload = isupload;
    }

    @Override
    public String toString() {
        return "DetectOffline{" +
                "id=" + id +
                ", usernumber='" + usernumber + '\'' +
                ", client='" + client + '\'' +
                ", similarity='" + similarity + '\'' +
                ", checktime=" + checktime +
                ", isupload=" + isupload +
                '}';
    }
}
