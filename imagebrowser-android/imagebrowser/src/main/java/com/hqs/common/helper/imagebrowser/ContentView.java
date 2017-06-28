package com.hqs.common.helper.imagebrowser;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.RelativeLayout;

/**
 * Created by super on 2017/1/23.
 */

public class ContentView extends RelativeLayout {

    private InterceptListener interceptListener;

    public ContentView(Context context) {
        super(context);
    }

    public ContentView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {

        if (interceptListener == null){
            if (this.isEnabled()){
                return super.onInterceptTouchEvent(ev);
            } else {
                return true;
            }
        }
        else{
            return interceptListener.onInterceptTouchEvent(ev);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (this.isEnabled()){
            return super.onTouchEvent(event);
        }
        else{
            return true;
        }
    }

    public void setInterceptListener(InterceptListener interceptListener) {
        this.interceptListener = interceptListener;
    }
}

interface InterceptListener {
    boolean onInterceptTouchEvent(MotionEvent ev);
}