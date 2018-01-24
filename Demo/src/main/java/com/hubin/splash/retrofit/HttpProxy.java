package com.hubin.splash.retrofit;

/*
 *  @项目名：  TaiyaoVerify 
 *  @包名：    com.taiyao.taiyaoverify.retrofit
 *  @文件名:   HttpProxy
 *  @创建者:   胡英姿
 *  @创建时间:  2018-01-05 10:12
 *  @描述：    网络请求代理类
 */

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.HashMap;

import hubin.splash.BuildConfig;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class HttpProxy<T> {

    private HttpListener mHttpListener;
    private String URL_BASE;

    public HttpProxy(String URL_BASE) {
        this.URL_BASE = URL_BASE;
    }

    /**
     * 创建 Retrofit接口服务 默认basa url
     * @return HttpInterface
     */
    private HttpInterface createApiService() {
        return createApiService(URL_BASE);
    }

    /**
     * 创建 Retrofit接口服务 其它Url
     * @param basrUrl
     * @return
     */
    private HttpInterface createApiService(String basrUrl) {
        return initRetrofit(basrUrl).create(HttpInterface.class);
    }

    /**
     * 初始化Retrofit 添加了拦截器
     * @param basrUrl
     * @return
     */
    private  Retrofit initRetrofit(String basrUrl){
        OkHttpClient httpClient = new OkHttpClient();
        if (BuildConfig.DEBUG) {
            HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
            logging.setLevel(HttpLoggingInterceptor.Level.BODY);
            httpClient = new OkHttpClient.Builder().addInterceptor(logging).build();
        }
        Gson gson = new GsonBuilder()
                .setDateFormat("yyyy-MM-dd'T'HH:mm:ssZ")
                .create();//使用 gson coverter，统一日期请求格式
        return new Retrofit.Builder()
                .baseUrl(basrUrl)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .client(httpClient)
                .build();
    }

    /**
     * 发起请求
     * @param model
     */
    private void enqueue(Call<T> model) {
        model.enqueue(new Callback<T>() { //4.发起请求
            @Override
            public void onResponse(Call<T> call, Response<T> response) {
                if (mHttpListener != null) {
                    mHttpListener.onHttpSuccess(response.body());
                }
            }
            @Override
            public void onFailure(Call<T> call, Throwable t) {
                if (mHttpListener != null) {
                    mHttpListener.onHttpFailure(t.getMessage());
                }
            }
        });
    }


    /**
     * get请求 获取app版本信息
     * @param mHashMap 多个参数自段和值的map集合
     */
    public void getVersionInfo(HashMap<String, String> mHashMap) {
        HttpInterface service = createApiService();//1.初始化Retrofit
        Call<T> model = service.getVersionInfo(mHashMap); //2.创建请求对象
        enqueue(model);//3.发起请求
    }

    /**
     * 网络请求结果监听器
     * @param httpListener
     */
    public void setOnHttpListener(HttpListener httpListener) {
        mHttpListener = httpListener;
    }


    public interface HttpListener<T>{

        /**
         * 网络请求成功
         * @param bean 后返回的bean
         */
        void onHttpSuccess(T bean);

        /**
         * 网络请求失败
         * @param errorMsg  错误信息
         */
        void onHttpFailure(String errorMsg);
    }

}
