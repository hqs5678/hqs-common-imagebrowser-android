package com.hqs.imagebrowser_android;

import android.content.res.AssetManager;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.hqs.common.helper.imagebrowser.ImageBrowser;
import com.hqs.common.helper.imagebrowser.QImage;
import com.hqs.common.utils.Log;
import com.hqs.common.utils.SDCardUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;

public class LocalImageActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_local_image);

        final ArrayList<String> files = getFiles();

        Log.print(files);

        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        recyclerView.setLayoutManager(layoutManager);

        recyclerView.setAdapter(new RecyclerView.Adapter() {
            @Override
            public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                RelativeLayout relativeLayout = (RelativeLayout) LayoutInflater.from(parent.getContext()).inflate(R.layout.image_holder, null);

                return new MyViewHolder(relativeLayout);
            }

            @Override
            public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {

                MyViewHolder myViewHolder = (MyViewHolder) holder;
                myViewHolder.imageView.setImageDrawable(Drawable.createFromPath(files.get(position)));

                myViewHolder.imageView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        ArrayList<QImage> imgs = new ArrayList<>();

                        for (int i = 0; i < files.size(); i++) {
                            QImage qImage = new QImage();
                            qImage.filePathOrUrl = files.get(position);

                            if(i == position){
                                qImage.srcImageView = (ImageView) v;
                            }

                            imgs.add(qImage);
                        }

                        ImageBrowser.placeHolderImageRes = R.mipmap.ic_launcher;
                        ImageBrowser.showWithImages(LocalImageActivity.this, imgs, position);

                    }
                });
            }

            @Override
            public int getItemCount() {
                return files.size();
            }
        });
    }

    ArrayList<String> getFiles(){
        ArrayList<String> files = new ArrayList<>();

        final AssetManager assetManager = getAssets();
        String sdDir = SDCardUtils.getSDCardPath() + "/imagebrowser_android/";

        try {


            File dir = new File(sdDir);
            if (dir.exists()){
                File[] names = dir.listFiles();
                for(int i = 0;i < names.length; i++){
                    files.add(names[i].getAbsolutePath());
                }
            }
            else{
                dir.mkdir();

                String[] list = assetManager.list("img");

                // 将assets 文件夹下的文件copy 到sd卡
                for(int i = 0;i < list.length; i++){

                    InputStream is = assetManager.open("img/" + list[i]);
                    OutputStream os = new FileOutputStream(sdDir + list[i]);
                    byte[] buffer = new byte[1024];
                    int byteCount=0;
                    while((byteCount=is.read(buffer))!=-1) {//循环从输入流读取 buffer字节
                        os.write(buffer, 0, byteCount);//将读取的输入流写入到输出流
                    }
                    os.flush();//刷新缓冲区
                    is.close();
                    os.close();

                    files.add(sdDir + list[i]);
                }
            }


        } catch (IOException e) {
            e.printStackTrace();
        }

        return files;
    }


    class MyViewHolder extends RecyclerView.ViewHolder{

        ImageView imageView;
        public MyViewHolder(View itemView) {
            super(itemView);
            this.imageView = (ImageView) itemView.findViewById(R.id.imgView);
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        }
    }
}
