package com.bananaplan.workflowandroid.data.activity.actions;

import android.content.Context;

import com.bananaplan.workflowandroid.R;
import com.bananaplan.workflowandroid.data.WorkingData;
import com.bananaplan.workflowandroid.data.loading.LoadingDataUtils;
import com.bananaplan.workflowandroid.data.network.PostRequestAsyncTask;
import com.bananaplan.workflowandroid.utility.Utils;

import org.json.JSONObject;

import java.util.HashMap;

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
        HashMap<String, String> headers = new HashMap<>();
        headers.put("x-user-id", WorkingData.getUserId());
        headers.put("x-auth-token", WorkingData.getAuthToken());
        headers.put("ed", mWorkerId);

        UploadingFileStrategy uploadingFileStrategy = new UploadingFileStrategy(mPhotoPath, LoadingDataUtils.WorkingDataUrl.EndPoints.COMMENT_IMAGE_ACTIVITY, headers);
        mPostRequestAsyncTask = new PostRequestAsyncTask(mContext, uploadingFileStrategy, this);
        mPostRequestAsyncTask.execute();
    }


    @Override
    public void onFinishPostingData() {
        JSONObject result = mPostRequestAsyncTask.getResult();
        Utils.showToast(mContext,
                String.format(mContext.getString(R.string.status_record_completed), mContext.getString(R.string.ov_tab_photo)));
    }

    @Override
    public void onFailPostingData(boolean isFailCausedByInternet) {

    }
}
