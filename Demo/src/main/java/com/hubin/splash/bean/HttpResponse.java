package com.hubin.splash.bean;

/*
 *  @项目名：  TaiyaoVerify 
 *  @包名：    com.taiyao.taiyaoverify.bean
 *  @文件名:   HttpResponse
 *  @创建者:   胡英姿
 *  @创建时间:  2017-12-04 10:35
 *  @描述：    TODO
 */

public class HttpResponse {
    //错误信息
    public String response;//相应信息 error：相应失败  login：相应成功
    public String error_code;//错误码
    public String error; //错误信息
    //警告信息
    public String warning_code;//警告
    public String warning; //警告信息

    //bean
    public VersionInfo version;

    /**
     * 服务器版本信息
     */
    public class VersionInfo {
        public String description; //app的中文描述
        public int newversion;//最新版本号
        public String versionname;//版本名字
        public String publishtime;//发布时间
        public String url; //新版apk下载路径

    }
}
