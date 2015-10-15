package com.bananaplan.workflowandroid.data.activity.actions;

import android.util.Log;

import com.bananaplan.workflowandroid.data.loading.LoadingDataUtils;
import com.bananaplan.workflowandroid.data.loading.RestfulUtils;
import com.bananaplan.workflowandroid.data.loading.URLUtils;
import com.bananaplan.workflowandroid.data.network.IPostRequestStrategy;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

/**
 * Created by daz on 10/15/15.
 */
public class UploadingCommentStrategy implements IPostRequestStrategy {

    private static final String TAG = UploadingCommentStrategy.class.toString();

    private String mWorkerId;
    private String mComment;

    public UploadingCommentStrategy (String workerId, String comment) {
        mWorkerId = workerId;
        mComment = comment;
    }

    @Override
    public JSONObject post() {
        try {
            HashMap<String, String> headers = new HashMap<>();
            // [TODO] should login with user
            headers.put("x-user-id", "qY7FdM7wnjevqmfws");
            headers.put("x-auth-token", "el1UPAsSmVf8F1LEKf8tRb8Ny5jAgOdK2qLNHztb7Cj");

            HashMap<String, String> queries = new HashMap<>();
            queries.put("ed", mWorkerId);
            queries.put("msg", mComment);

            String urlString = URLUtils.buildURLString(LoadingDataUtils.WorkingDataUrl.BASE_URL, LoadingDataUtils.WorkingDataUrl.EndPoints.COMMENT_TEXT_ACTIVITY, null);
            String responseString = RestfulUtils.restfulPostRequest(urlString, headers, queries);

            if (responseString != null) {
                JSONObject jsonObject = new JSONObject(responseString);
                if (jsonObject.getString("status").equals("success")) {
                    return jsonObject.getJSONObject("result");
                }
            }
        }  catch (JSONException e) {
            Log.e(TAG, "Exception in LoadingWorkerActivitiesStrategy()");
            e.printStackTrace();
        }
        return null;
    }
}