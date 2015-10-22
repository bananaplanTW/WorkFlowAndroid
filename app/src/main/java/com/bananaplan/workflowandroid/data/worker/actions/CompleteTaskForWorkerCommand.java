package com.bananaplan.workflowandroid.data.worker.actions;

import android.content.Context;

import com.bananaplan.workflowandroid.data.network.PostRequestAsyncTask;

/**
 * Created by daz on 10/22/15.
 */
public class CompleteTaskForWorkerCommand implements IWorkerActionCommand, PostRequestAsyncTask.OnFinishPostingDataListener {

    private PostRequestAsyncTask mPostRequestAsyncTask;

    private Context mContext;
    private String mWorkerId;
    private String mTaskId;

    public CompleteTaskForWorkerCommand (Context context, String workerId, String taskId) {
        mContext = context;
        mWorkerId = workerId;
        mTaskId = taskId;
    }


    @Override
    public void execute() {
        CompleteTaskForWorkerStrategy completeTaskForWorkerStrategy = new CompleteTaskForWorkerStrategy(mWorkerId, mTaskId);
        mPostRequestAsyncTask = new PostRequestAsyncTask(mContext, completeTaskForWorkerStrategy, this);
        mPostRequestAsyncTask.execute();
    }


    @Override
    public void onFinishPostingData() {

    }
    @Override
    public void onFailPostingData(boolean isFailCausedByInternet) {

    }
}
