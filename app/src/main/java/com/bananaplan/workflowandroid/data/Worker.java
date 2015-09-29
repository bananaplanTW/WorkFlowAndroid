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

    public static class PaymentClassification {
        String type;
        double base;
        double hourlyPayment;
        double overtimeBase;

        public PaymentClassification(String type, double base, double hourlyPayment, double overtimeBase) {
            this.type = type;
            this.base = base;
            this.hourlyPayment = hourlyPayment;
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
    public List<Task> scheduledTasks;
    public List<Task> warningTasks;

    public ArrayList<BaseData> records = new ArrayList<>();
    public ArrayList<LeaveData> leaveDatas = new ArrayList<>();
    public boolean isOvertime = false;

    private Drawable avatar;


    public Worker(
            String id,
            String name,
            String factoryId,
            String address,
            String phone,
            int score,
            boolean isOvertime,
            Status status,
            PaymentClassification payment,
            long lastUpdatedTime) {
        this.id = id;
        this.name = name;
        this.factoryId = factoryId;
        this.address = address;
        this.phone = phone;
        this.score = score;
        this.isOvertime = isOvertime;
        this.status = status;
        this.paymentClassification = payment;
        this.lastUpdatedTime = lastUpdatedTime;
    }

    public Worker(final Context context, String id, String name, String title) {
        this(context, id, name, title, new ArrayList<Task>());
    }

    public Worker(final Context context, String id, String name, String jobTitle, List<Task> scheduledTasks) {
        this.id = id;
        this.name = name;
        this.jobTitle = jobTitle;
        this.scheduledTasks = scheduledTasks;
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

    public void update(Worker worker) {
        this.id = worker.id;
        this.name = worker.name;
        this.factoryId = worker.factoryId;
        this.address = worker.address;
        this.phone = worker.phone;
        this.score = worker.score;
        this.isOvertime = worker.isOvertime;
        this.status = worker.status;
        this.paymentClassification = worker.paymentClassification;
        this.lastUpdatedTime = worker.lastUpdatedTime;
    }

    public boolean hasCurrentTask() {
        return currentTask != null;
    }

    public boolean hasScheduledTasks() {
        return scheduledTasks != null && scheduledTasks.size() != 0;
    }

    public static Status convertStringToStatus(String status) {
        Status result = Status.OFF;

        if ("wip".equals(status)) {
            result = Status.WIP;

        } else if ("pending".equals(status)) {
            result = Status.PENDING;

        } else if ("pause".equals(status)) {
            result = Status.PAUSE;

        } else if ("stop".equals(status)) {
            result = Status.STOP;

        } else if ("off".equals(status)) {
            result = Status.OFF;

        }

        return result;
    }
}
