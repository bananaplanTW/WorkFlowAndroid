package com.bananaplan.workflowandroid.data.task.actions;

import android.content.Context;

import com.bananaplan.workflowandroid.data.WorkingData;
import com.bananaplan.workflowandroid.data.activity.actions.PostRequestStrategy;
import com.bananaplan.workflowandroid.data.activity.actions.UploadingFileStrategy;
import com.bananaplan.workflowandroid.data.loading.LoadingDataUtils;
import com.bananaplan.workflowandroid.data.network.PostRequestAsyncTask;
import com.bananaplan.workflowandroid.data.network.PostRequestAsyncTask.OnFinishPostingDataListener;

import java.util.HashMap;

/**
 * Created by daz on 10/24/15.
 */
public class IncrementTaskAlertScheduleCommand implements ITaskActionCommand, OnFinishPostingDataListener {

    private PostRequestAsyncTask mPostRequestAsyncTask;
    private Context mContext;
    private String mTaskId;
    private long mWillAlertAt;
    private String mContent;

    public IncrementTaskAlertScheduleCommand (Context context, String taskId, long willAlertAt, String content) {
        mContext = context;
        mTaskId = taskId;
        mWillAlertAt = willAlertAt;
        mContent = content;
    }

    @Override
    public void execute() {
        HashMap<String, String> headers = new HashMap<>();
        headers.put("x-user-id", WorkingData.getUserId());
        headers.put("x-auth-token", WorkingData.getAuthToken());

        HashMap<String, String> bodies = new HashMap<>();
        bodies.put("td", mTaskId);
        bodies.put("wat", "" + mWillAlertAt);
        bodies.put("c", mContent);

        PostRequestStrategy postRequestStrategy = new PostRequestStrategy(LoadingDataUtils.WorkingDataUrl.EndPoints.INCREMENT_TASK_ALERT, headers, bodies);
        mPostRequestAsyncTask = new PostRequestAsyncTask(mContext, postRequestStrategy, this);
        mPostRequestAsyncTask.execute();
    }


    @Override
    public void onFinishPostingData() {

    }
    @Override
    public void onFailPostingData(boolean isFailCausedByInternet) {

    }
}
