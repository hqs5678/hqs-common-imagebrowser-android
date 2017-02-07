package com.hqs.common.helper.imagebrowser;

import android.app.Activity;
import android.content.Intent;
import android.graphics.RectF;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bm.library.Info;
import com.bm.library.PhotoView;
import com.bumptech.glide.Glide;
import com.hqs.common.utils.ScreenUtils;
import com.hqs.common.utils.StatusBarUtil;
import com.hqs.common.utils.ViewUtil;

import java.lang.reflect.Field;
import java.util.ArrayList;

/**
 * Created by apple on 2016/11/3.
 */

public class ImageBrowser {


    public static int placeHolderImageRes = -1;
    public static int backgroundColorRes = -1;
    public static int animDuration = 200;
    private static ArrayList<QImage> images;

    /**
     * show
     * @param filePaths         文件路径 或 url
     * @param currentIndex      当前显示的图片
     */
    public static void show(Activity activity, final ArrayList<String> filePaths, final int currentIndex){

        if (placeHolderImageRes == -1){
            Toast.makeText(activity, "请设置占位图", Toast.LENGTH_SHORT).show();
            return;
        }

        if (filePaths != null && filePaths.size() > 0 && activity != null){

            Intent intent = new Intent(activity, ImageActivity.class);
            intent.putExtra("filePaths", filePaths);
            intent.putExtra("currentIndex", currentIndex);

            activity.startActivity(intent);
            activity.overridePendingTransition(0, 0);
        }
    }


    public static void showWithImages(Activity activity, final ArrayList<QImage> images, final int currentIndex){

        if (placeHolderImageRes == -1){
            Toast.makeText(activity, "请设置占位图", Toast.LENGTH_SHORT).show();
            return;
        }

        if (images != null && images.size() > 0 && activity != null){

            Intent intent = new Intent(activity, ImageActivity.class);
            ImageBrowser.images = images;
            intent.putExtra("currentIndex", currentIndex);

            activity.startActivity(intent);
            activity.overridePendingTransition(0, 0);

        }
    }


    public static class ImageActivity extends Activity {

        private ArrayList<String> filePaths;
        private int currentIndex;
        private ContentView contentView;
        private RelativeLayout bgView;
        private ViewPager viewPager;
        private TextView tvIndex;
        private Handler mHandler;
        private float sw;
        private float sh;
        private ImageView.ScaleType scaleType = ImageView.ScaleType.FIT_CENTER;

        private ArrayList<PhotoView> views = new ArrayList<PhotoView>();

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);

            ScreenUtils.setScreenOrientationPortrait(this);
            StatusBarUtil.transparencyBar(this);

            Bundle extras = this.getIntent().getExtras();

            if (extras == null){
                finish();
                return;
            }
            sw = ScreenUtils.screenW(this);
            sh = ScreenUtils.screenH(this);

            mHandler = new Handler();
            filePaths = extras.getStringArrayList("filePaths");
            currentIndex = extras.getInt("currentIndex");

            if (filePaths == null){
                filePaths = new ArrayList<>();
                for (QImage image : images){
                    filePaths.add(image.filePath);
                }
            }

