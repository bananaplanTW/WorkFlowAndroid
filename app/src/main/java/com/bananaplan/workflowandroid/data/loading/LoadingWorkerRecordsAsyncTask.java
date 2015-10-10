package com.bananaplan.workflowandroid.data.loading;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.bananaplan.workflowandroid.main.MainApplication;

import org.json.JSONArray;

/**
 * Created by daz on 10/10/15.
 */
public class LoadingWorkerRecordsAsyncTask extends AsyncTask<Void, Void, JSONArray> {

    public interface OnFinishLoadingDataListener {
        void onFinishLoadingData(String workerId);
        void onFailLoadingData(boolean isFailCausedByInternet);
    }

    private OnFinishLoadingDataListener mOnFinishLoadingDataListener;
    private Context mContext;
    private String mWorkerId;
    private int recordLimit;
    private JSONArray result = new JSONArray();

    public LoadingWorkerRecordsAsyncTask(Context context, String workerId, int limit, OnFinishLoadingDataListener listener) {
        mContext = context;
        mOnFinishLoadingDataListener = listener;
        mWorkerId = workerId;
        recordLimit = limit;
    }


    public JSONArray getResult () {
        return result;
    }


    @Override
    protected JSONArray doInBackground(Void... voids) {
        if (!MainApplication.sUseTestData) {
            if (RestfulUtils.isConnectToInternet(mContext)) {
                return LoadingDataUtils.LoadRecordsByWorker(mWorkerId, recordLimit);
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
        result = jsonArray;
        mOnFinishLoadingDataListener.onFinishLoadingData(mWorkerId);
    }
}
