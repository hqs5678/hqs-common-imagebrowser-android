package com.hqs.common.helper.imagebrowser;

import android.app.Activity;
import android.content.Intent;
import android.graphics.RectF;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewCompat;
import android.support.v4.view.ViewPager;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
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
import java.util.HashMap;

/**
 * Created by apple on 2016/11/3.
 */

public class ImageBrowser {


    public static int placeHolderImageRes = -1;
    public static int backgroundColorRes = -1;
    public static int animDuration = 200;
    private static ArrayList<QImage> images;
    private static final int TOUCH_OFFSET = 40;

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
        private MyViewPager viewPager;
        private TextView tvIndex;
        private Handler mHandler;
        private MyPageAdapter adapter;
        private float sw;
        private float sh;
        private ImageView.ScaleType scaleType = ImageView.ScaleType.FIT_CENTER;
        private float startY;
        private float viewY;
        private boolean animating = false;
        private int dismissOffset = 0;

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
            dismissOffset = (int) (sw * 0.26);

            mHandler = new Handler();
            filePaths = extras.getStringArrayList("filePaths");
            currentIndex = extras.getInt("currentIndex");

            if (filePaths == null && images != null) {
                filePaths = new ArrayList<>();
                for (QImage image : images){
                    filePaths.add(image.filePath);
                }
            }

            contentView = (ContentView) LayoutInflater.from(this).inflate(R.layout.dialog_photo_browser, null);
            contentView.setEnabled(false);
            bgView = (RelativeLayout) contentView.findViewById(R.id.content_bg_view);
            if (backgroundColorRes != -1){
                bgView.setBackgroundResource(backgroundColorRes);
            }
            this.setContentView(contentView);

            tvIndex = (TextView) contentView.findViewById(R.id.tv_index);
            tvIndex.setText(currentIndex + 1 + "/" + filePaths.size());
            viewPager = (MyViewPager) contentView.findViewById(R.id.viewPager);

