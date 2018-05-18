package com.jaydenxiao.androidfire.ui.ClockResult.config;

import android.content.Context;
import android.os.Environment;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.Serializable;
import java.util.Properties;

/**
 * Created by xtt on 2017/9/7.
 *
 */

public class FaceTimeConfig implements Serializable{
    public Properties properties;
    public static final String facedbpath= Environment.getExternalStorageDirectory().getAbsolutePath()+"/checkfacedb";
    public static final String filepath=  Environment.getExternalStorageDirectory().getAbsolutePath()+"/com.checkface.config";
    public static final String facescore=Environment.getExternalStorageDirectory().getAbsolutePath()+"/com.checkface.config/facescore";
    public static final String configFileName="faceconfig.properties";
    public static final String lastSettingTime="lastSettingTime";//最新系统配置时间
    public static final String needSync="needSync";//最新用户图片更新时间
    public static final String SCEN_THRESHOLD="SCEN_THRESHOLD";//人脸识别阈值
    public static final String FACE_SIZE="FACE_SIZE";//最小人脸像素大小
    public static final String CONTINUE_FACE_MAXCOUNT="CONTINUE_FACE_MAXCOUNT";//人走开，最大持续人脸数字
    public static final String WAIT_TIME_COUNT="WAIT_TIME_COUNT";//待机之前持续时间次数

    private String path;
    private Context context;

    private FaceTimeConfig(){}

    public FaceTimeConfig(Context context){
        this.context=context;
       path=filepath+"/"+configFileName;
        properties=loadConfig(path);
        if(properties==null){
            properties=new Properties();
            saveConfig(path,properties);
        }
    }

    public  Properties loadConfig(String file) {
        Properties properties = new Properties();
        try {
            FileInputStream s = new FileInputStream(file);
            properties.load(s);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return properties;
    }
    //保存配置文件
    public  boolean saveConfig( String file, Properties properties) {
        try {
            File fil=new File(file);
            if(!fil.exists())
                fil.createNewFile();
            FileOutputStream s = new FileOutputStream(fil);
            properties.store(s, "");
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public  void addConfig(String key,String value){
        properties.put(key,value);
        saveConfig(path,properties);
    }

    public void removeConfig(String key){
        properties.remove(key);
        saveConfig(path,properties);
    }

    public String getConfig(String key){
        return properties.getProperty(key);
    }

}
