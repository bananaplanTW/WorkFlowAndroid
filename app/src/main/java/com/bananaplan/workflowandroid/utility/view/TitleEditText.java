package com.bananaplan.workflowandroid.utility.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.text.InputType;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bananaplan.workflowandroid.R;


/**
 * Customized view combines a title and a edit content
 *
 * Title       (TextView)
 * __________  (EditText)
 *
 * @author Danny Lin
 * @since 2015/9/9.
 */
public class TitleEditText extends LinearLayout {

    public static final int INPUT_TYPE_NONE = 0;
    public static final int INPUT_TYPE_TEXT = 1;
    public static final int INPUT_TYPE_NUMBER = 2;

    private TextView mTitleTextView;
    private EditText mContentEditText;

    private String mTitle;
    private String mContentHint;
    private int mContentMinLines;
    private int mContentInputType;
    private boolean mContentIsSingleLine;
    private int mContentBackgroundId;


    public TitleEditText(Context context) {
        super(context);
        init(context, null);
    }

    public TitleEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        if (attrs == null) return;

        TypedArray a = context.getTheme().obtainStyledAttributes(
                attrs,
                R.styleable.TitleEditText,
                0, 0);

        try {
            mTitle = a.getString(R.styleable.TitleEditText_tet_title);
            mContentHint = a.getString(R.styleable.TitleEditText_tet_contentHint);
            mContentMinLines = a.getInteger(R.styleable.TitleEditText_tet_contentMinLines, 1);
            mContentInputType = a.getInt(R.styleable.TitleEditText_tet_contentInputType, 1);
            mContentIsSingleLine = a.getBoolean(R.styleable.TitleEditText_tet_contentSingleLine, true);
            mContentBackgroundId = a.getResourceId(R.styleable.TitleEditText_tet_contentBackground, -1);
        } finally {
            a.recycle();
        }

        LayoutInflater.from(context).inflate(R.layout.title_edit_text, this);
        mTitleTextView = (TextView) findViewById(R.id.tet_title);
        mContentEditText = (EditText) findViewById(R.id.tet_content);

        setupValues();
    }

    private void setupValues() {
        mTitleTextView.setPadding(mContentEditText.getPaddingLeft(), 0, 0, 0);
        mTitleTextView.setText(mTitle);

        mContentEditText.setHint(mContentHint);
        mContentEditText.setSingleLine(mContentIsSingleLine);
        mContentEditText.setMinLines(mContentMinLines);

        switch (mContentInputType) {
            case INPUT_TYPE_NONE:
                mContentEditText.setRawInputType(InputType.TYPE_NULL);
                break;
            case INPUT_TYPE_TEXT:
                mContentEditText.setRawInputType(InputType.TYPE_CLASS_TEXT);
                break;
            case INPUT_TYPE_NUMBER:
                mContentEditText.setRawInputType(InputType.TYPE_CLASS_NUMBER);
                break;
        }

        if (mContentBackgroundId != -1) {
            mContentEditText.setBackgroundResource(mContentBackgroundId);
        }
    }

    public String getTitle() {
        return mTitle;
    }

    public void setTitle(String title) {
        mTitle = title;
        invalidate();
    }

    public String getContentHint() {
        return mContentHint;
    }

    public void setContentHint(String hint) {
        mContentHint = hint;
        invalidate();
    }

    public boolean isContentSingleLine() {
        return mContentIsSingleLine;
    }

    public void setContentSingleLine(boolean isSingleLine) {
        mContentIsSingleLine = isSingleLine;
        invalidate();
    }

    public int getContentInputType() {
        return mContentInputType;
    }

    public void setContentInputType(int inputType) {
        if (inputType != INPUT_TYPE_NONE && inputType != INPUT_TYPE_TEXT && inputType != INPUT_TYPE_NUMBER) return;
        mContentInputType = inputType;
        invalidate();
    }

    public int getContentMinLines() {
        return mContentMinLines;
    }

    public void setContentMinLines(int minLines) {
        mContentMinLines = minLines;
    }

    public int getContentBackgroundId() {
        return mContentBackgroundId;
    }

    public void setContentBackgroundId(int resoruceId) {
        mContentBackgroundId = resoruceId;
    }
}
