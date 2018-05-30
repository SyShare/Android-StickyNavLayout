package com.zhy.stickynavlayout.view;


import android.content.Context;
import android.util.AttributeSet;
import android.widget.FrameLayout;

import com.zhy.stickynavlayout.AbViewUtil;


/**
 * 直播间视频区域布局。
 * <p>
 * <p>
 * created by ice at 17/30/18
 */
public class LiveVideoLayout extends FrameLayout {
    /**
     * (width / height)
     */
    private float aspectRatio = 0.75f;

    private float mOtherHeightDp = 145 + 8 + 32;

    private int mMW, mMH;

    public LiveVideoLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }


    public void setAspectRatio(float aspectRatio) {
        if (aspectRatio == 0) {
            return;
        }
        this.aspectRatio = aspectRatio;
        invalidate();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        if (mMW > 0 && mMH > 0) {
            super.onMeasure(mMW, mMH);
            return;
        }
        int receivedWidth = MeasureSpec.getSize(widthMeasureSpec);
        int h = (int) (receivedWidth / aspectRatio);
        int screenHeight = getResources().getDisplayMetrics().heightPixels;
        int otherHeight = (int) (getResources().getDisplayMetrics().density * mOtherHeightDp);
        int avHeight = screenHeight - otherHeight;
        if (h > avHeight) {
            h = avHeight;
        }
        mMW = MeasureSpec.makeMeasureSpec((int) (h * aspectRatio), MeasureSpec.EXACTLY);
        mMH = MeasureSpec.makeMeasureSpec(h, MeasureSpec.EXACTLY) - AbViewUtil.dip2px(10);
//        }
        super.onMeasure(mMW, mMH);
    }
}