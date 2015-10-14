package com.bananaplan.workflowandroid.data.network;

import android.content.Context;
import android.os.AsyncTask;

import com.bananaplan.workflowandroid.data.loading.RestfulUtils;
import com.bananaplan.workflowandroid.main.MainApplication;

import org.json.JSONObject;

/**
 * Created by daz on 10/11/15.
 */
public class PostRequestAsyncTask extends AsyncTask<Void, Void, JSONObject> {

    public interface OnFinishPostingDataListener {
        void onFinishPostingData();
        void onFailPostingData(boolean isFailCausedByInternet);
    }

    private Context mContext;
    private IPostRequestStrategy mPostRequestStrategy;
    private OnFinishPostingDataListener mOnFinishPostingDataListener;

    public PostRequestAsyncTask(Context context, IPostRequestStrategy postRequestStrategy, OnFinishPostingDataListener onFinishPostingDataListener) {
        mContext = context;
        mPostRequestStrategy = postRequestStrategy;
        mOnFinishPostingDataListener = onFinishPostingDataListener;
    }

    @Override
    protected JSONObject doInBackground(Void... voids) {
        if (!MainApplication.sUseTestData) {
            if (RestfulUtils.isConnectToInternet(mContext)) {
                mPostRequestStrategy.post();
            } else {
                cancel(true);
            }
        }
        return null;
    }
    @Override
    protected void onCancelled(JSONObject jsonObject) {
        super.onCancelled(jsonObject);
        mOnFinishPostingDataListener.onFailPostingData(true);
    }
    @Override
    protected void onPostExecute(JSONObject jsonObject) {
        super.onPostExecute(jsonObject);
        mOnFinishPostingDataListener.onFinishPostingData();
    }
}
