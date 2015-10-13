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

    public UploadingImageStrategy(String filePath, String workerId) {
        mFilePath = filePath;
        mWorkerId = workerId;
    }

    @Override
    public JSONObject post() {
        try {
            HashMap<String, String> queries = new HashMap<>();
            queries.put("workerId", mWorkerId);
            String urlString = URLUtils.buildURLString(LoadingDataUtils.WorkingDataUrl.DEBUG_BASE_URL, LoadingDataUtils.WorkingDataUrl.EndPoints.COMMENT_IMAGE_ACTIVITY, null);
            Log.d("DAZZZZ", "going to post image");
            String responseString = RestfulUtils.restfulPostFileRequest(urlString, queries, mFilePath);
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
