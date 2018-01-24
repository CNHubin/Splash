package hubin.splash.utils;

/*
 *  @项目名：  TaiyaoVerify 
 *  @包名：    com.taiyao.taiyaoverify.utils
 *  @文件名:   SystemManager
 *  @创建者:   胡英姿
 *  @创建时间:  2018-01-03 16:30
 *  @描述：    TODO
 */

import android.util.Log;

import java.io.DataOutputStream;
import java.io.IOException;

public class SystemManager {


    /**
     * 判断是否拥有root权限 如果有切换到root
     * @return  没有开root 返回false
     */
    public static boolean getRootAhth( ) {
        Process process = null;
        DataOutputStream os = null;
        try {
            Log.i("roottest", "try it");
            String cmd = "touch /data/roottest.txt";
            process = Runtime.getRuntime().exec("su"); //切换到root帐号
            os = new DataOutputStream(process.getOutputStream());
            os.writeBytes(cmd + "\n");
            os.writeBytes("exit\n");
            os.flush();
            process.waitFor();
        } catch (Exception e) {
            return false;
        } finally {
            try {
                if (os != null) {
                    os.close();
                }
                process.destroy();
            } catch (Exception e) {
            }
        }
        return true;
    }

    /**
     * 应用程序运行命令获取 Root权限，设备必须已破解(获得ROOT权限)
     * @param command 命令：String apkRoot="chmod 777 "+getPackageCodePath();
     * @return  0 命令执行成功
     */
    public static int RootCommand(String command) {
        Process process = null;
        DataOutputStream os = null;
        try {
            process = Runtime.getRuntime().exec("su");
            os = new DataOutputStream(process.getOutputStream());
            os.writeBytes(command + "\n");
            os.writeBytes("exit\n");
            os.flush();
            int i = process.waitFor();

            Log.d("SystemManager", "i:" + i);
            return i;
        } catch (Exception e) {
            Log.d("SystemManager", e.getMessage());
            return -1;
        } finally {
            try {
                if (os != null) {
                    os.close();
                }
                process.destroy();
            } catch (Exception e) {
            }
        }
    }

    /**
     * 提升读写权限
     * @param filePath 文件路径
     * @return
     * @throws IOException
     */
    public static void setPermission(String filePath)  {
        String command = "chmod " + "777" + " " + filePath;
        Runtime runtime = Runtime.getRuntime();
        try {
            runtime.exec(command);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
