package com.jhjj9158.niupaivideo.widget;

import android.content.Context;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.AttributeSet;

/**
 * Created by oneki on 2017/6/6.
 */

public class StaggeredScrollEnable extends StaggeredGridLayoutManager {
    private boolean isScrollEnabled = true;

    public StaggeredScrollEnable(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    public StaggeredScrollEnable(int spanCount, int orientation) {
        super(spanCount, orientation);
    }

    public void setScrollEnabled(boolean flag) {
        this.isScrollEnabled = flag;
    }

    @Override
    public boolean canScrollVertically() {
        return isScrollEnabled && super.canScrollVertically();
    }
}
