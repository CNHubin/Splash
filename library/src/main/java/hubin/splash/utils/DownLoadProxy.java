package hubin.splash.utils;

/*
 *  @项目名：  TaiyaoVerify 
 *  @包名：    com.taiyao.taiyaoverify.utils
 *  @文件名:   DownLoadProxy
 *  @创建者:   胡英姿
 *  @创建时间:  2018-01-03 14:25
 *  @描述：   下载代理 第三方下载工具隔离
 */

import android.content.Context;

import com.liulishuo.filedownloader.BaseDownloadTask;
import com.liulishuo.filedownloader.FileDownloadListener;
import com.liulishuo.filedownloader.FileDownloader;

import java.io.File;

import static hubin.splash.BaseSplashActivity.TEMP_SUFFIX;

public class DownLoadProxy {

    /**
     * 单任务下载apk
     *
     * @param url  下载路径
     * @param path 存放路径
     */
    public void downLoaderApk(final Context context, String url, final String path) {
        File mFile = new File(path+ TEMP_SUFFIX);//临时文件路径
        if (mFile.exists()) {
            LogUtils.e("error  D :不支持断点续传 删除缓存文件");
            mFile.delete();
        }
        FileDownloader.setup(context);
        FileDownloader.getImpl().create(url)
                .setPath(path)//下载文件的存储绝对路径
//                .setForceReDownload(true)//不检查文件是否已经存在直接重新下载
                .setListener(new FileDownloadListener() {
                    @Override
                    protected void pending(BaseDownloadTask task, int soFarBytes, int totalBytes) {
                        LogUtils.d("pending  D : 等待，已经进入下载队列soFarBytes="+soFarBytes+" totalBytes="+totalBytes);
                    }

                    @Override
                    protected void connected(BaseDownloadTask task, String etag, boolean isContinue,
                                             int soFarBytes, int totalBytes) {
                        LogUtils.i("connected  D : 已经连接上etag="+etag+" isContinue="+isContinue+" soFarBytes="+soFarBytes+" totalBytes="+totalBytes);
                        if (mDownloadListener != null) {
                            mDownloadListener.connected(etag,isContinue,soFarBytes,totalBytes);
                        }
                    }

                    @Override
                    protected void progress(BaseDownloadTask task, int soFarBytes, int totalBytes) {
                        LogUtils.i("progress  I : 下载进度回调soFarBytes = " + soFarBytes + " totalBytes=" +
                                totalBytes+" 进度:"+(soFarBytes*100)/totalBytes+"%");
                        if (mDownloadListener != null) {
                            mDownloadListener.progress((soFarBytes*100)/totalBytes,task.getSpeed());
                        }
                    }

                    @Override
                    protected void blockComplete(BaseDownloadTask task) {
                        //在完成前同步调用该方法，此时已经下载完成
                        LogUtils.d("blockComplete  D : 下载完成");
                    }

                    @Override
                    protected void retry(final BaseDownloadTask task, final Throwable ex, final int
                            retryingTimes, final int soFarBytes) {
                        LogUtils.d("retry  D : 重试之前把将要重试是第几次回调回来");
                    }

                    @Override
                    protected void completed(BaseDownloadTask task) {//下载完成后
//                        SystemManager.setPermission(path);//提升文件读写权限
                        if (mDownloadListener != null) {
                            mDownloadListener.progress(100,task.getSpeed());
                            mDownloadListener.completed(path);//回调
                        }
                    }

                    @Override
                    protected void paused(BaseDownloadTask task, int soFarBytes, int totalBytes) {
                        LogUtils.d("paused  D : 暂停下载");
                    }

                    @Override
                    protected void error(BaseDownloadTask task, Throwable e) {
                        LogUtils.e("error  D : 下载出现错误");
                        if (mDownloadListener != null) {
                            mDownloadListener.error(task.getEx());
                        }
                    }

                    @Override
                    protected void warn(BaseDownloadTask task) {
                        LogUtils.d("warn  D : 在下载队列中(正在等待/正在下载)已经存在相同下载连接与相同存储路径的任务");
                    }
                }).start();
    }

    private DownloadListener mDownloadListener;

    public void setOnDownloadListener(DownloadListener downloadListener) {
        mDownloadListener = downloadListener;
    }
    public interface DownloadListener{

        /**
         * 下载完成
         */
        void completed(String path);

        /**
         * 下载出现错误
         */
        void error(Throwable e);

        /**
         * 下载进度回掉  百分比
         * @param progress    百分比
         * @param speed  下载速度
         */
        void progress(int progress, int speed);

        /**
         * 开始下载
         * @param etag
         * @param isContinue
         * @param soFarBytes  目前字节数
         * @param totalBytes  总字节数
         */
        void connected(String etag, boolean isContinue, int soFarBytes, int totalBytes);
    }
}
