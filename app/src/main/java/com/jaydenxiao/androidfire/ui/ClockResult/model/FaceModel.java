package com.jaydenxiao.androidfire.ui.ClockResult.model;

import android.util.Log;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.jaydenxiao.androidfire.api.Api;
import com.jaydenxiao.androidfire.api.HostType;
import com.jaydenxiao.androidfire.bean.CheckResult;
import com.jaydenxiao.androidfire.bean.CheckWorkBean;
import com.jaydenxiao.androidfire.bean.DetectOffline;
import com.jaydenxiao.androidfire.bean.FaceUser;
import com.jaydenxiao.androidfire.bean.FileBean;
import com.jaydenxiao.androidfire.bean.SettingBean;
import com.jaydenxiao.androidfire.bean.User;
import com.jaydenxiao.androidfire.ui.ClockResult.contract.FaceContract;
import com.jaydenxiao.androidfire.ui.face.FaceRecognition;
import com.jaydenxiao.androidfire.ui.face.ZzxFaceApplication;
import com.jaydenxiao.androidfire.utils.calendarutil.DateBean;
import com.jaydenxiao.common.baserx.RxSchedulers;

import org.xutils.db.sqlite.WhereBuilder;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Response;
import rx.Observable;
import rx.Subscriber;
import rx.functions.Func1;

import static com.alibaba.fastjson.JSON.parseObject;
import static com.jaydenxiao.common.baserx.RxSchedulers.io_main;

/**
 * Created by xtt on 2017/8/25.
 */

public class FaceModel implements FaceContract.Model {
    public List<FileBean.DataBean> dataBeanList = new ArrayList<>();
    FileBean.DataBean data1 = null;

