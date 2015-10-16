package com.bananaplan.workflowandroid.data.worker.actions;

import android.content.Context;

import com.bananaplan.workflowandroid.data.network.PostRequestAsyncTask;

/**
 * Created by daz on 10/16/15.
 */
public class AssignTaskToWorkerCommand implements IWorkerActionCommand, PostRequestAsyncTask.OnFinishPostingDataListener {

    private PostRequestAsyncTask mPostRequestAsyncTask;

    private Context mContext;
    private String mWorkerId;
    private String mTaskId;


    public AssignTaskToWorkerCommand (Context context, String workerId, String taskId) {
        mContext = context;
        mWorkerId = workerId;
        mTaskId = taskId;
    }

    @Override
    public void execute() {
        AssigningTaskStrategy assigningTaskStrategy = new AssigningTaskStrategy(mWorkerId, mTaskId);
        mPostRequestAsyncTask = new PostRequestAsyncTask(mContext, assigningTaskStrategy, this);
        mPostRequestAsyncTask.execute();
    }


    @Override
    public void onFinishPostingData() {

    }
    @Override
    public void onFailPostingData(boolean isFailCausedByInternet) {

    }
}
