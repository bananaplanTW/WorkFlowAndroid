package com.bananaplan.workflowandroid.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;

import com.bananaplan.workflowandroid.R;
import com.bananaplan.workflowandroid.dialog.ConfirmDialogFragment.OnConfirmDialogActionListener;

/**
 * Do not use this class directly, if you want to display the dialog, use ConfirmDialogFragment.
 *
 * @author Danny Lin
 * @since 2015/11/4.
 */
public class ConfirmDialog extends Dialog implements View.OnClickListener {

    private Context mContext;
    private int mType;

    private ImageView mConfirmDialogIcon;
    private TextView mConfirmDialogText;

    private TextView mConfirmDialogOkButton;
    private TextView mConfirmDialogCancelButton;
    private TextView mConfirmDialogCompleteButton;

    private OnConfirmDialogActionListener mOnConfirmDialogActionListener;


    public ConfirmDialog(Context context, OnConfirmDialogActionListener listener, int type) {
        super(context);
        mContext = context;
        mOnConfirmDialogActionListener = listener;
        mType = type;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_confirm);
        initialize();
    }

    private void initialize() {
        findViews();
        setupViews();
        setupButton();
    }

    private void findViews() {
        mConfirmDialogIcon = (ImageView) findViewById(R.id.confirm_dialog_icon);
        mConfirmDialogText = (TextView) findViewById(R.id.confirm_dialog_text);
        mConfirmDialogOkButton = (TextView) findViewById(R.id.confirm_dialog_ok_button);
        mConfirmDialogCancelButton = (TextView) findViewById(R.id.confirm_dialog_cancel_button);
        mConfirmDialogCompleteButton = (TextView) findViewById(R.id.confirm_dialog_complete_button);
    }

    private void setupViews() {
        switch (mType) {
            case ConfirmDialogFragment.Type.ADD_TASK:
                mConfirmDialogIcon.setImageResource(R.drawable.ic_confirm_dialog_completed);
                mConfirmDialogText.setText(mContext.getString(R.string.confirm_dialog_add_task_completed_text));
                mConfirmDialogOkButton.setVisibility(View.VISIBLE);
                mConfirmDialogCancelButton.setVisibility(View.GONE);
                mConfirmDialogCompleteButton.setVisibility(View.GONE);

                break;

            case ConfirmDialogFragment.Type.ADD_WARNING:
                mConfirmDialogIcon.setImageResource(R.drawable.ic_confirm_dialog_warning);
                mConfirmDialogText.setText(mContext.getString(R.string.confirm_dialog_add_warning_completed_text));
                mConfirmDialogOkButton.setVisibility(View.VISIBLE);
                mConfirmDialogCancelButton.setVisibility(View.GONE);
                mConfirmDialogCompleteButton.setVisibility(View.GONE);

                break;

            case ConfirmDialogFragment.Type.COMPLETE_TASK:
                mConfirmDialogIcon.setImageResource(R.drawable.ic_confirm_dialog_completed);
                mConfirmDialogText.setText(mContext.getString(R.string.confirm_dialog_complete_task_text));
                mConfirmDialogOkButton.setVisibility(View.GONE);
                mConfirmDialogCancelButton.setVisibility(View.VISIBLE);
                mConfirmDialogCompleteButton.setVisibility(View.VISIBLE);

                break;
        }
    }

    private void setupButton() {
        mConfirmDialogOkButton.setOnClickListener(this);
        mConfirmDialogCancelButton.setOnClickListener(this);
        mConfirmDialogCompleteButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (mOnConfirmDialogActionListener == null) return;

        switch (v.getId()) {
            case R.id.confirm_dialog_ok_button:
                mOnConfirmDialogActionListener.onClickOk();

                break;

            case R.id.confirm_dialog_cancel_button:
                mOnConfirmDialogActionListener.onClickCancel();

                break;

            case R.id.confirm_dialog_complete_button:
                mOnConfirmDialogActionListener.onClickCompleteTask();

                break;
        }
    }
}
