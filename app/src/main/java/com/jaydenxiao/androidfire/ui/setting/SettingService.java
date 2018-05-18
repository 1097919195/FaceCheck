package com.jaydenxiao.androidfire.ui.setting;

import com.jaydenxiao.androidfire.ui.face.ZzxFaceApplication;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.Properties;

/**
 * Created by xtt on 2017/10/26.
 */

public class SettingService {
    public static  final String IP="IP";
    public static  final String PORT="PORT";
    public Properties properties;

    public SettingService(){
        properties=loadConfig(ZzxFaceApplication.portPath);
        if(properties==null){
            properties=new Properties();
            saveConfig(ZzxFaceApplication.portPath,properties);
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

    public  void addConfig(String key,String value){
        properties.put(key,value);
        saveConfig(ZzxFaceApplication.portPath,properties);
    }

    public void removeConfig(String key){
        properties.remove(key);
        saveConfig(ZzxFaceApplication.portPath,properties);
    }

    public String getConfig(String key){
        return properties.getProperty(key);
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
}
