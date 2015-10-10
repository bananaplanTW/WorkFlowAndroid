package com.bananaplan.workflowandroid.data.activity;

import android.content.Context;

import com.bananaplan.workflowandroid.data.dataobserver.DataObserver;
import com.bananaplan.workflowandroid.data.dataobserver.DataSubject;
import com.bananaplan.workflowandroid.data.loading.LoadingWorkerActivitiesAsyncTask;
import com.bananaplan.workflowandroid.data.worker.status.BaseData;
import com.bananaplan.workflowandroid.data.worker.status.ActivityDataFactory;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by daz on 10/9/15.
 */
public class ActivityDataStore implements DataSubject, LoadingWorkerActivitiesAsyncTask.OnFinishLoadingDataListener {

    private Context mContext;
    private HashMap<String, LoadingWorkerActivitiesAsyncTask> loadingWorkerActivitiesAsyncTaskHashMap = new HashMap<>();

    private List<DataObserver> mDataObservers = new ArrayList<>();

    private HashMap<String, ArrayList<BaseData>> mWorkerActivitiesCache = new HashMap<>();
    private HashMap<String, ArrayList<BaseData>> mTaskActivityesCache = new HashMap<>();
    private HashMap<String, ArrayList<BaseData>> mWarningActivitiesCache = new HashMap<>();

    private volatile static ActivityDataStore sActivityData = null;

    public static final ActivityDataStore getInstance(Context context) {
        if (sActivityData == null) {
            synchronized (ActivityDataStore.class) {
                if (sActivityData == null) {
                    sActivityData = new ActivityDataStore(context);
                }
            }
        }
        return sActivityData;
    }
    private ActivityDataStore(Context context) {
        mContext = context;
    }


    public void loadWorkerActivities(String workerId, int limit) {
        if (!loadingWorkerActivitiesAsyncTaskHashMap.containsKey(workerId)) {
            synchronized (ActivityDataStore.class) {
                if (!loadingWorkerActivitiesAsyncTaskHashMap.containsKey(workerId)) {
                    LoadingWorkerActivitiesAsyncTask loadingWorkerActivitiesTask = new LoadingWorkerActivitiesAsyncTask(mContext, workerId, limit, this);
                    loadingWorkerActivitiesTask.execute();
                    loadingWorkerActivitiesAsyncTaskHashMap.put(workerId, loadingWorkerActivitiesTask);
                }
            }
        }
    }
    public ArrayList<BaseData> getWorkerActivities(String workerId) {
        return mWorkerActivitiesCache.get(workerId);
    }
    public boolean hasWorkerActivitiesCacheWithWorkerId(String workerId) {
        return mWorkerActivitiesCache.get(workerId) != null;
    }


    private void removeLoadingWorkerActivitiesAsyncTaskFromHashMap(String workerId) {
        if (loadingWorkerActivitiesAsyncTaskHashMap.containsKey(workerId)) {
            synchronized (ActivityDataStore.class) {
                if (loadingWorkerActivitiesAsyncTaskHashMap.containsKey(workerId)) {
                    loadingWorkerActivitiesAsyncTaskHashMap.remove(workerId);
                }
            }
        }
    }
    private void putActivityDataArrayListToCache(String workerId, ArrayList<BaseData> activityDataArrayList) {
        synchronized (ActivityDataStore.class) {
            if (mWorkerActivitiesCache.containsKey(workerId)) {
                mWorkerActivitiesCache.remove(workerId);
            }
            mWorkerActivitiesCache.put(workerId, activityDataArrayList);
        }
    }


    /**
     * OnFinishLoadingData Callbacks
     */
    @Override
    public void onFinishLoadingData(String workerId) {
        LoadingWorkerActivitiesAsyncTask loadingWorkerActivitiesTask = loadingWorkerActivitiesAsyncTaskHashMap.get(workerId);
        if (loadingWorkerActivitiesTask != null) {
            JSONArray recordJSONArray = loadingWorkerActivitiesTask.getResult();
            if (recordJSONArray != null) {
                ArrayList<BaseData> recordDataArrayList = parseWorkerActivityJSONArray(recordJSONArray);
                if (recordDataArrayList != null) {
                    putActivityDataArrayListToCache(workerId, recordDataArrayList);
                    notifyDataObservers();
                }
            }
            removeLoadingWorkerActivitiesAsyncTaskFromHashMap(workerId);
        }
    }
    @Override
    public void onFailLoadingData(boolean isFailCausedByInternet) {

    }


    @Override
    public void registerDataObserver(DataObserver o) {
        synchronized (ActivityDataStore.class) {
            mDataObservers.add(o);
        }
    }
    @Override
    public void removeDataObserver(DataObserver o) {
        synchronized (ActivityDataStore.class) {
            int index = mDataObservers.indexOf(o);
            if (index >= 0) {
                mDataObservers.remove(index);
            }
        }
    }
    @Override
    public void notifyDataObservers() {
        synchronized (ActivityDataStore.class) {
            for (DataObserver dataObserver : mDataObservers) {
                dataObserver.updateData();
            }
        }
    }


    private ArrayList<BaseData> parseWorkerActivityJSONArray(JSONArray activities) {
        ArrayList<BaseData> parsedActivities = new ArrayList<>();

        int length = activities.length();

        try {
            for (int i = 0; i < length; i++) {
                JSONObject activity = activities.getJSONObject(i);
                BaseData activityData = ActivityDataFactory.genData(activity);
                if (activityData != null) {
                    parsedActivities.add(activityData);
                }
            }
            return parsedActivities;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }
}
