# Android 图片浏览器

简单的Android浏览器
##### 运行效果图
![运行效果图](https://github.com/hqs5678/hqs-common-imagebrowser-android/blob/master/2017-07-06%2016_29_35.gif)

## 安装说明
### Gradle
```
    compile 'com.hqs.common.helper.imagebrowser:imagebrowser:1.1.19'
```
## 关于代码混淆
> 如果在缩放图片时出现莫名其妙的bug,由于使用了类的反射机制, 需要注意代码混淆

##### 请在项目的混淆文件如：proguard-rules.pro中添加如下代码
```
-keep class com.bm.library.** {*;}
```

##### 使用方法, 以RecyclerView的adapter为例, 相信代码请查看Demo
```
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
                qImage.filePathOrUrl = files.get(i);

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

```
