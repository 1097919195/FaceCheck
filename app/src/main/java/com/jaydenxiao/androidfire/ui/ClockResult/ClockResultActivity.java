package com.jaydenxiao.androidfire.ui.ClockResult;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Environment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.jaydenxiao.androidfire.R;
import com.jaydenxiao.androidfire.api.ApiConstants;
import com.jaydenxiao.androidfire.bean.CheckResult;
import com.jaydenxiao.androidfire.bean.CheckWorkBean;
import com.jaydenxiao.androidfire.bean.DetectOffline;
import com.jaydenxiao.androidfire.bean.FaceUser;
import com.jaydenxiao.androidfire.bean.FileBean;
import com.jaydenxiao.androidfire.bean.SettingBean;
import com.jaydenxiao.androidfire.bean.SimilarityConfirm;
import com.jaydenxiao.androidfire.bean.User;
import com.jaydenxiao.androidfire.ui.ChanceFaceActivity;
import com.jaydenxiao.androidfire.ui.ClockResult.config.FaceScoreLog;
import com.jaydenxiao.androidfire.ui.ClockResult.config.FaceTimeConfig;
import com.jaydenxiao.androidfire.ui.ClockResult.contract.FaceContract;
import com.jaydenxiao.androidfire.ui.ClockResult.model.FaceModel;
import com.jaydenxiao.androidfire.ui.ClockResult.presenter.FacePresenter;
import com.jaydenxiao.androidfire.ui.ClockResult.service.DownFileService;
import com.jaydenxiao.androidfire.ui.face.FaceRecognition;
import com.jaydenxiao.androidfire.ui.face.ZzxFaceApplication;
import com.jaydenxiao.androidfire.ui.setting.SettingActivity;
import com.jaydenxiao.androidfire.utils.AudioUtils;
import com.jaydenxiao.androidfire.utils.StringUtils;
import com.jaydenxiao.androidfire.utils.calendarutil.CalendarUtil;
import com.jaydenxiao.androidfire.utils.calendarutil.DateBean;
import com.jaydenxiao.androidfire.utils.calendarutil.SolarUtil;
import com.jaydenxiao.common.base.BaseActivity;
import com.jaydenxiao.common.commonutils.AppPhoneMgr;
import com.jaydenxiao.common.commonutils.NetWorkUtils;
import com.jaydenxiao.common.commonutils.ToastUitl;
import com.minivision.facerecognitionlib.FaceRecognitionManager;
import com.minivision.facerecognitionlib.entity.RecognizedFace;

import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

import java.io.File;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import butterknife.Bind;

import static com.jaydenxiao.androidfire.ui.ClockResult.config.FaceTimeConfig.needSync;
import static com.jaydenxiao.androidfire.utils.calendarutil.CalendarUtil.getMonthDate;


/**
 * 打卡成功后结果页面
 */
public class ClockResultActivity extends BaseActivity<FacePresenter, FaceModel> implements FaceRecognition.FaceRecogntionListener, FaceContract.View {
    int WAIT_TIME_COUNT = 300;
    int continuous_face = 2;//连续检测人脸通过数
    double similarity_grap=1.00;//如果两个人脸分数相差2分之类，则需要2次确认
    @Bind(R.id.Tv_time_minute)
    TextView TvTimeMinute;
    @Bind(R.id.Tv_time_date)
    TextView TvTimeDate;
    @Bind(R.id.Rl_await_parent)
    RelativeLayout RlAwaitParent;
    //推荐分数阈值 82.90,87.01
    private double THRESHOLD = 87.01;
    private int FACE_SIZE = 150;
    public static final String DOWNSUCCESSACTION = "download.success";
    private static final String TAG = ClockResultActivity.class.getSimpleName();
    public FaceRecognitionManager mFaceRecognitionManager;
    RecyclerView.LayoutManager layoutManager = new GridLayoutManager(ClockResultActivity.this,
            7, // 每行显示item项数目
            GridLayoutManager.VERTICAL, //水平排列
            false);
    FaceRecognition faceRecognition = null;
    @Bind(R.id.Rv_clock_result)
    RecyclerView RvClockResult;
    @Bind(R.id.Rl_clock_result_patent)
    RelativeLayout Rl_clock_result_patent;
    @Bind(R.id.Sv_face)
    SurfaceView SvFace;
    @Bind(R.id.Iv_pagebg)
    ImageView Iv_pagebg;
    @Bind(R.id.Ll_checkwork_parent)
    LinearLayout Ll_checkwork_parent;
    @Bind(R.id.Tv_usernumber)
    TextView TvUsernumber;
    @Bind(R.id.Tv_username)
    TextView TvUsername;
    @Bind(R.id.Tv_userdept)
    TextView TvUserdept;
    @Bind(R.id.Tv_userpost)
    TextView TvUserpost;
    @Bind(R.id.Tv_userpost_title)
    TextView Tv_userpost_title;
    @Bind(R.id.Iv_checkwork_pic)
    ImageView IvCheckworkPic;
    @Bind(R.id.Tv_checkwork_time)
    TextView TvCheckworkTime;
    @Bind(R.id.Tv_checkwork_describe)
    TextView TvCheckworkDescribe;
    @Bind(R.id.Ll_weekresult_parent)
    LinearLayout Ll_weekresult_parent;
    @Bind(R.id.Tv_week_tip)
    TextView TvWeekTip;
    @Bind(R.id.Tv_week_normal)
    TextView TvWeekNormal;
    @Bind(R.id.Tv_week_tardiness)
    TextView TvWeekTardiness;
    @Bind(R.id.Tv_week_leavingerly)
    TextView TvWeekLeavingerly;
    @Bind(R.id.Tv_week_lost)
    TextView TvWeekLost;
    @Bind(R.id.Ll_week_parent)
    LinearLayout LlWeekParent;
    @Bind(R.id.Iv_user_icon)
    ImageView IvUserIcon;
    @Bind(R.id.Tv_current_time_minute)
    TextView Tv_current_time_minute;
    @Bind(R.id.Tv_current_time_day)
    TextView Tv_current_time_day;
    @Bind(R.id.Ll_screen_parent)
    LinearLayout Ll_screen_parent;
    @Bind(R.id.Iv_screen_img)
    ImageView Iv_screen_img;
    @Bind(R.id.Tv_initface_tip)
    TextView Tv_initface_tip;
    @Bind(R.id.Tv_nonet_tip)
    TextView Tv_nonet_tip;
    @Bind(R.id.Ll_currenttime_setting)
    LinearLayout Ll_currenttime_setting;
    @Bind(R.id.Btn_format_detect)
    Button Btn_format_detect;
    @Bind(R.id.Btn_ls_detect)
    Button Btn_ls_detect;

