package com.jaydenxiao.androidfire.bean;

import java.util.List;

/**
 * des:
 * Created by xsf
 * on 2016.09.9:54
 */
public class User{

    /**
     * path : 4.jpg
     * editTime : 1503884415000
     * id : 10
     * department : {"children":[],"level":3,"name":"办公室","remark":"测试","id":2}
     * title : {"id":2,"title":"主任"}
     * userNumber : IMG_20170828_145850
     * realname : 刘强午
     * status : 1
     * username : liuqiangwu
     */

    private String path;
    private long editTime;
    private int id;
    private DepartmentBean department;
    private TitleBean title;
    private String userNumber;
    private String realname;
    private int status;
    private String username;

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
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

    public DepartmentBean getDepartment() {
        return department;
    }

    public void setDepartment(DepartmentBean department) {
        this.department = department;
    }

    public TitleBean getTitle() {
        return title;
    }

    public void setTitle(TitleBean title) {
        this.title = title;
    }

    public String getUserNumber() {
        return userNumber;
    }

    public void setUserNumber(String userNumber) {
        this.userNumber = userNumber;
    }

    public String getRealname() {
        return realname;
    }

    public void setRealname(String realname) {
        this.realname = realname;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public static class DepartmentBean {
        /**
         * children : []
         * level : 3
         * name : 办公室
         * remark : 测试
         * id : 2
         */

        private int level;
        private String name;
        private String remark;
        private int id;
        private List<?> children;

        public int getLevel() {
            return level;
        }

        public void setLevel(int level) {
            this.level = level;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getRemark() {
            return remark;
        }

        public void setRemark(String remark) {
            this.remark = remark;
        }

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public List<?> getChildren() {
            return children;
        }

        public void setChildren(List<?> children) {
            this.children = children;
        }
    }

    public static class TitleBean {
        /**
         * id : 2
         * title : 主任
         */

        private int id;
        private String title;

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }
    }
}
