package com.zhy.stickynavlayout;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.animation.FastOutLinearInInterpolator;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;

import com.zhy.stickynavlayout.event.EventScroll;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

/**
 * Created by syb on 2018/5/29.
 */

public class Main2Activity extends AppCompatActivity {

    private DollFragment dollFragment;
    private PageSwitchLayout frame;

    private ImageView img;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main2);
        img = (ImageView) findViewById(R.id.img);
        frame = (PageSwitchLayout) findViewById(R.id.frame);
        frame.setSwitchCallback(new PageSwitchLayout.SwitchCallback() {

            @Override
            public void onSwitch(PageSwitchLayout layout, boolean end) {
                if (end) {
                    frame.disableSwitcher();
                } else {
                    frame.enableSwitcher();
                }
                if (dollFragment != null) dollFragment.setEnableScroll(!end);
            }
        });
        setLayoutContainer();
        EventBus.getDefault().register(this);

        img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                animatorSet.start();
            }
        });
        initAnimator();
    }
    AnimatorSet animatorSet;
    private void initAnimator() {
        ObjectAnimator animatorLeft = ObjectAnimator.ofFloat(img, "translationX", 0, -30);
        animatorLeft.setRepeatMode(ValueAnimator.RESTART);
        animatorLeft.setRepeatCount(-1);

        ObjectAnimator animatorDown = ObjectAnimator.ofFloat(img, "translationY", 0, 30);
        animatorDown.setRepeatMode(ValueAnimator.RESTART);
        animatorDown.setRepeatCount(-1);


        animatorSet = new AnimatorSet();
        animatorSet.setDuration(500);
        animatorSet.setInterpolator(new LinearInterpolator());
        animatorSet.playTogether(animatorLeft, animatorDown);
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(EventScroll eventScroll) {
        if (eventScroll.enableScroll) {
            frame.enableSwitcher();
        } else {
            frame.disableSwitcher();
        }
    }

    /**
     * 设置填充布局
     */
    protected void setLayoutContainer() {
        FragmentManager fm = getSupportFragmentManager();
        if (fm.findFragmentByTag("dollFragment") instanceof DollFragment) {
            dollFragment = (DollFragment) fm.findFragmentByTag("dollFragment");
        }
        if (dollFragment == null) {
            //暂时填写自己的id¬
            dollFragment = new DollFragment();
            if (dollFragment == null) {
                return;
            }
            FragmentTransaction ft = fm.beginTransaction();
            ft.replace(R.id.container_layout, dollFragment, "dollFragment");
            ft.commit();
        }

    }

}
