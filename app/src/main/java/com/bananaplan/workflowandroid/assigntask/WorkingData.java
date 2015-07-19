package com.bananaplan.workflowandroid.assigntask;

import android.content.Context;

import com.bananaplan.workflowandroid.assigntask.tasks.TaskCase;
import com.bananaplan.workflowandroid.assigntask.tasks.TaskItem;
import com.bananaplan.workflowandroid.assigntask.workers.Factory;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Ben on 2015/7/18.
 */
public class WorkingData {
    private Context mContext;
    private ArrayList<Factory> mFactories = new ArrayList<Factory>();

    public WorkingData(Context context) {
        this.mContext = context;
    }

    public ArrayList<Factory> getFactories() {
        return mFactories;
    }

    // +++ only for test case
    public void generateFakeData() {
        List<TaskItem> case1 = new ArrayList<TaskItem>();
        List<TaskItem> case2 = new ArrayList<TaskItem>();
        List<TaskItem> case3 = new ArrayList<TaskItem>();

        case1.add(new TaskItem(
                "外面鑽孔", TaskItem.Status.WARNING, "沙孔", "11:00:00", "鑽孔機械A", "Danny Lin", TaskItem.Progress.IN_SCHEDULE));
        case1.add(new TaskItem(
                "外面鑽孔", TaskItem.Status.WARNING, "沙孔", "11:00:00", "鑽孔機械A", "Danny Lin", TaskItem.Progress.IN_SCHEDULE));
        case1.add(new TaskItem(
                "外面鑽孔", TaskItem.Status.WARNING, "沙孔", "11:00:00", "鑽孔機械A", "Danny Lin", TaskItem.Progress.IN_SCHEDULE));
        case1.add(new TaskItem(
                "外面鑽孔", TaskItem.Status.WARNING, "沙孔", "11:00:00", "鑽孔機械A", "Danny Lin", TaskItem.Progress.IN_SCHEDULE));
        case1.add(new TaskItem(
                "外面鑽孔", TaskItem.Status.WARNING, "沙孔", "11:00:00", "鑽孔機械A", "Danny Lin", TaskItem.Progress.IN_SCHEDULE));
        case1.add(new TaskItem(
                "外面鑽孔", TaskItem.Status.WARNING, "沙孔", "11:00:00", "鑽孔機械A", "Danny Lin", TaskItem.Progress.IN_SCHEDULE));
        case1.add(new TaskItem(
                "外面鑽孔", TaskItem.Status.WARNING, "沙孔", "11:00:00", "鑽孔機械A", "Danny Lin", TaskItem.Progress.IN_SCHEDULE));
        case1.add(new TaskItem(
                "外面鑽孔", TaskItem.Status.WARNING, "沙孔", "11:00:00", "鑽孔機械A", "Danny Lin", TaskItem.Progress.IN_SCHEDULE));
        case1.add(new TaskItem(
                "外面鑽孔", TaskItem.Status.WARNING, "沙孔", "11:00:00", "鑽孔機械A", "Danny Lin", TaskItem.Progress.IN_SCHEDULE));
        case1.add(new TaskItem(
                "外面鑽孔", TaskItem.Status.WARNING, "沙孔", "11:00:00", "鑽孔機械A", "Danny Lin", TaskItem.Progress.IN_SCHEDULE));

        case2.add(new TaskItem(
                "Hand", TaskItem.Status.WARNING, "沙孔", "11:00:00", "鑽孔機械A", "Danny Lin", TaskItem.Progress.IN_SCHEDULE));
        case2.add(new TaskItem(
                "Head", TaskItem.Status.WARNING, "沙孔", "11:00:00", "鑽孔機械A", "Danny Lin", TaskItem.Progress.IN_SCHEDULE));
        case2.add(new TaskItem(
                "Body", TaskItem.Status.WARNING, "沙孔", "11:00:00", "鑽孔機械A", "Danny Lin", TaskItem.Progress.IN_SCHEDULE));
        case2.add(new TaskItem(
                "Leg", TaskItem.Status.WARNING, "沙孔", "11:00:00", "鑽孔機械A", "Danny Lin", TaskItem.Progress.IN_SCHEDULE));

        case3.add(new TaskItem(
                "X", TaskItem.Status.WARNING, "沙孔", "11:00:00", "鑽孔機械A", "Danny Lin", TaskItem.Progress.IN_SCHEDULE));

        ArrayList<TaskCase> taskCaseDatas = new ArrayList<TaskCase>();
        taskCaseDatas.add(new TaskCase(1, "TaskCase1", "Tony", "8:00:12", "35:04:55", 3, case1));
        taskCaseDatas.add(new TaskCase(2, "TaskCase2", "Thor", "6:11:10", "5:04:55", 6, case2));
        taskCaseDatas.add(new TaskCase(3, "TaskCase3", "BBB", "4:32:11", "00:04:55", 5, case3));
        Factory factory = new Factory(1, "Honda", taskCaseDatas);
        for (TaskCase taskCase : taskCaseDatas) {
            taskCase.setFactory(factory);
        }
        mFactories.add(factory);
    }
    // --- only for test case
}
