package com.jaydenxiao.androidfire.bean;

import android.support.annotation.NonNull;

import com.jaydenxiao.androidfire.ui.face.ZzxFaceApplication;

import org.xutils.db.annotation.Column;
import org.xutils.db.annotation.Table;
import org.xutils.db.sqlite.WhereBuilder;
import org.xutils.ex.DbException;

import java.io.Serializable;

import static com.jaydenxiao.androidfire.ui.face.ZzxFaceApplication.commonApplication;

/**
 * Created by xtt on 2017/9/22.
 */

@Table(name = "faceuser")
public class FaceUser implements Serializable,Comparable<FaceUser>{//(Comparable此接口强行对实现它的每个类的对象进行整体排序。此排序被称为该类的自然排序 ，类的 compareTo 方法被称为它的自然比较方法 。)

    @Column(name="number",isId = true)
    private String number;

    @Column(name="departmentname")
    private String departMentName;

    @Column(name="userrealname")
    private String userRealName;

    @Column(name="titie")
    private String title;

    @Column(name="position")
    private String position;

    private double score;

    private boolean isCheck=false;

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getDepartMentName() {
        return departMentName;
    }

    public void setDepartMentName(String departMentName) {
        this.departMentName = departMentName;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getUserRealName() {
        return userRealName;
    }

    public void setUserRealName(String userRealName) {
        this.userRealName = userRealName;
    }

    public double getScore() {
        return score;
    }

    public void setScore(double score) {
        this.score = score;
    }

    public boolean isCheck() {
        return isCheck;
    }

    public void setCheck(boolean check) {
        isCheck = check;
    }

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    @Override
    public String toString() {
        return "FaceUser{" +
                "number='" + number + '\'' +
                ", departMentName='" + departMentName + '\'' +
                ", userRealName='" + userRealName + '\'' +
                ", title='" + title + '\'' +
                ", position='" + position + '\'' +
                ", score=" + score +
                ", isCheck=" + isCheck +
                '}';
    }

    //添加用户
    public static void addFaceUser(FaceUser faceUser){
        try {
            commonApplication.db.saveOrUpdate(faceUser);
        } catch (DbException e) {
            e.printStackTrace();
        }
    }

    //删除用户
    public static void deleteFaceUser(String number){
        try {
            commonApplication.db.delete(FaceUser.class, WhereBuilder.b("number","=",number));
        } catch (DbException e) {
            e.printStackTrace();
        }
    }

    //查询用户
    public static FaceUser getFaceUser(String number){
        try {
            return ZzxFaceApplication.commonApplication.db.selector(FaceUser.class).where("number","=",number).findFirst();
        } catch (DbException e) {
            e.printStackTrace();
        }
        return null;
    }


    @Override
    public int compareTo(@NonNull FaceUser o) {
        if(this.getScore()>o.getScore()){
            return 1;
        }
        if(this.getScore()>o.getScore()){
        return -1;
        }
        return 0;
    }
}
