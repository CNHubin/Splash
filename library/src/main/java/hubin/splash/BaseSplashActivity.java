package hubin.splash;

/*
 *  @项目名：  Splash 
 *  @包名：    hubin.splash
 *  @文件名:   BaseSplashActivity
 *  @创建者:   胡英姿
 *  @创建时间:  2018-01-23 15:45
 *  @描述：    导航页的抽象activity
 */

import android.Manifest;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import java.io.File;
import java.text.DecimalFormat;

import hubin.splash.utils.ApkUtils;
import hubin.splash.utils.DownLoadProxy;
import hubin.splash.utils.LogUtils;
import hubin.splash.utils.SDCardHelper;
import hubin.splash.utils.ToastUtils;
import permissions.dispatcher.NeedsPermission;
import permissions.dispatcher.OnNeverAskAgain;
import permissions.dispatcher.OnPermissionDenied;
import permissions.dispatcher.RuntimePermissions;


@RuntimePermissions
public abstract class BaseSplashActivity extends AppCompatActivity {
    static {
        System.loadLibrary("hubinPatch");
    }

    public static final String SD_CARD = Environment.getExternalStorageDirectory() + File.separator; //SD卡目录
    public static final String PATCH_SUFFIX = ".patch"; //补丁后缀名
    public static final String TEMP_SUFFIX = ".temp"; //下载补丁未完成时的临时文件后缀名
    public static final String APK_SUFFIX = ".apk"; //合并补丁后的apk后缀名

    private DownLoadProxy mDownLoadProxy;
    private String mPatchName; //差分文件名
    private String mPathFile;//补丁文件下载保存路径
    private String mOldfile; //原APK文件存放路径
    private String mNewFile; //更新后的新版apk存储路径
    private String mPackageName; //应用程序包名
    private String mVersionName;//本地版本名
    private int mVersionCode;//本地版本号
    private String mNewversionname; //最新版本名
    private int mNewversioncode; //最新版本号
    private String mPatchUrl;//下载补丁的链接地址

    /**
     * 下载 需要 读写外部存储权限
     */
    @NeedsPermission({Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE})
    void downLoaderApk() {
        //使用FileDownloader下载引擎 下载apk
        LogUtils.d("onCreate  D : 保存路径：" + mPathFile);
        if (mDownLoadProxy == null) {
            mDownLoadProxy = new DownLoadProxy();
            mDownLoadProxy.setOnDownloadListener(mDownloadListener);//下载监听
        }
        mDownLoadProxy.downLoaderApk(this, mPatchUrl, mPathFile);
    }

    // 用户拒绝授权回调
    @OnPermissionDenied({Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.READ_EXTERNAL_STORAGE})
    void showDeniedForCamera() {
        LogUtils.e("showDeniedForCamera E : 用户拒绝授权");
        Toast.makeText(this, "请您打开权限", Toast.LENGTH_SHORT).show();
        finish();
    }

    // 用户拒绝并勾选了“不再提醒”
    @OnNeverAskAgain({Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.READ_EXTERNAL_STORAGE})
    void showNeverAskForCamera() {
        LogUtils.e("showNeverAskForCamera E : 用户永久拒绝了读写权限");
        ToastUtils.toast(this,"您永久拒绝了外部存储读写权限");
        finish();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull
            int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        //权限回掉处理
        BaseSplashActivityPermissionsDispatcher.onRequestPermissionsResult(this, requestCode, grantResults);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mVersionCode = ApkUtils.getVersionCode(this);//本地版本号
        mVersionName = ApkUtils.getVersionName(this);//本地版本名
        mPackageName = getPackageName();

        getInternetInfo();//获取服务器版本信息
    }


