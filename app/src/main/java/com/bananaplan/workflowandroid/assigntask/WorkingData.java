package com.bananaplan.workflowandroid.assigntask;

import android.content.Context;

import com.bananaplan.workflowandroid.assigntask.tasks.TaskCase;
import com.bananaplan.workflowandroid.assigntask.tasks.TaskCaseAdapter;
import com.bananaplan.workflowandroid.assigntask.tasks.TaskItem;
import com.bananaplan.workflowandroid.assigntask.workers.Factory;
import com.bananaplan.workflowandroid.assigntask.workers.Tool;
import com.bananaplan.workflowandroid.assigntask.workers.Vendor;
import com.bananaplan.workflowandroid.assigntask.workers.WorkerItem;

import java.util.ArrayList;
import java.util.HashMap;

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
                WorkerItem workItem = new WorkerItem(j, "WorkerItemName" + j, "WorkerItemTitle" + j);
                workItem.factoryId = factory.id;
                factory.workerItems.add(workItem);
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
                    TaskItem taskItem = new TaskItem(k, "TaskItemName" + k);
                    taskItem.taskCaseId = taskCase.id;
                    taskCase.taskItems.add(taskItem);
                    mTaskItemsMap.put(taskItem.id, taskItem);
                    taskItem.toolId = tool.id;
                }
            }
        }
    }
    // --- only for test case
}
