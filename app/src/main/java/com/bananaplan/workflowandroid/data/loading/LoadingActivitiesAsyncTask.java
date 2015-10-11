package com.bananaplan.workflowandroid.data.loading;

import android.content.Context;
import android.os.AsyncTask;

import com.bananaplan.workflowandroid.data.loading.loadingactivities.ILoadingActivitiesStrategy;
import com.bananaplan.workflowandroid.main.MainApplication;

import org.json.JSONArray;

import java.util.concurrent.Callable;

/**
 * Created by daz on 10/10/15.
 */
public class LoadingActivitiesAsyncTask extends AsyncTask<Void, Void, JSONArray> {

    public interface OnFinishLoadingDataListener {
        void onFinishLoadingData(String id, ILoadingActivitiesStrategy.ActivityCategory category, JSONArray activities);
        void onFailLoadingData(boolean isFailCausedByInternet);
    }

    private ILoadingActivitiesStrategy mLoadingActivitiesStrategy;
    private OnFinishLoadingDataListener mOnFinishLoadingDataListener;
    private Context mContext;
    private String mWorkerId;
    private JSONArray result = new JSONArray();

    public LoadingActivitiesAsyncTask(Context context, String workerId, OnFinishLoadingDataListener listener, ILoadingActivitiesStrategy loadingActivitiesStrategy) {
        mContext = context;
        mOnFinishLoadingDataListener = listener;
        mWorkerId = workerId;
        mLoadingActivitiesStrategy = loadingActivitiesStrategy;
    }


    public JSONArray getResult () {
        return result;
    }


    @Override
    protected JSONArray doInBackground(Void... voids) {
        if (!MainApplication.sUseTestData) {
            if (RestfulUtils.isConnectToInternet(mContext)) {
                return mLoadingActivitiesStrategy.load();
            } else {
                cancel(true);
            }
        }
        return null;
    }
    @Override
    protected void onCancelled(JSONArray jsonArray) {
        super.onCancelled(jsonArray);
        mOnFinishLoadingDataListener.onFailLoadingData(true);
    }
    @Override
    protected void onPostExecute(JSONArray jsonArray) {
        super.onPostExecute(jsonArray);
        mOnFinishLoadingDataListener.onFinishLoadingData(mWorkerId, mLoadingActivitiesStrategy.getCategory(), jsonArray);
    }
}
