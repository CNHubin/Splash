
package hubin.splash.utils;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.support.v4.content.FileProvider;
import android.text.TextUtils;

import java.io.File;

/**
 * APK安装或者更新等操作的工具类
 */
public class ApkUtils {

    //获取APK版本号 是根据 key uuid判断（渠道 版本）
    public static int getVersionCode (Context context) {
        PackageManager pm = context.getPackageManager();
        try {
            PackageInfo info = pm.getPackageInfo(context.getPackageName(), 0);
            LogUtils.d("getVersionCode  D : 当前版本号="+info.versionCode);
            return info.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            return 0;
        }
    }

    //获取APK版本名 是根据 key uuid判断（渠道 版本）
    public static String getVersionName (Context context) {
        PackageManager pm = context.getPackageManager();
        try {
            PackageInfo info = pm.getPackageInfo(context.getPackageName(), 0);
            LogUtils.d("getVersionCode  D : 当前版本="+info.versionName);
            return info.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            return "1.0";
        }
    }
    /**
     * 获取已安装Apk文件的源Apk文件
     * 如：/data/app/my.apk
     * @param context
     * @param packageName
     * @return
     */
    public static String getSourceApkPath(Context context, String packageName) {
        if (TextUtils.isEmpty(packageName))
            return null;

        try {
            ApplicationInfo appInfo = context.getPackageManager()
                    .getApplicationInfo(packageName, 0);
            return appInfo.sourceDir;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }



    /**
     * 安装APK 适配android7.0
     * @param context  上下文
     * @param apkPath apk'路径
     * @param packageName  应用包名
     */
    public static void installNormal(Context context,String apkPath,String packageName) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        //版本在7.0以上是不能直接通过uri访问的
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.N) {//7.0以上版本
            File file = (new File(apkPath));
            // 由于没有在Activity环境下启动Activity,设置下面的标签
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            //参数1 上下文, 参数2 Provider主机地址 和配置文件中保持一致   参数3  共享的文件
            Uri apkUri = FileProvider.getUriForFile(context, packageName+".installProvider", file);
            //添加这一句表示对目标应用临时授权该Uri所代表的文件
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            intent.setDataAndType(apkUri, "application/vnd.android.package-archive");
        } else {
            intent.setDataAndType(Uri.fromFile(new File(apkPath)),
                    "application/vnd.android.package-archive");
        }
        context.startActivity(intent);
    }

    //通过Root方式安装
    private static void installRoot(Context context, String apkPath) {
    /*    Observable.just(apkPath)
                .map(mApkPath -> "pm install -r " + mApkPath)
                .map(SystemManager::RootCommand)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(integer -> {
                    if (integer == 0) {
                        Toast.makeText(context, "安装成功", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(context, "root权限获取失败,尝试普通安装", Toast.LENGTH_SHORT).show();
                        installApk(context,apkPath);
                    }
                });*/
    }
}
