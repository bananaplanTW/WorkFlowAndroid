package com.bananaplan.workflowandroid.data.activity.actions;

import android.content.Context;
import android.util.Log;

import com.bananaplan.workflowandroid.data.network.PostRequestAsyncTask;

import org.json.JSONObject;

/**
 * Created by daz on 10/14/15.
 */
public class LeaveAFileCommentToWorkerCommand implements ICreateActivityCommand, PostRequestAsyncTask.OnFinishPostingDataListener {


    private PostRequestAsyncTask mPostRequestAsyncTask;
    private Context mContext;
    private String mWorkerId;
    private String mFilePath;


    public LeaveAFileCommentToWorkerCommand (Context context, String workerId, String filePath) {
        mContext = context;
        mWorkerId = workerId;
        mFilePath = filePath;
    }


    @Override
    public void execute() {
        UploadingFileStrategy uploadingFileStrategy = new UploadingFileStrategy(mWorkerId, mFilePath);
        mPostRequestAsyncTask = new PostRequestAsyncTask(mContext, uploadingFileStrategy, this);
        mPostRequestAsyncTask.execute();
    }


    @Override
    public void onFinishPostingData() {
        JSONObject result = mPostRequestAsyncTask.getResult();
        Log.d("DAZZZZ", result.toString());
    }
    @Override
    public void onFailPostingData(boolean isFailCausedByInternet) {

    }
}
