package com.jaydenxiao.androidfire.ui.ClockResult.config;

import android.content.Context;
import android.os.Environment;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.Properties;

/**
 * Created by xtt on 2017/11/30.
 */

public class FaceScoreLog {
    public Properties properties;
    public static final String facescore=Environment.getExternalStorageDirectory().getAbsolutePath()+"/com.checkface.config/facescore";
    public static final String configFileName="faceconfig.properties";

    private String path;
    private Context context;

    private FaceScoreLog(){}

    public FaceScoreLog(Context context){
        this.context=context;
        path=facescore+"/"+configFileName;
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
