package com.zhy.stickynavlayout;

import android.content.Context;
import android.graphics.Rect;
import android.support.v4.view.ViewCompat;
import android.support.v4.view.WindowInsetsCompat;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;

/**
 * Created by roya on 2017/10/2.
 */

public class ScrimInsetsLinearLayout extends LinearLayout {

    private Rect instes;

    public ScrimInsetsLinearLayout(Context context) {
        this(context, null);
    }

    public ScrimInsetsLinearLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ScrimInsetsLinearLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        setWillNotDraw(true);

        ViewCompat.setOnApplyWindowInsetsListener(this,
                new android.support.v4.view.OnApplyWindowInsetsListener() {
                    @Override
                    public WindowInsetsCompat onApplyWindowInsets(View v, WindowInsetsCompat insets) {

                        ScrimInsetsLinearLayout.this.instes =
                                new Rect(insets.getSystemWindowInsetLeft(),
                                        insets.getSystemWindowInsetTop(),
                                        insets.getSystemWindowInsetRight(),
                                        insets.getStableInsetBottom());

                        onInsetsChanged(insets);
                        ViewCompat.postInvalidateOnAnimation(ScrimInsetsLinearLayout.this);

                        return insets.consumeSystemWindowInsets();
                    }
                });

    }

    public Rect getInstes() {
        return instes;
    }

    protected void onInsetsChanged(WindowInsetsCompat insets) {
        for (int i = 0; i < getChildCount(); i++) {
            ViewCompat.dispatchApplyWindowInsets(getChildAt(i), insets);
        }
    }

}
