package com.bananaplan.workflowandroid.dialog;

import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;

/**
 * @author Danny Lin
 * @since 2015/11/4.
 */
public class ConfirmDialogFragment extends DialogFragment {

    public static final String EXTRA_CONFIRM_TYPE = "extra_confirm_type";

    private static final String TAG_CONFIRM_DIALOG = "tag_confirm_dialog";

    public static final class Type {
        public static final int ADD_TASK = 0;
        public static final int ADD_WARNING = 1;
    }


    public static void showConfirmDialog(FragmentManager fragmentManager, int type) {
        ConfirmDialogFragment confirmDialogFragment = new ConfirmDialogFragment();

        Bundle bundle = new Bundle();
        bundle.putInt(EXTRA_CONFIRM_TYPE, type);
        confirmDialogFragment.setArguments(bundle);

        confirmDialogFragment.show(fragmentManager, TAG_CONFIRM_DIALOG);
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        return new ConfirmDialog(getActivity(), getArguments().getInt(EXTRA_CONFIRM_TYPE));
    }
}
