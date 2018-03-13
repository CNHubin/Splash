package hubin.splash.utils;

/*
 *  @项目名：  MahJongHall 
 *  @包名：    com.taiyao.mahjonghall.presenter.util
 *  @文件名:   ToastHelper
 *  @创建者:   胡英姿
 *  @创建时间:  2017-06-21 9:49
 *  @描述：    toast 工具类 防止吐司队列过多时 不停的弹出
 */

import android.content.Context;
import android.widget.Toast;

public class ToastHelper {

    private static Toast toast;

    public static void toast(Context context, String msg){
        if(toast==null) {
            toast = Toast.makeText(context, msg, Toast.LENGTH_SHORT);
        }else {
            toast.setText(msg);
        }
        toast.show();
    }

}
