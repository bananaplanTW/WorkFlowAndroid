package com.bananaplan.workflowandroid.utility.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.widget.ProgressBar;

import com.bananaplan.workflowandroid.R;

/**
 * Created by Ben on 2015/7/19.
 */
public class CustomProgressBar extends ProgressBar {
    private Paint mTextPaint;
    private Rect mBounds;

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
        setProgressDrawable(getResources().getDrawable(R.drawable.case_progress_drawable, null));
        setMax(100);
        mBounds = new Rect();
    }

    @Override
    protected synchronized void onDraw(@NonNull Canvas canvas) {
        super.onDraw(canvas);

        // draw percentage text
        String text = String.valueOf(getProgress()) + "%";
        mTextPaint.getTextBounds(text, 0, text.length(), mBounds);
        mTextPaint.setShadowLayer(3, 3, 3, 0xFFFFFF);
        int x = getWidth() / 2 - mBounds.centerX();
        int y = getHeight() / 2 - mBounds.centerY();
        canvas.drawText(text, x, y, mTextPaint);
    }
}
