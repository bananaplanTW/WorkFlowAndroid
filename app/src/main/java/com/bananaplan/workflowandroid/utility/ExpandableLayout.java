package com.bananaplan.workflowandroid.utility;

import android.content.Context;
import android.content.res.TypedArray;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.Transformation;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;

import com.bananaplan.workflowandroid.R;


public class ExpandableLayout extends RelativeLayout {

    public interface OnExpandCollapseListener {
        public void onExpand();
        public void onCollapse();
    }

    private Boolean mIsAnimationRunning = false;
    private Boolean mIsOpened = false;
    private Integer mDuration;
    private FrameLayout mContentLayout;
    private FrameLayout mHeaderLayout;
    private Animation mAnimation;

    private OnExpandCollapseListener mOnExpandCollapseListener;

    public ExpandableLayout(Context context)
    {
        super(context);
    }

    public ExpandableLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public ExpandableLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context, attrs);
    }

    public void setOnExpandCollapseListener(OnExpandCollapseListener listener) {
        mOnExpandCollapseListener = listener;
    }

    public int getDuration() {
        return mDuration;
    }

    public void setDuration(int duration) {
        mDuration = duration;
    }

    private void init(final Context context, AttributeSet attrs) {

        final View rootView = View.inflate(context, R.layout.view_expandable, this);
        mHeaderLayout = (FrameLayout) rootView.findViewById(R.id.view_expandable_headerlayout);
        mContentLayout = (FrameLayout) rootView.findViewById(R.id.view_expandable_contentLayout);

        final TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.ExpandableLayout);
        final int headerID = typedArray.getResourceId(R.styleable.ExpandableLayout_el_headerLayout, -1);
        final int contentID = typedArray.getResourceId(R.styleable.ExpandableLayout_el_contentLayout, -1);
        final int headerHeight = typedArray.getDimensionPixelSize(R.styleable.ExpandableLayout_el_headerHeight, -1);
        mDuration = typedArray.getInt(
                R.styleable.ExpandableLayout_el_duration,
                context.getResources().getInteger(android.R.integer.config_shortAnimTime));

        if (headerID == -1 || contentID == -1) {
            throw new IllegalArgumentException("HeaderLayout and ContentLayout cannot be null!");
        }

        if (isInEditMode()) {
            return;
        }

        final View headerView = View.inflate(context, headerID, null);
        headerView.setLayoutParams(new ViewGroup.LayoutParams(LayoutParams.MATCH_PARENT,
                                                              headerHeight == -1 ? LayoutParams.WRAP_CONTENT : headerHeight));
        mHeaderLayout.addView(headerView);

        final View contentView = View.inflate(context, contentID, null);
        contentView.setLayoutParams(new ViewGroup.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
        mContentLayout.addView(contentView);
        mContentLayout.setVisibility(GONE);

        mHeaderLayout.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!mIsAnimationRunning) {
                    if (mContentLayout.getVisibility() == VISIBLE) {
                        collapse(mContentLayout);
                    } else {
                        expand(mContentLayout);
                    }

                    mIsAnimationRunning = true;
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            mIsAnimationRunning = false;
                        }
                    }, mDuration);
                }
            }
        });

        typedArray.recycle();
    }

    private void expand(final View v) {
        v.measure(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        final int targetHeight = v.getMeasuredHeight();
        v.getLayoutParams().height = 0;
        v.setVisibility(VISIBLE);

        mAnimation = new Animation() {
            @Override
            protected void applyTransformation(float interpolatedTime, Transformation t) {
                if (interpolatedTime == 1)
                    mIsOpened = true;
                v.getLayoutParams().height = (interpolatedTime == 1) ? LayoutParams.WRAP_CONTENT : (int) (targetHeight * interpolatedTime);
                v.requestLayout();
            }


            @Override
            public boolean willChangeBounds() {
                return true;
            }
        };
        mAnimation.setDuration(mDuration);
        v.startAnimation(mAnimation);
        if (mOnExpandCollapseListener != null) {
            mOnExpandCollapseListener.onExpand();
        }
    }

    private void collapse(final View v) {
        final int initialHeight = v.getMeasuredHeight();
        mAnimation = new Animation() {
            @Override
            protected void applyTransformation(float interpolatedTime, Transformation t) {
                if(interpolatedTime == 1) {
                    v.setVisibility(View.GONE);
                    mIsOpened = false;
                } else {
                    v.getLayoutParams().height = initialHeight - (int)(initialHeight * interpolatedTime);
                    v.requestLayout();
                }
            }

            @Override
            public boolean willChangeBounds() {
                return true;
            }
        };

        mAnimation.setDuration(mDuration);
        v.startAnimation(mAnimation);
        if (mOnExpandCollapseListener != null) {
            mOnExpandCollapseListener.onCollapse();
        }
    }

    public Boolean isOpened()
    {
        return mIsOpened;
    }

    public void show() {
        if (!mIsAnimationRunning) {
            expand(mContentLayout);
            mIsAnimationRunning = true;
            new Handler().postDelayed(new Runnable()
            {
                @Override
                public void run()
                {
                    mIsAnimationRunning = false;
                }
            }, mDuration);
        }
    }

    public FrameLayout getHeaderLayout()
    {
        return mHeaderLayout;
    }

    public FrameLayout getContentLayout()
    {
        return mContentLayout;
    }

    public void hide() {
        if (!mIsAnimationRunning) {
            collapse(mContentLayout);
            mIsAnimationRunning = true;
            new Handler().postDelayed(new Runnable()
            {
                @Override
                public void run()
                {
                    mIsAnimationRunning = false;
                }
            }, mDuration);
        }
    }

    @Override
    public void setLayoutAnimationListener(Animation.AnimationListener animationListener) {
        mAnimation.setAnimationListener(animationListener);
    }
}
