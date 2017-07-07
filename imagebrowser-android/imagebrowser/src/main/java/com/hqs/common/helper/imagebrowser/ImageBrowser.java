package com.hqs.common.helper.imagebrowser;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Rect;
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
 *
 *
 * 注意事项:
 *      在 app/proguard-rules.pro 中添加如下:
 *      -keep class com.bm.library.** {*;}
 *
 */



public class ImageBrowser {


    public static int placeHolderImageRes = -1;
    public static int backgroundColorRes = -1;
    public static int animDuration = 200;
    private static ArrayList<QImage> images;
    public static final int TOUCH_OFFSET = 40;

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
        private MyPageAdapter adapter;
        private float sw;
        private float sh;
        private ImageView.ScaleType scaleType = ImageView.ScaleType.FIT_CENTER;
        private float startY;
        private boolean animating = false;
        private int dismissOffset = 0;
        private float startX;
        private int orientation = -1;
        private boolean onTouching = false;
        private int sign = 1;
        // 垂直动画时图片的高度(上空白高度 + 图片有效高度)
        private HashMap<String, Float> heights = new HashMap<>();

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
            dismissOffset = (int) (sw * 0.3);

            mHandler = new Handler();
            filePaths = extras.getStringArrayList("filePaths");
            currentIndex = extras.getInt("currentIndex");

