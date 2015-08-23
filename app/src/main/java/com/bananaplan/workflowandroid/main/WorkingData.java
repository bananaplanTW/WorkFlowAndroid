package com.bananaplan.workflowandroid.main;

import android.content.Context;

import com.bananaplan.workflowandroid.R;
import com.bananaplan.workflowandroid.assigntask.tasks.TaskCase;
import com.bananaplan.workflowandroid.assigntask.tasks.TaskItem;
import com.bananaplan.workflowandroid.assigntask.tasks.Warning;
import com.bananaplan.workflowandroid.assigntask.tasks.Warning.WarningStatus;
import com.bananaplan.workflowandroid.assigntask.workers.Factory;
import com.bananaplan.workflowandroid.assigntask.workers.Tool;
import com.bananaplan.workflowandroid.assigntask.workers.Vendor;
import com.bananaplan.workflowandroid.assigntask.workers.WorkerItem;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Random;

/**
 * Created by Ben on 2015/7/18.
 */
public final class WorkingData {

    private volatile static WorkingData sWorkingData = null;

    private Context mContext;

    private HashMap<Long, Vendor> mVendorsMap = new HashMap<>();
    private HashMap<Long, WorkerItem> mWorkersMap = new HashMap<>();
    private HashMap<Long, TaskItem> mTaskItemsMap = new HashMap<>();
    private HashMap<Long, TaskCase> mTaskCaseMap = new HashMap<>();
    private HashMap<Long, Tool> mToolsMap = new HashMap<>();
    private HashMap<Long, Factory> mFactoriesMap = new HashMap<>();


    public static WorkingData getInstance(Context context) {
        if (sWorkingData == null) {
            synchronized (WorkingData.class) {
                if (sWorkingData == null) {
                    sWorkingData = new WorkingData(context);
                }
            }
        }
        return sWorkingData;
    }

    private WorkingData(Context context) {
        mContext = context;
        generateFakeData(); // +++ only for test case
    }

    public ArrayList<Factory> getFactories() {
        return new ArrayList<>(mFactoriesMap.values());
    }

    public ArrayList<Vendor> getVendors() {
        return new ArrayList<>(mVendorsMap.values());
    }

    public ArrayList<TaskCase> getTaskCases() {
        return new ArrayList<>(mTaskCaseMap.values());
    }

    public ArrayList<TaskItem> getTaskItemsByWorker(WorkerItem worker) {
        ArrayList<TaskItem> tmp = new ArrayList<>();
        ArrayList<TaskItem> orig = new ArrayList<>(mTaskItemsMap.values());
        for (TaskItem item : orig) {
            if (item.workerId == worker.id) {
                tmp.add(item);
            }
        }
        return tmp;
    }

    public TaskCase getTaskCaseById(long taskCaseId) {
        return mTaskCaseMap.get(taskCaseId);
    }

    public Vendor getVendorById(long vendorId) {
        return mVendorsMap.get(vendorId);
    }

    public WorkerItem getWorkerItemById(long workerId) {
        return mWorkersMap.get(workerId);
    }

    public TaskItem getTaskItemById(long taskId) {
        return mTaskItemsMap.get(taskId);
    }

    public Tool getToolById(long toolId) {
        return mToolsMap.get(toolId);
    }

    public Factory getFactoryById(long factoryId) {
        return mFactoriesMap.get(factoryId);
    }

    public void updateWorkerItemInTaskItem(long taskItemId, long workerId) {
        mTaskItemsMap.get(taskItemId).workerId = workerId;
    }

    // +++ only for test case
    private void generateFakeData() {
        final int factoryCount = 3;
        final int workerCount = 20;
        final int vendorCount = 3;
        final int taskCaseCount = 3;
        final int taskItemCount = 10;

        for (int i = 1; i <= factoryCount; i++) {
            Factory factory = new Factory(i, "Factory" + i);
            mFactoriesMap.put(factory.id, factory);
            for (int j = 1; j <= workerCount; j++) {
                WorkerItem workItem = new WorkerItem(mContext, j, "Worker" + j, "Title" + j);
                workItem.factoryId = factory.id;
                factory.workerItems.add(workItem);
                mWorkersMap.put(workItem.id, workItem);
            }
        }

        for (int i = 1; i <= vendorCount; i++) {
            Vendor vendor = new Vendor(i, "Vendor" + i);
            mVendorsMap.put(vendor.id, vendor);
            for (int j = 1; j <= taskCaseCount; j++) {
                TaskCase taskCase = new TaskCase(j, "Case" + j);
                mTaskCaseMap.put(taskCase.id, taskCase);
                taskCase.vendorId = vendor.id;
                taskCase.workerId = getRandomWorkerId();
                taskCase.feedDate = getRandomDate();
                taskCase.deliveryDate = getRandomDate();
                taskCase.figureDate = getRandomDate();
                vendor.taskCases.add(taskCase);
                for (int k = 1; k <= taskItemCount; k++) {
                    Tool tool = new Tool(k, "Tool" + k);
                    mToolsMap.put(tool.id, tool);
                    TaskItem taskItem = new TaskItem(100 * i + 10 * j + k, "Item" + k);
                    taskItem.status = getRandomStatus();
                    if (taskItem.status != TaskItem.Status.NOT_START) {
                        taskItem.startDate = getRandomDate();
                        if (taskItem.status == TaskItem.Status.FINISH) {
                            taskItem.finishDate = getRandomDate();
                        }
                    }
                    taskItem.taskCaseId = taskCase.id;
                    Warning w1 = new Warning("No power", WarningStatus.SOLVED);
                    Warning w2 = new Warning("No power", WarningStatus.SOLVED);
                    Warning w3 = new Warning("No resource", WarningStatus.UNSOLVED);
                    Warning w4 = new Warning("No resource", WarningStatus.UNSOLVED);
                    w1.taskItemId = taskItem.id;
                    w2.taskItemId = taskItem.id;
                    w3.taskItemId = taskItem.id;
                    w4.taskItemId = taskItem.id;
                    w1.handle = getRandomWorkerId();
                    w2.handle = getRandomWorkerId();
                    w3.handle = getRandomWorkerId();
                    w4.handle = getRandomWorkerId();
                    taskItem.warningList.add(w1);
                    taskItem.warningList.add(w2);
                    taskItem.warningList.add(w3);
                    taskItem.warningList.add(w4);
                    taskCase.taskItems.add(taskItem);
                    mTaskItemsMap.put(taskItem.id, taskItem);
                    taskItem.toolId = tool.id;
                    taskItem.workerId = getRandomWorkerId();
                }
            }
        }
    }

    private TaskItem.Status getRandomStatus() {
        TaskItem.Status[] statuses = TaskItem.Status.values();
        int idx = (int) (Math.random() * statuses.length);
        return statuses[idx];
    }

    private long getRandomWorkerId() {
        int num = (int) (Math.random() * mWorkersMap.keySet().size());
        List<Long> list = new ArrayList<>(mWorkersMap.keySet());
        if (list.size() == 0) return 0;
        return list.get(num);
    }

    private Date getRandomDate() {
        int year = randBetween(2015, 2015);
        int month = randBetween(0, 11);
        GregorianCalendar gc = new GregorianCalendar(year, month, 1);
        int day = randBetween(1, gc.getActualMaximum(gc.DAY_OF_MONTH));
        gc.set(year, month, day);
        return gc.getTime();
    }

    private int randBetween(int start, int end) {
        return start + (int) Math.round(Math.random() * (end - start));
    }
    // --- only for test case
}
