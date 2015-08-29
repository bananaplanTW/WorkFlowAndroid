package com.bananaplan.workflowandroid.utility;

import android.support.v7.widget.GridLayoutManager;


/**
 * Only for RecylerView with grid layout,
 * we can use this class to set the span size of the header item.
 *
 * @author Danny Lin
 * @since 2015/7/16.
 */
public class GridSpanSizeLookup extends GridLayoutManager.SpanSizeLookup {

    private final GridLayoutManager mLayoutManager;


    public GridSpanSizeLookup(GridLayoutManager layoutManager) {
        this.mLayoutManager = layoutManager;
    }

    @Override public int getSpanSize(int position) {
        return position == 0 ? mLayoutManager.getSpanCount() : 1;
    }
}
