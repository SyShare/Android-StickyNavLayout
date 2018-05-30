package com.zhy.stickynavlayout;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.support.v4.view.animation.FastOutLinearInInterpolator;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;

/**
 * Created by roya on 16/6/9.
 */
public class LiveTouchListener implements View.OnTouchListener {

    private static final int MODE_NONE = 0;
    private static final int MODE_VERTICAL = 1;
    private static final int MODE_HORIZONTAL = 2;
    private int mode = MODE_NONE;

    /* 双击的最大间隔 */
    private static final int VALUE_DOUBLE_CLICK_INTERVAL = 300;

    private boolean disable;

    float minMove;

    float viewX, viewY;
    float lastX, lastY;
    float oX, oY;

    private View view;
    private float minTrans;
    private long mLastClickTime;

    private HorizonMovingListener horizonMovingListener;
    private VerticalMovingListener verticalMovingListener;
    private DoubleClickListener mDoubleClickListener;
    private VelocityTracker velocityTracker;

    public LiveTouchListener(View view) {
        this.view = view;
        minMove = AbViewUtil.dip2px(6f);
        minTrans = AbViewUtil.dip2px(60);
    }

    public void setHorizonMovingListener(HorizonMovingListener movingListener) {
        this.horizonMovingListener = movingListener;
    }

    public void setVerticalMovingListener(VerticalMovingListener movingListener) {
        this.verticalMovingListener = movingListener;
    }

    public void setDoubleClickListener(DoubleClickListener doubleClickListener) {
        mDoubleClickListener = doubleClickListener;
    }

    private void acquireVelocityTracker(final MotionEvent event) {
        if (null == velocityTracker) {
            velocityTracker = VelocityTracker.obtain();
        }
        velocityTracker.addMovement(event);
    }

    public void setDisable(boolean disable) {
        this.disable = disable;
    }

    @Override
    public boolean onTouch(View v, MotionEvent ev) {
        if (disable) {
            if (view.getX() != 0) {
                view.setX(0);
            }
            return false;
        }

        acquireVelocityTracker(ev);
        if (ev.getAction() == MotionEvent.ACTION_DOWN) {
            view.animate().cancel();
            mode = MODE_NONE;

            viewX = view.getX();
            viewY = view.getY();
            lastX = ev.getX();
            lastY = ev.getY();
            oX = ev.getX();
            oY = ev.getY();

        } else if (ev.getAction() == MotionEvent.ACTION_MOVE) {
            float dx = ev.getX() - lastX;
            float dy = ev.getY() - lastY;
            if (Math.abs(dx) + Math.abs(dy) > 8) {
                mLastClickTime = 0;
            }

            if (MODE_VERTICAL == mode) {
                if (verticalMovingListener != null) {
                    verticalMovingListener.onMoving(ev.getY() - lastY);
                }

                velocityTracker.computeCurrentVelocity(1000, ViewConfiguration.getMaximumFlingVelocity());
                lastX = ev.getX();
                lastY = ev.getY();
                return true;
            }

            if (MODE_HORIZONTAL == mode) {
                float tx = view.getX() + dx;
                if (tx < 0)
                    tx = 0;
                if (tx > view.getWidth() - 1)
                    tx = view.getWidth() - 1;

                view.setX(tx);
            }

            if (MODE_NONE == mode) {
                if (Math.abs(ev.getX() - oX) >= minMove) {
                    mode = MODE_HORIZONTAL;
                } else if (Math.abs(ev.getY() - oY) >= minMove) {
                    mode = MODE_VERTICAL;
                    if (verticalMovingListener != null) {
                        verticalMovingListener.onStart();
                    }
                }
            }


            lastX = ev.getX();
            lastY = ev.getY();
            return true;
        } else if (ev.getAction() == MotionEvent.ACTION_UP) {
            long currentTime = System.currentTimeMillis();
            if (currentTime - mLastClickTime < VALUE_DOUBLE_CLICK_INTERVAL) {
                mLastClickTime = 0;
                if (mDoubleClickListener != null) {
                    mDoubleClickListener.onDoubleClick((int) ev.getX(), (int) ev.getY());
                }
            } else {
                mLastClickTime = currentTime;
            }

            if (MODE_HORIZONTAL == mode) {
                if ((viewX == 0 && view.getX() >= minTrans) || view.getX() > (view.getWidth() - minTrans)) {
                    view.animate()
                            .x(view.getWidth() - 1)
                            .setInterpolator(new FastOutLinearInInterpolator())
                            .setListener(new AnimatorListenerAdapter() {
                                @Override
                                public void onAnimationEnd(Animator animation) {
                                    super.onAnimationEnd(animation);
                                    if (horizonMovingListener != null) {
                                        horizonMovingListener.onFlingToHide();
                                    }
                                }
                            })
                            .start();
                } else {
                    view.animate()
                            .x(0)
                            .setInterpolator(new FastOutLinearInInterpolator())
                            .setListener(new AnimatorListenerAdapter() {
                                @Override
                                public void onAnimationEnd(Animator animation) {
                                    super.onAnimationEnd(animation);
                                    if (horizonMovingListener != null) {
                                        horizonMovingListener.onFlingToShow();
                                    }
                                }
                            })
                            .start();
                }
            } else if (MODE_VERTICAL == mode) {
                if (verticalMovingListener != null) {
                    verticalMovingListener.onUp(velocityTracker.getYVelocity());
                }
            }

            velocityTracker.clear();
            velocityTracker.recycle();
            velocityTracker = null;
            return MODE_NONE != mode;
        }

        return false;
    }

    public void showView() {
        if (view == null) {
            return;
        }
        view.animate()
                .x(0)
                .setInterpolator(new FastOutLinearInInterpolator())
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        super.onAnimationEnd(animation);
                        if (horizonMovingListener != null) {
                            horizonMovingListener.onFlingToShow();
                        }
                    }
                })
                .start();
    }

    public interface VerticalMovingListener {
        void onStart();

        void onMoving(float dy);

        void onUp(float yVelocity);
    }

    public interface HorizonMovingListener {
        void onFlingToHide();

        void onFlingToShow();
    }

    public interface DoubleClickListener {
        void onDoubleClick(int x, int y);
    }
}
