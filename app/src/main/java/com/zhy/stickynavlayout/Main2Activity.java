package com.zhy.stickynavlayout;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;

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

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        frame = (PageSwitchLayout) findViewById(R.id.frame);
        frame.setSwitchCallback(new PageSwitchLayout.SwitchCallback() {

            @Override
            public void onSwitch(PageSwitchLayout layout, boolean end) {
                if (end) {
                    frame.disableSwitcher();
                } else {
                    frame.enableSwitcher();
                }
            }
        });
        setLayoutContainer();
        EventBus.getDefault().register(this);
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
