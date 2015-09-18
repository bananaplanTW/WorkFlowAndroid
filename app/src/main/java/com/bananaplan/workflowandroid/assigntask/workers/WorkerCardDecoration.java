package com.bananaplan.workflowandroid.assigntask.workers;

import android.content.Context;
import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.bananaplan.workflowandroid.R;


/**
 * @author Danny Lin
 * @since 2015/8/1.
 */
public class WorkerCardDecoration extends RecyclerView.ItemDecoration {

    private Context mContext;


    public WorkerCardDecoration(Context context) {
        mContext = context;
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        super.getItemOffsets(outRect, view, parent, state);
        int position = parent.getChildAdapterPosition(view);
        int itemCount = parent.getAdapter().getItemCount();

        int leftRightMargin = mContext.getResources().getDimensionPixelSize(R.dimen.worker_card_margin_left_right);
        int topDownMargin = mContext.getResources().getDimensionPixelSize(R.dimen.worker_card_margin_top_down);

        // Top and bottom
        if (position == 0 || position == 1 || position == 2) {
            outRect.bottom = topDownMargin;
        } else if (position == itemCount - 1 || position == itemCount - 2 || position == itemCount - 3) {
            outRect.top = topDownMargin;
        } else {
            outRect.top = topDownMargin;
            outRect.bottom = topDownMargin;
        }

        // Left and right
        if (position % 3 == 0) {
            outRect.right = leftRightMargin;
        } else if (position % 3 == 2) {
            outRect.left = leftRightMargin;
        } else {
            outRect.left = leftRightMargin;
            outRect.right = leftRightMargin;
        }
    }
}
