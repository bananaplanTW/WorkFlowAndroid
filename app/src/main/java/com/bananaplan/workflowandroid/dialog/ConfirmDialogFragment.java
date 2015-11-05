package com.bananaplan.workflowandroid.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;

/**
 * @author Danny Lin
 * @since 2015/11/4.
 */
public class ConfirmDialogFragment extends DialogFragment {

    public static final String EXTRA_CONFIRM_TYPE = "extra_confirm_type";

    public static final String TAG_CONFIRM_DIALOG = "tag_confirm_dialog";

    public static final class Type {
        public static final int ADD_TASK = 0;
        public static final int ADD_WARNING = 1;
        public static final int COMPLETE_TASK = 2;
    }

    public interface OnConfirmDialogActionListener {
        void onClickCompleteTask();
        void onClickCancel();
        void onClickOk();
    }

    private OnConfirmDialogActionListener mOnConfirmDialogActionListener;


    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mOnConfirmDialogActionListener = (OnConfirmDialogActionListener) getTargetFragment();
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        return new ConfirmDialog(getActivity(), mOnConfirmDialogActionListener, getArguments().getInt(EXTRA_CONFIRM_TYPE));
    }
}
