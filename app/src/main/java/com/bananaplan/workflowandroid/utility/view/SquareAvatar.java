package com.bananaplan.workflowandroid.utility.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bananaplan.workflowandroid.R;


/**
 * @author Danny Lin
 * @since 2015/10/5.
 */
public class SquareAvatar extends RelativeLayout {

    private View mView;
    private ImageView mAvatarImage;
    private TextView mAvatarText;

    private Drawable mDrawable;
    private String mText;


    public SquareAvatar(Context context) {
        super(context);
        init(context, null);
    }

    public SquareAvatar(Context context, Drawable drawable, String text) {
        super(context);
        init(context, drawable, text);
    }

    public SquareAvatar(Context context, AttributeSet attrs) {
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
            mDrawable = context.getDrawable(a.getResourceId(R.styleable.SquareAvatar_sa_avatar, -1));
            mText = a.getString(R.styleable.SquareAvatar_sa_text);
        } finally {
            a.recycle();
        }

        LayoutInflater.from(context).inflate(R.layout.square_avatar, this);
        mView = findViewById(R.id.square_avatar);
        mAvatarImage = (ImageView) findViewById(R.id.square_avatar_image);
        mAvatarText = (TextView) findViewById(R.id.square_avatar_text);

        setupValues(context);
    }

    private void init(Context context, Drawable drawable, String text) {
        mDrawable = drawable;
        mText = text;

        LayoutInflater.from(context).inflate(R.layout.square_avatar, this);
        mView = findViewById(R.id.square_avatar);
        mAvatarImage = (ImageView) findViewById(R.id.square_avatar_image);
        mAvatarText = (TextView) findViewById(R.id.square_avatar_text);

        setupValues(context);
    }

    private void setupValues(Context context) {
        if (mDrawable != null) {
            ((GradientDrawable) mView.getBackground()).setColor(null);
            mAvatarImage.setImageDrawable(mDrawable);
            mAvatarImage.setVisibility(VISIBLE);
            mAvatarText.setVisibility(GONE);
        } else {
            ((GradientDrawable) mView.getBackground()).
                    setColor(context.getResources().getColor(R.color.square_avatar_background_color));
            mAvatarText.setText(mText);
            mAvatarImage.setVisibility(GONE);
            mAvatarText.setVisibility(VISIBLE);
        }
    }
}
