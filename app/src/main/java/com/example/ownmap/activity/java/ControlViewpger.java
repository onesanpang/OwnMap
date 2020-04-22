package com.example.ownmap.activity.java;

import android.content.Context;

import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;

import androidx.viewpager.widget.ViewPager;

/**
 * Created by KID on 2018/11/14.
 * 可动态禁止（允许）左滑/右滑的viewpager
 */
public class ControlViewpger  extends ViewPager {
    //true  是不可滑动    false  是可滑动
    private boolean noScroll = true;

    public ControlViewpger(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ControlViewpger(Context context) {
        super(context);
    }

    public void setNoScroll(boolean noScroll) {
        this.noScroll = noScroll;
    }

    @Override
    public void scrollTo(int x, int y) {
        super.scrollTo(x, y);
    }

    @Override
    public boolean onTouchEvent(MotionEvent arg0) {
        if (noScroll)
            return false;
        else
            return super.onTouchEvent(arg0);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent arg0) {
        if (noScroll)
            return false;
        else
            return super.onInterceptTouchEvent(arg0);
    }

    @Override
    public void setCurrentItem(int item, boolean smoothScroll) {
        super.setCurrentItem(item, smoothScroll);
    }

    @Override
    public void setCurrentItem(int item) {
        super.setCurrentItem(item);
    }
}