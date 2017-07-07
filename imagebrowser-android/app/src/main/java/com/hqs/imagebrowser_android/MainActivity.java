package com.hqs.imagebrowser_android;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.hqs.common.utils.Log;
import com.hqs.common.utils.ScreenUtils;
import com.wenming.library.LogReport;
import com.wenming.library.save.imp.CrashWriter;

import java.io.File;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ScreenUtils.setScreenOrientationPortrait(this);
        setContentView(R.layout.activity_main);


        LogReport.getInstance()
                .setCacheSize(30 * 1024 * 1024)//支持设置缓存大小，超出后清空
                .setLogDir(getApplicationContext(), "sdcard/" + this.getString(this.getApplicationInfo().labelRes) + "/")//定义路径为：sdcard/[app name]/
                .setWifiOnly(true)//设置只在Wifi状态下上传，设置为false为Wifi和移动网络都上传
                .setLogSaver(new CrashWriter(getApplicationContext()))//支持自定义保存崩溃信息的样式
                //.setEncryption(new AESEncode()) //支持日志到AES加密或者DES加密，默认不开启
                .init(getApplicationContext());

        Log.print("sdcard/" + this.getString(this.getApplicationInfo().labelRes) + "/");



    }

    @Override
    protected void onResume() {
        super.onResume();

        sendLog();
    }

    private void sendLog(){
        String logFile = "sdcard/" + this.getString(this.getApplicationInfo().labelRes) + "/";
        ArrayList<File> files = FileUtil.findFiles(logFile);
        if (files != null && files.size() > 0){
            for (File file: files){
                String log = FileUtil.fileToString(file);
                Log.print("CrashLog: " + log);

            }
        }
        FileUtil.removeDir(new File(logFile));
    }


    public void onClick(View view) {

        if (view.getId() == R.id.button2){
            Intent intent = new Intent(this, LocalImageActivity.class);
            startActivity(intent);
        }
        else{
            Intent intent = new Intent(this, NetImageActivity.class);
            startActivity(intent);
        }

    }


}
