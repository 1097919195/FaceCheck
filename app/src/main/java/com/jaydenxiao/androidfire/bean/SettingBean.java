package com.jaydenxiao.androidfire.bean;

import java.io.Serializable;

/**
 * Created by xtt on 2017/8/28.
 */

public class SettingBean implements Serializable{


    /**
     * canEdit : 1
     * description :
     * editTime : 1503563754000
     * id : 293
     * key : UP_TIME_HOUR
     * type : int
     * value : 8
     */

    private int canEdit;
    private String description;
    private long editTime;
    private int id;
    private String key;
    private String type;
    private String value;

    public int getCanEdit() {
        return canEdit;
    }

    public void setCanEdit(int canEdit) {
        this.canEdit = canEdit;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public long getEditTime() {
        return editTime;
    }

    public void setEditTime(long editTime) {
        this.editTime = editTime;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
