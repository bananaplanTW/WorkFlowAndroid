package com.bananaplan.workflowandroid.data.loading;

import android.content.Context;
import android.os.AsyncTask;

import com.bananaplan.workflowandroid.data.Case;
import com.bananaplan.workflowandroid.data.Factory;
import com.bananaplan.workflowandroid.data.Vendor;
import com.bananaplan.workflowandroid.data.Worker;
import com.bananaplan.workflowandroid.data.WorkingData;


/**
 * Async task to load data from server
 *
 * @author Danny Lin
 * @since 2015/10/1.
 */
public class LoadingDataTask extends AsyncTask<Void, Void, Void> {

    public interface OnFinishLoadingDataListener {
        void onFinishLoadingData();
    }

    private Context mContext;
    private OnFinishLoadingDataListener mOnFinishLoadingDataListener;


    public LoadingDataTask(Context context, OnFinishLoadingDataListener listener) {
        mContext = context;
        mOnFinishLoadingDataListener = listener;
    }

    @Override
    protected Void doInBackground(Void... params) {
        loadCases();
        loadFactories();
        connectTasksWithWorkers();
        connectCasesWithVendors();

        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        mOnFinishLoadingDataListener.onFinishLoadingData();
    }

    /**
     * Load all cases including all tasks in each case
     */
    private void loadCases() {
        LoadingDataUtils.loadCases(mContext);
        for (Case aCase : WorkingData.getInstance(mContext).getCases()) {
            LoadingDataUtils.loadTasksByCase(mContext, aCase.id);
        }
    }

    /**
     * Load all factories including all workers in each factory
     */
    private void loadFactories() {
        LoadingDataUtils.loadFactories(mContext);
        for (Factory factory : WorkingData.getInstance(mContext).getFactories()) {
            LoadingDataUtils.loadWorkersByFactory(mContext, factory.id);
        }
    }

    /**
     * Connect WIP-task and scheduled-tasks with each worker
     */
    private void connectTasksWithWorkers() {
        for (Worker worker : WorkingData.getInstance(mContext).getWorkers()) {
            if (WorkingData.getInstance(mContext).hasTask(worker.wipTaskId)) {
                worker.wipTask = WorkingData.getInstance(mContext).getTaskById(worker.wipTaskId);
            }

            worker.scheduledTasks.clear();
            for (String stId : worker.scheduledTaskIds) {
                if (WorkingData.getInstance(mContext).hasTask(stId)) {
                    worker.scheduledTasks.add(WorkingData.getInstance(mContext).getTaskById(stId));
                }
            }
        }
    }

    private void connectCasesWithVendors() {
        for (Vendor vendor : WorkingData.getInstance(mContext).getVendors()) {
            vendor.cases.clear();
            for (String caseId : vendor.caseIds) {
                if (WorkingData.getInstance(mContext).hasCase(caseId)) {
                    vendor.cases.add(WorkingData.getInstance(mContext).getCaseById(caseId));
                }
            }
        }
    }
}
