package com.bananaplan.workflowandroid.data;

import android.content.Context;
import android.graphics.drawable.Drawable;

import com.bananaplan.workflowandroid.R;
import com.bananaplan.workflowandroid.data.worker.attendance.LeaveData;
import com.bananaplan.workflowandroid.data.worker.status.BaseData;

import java.util.ArrayList;
import java.util.List;


/**
 * @author Danny Lin
 * @since 2015/6/27.
 */
public class Worker extends IdData {

    public static Drawable sDefaultAvatarDrawable;

    public enum Status {
        WIP, PENDING, PAUSE, STOP, OFF
    }

    private class PaymentClassification {
        String type;
        double base;
        double overtimeBase;

        public PaymentClassification(String type, double base, double overtimeBase) {
            this.type = type;
            this.base = base;
            this.overtimeBase = overtimeBase;
        }
    }

    public String factoryId;

    public String jobTitle;
    public String address;
    public String phone;
    public int score;
    public Status status;
    public PaymentClassification paymentClassification;

    public Task currentTask;
    public List<Task> nextTasks;
    public List<Task> warningTasks;

    public ArrayList<BaseData> records = new ArrayList<>();
    public ArrayList<LeaveData> leaveDatas = new ArrayList<>();
    public boolean isOverTime = false;

    private Drawable avatar;


    public Worker(final Context context, String id, String name, String title) {
        this(context, id, name, title, new ArrayList<Task>());
    }

    public Worker(final Context context, String id, String name, String jobTitle, List<Task> nextTasks) {
        this.id = id;
        this.name = name;
        this.jobTitle = jobTitle;
        this.nextTasks = nextTasks;
        if (sDefaultAvatarDrawable == null) {
            sDefaultAvatarDrawable = context.getDrawable(R.drawable.ic_person_black);
        }
    }

    public Drawable getAvator() {
        if (this.avatar != null) {
            return this.avatar;
        }
        return sDefaultAvatarDrawable;
    }

    public boolean hasCurrentTask() {
        return currentTask != null;
    }

    public boolean hasNextTasks() {
        return nextTasks != null && nextTasks.size() != 0;
    }
}
