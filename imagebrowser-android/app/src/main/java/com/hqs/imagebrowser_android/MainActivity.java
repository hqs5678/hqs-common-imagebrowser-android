package com.hqs.imagebrowser_android;

import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.hqs.common.helper.imagebrowser.ImageBrowser;
import com.hqs.common.utils.SDCardUtils;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

    }


    public void onClick(View view){
        ImageBrowser.placeHolderImageRes = R.mipmap.ic_launcher;
        ArrayList<String> filePaths = new ArrayList<>();
        filePaths.add("http://d.hiphotos.baidu.com/image/pic/item/10dfa9ec8a136327a1de913a938fa0ec08fac78c.jpg");
        filePaths.add("http://www.7x24home.com/upload/pics/beijing/2017/01/19/WX20160520991978SM991986/2017-01-19-144216193094.png");

        filePaths.add(SDCardUtils.getSDCardPath() + "000.jpg");
        ImageBrowser.show(MainActivity.this, filePaths, 0);
    }
}
