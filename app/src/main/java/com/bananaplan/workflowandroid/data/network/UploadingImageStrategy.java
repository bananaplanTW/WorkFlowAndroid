package com.bananaplan.workflowandroid.data.network;

import android.util.Log;

import com.bananaplan.workflowandroid.data.loading.LoadingDataUtils;
import com.bananaplan.workflowandroid.data.loading.RestfulUtils;
import com.bananaplan.workflowandroid.data.loading.URLUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

/**
 * Created by daz on 10/13/15.
 */
public class UploadingImageStrategy implements IPostRequestStrategy {

    private static final String TAG = UploadingImageStrategy.class.toString();

    private String mFilePath;
    private String mWorkerId;

    public UploadingImageStrategy(String workerId, String filePath) {
        mFilePath = filePath;
        mWorkerId = workerId;
    }

    @Override
    public JSONObject post() {
        try {
            HashMap<String, String> queries = new HashMap<>();
            queries.put("ed", mWorkerId);
            // [TODO] should login with user
            queries.put("ud", "qY7FdM7wnjevqmfws");
            queries.put("t", "el1UPAsSmVf8F1LEKf8tRb8Ny5jAgOdK2qLNHztb7Cj");

            String urlString = URLUtils.buildURLString(LoadingDataUtils.WorkingDataUrl.BASE_URL, LoadingDataUtils.WorkingDataUrl.EndPoints.COMMENT_IMAGE_ACTIVITY, null);
            String responseString = RestfulUtils.restfulPostFileRequest(urlString, queries, mFilePath, "image/png");
            JSONObject jsonObject = new JSONObject(responseString);
            if (jsonObject.getString("status") == "success") {
                return jsonObject.getJSONObject("result");
            }
        }  catch (JSONException e) {
            Log.e(TAG, "Exception in LoadingWorkerActivitiesStrategy()");
            e.printStackTrace();
        }


        return null;
    }
}
