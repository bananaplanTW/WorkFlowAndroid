package com.bananaplan.workflowandroid.data;

import android.content.Context;
import android.graphics.drawable.Drawable;

import com.bananaplan.workflowandroid.R;
import com.bananaplan.workflowandroid.data.worker.attendance.WorkerAttendance;
import com.bananaplan.workflowandroid.data.worker.status.BaseData;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
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
    public Status status = Status.WIP;
    public PaymentClassification paymentClassification;

    public String wipTaskId;
    private Task wipTask;

    public List<String> scheduledTaskIds = new ArrayList<>();
    private List<Task> scheduledTasks = new ArrayList<>();

    public List<Task> warningTasks = new ArrayList<>();

    public ArrayList<BaseData> records = new ArrayList<>();
    public HashMap<String, WorkerTimeCard> timeCards = new HashMap<>();
    public boolean isOvertime = false;

    private List<WorkerAttendance> attendanceList = new ArrayList<>();

    private Drawable avatar;


    public Worker(
            Context context,
            String id,
            String name,
            String factoryId,
            String wipTaskId,
            String address,
            String phone,
            int score,
            boolean isOvertime,
            Status status,
            PaymentClassification payment,
            List<String> scheduledTaskIds,
            long lastUpdatedTime) {
        this.id = id;
        this.name = name;
        this.factoryId = factoryId;
        this.wipTaskId = wipTaskId;
        this.address = address;
        this.phone = phone;
        this.score = score;
        this.isOvertime = isOvertime;
        this.status = status;
        this.paymentClassification = payment;
        this.scheduledTaskIds = scheduledTaskIds;
        this.lastUpdatedTime = lastUpdatedTime;

        if (sDefaultAvatarDrawable == null) {
            sDefaultAvatarDrawable = context.getDrawable(R.drawable.ic_person_black);
        }
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
        this.name = worker.name;
        this.factoryId = worker.factoryId;
        this.wipTaskId = worker.wipTaskId;
        this.address = worker.address;
        this.phone = worker.phone;
        this.score = worker.score;
        this.isOvertime = worker.isOvertime;
        this.status = worker.status;
        this.paymentClassification = worker.paymentClassification;
        this.scheduledTaskIds = worker.scheduledTaskIds;
        this.lastUpdatedTime = worker.lastUpdatedTime;
    }

    public void setWipTask(Task task) {
        wipTaskId = task == null ? "" : task.id;
        wipTask = task;
    }

    public Task getWipTask() {
        return wipTask;
    }

    public void setScheduledTasks(List<Task> tasks) {
        List<String> tasksIds = new ArrayList<>();
        for (Task task : tasks) {
            tasksIds.add(task.id);
        }

        scheduledTaskIds = tasksIds;
        scheduledTasks = tasks;
    }

    public void addScheduledTask(Task task) {
        if (!scheduledTaskIds.contains(task.id)) {
            scheduledTaskIds.add(task.id);
        }

        if (!scheduledTasks.contains(task)) {
            scheduledTasks.add(task);
        }
    }

    public void addAllScheduleTasks(List<Task> tasks) {
        List<String> tasksIds = new ArrayList<>();
        for (Task task : tasks) {
            tasksIds.add(task.id);
        }

        scheduledTaskIds.addAll(tasksIds);
        scheduledTasks.addAll(tasks);
    }

    public void removeScheduleTask(Task task) {
        scheduledTaskIds.remove(task.id);
        scheduledTasks.remove(task);
    }

    public void clearScheduleTasks() {
        scheduledTaskIds.clear();
        scheduledTasks.clear();
    }

    public List<WorkerAttendance> getAttendanceList() {
        return attendanceList;
    }

    public void addAttendance(WorkerAttendance attendance) {
        for (WorkerAttendance workerAttendance : attendanceList) {
            if (workerAttendance.id.equals(attendance.id)) return;
        }

        attendanceList.add(attendance);
    }

    public List<Task> getScheduledTasks() {
        return scheduledTasks;
    }

    public boolean hasWipTask() {
        return this.wipTask != null;
    }

    public boolean hasScheduledTasks() {
        return this.scheduledTasks != null && this.scheduledTasks.size() != 0;
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

    public static String getWorkerStatusString(Context context, Status status) {
        String statusString = "";

        switch (status) {
            case OFF:
                statusString = context.getString(R.string.worker_status_off);
                break;
            case STOP:
                statusString = context.getString(R.string.worker_status_stop);
                break;
            case PAUSE:
                statusString = context.getString(R.string.worker_status_pause);
                break;
            case PENDING:
                statusString = context.getString(R.string.worker_status_pending);
                break;
            case WIP:
                statusString = context.getString(R.string.worker_status_wip);
                break;
        }

        return statusString;
    }

    public long[][] getBarChartData(Context context, long start, long end) {
        long[][] data = new long[2][7];
        Arrays.fill(data[0], 0);
        Arrays.fill(data[1], 0);
        for (WorkerTimeCard timeCard : timeCards.values()) {
            if (timeCard.startDate > start && timeCard.endDate < end) {
                Calendar startCal = Calendar.getInstance();
                startCal.setTimeInMillis(timeCard.startDate);
                Calendar endCal = Calendar.getInstance();
                endCal.setTimeInMillis(timeCard.endDate);
                Calendar workOffCal = Calendar.getInstance();
                workOffCal.setTimeInMillis(timeCard.endDate);
                workOffCal.set(Calendar.HOUR_OF_DAY, WorkingData.getInstance(context).hourWorkingOff);
                workOffCal.set(Calendar.MINUTE, WorkingData.getInstance(context).minWorkingOff);
                workOffCal.clear(Calendar.SECOND);
                workOffCal.clear(Calendar.MILLISECOND);
                int idx = (startCal.get(Calendar.DAY_OF_WEEK) - 1 + 6) % 7;
                long value;
                if (timeCard.status == CaseTimeCard.STATUS.CLOSE) {
                    value = timeCard.endDate - timeCard.startDate;
                } else {
                    value = System.currentTimeMillis() - timeCard.startDate;
                }
                data[0][idx] += value;
                if (endCal.after(workOffCal)) {
                    if (timeCard.status == CaseTimeCard.STATUS.CLOSE) {
                        if (startCal.after(workOffCal)) {
                            data[1][idx] += (endCal.getTimeInMillis() - startCal.getTimeInMillis());
                        } else {
                            data[1][idx] += (endCal.getTimeInMillis() - workOffCal.getTimeInMillis());
                        }
                    } else {
                        if (startCal.after(workOffCal)) {
                            data[1][idx] += (System.currentTimeMillis() - startCal.getTimeInMillis());
                        } else {
                            data[1][idx] += (System.currentTimeMillis() - workOffCal.getTimeInMillis());
                        }
                    }
                }
            }
        }
        return data;
    }
}