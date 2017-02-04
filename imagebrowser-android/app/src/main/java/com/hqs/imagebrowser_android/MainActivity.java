package com.hqs.imagebrowser_android;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.bm.library.PhotoView;
import com.hqs.common.helper.imagebrowser.ImageBrowser;
import com.hqs.common.helper.imagebrowser.QImage;
import com.hqs.common.utils.SDCardUtils;
import com.hqs.common.utils.ScreenUtils;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ScreenUtils.setScreenOrientationPortrait(this);
        setContentView(R.layout.activity_main);




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


        ImageBrowser.showWithImages(this, arrayList, index);
    }


    private void showImg1(int index){
        ImageBrowser.placeHolderImageRes = R.mipmap.ic_launcher;
        ImageBrowser.backgroundColorRes = R.color.colorAccent;

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

        ImageBrowser.showWithImages(this, arrayList, index);
    }
}
