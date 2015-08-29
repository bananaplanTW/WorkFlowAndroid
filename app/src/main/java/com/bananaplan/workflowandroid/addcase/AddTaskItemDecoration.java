package com.bananaplan.workflowandroid.addcase;

import android.content.Context;
import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.bananaplan.workflowandroid.R;


public class AddTaskItemDecoration extends RecyclerView.ItemDecoration {

    private Context mContext;

    private int mSpanCount = 0;

    public AddTaskItemDecoration(Context context, int spanCount) {
        mContext = context;
        mSpanCount = spanCount;
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        super.getItemOffsets(outRect, view, parent, state);
        int position = parent.getChildAdapterPosition(view);
        int itemCount = parent.getAdapter().getItemCount();

        int parentPadding = mContext.getResources().getDimensionPixelSize(R.dimen.add_case_add_task_grid_view_padding);
        int leftRightMargin = mContext.getResources().getDimensionPixelSize(R.dimen.add_case_task_item_margin_left_right);
        int topDownMargin = mContext.getResources().getDimensionPixelSize(R.dimen.add_case_task_item_margin_top_down);

        // Header
        if (position == 0) return;

        // Top and bottom
        if (position <= mSpanCount) {
            outRect.top = parentPadding;
            outRect.bottom = topDownMargin;
        } else if (position == itemCount - 1) {
            outRect.top = topDownMargin;
            outRect.bottom = parentPadding;
        } else {
            outRect.top = topDownMargin;
            outRect.bottom = topDownMargin;
        }

        // Left and right
        if (position % mSpanCount == 1) {
            outRect.right = leftRightMargin;
            outRect.left = parentPadding;
        } else if (position % mSpanCount == 0) {
            outRect.right = parentPadding;
            outRect.left = leftRightMargin;
        } else {
            outRect.right = leftRightMargin;
            outRect.left = leftRightMargin;
        }
    }
}