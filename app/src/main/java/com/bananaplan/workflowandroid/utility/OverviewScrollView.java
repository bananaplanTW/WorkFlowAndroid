package com.bananaplan.workflowandroid.utility;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ScrollView;

/**
 * Created by Ben on 2015/9/12.
 */
public class OverviewScrollView extends ScrollView {
    private boolean mScrollEnable = true;
    public OverviewScrollView(Context context) {
        super(context);
    }

    public OverviewScrollView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public OverviewScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public void scrollTo(int x, int y) {
        if (!mScrollEnable) {
            x = 0;
            y = 0;
        }
        super.scrollTo(x, y);
    }

    public void setScrollEnable(boolean enable) {
        mScrollEnable = enable;
    }
}
