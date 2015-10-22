package com.bananaplan.workflowandroid.assigntask.tasks;

import android.content.Context;
import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.bananaplan.workflowandroid.R;


/**
 * @author Danny Lin
 * @since 2015/7/28.
 */
public class CaseCardDecoration extends RecyclerView.ItemDecoration {

    private Context mContext;
    private int mSpanCount;


    public CaseCardDecoration(Context context) {
        mContext = context;
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        super.getItemOffsets(outRect, view, parent, state);
        int position = parent.getChildAdapterPosition(view);
        int itemCount = parent.getAdapter().getItemCount();

        int normalMargin = mContext.getResources().getDimensionPixelSize(R.dimen.task_card_normal_margin);
        int boundaryMargin = mContext.getResources().getDimensionPixelSize(R.dimen.task_card_boundary_margin);

        // Header
        if (position == 0) return;

        // Top and bottom
        if (position == 1) {
            outRect.top = boundaryMargin;
            outRect.bottom = normalMargin;
        } else if (position == itemCount - 1) {
            outRect.top = normalMargin;
            outRect.bottom = boundaryMargin;
        } else {
            outRect.top = normalMargin;
            outRect.bottom = normalMargin;
        }

        // Left and right
        outRect.left = boundaryMargin;
        outRect.right = boundaryMargin;
    }
}
