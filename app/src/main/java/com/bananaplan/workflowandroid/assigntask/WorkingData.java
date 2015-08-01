package com.bananaplan.workflowandroid.assigntask;

import android.content.Context;

import com.bananaplan.workflowandroid.R;
import com.bananaplan.workflowandroid.assigntask.tasks.TaskCase;
import com.bananaplan.workflowandroid.assigntask.tasks.TaskCaseAdapter;
import com.bananaplan.workflowandroid.assigntask.tasks.TaskItem;
import com.bananaplan.workflowandroid.assigntask.workers.Factory;
import com.bananaplan.workflowandroid.assigntask.workers.Tool;
import com.bananaplan.workflowandroid.assigntask.workers.Vendor;
import com.bananaplan.workflowandroid.assigntask.workers.WorkerItem;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

/**
 * Created by Ben on 2015/7/18.
 */
public class WorkingData {
    private Context mContext;
    private ArrayList<Factory> mFactories = new ArrayList<Factory>();
    private HashMap<Long, Vendor> mVendorsMap = new HashMap<Long, Vendor>();
    private HashMap<Long, WorkerItem> mWorkersMap = new HashMap<Long, WorkerItem>();
    private HashMap<Long, TaskItem> mTaskItemsMap = new HashMap<Long, TaskItem>();
    private HashMap<Long, Tool> mToolsMap = new HashMap<Long, Tool>();

    public WorkingData(Context context) {
        this.mContext = context;
        WorkerItem.sDefaultAvatarDrawable = mContext.getDrawable(R.drawable.ic_person_black);
    }

    public ArrayList<Factory> getFactories() {
        return mFactories;
    }

    public ArrayList<Vendor> getVendors() {
        return new ArrayList<Vendor>(mVendorsMap.values());
    }

    public Vendor getVendorById(long vendorId) {
        return mVendorsMap.get(vendorId);
    }

    public WorkerItem getWorkerItemById(long workerId) {
        return mWorkersMap.get(workerId);
    }

    public Tool getToolById(long toolId) {
        return mToolsMap.get(toolId);
    }

    public void updateWorkerItemForTaskItem(long taskItemId, long workerId) {
        mTaskItemsMap.get(taskItemId).workerId = workerId;
    }

    // +++ only for test case
    public void generateFakeData() {
        final int factoryCount = 3;
        final int workerCount = 20;
        final int vendorCount = 3;
        final int taskCaseCount = 3;
        final int taskItemCount = 10;
        for (int i = 1; i <= factoryCount; i++) {
            Factory factory = new Factory(i, "Factory" + i);
            mFactories.add(factory);
            for (int j = 1; j <= workerCount; j++) {
                WorkerItem workItem = new WorkerItem(j, "Worker" + j, "WorkerItemTitle" + j);
                workItem.factoryId = factory.id;
                factory.workerItems.add(workItem);
                mWorkersMap.put(workItem.id, workItem);
            }
        }
        for (int i = 1; i <= vendorCount; i++) {
            Vendor vendor = new Vendor(i, "VendorName" + i);
            mVendorsMap.put(vendor.id, vendor);
            for (int j = 1; j <= taskCaseCount; j++) {
                TaskCase taskCase = new TaskCase(j, "TaskCaseName" + j);
                taskCase.vendorId = vendor.id;
                vendor.taskCases.add(taskCase);
                for (int k = 1; k <= taskItemCount; k++) {
                    Tool tool = new Tool(k, "ToolName" + k);
                    mToolsMap.put(tool.id, tool);
                    TaskItem taskItem = new TaskItem(k, "ItemName" + k);
                    taskItem.taskCaseId = taskCase.id;
                    taskCase.taskItems.add(taskItem);
                    mTaskItemsMap.put(taskItem.id, taskItem);
                    taskItem.toolId = tool.id;
                    taskItem.workerId = getRandomWorkerId();
                }
            }
        }
    }

    private long getRandomWorkerId() {
        Random random = new Random();
        int num = (int) (Math.random() * mWorkersMap.keySet().size());
        List<Long> list = new ArrayList<Long>(mWorkersMap.keySet());
        if (list.size() == 0) return 0;
        return list.get(num);
    }
    // --- only for test case
}
