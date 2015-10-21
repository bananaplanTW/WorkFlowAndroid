package com.bananaplan.workflowandroid.data.activity.actions;

import android.content.Context;

import com.bananaplan.workflowandroid.data.WorkingData;
import com.bananaplan.workflowandroid.data.loading.LoadingDataUtils;
import com.bananaplan.workflowandroid.data.network.PostRequestAsyncTask;

import java.util.HashMap;

/**
 * Created by daz on 10/18/15.
 */
public class LeaveATextCommentToTaskCommand implements ICreateActivityCommand, PostRequestAsyncTask.OnFinishPostingDataListener {

    private PostRequestAsyncTask mPostRequestAsyncTask;
    private Context mContext;
    private String mTaskId;
    private String mComment;

    public LeaveATextCommentToTaskCommand(Context context, String taskId, String comment) {
        mContext = context;
        mTaskId = taskId;
        mComment = comment;
    }


    @Override
    public void execute() {
        HashMap<String, String> headers = new HashMap<>();
        headers.put("x-user-id", WorkingData.getUserId());
        headers.put("x-auth-token", WorkingData.getAuthToken());

        HashMap<String, String> bodies = new HashMap<>();
        bodies.put("td", mTaskId);
        bodies.put("msg", mComment);

        PostRequestStrategy uploadingCommentStrategy = new PostRequestStrategy(LoadingDataUtils.WorkingDataUrl.EndPoints.COMMENT_TEXT_ACTIVITY_TO_TASK, headers, bodies);
        mPostRequestAsyncTask = new PostRequestAsyncTask(mContext, uploadingCommentStrategy, this);
        mPostRequestAsyncTask.execute();
    }


    @Override
    public void onFinishPostingData() {

    }
    @Override
    public void onFailPostingData(boolean isFailCausedByInternet) {

    }
}
