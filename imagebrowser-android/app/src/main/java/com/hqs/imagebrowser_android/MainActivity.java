package com.hqs.imagebrowser_android;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;

import com.hqs.common.helper.imagebrowser.ImageBrowser;
import com.hqs.common.helper.imagebrowser.QImage;
import com.hqs.common.utils.SDCardUtils;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

    }


    public void onClick(View view) {

        if (view instanceof ImageView) {
            if (view.getId() == R.id.image2){
                showImg(2);
            }
            else if (view.getId() == R.id.imageView0){
                showImg(0);
            }
            else if (view.getId() == R.id.imageView1){
                showImg(1);
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


        QImage image;
        ArrayList<QImage> arrayList = new ArrayList<>();

        image = new QImage();
        image.srcImageView = (ImageView) findViewById(R.id.imageView0);
        arrayList.add(image);

        image = new QImage();
        image.srcImageView = (ImageView) findViewById(R.id.imageView1);
        arrayList.add(image);

        image = new QImage();
        image.srcImageView = (ImageView) findViewById(R.id.image2);
        arrayList.add(image);

        ImageBrowser.showWithImages(this, arrayList, index);
    }
}
