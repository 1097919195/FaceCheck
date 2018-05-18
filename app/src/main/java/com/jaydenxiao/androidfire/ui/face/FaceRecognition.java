package com.jaydenxiao.androidfire.ui.face;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageFormat;
import android.hardware.Camera;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Environment;
import android.util.Log;
import android.util.Pair;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.jaydenxiao.androidfire.R;
import com.jaydenxiao.androidfire.bean.FaceUser;
import com.jaydenxiao.androidfire.bean.SimilarityConfirm;
import com.jaydenxiao.androidfire.utils.CameraProviderUtil;
import com.minivision.facerecognitionlib.FaceRecognitionManager;
import com.minivision.facerecognitionlib.Utils;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


/**
 * Created by xtt on 2017/8/21.
 */

public class FaceRecognition implements SurfaceHolder.Callback, Camera.PreviewCallback {
    public static final String imageDir = Environment.getExternalStorageDirectory().getAbsolutePath() + "/Minivision/images";
    private static final  String FACEKEY="FACE_REC";
    private static final String LASTERUPDATETIME="LasterUpdateTime";
    public static FaceRecognitionManager mFaceRecognitionManager=null;


    public static void addFeature(){
        File imageFile = new File(imageDir);
        File[] images = imageFile.listFiles();
        if(images != null && images.length > 0) {
            for (File f : images) {
                String fileName = f.getName();
                String name = fileName.substring(0, fileName.lastIndexOf("."));
                //此方法也会提取人脸特征值，没有人脸时 返回 null; 图片中包含多张人脸时，返回最大脸的特征值
//                float[] feature = mFaceRecognitionManager.getFeature(BitmapFactory.decodeFile(f.getAbsolutePath()));
                BitmapFactory.Options opts = new BitmapFactory.Options();
                Bitmap bitmap = BitmapFactory.decodeFile(f.getAbsolutePath(),opts);
                int w = 480, h = 640;
                if (bitmap.getWidth() > bitmap.getHeight()){
                    h = (int) (640 * bitmap.getHeight() / (float)bitmap.getWidth());
                    w = 640;
                    if(h%2!=0){
                        h -= 1;
                    }
                }else {
                    w = (int) (640 * bitmap.getWidth() / (float)bitmap.getHeight());
                    if(w%2!=0){
                        w -= 1;
                    }
                }
                Bitmap scaledBitmap = Bitmap.createScaledBitmap(bitmap, w, h, false);

                //Log.d(TAG, "get feature : name -->" + fileName);
                Pair<Integer, float[]> result = mFaceRecognitionManager.getFeature1(scaledBitmap);
                if(result.first == 1) {
                    float[] feature = result.second;
                    if(feature != null) {
                        mFaceRecognitionManager.addFeature(name, feature);
                    }
                }else {
                    Log.d(TAG, "checked face num : " + result.first);
                }

                if(bitmap!=null){
                    bitmap.recycle();
                    bitmap=null;
                }


            }
        }
    }


    public static float[]  addFeature(String filename,String name){
        File file=new File(imageDir,filename);
        if(file.exists()){
            BitmapFactory.Options opts = new BitmapFactory.Options();
            opts.inDither=false;
            Bitmap bitmap = BitmapFactory.decodeFile(file.getAbsolutePath(),opts);
            int w = 480, h = 640;
            if (bitmap.getWidth() > bitmap.getHeight()){
                h = (int) (640 * bitmap.getHeight() / (float)bitmap.getWidth());
                w = 640;
                if(h % 2 !=0){
                    h -= 1;
                }
            }else {
                w = (int) (640 * bitmap.getWidth() / (float)bitmap.getHeight());
                if(w % 2 !=0){
                    w -= 1;
                }
            }
            //Log.e("AA","W:"+w+"H:"+h+"");
            Bitmap scaledBitmap = Bitmap.createScaledBitmap(bitmap, w, h, false);
            //Log.e("AA","scaledBitmap:"+scaledBitmap.getWidth()+"scaledBitmap:"+scaledBitmap.getHeight());
            Pair<Integer, float[]> result = mFaceRecognitionManager.getFeature1(scaledBitmap);
            if(result.first == 1) {
                float[] feature = result.second;
                if(feature != null) {
                    mFaceRecognitionManager.addFeature(name, feature);
                    return feature;
                }
            }else {
               /* String path=Environment.getExternalStorageDirectory().getAbsolutePath() + "/Minivision/capture/"+filename;
                try {
                    scaledBitmap.compress(Bitmap.CompressFormat.JPEG,100,new FileOutputStream(path));
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
                Log.d(TAG, "checked face num : " + result.first);*/
            }
            if(bitmap!=null){
                bitmap.recycle();
                bitmap=null;
            }


            if(scaledBitmap!=null){
                scaledBitmap.recycle();
                scaledBitmap=null;
            }
    }
        return null;
    }


    private void delete(String number){

    }

