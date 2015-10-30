package com.bananaplan.workflowandroid.utility;

import android.app.Activity;
import android.support.v4.app.Fragment;

import com.bananaplan.workflowandroid.data.Equipment;
import com.bananaplan.workflowandroid.detail.worker.DetailedWorkerActivity;
import com.bananaplan.workflowandroid.overview.caseoverview.CaseOverviewFragment;
import com.bananaplan.workflowandroid.main.UIController;
import com.bananaplan.workflowandroid.overview.equipmentoverview.EquipmentOverviewFragment;
import com.bananaplan.workflowandroid.overview.workeroverview.WorkerOverviewFragment;
import com.bananaplan.workflowandroid.data.Case;
import com.bananaplan.workflowandroid.data.Worker;

import java.util.ArrayList;

/**
 * Created by Ben on 2015/8/23.
 */
public abstract class OvTabFragmentBase extends Fragment {
    public abstract Object getCallBack();

    public interface OvCallBack {
        void onItemSelected(Object item);
    }

    private ArrayList<OvCallBack> mOvCallBacks;

    public OvTabFragmentBase() {
        mOvCallBacks = new ArrayList<>();
    }

    protected void registerOvCallBack(OvCallBack callback) {
        if (mOvCallBacks == null || callback == null) return;
        mOvCallBacks.add(callback);
    }

    protected void unregisterOvCallBack(OvCallBack callback) {
        if (mOvCallBacks == null || callback == null) return;
        mOvCallBacks.remove(callback);
    }

    public void selectItem(Object item) {
        for (OvCallBack callback : mOvCallBacks) {
            callback.onItemSelected(item);
        }
    }

    public Worker getSelectedWorker() {
        if (getActivity() instanceof DetailedWorkerActivity) {
            return ((DetailedWorkerActivity) getActivity()).getSelectedWorker();
        } else {
            Fragment frag = getFragmentManager().findFragmentByTag(UIController.FragmentTag.WORKER_OVERVIEW_FRAGMENT);
            if (frag == null) return null;
            if (!(frag instanceof WorkerOverviewFragment)) return null;
            return ((WorkerOverviewFragment) frag).getSelectedWorker();
        }
    }

    public Case getSelectedTaskCase() {
        Fragment frag = getFragmentManager().findFragmentByTag(UIController.FragmentTag.CASE_OVERVIEW_FRAGMENT);
        if (frag == null) return null;
        if (!(frag instanceof CaseOverviewFragment)) return null;
        return ((CaseOverviewFragment) frag).getSelectedCase();
    }

    public Equipment getSelectedEquipment() {
        Fragment frag = getFragmentManager().findFragmentByTag(UIController.FragmentTag.EQUIPMENT_OVERVIEW_FRAGMENT);
        if (frag == null) return null;
        if (!(frag instanceof EquipmentOverviewFragment)) return null;
        return ((EquipmentOverviewFragment) frag).getSelectedEquipment();
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        if (getCallBack() instanceof OvCallBack) {
            registerOvCallBack((OvCallBack) getCallBack());
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        if (getCallBack() instanceof OvCallBack) {
            unregisterOvCallBack((OvCallBack) getCallBack());
        }
    }
}
