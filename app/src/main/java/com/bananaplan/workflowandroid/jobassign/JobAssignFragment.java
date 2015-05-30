package com.bananaplan.workflowandroid.jobassign;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TabHost;

import com.bananaplan.workflowandroid.R;


/**
 *
 *
 * @author Danny Lin
 * @since 2015.05.30
 */
public class JobAssignFragment extends Fragment {

    private Activity mMainActivity;

    private TabHost mWorkerTabHost;
    private TabHost mJobTabHost;

    public JobAssignFragment() {

    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mMainActivity = activity;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_job_assign, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initialize();
    }

    private void initialize() {
        findViews();
        initWorkerTab();
        initJobTab();
    }

    private void findViews() {
        mWorkerTabHost = (TabHost) mMainActivity.findViewById(R.id.worker_container);
        mJobTabHost = (TabHost) mMainActivity.findViewById(R.id.job_container);
    }


    private void initWorkerTab() {
        mWorkerTabHost.setup();
        mWorkerTabHost.addTab(mWorkerTabHost.newTabSpec("1").setIndicator("1").setContent(new TabHost.TabContentFactory() {
            @Override
            public View createTabContent(String tag) {
                View v = new View(mMainActivity);
                v.setMinimumWidth(0);
                v.setMinimumHeight(0);
                v.setVisibility(View.GONE);
                return v;
            }
        }));
        mWorkerTabHost.addTab(mWorkerTabHost.newTabSpec("2").setIndicator("2").setContent(new TabHost.TabContentFactory() {
            @Override
            public View createTabContent(String tag) {
                View v = new View(mMainActivity);
                v.setMinimumWidth(0);
                v.setMinimumHeight(0);
                v.setVisibility(View.GONE);
                return v;
            }
        }));
//        addTab(TabTag.START_TAB_TAG);
//        addTab(TabTag.MY_TAB_TAG);
//        addTab(TabTag.MUSIC_LIST_TAB_TAG);
//        addTab(TabTag.MUSIC_RANK_TAB_TAG);
    }

    private void initJobTab() {
        mJobTabHost.setup();
        mJobTabHost.addTab(mJobTabHost.newTabSpec("1").setIndicator("1").setContent(new TabHost.TabContentFactory() {
            @Override
            public View createTabContent(String tag) {
                View v = new View(mMainActivity);
                v.setMinimumWidth(0);
                v.setMinimumHeight(0);
                v.setVisibility(View.GONE);
                return v;
            }
        }));
        mJobTabHost.addTab(mJobTabHost.newTabSpec("2").setIndicator("2").setContent(new TabHost.TabContentFactory() {
            @Override
            public View createTabContent(String tag) {
                View v = new View(mMainActivity);
                v.setMinimumWidth(0);
                v.setMinimumHeight(0);
                v.setVisibility(View.GONE);
                return v;
            }
        }));
//        addTab(TabTag.START_TAB_TAG);
//        addTab(TabTag.MY_TAB_TAG);
//        addTab(TabTag.MUSIC_LIST_TAB_TAG);
//        addTab(TabTag.MUSIC_RANK_TAB_TAG);
    }

}
