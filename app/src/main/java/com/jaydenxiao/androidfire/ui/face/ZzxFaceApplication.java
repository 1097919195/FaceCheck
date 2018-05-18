package com.jaydenxiao.androidfire.ui.face;

import android.content.Context;
import android.os.Environment;

import com.jaydenxiao.androidfire.CrashHandler;
import com.jaydenxiao.androidfire.api.ApiConstants;
import com.jaydenxiao.androidfire.ui.ClockResult.config.FaceTimeConfig;
import com.jaydenxiao.androidfire.ui.setting.SettingService;
import com.jaydenxiao.androidfire.utils.StringUtils;
import com.jaydenxiao.common.baseapp.BaseApplication;

import org.xutils.DbManager;
import org.xutils.db.table.TableEntity;
import org.xutils.x;

import java.io.File;
import java.io.IOException;


/**
 * Created by xtt on 2017/8/16.
 */

public class ZzxFaceApplication extends BaseApplication {
    public static final  String portPath= Environment.getExternalStorageDirectory().getAbsolutePath()+"/com.checkface.config/checkfaceport.properties";
    public static final String logpath=FaceTimeConfig.facescore + "/facescore.log";
    public static ZzxFaceApplication commonApplication;
    public static int db_version=1;
    public SettingService settingService;
    private CrashHandler crashHandler=CrashHandler.getInstance();
    public static Context getAppContext() {
        return commonApplication;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        commonApplication=this;
        crashHandler.init(getAppContext());
        x.Ext.init(this);
        initDb();
        File file=new File(FaceRecognition.imageDir);
        if(!file.exists()){
            file.mkdirs();
        }
        File file1=new File(FaceTimeConfig.filepath);
        if(!file1.exists()){
            file1.mkdirs();
        }
        File file2=new File(FaceTimeConfig.facescore);
        if(!file2.exists()){
            file2.mkdirs();
        }
        File file3=new File(FaceTimeConfig.facedbpath);
        if(!file3.exists()){
            file3.mkdirs();
        }
        File file4=new File(portPath);
        if(!file4.exists()){
                try {
                file4.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        initIpAndPort();
    }



    private void initIpAndPort(){
        settingService=new SettingService();
        String IP=settingService.getConfig(SettingService.IP);
        String PORT=settingService.getConfig(SettingService.PORT);
        if(!StringUtils.isStringNull(IP)&&!StringUtils.isStringNull(PORT)){
            ApiConstants.IP=IP;
            ApiConstants.PORT=PORT;
        }else{
            settingService.addConfig(SettingService.IP,ApiConstants.IP);
            settingService.addConfig(SettingService.PORT,ApiConstants.PORT);
        }
    }


    public DbManager db;
    protected void initDb(){
        //本地数据的初始化
        DbManager.DaoConfig daoConfig = new DbManager.DaoConfig()
                .setDbName("face_db") //设置数据库名
                .setDbVersion(db_version).setDbDir(new File(FaceTimeConfig.facedbpath)) //设置数据库版本,每次启动应用时将会检查该版本号,
                //发现数据库版本低于这里设置的值将进行数据库升级并触发DbUpgradeListener
                .setAllowTransaction(true)//设置是否开启事务,默认为false关闭事务
                .setTableCreateListener(new DbManager.TableCreateListener() {
                    @Override
                    public void onTableCreated(DbManager db, TableEntity<?> table) {
                    }
                })
                .setDbUpgradeListener(new DbManager.DbUpgradeListener() {
                    @Override
                    public void onUpgrade(DbManager db, int oldVersion, int newVersion) {
                        //balabala...
                    }
                });//设置数据库升级时的Listener,这里可以执行相关数据库表的相关修改,比如alter语句增加字段等
        //.setDbDir(null);//设置数据库.db文件存放的目录,默认为包名下databases目录下
        db = x.getDb(daoConfig);
    }





}
