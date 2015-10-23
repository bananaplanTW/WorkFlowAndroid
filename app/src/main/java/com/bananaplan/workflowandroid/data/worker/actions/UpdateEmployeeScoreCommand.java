package com.bananaplan.workflowandroid.data.worker.actions;

import android.content.Context;

import com.bananaplan.workflowandroid.data.network.PostRequestAsyncTask;

/**
 * Created by daz on 10/23/15.
 */
public class UpdateEmployeeScoreCommand implements IWorkerActionCommand, PostRequestAsyncTask.OnFinishPostingDataListener {

    private PostRequestAsyncTask mPostRequestAsyncTask;

    private Context mContext;
    private String mWorkerId;
    private int mScore;

    public UpdateEmployeeScoreCommand (Context context, String workerId, int score) {
        mContext = context;
        mWorkerId = workerId;
        mScore = score;
    }


    @Override
    public void execute() {
        UpdateEmployeeScoreStrategy updateEmployeeScoreStrategy = new UpdateEmployeeScoreStrategy(mWorkerId, mScore);
        mPostRequestAsyncTask = new PostRequestAsyncTask(mContext, updateEmployeeScoreStrategy, this);
        mPostRequestAsyncTask.execute();
    }


    @Override
    public void onFinishPostingData() {

    }
    @Override
    public void onFailPostingData(boolean isFailCausedByInternet) {

    }
}
