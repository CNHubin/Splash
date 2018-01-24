package com.hubin.splash;

/*
 *  @项目名：  Splash 
 *  @包名：    hubin.splash
 *  @文件名:   Config
 *  @创建者:   胡英姿
 *  @创建时间:  2018-01-24 11:40
 *  @描述：    配置文件
 */

public class Config {

    //Map集合中需要携带的字段
    public static final String SERVER_APPID ="appid"; //get请求参数 apk在服务端的编号
    public static final String SERVER_VERSIONCODE ="versioncode";//get请求参数 版本号

    public static final String APPID ="1001"; //apk在服务端的编号

    public static final String MAP_URL_BASE ="http://verfy.taiyaoguangdian.com/";//服务器根路径
    public static final String MAP_URL_CHILD ="taiyaofile/version";//服务器二级路径
    public static final String MAP_URL_DOWNLOAD_BASE ="http://verfy.taiyaoguangdian.com/taiyaofile/";//下载服务器根路径

}
