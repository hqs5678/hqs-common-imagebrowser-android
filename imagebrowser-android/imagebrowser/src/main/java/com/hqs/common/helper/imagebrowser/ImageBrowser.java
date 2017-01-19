package com.hqs.common.helper.imagebrowser;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.widget.TextView;
import android.widget.Toast;

import com.bm.library.PhotoView;
import com.bumptech.glide.Glide;
import com.hqs.common.utils.StatusBarUtil;

import java.util.ArrayList;

/**
 * Created by apple on 2016/11/3.
 */

public class ImageBrowser {


    public static int placeHolderImageRes = -1;
    public static int backgroundColorRes = -1;

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

        }
    }


    public static class ImageActivity extends Activity {

        private ArrayList<String> filePaths;
        private int currentIndex;

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);

            StatusBarUtil.transparencyBar(this);

            Bundle extras = this.getIntent().getExtras();

            if (extras == null){
                finish();
                return;
            }
            filePaths = extras.getStringArrayList("filePaths");
            currentIndex = extras.getInt("currentIndex");

            final View view = LayoutInflater.from(this).inflate(R.layout.dialog_photo_browser, null);
            if (backgroundColorRes != -1){
                view.setBackgroundResource(backgroundColorRes);
            }
            this.setContentView(view);

            final TextView tvIndex = (TextView) view.findViewById(R.id.tv_index);
            tvIndex.setText(currentIndex + 1 + "/" + filePaths.size());

            ViewPager viewPager = (ViewPager) view.findViewById(R.id.viewPager);
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

                            photoView.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    ImageActivity.this.finish();
                                }
                            });

                            views.add(photoView);
                        }

                    }
                    photoView = views.get(position % maxViews);

                    String path = filePaths.get(position);
                    Glide.with(ImageActivity.this).load(path).placeholder(placeHolderImageRes).into(photoView);

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
    }


}
