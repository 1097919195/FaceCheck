package com.jaydenxiao.androidfire.bean;

import java.util.List;

/**
 * Created by xtt on 2017/8/28.
 */

public class CheckWorkBean {
    /**
     * dailyStatus : [2,1,1,1,0,1,0,0,3,2,0,2,1,0,0,1,0,3,1,2,0,0,2,3,0,3,1]
     * leavingEarly : 5
     * lost : 4
     * normal : 10
     * tardiness : 8
     */
    private int leavingEarly;//早退
    private int lost;//缺失
    private int normal;//正常
    private int tardiness;//迟到
    private List<Integer> dailyStatus;//本月的打卡记录

    public int getLeavingEarly() {
        return leavingEarly;
    }

    public void setLeavingEarly(int leavingEarly) {
        this.leavingEarly = leavingEarly;
    }

    public int getLost() {
        return lost;
    }

    public void setLost(int lost) {
        this.lost = lost;
    }

    public int getNormal() {
        return normal;
    }

    public void setNormal(int normal) {
        this.normal = normal;
    }

    public int getTardiness() {
        return tardiness;
    }

    public void setTardiness(int tardiness) {
        this.tardiness = tardiness;
    }

    public List<Integer> getDailyStatus() {
        return dailyStatus;
    }

    public void setDailyStatus(List<Integer> dailyStatus) {
        this.dailyStatus = dailyStatus;
    }
}
