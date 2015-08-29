package com.bananaplan.workflowandroid.utility;

import android.app.Activity;
import android.support.v4.app.Fragment;

import com.bananaplan.workflowandroid.assigntask.tasks.TaskCase;
import com.bananaplan.workflowandroid.assigntask.workers.WorkerItem;
import com.bananaplan.workflowandroid.caseoverview.CaseOverviewFragment;
import com.bananaplan.workflowandroid.main.UIController;
import com.bananaplan.workflowandroid.workeroverview.WorkerOverviewFragment;

import java.util.ArrayList;

/**
 * Created by Ben on 2015/8/23.
 */
public abstract class OvTabFragmentBase extends Fragment {
    public abstract Object getCallBack();

    public interface WorkerOvCallBack {
        void onWorkerSelected(WorkerItem worker);
    }
    public interface CaseOvCallBack {
        void onCaseSelected(TaskCase taskCase);
    }

    private ArrayList<WorkerOvCallBack> mWorkerOvCallBacks;
    private ArrayList<CaseOvCallBack> mCaseOvCallBacks;

    public OvTabFragmentBase() {
        mWorkerOvCallBacks = new ArrayList<>();
        mCaseOvCallBacks = new ArrayList<>();
    }

    protected void registerWorkerOvCallBack(WorkerOvCallBack callback) {
        if (mWorkerOvCallBacks == null || callback == null) return;
        mWorkerOvCallBacks.add(callback);
    }

    protected void unregisterWorkerOvCallBack(WorkerOvCallBack callback) {
        if (mWorkerOvCallBacks == null || callback == null) return;
        mWorkerOvCallBacks.remove(callback);
    }

    protected void registerCaseOvCallBack(CaseOvCallBack callback) {
        if (mCaseOvCallBacks == null || callback == null) return;
        mCaseOvCallBacks.add(callback);
    }

    protected void unregisterCaseOvCallBack(CaseOvCallBack callback) {
        if (mCaseOvCallBacks == null || callback == null) return;
        mCaseOvCallBacks.remove(callback);
    }

    public void selectWorker(WorkerItem worker) {
        for (WorkerOvCallBack callback : mWorkerOvCallBacks) {
            callback.onWorkerSelected(worker);
        }
    }

    public void selectTaskCase(TaskCase taskCase) {
        for (CaseOvCallBack callback : mCaseOvCallBacks) {
            callback.onCaseSelected(taskCase);
        }
    }

    public WorkerItem getSelectedWorker() {
        Fragment frag = getFragmentManager().findFragmentByTag(UIController.FragmentTag.WORKER_OVERVIEW_FRAGMENT);
        if (frag == null) return null;
        if (!(frag instanceof WorkerOverviewFragment)) return null;
        return ((WorkerOverviewFragment) frag).getSelectedWorker();
    }

    public TaskCase getSelectedTaskCase() {
        Fragment frag = getFragmentManager().findFragmentByTag(UIController.FragmentTag.CASE_OVERVIEW_FRAGMENT);
        if (frag == null) return null;
        if (!(frag instanceof CaseOverviewFragment)) return null;
        return ((CaseOverviewFragment) frag).getSelectedTaskCase();
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        if (getCallBack() instanceof CaseOvCallBack) {
            registerCaseOvCallBack((CaseOvCallBack) getCallBack());
        } else if (getCallBack() instanceof WorkerOvCallBack) {
            registerWorkerOvCallBack((WorkerOvCallBack) getCallBack());
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        if (getCallBack() instanceof CaseOvCallBack) {
            unregisterCaseOvCallBack((CaseOvCallBack) getCallBack());
        } else if (getCallBack() instanceof WorkerOvCallBack) {
            unregisterWorkerOvCallBack((WorkerOvCallBack) getCallBack());
        }
    }
}
