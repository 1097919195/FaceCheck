package com.jaydenxiao.androidfire.utils.calendarutil;

import java.util.Arrays;

public class DateBean {
    private int[] solar;//阳历年、月、日
    private String[] lunar;//农历月、日
    private String solarHoliday;//阳历节假日
    private String lunarHoliday;//阳历节假日
    private int type;//0:上月，1:当月，2:下月
    private String term;//节气
    //0缺失，1正常，2迟到，3早退，4迟到且早退，5只打卡一次,100 还未到上班日期白色 101默认灰掉
    private int status=101;//默认灰掉

    public int[] getSolar() {
        return solar;
    }

    public void setSolar(int year, int month, int day) {
        this.solar = new int[]{year, month, day};
    }

    public String[] getLunar() {
        return lunar;
    }

    public void setLunar(String[] lunar) {
        this.lunar = lunar;
    }

    public String getSolarHoliday() {
        return solarHoliday;
    }

    public void setSolarHoliday(String solarHoliday) {
        this.solarHoliday = solarHoliday;
    }

    public String getLunarHoliday() {
        return lunarHoliday;
    }

    public void setLunarHoliday(String lunarHoliday) {
        this.lunarHoliday = lunarHoliday;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getTerm() {
        return term;
    }

    public void setTerm(String term) {
        this.term = term;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "DateBean{" +
                "solar=" + Arrays.toString(solar) +
                ", lunar=" + Arrays.toString(lunar) +
                ", solarHoliday='" + solarHoliday + '\'' +
                ", lunarHoliday='" + lunarHoliday + '\'' +
                ", type=" + type +
                ", term='" + term + '\'' +
                '}';
    }
}
