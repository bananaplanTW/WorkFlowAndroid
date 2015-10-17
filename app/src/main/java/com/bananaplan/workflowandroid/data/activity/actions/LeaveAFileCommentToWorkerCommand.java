package com.bananaplan.workflowandroid.data.activity.actions;

import android.content.Context;

import com.bananaplan.workflowandroid.data.loading.LoadingDataUtils;
import com.bananaplan.workflowandroid.data.network.PostRequestAsyncTask;

import org.json.JSONObject;

import java.util.HashMap;

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
        HashMap<String, String> queries = new HashMap<>();
        queries.put("ed", mWorkerId);
        // [TODO] should login with user
        queries.put("ud", "qY7FdM7wnjevqmfws");
        queries.put("t", "el1UPAsSmVf8F1LEKf8tRb8Ny5jAgOdK2qLNHztb7Cj");

        UploadingFileStrategy uploadingFileStrategy = new UploadingFileStrategy(mFilePath, LoadingDataUtils.WorkingDataUrl.EndPoints.COMMENT_FILE_ACTIVITY, queries);
        mPostRequestAsyncTask = new PostRequestAsyncTask(mContext, uploadingFileStrategy, this);
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
