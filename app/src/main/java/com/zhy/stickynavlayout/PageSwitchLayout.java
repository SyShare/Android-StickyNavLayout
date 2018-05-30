package com.zhy.stickynavlayout;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.PointF;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;


/**
 * Created by roya on 2017/10/2.
 */

public class PageSwitchLayout extends ScrimInsetsLinearLayout {
    public PageSwitchLayout(Context context) {
        this(context, null);
    }

    public PageSwitchLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public PageSwitchLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private SwitchCallback switchCallback;

    private float lastY = 0;
    private int minTouchSlop = 0;
    private PointF firstPoint = null;
    private VelocityTracker mVelocityTracker;
    private ValueAnimator scrollAnimator;

    private boolean draggingY = false;
    private boolean draggingX = false;

    private boolean enableSwitcher = true;

    private void init() {
        minTouchSlop = ViewConfiguration.get(getContext()).getScaledTouchSlop();
    }

    public void setSwitchCallback(SwitchCallback switchCallback) {
        this.switchCallback = switchCallback;
    }

    public void enableSwitcher2() {
        enableSwitcher = true;
    }

    public void disableSwitcher2() {
        enableSwitcher = false;
        fling();
    }

    public void enableSwitcher() {
        enableSwitcher = true;
    }

    public void disableSwitcher() {
        enableSwitcher = false;
        fling();
    }

    public void switchToTop() {
        scrollWithAnimation(0);
        if (switchCallback != null) {
            switchCallback.onSwitch(this, false);
        }

        enableSwitcher = true;
    }

    public void switchToEnd() {
        scrollWithAnimation(getScrollMax());
        if (switchCallback != null) {
            switchCallback.onSwitch(this, true);
        }

        enableSwitcher = false;
    }

    private int getScrollMax() {
        int scrollMax = 0;
        for (int i = 0; i < getChildCount(); i++) {
            View c = getChildAt(i);
            scrollMax += c.getHeight();
        }
        scrollMax -= getHeight();


        return scrollMax;
    }

    private void scrollTo(int y) {
        if (y < 0) {
            y = 0;
        } else if (y > getScrollMax()) {
            y = getScrollMax();
        }

        super.scrollTo(0, y);
    }

    private void fling() {
        if (mVelocityTracker == null) {
            return;
        }

        mVelocityTracker.computeCurrentVelocity(1000);
        float v = mVelocityTracker.getYVelocity();

        mVelocityTracker.recycle();
        mVelocityTracker = null;

        if (getScrollY() == 0 || getScrollY() == getScrollMax()) {
            return;
        }

        if (v > 0 || getScrollY() < AbViewUtil.dip2px(56)) {
            switchToTop();
        } else if (v < 0) {
            switchToEnd();
        } else {
            final DisplayMetrics displayMetrics = getContext().getResources().getDisplayMetrics();
            final int half = displayMetrics.heightPixels / 2;
            final int scrollY = getScrollY();
            if (scrollY < half) {
                switchToTop();
            } else {
                switchToEnd();
            }
        }
    }

    private void scrollWithAnimation(int y) {
        scrollAnimator = ValueAnimator.ofInt(getScrollY(), y);
        scrollAnimator.setInterpolator(new DecelerateInterpolator());
        scrollAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                int v = (int) animation.getAnimatedValue();
                scrollTo(0, v);
            }
        });
        scrollAnimator.setDuration(300);
        scrollAnimator.start();
    }

    private void stopScrollWithAnimation() {
        if (scrollAnimator != null) {
            scrollAnimator.cancel();
            scrollAnimator = null;
        }
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        Log.d("LiveScrollTest", "PageSwitchLayout dispatchTouchEvent enableSwitcher:" + enableSwitcher);
        if (!enableSwitcher) {
            if (ev.getAction() == MotionEvent.ACTION_DOWN) {
                draggingX = false;
                draggingY = false;
            }
            return super.dispatchTouchEvent(ev);
        }

        if (mVelocityTracker == null) {
            mVelocityTracker = VelocityTracker.obtain();
        }
        mVelocityTracker.addMovement(ev);

        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN: {
                draggingX = false;
                draggingY = false;

                stopScrollWithAnimation();
                lastY = ev.getY();
                firstPoint = new PointF(ev.getX(), ev.getY());
                break;
            }
            case MotionEvent.ACTION_MOVE: {
                if (draggingX) {

                } else if (draggingY) {
                    scrollTo((int) (getScrollY() + (lastY - ev.getY())));
                    lastY = ev.getY();
                } else {
                    if (Math.abs(firstPoint.y - ev.getY()) >= minTouchSlop) {
                        draggingY = true;
                        draggingX = false;
                    } else if (Math.abs(firstPoint.x - ev.getX()) >= minTouchSlop) {
                        draggingX = true;
                        draggingY = false;
                    }
                }
                break;
            }
            case MotionEvent.ACTION_UP: {
                fling();
                draggingY = false;
                draggingX = false;
                break;
            }
            default:
                break;
        }
        return super.dispatchTouchEvent(ev);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        Log.d("LiveScrollTest", "PageSwitchLayout onInterceptTouchEvent result:" + draggingY);
        return super.onInterceptTouchEvent(ev);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        Log.d("LiveScrollTest", "PageSwitchLayout onTouchEvent result:" + !draggingX);
        return super.onTouchEvent(event) || !draggingX;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        if (getChildCount() >= 1) {

            View child = getChildAt(getChildCount() - 1);
            final int desiredHeight = getMeasuredHeight();

            if (child.getMeasuredHeight() < desiredHeight) {
                final int childWidthMeasureSpec = getChildMeasureSpec(
                        widthMeasureSpec, 0, child.getLayoutParams().width);

                int childHeightMeasureSpec;
                if (child.getLayoutParams().height != LayoutParams.MATCH_PARENT) {
                    ViewGroup.LayoutParams lp = child.getLayoutParams();
                    childHeightMeasureSpec = getChildMeasureSpec(
                            desiredHeight, 0, lp.height);

                } else {
                    int paddingh = 0;
                    if (getInstes() != null) {
                        paddingh += getInstes().top;
                        paddingh += getInstes().bottom;
                    }

                    childHeightMeasureSpec = MeasureSpec.makeMeasureSpec(
                            desiredHeight - paddingh, MeasureSpec.EXACTLY);
                }

                child.measure(childWidthMeasureSpec, childHeightMeasureSpec);
            }
        }
    }

    public interface SwitchCallback {
        void onSwitch(PageSwitchLayout layout, boolean end);
    }
}
