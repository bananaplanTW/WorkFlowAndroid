package com.bananaplan.workflowandroid.data.warning.actions;

import android.content.Context;

import com.bananaplan.workflowandroid.data.WorkingData;
import com.bananaplan.workflowandroid.data.activity.actions.PostRequestStrategy;
import com.bananaplan.workflowandroid.data.loading.LoadingDataUtils;
import com.bananaplan.workflowandroid.data.network.PostRequestAsyncTask;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.concurrent.ExecutionException;

/**
 * Created by daz on 10/28/15.
 */
public class CreateTaskWarningCommand implements IWarningActionCommand, PostRequestAsyncTask.OnFinishPostingDataListener {

    public interface OnFinishedCreatingTaskWarningListener {
        void onCreatedTaskWarning(String taskWarningId);
        void onFailCreatingTaskWarning();
    }

    private PostRequestAsyncTask mPostRequestAsyncTask;
    private OnFinishedCreatingTaskWarningListener mOnFinishedCreatingTaskWarningListener;

    private Context mContext;
    private String mTaskId;
    private String mWarningId;
    private String mMangerId;
    private String mDescription;

    public CreateTaskWarningCommand(Context context, String taskId, String warningId, String managerId, String description, OnFinishedCreatingTaskWarningListener onFinishedCreatingTaskWarningListener) {
        mContext = context;
        mTaskId = taskId;
        mWarningId = warningId;
        mMangerId = managerId;
        mDescription = description;
        mOnFinishedCreatingTaskWarningListener = onFinishedCreatingTaskWarningListener;
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
        try {
            JSONObject result = mPostRequestAsyncTask.get();
            mOnFinishedCreatingTaskWarningListener.onCreatedTaskWarning(result.getString("_id"));
            return;
        } catch (InterruptedException e) {
            e.printStackTrace();
            mOnFinishedCreatingTaskWarningListener.onFailCreatingTaskWarning();
        } catch (ExecutionException e) {
            e.printStackTrace();
            mOnFinishedCreatingTaskWarningListener.onFailCreatingTaskWarning();
        } catch (JSONException e) {
            e.printStackTrace();
            mOnFinishedCreatingTaskWarningListener.onFailCreatingTaskWarning();
        }
    }
    @Override
    public void onFailPostingData(boolean isFailCausedByInternet) {
        mOnFinishedCreatingTaskWarningListener.onFailCreatingTaskWarning();
    }
}
