package com.hubin.splash;

import android.os.Bundle;

import com.hubin.splash.bean.HttpResponse;
import com.hubin.splash.retrofit.HttpProxy;

import java.util.HashMap;

import hubin.splash.BaseSplashActivity;
import hubin.splash.utils.LogUtils;

import static com.hubin.splash.Config.APPID;
import static com.hubin.splash.Config.URL_BASE;
import static com.hubin.splash.Config.URL_DOWNLOAD_BASE;
import static com.hubin.splash.Config.SERVER_APPID;
import static com.hubin.splash.Config.SERVER_VERSIONCODE;

public class MainActivity extends BaseSplashActivity {
    private HttpResponse.VersionInfo mVersionInfo;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }


    @Override
    public void getInternetInfo() {
        HttpProxy<HttpResponse> mHttpProxy = new HttpProxy<>(URL_BASE);
        mHttpProxy.setOnHttpListener(mHttpListener);//网络请求结果回掉
        //根据appid 请求版本信息
        HashMap<String, String> mGetParam = new HashMap<>();
        mGetParam.put(SERVER_APPID,APPID);
        mGetParam.put(SERVER_VERSIONCODE,BuildConfig.VERSION_CODE+"");
        mHttpProxy.getVersionInfo(mGetParam);
    }


    /**
     * 网络请求接听
     */
    private HttpProxy.HttpListener<HttpResponse> mHttpListener = new HttpProxy.HttpListener<HttpResponse>() {
        @Override
        public void onHttpSuccess(HttpResponse bean) {
            LogUtils.d("onHttpSuccess  D : 请求成功"+bean.response);
            mVersionInfo = bean.version;
            String  patchUrl = URL_DOWNLOAD_BASE + mVersionInfo.url; //补丁下载地址拼接

            //检查版本下载更新
            updateVersion(mVersionInfo.versionname,mVersionInfo.newversion,patchUrl,mVersionInfo.size);
        }
        @Override
        public void onHttpFailure(String errorMsg) {
            LogUtils.e("onHttpFailure E : 请求失败："+errorMsg);
        }
    };


    /**
     * 开始下载
     * @param isContinue true 表示是续传
     */
    @Override
    protected void startDownLoad(boolean isContinue) {
        LogUtils.d("startDownLoad  D : 开始下载："+isContinue);
    }

    /**
     * 下载进度
     * @param progress
     */
    @Override
    protected void downProgress(int progress) {
        LogUtils.d("progress  D : 下载进度："+progress);
    }


    /**
     * 去下一个页面
     */
    @Override
    protected void toNextPage() {
        LogUtils.d("toNextPage  D : 准备去下一个页面");
    }
}
