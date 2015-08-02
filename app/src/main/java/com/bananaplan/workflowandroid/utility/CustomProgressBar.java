package com.bananaplan.workflowandroid.utility;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.widget.ProgressBar;

import com.bananaplan.workflowandroid.R;

/**
 * Created by Ben on 2015/7/19.
 */
public class CustomProgressBar extends ProgressBar {
    private Paint mTextPaint;

    public CustomProgressBar(Context context) {
        super(context);
        init();
    }

    public CustomProgressBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public CustomProgressBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        mTextPaint = new Paint();
        mTextPaint.setColor(Color.WHITE);
        mTextPaint.setAntiAlias(true);
        mTextPaint.setTextSize(getResources().getDimension(R.dimen.case_progress_bar_text_size));
        setProgressDrawable(getResources().getDrawable(R.drawable.case_progress_drawable));
        setMax(100);
    }

    @Override
    protected synchronized void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        // draw percentage text
        Rect bounds = new Rect();
        String text = String.valueOf(getProgress()) + "%";
        mTextPaint.getTextBounds(text, 0, text.length(), bounds);
        mTextPaint.setShadowLayer(3, 3, 3, 0xFFFFFF);
        int x = getWidth() / 2 - bounds.centerX();
        int y = getHeight() / 2 - bounds.centerY();
        canvas.drawText(text, x, y, mTextPaint);
    }
}
