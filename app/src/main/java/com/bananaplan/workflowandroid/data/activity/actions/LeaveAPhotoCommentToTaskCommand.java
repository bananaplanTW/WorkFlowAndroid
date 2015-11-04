package com.bananaplan.workflowandroid.data.activity.actions;

import android.content.Context;

import com.bananaplan.workflowandroid.R;
import com.bananaplan.workflowandroid.data.WorkingData;
import com.bananaplan.workflowandroid.data.loading.LoadingDataUtils;
import com.bananaplan.workflowandroid.data.network.PostRequestAsyncTask;
import com.bananaplan.workflowandroid.utility.Utils;

import java.util.HashMap;

/**
 * Created by daz on 10/17/15.
 */
public class LeaveAPhotoCommentToTaskCommand implements ICreateActivityCommand, PostRequestAsyncTask.OnFinishPostingDataListener {

    private PostRequestAsyncTask mPostRequestAsyncTask;
    private Context mContext;
    private String mTaskId;
    private String mFilePath;

    public LeaveAPhotoCommentToTaskCommand (Context context, String taskId, String filePath) {
        mContext = context;
        mTaskId = taskId;
        mFilePath = filePath;
    }


    @Override
    public void execute() {
        HashMap<String, String> headers = new HashMap<>();
        headers.put("x-user-id", WorkingData.getUserId());
        headers.put("x-auth-token", WorkingData.getAuthToken());
        headers.put("td", mTaskId);

        UploadingFileStrategy uploadingFileStrategy = new UploadingFileStrategy(mFilePath, LoadingDataUtils.WorkingDataUrl.EndPoints.COMMENT_IMAGE_ACTIVITY_TO_TASK, headers);
        mPostRequestAsyncTask = new PostRequestAsyncTask(mContext, uploadingFileStrategy, this);
        mPostRequestAsyncTask.execute();
    }


    @Override
    public void onFinishPostingData() {
        Utils.showToast(mContext,
                String.format(mContext.getString(R.string.status_record_completed), mContext.getString(R.string.ov_tab_photo)));
    }

    @Override
    public void onFailPostingData(boolean isFailCausedByInternet) {

    }
}