            contentView.setInterceptListener(new InterceptListener() {
                @Override
                public boolean onInterceptTouchEvent(MotionEvent ev) {
                    if (contentView.isEnabled() == false){
                        return true;
                    }

                    if (ev.getPointerCount() == 1){
                        PhotoView photoView = adapter.getView(viewPager.getCurrentItem());
                        Info info = photoView.getInfo();

                        try {
                            Class cls = info.getClass();
                            Field field = cls.getDeclaredField("mScale");
                            field.setAccessible(true);

                            float scale = (float) field.get(info);
                            if (scale == 1.0){
                                return onGesture(ev);
                            }

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                    return false;
                }

                @Override
                public boolean onTouchEvent(MotionEvent event) {
                    return true;
                }
            });

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
            imageView.setImageDrawable(srcImgView.getDrawable());
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
                }
            }, animDuration + 50);

            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    Animation fade = AnimationUtils.loadAnimation(ImageActivity.this, android.R.anim.fade_out);
                    fade.setDuration(100);
                    fade.setFillAfter(true);
                    fade.setAnimationListener(new Animation.AnimationListener() {
                        @Override
                        public void onAnimationStart(Animation animation) {

                        }

                        @Override
                        public void onAnimationEnd(Animation animation) {
                            contentView.removeView(imageView);
                            contentView.setEnabled(true);
                        }

                        @Override
                        public void onAnimationRepeat(Animation animation) {

                        }
                    });
                    imageView.clearAnimation();
                    imageView.setAnimation(fade);
                    fade.start();
                }
            }, animDuration + 200);

            Animation fade = AnimationUtils.loadAnimation(this, android.R.anim.fade_in);
            fade.setDuration(animDuration);

            bgView.setAnimation(fade);



        }

        private void setupViewPager() {
            adapter = new MyPageAdapter();
            viewPager.setAdapter(adapter);

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
            // finish
            animating = true;
            viewPager.setY(-1);
            viewPager.postOnAnimation(new AnimActionOut(viewPager, bgView));
        }

        private class AnimAction implements Runnable {
            View slideView;
            View fadeView;
            int step;
            final int minStep = 3;

            public AnimAction(View slideView, View fadeView) {
                this.slideView = slideView;
                this.fadeView = fadeView;
            }
            @Override
            public void run() {

            }

            public void fade(int y){
                float alpha = 1.0f - Math.abs(y / sh);
                fadeView.setAlpha(alpha);
            }
        }

        private class AnimActionOut extends AnimAction {

            public AnimActionOut(View slideView, View fadeView) {
                super(slideView, fadeView);

                int y = (int) this.slideView.getY();
                float s;
                if (y > 0){
                    s = (sh - y) / 6;
                }
                else{
                    s = -(this.slideView.getBottom() + this.slideView.getY()) / 6;
                }

                if (Math.abs(s) < minStep){
                    if (y < 0){
                        step = -minStep;
                    }
                    else{
                        step = minStep;
                    }
                }
                else{
                    step = (int) s;
                }
            }
            @Override
            public void run() {
                int y = (int) (slideView.getY() + step);
                if (step > 0){
                    if (y > sh){
                        y = (int) sh;
                    }
                }
                else{
                    if (y < -sh){
                        y = (int) -sh;
                    }
                }
                slideView.setY(y);
                fade(y);
                if (Math.abs(slideView.getY()) != sh && animating){
                    ViewCompat.postOnAnimationDelayed(slideView, new AnimActionOut(slideView, fadeView), 10);
                }
                else{
                    onAnimationStop();
                }
            }

        }

        private class AnimActionBack extends AnimAction {


            public AnimActionBack(View slideView, View fadeView) {
                super(slideView, fadeView);

                float s = -slideView.getY() / 6;
                if (Math.abs(s) < minStep){
                    if (s < 0){
                        step = -minStep;
                    }
                    else{
                        step = minStep;
                    }
                }
                else{
                    step = (int) s;
                }
            }
            @Override
            public void run() {
                int y = (int) (slideView.getY() + step);

                if (Math.abs(y) < Math.abs(step)){
                    y = 0;
                }
                slideView.setY(y);
                fade(y);
                if (slideView.getY() != 0 && animating){
                    ViewCompat.postOnAnimationDelayed(slideView, new AnimActionBack(slideView, fadeView), 10);
                }
            }
        }

        private void onAnimationStop() {
            finish();
        }

        private boolean onGesture(MotionEvent ev) {

            switch (ev.getAction()){
                case MotionEvent.ACTION_DOWN:
                    startY = ev.getY();
                    viewY = viewPager.getY();
                    animating = false;
                    break;
                case MotionEvent.ACTION_MOVE:
                    int top = (int) (ev.getY() - startY + viewY);
                    if (Math.abs(top) > TOUCH_OFFSET){
                        if (ev.getY() - startY > 0){
                            viewPager.setY(top - TOUCH_OFFSET);
                        }
                        else{
                            viewPager.setY(top + TOUCH_OFFSET);
                        }
                        viewPager.setEnabled(false);

                        float alpha = 1.0f - Math.abs((top - TOUCH_OFFSET) / sh);
                        bgView.setAlpha(alpha);
                    }

                    break;
                case MotionEvent.ACTION_UP:
                case MotionEvent.ACTION_CANCEL:
                    viewPager.setEnabled(true);
                    animating = true;
                    if (Math.abs(ev.getY() - startY) > dismissOffset){
                        viewPager.postOnAnimation(new AnimActionOut(viewPager, bgView));
                    }
                    else{
                        viewPager.postOnAnimation(new AnimActionBack(viewPager, bgView));
                    }

                    break;
            }

            return false;
        }

        class MyPageAdapter extends PagerAdapter {

            PhotoView destroyedView;
            private HashMap<Integer, PhotoView> cacheViews;

            public MyPageAdapter(){
                cacheViews = new HashMap<>();
            }

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

                PhotoView p;
                if (destroyedView != null){
                    p = destroyedView;
                    destroyedView = null;
                }
                else{
                    p = getView();
                }
                final PhotoView photoView = p;

                String path = filePaths.get(position);
                if (images == null){
                    Glide.with(ImageActivity.this)
                            .load(path)
                            .placeholder(placeHolderImageRes)
                            .into(photoView);
                }
                else{
                    Glide.with(ImageActivity.this)
                            .load(path)
                            .placeholder(images.get(position).srcImageView.getDrawable())
                            .into(photoView);
                }

                container.addView(photoView);
                cacheViews.put(position, photoView);

                return photoView;
            }

            @Override
            public void destroyItem(ViewGroup container, int position, Object object) {
                destroyedView = (PhotoView) object;
                container.removeView(destroyedView);
            }

            public PhotoView getView(int position){
                return cacheViews.get(position);
            }

            private PhotoView getView(){

                PhotoView photoView = new PhotoView(ImageActivity.this);

                // 启用图片缩放功能
                photoView.enable();
                photoView.setAnimaDuring(300);
                photoView.setMaxScale(6);
                photoView.setInterpolator(new DecelerateInterpolator());
                photoView.setScaleType(scaleType);
                return photoView;
            }
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
                    contentView.setEnabled(false);
                    onFinish();
                }
                return true;
            }
            return super.onKeyDown(keyCode, event);
        }
    }

}
