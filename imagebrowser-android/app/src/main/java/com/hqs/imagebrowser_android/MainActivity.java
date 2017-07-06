package com.hqs.imagebrowser_android;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.bm.library.PhotoView;
import com.bumptech.glide.Glide;
import com.hqs.common.helper.imagebrowser.ImageBrowser;
import com.hqs.common.helper.imagebrowser.QImage;
import com.hqs.common.utils.Log;
import com.hqs.common.utils.SDCardUtils;
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


        Glide.with(this).load("https://www.7x24home.com:8889/upload/pics/beijing/2017/06/19/WX20170420657648SM657685test/f751332f-4ad1-4d40-ba95-f910f8ea10e0/2017-06-19-104805418574.png")
                .into((PhotoView) findViewById(R.id.imageView0));

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

        if (view instanceof PhotoView) {
            if (view.getId() == R.id.image2){
                showImg(2);
            }
            else if (view.getId() == R.id.imageView0){
                showImg(0);
            }
            else if (view.getId() == R.id.imageView1){
                showImg(1);
            }
            else if (view.getId() == R.id.image3){
                showImg(3);
            }
            else if (view.getId() == R.id.image10){
                showImg1(0);
            }
            else if (view.getId() == R.id.image11){
                showImg1(1);
            }
            else if (view.getId() == R.id.image12){
                showImg1(2);
            }
            else if (view.getId() == R.id.image13){
                showImg1(3);
            }
            else if (view.getId() == R.id.image14){
                showImg1(4);
            }
            else if (view.getId() == R.id.image15){
                showImg1(5);
            }
            else if (view.getId() == R.id.image16){
                showImg1(6);
            }
            else if (view.getId() == R.id.image17){
                showImg1(7);
            }
            else if (view.getId() == R.id.image18){
                showImg1(8);
            }
            else if (view.getId() == R.id.image19){
                showImg1(9);
            }
            else if (view.getId() == R.id.image20){
                showImg1(10);
            }
        } else {
            ImageBrowser.placeHolderImageRes = R.mipmap.ic_launcher;
            ArrayList<String> filePaths = new ArrayList<>();
            filePaths.add("http://d.hiphotos.baidu.com/image/pic/item/10dfa9ec8a136327a1de913a938fa0ec08fac78c.jpg");

            filePaths.add(SDCardUtils.getSDCardPath() + "000.jpg");
            ImageBrowser.show(MainActivity.this, filePaths, 0);
        }


    }

    private void showImg(int index){
        ImageBrowser.placeHolderImageRes = R.mipmap.ic_launcher;
        ImageBrowser.backgroundColorRes = R.color.colorAccent;

        QImage image;
        ArrayList<QImage> arrayList = new ArrayList<>();

        image = new QImage();
        image.srcImageView = (PhotoView) findViewById(R.id.imageView0);

        image.filePath = "https://www.7x24home.com:8889/upload/pics/beijing/2017/06/19/WX20170420657648SM657685test/f751332f-4ad1-4d40-ba95-f910f8ea10e0/2017-06-19-104805418574.png";
        arrayList.add(image);

        image = new QImage();
        image.srcImageView = (PhotoView) findViewById(R.id.imageView1);
        arrayList.add(image);

        image = new QImage();
        image.srcImageView = (PhotoView) findViewById(R.id.image2);
        arrayList.add(image);

        image = new QImage();
        image.srcImageView = (PhotoView) findViewById(R.id.image3);
        arrayList.add(image);

        ImageBrowser.animDuration = 200;
        ImageBrowser.showWithImages(this, arrayList, index);
    }


    private void showImg1(int index){
        ImageBrowser.placeHolderImageRes = R.mipmap.ic_launcher;
        ImageBrowser.backgroundColorRes = R.color.colorAccent;
        ImageBrowser.animDuration = 200;

        QImage image;
        ArrayList<QImage> arrayList = new ArrayList<>();

        image = new QImage();
        image.srcImageView = (PhotoView) findViewById(R.id.image10);
        arrayList.add(image);

        image = new QImage();
        image.srcImageView = (PhotoView) findViewById(R.id.image11);
        arrayList.add(image);

        image = new QImage();
        image.srcImageView = (PhotoView) findViewById(R.id.image12);
        arrayList.add(image);

        image = new QImage();
        image.srcImageView = (PhotoView) findViewById(R.id.image13);
        arrayList.add(image);

        image = new QImage();
        image.srcImageView = (PhotoView) findViewById(R.id.image14);
        arrayList.add(image);

        image = new QImage();
        image.srcImageView = (PhotoView) findViewById(R.id.image15);
        arrayList.add(image);

        image = new QImage();
        image.srcImageView = (PhotoView) findViewById(R.id.image16);
        arrayList.add(image);

        image = new QImage();
        image.srcImageView = (PhotoView) findViewById(R.id.image17);
        arrayList.add(image);

        image = new QImage();
        image.srcImageView = (PhotoView) findViewById(R.id.image18);
        arrayList.add(image);

        image = new QImage();
        image.srcImageView = (PhotoView) findViewById(R.id.image19);
        arrayList.add(image);

        image = new QImage();
        image.srcImageView = (PhotoView) findViewById(R.id.image20);
        arrayList.add(image);

        ImageBrowser.showWithImages(this, arrayList, index);
    }
}
