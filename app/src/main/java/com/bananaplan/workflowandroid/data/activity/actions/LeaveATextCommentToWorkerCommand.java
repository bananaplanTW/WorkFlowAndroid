package com.bananaplan.workflowandroid.data.activity.actions;

import android.content.Context;
import android.util.Log;

import com.bananaplan.workflowandroid.data.network.PostRequestAsyncTask;

import org.json.JSONObject;

/**
 * Created by daz on 10/15/15.
 */
public class LeaveATextCommentToWorkerCommand implements ICreateActivityCommand, PostRequestAsyncTask.OnFinishPostingDataListener {

    private PostRequestAsyncTask mPostRequestAsyncTask;
    private Context mContext;
    private String mWorkerId;
    private String mComment;

    public LeaveATextCommentToWorkerCommand(Context context, String workerId, String comment) {
        mContext = context;
        mWorkerId = workerId;
        mComment = comment;
    }


    @Override
    public void execute() {
        UploadingCommentStrategy uploadingCommentStrategy = new UploadingCommentStrategy(mWorkerId, mComment);
        mPostRequestAsyncTask = new PostRequestAsyncTask(mContext, uploadingCommentStrategy, this);
        mPostRequestAsyncTask.execute();
    }


    @Override
    public void onFinishPostingData() {
        JSONObject result = mPostRequestAsyncTask.getResult();
    }
    @Override
    public void onFailPostingData(boolean isFailCausedByInternet) {

    }
}
