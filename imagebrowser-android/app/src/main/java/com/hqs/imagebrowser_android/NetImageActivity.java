package com.hqs.imagebrowser_android;

import android.content.res.AssetManager;
import android.graphics.drawable.Drawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.bumptech.glide.Glide;
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

public class NetImageActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_net_image);

        final String[] files = getFiles();


        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        recyclerView.setLayoutManager(layoutManager);

        recyclerView.setAdapter(new RecyclerView.Adapter() {
            @Override
            public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                RelativeLayout relativeLayout = (RelativeLayout) LayoutInflater.from(parent.getContext()).inflate(R.layout.image_holder, null);

                return new LocalImageActivity.MyViewHolder(relativeLayout);
            }

            @Override
            public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {

                LocalImageActivity.MyViewHolder myViewHolder = (LocalImageActivity.MyViewHolder) holder;
                Glide.with(NetImageActivity.this)
                        .load(files[position])
                        .placeholder(R.mipmap.ic_launcher)
                        .into(myViewHolder.imageView);


                myViewHolder.imageView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        ArrayList<QImage> imgs = new ArrayList<>();

                        for (int i = 0; i < files.length; i++) {
                            QImage qImage = new QImage();
                            qImage.filePathOrUrl = files[i];

                            if(i == position){
                                qImage.srcImageView = (ImageView) v;
                            }

                            imgs.add(qImage);
                        }

                        ImageBrowser.placeHolderImageRes = R.mipmap.ic_launcher;
                        ImageBrowser.showWithImages(NetImageActivity.this, imgs, position);

                    }
                });
            }

            @Override
            public int getItemCount() {
                return files.length;
            }
        });
    }

    String[] getFiles(){
        String[] files = new String[]{
                "http://img3.duitang.com/uploads/item/201508/15/20150815232316_nEvme.png",
                "http://image.tianjimedia.com/uploadImages/2014/287/00/WU13N728772L.jpg",
                "http://cdn.duitang.com/uploads/item/201508/23/20150823182754_svXVS.jpeg",
                "http://img3.imgtn.bdimg.com/it/u=1891722812,1090133293&fm=26&gp=0.jpg",
                "http://img4.duitang.com/uploads/item/201607/18/20160718175115_K5ivn.png",
                "http://img3.duitang.com/uploads/item/201409/13/20140913134633_3xTMa.jpeg"
        };

        return files;
    }

}
