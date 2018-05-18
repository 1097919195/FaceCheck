package com.jaydenxiao.androidfire.ui.ClockResult.contract;

import com.jaydenxiao.androidfire.bean.CheckResult;
import com.jaydenxiao.androidfire.bean.CheckWorkBean;
import com.jaydenxiao.androidfire.bean.DetectOffline;
import com.jaydenxiao.androidfire.bean.FileBean;
import com.jaydenxiao.androidfire.bean.SettingBean;
import com.jaydenxiao.androidfire.bean.User;
import com.jaydenxiao.androidfire.ui.ClockResult.config.FaceTimeConfig;
import com.jaydenxiao.androidfire.utils.calendarutil.DateBean;
import com.jaydenxiao.common.base.BaseModel;
import com.jaydenxiao.common.base.BasePresenter;
import com.jaydenxiao.common.base.BaseView;

import java.util.List;

import rx.Observable;

/**
 * Created by xtt on 2017/8/25.
 */

public interface FaceContract {

    public interface Model extends BaseModel{
        /**
         *
         * @param
         * @return
         */
        Observable<FileBean.DataBean> downPictrue(List<FileBean.DataBean> dataBeen);

        Observable<List<SettingBean>> getSetting();

        Observable<Long> getLastSettingTime();

        Observable<Long> getNeedTime();

        Observable<User> getUserInfo(String number);

        Observable<CheckWorkBean> getUserCheckWork(String number);

        Observable<CheckResult> uploadCheckWork(String number, String clientNumber, String similarity);

        void getDataBeanCalendar(List<DateBean> dateBeanList,CheckWorkBean checkWorkBean,CalendarDataListener calendarDataListener);

        Observable<Long> getServerTime();

        Observable<Long> getCurrentTime(Long now);

        Observable<String> getCurrentTime();

        Observable<FileBean> getUserSync(String clientNumber,String start,String end);

        //定时获取需要同步的图片数据
        Observable timerGetUserSync();

        Observable<String> uploadNoNetWorkRecord();

        Observable<Boolean> checkIsNoNetWork();

        Observable timerToDeleteLog();

        Observable timerReStart();

    }

    public interface View extends BaseView{
        void getSetting(List<SettingBean> settingBeans);
        void getSettingFail();
        void getLastSetting(Long lastSettingTime);
        void getLastSettingFail();
        void getUserCheckWork(CheckWorkBean checkWorkBean);
        void uploadCheckWorkResult(CheckResult checkResult);
        void getUserInfo(User user);
        void getDataBeanCalendar(List<DateBean> dateBeans);
        void getServerTime(Long now);
        void getCurrentTime(String minutes,String day,long now1);

        //获取考勤失败
        void getUserCheckWorkFail();
        //打卡失败
        void uploadCheckWorkResultFail();
        //获取服务器时间失败
        void getServerTimeFail();

        void initFaceSuccess();

        //返回需要下载的图片集合
        void getUserSync(FileBean fileBean,String lastSettingTime);

        void getNeedTime(Long needTime);

        void getNeedTimeFail();
        //定时去取最新配置信息
        void timerGetUserSync();

        void noNetWorkUploadWorkSuccess(DetectOffline detectOffline);

        void noNetWorkUploadWorkFail(DetectOffline detectOffline);

        void checkIsNoNetWork(boolean isNetWork);

        void timerReStart();


    }

    abstract static class Presenter extends BasePresenter<View,Model>{
        public abstract void downPictrue(FileBean fileBean);
        public abstract void getSetting(FaceTimeConfig faceTimeConfig);

        public abstract void getLastSettingTime();

        public abstract  void getUserInfo(String number);

        public abstract void getUserCheckWork(String number);

        public abstract boolean checkFaceTheshold(double FaceScore,double theShold);

        public abstract void uploadCheckWork(String number,String clientNumber,String similarity);

        public abstract void getDataBeanCalendar(List<DateBean> dateBeanList,CheckWorkBean checkWorkBean);

        public abstract  void getServerTime();

        public abstract void getCurrentTime(Long now);

        public abstract void  getUserSync(String clientNumber,String start,String end);

        public abstract void timerGetUserSync();

        public abstract  void getNeedSync();

        //离线保存打卡记录
        public abstract  void noNetWorkUploadWork(DetectOffline detectOffline);

        public abstract  void uploadNoNetWorkRecord();

        //每次5分钟去检测有无网络
        public abstract void checkIsNoNetWork();

        //定时删除日志
        public abstract void TimeToDeleteLog();

        public abstract void timerReStart();

    }


    public interface CalendarDataListener{
        void getDataBeanCalendar(List<DateBean> dateBeanList);
    }


}
