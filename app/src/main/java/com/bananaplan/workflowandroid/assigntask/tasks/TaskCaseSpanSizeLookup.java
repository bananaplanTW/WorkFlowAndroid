package com.bananaplan.workflowandroid.assigntask.tasks;

import android.support.v7.widget.GridLayoutManager;


/**
 * @author Danny Lin
 * @since 2015/7/16.
 */
public class TaskCaseSpanSizeLookup extends GridLayoutManager.SpanSizeLookup {

    private final GridLayoutManager mLayoutManager;


    public TaskCaseSpanSizeLookup(GridLayoutManager layoutManager) {
        this.mLayoutManager = layoutManager;
    }

    @Override public int getSpanSize(int position) {
        return position == 0 ? mLayoutManager.getSpanCount() : 1;
    }
}