            if (filePaths == null && images != null) {
                filePaths = new ArrayList<>();
                for (QImage image : images){
                    filePaths.add(image.filePathOrUrl);
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
            viewPager = (ViewPager) contentView.findViewById(R.id.viewPager);

            contentView.setInterceptListener(new InterceptListener() {
                @Override
                public boolean onInterceptTouchEvent(MotionEvent ev) {
                    if (contentView.isEnabled() == false){
                        return true;
                    }

                    if (ev.getPointerCount() == 1){
                        PhotoView photoView = adapter.getView(viewPager.getCurrentItem());

                        float scale = getScale(photoView);
                        if (scale == 1.0) {
                            return onGesture(ev);
                        }
                    }
                    return false;
                }
            });

            setup();
        }

        private void setup(){

            // 先完成动画, 再显示所有, 设置viewpager
            if (images != null && images.size() > 0) {
                final ImageView imgView = images.get(currentIndex).srcImageView;
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

        private void setValueForInfo(Info info, String key, Object value){
            try {
                Class cls = info.getClass();
                Field f = cls.getDeclaredField(key);
                f.setAccessible(true);
                f.set(info, value);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        private void addAnimationEnter(RectF rectF, ImageView srcImgView){
            final PhotoView imageView = new PhotoView(this);
            imageView.setImageDrawable(srcImgView.getDrawable());
            imageView.setBackground(srcImgView.getBackground());
            imageView.setScaleType(scaleType);

            RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams((int) sw, (int) sh);
            imageView.setLayoutParams(layoutParams);

            contentView.addView(imageView);

            Info info = imageView.getInfo();
            setValueForInfo(info, "mRect", rectF);
            setValueForInfo(info, "mImgRect", rectF);
            setValueForInfo(info, "mBaseRect", rectF);
            setValueForInfo(info, "mWidgetRect", rectF);
            setValueForInfo(info, "mScaleType", srcImgView.getScaleType());

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
            orientation = 0;
            viewPager.setY(-1);
            viewPager.postOnAnimation(new AnimActionOut());
        }

        private float getScale(PhotoView p){
            try {
                Info info = p.getInfo();
                Class cls = info.getClass();
                Field field = cls.getDeclaredField("mScale");
                field.setAccessible(true);
                float scale = (float) field.get(info);
                return scale;
            } catch (Exception e) {
                e.printStackTrace();
                return 1;
            }
        }

        private int getOrientation(PhotoView p){
            try {
                Info info = p.getInfo();
                Class cls = info.getClass();
                Field field = cls.getDeclaredField("mDegrees");
                field.setAccessible(true);
                float mDegrees = (float) field.get(info);
                if (mDegrees % 180 == 0){
                    // vertical
                    return 0;
                }
                else{
                    // horizontal
                    return 1;
                }

            } catch (Exception e) {
                e.printStackTrace();
                // vertical
                return 0;
            }
        }

        private float getH(float height){
            if (height <= 0){
                PhotoView p = adapter.getView(viewPager.getCurrentItem());
                float scale = getScale(p);
                int ort = getOrientation(p);
                Float f = heights.get(viewPager.getCurrentItem() + "-" + ort + "-" + scale);
                if (f != null){
                    height = f;
                }
                if (height > 0){
                    return height;
                }
                // 重新计算
//                Log.print("getH  calculate ");
                try {
                    if (ort == 0){
                        // orientation == vertical

                        Rect rect = p.getDrawable().getBounds();
                        float h = scale * rect.height() * sw / rect.width();
                        if (h > sh){
                            height = sh;
                        }
                        else{
                            height = (sh - h) * 0.5f + h;
                        }
                    }
                    else{
                        // orientation == horizontal

                        float h = scale * sw;
                        if (h > sh){
                            height = sh;
                        }
                        else{
                            height = (sh - h) * 0.5f + h;
                        }
                    }

                } catch (Exception e) {
                    height = sh;
                }
                heights.put(viewPager.getCurrentItem() + "-" + ort + "-" + scale, height);
            }
            return height;
        }

        // 动画基类
        private class AnimAction implements Runnable {

            public float k = 6;
            int step;
            final int minStep = 3;

            public AnimAction() {
            }
            @Override
            public void run() {

            }
        }

        // 甩出动画
        private class AnimActionOut extends AnimAction {

            float height = 0;

            public AnimActionOut(float height){
                this.height = height;
                init();
            }

            public AnimActionOut() {
                super();
                init();
            }

            private void init(){
                int position;
                float s;
                if (orientation == 0){
                    height = getH(height);

                    position = (int) viewPager.getY();
                    if (position > 0){
                        s = (height - position) / k;
                    }
                    else{
                        s = -(height + position) / k;
                    }
                }
                else{
                    position = (int) viewPager.getX();
                    if (position > 0){
                        s = (sw - position) / k;
                    }
                    else{
                        s = -(viewPager.getRight() + position) / k;
                    }
                }

                if (Math.abs(s) < minStep){
                    if (position < 0){
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
                if (orientation == 0){
                    int y = (int) (viewPager.getY() + step);
                    if (step > 0){
                        if (y > height){
                            y = (int) height;
                        }
                    }
                    else{
                        if (y < -height){
                            y = (int) -height;
                        }
                    }
                    viewPager.setY(y);
                    fadeY(y);
                    if (Math.abs(y) != (int) height && animating){
                        ViewCompat.postOnAnimationDelayed(viewPager, new AnimActionOut(height), 10);
                    }
                    else{
                        onAnimationStop();
                    }
                }
                else{
                    int x = (int) (viewPager.getX() + step);
                    if (step > 0){
                        if (x > sw){
                            x = (int) sw;
                        }
                    }
                    else{
                        if (x < -sw){
                            x = (int) -sw;
                        }
                    }
                    viewPager.setX(x);
                    fadeX(x);
                    if (Math.abs(x) != (int) sw && animating){
                        ViewCompat.postOnAnimationDelayed(viewPager, new AnimActionOut(), 10);
                    }
                    else{
                        onAnimationStop();
                    }
                }

            }

        }

        // 弹回动画
        private class AnimActionBack extends AnimAction {


            public AnimActionBack() {
                super();

                float s;
                if (orientation == 0){
                    s = -viewPager.getY() / k;
                }
                else{
                    s = -viewPager.getX() / k;
                }

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
                if (orientation == 0){
                    int y = (int) (viewPager.getY() + step);

                    if (Math.abs(y) < Math.abs(step)){
                        y = 0;
                    }
                    viewPager.setY(y);
                    fadeY(y);
                    if (viewPager.getY() != 0 && animating){
                        ViewCompat.postOnAnimationDelayed(viewPager, new AnimActionBack(), 10);
                    }
                    else{
                        orientation = -1;
                    }
                }
                else{
                    int x = (int) (viewPager.getX() + step);

                    if (Math.abs(x) < Math.abs(step)){
                        x = 0;
                    }
                    viewPager.setX(x);
                    fadeX(x);
                    if (viewPager.getX() != 0 && animating){
                        ViewCompat.postOnAnimationDelayed(viewPager, new AnimActionBack(), 10);
                    }
                    else{
                        orientation = -1;
                    }
                }
            }
        }

        private boolean onGesture(MotionEvent ev) {

            switch (ev.getAction()){
                case MotionEvent.ACTION_DOWN:
                    startY = ev.getY();
                    startX = ev.getX();
                    animating = false;
                    break;

                case MotionEvent.ACTION_MOVE:

                    if (viewPager.getCurrentItem() == 0 && ev.getX() - startX > 0
                            || viewPager.getCurrentItem() + 1 == adapter.getCount() && ev.getX() - startX < 0){
                        viewPager.setEnabled(false);
                    }

                    if (!moveVertical(ev)){
                        moveHorizontal(ev);
                    }

                    break;

                case MotionEvent.ACTION_UP:
                case MotionEvent.ACTION_CANCEL:

                    viewPager.setEnabled(true);
                    animating = true;
                    if (orientation == 0){
                        if (Math.abs(ev.getY() - startY) > dismissOffset) {
                            viewPager.postOnAnimation(new AnimActionOut());
                        } else {
                            viewPager.postOnAnimation(new AnimActionBack());
                        }
                    }
                    else{
                        if (Math.abs(ev.getX() - startX) > dismissOffset * 0.8) {
                            viewPager.postOnAnimation(new AnimActionOut());
                        } else {
                            viewPager.postOnAnimation(new AnimActionBack());
                        }
                    }
                    onTouching = false;

                    break;
            }

            return false;
        }

        private boolean moveVertical(MotionEvent ev){
            int top = (int) (ev.getY() - startY);
            if (Math.abs(top) > TOUCH_OFFSET && !onTouching && orientation != 1){
                viewPager.setEnabled(false);
                orientation = 0;
                onTouching = true;

                if (top > 0){
                    sign = -1;
                }
                else{
                    sign = 1;
                }
            }
            if (onTouching){
                int y = top + TOUCH_OFFSET * sign;

                viewPager.setY(y);
                fadeY(y);
            }
            return orientation == 0;
        }
        private void moveHorizontal(MotionEvent ev){
            int left = (int) (ev.getX() - startX);
            if ((viewPager.getCurrentItem() == 0 && left > 0 && Math.abs(left) > TOUCH_OFFSET)
                    || (viewPager.getCurrentItem() + 1 == adapter.getCount()
                        && left < 0 && Math.abs(left) > TOUCH_OFFSET)){
                viewPager.setEnabled(false);
                orientation = 1;
                int x;
                if (left > 0){
                    x = left - TOUCH_OFFSET;
                }
                else{
                    x = left + TOUCH_OFFSET;
                }
                viewPager.setX(x);
                fadeX(x);
            }
        }

        private void fadeY(int y){
            float height = getH(0);
            float alpha = 1.0f - Math.abs(y / height);
            bgView.setAlpha(alpha);
            tvIndex.setAlpha(alpha);
        }

        private void fadeX(int x){
            float alpha = 1.0f - Math.abs(x / sw);
            bgView.setAlpha(alpha);
            tvIndex.setAlpha(alpha);
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

                PhotoView photoView;
                if (destroyedView != null){
                    photoView = destroyedView;
                    destroyedView = null;
                }
                else{
                    photoView = getView();
                }

                String path = filePaths.get(position);
                if (images == null){
                    Glide.with(ImageActivity.this)
                            .load(path)
                            .dontAnimate()
                            .placeholder(placeHolderImageRes)
                            .into(photoView);
                }
                else{
                    Glide.with(ImageActivity.this)
                            .load(path)
                            .dontAnimate()
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
                photoView.setMaxScale(5);
                photoView.setInterpolator(new DecelerateInterpolator());
                photoView.setScaleType(scaleType);
                return photoView;
            }
        }

        private void onAnimationStop() {
            finish();
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
