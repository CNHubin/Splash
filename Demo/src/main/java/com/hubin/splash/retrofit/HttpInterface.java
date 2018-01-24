package com.hubin.splash.retrofit;

/*
 *  @项目名：  TaiyaoVerfication 
 *  @包名：    com.taiyao.taiyaoverfication
 *  @文件名:   HttpInterface
 *  @创建者:   胡英姿
 *  @创建时间:  2017-11-30 18:00
 *  @描述：    TODO
 */


import com.hubin.splash.bean.HttpResponse;

import java.util.Map;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.QueryMap;

public interface HttpInterface<T> {
    //get请求带多个不确定参数
    @GET("taiyaofile/version")
    Call<HttpResponse> getVersionInfo(@QueryMap Map<String, String> map);


}
