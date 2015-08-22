package com.bananaplan.workflowandroid.addworker;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bananaplan.workflowandroid.R;


/**
 * @author Danny Lin
 * @since 2015/8/22.
 */
public class AddWorkerFragment extends Fragment {

    private Activity mMainActivity;
    private View mFragmentView;


    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mMainActivity = activity;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_add_worker, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initialize();
    }

    private void initialize() {
        mFragmentView = getView();
    }
}
