package com.bananaplan.workflowandroid.data.activity;

import android.content.Context;

import com.bananaplan.workflowandroid.data.dataobserver.DataObserver;
import com.bananaplan.workflowandroid.data.dataobserver.DataSubject;
import com.bananaplan.workflowandroid.data.loading.LoadingActivitiesAsyncTask;
import com.bananaplan.workflowandroid.data.loading.loadingactivities.ILoadingActivitiesStrategy;
import com.bananaplan.workflowandroid.data.loading.loadingactivities.LoadingTaskActivitiesStrategy;
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
                    LoadingWorkerActivitiesStrategy loadingWorkerActivitiesStrategy = new LoadingWorkerActivitiesStrategy(workerId, limit);
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
    public void addWorkerActivity (String workerId, BaseData activity) {
        ArrayList<BaseData> workerActivities = mWorkerActivitiesCache.get(workerId);
        if (workerActivities != null) {
            synchronized (ActivityDataStore.class) {
                if (workerActivities != null) {
                    workerActivities.add(0, activity);
                }
            }
        }
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
    private void putWorkerActivityDataArrayListToCache(String workerId, ArrayList<BaseData> activityDataArrayList) {
        synchronized (ActivityDataStore.class) {
            if (mWorkerActivitiesCache.containsKey(workerId)) {
                mWorkerActivitiesCache.remove(workerId);
            }
            mWorkerActivitiesCache.put(workerId, activityDataArrayList);
        }
    }


    /**
     * load task activities
     * @param taskId
     * @param limit
     */
    public void loadTaskActivities(String taskId, int limit) {
        if (!loadingTaskActivitiesAsyncTaskHashMap.containsKey(taskId)) {
            synchronized (ActivityDataStore.class) {
                if (!loadingTaskActivitiesAsyncTaskHashMap.containsKey(taskId)) {
                    LoadingTaskActivitiesStrategy loadingTaskActivitiesStrategy = new LoadingTaskActivitiesStrategy(taskId, limit);
                    LoadingActivitiesAsyncTask loadingWorkerActivitiesTask = new LoadingActivitiesAsyncTask(mContext, taskId, this, loadingTaskActivitiesStrategy);
                    loadingWorkerActivitiesTask.execute();
                    loadingTaskActivitiesAsyncTaskHashMap.put(taskId, loadingWorkerActivitiesTask);
                }
            }
        }
    }
    public ArrayList<BaseData> getTaskActivities(String taskId) {
        return mTaskActivityesCache.get(taskId);
    }
    public boolean hasTaskActivitiesCacheWithTaskId(String taskId) {
        return mTaskActivityesCache.get(taskId) != null;
    }
    private void removeLoadingTaskActivitiesAsyncTaskFromHashMap(String taskId) {
        if (loadingTaskActivitiesAsyncTaskHashMap.containsKey(taskId)) {
            synchronized (ActivityDataStore.class) {
                if (loadingTaskActivitiesAsyncTaskHashMap.containsKey(taskId)) {
                    loadingTaskActivitiesAsyncTaskHashMap.remove(taskId);
                }
            }
        }
    }
    private void putTaskActivityDataArrayListToCache(String taskId, ArrayList<BaseData> activityDataArrayList) {
        synchronized (ActivityDataStore.class) {
            if (mTaskActivityesCache.containsKey(taskId)) {
                mTaskActivityesCache.remove(taskId);
            }
            mTaskActivityesCache.put(taskId, activityDataArrayList);
        }
    }


    /**
     * LoadingWorkerActivitiesAsyncTask.OnFinishLoadingData Callbacks
     */
    @Override
    public void onFinishLoadingData(String id, ILoadingActivitiesStrategy.ActivityCategory activityCategory, JSONArray activities) {
        switch (activityCategory) {
            case WORKER:
                removeLoadingWorkerActivitiesAsyncTaskFromHashMap(id);
                if (activities != null) {
                    ArrayList<BaseData> activityDataArrayList = parseWorkerActivityJSONArray(activities);
                    if (activityDataArrayList != null) {
                        putWorkerActivityDataArrayListToCache(id, activityDataArrayList);
                        notifyDataObservers();
                    }
                }
                break;
            case TASK:
                removeLoadingTaskActivitiesAsyncTaskFromHashMap(id);
                if (activities != null) {
                    ArrayList<BaseData> activityDataArrayList = parseTaskActivityJSONArray(activities);
                    if (activityDataArrayList != null) {
                        putTaskActivityDataArrayListToCache(id, activityDataArrayList);
                        notifyDataObservers();
                    }
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
    private ArrayList<BaseData> parseTaskActivityJSONArray(JSONArray activities) {
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
