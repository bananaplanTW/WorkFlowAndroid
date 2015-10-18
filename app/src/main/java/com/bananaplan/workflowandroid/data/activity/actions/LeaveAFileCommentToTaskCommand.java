package com.bananaplan.workflowandroid.data.activity.actions;

import android.content.Context;

import com.bananaplan.workflowandroid.data.loading.LoadingDataUtils;
import com.bananaplan.workflowandroid.data.network.PostRequestAsyncTask;

import java.util.HashMap;

/**
 * Created by daz on 10/18/15.
 */
public class LeaveAFileCommentToTaskCommand implements ICreateActivityCommand, PostRequestAsyncTask.OnFinishPostingDataListener {

    private PostRequestAsyncTask mPostRequestAsyncTask;

    private Context mContext;
    private String mTaskId;
    private String mFilePath;

    public LeaveAFileCommentToTaskCommand (Context context, String taskId, String filePath) {
        mContext = context;
        mTaskId = taskId;
        mFilePath = filePath;
    }


    @Override
    public void execute() {
        HashMap<String, String> queries = new HashMap<>();
        queries.put("td", mTaskId);
        // [TODO] should login with user
        queries.put("ud", "qY7FdM7wnjevqmfws");
        queries.put("t", "el1UPAsSmVf8F1LEKf8tRb8Ny5jAgOdK2qLNHztb7Cj");

        UploadingFileStrategy uploadingFileStrategy = new UploadingFileStrategy(mFilePath, LoadingDataUtils.WorkingDataUrl.EndPoints.COMMENT_FILE_ACTIVITY_TO_TASK, queries);
        mPostRequestAsyncTask = new PostRequestAsyncTask(mContext, uploadingFileStrategy, this);
        mPostRequestAsyncTask.execute();
    }


    @Override
    public void onFinishPostingData() {

    }
    @Override
    public void onFailPostingData(boolean isFailCausedByInternet) {

    }
}
