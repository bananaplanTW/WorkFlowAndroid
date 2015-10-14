package com.bananaplan.workflowandroid.data.activity.actions;

import android.content.Context;

import com.bananaplan.workflowandroid.data.network.PostRequestAsyncTask;

import org.json.JSONObject;

/**
 * Created by daz on 10/14/15.
 */
public class LeaveAPhotoCommentToWorkerCommand implements ICreateActivityCommand, PostRequestAsyncTask.OnFinishPostingDataListener {


    private PostRequestAsyncTask mPostRequestAsyncTask;
    private Context mContext;
    private String mWorkerId;
    private String mPhotoPath;


    public LeaveAPhotoCommentToWorkerCommand(Context context, String workerId, String photoPath) {
        mContext = context;
        mWorkerId = workerId;
        mPhotoPath = photoPath;
    }


    @Override
    public void execute() {
        UploadingImageStrategy uploadingImageStrategy = new UploadingImageStrategy(mWorkerId, mPhotoPath);
        mPostRequestAsyncTask = new PostRequestAsyncTask(mContext, uploadingImageStrategy, this);
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
