package com.bananaplan.workflowandroid.data.task.actions;

import android.content.Context;

import com.bananaplan.workflowandroid.data.WorkingData;
import com.bananaplan.workflowandroid.data.activity.actions.PostRequestStrategy;
import com.bananaplan.workflowandroid.data.loading.LoadingDataUtils;
import com.bananaplan.workflowandroid.data.network.PostRequestAsyncTask;

import java.util.HashMap;

/**
 * Created by daz on 10/25/15.
 */
public class PassTaskCommand implements ITaskActionCommand, PostRequestAsyncTask.OnFinishPostingDataListener {

    private PostRequestAsyncTask mPostRequestAsyncTask;
    private Context mContext;
    private String mTaskId;

    public PassTaskCommand (Context context, String taskId) {
        mContext = context;
        mTaskId = taskId;
    }


    @Override
    public void execute() {
        HashMap<String, String> headers = new HashMap<>();
        headers.put("x-user-id", WorkingData.getUserId());
        headers.put("x-auth-token", WorkingData.getAuthToken());

        HashMap<String, String> bodies = new HashMap<>();
        bodies.put("td", mTaskId);

        PostRequestStrategy postRequestStrategy = new PostRequestStrategy(LoadingDataUtils.WorkingDataUrl.EndPoints.PASS_TASK, headers, bodies);
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
