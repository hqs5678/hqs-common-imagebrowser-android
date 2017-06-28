package com.hqs.common.helper.imagebrowser;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

import com.hqs.common.utils.Log;

/**
 * Created by super on 2017/6/28.
 */

public class MyViewPager extends ViewPager {

    private float startX;

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
            return true;
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        if (isEnabled()){
            switch (ev.getAction()){
                case MotionEvent.ACTION_DOWN:
                    startX = ev.getX();
                    break;
                case MotionEvent.ACTION_MOVE:
                    if (ev.getX() - startX > 0){
                        if (getScrollX() == 0){
                            setEnabled(false);
                            Log.print(ev.getX());
                            return true;
                        }
                    }
                    break;
                case MotionEvent.ACTION_UP:
                case MotionEvent.ACTION_CANCEL:
                    setEnabled(true);
                    break;
            }
            return super.onTouchEvent(ev);
        }
        else{
            return true;
        }
    }


}
