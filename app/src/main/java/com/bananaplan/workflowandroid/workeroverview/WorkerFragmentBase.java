package com.bananaplan.workflowandroid.workeroverview;

import android.support.v4.app.Fragment;

import com.bananaplan.workflowandroid.assigntask.workers.WorkerItem;
import com.bananaplan.workflowandroid.main.UIController;

/**
 * Created by Ben on 2015/8/14.
 */
public abstract class WorkerFragmentBase extends Fragment {
    public abstract void onWorkerSelected(WorkerItem worker);
    public WorkerItem getSelectedWorker() {
        Fragment frag = getFragmentManager().findFragmentByTag(UIController.FragmentTag.WORKER_OVERVIEW_FRAGMENT);
        if (frag == null) return null;
        return ((WorkerOverviewFragment) frag).getSelectedWorker();
    }
}