    /**
     * 检查版本
     * @param newversionname    服务器版本名
     * @param newversioncode    服务端版本号
     * @param patchUrl          补丁下载路径
     */
    protected void updateVersion(String newversionname, int newversioncode, String  patchUrl,String length) {
        mNewversionname = newversionname;
        mNewversioncode = newversioncode;
        mPatchUrl = patchUrl;
        String size = length;
        if (length == null) {
            size = "0";
        }

        //下载保存路径 并重命名
        mPatchName = mPackageName+mVersionName+"to"+mNewversionname;
        mPathFile = SD_CARD + mPatchName + PATCH_SUFFIX;
        //本地版校验
        if (mVersionCode < mNewversioncode) {
            //保留2位小数
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("发现新版本")
                    .setMessage("安装包大小"+b2Mb(size)+"M，是否现在更新？")
                    .setCancelable(false)  //点击空白处不消失
                    .setPositiveButton("现在更新", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            //SD卡状态
                            if (SDCardHelper.isSDCardMounted()) {//SD卡已挂载
                                LogUtils.d("onCreate  D : 开始更新");
                                BaseSplashActivityPermissionsDispatcher.downLoaderApkWithPermissionCheck(BaseSplashActivity.this); //下载
                            } else {
                                LogUtils.e("onCreate E : SD卡不存在");
                                ToastUtils.toast(BaseSplashActivity.this,"您的SD卡不存在，无法更新");
                                toNextPage();//去下一页
                            }
                        }
                    })
                    .setNegativeButton("暂不更新", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            LogUtils.d("onCreate  D : 暂时不更新");
                            toNextPage();//去下一页
                        }
                    })
                    .show();
        } else {
            LogUtils.d("onCreate  D : 当前已经是最新版本");
            toNextPage();//去下一页
        }
    }




    //APK下载监听
    private DownLoadProxy.DownloadListener mDownloadListener = new DownLoadProxy.DownloadListener() {
        /**
         * 开始下载
         * @param etag
         * @param isContinue
         * @param soFarBytes 目前字节数
         * @param totalBytes 总字节数
         */
        @Override
        public void connected(String etag, boolean isContinue, int soFarBytes, int totalBytes) {
            startDownLoad(isContinue);
        }
        /**
         * 下载成功
         * @param patch //保存的路径
         */
        @Override
        public void completed(String patch) {
            //合并补丁
            mOldfile = ApkUtils.getSourceApkPath(BaseSplashActivity.this, getPackageName());
            mNewFile = SD_CARD+mPackageName+mNewversionname+APK_SUFFIX;
            LogUtils.d("completed  D 补丁路径："+patch+"  旧apk路径："+mOldfile+"  新APK路径："+mNewFile);

            if (patch(mOldfile, mNewFile, mPathFile) == 0) {
                LogUtils.d("completed  D : 补丁合并成功");
                ApkUtils.installNormal(BaseSplashActivity.this, mNewFile,getPackageName()); //安装apk
            }
        }

        /**
         * 下载出现错误
         * @param e
         */
        @Override
        public void error(Throwable e) {
            String exception = e.toString();
            LogUtils.e("error E : 下载异常" + exception);
            if (exception.equals("java.io.IOException: Permission denied")) {//没有权限异常
                ToastUtils.toast(BaseSplashActivity.this,"请打外存储部读写权限");
                finish();
            }
            if (exception.startsWith("com.liulishuo.filedownloader.exception" +
                    ".FileDownloadGiveUpRetryException: require rang")) {//断点续传不支持
                File mFile = new File(mPathFile +TEMP_SUFFIX);
                if (mFile.exists()) {
                    LogUtils.d("error  D :不支持断点续传 删除缓存文件");
                    mFile.delete();
                    downLoaderApk();//重新下载
                }
            }
        }

        /**
         * 下载进度回掉
         * @param progress
         */
        @Override
        public void progress(int progress, int speed) {
            downProgress(progress,speed);
        }

    };

    /**
     * 将字节转换为M并保留两位小数
     * @param size
     * @return
     */
    protected String b2Mb(String size) {
        DecimalFormat decimalFormat=new DecimalFormat(".00");//构造方法的字符格式这里如果小数不足2位,会以0补足.
        return decimalFormat.format((float)Integer.parseInt(size)/1024/1024);
    }

     //获取服务器最新版本信息
    public abstract void getInternetInfo();

    /**
     * 开始下载
     * @param isContinue    true 表示是续传
     */
    protected abstract void startDownLoad(boolean isContinue);

    /**
     * 下载进度回调
     * @param progress
     */
    protected abstract void downProgress(int progress, int speed);

    /**
     * 去下一页
     */
    protected abstract void toNextPage();

    /**
     * 合并差分包
     * @param oldfile 旧版apk路径
     * @param newFile  新apk路径
     * @param patchFile 差分包路径
     * @return
     */
    public native static int patch(String oldfile, String newFile, String patchFile);

}
