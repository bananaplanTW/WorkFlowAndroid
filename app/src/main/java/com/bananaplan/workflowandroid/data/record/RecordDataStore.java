package com.bananaplan.workflowandroid.data.record;

import android.content.Context;
import android.util.Log;

import com.bananaplan.workflowandroid.data.dataobserver.DataObserver;
import com.bananaplan.workflowandroid.data.dataobserver.DataSubject;
import com.bananaplan.workflowandroid.data.loading.LoadingWorkerRecordsAsyncTask;
import com.bananaplan.workflowandroid.data.worker.status.BaseData;
import com.bananaplan.workflowandroid.data.worker.status.RecordDataFactory;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by daz on 10/9/15.
 */
public class RecordDataStore implements DataSubject, LoadingWorkerRecordsAsyncTask.OnFinishLoadingDataListener {

    public enum WorkerRecordDataType {
        CHECK_IN, CHECK_OUT, BECOME_WIP, BECOME_PAUSE, BECOME_RESUME, BECOME_OVERWORK, BECOME_STOP, BECOME_PENDING, BECOME_OFF,
    }

    private Context mContext;
    private HashMap<String, LoadingWorkerRecordsAsyncTask> loadingWorkerRecordsAsyncTaskHashMap = new HashMap<>();

    private List<DataObserver> mDataObservers = new ArrayList<>();

    private HashMap<String, ArrayList<BaseData>> mWorkerRecordCache = new HashMap<>();
    private HashMap<String, ArrayList<BaseData>> mTaskRecord = new HashMap<>();
    private HashMap<String, ArrayList<BaseData>> mWarningRecord = new HashMap<>();

    private volatile static RecordDataStore sRecordData = null;

    public static final RecordDataStore getInstance(Context context) {
        if (sRecordData == null) {
            synchronized (RecordDataStore.class) {
                if (sRecordData == null) {
                    sRecordData = new RecordDataStore(context);
                }
            }
        }
        return sRecordData;
    }
    private RecordDataStore(Context context) {
        mContext = context;
    }


    public void loadWorkerRecords(String workerId, int limit) {
        if (!loadingWorkerRecordsAsyncTaskHashMap.containsKey(workerId)) {
            synchronized (RecordDataStore.class) {
                if (!loadingWorkerRecordsAsyncTaskHashMap.containsKey(workerId)) {
                    LoadingWorkerRecordsAsyncTask loadingWorkerRecordsTask = new LoadingWorkerRecordsAsyncTask(mContext, workerId, limit, this);
                    loadingWorkerRecordsTask.execute();
                    loadingWorkerRecordsAsyncTaskHashMap.put(workerId, loadingWorkerRecordsTask);
                }
            }
        }
    }
    public ArrayList<BaseData> getWorkerRecords (String workerId) {
        return mWorkerRecordCache.get(workerId);
    }
    public boolean hasWorkerRecordsCacheWithWorkerId (String workerId) {
        return mWorkerRecordCache.get(workerId) != null;
    }


    private void removeLoadingWorkerRecordsAsyncTaskFromHashMap(String workerId) {
        if (loadingWorkerRecordsAsyncTaskHashMap.containsKey(workerId)) {
            synchronized (RecordDataStore.class) {
                if (loadingWorkerRecordsAsyncTaskHashMap.containsKey(workerId)) {
                    loadingWorkerRecordsAsyncTaskHashMap.remove(workerId);
                }
            }
        }
    }
    private void putRecordDataArrayListToCache (String workerId, ArrayList<BaseData> recordDataArrayList) {
        synchronized (RecordDataStore.class) {
            if (mWorkerRecordCache.containsKey(workerId)) {
                mWorkerRecordCache.remove(workerId);
            }
            mWorkerRecordCache.put(workerId, recordDataArrayList);
        }
    }


    /**
     * OnFinishLoadingData Callbacks
     */
    @Override
    public void onFinishLoadingData(String workerId) {
        LoadingWorkerRecordsAsyncTask loadingWorkerRecordsTask = loadingWorkerRecordsAsyncTaskHashMap.get(workerId);
        if (loadingWorkerRecordsTask != null) {
            JSONArray recordJSONArray = loadingWorkerRecordsTask.getResult();
            if (recordJSONArray != null) {
                ArrayList<BaseData> recordDataArrayList = parseWorkerRecordJSONArray(recordJSONArray);
                if (recordDataArrayList != null) {
                    putRecordDataArrayListToCache(workerId, recordDataArrayList);
                    notifyDataObservers();
                }
            }
            removeLoadingWorkerRecordsAsyncTaskFromHashMap(workerId);
        }
    }
    @Override
    public void onFailLoadingData(boolean isFailCausedByInternet) {

    }


    @Override
    public void registerDataObserver(DataObserver o) {
        synchronized (RecordDataStore.class) {
            mDataObservers.add(o);
        }
    }
    @Override
    public void removeDataObserver(DataObserver o) {
        synchronized (RecordDataStore.class) {
            int index = mDataObservers.indexOf(o);
            if (index >= 0) {
                mDataObservers.remove(index);
            }
        }
    }
    @Override
    public void notifyDataObservers() {
        synchronized (RecordDataStore.class) {
            for (DataObserver dataObserver : mDataObservers) {
                dataObserver.updateData();
            }
        }
    }


    private ArrayList<BaseData> parseWorkerRecordJSONArray(JSONArray records) {
        ArrayList<BaseData> parsedRecords = new ArrayList<>();

        int length = records.length();

        try {
            for (int i = 0; i < length; i++) {
                JSONObject record = records.getJSONObject(i);
                BaseData recordData = RecordDataFactory.genData(record);
                if (recordData != null) {
                    parsedRecords.add(recordData);
                }
            }
            return parsedRecords;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }
}