            contentView = (ContentView) LayoutInflater.from(this).inflate(R.layout.dialog_photo_browser, null);
            bgView = (RelativeLayout) contentView.findViewById(R.id.content_bg_view);
            if (backgroundColorRes != -1){
                bgView.setBackgroundResource(backgroundColorRes);
            }
            this.setContentView(contentView);
            contentView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onFinish();
                }
            });

            tvIndex = (TextView) contentView.findViewById(R.id.tv_index);
            tvIndex.setText(currentIndex + 1 + "/" + filePaths.size());
            viewPager = (ViewPager) contentView.findViewById(R.id.viewPager);

            setup();
        }

        private void setup(){

            // 先完成动画, 再显示所有, 设置viewpager
            if (images != null && images.size() > 0) {
                final PhotoView imgView = images.get(currentIndex).srcImageView;
                ViewUtil.getViewRect(imgView, new ViewUtil.OnViewRectCallBack() {
                    @Override
                    public void onRect(final RectF rectF) {
                        mHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                addAnimationEnter(rectF, imgView);
                            }
                        });
                    }
                });
            }
            else{
                setupViewPager();
                Animation animation = AnimationUtils.loadAnimation(this,android.R.anim.fade_in);
                animation.setDuration(animDuration);
                animation.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {
                        contentView.setEnabled(false);
                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {
                        contentView.setEnabled(true);
                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {

                    }
                });

                contentView.clearAnimation();
                contentView.setAnimation(animation);
            }

        }

        private void addAnimationEnter(RectF rectF, PhotoView srcImgView){
            final PhotoView imageView = new PhotoView(this);
            Glide.with(ImageActivity.this).load(images.get(currentIndex).filePath).placeholder(srcImgView.getDrawable()).into(imageView);
            imageView.setScaleType(scaleType);

            RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams((int) sw, (int) sh);
            imageView.setLayoutParams(layoutParams);

            contentView.addView(imageView);

            Info info = srcImgView.getInfo();

            try {
                Class cls = info.getClass();
                Field f = cls.getDeclaredField("mRect");
                f.setAccessible(true);
                f.set(info, rectF);
            } catch (Exception e) {
                e.printStackTrace();
            }


            imageView.setAnimaDuring(animDuration);
            imageView.animaFrom(info);
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    setupViewPager();
                    mHandler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            contentView.removeView(imageView);
                            contentView.setEnabled(true);
                        }
                    }, 300);
                }
            }, animDuration + 50);

            Animation fade = AnimationUtils.loadAnimation(this, android.R.anim.fade_in);
            fade.setDuration(animDuration);

            bgView.setAnimation(fade);



        }

        private void setupViewPager() {

            viewPager.setAdapter(new PagerAdapter() {


                @Override
                public int getCount() {
                    return filePaths.size();
                }

                @Override
                public boolean isViewFromObject(View view, Object object) {
                    return view == object;
                }

                @Override
                public Object instantiateItem(ViewGroup container, int position) {

                    final PhotoView photoView = getView(position);




                    photoView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            ImageActivity.this.onFinish();
                        }
                    });

                    String path = filePaths.get(position);
                    if (images == null){
                        Glide.with(ImageActivity.this).load(path).placeholder(placeHolderImageRes).into(photoView);
                    }
                    else{
                        Glide.with(ImageActivity.this).load(path).placeholder(images.get(position).srcImageView.getDrawable()).into(photoView);
                    }




                    container.addView(photoView);

                    return photoView;
                }

                @Override
                public void destroyItem(ViewGroup container, int position, Object object) {
                    container.removeView((View) object);
                }

                private PhotoView getView(int position){

                    if (views.size() == 0 || position + 1 > views.size()){
                        PhotoView photoView;
                        for (int i = views.size(); i < position + 1; i ++){
                            photoView = new PhotoView(ImageActivity.this);

                            // 启用图片缩放功能
                            photoView.enable();
                            photoView.setAnimaDuring(300);
                            photoView.setMaxScale(6);
                            photoView.setInterpolator(new DecelerateInterpolator());
                            photoView.setScaleType(scaleType);

                            views.add(photoView);
                        }
                    }

                    return views.get(position);
                }

            });

            viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
                @Override
                public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

                }

                @Override
                public void onPageSelected(int position) {
                    tvIndex.setText(position + 1 + "/" + filePaths.size());
                    currentIndex = position;
                }

                @Override
                public void onPageScrollStateChanged(int state) {

                }
            });

            viewPager.setCurrentItem(currentIndex);
        }

        private void onFinish(){

            // 添加动画
            if (images != null && images.size() > 0){
                final PhotoView imageView = images.get(currentIndex).srcImageView;
                ViewUtil.getViewRect(imageView, new ViewUtil.OnViewRectCallBack() {
                    @Override
                    public void onRect(final RectF rectF) {
                        mHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                addAnimationExit(rectF, imageView);
                            }
                        });

                    }
                });
            }
            else {
                Animation animation = AnimationUtils.loadAnimation(this, android.R.anim.fade_out);
                animation.setDuration(animDuration + 100);
                animation.setFillAfter(true);
                animation.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {
                        contentView.setEnabled(false);
                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {
                        finish();
                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {

                    }
                });

                contentView.clearAnimation();
                contentView.setAnimation(animation);
            }
        }

        private void addAnimationExit(RectF rectF, PhotoView srcImageView){

            PhotoView photoView = views.get(currentIndex);

            Info info = srcImageView.getInfo();
            try {
                Class cls = info.getClass();
                Field fImg = cls.getDeclaredField("mImgRect");
                fImg.setAccessible(true);
                RectF imgRect = (RectF) fImg.get(info);

                Field f = cls.getDeclaredField("mRect");
                f.setAccessible(true);
                RectF mRect = (RectF) f.get(info);

                float d = rectF.top + imgRect.top;
                float h = mRect.height();
                mRect.top = d;
                mRect.bottom = d + h;

            } catch (Exception e) {
                e.printStackTrace();
            }
            photoView.setAnimaDuring(animDuration);
            photoView.animaTo(info, new Runnable() {
                @Override
                public void run() {

                }
            });


            Animation fade = AnimationUtils.loadAnimation(this, android.R.anim.fade_out);
            fade.setDuration(animDuration);
            fade.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {
                    contentView.setEnabled(false);
                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    finish();
                }

                @Override
                public void onAnimationRepeat(Animation animation) {

                }
            });
            fade.setFillAfter(true);

            bgView.clearAnimation();
            bgView.setAnimation(fade);

        }

        @Override
        public void finish() {
            super.finish();
            overridePendingTransition(0, 0);
        }

        @Override
        protected void onDestroy() {
            images = null;
            backgroundColorRes = -1;
            placeHolderImageRes = -1;
            super.onDestroy();
        }

        @Override
        public boolean onKeyDown(int keyCode, KeyEvent event) {
            if (keyCode == KeyEvent.KEYCODE_BACK){
                if (contentView.isEnabled()){
                    onFinish();
                }
                return true;
            }
            return super.onKeyDown(keyCode, event);
        }
    }




}
