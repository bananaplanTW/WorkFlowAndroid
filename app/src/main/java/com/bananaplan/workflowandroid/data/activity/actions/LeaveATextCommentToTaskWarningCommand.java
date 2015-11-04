package com.bananaplan.workflowandroid.data.activity.actions;

import android.content.Context;

import com.bananaplan.workflowandroid.R;
import com.bananaplan.workflowandroid.data.WorkingData;
import com.bananaplan.workflowandroid.data.loading.LoadingDataUtils;
import com.bananaplan.workflowandroid.data.network.PostRequestAsyncTask;
import com.bananaplan.workflowandroid.utility.Utils;

import java.util.HashMap;

/**
 * Created by daz on 10/30/15.
 */
public class LeaveATextCommentToTaskWarningCommand implements ICreateActivityCommand, PostRequestAsyncTask.OnFinishPostingDataListener {

    private PostRequestAsyncTask mPostRequestAsyncTask;
    private Context mContext;
    private String mTaskWarningId;
    private String mComment;

    public LeaveATextCommentToTaskWarningCommand(Context context, String taskWarningId, String comment) {
        mContext = context;
        mTaskWarningId = taskWarningId;
        mComment = comment;
    }


    @Override
    public void execute() {
        HashMap<String, String> headers = new HashMap<>();
        headers.put("x-user-id", WorkingData.getUserId());
        headers.put("x-auth-token", WorkingData.getAuthToken());

        HashMap<String, String> bodies = new HashMap<>();
        bodies.put("ted", mTaskWarningId);
        bodies.put("msg", mComment);

        PostRequestStrategy uploadingCommentStrategy = new PostRequestStrategy(LoadingDataUtils.WorkingDataUrl.EndPoints.COMMENT_TEXT_ACTIVITY_TO_TASK_WARNING, headers, bodies);
        mPostRequestAsyncTask = new PostRequestAsyncTask(mContext, uploadingCommentStrategy, this);
        mPostRequestAsyncTask.execute();
    }


    @Override
    public void onFinishPostingData() {
        Utils.showToast(mContext,
                String.format(mContext.getString(R.string.status_record_completed), mContext.getString(R.string.ov_message)));
    }

    @Override
    public void onFailPostingData(boolean isFailCausedByInternet) {

    }
}
