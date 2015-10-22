package com.bananaplan.workflowandroid.data.worker.actions;

import android.util.Log;

import com.bananaplan.workflowandroid.data.WorkingData;
import com.bananaplan.workflowandroid.data.loading.LoadingDataUtils;
import com.bananaplan.workflowandroid.data.loading.RestfulUtils;
import com.bananaplan.workflowandroid.data.loading.URLUtils;
import com.bananaplan.workflowandroid.data.network.IPostRequestStrategy;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

/**
 * Created by daz on 10/22/15.
 */
public class CompleteTaskForWorkerStrategy implements IPostRequestStrategy {

    private static final String TAG = CompleteTaskForWorkerStrategy.class.toString();

    private String mWorkerId;
    private String mTaskId;

    public CompleteTaskForWorkerStrategy (String workerId, String taskId) {
        mWorkerId = workerId;
        mTaskId = taskId;
    }

    @Override
    public JSONObject post() {
        try {
            HashMap<String, String> headers = new HashMap<>();
            headers.put("x-user-id", WorkingData.getUserId());
            headers.put("x-auth-token", WorkingData.getAuthToken());

            HashMap<String, String> bodies = new HashMap<>();
            bodies.put("ed", mWorkerId);
            bodies.put("td", mTaskId);

            String urlString = URLUtils.buildURLString(LoadingDataUtils.WorkingDataUrl.BASE_URL, LoadingDataUtils.WorkingDataUrl.EndPoints.COMPLETE_TASK, null);
            String responseString = RestfulUtils.restfulPostRequest(urlString, headers, bodies);

            if (responseString != null) {
                JSONObject jsonObject = new JSONObject(responseString);
                if (jsonObject.getString("status").equals("success")) {
                    return jsonObject.getJSONObject("result");
                }
            }
        }  catch (JSONException e) {
            Log.e(TAG, "Exception in CompleteTaskForWorkerStrategy()");
            e.printStackTrace();
        }
        return null;
    }
}
