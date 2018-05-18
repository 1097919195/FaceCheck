package com.jaydenxiao.androidfire.bean;

import okhttp3.ResponseBody;

/**
 * Created by xtt on 2017/9/4.
 */

public class ResponseBodyBean {
    private String filepath;
    private ResponseBody responseBody;

    public String getFilepath() {
        return filepath;
    }

    public void setFilepath(String filepath) {
        this.filepath = filepath;
    }

    public ResponseBody getResponseBody() {
        return responseBody;
    }

    public void setResponseBody(ResponseBody responseBody) {
        this.responseBody = responseBody;
    }
}
