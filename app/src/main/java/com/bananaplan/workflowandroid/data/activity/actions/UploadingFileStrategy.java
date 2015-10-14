package com.bananaplan.workflowandroid.data.activity.actions;

import android.content.Context;
import android.util.Log;

import com.bananaplan.workflowandroid.data.loading.LoadingDataUtils;
import com.bananaplan.workflowandroid.data.loading.RestfulUtils;
import com.bananaplan.workflowandroid.data.loading.URLUtils;
import com.bananaplan.workflowandroid.data.network.IPostRequestStrategy;
import com.bananaplan.workflowandroid.utility.Utils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

/**
 * Created by daz on 10/14/15.
 */
public class UploadingFileStrategy implements IPostRequestStrategy {

    private static final String TAG = UploadingFileStrategy.class.toString();

    private String mWorkerId;
    private String mFilePath;

    public UploadingFileStrategy (String workerId, String filePath) {
        mWorkerId = workerId;
        mFilePath = filePath;
    }


    @Override
    public JSONObject post() {
        try {
            HashMap<String, String> queries = new HashMap<>();
            queries.put("ed", mWorkerId);
            // [TODO] should login with user
            queries.put("ud", "qY7FdM7wnjevqmfws");
            queries.put("t", "el1UPAsSmVf8F1LEKf8tRb8Ny5jAgOdK2qLNHztb7Cj");

            String urlString = URLUtils.buildURLString(LoadingDataUtils.WorkingDataUrl.BASE_URL, LoadingDataUtils.WorkingDataUrl.EndPoints.COMMENT_FILE_ACTIVITY, null);
            String responseString = RestfulUtils.restfulPostFileRequest(urlString, queries, mFilePath);
            JSONObject jsonObject = new JSONObject(responseString);
            if (jsonObject.getString("status").equals("success")) {
                return jsonObject.getJSONObject("result");
            }
        }  catch (JSONException e) {
            Log.e(TAG, "Exception in LoadingWorkerActivitiesStrategy()");
            e.printStackTrace();
        }

        return null;
    }
}
