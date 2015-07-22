package com.bananaplan.workflowandroid.caseoverview;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.widget.ProgressBar;

/**
 * Created by Ben on 2015/7/19.
 */
public class CaseCustomProgressBar extends ProgressBar {
    private Paint mTextPaint;

    public CaseCustomProgressBar(Context context) {
        super(context);
        init(null);
    }

    public CaseCustomProgressBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs);
    }

    public CaseCustomProgressBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs);
    }

    private void init(AttributeSet attrs) {
        mTextPaint = new Paint();
        mTextPaint.setColor(Color.WHITE);
        mTextPaint.setAntiAlias(true);
        mTextPaint.setTextSize(15);
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
