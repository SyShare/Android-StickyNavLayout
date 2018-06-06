package com.zhy.stickynavlayout;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;

/**
 * Created by syb on 2018/5/30.
 */

public class AutoTopRecyclerView extends RecyclerView {

    public AutoTopRecyclerView(Context context) {
        this(context, null);
    }

    public AutoTopRecyclerView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, -1);
    }

    public AutoTopRecyclerView(Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    protected void onWindowVisibilityChanged(int visibility) {
        super.onWindowVisibilityChanged(visibility);
        if(visibility == VISIBLE){
            scrollToPosition(0);
        }
    }

    @Override
    public void onWindowFocusChanged(boolean hasWindowFocus) {
        super.onWindowFocusChanged(hasWindowFocus);
        if(hasWindowFocus){
            scrollToPosition(0);
        }
    }
}
