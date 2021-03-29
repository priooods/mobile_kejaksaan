package com.prio.kejaksaan.tools;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.viewpager.widget.ViewPager;

public class CustomViewpager extends ViewPager {

    public CustomViewpager(@NonNull Context context) {
        super(context);
    }

    public CustomViewpager(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        int height = 0;
        for (int i=0; i<getChildCount();i++){
            View child = getChildAt(i);
            child.measure(widthMeasureSpec, heightMeasureSpec);
            int h = child.getMeasuredHeight();
            if (h>height) height = h;
        }
        heightMeasureSpec = MeasureSpec.makeMeasureSpec(height,MeasureSpec.EXACTLY);
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }
}
