package com.bananaplan.workflowandroid.data.loading.loadingworkerattendance;

import android.content.Context;
import android.util.Log;

import com.bananaplan.workflowandroid.data.WorkingData;
import com.bananaplan.workflowandroid.data.loading.LoadingDataUtils;
import com.bananaplan.workflowandroid.data.loading.RestfulUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

/**
 * @author Danny Lin
 * @since 10/20/15.
 */
public class LoadingWorkerAttendanceStrategy implements ILoadingWorkerAttendanceStrategy {

    private static final String TAG = LoadingWorkerAttendanceStrategy.class.toString();

    private Context mContext;

    private String mWorkerId;
    private long mStartDate;
    private long mEndDate;


    public LoadingWorkerAttendanceStrategy(Context context, String workerId, long startDate, long endDate) {
        mContext = context;
        mWorkerId = workerId;
        mStartDate = startDate;
        mEndDate = endDate;
    }

    @Override
    public void load() {
        try {
            HashMap<String, String> headers = new HashMap<>();
            headers.put("x-user-id", WorkingData.getUserId());
            headers.put("x-auth-token", WorkingData.getAuthToken());

            String urlString = getWorkerAttendanceUrl();
            String responseJSONString = RestfulUtils.restfulGetRequest(urlString, headers);
            JSONObject responseJSON = new JSONObject(responseJSONString);

            if (responseJSON.getString("status").equals("success")) {
                JSONArray attendanceList = responseJSON.getJSONArray("result");

                for (int i = 0 ; i < attendanceList.length() ; i++) {
                    WorkingData.getInstance(mContext).getWorkerById(mWorkerId).
                            addAttendance(LoadingDataUtils.retrieveWorkerAttendance(attendanceList.getJSONObject(i)));
                }
            }

        } catch (JSONException e) {
            Log.e(TAG, "Exception in LoadingWorkerAttendanceStrategy()");
            e.printStackTrace();
        }
    }

    private String getWorkerAttendanceUrl() {
        String url = String.format(LoadingDataUtils.WorkingDataUrl.WORKER_ATTENDANCE, mWorkerId, mStartDate, mEndDate);
        Log.d(TAG, "getWorkerAttendanceUrl url = " + url);

        return url;
    }
}
