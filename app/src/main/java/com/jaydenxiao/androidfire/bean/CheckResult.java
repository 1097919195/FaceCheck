package com.jaydenxiao.androidfire.bean;

/**
 * Created by xtt on 2017/8/29.
 */

public class CheckResult {
    private boolean sucessful;
    private long editTime;
    private String msg;

    public boolean isSucessful() {
        return sucessful;
    }

    public void setSucessful(boolean sucessful) {
        this.sucessful = sucessful;
    }

    public long getEditTime() {
        return editTime;
    }

    public void setEditTime(long editTime) {
        this.editTime = editTime;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    @Override
    public String toString() {
        return "CheckResult{" +
                "sucessful=" + sucessful +
                ", editTime='" + editTime + '\'' +
                '}';
    }
}
