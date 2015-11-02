package com.bananaplan.workflowandroid.warning;

import android.content.Context;
import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.bananaplan.workflowandroid.R;


public class WarningCardDecoration extends RecyclerView.ItemDecoration {

    private Context mContext;

    private int mSpanCount = 0;

    public WarningCardDecoration(Context context, int spanCount) {
        mContext = context;
        mSpanCount = spanCount;
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        super.getItemOffsets(outRect, view, parent, state);
        int position = parent.getChildAdapterPosition(view);
        int itemCount = parent.getAdapter().getItemCount();

        int parentPadding
                = mContext.getResources().getDimensionPixelSize(R.dimen.warning_frag_warning_card_parent_padding);
        int leftRightMargin
                = mContext.getResources().getDimensionPixelSize(R.dimen.warning_frag_warning_card_left_right_margin);
        int topDownMargin
                = mContext.getResources().getDimensionPixelSize(R.dimen.warning_frag_warning_card_top_bottom_margin);

        int lastRowCount = itemCount % mSpanCount;

        // Top and bottom
        if (position < mSpanCount) {
            outRect.top = parentPadding;
            outRect.bottom = topDownMargin;
        } else if (position >= itemCount - lastRowCount) {
            outRect.top = topDownMargin;
            outRect.bottom = parentPadding;
        } else {
            outRect.top = topDownMargin;
            outRect.bottom = topDownMargin;
        }

        // Left and right
        if (position % mSpanCount == 0) {
            outRect.right = leftRightMargin;
            outRect.left = parentPadding;
        } else if (position % mSpanCount == 2) {
            outRect.right = parentPadding;
            outRect.left = leftRightMargin;
        } else {
            outRect.right = leftRightMargin;
            outRect.left = leftRightMargin;
        }
    }
}