package com.wevan.wean.a75;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageItemInfo;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.preference.DialogPreference;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class SplashActivity extends AppCompatActivity {

    private static final int MSG_UPDATE_DITALOG = 1;
    private static final int MSG_ENTER_HOME = 2;
    private String code;
    private String apkurl;
    private String des;

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_UPDATE_DITALOG:
                    showdialog();
                    break;
                case MSG_ENTER_HOME:
                    enterHome();
                    break;
            }
        }
    };


    private void showdialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(false);
        builder.setTitle("新版本" + code);
        builder.setIcon(R.drawable.luncher_bg);
        builder.setMessage(des);
        builder.setPositiveButton("升级", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
                enterHome();
            }
        });
        builder.show();
        //  builder.create().show();
    }

    //跳转到主界面
    private void enterHome() {
        //跳转界面用Intent
        Intent intent = new Intent(this, HomeActivity.class);
        startActivity(intent);
        //防止从主界面跳到欢迎界面
        finish();
    }

    private TextView tv_splash_versionname;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        tv_splash_versionname = (TextView) findViewById(R.id.tv_splash_versionname);
        tv_splash_versionname.setText("版本号" + getVersionName());

        update();
    }

    //提醒用户更新版本
    private void update() {
        //1.连接服务器，查看是否有最新版本，联网操作，耗时操作。4.0后不允许在主线程中执行，放在子线程中执行
        new Thread() {
            private int startTime;

            @Override
            public void run() {
                Message message = Message.obtain();
                //在连接之前获取一个时间
                startTime = (int) System.currentTimeMillis();
                try {
                    //链接服务器,参数为链接路径
                    URL url = new URL("http://10.0.2.2:8080/updateinfo.html");
                    //获取连接操作
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();//http协议
                    //设置超时时间
                    conn.setConnectTimeout(5000);//设置连接超时时间
                    conn.setReadTimeout(5000);//设置读取超时时间
                    //设置请求方式
                    conn.setRequestMethod("GET");
                    //获取服务器返回的状态码
                    int responseCode = conn.getResponseCode();
                    System.out.println("222");
                    if (responseCode == 200) {
                        //连接成功,获取服务器返回的数据，code：新版本的版本号 apkurl:新版本的下载路径 des：告诉用户增加了那些功能，修改了那些bug
                        //获取数据之前，服务器是如何封装数据的xml json（常用）
                        System.out.println("连接成功...");
                        //获取服务器流信息
                        InputStream in = conn.getInputStream();
                        //将获取到的流信息转化成字符串
                        String json = StreamUtil.parserStreamUtil(in);
                        //解析json数据
                        JSONObject jsonObject = new JSONObject(json);
                        //获取数据
                        code = jsonObject.getString("code");
                        apkurl = jsonObject.getString("apkurl");
                        des = jsonObject.getString("des");
                        System.out.println("code" + code + "    apkurl" + apkurl + "  des" + des);

                        //1.2查看是否有最新版本
                        //判断服务器返回的版本号和当前应用程序的版本是否一致
                        if (code.equals(getVersionName())) {
                            message.what = MSG_ENTER_HOME;
                        } else {
                            //弹出对话框提醒更新版本
                            System.out.println("新版本");
                            message.what = MSG_UPDATE_DITALOG;

                        }
                    } else {
                        //连接失败
                        System.out.println("连接失败...");
                        //获取服务器返回的流信息
                        InputStream in = conn.getInputStream();
                        //将获取到的流信息转化成字符串
                    }

                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                } finally {
                    int endTime = (int) System.currentTimeMillis();
                    int dTime = endTime - startTime;
                    SystemClock.sleep(2000);
                    //处理连接外网连接时间的问题
                    //连接成功后获取一个时间
                    if (dTime < 2000) {
                        SystemClock.sleep(2000 - dTime);
                    }
                    handler.sendMessage(message);
                }
            }

            ;
        }.start();
    }
    //获取当前应用程序版本号

    private String getVersionName() {
        //包的管理者，获取清单文件中的所有信息
        PackageManager pm = getPackageManager();
        try {
            //根据包名获得清单文件的信息，其实就是返回一个保存清单文件信息的javabean
            //应用程序的包名，指定信息的标签（0代表获取一些基础信息，如包名，版本号等，要想获取权限必须通过标签指定才能获取
            //GET PERMISSIONS除了获取基础信息之外还额外获取权限的信息
            //getpakagename：获取当前应用程序包名，返回String类型
            PackageInfo packageInfo = pm.getPackageInfo(getPackageName(), 0);
            //获取版本号名称
            String versionName = packageInfo.versionName;
            return versionName;
        } catch (PackageManager.NameNotFoundException e) {
            //包名找不到的异常
            e.printStackTrace();
        }
        return null;

    }

}
