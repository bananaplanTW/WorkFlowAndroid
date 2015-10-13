package com.bananaplan.workflowandroid.data.network;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.bananaplan.workflowandroid.data.loading.LoadingDataUtils;
import com.bananaplan.workflowandroid.data.loading.RestfulUtils;
import com.bananaplan.workflowandroid.data.loading.URLUtils;
import com.bananaplan.workflowandroid.main.MainApplication;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

/**
 * Created by daz on 10/11/15.
 */
public class PostRequestAsyncTask extends AsyncTask<Void, Void, Void> {

    private Context mContext;

    public PostRequestAsyncTask(Context context) {
        mContext = context;
    }

    @Override
    protected Void doInBackground(Void... voids) {
        if (!MainApplication.sUseTestData) {
            if (RestfulUtils.isConnectToInternet(mContext)) {
                // should use strategy pattern to outsource the algo of post request
                String urlString = URLUtils.buildURLString(LoadingDataUtils.WorkingDataUrl.DEBUG_BASE_URL, LoadingDataUtils.WorkingDataUrl.EndPoints.DISPATCH, null);
                String responseJSONString = RestfulUtils.restfulPostRequest(urlString, null);
                Log.d("DAZZZZ", "responseJSONString : " + responseJSONString);
//                JSONObject responseJSON = null;
//                try {
//                    responseJSON = new JSONObject(responseJSONString);
//
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }
            } else {
                cancel(true);
            }
        }
        return null;
    }
}
