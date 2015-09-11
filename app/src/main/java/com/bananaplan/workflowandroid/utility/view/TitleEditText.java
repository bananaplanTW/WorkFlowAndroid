package com.bananaplan.workflowandroid.utility.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.text.InputType;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bananaplan.workflowandroid.R;


/**
 * Customized view which combines a title and a edit content
 *
 * Title       (TextView)
 * __________  (EditText)
 *
 * @author Danny Lin
 * @since 2015/9/9.
 */
public class TitleEditText extends LinearLayout implements View.OnClickListener, View.OnFocusChangeListener {

    private static final String TAG = "TitleEditText";

    public static final class TetInputType {
        public static final int NONE = 0;
        public static final int TEXT = 1;
        public static final int NUMBER = 2;
        public static final int DATE_PICKER = 3;
        public static final int VENDOR_PICKER = 4;
        public static final int PIC_PICKER = 5;
    }

    public interface OnClickContentListener {
        void onClickContent(TitleEditText tet);
    }

    private TextView mTitleTextView;
    private EditText mContentEditText;

    private String mTitle;
    private String mContent;
    private String mContentHint;
    private int mContentMinLines;
    private int mContentInputType;
    private boolean mContentIsSingleLine;
    private int mContentBackgroundId;

    private OnClickContentListener mOnClickContentListener;


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
            mContent = a.getString(R.styleable.TitleEditText_tet_content);
            mContentHint = a.getString(R.styleable.TitleEditText_tet_contentHint);
            mContentMinLines = a.getInteger(R.styleable.TitleEditText_tet_contentMinLines, 1);
            mContentInputType = a.getInt(R.styleable.TitleEditText_tet_contentInputType, TetInputType.TEXT);
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

        setTitle(mTitle);
        setContent(mContent);
        setContentHint(mContentHint);
        setContentSingleLine(mContentIsSingleLine);
        setContentMinLines(mContentMinLines);
        setContentInputType(mContentInputType);
        setContentBackgroundId(mContentBackgroundId);
    }

    public String getTitle() {
        return mTitle;
    }

    public void setTitle(String title) {
        mTitleTextView.setText(title);
        mTitle = title;
    }

    public String getContent() {
        return mContent;
    }

    public void setContent(String content) {
        mContentEditText.setText(content);
        mContent = content;
    }

    public String getContentHint() {
        return mContentHint;
    }

    public void setContentHint(String hint) {
        mContentEditText.setHint(hint);
        mContentHint = hint;
    }

    public boolean isContentSingleLine() {
        return mContentIsSingleLine;
    }

    public void setContentSingleLine(boolean isSingleLine) {
        mContentEditText.setSingleLine(isSingleLine);
        mContentIsSingleLine = isSingleLine;
    }

    public int getContentInputType() {
        return mContentInputType;
    }

    public void setContentInputType(int inputType) {
        switch (inputType) {
            case TetInputType.TEXT:
                mContentEditText.setRawInputType(InputType.TYPE_CLASS_TEXT);
                break;
            case TetInputType.NUMBER:
                mContentEditText.setRawInputType(InputType.TYPE_CLASS_NUMBER);
                break;
            default:
                mContentEditText.setRawInputType(InputType.TYPE_NULL);
                mContentEditText.setOnClickListener(this);
                mContentEditText.setOnFocusChangeListener(this);
                break;
        }
        mContentInputType = inputType;
    }

    public int getContentMinLines() {
        return mContentMinLines;
    }

    public void setContentMinLines(int minLines) {
        mContentEditText.setMinLines(minLines);
        mContentMinLines = minLines;
    }

    public int getContentBackgroundId() {
        return mContentBackgroundId;
    }

    public void setContentBackgroundId(int resoruceId) {
        if (mContentBackgroundId == -1) return;
        mContentEditText.setBackgroundResource(mContentBackgroundId);
        mContentBackgroundId = resoruceId;
    }

    public OnClickContentListener getOnClickContentListener() {
        return mOnClickContentListener;
    }

    public void setOnClickContentListener(OnClickContentListener listener) {
        mOnClickContentListener = listener;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tet_content:
                onClickContentEvent();
                break;
        }
    }

    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        if (!hasFocus) return;
        onClickContentEvent();
    }

    private void onClickContentEvent() {
        if (mOnClickContentListener == null) return;
        mOnClickContentListener.onClickContent(this);
    }
}
