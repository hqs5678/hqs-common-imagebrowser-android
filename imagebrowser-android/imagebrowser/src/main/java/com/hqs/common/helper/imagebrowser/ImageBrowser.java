package com.hqs.common.helper.imagebrowser;

import android.app.Activity;
import android.content.Intent;
import android.graphics.RectF;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.AnimationUtils;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.ScaleAnimation;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bm.library.PhotoView;
import com.bumptech.glide.Glide;
import com.hqs.common.utils.Log;
import com.hqs.common.utils.ScreenUtils;
import com.hqs.common.utils.StatusBarUtil;
import com.hqs.common.utils.ViewUtil;

import java.util.ArrayList;

/**
 * Created by apple on 2016/11/3.
 */

public class ImageBrowser {


    public static int placeHolderImageRes = -1;
    public static int backgroundColorRes = -1;
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
        private RelativeLayout contentView;
        private ViewPager viewPager;
        private TextView tvIndex;
        private Handler mHandler;

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);

            StatusBarUtil.transparencyBar(this);

            Bundle extras = this.getIntent().getExtras();

            if (extras == null){
                finish();
                return;
            }
            mHandler = new Handler();
            filePaths = extras.getStringArrayList("filePaths");
            currentIndex = extras.getInt("currentIndex");

            if (filePaths == null){
                filePaths = new ArrayList<>();
                for (QImage image : images){
                    filePaths.add(image.filePath);
                    ViewUtil.getViewRect(image.srcImageView, new ViewUtil.OnViewRectCallBack() {
                        @Override
                        public void onRect(RectF rectF) {
                            Log.print(rectF);
                        }
                    });
                }
            }

            contentView = (RelativeLayout) LayoutInflater.from(this).inflate(R.layout.dialog_photo_browser, null);
            if (backgroundColorRes != -1){
                contentView.setBackgroundResource(backgroundColorRes);
            }
            this.setContentView(contentView);
            contentView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    finish();
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
                final ImageView imgView = images.get(currentIndex).srcImageView;
                ViewUtil.getViewRect(imgView, new ViewUtil.OnViewRectCallBack() {
                    @Override
                    public void onRect(RectF rectF) {
                        addAnimation(rectF, imgView);
                    }
                });
            }



        }

        private void addAnimation(RectF rectF, ImageView srcImgView){
            final ImageView imageView = new ImageView(this);
            imageView.setImageDrawable(srcImgView.getDrawable());

            RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams((int) rectF.width(), (int) rectF.height());
            imageView.setLayoutParams(layoutParams);

            contentView.addView(imageView);

            float fx = rectF.left;
            float tx = 0;
            float fy = rectF.top;
            float h = rectF.width() * ScreenUtils.screenW(this) / rectF.height();
            float ty = (ScreenUtils.screenH(this) - h) * 0.5f;

            int duration = 300;
            Animation translateAnimation = new TranslateAnimation(fx, tx, fy, ty);
            translateAnimation.setDuration(duration);

            float scale = ScreenUtils.screenW(this)/rectF.width();


            ScaleAnimation scaleAnimation = new ScaleAnimation(1, scale, 1, scale, 0, 0);
            scaleAnimation.setDuration(duration);


            AnimationSet animationSet = new AnimationSet(true);
            animationSet.addAnimation(scaleAnimation);
            animationSet.addAnimation(translateAnimation);
            animationSet.setFillAfter(true);
            imageView.setAnimation(animationSet);
            animationSet.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {
                    contentView.setEnabled(false);
                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    setupViewPager();
                    mHandler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            contentView.removeView(imageView);
                        }
                    }, 200);

                }

                @Override
                public void onAnimationRepeat(Animation animation) {

                }
            });


            Animation fade = AnimationUtils.loadAnimation(this, android.R.anim.fade_in);
            fade.setDuration(duration);

            contentView.setAnimation(fade);



        }

        private void setupViewPager() {

            viewPager.setAdapter(new PagerAdapter() {

                ArrayList<PhotoView> views = new ArrayList<PhotoView>();
                int maxViews = 4;
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

                    PhotoView photoView;
                    if (views.size() == 0){
                        for (int i = 0; i< maxViews; i ++){
                            photoView = new PhotoView(ImageActivity.this);

                            // 启用图片缩放功能
                            photoView.enable();
                            photoView.setAnimaDuring(300);
                            photoView.setMaxScale(3);
                            photoView.setInterpolator(new DecelerateInterpolator());
                            photoView.setScaleType(ImageView.ScaleType.FIT_CENTER);

                            photoView.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    ImageActivity.this.finish();
                                    ImageActivity.this.overridePendingTransition(0, 0);
                                }
                            });

                            views.add(photoView);
                        }

                    }
                    photoView = views.get(position % maxViews);

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

            });

            viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
                @Override
                public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

                }

                @Override
                public void onPageSelected(int position) {
                    tvIndex.setText(position + 1 + "/" + filePaths.size());
                }

                @Override
                public void onPageScrollStateChanged(int state) {

                }
            });

            viewPager.setCurrentItem(currentIndex);
        }

        @Override
        public void finish() {
            super.finish();
            overridePendingTransition(0, 0);
        }
    }


}
