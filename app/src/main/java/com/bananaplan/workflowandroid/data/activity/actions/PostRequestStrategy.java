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
public class PostRequestStrategy implements IPostRequestStrategy {

    private static final String TAG = PostRequestStrategy.class.toString();

    private HashMap<String, String> mHeaders;
    private HashMap<String, String> mBodies;
    private String mEndPoint;

    public PostRequestStrategy(String endPoint, HashMap<String, String> headers, HashMap<String, String> bodies) {
        mEndPoint = endPoint;
        mHeaders = headers;
        mBodies = bodies;
    }

    @Override
    public JSONObject post() {
        try {
            String urlString = URLUtils.buildURLString(LoadingDataUtils.sBaseUrl, mEndPoint, null);
            String responseString = RestfulUtils.restfulPostRequest(urlString, mHeaders, mBodies);
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
