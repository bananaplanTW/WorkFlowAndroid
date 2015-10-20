package com.bananaplan.workflowandroid.login;

import android.content.Context;
import android.util.Log;

import com.bananaplan.workflowandroid.data.network.PostRequestAsyncTask;
import com.bananaplan.workflowandroid.data.worker.actions.IWorkerActionCommand;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

/**
 * Created by daz on 10/20/15.
 */
public class UserLoggingInCommand implements IWorkerActionCommand, PostRequestAsyncTask.OnFinishPostingDataListener {

    public interface OnFinishLoggedInListener {
        void onLoggedInSucceed(String userId, String authToken);
        void onLoggedInFailed();
    }

    private Context mContext;

    private OnFinishLoggedInListener mOnFinishLoggedInListener;
    private PostRequestAsyncTask mPostRequestAsyncTask;
    private String mUsername;
    private String mPassword;

    public UserLoggingInCommand (Context context, String username, String password, OnFinishLoggedInListener onFinishLoggedInListener) {
        mContext = context;
        mUsername = username;
        mPassword = password;
        mOnFinishLoggedInListener = onFinishLoggedInListener;
    }

    @Override
    public void execute() {
        HashMap<String, String> bodies = new HashMap<>();
        bodies.put("username", mUsername);
        bodies.put("password", mPassword);

        UserLoggingInStrategy userLoggingInStrategy = new UserLoggingInStrategy(bodies);
        mPostRequestAsyncTask = new PostRequestAsyncTask(mContext, userLoggingInStrategy, this);
        mPostRequestAsyncTask.execute();
    }


    @Override
    public void onFinishPostingData() {
        JSONObject result = mPostRequestAsyncTask.getResult();
        if (result != null) {
            try {
                mOnFinishLoggedInListener.onLoggedInSucceed(result.getString("userId"), result.getString("authToken"));
            } catch (JSONException e) {
                e.printStackTrace();
                mOnFinishLoggedInListener.onLoggedInFailed();
            }
        } else {
            mOnFinishLoggedInListener.onLoggedInFailed();
        }
    }
    @Override
    public void onFailPostingData(boolean isFailCausedByInternet) {
        mOnFinishLoggedInListener.onLoggedInFailed();
    }
}
