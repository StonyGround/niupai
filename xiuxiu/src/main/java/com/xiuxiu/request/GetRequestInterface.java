package com.xiuxiu.request;

import com.xiuxiu.model.ThemeInfo;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;

/**
 * Created by hzdykj on 2017/7/3.
 */

public interface GetRequestInterface {
    @GET("/restheme.xml")
    Call<ThemeInfo> getCall();
    // 注解里传入 网络请求 的部分URL地址
    // getCall()是接受网络请求数据的方法
}
