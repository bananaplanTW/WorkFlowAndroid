package com.bananaplan.workflowandroid.data.activity.actions;

import android.content.Context;

import com.bananaplan.workflowandroid.data.WorkingData;
import com.bananaplan.workflowandroid.data.loading.LoadingDataUtils;
import com.bananaplan.workflowandroid.data.network.PostRequestAsyncTask;

import org.json.JSONObject;

import java.util.HashMap;

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
        HashMap<String, String> headers = new HashMap<>();
        headers.put("x-user-id", WorkingData.getUserId());
        headers.put("x-auth-token", WorkingData.getAuthToken());

        HashMap<String, String> bodies = new HashMap<>();
        bodies.put("ed", mWorkerId);
        bodies.put("msg", mComment);

        PostRequestStrategy uploadingCommentStrategy = new PostRequestStrategy(LoadingDataUtils.WorkingDataUrl.EndPoints.COMMENT_TEXT_ACTIVITY, headers, bodies);
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
