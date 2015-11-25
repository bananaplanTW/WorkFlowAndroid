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
 * Created by daz on 10/23/15.
 */
public class UpdateEmployeeScoreStrategy implements IPostRequestStrategy {

    private static final String TAG = CompleteTaskForWorkerStrategy.class.toString();

    private String mWorkerId;
    private int mScore;

    public UpdateEmployeeScoreStrategy (String workerId, int score) {
        mWorkerId = workerId;
        mScore = score;
    }

    @Override
    public JSONObject post() {
        try {
            HashMap<String, String> headers = new HashMap<>();
            headers.put("x-user-id", WorkingData.getUserId());
            headers.put("x-auth-token", WorkingData.getAuthToken());

            HashMap<String, String> bodies = new HashMap<>();
            bodies.put("ed", mWorkerId);
            bodies.put("sc", "" + mScore);

            String urlString = URLUtils.buildURLString(LoadingDataUtils.sBaseUrl, LoadingDataUtils.WorkingDataUrl.EndPoints.SCORE_EMPLOYEE, null);
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
