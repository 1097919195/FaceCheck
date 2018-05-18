package com.jaydenxiao.androidfire.ui.ClockResult.presenter;

import android.content.Context;
import android.net.wifi.WifiManager;
import android.util.Log;

import com.jaydenxiao.androidfire.bean.CheckResult;
import com.jaydenxiao.androidfire.bean.CheckWorkBean;
import com.jaydenxiao.androidfire.bean.DetectOffline;
import com.jaydenxiao.androidfire.bean.FileBean;
import com.jaydenxiao.androidfire.bean.SettingBean;
import com.jaydenxiao.androidfire.bean.User;
import com.jaydenxiao.androidfire.ui.ClockResult.config.FaceTimeConfig;
import com.jaydenxiao.androidfire.ui.ClockResult.contract.FaceContract;
import com.jaydenxiao.androidfire.ui.face.ZzxFaceApplication;
import com.jaydenxiao.androidfire.utils.calendarutil.DateBean;
import com.jaydenxiao.common.baserx.RxSubscriber;

import org.xutils.ex.DbException;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * Created by xtt on 2017/8/25.
 */

public class FacePresenter  extends FaceContract.Presenter{
    @Override
    public void downPictrue(FileBean fileBean) {
        mRxManage.add(mModel.downPictrue(fileBean.getData()).subscribe(new RxSubscriber<FileBean.DataBean>(mContext,false) {
                @Override
                protected void _onNext(FileBean.DataBean s) {

                }

                @Override
                protected void _onError(String message) {

                }
            })
        );
    }


    @Override
    public void getSetting(final FaceTimeConfig faceTimeConfig) {
        mRxManage.add(mModel.getSetting().subscribe(new RxSubscriber<List<SettingBean>>(mContext,false) {
            @Override
            protected void _onNext(List<SettingBean> settingBeans) {
                for(SettingBean settingBean:settingBeans){
                    if(settingBean.getKey().equals(FaceTimeConfig.SCEN_THRESHOLD)){
                        faceTimeConfig.addConfig(FaceTimeConfig.SCEN_THRESHOLD,settingBean.getValue());
                    }else if(settingBean.getKey().equals(FaceTimeConfig.CONTINUE_FACE_MAXCOUNT)){
                        faceTimeConfig.addConfig(FaceTimeConfig.CONTINUE_FACE_MAXCOUNT,settingBean.getValue());
                    }else if(settingBean.getKey().equals(FaceTimeConfig.FACE_SIZE)){
                        faceTimeConfig.addConfig(FaceTimeConfig.FACE_SIZE,settingBean.getValue());
                    }else if(settingBean.getKey().equals(FaceTimeConfig.WAIT_TIME_COUNT)){
                        faceTimeConfig.addConfig(FaceTimeConfig.WAIT_TIME_COUNT,settingBean.getValue());
                    }
                }
                mView.getSetting(settingBeans);

            }
            @Override
            protected void _onError(String message) {
                    mView.getSettingFail();
            }
        }));
    }

    @Override
    public void getLastSettingTime() {
        mRxManage.add(mModel.getLastSettingTime().subscribe(new RxSubscriber<Long>(mContext,false) {
            @Override
            protected void _onNext(Long lastSettingTime) {
                mView.getLastSetting(lastSettingTime);
            }

            @Override
            protected void _onError(String message) {
                mView. getSettingFail();
            }
        }));
    }

    @Override
    public void getUserInfo(String number) {
        mRxManage.add(mModel.getUserInfo(number).subscribe(new RxSubscriber<User>(mContext,false) {
            @Override
            protected void _onNext(User user) {
                    mView.getUserInfo(user);
            }

            @Override
            protected void _onError(String message) {

            }
        }));
    }

    @Override
    public void getUserCheckWork(String number) {
        mRxManage.add(mModel.getUserCheckWork(number).subscribe(new RxSubscriber<CheckWorkBean>(mContext,false) {
            @Override
            protected void _onNext(CheckWorkBean checkWorkBean) {
                mView.getUserCheckWork(checkWorkBean);
            }

            @Override
            protected void _onError(String message) {
                mView.getUserCheckWorkFail();
            }
        }));
    }

    @Override
    public boolean checkFaceTheshold(double faceScore,double theShold) {
        BigDecimal data1 = new BigDecimal(faceScore);
        BigDecimal data2 = new BigDecimal(theShold);
        int code=data1.compareTo(data2);
        if(code>=0){
            return true ;
        }
        return false;
    }

    @Override
    public void uploadCheckWork(String number, String clientNumber, String similarity) {
            mRxManage.add(mModel.uploadCheckWork(number, clientNumber, similarity).subscribe(new RxSubscriber<CheckResult>(mContext,false) {
                @Override
                protected void _onNext(CheckResult checkResult) {
                    mView.uploadCheckWorkResult(checkResult);
                }

                @Override
                protected void _onError(String message) {
                    mView.uploadCheckWorkResultFail();
                }
            }));
    }

