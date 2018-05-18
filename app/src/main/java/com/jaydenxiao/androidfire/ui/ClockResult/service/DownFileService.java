package com.jaydenxiao.androidfire.ui.ClockResult.service;

import android.app.IntentService;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.util.Log;

import com.jaydenxiao.androidfire.api.Api;
import com.jaydenxiao.androidfire.bean.FileBean;
import com.jaydenxiao.androidfire.ui.ClockResult.ClockResultActivity;
import com.jaydenxiao.androidfire.ui.ClockResult.model.FaceModel;
import com.jaydenxiao.androidfire.ui.face.FaceRecognition;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Response;

/**
 * Created by xtt on 2017/9/5.
 */

public class DownFileService extends IntentService {

    /**
     * 所需要下载的文件集合
     */
    List<FileBean.DataBean> data = null;
    FaceModel faceModel = null;
    /**
     * 下载失败的图片集合
     */
    List<FileBean.DataBean> downFailList = null;
    /**
     * 添加特征失败图片集合
     */
    List<FileBean.DataBean> addFaceFailList = null;

    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     *
     * @paramUsed to name the worker thread, important only for debugging.
     */
    public DownFileService() {
        super("DownFileService");
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        //工作线程
        Log.e("onHandleIntent", "onHandleIntentStart++" + System.currentTimeMillis());
        data = (List<FileBean.DataBean>) intent.getSerializableExtra("databean");
        //下载失败时候的文件信息记录
        downFailList = new ArrayList<>();
        addFaceFailList = new ArrayList<>();
        faceModel = new FaceModel();
        for (FileBean.DataBean data1 : data) {
            try {
                donwloadPic(data1);
            } catch (IOException e) {
                try {
                    donwloadPic(data1);
                } catch (IOException e1) {
                    try {
                        donwloadPic(data1);
                    } catch (IOException e2) {
                        downFailList.add(data1);
                        e2.printStackTrace();
                    }
                    e1.printStackTrace();
                }
                e.printStackTrace();
            }
        }
        FaceRecognition.mFaceRecognitionManager.updateFeatureToFile();
        Log.e("onHandleIntent", "onHandleIntentEnd++" + System.currentTimeMillis());
        //recordFail();
        Intent intent1 = new Intent(ClockResultActivity.DOWNSUCCESSACTION);
        sendBroadcast(intent1);
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }


    public void donwloadPic(FileBean.DataBean data1) throws IOException {
        Call<ResponseBody> call = Api.getDefault().downPictrueService(data1.getUserNumber());
        Response<ResponseBody> responseBody = null;
        responseBody = call.execute();
        String filepath = FaceRecognition.imageDir + "/" + data1.getPath();
        ResponseBody responseBody1 = responseBody.body();
        if (responseBody1 != null) {
            faceModel.wirte(filepath, responseBody1);
            float[]  addResult = FaceRecognition.addFeature(data1.getPath(), data1.getUserNumber());
            if (addResult==null) {
                //addResult = FaceRecognition.addFeature(data1.getPath(), data1.getUserNumber());
                Call<ResponseBody>  responseBodyCall=Api.getDefault().uploadUserFeatrue(data1.getUserNumber(),"0");
                responseBodyCall.execute();
                addFaceFailList.add(data1);
            }else{
                Call<ResponseBody>  responseBodyCall=Api.getDefault().uploadUserFeatrue(data1.getUserNumber(),"1");
                responseBodyCall.execute();
            }
        } else {
            downFailList.add(data1);
        }
    }

    /**
     * 记录失败的图片名称
     */
    /*private void recordFail() {
        StringBuilder stringBuilder = new StringBuilder();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        stringBuilder.append(simpleDateFormat.format(new Date()));
        stringBuilder.append("Download Pictrue fail:\n"+"Num:"+downFailList.size());
        for (FileBean.DataBean dataBean : downFailList) {
            stringBuilder.append(dataBean.getPath());
            stringBuilder.append("\n");
        }
        stringBuilder.append(simpleDateFormat.format(new Date()));
        stringBuilder.append("Add feature fail:\n"+"Num:"+addFaceFailList.size());
        for (FileBean.DataBean dataBean : addFaceFailList) {
            stringBuilder.append(dataBean.getPath());
            stringBuilder.append("\n");
        }
        ZzxFaceApplication.commonApplication.gLogger.debug(stringBuilder.toString());
    }*/
}
