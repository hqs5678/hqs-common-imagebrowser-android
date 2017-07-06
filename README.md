# Android 图片浏览器

简单的Android浏览器
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

