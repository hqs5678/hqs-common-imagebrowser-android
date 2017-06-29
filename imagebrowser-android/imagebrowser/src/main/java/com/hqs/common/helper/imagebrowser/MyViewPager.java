package com.hqs.common.helper.imagebrowser;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import com.hqs.common.utils.Log;

import static com.hqs.common.helper.imagebrowser.ImageBrowser.TOUCH_OFFSET;

/**
 * Created by super on 2017/6/28.
 */

public class MyViewPager extends ViewPager {

    private float startX;
    private float sx;

    public MyViewPager(Context context) {
        super(context);
    }

    public MyViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        if (isEnabled()){
            return super.onInterceptTouchEvent(ev);
        }
        else{
            return false;
        }
    }


    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {

        switch (ev.getAction()){
            case MotionEvent.ACTION_DOWN:
                sx = getScrollX();
                startX = ev.getX();

                Log.print(startX);
                break;
            case MotionEvent.ACTION_MOVE:
                if (ev.getX() > startX){
                    if (getScrollX() == sx && startX + TOUCH_OFFSET < ev.getX()){
//                        Log.print("dispatchTouchEvent view pager");
                        setEnabled(false);
                        ((ViewGroup) this.getParent()).onInterceptTouchEvent(ev);
                        return false;
                    }
                }

                break;
        }
        return super.dispatchTouchEvent(ev);
    }

}