    private int mScreenWidth;
    private int mScreenHeight;
    private int year;
    private int month;
    private int day;
    private int width;

    private String androidId;

    ClockResultAdapter clockResultAdapter;
    List<DateBean> dateBeens = new ArrayList<>();

    List<DateBean> currentDateBean = new ArrayList<>();
    /**
     * 当前用户
     */
    private String currentName = "";

    DownSuccessReciver downSuccessReciver = null;
    FaceTimeConfig faceTimeConfig = null;
    FaceScoreLog faceScoreLog=null;

    long currentTime = System.currentTimeMillis();//每秒更新一次

    int detecttype=-1;//打卡类型0普通打卡1留守打卡

    AudioUtils audioUtils=null;

    @Override
    public int getLayoutId() {
        return R.layout.activity_clock_result;
    }

    @Override
    public void initPresenter() {
        mPresenter.setVM(this, mModel);
    }

    @Override
    public void initView() {
        getWindow().setBackgroundDrawable(null);//window的默认毕竟色是白色，如果没了那就要在xml根布局自己把背景色白色加上，要不会有问题
        audioUtils=new AudioUtils(this);
        androidId = AppPhoneMgr.getAndroidID(ZzxFaceApplication.getAppContext());
        ViewGroup.LayoutParams lp = RvClockResult.getLayoutParams();
        width = lp.width;
        initCalendar();
        faceRecognition = new FaceRecognition();
        faceScoreLog=new FaceScoreLog(this);
        initCamera();
        mPresenter.setWifi(true,this);
        mPresenter.TimeToDeleteLog();
        ZzxFaceApplication.commonApplication.isConnectNet = NetWorkUtils.isConnect(ZzxFaceApplication.getAppContext());
        Log.e("isConnectNet",String.valueOf(ZzxFaceApplication.commonApplication.isConnectNet));
        if (ZzxFaceApplication.commonApplication.isConnectNet) {
            mPresenter.uploadNoNetWorkRecord();
        }
        faceTimeConfig = new FaceTimeConfig(ClockResultActivity.this);
        downSuccessReciver = new DownSuccessReciver();
        IntentFilter intentFilter = new IntentFilter(DOWNSUCCESSACTION);
        registerReceiver(downSuccessReciver, intentFilter);
        //定时获取有无需要同步的图片数据
        mPresenter.timerGetUserSync();
        //定时检测有无可用网络
        mPresenter.checkIsNoNetWork();
        //与服务器时间同步
        mPresenter.getServerTime();
        mPresenter.getLastSettingTime();

        Ll_currenttime_setting.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                startActivity(SettingActivity.class);
                return false;
            }
        });

        Btn_format_detect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                detecttype=0;
                Btn_format_detect.setAlpha(1f);
                Btn_ls_detect.setAlpha(0.45f);
            }
        });

        Btn_ls_detect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                detecttype=1;
                Btn_format_detect.setAlpha(0.45f);
                Btn_ls_detect.setAlpha(1f);
            }
        });

        /*SvFace.postDelayed(new Runnable() {
            @Override
            public void run() {
                //重启应用
                Intent i = getBaseContext().getPackageManager()
                        .getLaunchIntentForPackage(getBaseContext().getPackageName());
                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(i);
            }
        },8000);*/
    }

    private void initFaceFeature() {
        //AsyncTask 实际上是一个帮助类，可以让我们很简单的从子线程切换到主线程，去更新UI 界面,而我们却又可以在 doInBackground() 方法中异步执行耗时任务。这样的话，我们就不需要频繁的手动切换线程去更新UI了。
        new AsyncTask<Void, Void, Void>() {//http://blog.csdn.net/hwaphon/article/details/51158655
            //该方法是 AysncTask 运行的第一个方法， 可以用于初始化UI，比如说 显示一个进度条等。
            @Override
            protected void onPreExecute() {
            }

            //这个方法所有代码都会在一个子线程中执行，所有的耗时任务都在这处理，处理完之后通过 return 将结果返回。
            @Override
            protected Void doInBackground(Void... params) {
                try {
                    int result = FaceRecognitionManager.init(ZzxFaceApplication.getAppContext(), FACE_SIZE);
                    Log.e("AA", FACE_SIZE + "FACE_SIZE++++");
                    //Log.d(TAG, "int FaceRecognitionManager result ----> " + result);
                    if (result == 200) {
                        mFaceRecognitionManager = FaceRecognitionManager.getInstance();
                        FaceRecognition.mFaceRecognitionManager = mFaceRecognitionManager;
                        mPresenter.getNeedSync();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return null;
            }

            //doInBackground 执行return语句后，就会转到该方法上来，主要用于处理一些善后工作，比如关闭进度条。
            @Override
            protected void onPostExecute(Void aVoid) {
                //mPresenter.K();
            }
        }.execute();
    }


    public class DownSuccessReciver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals(DOWNSUCCESSACTION)) {
                SvFace.post(new Runnable() {
                    @Override
                    public void run() {
                        faceTimeConfig.addConfig(needSync, needTime + "");
                        showCamera();
                    }
                });

            }
        }
    }


    private void initCalendar() {
        Calendar calendar = Calendar.getInstance();
        year = calendar.get(Calendar.YEAR);
        month = calendar.get(Calendar.MONTH) + 1;
        day = calendar.get(Calendar.DAY_OF_MONTH);
        TvWeekTip.post(new Runnable() {
            @Override
            public void run() {
                TvWeekTip.setText(month + "月份考勤记录");
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
    }


    private void initCamera() {
        mScreenWidth = getWindowManager().getDefaultDisplay().getWidth();
        mScreenHeight = getWindowManager().getDefaultDisplay().getHeight();
        faceRecognition.initFaceEngine(SvFace, ClockResultActivity.this, mScreenWidth, mScreenHeight, ClockResultActivity.this);
    }

    int MaxCount = 10;

    //人走开之后延迟计数
    int currentCount = 0;
    //待机计数
    int currentWaitCount = 0;
    //人脸放入时，识别多少次才识别出真脸
    //int faceRecogTime = 0;

    List<String> currentNames = new ArrayList<>();
    List<SimilarityConfirm> similarityConfirms=new ArrayList<>();

    DetectOffline detectOffline = new DetectOffline();


    private boolean startFace=false;

    int maxFaceNum2=10;//当几次连续识别到一张脸时进行分数取值。
    int currentMaxFaceNum2=11;//

    @Override
    public void onFaceResult(byte[] frame, int mPreviewWidth, int mPreviewHeight) {
        //1366*768
        //Log.e("AA",SvFace.getWidth()+"------------------"+SvFace.getHeight());
        //Log.d("AA", "frame++:" + System.currentTimeMillis() + "");
        //swtich(frame,mPreviewWidth,mPreviewHeight);
        initCalendar();
        if (mFaceRecognitionManager != null&&startFace) {
            //Log.d("AA","faceNum++:"+System.currentTimeMillis()+"");
            int faceNum = mFaceRecognitionManager.detectFace(frame, mPreviewWidth, mPreviewHeight);
            Log.d(TAG, "detect face number : " + faceNum);
            if(faceNum==1){
                currentMaxFaceNum2++;
            }else if(faceNum>1){
                currentMaxFaceNum2=0;
                currentName = "";
                currentNames.clear();
                similarityConfirms.clear();
                currentCount = 0;
                SvFace.post(new Runnable() {
                    @Override
                    public void run() {
                     ToastUitl.show("只允许一张脸",0);
                    }
                });
            }/*else{
                currentMaxFaceNum2++;
            }*/
            if(maxFaceNum2<currentMaxFaceNum2) {
                if (faceNum != 0) {
                    currentWaitCount = 0;
                    SvFace.post(new Runnable() {
                        @Override
                        public void run() {
                            if (RlAwaitParent.getVisibility() == View.VISIBLE) {
                                RlAwaitParent.setVisibility(View.GONE);
                                objectAnimator.start();
                            }
                        }
                    });
                }
                if (faceNum == 1) {
                    currentCount = 0;
                    //Log.d("AA", "System++:" + System.currentTimeMillis() + "");
                    ArrayList<RecognizedFace> recognitionFaces = mFaceRecognitionManager.recognize(frame,
                            mPreviewWidth, mPreviewHeight, 0);
                    //Log.d("AA", "System++" + System.currentTimeMillis() + "");
                    if (recognitionFaces != null) {
                        for (RecognizedFace recognizedFace : recognitionFaces) {
                            Log.e("oop", "recognizedFace" + recognizedFace.getScore() + "recognizedFace:" + recognizedFace.getName());
                        }
                        RecognizedFace recognizedFace = recognitionFaces.get(0);
                        Log.i("AA", "recognizedFace" + recognizedFace.getScore() + "recognizedFace:" + recognizedFace.getName());
                        if (mPresenter.checkFaceTheshold(recognizedFace.getScore(), THRESHOLD)) {
                            //人脸检测通过currentName
                            //Log.i("AA", "faceRecogTime" + faceRecogTime);
                            //faceRecogTime = 0;
                            if (currentNames.size() > 0) {
                                if (currentNames.contains(recognizedFace.getName())) {
                                } else {
                                    currentNames.clear();
                                    similarityConfirms.clear();
                                }
                                currentNames.add(recognizedFace.getName());
                                SimilarityConfirm similarityConfirm = new SimilarityConfirm();
                                similarityConfirm.setName(recognizedFace.getName());
                                similarityConfirm.setRecognitionFaces(recognitionFaces);
                                similarityConfirms.add(similarityConfirm);
                            } else {
                                currentNames.add(recognizedFace.getName());
                                SimilarityConfirm similarityConfirm = new SimilarityConfirm();
                                similarityConfirm.setName(recognizedFace.getName());
                                similarityConfirm.setRecognitionFaces(recognitionFaces);
                                similarityConfirms.add(similarityConfirm);
                            }
                            if (currentNames.size() == continuous_face) {
                                recordFaceScore(recognizedFace.getName(),recognizedFace.getScore());
                                currentName = recognizedFace.getName();
                                //上传考勤信息
                                int score = (int) recognizedFace.getScore();
                                ArrayList<FaceUser> arrayList = faceRecognition.TwoSimilarity(similarity_grap, similarityConfirms);
                                if (arrayList.size() > 1) {
                                    //
                                    SvFace.post(new Runnable() {
                                        @Override
                                        public void run() {
                                            hideResultView();
                                        }
                                    });
                                    currentCount = 0;
                                    Intent intent = new Intent(ClockResultActivity.this, ChanceFaceActivity.class);
                                    intent.putExtra("face", arrayList);
                                    ClockResultActivity.this.startActivityForResult(intent, 14);
                                } else {
                                    SvFace.post(new Runnable() {
                                        @Override
                                        public void run() {
                                            clearData();
                                            showResultView();
                                        }
                                    });
                                    if (ZzxFaceApplication.commonApplication.isConnectNet) {
                                        //有网直接上传
                                        mPresenter.uploadCheckWork(currentName, androidId, score + "");
                                        //获取本地用户信息
                                        FaceUser faceUser = FaceUser.getFaceUser(currentName);
                                        if (faceUser != null) {
                                            setFaceUserData(faceUser);
                                        }
                                        //mPresenter.getUserInfo(currentName);
                                        mPresenter.getUserCheckWork(currentName);
                                    } else {
                                        //无网先存本地
                                        detectOffline.setClient(androidId);
                                        detectOffline.setSimilarity(score + "");
                                        detectOffline.setUsernumber(currentName);
                                        detectOffline.setIsupload(0);
                                        detectOffline.setChecktime(currentTime);
                                        mPresenter.noNetWorkUploadWork(detectOffline);
                                    }
                                }
                            }
                        } else {
                            //人脸检测不通过
                            //currentNames.clear();
                        }
                    }
                } /*else if (faceNum > 1) {
                //faceRecognition.playVoice(2);
                SvFace.post(new Runnable() {
                    @Override
                    public void run() {
                        ToastUitl.show("只允许一张脸", 0);
                    }
                });
            }*/ else if (faceNum == 0 && !TextUtils.isEmpty(currentName)) {
                    //持续多久跳出结果页面
                    currentCount++;
                    if (MaxCount <= currentCount) {
                        currentName = "";
                        currentNames.clear();
                        similarityConfirms.clear();
                        currentCount = 0;
                    }
                } else {
                    currentCount = 0;
                    if (RlAwaitParent.getVisibility() != View.VISIBLE) {
                        currentWaitCount++;
                        if (currentWaitCount > WAIT_TIME_COUNT) {
                            SvFace.post(new Runnable() {
                                @Override
                                public void run() {
                                    objectAnimator.cancel();
                                    RlAwaitParent.setVisibility(View.VISIBLE);
                                }
                            });
                        }
                    }
                    SvFace.post(new Runnable() {
                        @Override
                        public void run() {
                            hideResultView();
                        }
                    });
                }
            }
        }
        frame=null;
    }


    void swtich(byte[] bytes,int mPreviewWidth,int mPreviewHeight){
        Mat yuv = new Mat(mPreviewHeight + (mPreviewHeight / 2), mPreviewWidth, CvType.CV_8UC1);
        Mat rgb = new Mat(mPreviewHeight + (mPreviewHeight / 2), mPreviewWidth, CvType.CV_8UC3);
        yuv.put(0, 0, bytes);
        Imgproc.cvtColor(yuv, rgb, Imgproc.COLOR_YUV2BGR_NV21, 3);
        String path = Environment.getExternalStorageDirectory().getAbsolutePath() +
                "/Minivision/demo/" + System.currentTimeMillis() + ".jpg";
        Imgcodecs.imwrite(path, rgb);
    }

    StringBuilder stringBuilder=null;
    //设置本地用户信息
    private void setFaceUserData(final FaceUser faceUser){
        stringBuilder=new StringBuilder();
        if(!StringUtils.isStringNull(faceUser.getTitle())){
            stringBuilder.append(faceUser.getTitle());
        }
        if(!StringUtils.isStringNull(faceUser.getPosition())){
            stringBuilder.append("(");
            stringBuilder.append(faceUser.getPosition());
            stringBuilder.append(")");
        }
        TvUsernumber.post(new Runnable() {
            @Override
            public void run() {
                TvUsernumber.setText("工号：" + faceUser.getNumber());
                TvUsername.setText("姓名：" + faceUser.getUserRealName());
                TvUserdept.setText("部门：" + faceUser.getDepartMentName());
                TvUserpost.setText(stringBuilder.toString());
                Tv_userpost_title.setText("职位：");
                BitmapFactory.Options option = new BitmapFactory.Options();
                option.inSampleSize = ApiConstants.inSampleSize;
                option.inDither = false;
                option.inPreferredConfig = Bitmap.Config.ARGB_4444;
                Bitmap bitmap = BitmapFactory.decodeFile(new File(faceRecognition.imageDir, faceUser.getNumber()+".jpg").getAbsolutePath(), option);
                IvUserIcon.setImageBitmap(bitmap);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        startFace=true;
    }

    @Override
    protected void onPause() {
        super.onPause();
        startFace=false;
    }

    @Override
    public void showLoading(String title) {

    }

    @Override
    public void stopLoading() {

    }

    @Override
    public void showErrorTip(String msg) {

    }

    @Override
    public void getSetting(List<SettingBean> settingBeans) {
        for (SettingBean settingBean : settingBeans) {
            if (settingBean.getKey().equals(FaceTimeConfig.SCEN_THRESHOLD)) {
                THRESHOLD = Double.parseDouble(settingBean.getValue());
            } else if (settingBean.getKey().equals(FaceTimeConfig.CONTINUE_FACE_MAXCOUNT)) {
                MaxCount = Integer.parseInt(settingBean.getValue());
            } else if (settingBean.getKey().equals(FaceTimeConfig.FACE_SIZE)) {
                FACE_SIZE = Integer.parseInt(settingBean.getValue());
            } else if (settingBean.getKey().equals(FaceTimeConfig.WAIT_TIME_COUNT)) {
                WAIT_TIME_COUNT = Integer.parseInt(settingBean.getValue());
            }
        }
        faceTimeConfig.addConfig(FaceTimeConfig.lastSettingTime, lastSettingTime + "");
        initFaceFeature();
    }

    @Override
    public void getSettingFail() {
        getCacheFileConfig();
        initFaceFeature();
    }


    private void getCacheFileConfig() {
        if (!TextUtils.isEmpty(faceTimeConfig.getConfig(FaceTimeConfig.SCEN_THRESHOLD))) {
            THRESHOLD = Double.parseDouble(faceTimeConfig.getConfig(FaceTimeConfig.SCEN_THRESHOLD));
        }
        if (!TextUtils.isEmpty(faceTimeConfig.getConfig(FaceTimeConfig.CONTINUE_FACE_MAXCOUNT))) {
            MaxCount = Integer.parseInt(faceTimeConfig.getConfig(FaceTimeConfig.CONTINUE_FACE_MAXCOUNT));
        }
        if (!TextUtils.isEmpty(faceTimeConfig.getConfig(FaceTimeConfig.FACE_SIZE))) {
            FACE_SIZE = Integer.parseInt(faceTimeConfig.getConfig(FaceTimeConfig.FACE_SIZE));
        }
        if (!TextUtils.isEmpty(faceTimeConfig.getConfig(FaceTimeConfig.WAIT_TIME_COUNT))) {
            WAIT_TIME_COUNT = Integer.parseInt(faceTimeConfig.getConfig(FaceTimeConfig.WAIT_TIME_COUNT));
        }

    }

    long lastSettingTime = 0;

    @Override
    public void getLastSetting(Long lastSettingTime) {
        this.lastSettingTime = lastSettingTime;
        String localtime = faceTimeConfig.getConfig(FaceTimeConfig.lastSettingTime);
        if (TextUtils.isEmpty(localtime)) {
            localtime = "0";
        }
        if (lastSettingTime > Long.parseLong(localtime)) {
            mPresenter.getSetting(faceTimeConfig);
        } else {
            getCacheFileConfig();
            initFaceFeature();
        }
    }

    @Override
    public void getLastSettingFail() {
            getCacheFileConfig();
            initFaceFeature();
    }

    @Override
    public void getUserCheckWork(CheckWorkBean checkWorkBean) {
        //获取当前本月日历信息
        if (checkWorkBean != null) {
            dateBeens = getMonthDate(year, month);
            mPresenter.getDataBeanCalendar(dateBeens, checkWorkBean);
            TvWeekNormal.setText("打卡：" + checkWorkBean.getNormal());
            TvWeekTardiness.setText("迟到：" + checkWorkBean.getTardiness());
            TvWeekLeavingerly.setText("早退：" + checkWorkBean.getLeavingEarly());
            TvWeekLost.setText("缺失：" + checkWorkBean.getLost());
        }
    }

    @Override
    public void uploadCheckWorkResult(CheckResult checkResult) {
        //打卡结果
        //showResultView();
        if (checkResult.isSucessful()) {
            //打卡成功
            IvCheckworkPic.setImageDrawable(ZzxFaceApplication.getAppResources().getDrawable(R.drawable.success));
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm");
            String time = simpleDateFormat.format(new Date(checkResult.getEditTime()));
            TvCheckworkDescribe.setText(checkResult.getMsg());
            TvCheckworkTime.setText(time);
        } else {
            //打卡失败
            IvCheckworkPic.setImageDrawable(ZzxFaceApplication.getAppResources().getDrawable(R.drawable.wrong));
            TvCheckworkTime.setText(checkResult.getMsg());
            TvCheckworkDescribe.setText("正在重新识别");
            currentName = "";
            currentNames.clear();
            similarityConfirms.clear();
        }
        audioUtils.playTTS(checkResult.getMsg());
    }


    @Override
    public void getUserInfo(User user) {
        //获取员工信息
        if (user != null) {
            TvUsernumber.setText("工号：" + user.getUserNumber());
            TvUsername.setText("姓名：" + user.getRealname());
            TvUserdept.setText("部门：" + user.getDepartment().getName());
            TvUserpost.setText("职位:：" + user.getTitle().getTitle());
            BitmapFactory.Options option = new BitmapFactory.Options();
            option.inSampleSize = ApiConstants.inSampleSize;
            option.inDither = false;
            option.inPreferredConfig = Bitmap.Config.ARGB_4444;
            Bitmap bitmap = BitmapFactory.decodeFile(new File(faceRecognition.imageDir, user.getPath()).getAbsolutePath(), option);
            IvUserIcon.setImageBitmap(bitmap);
            /*Glide.with(this).load(new File(faceRecognition.imageDir, user.getPath())).crossFade().skipMemoryCache(true)
                    .diskCacheStrategy(DiskCacheStrategy.NONE).into(IvUserIcon);*/
        }
    }

    //绑定日历数据
    @Override
    public void getDataBeanCalendar(List<DateBean> dateBeanList) {
        currentDateBean.clear();
        currentDateBean.addAll(dateBeanList);
        RvClockResult.setLayoutManager(layoutManager);
        if (clockResultAdapter == null) {
            clockResultAdapter = new ClockResultAdapter(currentDateBean, ClockResultActivity.this, width, day);
            RvClockResult.setAdapter(clockResultAdapter);
        } else {
            clockResultAdapter.notifyDataSetChanged();
        }
    }

    //获取服务器时间
    @Override
    public void getServerTime(Long now) {
        mPresenter.getCurrentTime(now);
    }

    @Override
    public void getCurrentTime(final String minutes, final String day, final long now1) {
        currentTime=0;
        currentTime = now1;
        Tv_current_time_minute.post(new Runnable() {
            @Override
            public void run() {
                Tv_current_time_minute.setText(minutes + "/");
                Tv_current_time_day.setText(day);
                TvTimeMinute.setText(minutes);
                TvTimeDate.setText(day + "\r\r" + SolarUtil.getMillisecondToWeek(now1));
            }
        });
    }

    //获取考勤信息失败
    @Override
    public void getUserCheckWorkFail() {
        currentDateBean.clear();
        currentDateBean.addAll(CalendarUtil.getMonthDate(year, month));
        RvClockResult.setLayoutManager(layoutManager);
        if (clockResultAdapter == null) {
            clockResultAdapter = new ClockResultAdapter(currentDateBean, ClockResultActivity.this, width, day);
            RvClockResult.setAdapter(clockResultAdapter);
        } else {
            clockResultAdapter.notifyDataSetChanged();
        }
    }

    //打卡失败
    @Override
    public void uploadCheckWorkResultFail() {
        showResultView();
        IvCheckworkPic.setImageDrawable(ZzxFaceApplication.getAppResources().getDrawable(R.drawable.wrong));
        TvCheckworkTime.setText("打卡失败");
        TvCheckworkDescribe.setText("正在重新识别");
        faceRecognition.playVoice(0);
        currentName = "";
        currentNames.clear();
        similarityConfirms.clear();
    }

    @Override
    public void getServerTimeFail() {
        mPresenter.getCurrentTime(System.currentTimeMillis());
    }

    @Override
    public void initFaceSuccess() {

    }

    @Override
    public void getUserSync(FileBean fileBean, String lastSettingTime) {
        Intent intent = new Intent(ClockResultActivity.this, DownFileService.class);
        intent.putExtra("databean", (Serializable) fileBean.getData());
        ClockResultActivity.this.startService(intent);
        Log.e("getUserSync","getUserSync");
    }

    long needTime = 0;

    @Override
    public void getNeedTime(Long needTime) {
        this.needTime = needTime;
        String locaktime = faceTimeConfig.getConfig(needSync);
        if (TextUtils.isEmpty(locaktime)) {
            locaktime = "0";
        }
        Log.e("LQW", "lastSettingTime+" + needTime + "androidId+" + androidId);
        long localTime = Long.parseLong(locaktime);
        if (needTime > localTime) {
            mPresenter.getUserSync(androidId, localTime + "", needTime + "");
        } else {
            showCamera();
        }
    }


    private void showCamera() {
        if (!isTimer) {
            Tv_initface_tip.setVisibility(View.GONE);
            SvFace.postDelayed(new Runnable() {
                @Override
                public void run() {
                    Ll_screen_parent.setVisibility(View.VISIBLE);
                    startAnimationSet();
                    clearResetImageView();
                    Iv_pagebg.setImageDrawable(getResources().getDrawable(R.drawable.background));
                }
            }, 1000);
        }
        isTimer = false;
    }

    @Override
    public void getNeedTimeFail() {
        showCamera();
    }

    boolean isTimer = false;

    @Override
    public void timerGetUserSync() {
        isTimer = true;
        mPresenter.getLastSettingTime();
    }

    @Override
    public void noNetWorkUploadWorkSuccess(final DetectOffline detectOffline) {
        final FaceUser faceUser=FaceUser.getFaceUser(detectOffline.getUsernumber());
        stringBuilder=new StringBuilder();
        if(faceUser==null){
            return;
        }
        if(!StringUtils.isStringNull(faceUser.getTitle())){
            stringBuilder.append(faceUser.getTitle());
    }
        if(!StringUtils.isStringNull(faceUser.getPosition())){
            stringBuilder.append("(");
            stringBuilder.append(faceUser.getPosition());
            stringBuilder.append(")");
        }
        SvFace.post(new Runnable() {
            @Override
            public void run() {
                TvUsernumber.setText("工号：" + faceUser.getNumber());
                TvUsername.setText("姓名：" + faceUser.getUserRealName());
                TvUserdept.setText("部门：" + faceUser.getDepartMentName());
                TvUserpost.setText(stringBuilder.toString());
                Tv_userpost_title.setText("职位：");
                IvCheckworkPic.setImageDrawable(ZzxFaceApplication.getAppResources().getDrawable(R.drawable.success));
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm");
                String time = simpleDateFormat.format(new Date(detectOffline.getChecktime()));
                TvCheckworkDescribe.setText("打卡成功!");
                TvCheckworkTime.setText(time);
                audioUtils.playTTS("打卡成功!");
                BitmapFactory.Options option = new BitmapFactory.Options();
                option.inSampleSize = ApiConstants.inSampleSize;
                option.inDither = false;
                option.inPreferredConfig = Bitmap.Config.ARGB_4444;
                File file = new File(faceRecognition.imageDir, detectOffline.getUsernumber() + ".jpg");
                Bitmap bitmap = BitmapFactory.decodeFile(file.getAbsolutePath(), option);
                IvUserIcon.setImageBitmap(bitmap);
            }
        });
    }

    @Override
    public void noNetWorkUploadWorkFail(final DetectOffline detectOffline) {
        //打卡失败
       final FaceUser faceUser=FaceUser.getFaceUser(detectOffline.getUsernumber());
        /*currentDateBean.clear();
        currentDateBean.addAll(CalendarUtil.getMonthDate(year, month));*/
        stringBuilder=new StringBuilder();
        if(!StringUtils.isStringNull(faceUser.getTitle())){
            stringBuilder.append(faceUser.getTitle());
        }
        if(!StringUtils.isStringNull(faceUser.getPosition())){
            stringBuilder.append("(");
            stringBuilder.append(faceUser.getPosition());
            stringBuilder.append(")");
        }
        SvFace.post(new Runnable() {
            @Override
            public void run() {
            /*    RvClockResult.setLayoutManager(layoutManager);
                if (clockResultAdapter == null) {
                    clockResultAdapter = new ClockResultAdapter(currentDateBean, ClockResultActivity.this, width, day);
                    RvClockResult.setAdapter(clockResultAdapter);
                } else {
                    clockResultAdapter.notifyDataSetChanged();
                }*/
                TvUsernumber.setText("工号：" + faceUser.getNumber());
                TvUsername.setText("姓名：" + faceUser.getUserRealName());
                TvUserdept.setText("部门：" + faceUser.getDepartMentName());
                TvUserpost.setText(stringBuilder.toString());
                Tv_userpost_title.setText("职位：");
                IvCheckworkPic.setImageDrawable(ZzxFaceApplication.getAppResources().getDrawable(R.drawable.wrong));
                TvCheckworkTime.setText("打卡失败!");
                TvCheckworkDescribe.setText("正在重新识别");
                audioUtils.playTTS("打卡失败!");
                currentName = "";
                currentNames.clear();
                similarityConfirms.clear();
                BitmapFactory.Options option = new BitmapFactory.Options();
                option.inSampleSize =ApiConstants.inSampleSize;
                option.inDither = false;
                option.inPreferredConfig = Bitmap.Config.ARGB_4444;
                File file = new File(faceRecognition.imageDir, detectOffline.getUsernumber() + ".jpg");
                Bitmap bitmap = BitmapFactory.decodeFile(file.getAbsolutePath(), option);
                IvUserIcon.setImageBitmap(bitmap);
            }
        });
    }

    @Override
    public void checkIsNoNetWork(boolean isNetWork) {
        Log.e("isNetWork",isNetWork+"+isNetWork");
        if(!ZzxFaceApplication.commonApplication.isConnectNet&&isNetWork){
            mPresenter.uploadNoNetWorkRecord();
        }
        ZzxFaceApplication.commonApplication.isConnectNet=isNetWork;
        if(isNetWork){
            if(Tv_nonet_tip.getVisibility()==View.VISIBLE) {
                Tv_nonet_tip.setVisibility(View.GONE);
                RvClockResult.setVisibility(View.VISIBLE);
                LlWeekParent.setVisibility(View.VISIBLE);
            }
        }else{
            if(Tv_nonet_tip.getVisibility()==View.GONE)
            Tv_nonet_tip.setVisibility(View.VISIBLE);
            RvClockResult.setVisibility(View.GONE);
            LlWeekParent.setVisibility(View.GONE);
        }
    }

    @Override
    public void timerReStart() {
        Intent i = getBaseContext().getPackageManager()
                .getLaunchIntentForPackage(getBaseContext().getPackageName());
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(i);
    }


    //清除Imageview，重新给予背景
    private void clearResetImageView() {
        Bitmap bmp = Iv_pagebg.getDrawingCache();
            if (null != bmp && !bmp.isRecycled()) {
                bmp = null;
        }
        Iv_pagebg.setImageDrawable(null);
        Iv_pagebg.refreshDrawableState();
    }

    private void showResultView() {
        clearResetImageView();
        Ll_screen_parent.setAlpha(0f);
        Ll_checkwork_parent.setVisibility(View.VISIBLE);
        Iv_pagebg.setImageDrawable(getResources().getDrawable(R.drawable.b2background));
    }

    private void hideResultView() {
        clearResetImageView();
        if (Ll_checkwork_parent.getVisibility() == View.VISIBLE) {
            Ll_screen_parent.setAlpha(1f);
            Ll_checkwork_parent.setVisibility(View.GONE);
            Iv_pagebg.setImageDrawable(getResources().getDrawable(R.drawable.background));
            clearData();
        }
    }

    ObjectAnimator objectAnimator=null;
    public void startAnimationSet() {
        ViewTreeObserver vto = Iv_screen_img.getViewTreeObserver();
        vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                Iv_screen_img.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                int height = Iv_screen_img.getHeight();
                objectAnimator = ObjectAnimator.ofFloat(Iv_screen_img, "translationY", -height, 0);
                objectAnimator.setDuration(1500);
                objectAnimator.setRepeatCount(-1);
                objectAnimator.setRepeatMode(ValueAnimator.RESTART);
                objectAnimator.start();
            }
        });
        Iv_screen_img.setAlpha(0.45f);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (downSuccessReciver != null)
            unregisterReceiver(downSuccessReciver);
        if(objectAnimator!=null)
            objectAnimator.cancel();
    }

    private void clearData() {
        currentDateBean.clear();
        RvClockResult.setLayoutManager(layoutManager);
        if (clockResultAdapter == null) {
            clockResultAdapter = new ClockResultAdapter(currentDateBean, ClockResultActivity.this, width, day);
            RvClockResult.setAdapter(clockResultAdapter);
        } else {
            clockResultAdapter.notifyDataSetChanged();
        }
        TvUsernumber.setText("");
        TvUsername.setText("");
        TvUserdept.setText("");
        TvUserpost.setText("");
        Tv_userpost_title.setText("");
        TvWeekTip.setText("");
        TvWeekLeavingerly.setText("");
        TvWeekTardiness.setText("");
        TvWeekLost.setText("");
        TvWeekNormal.setText("");
        IvUserIcon.setImageBitmap(null);
        IvUserIcon.refreshDrawableState();
    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode==14){
            if(data!=null){
                FaceUser faceUser1= (FaceUser) data.getSerializableExtra("face");
                SvFace.post(new Runnable() {
                    @Override
                    public void run() {
                        clearData();
                        showResultView();
                    }
                });
                if(!currentName.equals(faceUser1.getNumber())){
                    currentName=faceUser1.getNumber();
                    currentNames.clear();
                    currentNames.add(currentName);
                    currentNames.add(currentName);
                    similarityConfirms.clear();
                }
                if (ZzxFaceApplication.commonApplication.isConnectNet) {
                    //有网直接上传
                    mPresenter.uploadCheckWork(currentName, androidId,(int)faceUser1.getScore() + "");
                    //获取本地用户信息
                    FaceUser faceUser=FaceUser.getFaceUser(faceUser1.getNumber());
                    if(faceUser!=null){
                        setFaceUserData(faceUser);
                    }
                    //mPresenter.getUserInfo(currentName);
                    mPresenter.getUserCheckWork(faceUser1.getNumber());
                } else {
                    //无网先存本地
                    detectOffline.setClient(androidId);
                    detectOffline.setSimilarity((int)faceUser1.getScore() + "");
                    detectOffline.setUsernumber(currentName);
                    detectOffline.setIsupload(0);
                    detectOffline.setChecktime(currentTime);
                    mPresenter.noNetWorkUploadWork(detectOffline);
                }
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
    private void recordFaceScore(String facename,double faceScore) {
        if (faceScoreLog != null) {
            StringBuilder stringBuilder = new StringBuilder();
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            stringBuilder.append("face_number:");
            stringBuilder.append(facename);
            stringBuilder.append("faceScore:");
            stringBuilder.append(faceScore);
            faceScoreLog.addConfig(simpleDateFormat.format(new Date()),stringBuilder.toString());
        }
    }
}
