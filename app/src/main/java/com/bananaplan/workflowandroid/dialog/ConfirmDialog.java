package com.bananaplan.workflowandroid.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;

import com.bananaplan.workflowandroid.R;

/**
 * @author Danny Lin
 * @since 2015/11/4.
 */
public class ConfirmDialog extends Dialog implements View.OnClickListener {

    private Context mContext;
    private int mType;

    private ImageView mConfirmDialogIcon;
    private TextView mConfirmDialogText;
    private TextView mConfirmDialogOkButton;


    public ConfirmDialog(Context context, int type) {
        super(context);
        mContext = context;
        mType = type;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_confirm);
        setCanceledOnTouchOutside(false);
        initialize();
    }

    private void initialize() {
        findViews();
        setupIconAndText();
        setupButton();
    }

    private void findViews() {
        mConfirmDialogIcon = (ImageView) findViewById(R.id.confirm_dialog_icon);
        mConfirmDialogText = (TextView) findViewById(R.id.confirm_dialog_text);
        mConfirmDialogOkButton = (TextView) findViewById(R.id.confirm_dialog_ok_button);
    }

    private void setupIconAndText() {
        switch (mType) {
            case ConfirmDialogFragment.Type.ADD_TASK:
                mConfirmDialogIcon.setImageResource(R.drawable.ic_confirm_dialog_completed);
                mConfirmDialogText.setText(mContext.getString(R.string.confirm_dialog_add_task_completed_text));
                break;

            case ConfirmDialogFragment.Type.ADD_WARNING:
                mConfirmDialogIcon.setImageResource(R.drawable.ic_confirm_dialog_warning);
                mConfirmDialogText.setText(mContext.getString(R.string.confirm_dialog_add_warning_completed_text));
                break;
        }
    }

    private void setupButton() {
        mConfirmDialogOkButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.confirm_dialog_ok_button:
                dismiss();

                break;
        }
    }
}
