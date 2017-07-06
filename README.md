# Android 图片浏览器

简单的Android图片浏览器, 支持本地图片和在线图片
##### 运行效果图
![运行效果图](https://github.com/hqs5678/hqs-common-imagebrowser-android/blob/master/2017-07-06%2016_29_35.gif)

## 安装说明
### Gradle
```
    compile 'com.hqs.common.helper.imagebrowser:imagebrowser:1.1.18'
```
## 关于代码混淆
> 如果在缩放图片时出现莫名其妙的bug,由于使用了类的反射机制, 需要注意代码混淆

##### 请在项目的混淆文件如：proguard-rules.pro中添加如下代码
```
-keep class com.bm.library.** {*;}
```

## 使用方法
```
public void showImage(int index){

    // 设置占位图片
    ImageBrowser.placeHolderImageRes = R.mipmap.ic_launcher;
    // 设置浏览时背景颜色
    ImageBrowser.backgroundColorRes = R.color.colorAccent;
    // 开始时缩放时的时长
    ImageBrowser.animDuration = 200;

    // 添加图片
    
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
        
    .
    .
    .

    ImageBrowser.showWithImages(this, arrayList, index);
}
```
