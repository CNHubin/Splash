package hubin.splash;

/*
 *  @项目名：  Splash 
 *  @包名：    hubin.splash
 *  @文件名:   BaseSplashActivity
 *  @创建者:   胡英姿
 *  @创建时间:  2018-01-23 15:45
 *  @描述：    TODO
 */

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

public class BaseSplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Toast.makeText(this, "我是你爸爸2", Toast.LENGTH_SHORT).show();

    }
}
