package com.bananaplan.workflowandroid.data.activity;

import android.content.Context;

import com.bananaplan.workflowandroid.data.dataobserver.DataObserver;
import com.bananaplan.workflowandroid.data.dataobserver.DataSubject;
import com.bananaplan.workflowandroid.data.loading.LoadingActivitiesAsyncTask;
import com.bananaplan.workflowandroid.data.loading.loadingactivities.ILoadingActivitiesStrategy;
import com.bananaplan.workflowandroid.data.loading.loadingactivities.LoadingWorkerActivitiesStrategy;
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
public class ActivityDataStore implements DataSubject, LoadingActivitiesAsyncTask.OnFinishLoadingDataListener {

    private Context mContext;
    private HashMap<String, LoadingActivitiesAsyncTask> loadingWorkerActivitiesAsyncTaskHashMap = new HashMap<>();
    private HashMap<String, LoadingActivitiesAsyncTask> loadingTaskActivitiesAsyncTaskHashMap = new HashMap<>();

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


    /**
     * Load worker activities
     * @param workerId
     * @param limit
     */
    public void loadWorkerActivities(String workerId, int limit) {
        if (!loadingWorkerActivitiesAsyncTaskHashMap.containsKey(workerId)) {
            synchronized (ActivityDataStore.class) {
                if (!loadingWorkerActivitiesAsyncTaskHashMap.containsKey(workerId)) {
                    LoadingWorkerActivitiesStrategy loadingWorkerActivitiesStrategy = new LoadingWorkerActivitiesStrategy(workerId, 15);
                    LoadingActivitiesAsyncTask loadingWorkerActivitiesTask = new LoadingActivitiesAsyncTask(mContext, workerId, this, loadingWorkerActivitiesStrategy);
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


//    public void loadTaskActivities(String taskId, int limit) {
//        if (!loadingWorkerActivitiesAsyncTaskHashMap.containsKey(taskId)) {
//            synchronized (ActivityDataStore.class) {
//                if (!loadingWorkerActivitiesAsyncTaskHashMap.containsKey(taskId)) {
//                    LoadingActivitiesAsyncTask loadingWorkerActivitiesTask = new LoadingActivitiesAsyncTask(mContext, workerId, limit, this);
//                    loadingWorkerActivitiesTask.execute();
//                    loadingWorkerActivitiesAsyncTaskHashMap.put(taskId, loadingWorkerActivitiesTask);
//                }
//            }
//        }
//    }
//    public ArrayList<BaseData> getTaskActivities(String taskId) {
//        return mWorkerActivitiesCache.get(taskId);
//    }
//    public boolean hasTaskActivitiesCacheWithTaskId(String taskId) {
//        return mWorkerActivitiesCache.get(taskId) != null;
//    }


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
     * LoadingWorkerActivitiesAsyncTask.OnFinishLoadingData Callbacks
     */
    @Override
    public void onFinishLoadingData(String id, ILoadingActivitiesStrategy.ActivityCategory activityCategory, JSONArray activities) {
        switch (activityCategory) {
            case WORKER:
                LoadingActivitiesAsyncTask loadingWorkerActivitiesTask = loadingWorkerActivitiesAsyncTaskHashMap.get(id);
                if (loadingWorkerActivitiesTask != null) {
                    if (activities != null) {
                        ArrayList<BaseData> recordDataArrayList = parseWorkerActivityJSONArray(activities);
                        if (recordDataArrayList != null) {
                            putActivityDataArrayListToCache(id, recordDataArrayList);
                            notifyDataObservers();
                        }
                    }
                    removeLoadingWorkerActivitiesAsyncTaskFromHashMap(id);
                }
                break;
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
                BaseData activityData = ActivityDataFactory.genData(activity, mContext);
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
