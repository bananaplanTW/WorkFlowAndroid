package com.bananaplan.workflowandroid.data.warning.actions;

import android.content.Context;

import com.bananaplan.workflowandroid.data.WorkingData;
import com.bananaplan.workflowandroid.data.activity.actions.PostRequestStrategy;
import com.bananaplan.workflowandroid.data.loading.LoadingDataUtils;
import com.bananaplan.workflowandroid.data.network.PostRequestAsyncTask;

import java.util.HashMap;

/**
 * Created by daz on 11/4/15.
 */
public class CloseTaskWarningCommand implements IWarningActionCommand, PostRequestAsyncTask.OnFinishPostingDataListener {

    public interface OnFinishedClosingTaskWarningListener {
        void onClosedTaskWarning();
        void onFailClosingTaskWarning();
    }

    private PostRequestAsyncTask mPostRequestAsyncTask;
    private OnFinishedClosingTaskWarningListener mOnFinishedClosingTaskWarningListener;

    private Context mContext;
    private String mTaskWarningId;

    public CloseTaskWarningCommand(Context context, String taskWarningId, OnFinishedClosingTaskWarningListener onFinishedClosingTaskWarningListener) {
        mContext = context;
        mTaskWarningId = taskWarningId;
        mOnFinishedClosingTaskWarningListener = onFinishedClosingTaskWarningListener;
    }


    @Override
    public void execute() {
        HashMap<String, String> headers = new HashMap<>();
        headers.put("x-user-id", WorkingData.getUserId());
        headers.put("x-auth-token", WorkingData.getAuthToken());

        HashMap<String, String> bodies = new HashMap<>();
        bodies.put("ted", mTaskWarningId);

        PostRequestStrategy postRequestStrategy = new PostRequestStrategy(LoadingDataUtils.WorkingDataUrl.EndPoints.CLOSE_TASK_EXCEPTION, headers, bodies);
        mPostRequestAsyncTask = new PostRequestAsyncTask(mContext, postRequestStrategy, this);
        mPostRequestAsyncTask.execute();
    }


    @Override
    public void onFinishPostingData() {
        mOnFinishedClosingTaskWarningListener.onClosedTaskWarning();
    }

    @Override
    public void onFailPostingData(boolean isFailCausedByInternet) {
        mOnFinishedClosingTaskWarningListener.onFailClosingTaskWarning();
    }
}
