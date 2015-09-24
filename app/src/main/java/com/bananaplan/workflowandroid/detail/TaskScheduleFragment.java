package com.bananaplan.workflowandroid.detail;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bananaplan.workflowandroid.R;

/**
 * Task schedule of a worker
 *
 * @author Danny Lin
 * @since 2015/9/21.
 */
public class TaskScheduleFragment extends Fragment implements View.OnClickListener {

    private Context mContext;
    private View mMainView;

    private TextView mCompleteTaskButton;
    private TextView mAddWarningButton;
    private TextView mManageWarningButton;


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return LayoutInflater.from(mContext).inflate(R.layout.fragment_task_schedule, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initialize();
    }

    private void initialize() {
        mMainView = getView();
        findViews();
        setupButtons();
    }

    private void findViews() {
        mCompleteTaskButton = (TextView) mMainView.findViewById(R.id.complete_task_button);
        mAddWarningButton = (TextView) mMainView.findViewById(R.id.add_warning_button);
        mManageWarningButton = (TextView) mMainView.findViewById(R.id.manage_warning_button);
    }

    private void setupButtons() {
        mCompleteTaskButton.setOnClickListener(this);
        mAddWarningButton.setOnClickListener(this);
        mManageWarningButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.complete_task_button:
                break;
            case R.id.add_warning_button:
                break;
            case R.id.manage_warning_button:
                break;
        }
    }
}
