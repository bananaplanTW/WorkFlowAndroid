package com.bananaplan.workflowandroid.data.warning.actions;

import android.content.Context;

import com.bananaplan.workflowandroid.data.WorkingData;
import com.bananaplan.workflowandroid.data.activity.actions.PostRequestStrategy;
import com.bananaplan.workflowandroid.data.loading.LoadingDataUtils;
import com.bananaplan.workflowandroid.data.network.PostRequestAsyncTask;

import java.util.HashMap;

/**
 * Created by daz on 10/28/15.
 */
public class CreateTaskWarningCommand implements IWarningActionCommand, PostRequestAsyncTask.OnFinishPostingDataListener {

    private PostRequestAsyncTask mPostRequestAsyncTask;

    private Context mContext;
    private String mTaskId;
    private String mWarningId;
    private String mMangerId;
    private String mDescription;

    public CreateTaskWarningCommand(Context context, String taskId, String warningId, String managerId, String description) {
        mContext = context;
        mTaskId = taskId;
        mWarningId = warningId;
        mMangerId = managerId;
        mDescription = description;
    }


    @Override
    public void execute() {
        HashMap<String, String> headers = new HashMap<>();
        headers.put("x-user-id", WorkingData.getUserId());
        headers.put("x-auth-token", WorkingData.getAuthToken());

        HashMap<String, String> bodies = new HashMap<>();
        bodies.put("td", mTaskId);
        bodies.put("exd", mWarningId);
        bodies.put("md", mMangerId);
        bodies.put("d", mDescription);

        PostRequestStrategy postRequestStrategy = new PostRequestStrategy(LoadingDataUtils.WorkingDataUrl.EndPoints.CREATE_TASK_EXCEPTION, headers, bodies);
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