    @Override
    public Observable<FileBean.DataBean> downPictrue(final List<FileBean.DataBean> data) {
        return Observable.from(data).flatMap(new Func1<FileBean.DataBean, Observable<ResponseBody>>() {
            @Override
            public Observable<ResponseBody> call(FileBean.DataBean dataBean) {
                data1 = dataBean;
                return Api.getDefault().downPictrue(dataBean.getUserNumber());
            }
        }).map(new Func1<ResponseBody, FileBean.DataBean>() {
            @Override
            public FileBean.DataBean call(ResponseBody responseBody) {
                String filepath = FaceRecognition.imageDir + "/" + data1.getPath();
                try {
                    wirte(filepath, responseBody);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                float[] addResult = FaceRecognition.addFeature(data1.getPath(), data1.getUserNumber());
                Log.e("addResult", "addResult" + addResult);
                FaceRecognition.mFaceRecognitionManager.updateFeatureToFile();
                return data1;
            }
        }).compose(RxSchedulers.<FileBean.DataBean>io_main());
    }


    @Override
    public Observable<List<SettingBean>> getSetting() {
        return Api.getDefault(HostType.WORK_CHECK).getSetting().compose(RxSchedulers.<List<SettingBean>>io_main());
    }

    @Override
    public Observable<Long> getLastSettingTime() {
        return Api.getDefault(HostType.WORK_CHECK).getLastSettingTime().map(new Func1<ResponseBody, Long>() {
            @Override
            public Long call(ResponseBody response) {
                JSONObject jsonObject = null;
                try {
                    jsonObject = JSON.parseObject(response.string());
                } catch (IOException e) {
                    e.printStackTrace();
                }
                Long lastSettingTime = jsonObject.getLong("timestamp");
                return lastSettingTime;
            }
        }).compose(RxSchedulers.<Long>io_main());
    }

    @Override
    public Observable<Long> getNeedTime() {
        return Api.getDefault(HostType.WORK_CHECK).getNeedSyncTime().map(new Func1<ResponseBody, Long>() {
            @Override
            public Long call(ResponseBody response) {
                JSONObject jsonObject = null;
                try {
                    jsonObject = JSON.parseObject(response.string());
                } catch (IOException e) {
                    e.printStackTrace();
                }
                Long lastSettingTime = jsonObject.getLong("timestamp");
                return lastSettingTime;
            }
        }).compose(RxSchedulers.<Long>io_main());
    }

    @Override
    public Observable<User> getUserInfo(String number) {
        return Api.getDefault().getUserByNumber(number).compose(RxSchedulers.<User>io_main());
    }

    @Override
    public Observable<CheckWorkBean> getUserCheckWork(String number) {
        return Api.getDefault(HostType.WORK_CHECK).getUserCount(number).compose(RxSchedulers.<CheckWorkBean>io_main());
    }

    @Override
    public Observable<CheckResult> uploadCheckWork(String number, String clientNumber, String similarity) {
        return Api.getDefault().uploadCheckWork(number, clientNumber, similarity).map(new Func1<ResponseBody, CheckResult>() {
            @Override
            public CheckResult call(ResponseBody responseBody) {
                CheckResult checkResult = new CheckResult();
                JSONObject jsonObject = null;
                try {
                    jsonObject = parseObject(responseBody.string());
                } catch (IOException e) {
                    e.printStackTrace();
                }
                if (jsonObject.getBoolean("sucessful")) {
                    JSONObject objectData = jsonObject.getJSONObject("data");
                    checkResult.setSucessful(jsonObject.getBoolean("sucessful"));
                    checkResult.setEditTime(objectData.getLong("timestamp"));
                    checkResult.setMsg(jsonObject.getString("msg"));
                } else {
                    checkResult.setSucessful(false);
                }
                return checkResult;
            }
        }).compose(RxSchedulers.<CheckResult>io_main());
    }

    @Override
    public void getDataBeanCalendar(List<DateBean> dateBeanList, CheckWorkBean checkWorkBean, FaceContract.CalendarDataListener calendarDataListener) {
        List<DateBean> dateBeanList1 = new ArrayList<DateBean>();
        for (DateBean dateBean : dateBeanList) {
            if (dateBean.getType() == 1) {
                if (checkWorkBean.getDailyStatus() == null || checkWorkBean.getDailyStatus().size() == 0) {
                    dateBean.setStatus(100);
                    dateBeanList1.add(dateBean);
                    continue;
                }
                try {
                    if(checkWorkBean.getDailyStatus().size()>dateBean.getSolar()[2] - 1){
                    dateBean.setStatus(checkWorkBean.getDailyStatus().get(dateBean.getSolar()[2] - 1));
                    }else{
                        dateBean.setStatus(100);
                    }
                } catch (Exception e) {
                    dateBean.setStatus(100);
                    e.printStackTrace();
                }
            }
            dateBeanList1.add(dateBean);
        }
        calendarDataListener.getDataBeanCalendar(dateBeanList1);
    }

    @Override
    public Observable<Long> getServerTime() {
        return Api.getDefault(HostType.WORK_CHECK).getSyncTime().map(new Func1<ResponseBody, Long>() {
            @Override
            public Long call(ResponseBody responseBody) {
                Long now = null;
                try {
                    JSONObject jsonObject = JSON.parseObject(responseBody.string());
                    now = jsonObject.getLong("now");
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return now;
            }
        }).compose(RxSchedulers.<Long>io_main());
    }

    long now1=0;
    @Override
    public Observable<Long> getCurrentTime(Long now) {
        now1=now;
        return Observable.interval(0, 1, TimeUnit.SECONDS).map(new Func1<Long, Long>() {
            @Override
            public Long call(Long aLong) {
                now1+=1000;
                return now1;
            }
        }).compose(RxSchedulers.<Long>io_main());
    }

    @Override
    public Observable<String> getCurrentTime() {
        return Observable.interval(0, 1, TimeUnit.SECONDS).map(new Func1<Long, String>() {
            @Override
            public String call(Long aLong) {
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MM-dd HH:mm:ss");
                String time = simpleDateFormat.format(new Date());
                String[] times = time.split(" ");
                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append(times[1]);
                stringBuilder.append("#");
                stringBuilder.append(times[0].split("-")[0]);
                stringBuilder.append("月");
                stringBuilder.append(times[0].split("-")[1]);
                stringBuilder.append("日");
                return stringBuilder.toString();
            }
        }).compose(RxSchedulers.<String>io_main());
    }

    //
    @Override
    public Observable<FileBean> getUserSync(String clientNumber, String start, String end) {
        return Api.getDefault().getUserSync(clientNumber, start, end).map(new Func1<FileBean, FileBean>() {
            @Override
            public FileBean call(FileBean fileBean) {
                return CompareFacePic(fileBean);
            }
        }).compose(RxSchedulers.<FileBean>io_main());
    }

    @Override
    public Observable timerGetUserSync() {
        return Observable.interval(1, 1, TimeUnit.HOURS).compose(io_main());
    }

    @Override
    public Observable<String> uploadNoNetWorkRecord() {
        return Observable.create(new Observable.OnSubscribe<String>() {
            @Override
            public void call(Subscriber<? super String> subscriber) {
                try {
                   List<DetectOffline> detectOfflines=ZzxFaceApplication.commonApplication.db.selector(DetectOffline.class).where("isupload","=",0).findAll();
                    if(detectOfflines!=null){
                    for(DetectOffline detectOffline:detectOfflines){
                        Response<ResponseBody> responseBody = null;
                        Call<ResponseBody> call=Api.getDefault().uploadNoNetCheckWork(detectOffline.getUsernumber(),detectOffline.getClient(),detectOffline.getSimilarity(),String.valueOf(detectOffline.getChecktime()));
                        responseBody=call.execute();
                        JSONObject jsonObject = null;
                        try {
                            jsonObject = parseObject(responseBody.body().string());
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        if (jsonObject.getBoolean("sucessful")) {
                            ZzxFaceApplication.commonApplication.db.delete(DetectOffline.class,WhereBuilder.b("id","=",detectOffline.getId()));
                        } else {
                        }
                    }
                    }
                    subscriber.onNext("success");
                } catch (Exception e) {
                    subscriber.onNext("fail");
                    e.printStackTrace();
                }
                subscriber.onCompleted();
            }
        }).compose(RxSchedulers.<String>io_main());
    }

    @Override
    public Observable<Boolean> checkIsNoNetWork() {
        return Observable.interval(0,10,TimeUnit.SECONDS).map(new Func1<Long, Boolean>() {
            @Override
            public Boolean call(Long aLong) {
                boolean result=false;
                Call<ResponseBody>  responseBodyCall=Api.getDefault().getSyncTimeCheckNetWork();
                try {
                    Response<ResponseBody> responseBody =responseBodyCall.execute();
                    if(responseBody!=null){
                    result=true;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    result=false;
                }
              /*  try {
                    Process p = Runtime.getRuntime().exec("ping -c 2 -w 100 " + ApiConstants.SERVER_IP);//ping3次
                    int status = p.waitFor();
                    if (status == 0) {
                        result = true;
                    } else {
                        result = false;
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }*/
                return result;
            }
        }).compose(RxSchedulers.<Boolean>io_main());
    }

    @Override
    public Observable timerToDeleteLog() {
        return Observable.interval(12,12,TimeUnit.HOURS).map(new Func1() {
            @Override
            public Object call(Object o) {
                File file=new File(ZzxFaceApplication.logpath);
                if(file!=null&&file.exists()){
                    file.delete();
                    file.mkdirs();
                }
                return null;
            }
        }).compose(RxSchedulers.io_main());
    }

    @Override
    public Observable timerReStart() {
        return Observable.interval(6,6,TimeUnit.HOURS).compose(RxSchedulers.io_main());
    }


    /**
     * 比对出需要下载的图片对象
     *
     * @param fileBean
     * @return
     */
    public FileBean CompareFacePic(FileBean fileBean) {
        boolean apk = FaceRecognition.getApk();
        FileBean fileBean1 = new FileBean();
        List<FileBean.DataBean> dateBeens = new ArrayList<FileBean.DataBean>();
        File imageFile = new File(FaceRecognition.imageDir);
        File[] images = imageFile.listFiles();
        List<File> files = new ArrayList<>();
        FaceUser faceUser=null;
        /*if (images != null && images.length > 0) {
            for (File f : images) {
                String fileName = f.getName();
                for (FileBean.DataBean dataBean : fileBean.getData()) {
                    if (fileName.equals(dataBean.getPath())) {
                        // 判断不是第一次表示有需要更新的，所以先删除此文件，再加入需要下载的图片集合中
                        if (apk) {
                            files.add(f);
                            dateBeens.add(dataBean);
                            break;
                        }
                    } else {
                        //去除重复的图片对象
                        if (!dateBeens.contains(dataBean)) {
                            dateBeens.add(dataBean);
                        }
                    }
                }
            }
        } else {
            dateBeens.addAll(fileBean.getData());
        }*/
        if (images != null && images.length > 0) {
            for (FileBean.DataBean dataBean : fileBean.getData()) {
                int i=0;
                for (File f : images) {
                    String fileName = f.getName();
                    if (fileName.equals(dataBean.getPath())) {
                        // 判断不是第一次表示有需要更新的，所以先删除此文件，再加入需要下载的图片集合中
                        if (apk) {
                            if(!files.contains(f)){
                            files.add(f);
                            }
                            if (!dateBeens.contains(dataBean)) {
                                if(dataBean.getStatus() != 0)
                                dateBeens.add(dataBean);
                            }
                            FaceRecognition.mFaceRecognitionManager.deleteFeatureByName(dataBean.getUserNumber());
                            FaceRecognition.mFaceRecognitionManager.updateFeatureToFile();
                        }else{
                            FaceRecognition.addFeature(fileName,dataBean.getUserNumber());
                            FaceRecognition.mFaceRecognitionManager.updateFeatureToFile();
                        }
                        break;
                    } else {
                        //去除重复的图片对象
                        i++;
                        if(i==images.length){
                        if (!dateBeens.contains(dataBean)) {
                            if(dataBean.getStatus() != 0)
                            dateBeens.add(dataBean);
                        }
                        }
                    }
                }
                //添加用户信息
                if(dataBean.getStatus()==0){
                       FaceUser.deleteFaceUser(dataBean.getUserNumber());
                }else{
                    faceUser=new FaceUser();
                    if(dataBean.getTitle()!=null){
                        faceUser.setTitle(dataBean.getTitle().getTitle());
                    }
                    faceUser.setNumber(dataBean.getUserNumber());
                    faceUser.setDepartMentName(dataBean.getDepartment().getName());
                    faceUser.setUserRealName(dataBean.getRealname());
                    if(dataBean.getPosition()!=null){
                        faceUser.setPosition(dataBean.getPosition().getPosition());
                    }
                    FaceUser.addFaceUser(faceUser);
                }
            }
        }else{
            for(FileBean.DataBean dataBean :fileBean.getData()) {
                if (dataBean.getStatus() == 0) {
                    FaceUser.deleteFaceUser(dataBean.getUserNumber());
                } else {
                    faceUser = new FaceUser();
                    if(dataBean.getTitle()!=null){
                        faceUser.setTitle(dataBean.getTitle().getTitle());
                    }
                    faceUser.setNumber(dataBean.getUserNumber());
                    faceUser.setDepartMentName(dataBean.getDepartment().getName());
                    faceUser.setUserRealName(dataBean.getRealname());
                    if(dataBean.getPosition()!=null){
                        faceUser.setPosition(dataBean.getPosition().getPosition());
                    }
                    FaceUser.addFaceUser(faceUser);
                    dateBeens.add(dataBean);
                }
            }
        }
        //删除人脸底库通过status=0去判断此人已被停用
        for (FileBean.DataBean dataBean : fileBean.getData()) {
            if (dataBean.getStatus() == 0) {
                File file = new File(FaceRecognition.imageDir, dataBean.getPath());
                if (file.exists()) {
                    file.delete();
                }
                FaceRecognition.mFaceRecognitionManager.deleteFeatureByName(dataBean.getUserNumber());
                FaceRecognition.mFaceRecognitionManager.updateFeatureToFile();
            }
        }
        //修改删除所有文件
        /*for (File file : files) {
            if (file.exists()) {
                file.delete();
            }
        }*/
        fileBean1.setData(dateBeens);
        if (!FaceRecognition.getApk()) {
            FaceRecognition.putApk(true);
        }
        return fileBean1;
    }


    public synchronized void wirte(String filepath, ResponseBody responseBody) throws IOException {
        InputStream is = responseBody.byteStream();//从服务器得到输入流对象
        long sum = 0;
        File file = new File(filepath);//根据目录和文件名得到file对象
        FileOutputStream fos = new FileOutputStream(file);
        byte[] buf = new byte[1024];
        int len = 0;
        while ((len = is.read(buf)) != -1) {
            fos.write(buf, 0, len);
        }
        fos.flush();
    }
}