    SoundPool  soundPool=null;
    public  void playVoice(int type){
        soundPool = new SoundPool(10, AudioManager.STREAM_SYSTEM, 5);
        if(type==1) {
            soundPool.load(context, R.raw.success, 1);
        }else if(type==0){
            soundPool.load(context, R.raw.fail, 1);
        }else if(type==2){
            soundPool.load(context,R.raw.face_max_one,1);
        }
        surfaceView.postDelayed(new Runnable() {
            @Override
            public void run() {
                soundPool.play(1,1, 1, 0, 0, 1);
            }
        },1000);
    }


    /**
     * 更新 最后同步时间
     */
    public static void putLasterUpdateTime(String updateTime){
        SharedPreferences sp=ZzxFaceApplication.getAppContext().getSharedPreferences(LASTERUPDATETIME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor=sp.edit();
        editor.putString(LASTERUPDATETIME,updateTime);
        editor.commit();
    }

    /**
     * 获取 最后同步时间
     * @return
     */
    public static String getLasterUpdateTime(){
        SharedPreferences sp=ZzxFaceApplication.getAppContext().getSharedPreferences(LASTERUPDATETIME, Context.MODE_PRIVATE);
        return sp.getString(LASTERUPDATETIME,"0");
    }



    public static  void putApk(boolean isonces){
        SharedPreferences sp=ZzxFaceApplication.getAppContext().getSharedPreferences(FACEKEY, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor=sp.edit();
        editor.putBoolean(FACEKEY,isonces);
        editor.commit();
    }

    public static boolean getApk(){
        SharedPreferences sp=ZzxFaceApplication.getAppContext().getSharedPreferences(FACEKEY, Context.MODE_PRIVATE);
        return sp.getBoolean(FACEKEY,false);
    }

    /**
     * 人脸识别方法
     */
    private static final String TAG=FaceRecognition.class.getSimpleName();
    public SurfaceView surfaceView;
    private Context  context;

    private static final float ALGORITHM_RATIO = 2f;
    private int mBackCameraId = Camera.CameraInfo.CAMERA_FACING_BACK;
    private int mFrontCameraId = Camera.CameraInfo.CAMERA_FACING_FRONT;
    private int mCameraId;
    private Camera mCamera;
    private int mPreviewHeight;
    private int mPreviewWidth;
    private int mScreenWidth;
    private int mScreenHeight;
    private volatile boolean isThreadWorking;
    private Thread detectThread;
    private FaceRecogntionListener faceRecogntionListener;
    private  SurfaceHolder holder;
    public  void initFaceEngine(SurfaceView surfaceView,Context  context,int mScreenWidth,int mScreenHeight,FaceRecogntionListener faceRecogntionListener){
        this.surfaceView=surfaceView;
        this.context=context;
        this.mScreenWidth=mScreenWidth;
        this.mScreenHeight=mScreenHeight;
        this.faceRecogntionListener=faceRecogntionListener;
        holder = surfaceView.getHolder();
        holder.addCallback(this);
        holder.setFormat(ImageFormat.NV21);
        boolean h=CameraProviderUtil.hasBackFacingCamera();
        boolean q=CameraProviderUtil.hasFrontFacingCamera();
        Log.e(TAG,"H:"+h+"Q:"+q);
    }

    @Override
    public void onPreviewFrame(byte[] data, Camera camera) {
        if(!isThreadWorking && data != null) {
            isThreadWorking = true;
            //waitForDetectThreadComplete();
            detectThread = new Thread(new RecognitionRunnable(data));
            detectThread.start();
        }
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        try {
//            mCameraId = mBackCameraId;
            mCameraId = mBackCameraId;
            mCamera = Camera.open(mCameraId);
            mCamera.setPreviewDisplay(holder);
            Log.d(TAG, "surfaceCreated: init camera success" );
        }catch (Exception e) {
            Log.d(TAG, "OpenCamera: 开启前置Camera失败...");
            e.printStackTrace();
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        if (holder.getSurface() == null) {
            return;
        }
        //停止当前预览
        try {
            mCamera.stopPreview();
        } catch (Exception ignored) {

        }

        configureCamera(width, height);
        setDisplayOrientation();
        try {
            mCamera.setPreviewDisplay(holder);
        } catch (IOException e) {
            e.printStackTrace();
        }
        //启动摄像头预览
        startPreview();
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        if(mCamera != null) {
            mCamera.setPreviewCallbackWithBuffer(null);
            mCamera.setErrorCallback(null);
            mCamera.release();
            mCamera = null;
        }
    }
    private void configureCamera(int width, int height) {
        Camera.Parameters parameters = mCamera.getParameters();
        //设置预览界面尺寸和自动对焦:
        setOptimalPreviewSize(parameters, width, height);
        //setAutoFocus(parameters);
        //parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_FIXED);
        mCamera.cancelAutoFocus();
        //设置相机参数:
        mCamera.setParameters(parameters);

    }

    private void setOptimalPreviewSize(Camera.Parameters cameraParameters, int width, int height) {
    /*    mPreviewWidth = 1088;
        mPreviewHeight = 1088;*/
        List<Camera.Size> sizes=cameraParameters.getSupportedPreviewSizes();
        for(Camera.Size size:sizes) {
            Log.d(TAG, "setOptimalPreviewSize: " + size.width + "----" + size.height);
            /*if((size.width%size.height==0)){
                mPreviewWidth=size.width;
                mPreviewHeight=size.height;
                break;
            }*/
            mPreviewWidth=size.width;
            mPreviewHeight=size.height;

        }
        // cameraParameters.setPreviewSize(1280, 720);
        cameraParameters.setPreviewSize(mPreviewWidth,mPreviewHeight);
    }

    private void setAutoFocus(Camera.Parameters cameraParameters) {
        List<String> focusModes = cameraParameters.getSupportedFocusModes();
        if (focusModes.contains(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE))
            cameraParameters.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE);
    }

    private void setDisplayOrientation() {
        //设置显示方向
        int displayRotation = Utils.getDisplayRotation((Activity) context);
        int displayOrientation = Utils.getDisplayOrientation(displayRotation, mCameraId);
        mCamera.setDisplayOrientation(displayOrientation);
    }

    public void startPreview() {
        if (mCamera != null) {
            isThreadWorking = false;
            mCamera.startPreview();
            mCamera.setPreviewCallback(this);
        }
    }

    private void waitForDetectThreadComplete() {
        if (detectThread == null) {
            return;
        }

        if (detectThread.isAlive()) {
            try {
                detectThread.join();
                detectThread = null;
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
    class RecognitionRunnable implements Runnable {
        private byte[] frame;

        public RecognitionRunnable(byte[] data) {
            frame = data;
        }

        @Override
        public void run() {
            if(faceRecogntionListener!=null){
                faceRecogntionListener.onFaceResult(frame,mPreviewWidth,mPreviewHeight);
            }
            isThreadWorking = false;
        }
    }

    public interface FaceRecogntionListener{
        void onFaceResult(byte[] bytes,int mPreviewWidth,int mPreviewHeight);
    }


    /**
     * 判断前后2个人的分数值是不是非常接近
     */
    public ArrayList<FaceUser>  TwoSimilarity(double similarity_grap,List<SimilarityConfirm> similarityConfirms){
        ArrayList<FaceUser> faceUsers=new ArrayList<>();
        SimilarityConfirm similarityConfirm1=similarityConfirms.get(0);
        SimilarityConfirm similarityConfirm2=similarityConfirms.get(1);
        FaceUser faceUser=new FaceUser();
        String name1=similarityConfirm1.getRecognitionFaces().get(1).getName();
        String name2=similarityConfirm2.getRecognitionFaces().get(1).getName();

        faceUser.setScore(similarityConfirm1.getRecognitionFaces().get(0).getScore());
        faceUser.setNumber(similarityConfirm1.getRecognitionFaces().get(0).getName());
        faceUsers.add(faceUser);
            BigDecimal b1 = new BigDecimal(Double.toString(similarityConfirm1.getRecognitionFaces().get(0).getScore()));
            BigDecimal b2 = new BigDecimal(Double.toString(similarityConfirm1.getRecognitionFaces().get(1).getScore()));
            double cha1=b1.subtract(b2).doubleValue();
             BigDecimal b3 = new BigDecimal(Double.toString(similarityConfirm2.getRecognitionFaces().get(0).getScore()));
             BigDecimal b4 = new BigDecimal(Double.toString(similarityConfirm2.getRecognitionFaces().get(1).getScore()));
            double cha2=b3.subtract(b4).doubleValue();
            if(cha1<=similarity_grap){
                FaceUser faceUser1=new FaceUser();
                faceUser1.setScore(similarityConfirm1.getRecognitionFaces().get(1).getScore());
                faceUser1.setNumber(similarityConfirm1.getRecognitionFaces().get(1).getName());
                faceUsers.add(faceUser1);
            }
            if(cha2<=similarity_grap&&!name2.equals(name1)){
                FaceUser faceUser2=new FaceUser();
                faceUser2.setScore(similarityConfirm2.getRecognitionFaces().get(1).getScore());
                faceUser2.setNumber(similarityConfirm2.getRecognitionFaces().get(1).getName());
                faceUsers.add(faceUser2);
            }
        Collections.sort(faceUsers);
        return faceUsers;
    }

    public  void setCameraDisplayOrientation(Activity activity,
                                             int cameraId, android.hardware.Camera camera) {
        try {
            android.hardware.Camera.CameraInfo info = new android.hardware.Camera.CameraInfo();
            android.hardware.Camera.getCameraInfo(cameraId, info);
            int rotation = activity.getWindowManager().getDefaultDisplay().getRotation();
            int degrees = 0;
            switch (rotation) {
                case Surface.ROTATION_0:
                    degrees = 0;
                    break;
                case Surface.ROTATION_90:
                    degrees = 90;
                    break;
                case Surface.ROTATION_180:
                    degrees = 180;
                    break;
                case Surface.ROTATION_270:
                    degrees = 270;
                    break;
            }

            int result;
            if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
                result = (info.orientation + degrees) % 360;
                result = (360 - result) % 360;  // compensate the mirror
            } else {  // back-facing
                result = (info.orientation - degrees + 360) % 360;
            }
            camera.setDisplayOrientation(result);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}
