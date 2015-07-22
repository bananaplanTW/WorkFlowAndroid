package com.bananaplan.workflowandroid.assigntask;

import android.content.Context;

import com.bananaplan.workflowandroid.assigntask.tasks.TaskCase;
import com.bananaplan.workflowandroid.assigntask.tasks.TaskItem;
import com.bananaplan.workflowandroid.assigntask.workers.Factory;
import com.bananaplan.workflowandroid.assigntask.workers.Vendor;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Ben on 2015/7/18.
 */
public class WorkingData {
    private Context mContext;
    private ArrayList<Factory> mFactories = new ArrayList<Factory>();
    private ArrayList<Vendor> mVendors = new ArrayList<Vendor>();
    private HashMap<Long, Vendor> mVendorMap = new HashMap<Long, Vendor>();

    public WorkingData(Context context) {
        this.mContext = context;
    }

    public ArrayList<Factory> getFactories() {
        return mFactories;
    }

    public ArrayList<Vendor> getVendors() {
        return mVendors;
    }

    public Vendor getVendorById(long vendorId) {
        return mVendorMap.get(vendorId);
    }

    // +++ only for test case
    public void generateFakeData() {
        Vendor vendor1 = new Vendor(1, "Honda");
        Vendor vendor2 = new Vendor(2, "Toyota");
        Vendor vendor3 = new Vendor(3, "Yamaha");
        mVendors.add(vendor1);
        mVendors.add(vendor2);
        mVendors.add(vendor3);
        TaskCase taskCase1 = new TaskCase(1, "D037049", "Dannt Lin");
        taskCase1.taskItems.add(new TaskItem(1,
                "Outside drilling", TaskItem.Status.WARNING, "Sand holes", "11:00:00", "Boring MachineA", "Danny Lin", TaskItem.Progress.IN_SCHEDULE));
        taskCase1.taskItems.add(new TaskItem(2,
                "Outside drilling", TaskItem.Status.WARNING, "Sand holes", "11:00:00", "Boring MachineA", "Danny Lin", TaskItem.Progress.IN_SCHEDULE));
        taskCase1.taskItems.add(new TaskItem(3,
                "Outside drilling", TaskItem.Status.WARNING, "Sand holes", "11:00:00", "Boring MachineA", "Danny Lin", TaskItem.Progress.IN_SCHEDULE));
        taskCase1.taskItems.add(new TaskItem(4,
                "Outside drilling", TaskItem.Status.WARNING, "Sand holes", "11:00:00", "Boring MachineA", "Danny Lin", TaskItem.Progress.IN_SCHEDULE));
        taskCase1.taskItems.add(new TaskItem(5,
                "Outside drilling", TaskItem.Status.WARNING, "Sand holes", "11:00:00", "Boring MachineA", "Danny Lin", TaskItem.Progress.IN_SCHEDULE));
        taskCase1.taskItems.add(new TaskItem(6,
                "Outside drilling", TaskItem.Status.WARNING, "Sand holes", "11:00:00", "Boring MachineA", "Danny Lin", TaskItem.Progress.IN_SCHEDULE));
        taskCase1.taskItems.add(new TaskItem(7,
                "Outside drilling", TaskItem.Status.WARNING, "Sand holes", "11:00:00", "Boring MachineA", "Danny Lin", TaskItem.Progress.IN_SCHEDULE));
        taskCase1.taskItems.add(new TaskItem(8,
                "Outside drilling", TaskItem.Status.WARNING, "Sand holes", "11:00:00", "Boring MachineA", "Danny Lin", TaskItem.Progress.IN_SCHEDULE));
        taskCase1.taskItems.add(new TaskItem(9,
                "Outside drilling", TaskItem.Status.WARNING, "Sand holes", "11:00:00", "Boring MachineA", "Danny Lin", TaskItem.Progress.IN_SCHEDULE));
        taskCase1.taskItems.add(new TaskItem(10,
                "Outside drilling", TaskItem.Status.WARNING, "Sand holes", "11:00:00", "Boring MachineA", "Danny Lin", TaskItem.Progress.IN_SCHEDULE));

        TaskCase taskCase2 = new TaskCase(2, "A147501", "Ben Lai");
        taskCase2.taskItems.add(new TaskItem(11,
                "Hand", TaskItem.Status.WARNING, "Sand holes", "11:00:00", "Boring MachineA", "Danny Lin", TaskItem.Progress.IN_SCHEDULE));
        taskCase2.taskItems.add(new TaskItem(12,
                "Head", TaskItem.Status.WARNING, "Sand holes", "11:00:00", "Boring MachineA", "Danny Lin", TaskItem.Progress.IN_SCHEDULE));
        taskCase2.taskItems.add(new TaskItem(13,
                "Body", TaskItem.Status.WARNING, "Sand holes", "11:00:00", "Boring MachineA", "Danny Lin", TaskItem.Progress.IN_SCHEDULE));
        taskCase2.taskItems.add(new TaskItem(14,
                "Leg", TaskItem.Status.WARNING, "Sand holes", "11:00:00", "Boring MachineA", "Danny Lin", TaskItem.Progress.IN_SCHEDULE));

        TaskCase taskCase3 = new TaskCase(3, "A147501", "Danny Chan");
        taskCase3.taskItems.add(new TaskItem(15,
                "X", TaskItem.Status.WARNING, "Sand holes", "11:00:00", "Boring MachineA", "Danny Lin", TaskItem.Progress.IN_SCHEDULE));
        vendor1.taskCases.add(taskCase1);
        vendor1.taskCases.add(taskCase2);
        vendor1.taskCases.add(taskCase3);

        TaskCase taskCase4 = new TaskCase(4, "D037078", "Mary Wang");
        taskCase4.taskItems.add(new TaskItem(16,
                "X", TaskItem.Status.WARNING, "Sand holes", "11:00:00", "Boring MachineA", "Danny Lin", TaskItem.Progress.IN_SCHEDULE));
        vendor2.taskCases.add(taskCase4);

        TaskCase taskCase5 = new TaskCase(5, "E339017", "Mary Wang");
        taskCase5.taskItems.add(new TaskItem(17,
                "Y", TaskItem.Status.WARNING, "Sand holes", "11:00:00", "Boring MachineA", "Danny Lin", TaskItem.Progress.IN_SCHEDULE));
        TaskCase taskCase6 = new TaskCase(6, "H827403", "Jane Chen");
        taskCase6.taskItems.add(new TaskItem(18,
                "Z", TaskItem.Status.WARNING, "Sand holes", "11:00:00", "Boring MachineA", "Danny Lin", TaskItem.Progress.IN_SCHEDULE));
        vendor3.taskCases.add(taskCase5);
        vendor3.taskCases.add(taskCase6);

        for (Vendor vendor : mVendors) {
            mVendorMap.put(vendor.id, vendor);
            for (TaskCase taskCase : vendor.taskCases) {
                taskCase.vendorId = vendor.id;
                for (TaskItem taskItem : taskCase.taskItems) {
                    taskItem.taskCaseId = taskCase.id;
                }
            }
        }
    }
    // --- only for test case
}