    @Override
    public void getDataBeanCalendar(List<DateBean> dateBeanList, CheckWorkBean checkWorkBean) {
        mModel.getDataBeanCalendar(dateBeanList, checkWorkBean, new FaceContract.CalendarDataListener() {
            @Override
            public void getDataBeanCalendar(List<DateBean> dateBeanList) {
            mView.getDataBeanCalendar(dateBeanList);
            }
        });
    }

    @Override
    public void getServerTime() {
        mRxManage.add(mModel.getServerTime().subscribe(new RxSubscriber<Long>(mContext,false) {
            @Override
            protected void _onNext(Long now) {
                mView.getServerTime(now);
            }

            @Override
            protected void _onError(String message) {
                    mView.getServerTimeFail();
            }
        }));
    }

    @Override
    public void getCurrentTime(Long now) {
        mRxManage.add(mModel.getCurrentTime(now).subscribe(new RxSubscriber<Long>(mContext,false) {
            @Override
            protected void _onNext(Long  now1) {
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MM-dd HH:mm:ss");
                String time = simpleDateFormat.format(new Date(now1));
                String[] times = time.split(" ");
                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append(times[1]);
                stringBuilder.append("#");
                stringBuilder.append(times[0].split("-")[0]);
                stringBuilder.append("月");
                stringBuilder.append(times[0].split("-")[1]);
                stringBuilder.append("日");
                String timeString=stringBuilder.toString();
                mView.getCurrentTime(stringBuilder.toString().split("#")[0],timeString.split("#")[1],now1);
            }

            @Override
            protected void _onError(String message) {

            }
        }));
    }

    @Override
    public void getUserSync(String clientNumber, String start, final String end) {
        Log.e("getUserSync","getUserSync");
        mRxManage.add(mModel.getUserSync(clientNumber,start,end).subscribe(new RxSubscriber<FileBean>(mContext,false) {
            @Override
            protected void _onNext(FileBean fileBean) {
                    //downPictrue(fileBean);
                    mView.getUserSync(fileBean,end);
            }

            @Override
            protected void _onError(String message) {

            }
        }));
    }

    @Override
    public void timerGetUserSync() {
        mRxManage.add(mModel.timerGetUserSync().subscribe(new RxSubscriber(mContext,false) {
            @Override
            protected void _onNext(Object o) {
                mView.timerGetUserSync();
                //uploadNoNetWorkRecord();
            }

            @Override
            protected void _onError(String message) {

            }
        }));
    }

    @Override
    public void getNeedSync() {
        mRxManage.add(mModel.getNeedTime().subscribe(new RxSubscriber<Long>(mContext,false) {
            @Override
            protected void _onNext(Long time) {
                    mView.getNeedTime(time);
            }

            @Override
            protected void _onError(String message) {
                mView.getNeedTimeFail();
            }
        }));
    }

    @Override
    public void noNetWorkUploadWork(DetectOffline detectOffline) {
        try {
            ZzxFaceApplication.commonApplication.db.save(detectOffline);
            mView.noNetWorkUploadWorkSuccess(detectOffline);
        } catch (DbException e) {
            mView.noNetWorkUploadWorkFail(detectOffline);
            e.printStackTrace();
        }
    }

    @Override
    public void uploadNoNetWorkRecord() {
        mRxManage.add(mModel.uploadNoNetWorkRecord().subscribe(new RxSubscriber<String>(mContext,false) {
            @Override
            protected void _onNext(String s) {

            }

            @Override
            protected void _onError(String message) {

            }
        }));
    }

    @Override
    public void checkIsNoNetWork() {
            mRxManage.add(mModel.checkIsNoNetWork().subscribe(new RxSubscriber<Boolean>(mContext,false) {
                @Override
                protected void _onNext(Boolean aBoolean) {
                    mView.checkIsNoNetWork(aBoolean);
                }
                @Override
                protected void _onError(String message) {
                    mView.checkIsNoNetWork(false);
                }
            }));
    }

    @Override
    public void TimeToDeleteLog() {
        mRxManage.add(mModel.timerToDeleteLog().subscribe(new RxSubscriber(mContext,false) {
            @Override
            protected void _onNext(Object o) {

            }

            @Override
            protected void _onError(String message) {

            }
        }));
    }

    @Override
    public void timerReStart() {
        mRxManage.add(mModel.timerReStart().subscribe(new RxSubscriber(mContext,false) {
            @Override
            protected void _onNext(Object o) {
                mView.timerReStart();
            }

            @Override
            protected void _onError(String message) {

            }
        }));
    }

    WifiManager mWm=null;
    public void setWifi(boolean isEnable,Context conetxt) {
        if (mWm == null) {
            mWm = (WifiManager) conetxt.getSystemService(Context.WIFI_SERVICE);
        }
        if (isEnable) {// 开启wifi
            if (!mWm.isWifiEnabled()) {
                mWm.setWifiEnabled(true);
            }
        } else {
            // 关闭 wifi
            if (mWm.isWifiEnabled()) {
                mWm.setWifiEnabled(false);
            }
        }

    }
}
