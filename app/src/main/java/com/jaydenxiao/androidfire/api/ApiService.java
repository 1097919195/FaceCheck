package com.jaydenxiao.androidfire.api;

import com.jaydenxiao.androidfire.bean.CheckWorkBean;
import com.jaydenxiao.androidfire.bean.FileBean;
import com.jaydenxiao.androidfire.bean.SettingBean;
import com.jaydenxiao.androidfire.bean.User;

import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;
import retrofit2.http.Streaming;
import rx.Observable;

/**
 * des:ApiService
 * Created by xsf
 * on 2016.06.15:47
 */
public interface ApiService {

    /*@GET("login")
    Observable<BaseRespose<User>> login(@Query("username") String username, @Query("password") String password);

    @GET("nc/article/{postId}/full.html")
    Observable<Map<String, NewsDetail>> getNewDetail(
            @Header("Cache-Control") String cacheControl,
            @Path("postId") String postId);

    @GET("nc/article/{type}/{id}/{startPage}-20.html")
    Observable<Map<String, List<NewsSummary>>> getNewsList(
            @Header("Cache-Control") String cacheControl,
            @Path("type") String type, @Path("id") String id,
            @Path("startPage") int startPage);

    @GET
    Observable<ResponseBody> getNewsBodyHtmlPhoto(
            @Header("Cache-Control") String cacheControl,
            @Url String photoPath);
    //@Url，它允许我们直接传入一个请求的URL。这样以来我们可以将上一个请求的获得的url直接传入进来，baseUrl将被无视
    // baseUrl 需要符合标准，为空、""、或不合法将会报错

    @GET("data/福利/{size}/{page}")
    Observable<GirlData> getPhotoList(
            @Header("Cache-Control") String cacheControl,
            @Path("size") int size,
            @Path("page") int page);

    @GET("nc/video/list/{type}/n/{startPage}-10.html")
    Observable<Map<String, List<VideoData>>> getVideoList(
            @Header("Cache-Control") String cacheControl,
            @Path("type") String type,
            @Path("startPage") int startPage);*/

    //下载文件
    @Streaming
    @GET("download/{number}")
    Observable<ResponseBody> downPictrue(@Path("number") String number);

    @Streaming
    @GET("download/{number}")
    Call<ResponseBody> downPictrueService(@Path("number") String number);

    //获得所有配置信息
    @GET("settings")
    Observable<List<SettingBean>> getSetting();

    //获得最新配置的时间
    @GET("settings/lastSettingTime")
    Observable<ResponseBody>  getLastSettingTime();

    //获取服务器时间
    @GET("settings/syncTime")
    Observable<ResponseBody> getSyncTime();

    //获取服务器时间
    @GET("settings/syncTime")
    Call<ResponseBody> getSyncTimeCheckNetWork();

    //获取用户图片配置时间
    @GET("user/needSync")
    Observable<ResponseBody> getNeedSyncTime();

    //获取用户工号为number的信息。
    @GET("user/{number}")
    Observable<User> getUserByNumber(@Path("number") String number);

    //获得某个用户至昨日的统计情况。
    //测试阶段有stub，因为没有数据库统计支持。发布时将去掉stub
    @GET("attendance/month")
    Observable<CheckWorkBean> getUserCount(@Query("user") String number);

    /**
     *上传考勤信息
     * @param number
     * @param clientNumber 客户端唯一标识码
     * @param similarity 相似度
     * @return
     */
    @FormUrlEncoded
    @POST("attendance/detect")
    Observable<ResponseBody> uploadCheckWork(@Field("user") String number, @Field("client") String clientNumber, @Field("similarity") String similarity);


    @GET("user/sync")
    Observable<FileBean> getUserSync(@Query("client") String clientNumber, @Query("start") String startTime, @Query("end") String end);

    /**
     * 上传离线打卡信息
     */
    @FormUrlEncoded
    @POST("attendance/detectOffline")
    Call<ResponseBody> uploadNoNetCheckWork(@Field("user") String number, @Field("client") String clientNumber, @Field("similarity") String similarity,@Field("timestamp") String timestamp);

    /**
     * 上传用户的特征码
     */
    @FormUrlEncoded
    @POST("user/feature/{number}")
    Call<ResponseBody> uploadUserFeatrue(@Path("number") String number,@Field("feature") String feature);


}
