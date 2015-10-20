package com.bananaplan.workflowandroid.login;

import android.content.Context;
import android.util.Log;

import com.bananaplan.workflowandroid.data.network.GetRequestAsyncTask;
import com.bananaplan.workflowandroid.data.worker.actions.IWorkerActionCommand;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by daz on 10/20/15.
 */
public class CheckLoggedInStatusCommand implements IWorkerActionCommand, GetRequestAsyncTask.OnFinishGettingDataListener {

    public interface OnFinishCheckingLoggedInStatusListener {
        void onLoggedIn();
        void onLoggedOut();
    }

    private GetRequestAsyncTask mGetRequestAsyncTask;
    private OnFinishCheckingLoggedInStatusListener mOnFinishCheckingLoggedInStatusListener;

    private Context mContext;

    public CheckLoggedInStatusCommand (Context context, OnFinishCheckingLoggedInStatusListener onFinishCheckingLoggedInStatusListener) {
        mContext = context;
        mOnFinishCheckingLoggedInStatusListener = onFinishCheckingLoggedInStatusListener;
    }


    @Override
    public void execute() {
        CheckLoggedInStatusStrategy checkLoggedInStatusStrategy = new CheckLoggedInStatusStrategy();
        mGetRequestAsyncTask = new GetRequestAsyncTask(mContext, checkLoggedInStatusStrategy, this);
        mGetRequestAsyncTask.execute();
    }


    @Override
    public void onFinishGettingData() {
        JSONObject result = mGetRequestAsyncTask.getResult();
        try {
            if (result != null && result.getString("ud") != null) {
                mOnFinishCheckingLoggedInStatusListener.onLoggedIn();
            } else {
                mOnFinishCheckingLoggedInStatusListener.onLoggedOut();
            }
        } catch (JSONException e) {
            e.printStackTrace();
            mOnFinishCheckingLoggedInStatusListener.onLoggedOut();
        }
    }
    @Override
    public void onFailGettingData(boolean isFailCausedByInternet) {
        mOnFinishCheckingLoggedInStatusListener.onLoggedOut();
    }
}
