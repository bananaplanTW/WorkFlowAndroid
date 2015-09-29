package com.bananaplan.workflowandroid.data;

import android.content.Context;
import android.util.Log;

import com.bananaplan.workflowandroid.R;
import com.bananaplan.workflowandroid.data.equipment.MaintenanceRecord;
import com.bananaplan.workflowandroid.data.worker.attendance.LeaveData;
import com.bananaplan.workflowandroid.data.worker.status.BaseData;
import com.bananaplan.workflowandroid.data.worker.status.DataFactory;
import com.bananaplan.workflowandroid.data.worker.status.FileData;
import com.bananaplan.workflowandroid.data.worker.status.HistoryData;
import com.bananaplan.workflowandroid.data.worker.status.PhotoData;
import com.bananaplan.workflowandroid.data.worker.status.RecordData;
import com.bananaplan.workflowandroid.utility.Utils;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;


/**
 * Created by Ben on 2015/7/18.
 */
public final class WorkingData {

    private static final String TAG = "WorkingData";

    private static final class DataType {
        public static final int EQUIPMENT = 0;
        public static final int FACTIRY = 1;
        public static final int MANAGER = 2;
        public static final int TASK = 3;
        public static final int CASE = 4;
        public static final int VENDOR = 5;
        public static final int WARNING = 6;
        public static final int WORKER = 7;
    }

    private volatile static WorkingData sWorkingData = null;
    private static int sDataIdCount = -1;

    private Context mContext;

    private HashMap<String, Manager> mManagersMap = new HashMap<>();
    private HashMap<String, Worker> mWorkersMap = new HashMap<>();
    private HashMap<String, Vendor> mVendorsMap = new HashMap<>();
    private HashMap<String, Task> mTasksMap = new HashMap<>();
    private HashMap<String, Case> mCasesMap = new HashMap<>();
    private HashMap<String, Equipment> mEquipmentsMap = new HashMap<>();
    private HashMap<String, Factory> mFactoriesMap = new HashMap<>();
    private HashMap<String, Tag> mTagsMap = new HashMap<>();


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


    public void addCase(Case aCase) {
        if (aCase == null) return;
        mCasesMap.put(aCase.id, aCase);
        Log.d(TAG, "Add case " + aCase.name);
    }
    public void addTask(Task task) {
        if (task == null) return;
        mTasksMap.put(task.id, task);
        Log.d(TAG, "Add task " + task.name);
    }
    public void addManager(Manager manager) {
        if (manager == null) return;
        mManagersMap.put(manager.id, manager);
        Log.d(TAG, "Add manager " + manager.name);
    }
    public void addVendor(Vendor vendor) {
        if (vendor == null) return;
        mVendorsMap.put(vendor.id, vendor);
        Log.d(TAG, "Add vendor " + vendor.name);
    }
    public void addTag(Tag tag) {
        if (tag == null) return;
        mTagsMap.put(tag.id, tag);
        Log.d(TAG, "Add tag " + tag.name);
    }


    public boolean hasCase(String caseId) {
        return mCasesMap.containsKey(caseId);
    }
    public boolean hasVendor(String vendorId) {
        return mVendorsMap.containsKey(vendorId);
    }
    public boolean hasManager(String managerId) {
        return mManagersMap.containsKey(managerId);
    }
    public boolean hasTag(String tagId) {
        return mTagsMap.containsKey(tagId);
    }


    public ArrayList<Manager> getManagers() {
        return new ArrayList<>(mManagersMap.values());
    }
    public ArrayList<Worker> getWorkers() {
        return new ArrayList<>(mWorkersMap.values());
    }
    public ArrayList<Factory> getFactories() {
        return new ArrayList<>(mFactoriesMap.values());
    }
    public ArrayList<Vendor> getVendors() {
        return new ArrayList<>(mVendorsMap.values());
    }
    public ArrayList<Case> getCases() {
        return new ArrayList<>(mCasesMap.values());
    }
    public ArrayList<Equipment> getEquipments() {
        return new ArrayList<>(mEquipmentsMap.values());
    }


    public ArrayList<Task> getTasksByWorker(Worker worker) {
        ArrayList<Task> tmp = new ArrayList<>();
        if (worker == null) return tmp;
        ArrayList<Task> orig = new ArrayList<>(mTasksMap.values());
        for (Task item : orig) {
            if (Utils.isSameId(item.workerId, worker.id)) {
                tmp.add(item);
            }
        }
        return tmp;
    }
    public ArrayList<Task> getTasksByEquipment(Equipment equipment) {
        ArrayList<Task> tmp = new ArrayList<>();
        if (equipment == null) return tmp;
        ArrayList<Task> orig = new ArrayList<>(mTasksMap.values());
        for (Task item : orig) {
            if (Utils.isSameId(item.equipmentId, equipment.id)) {
                tmp.add(item);
            }
        }
        return tmp;
    }


    public Manager getManagerById(String managerId) {
        return mManagersMap.get(managerId);
    }
    public Case getCaseById(String caseId) {
        return mCasesMap.get(caseId);
    }
    public Vendor getVendorById(String vendorId) {
        return mVendorsMap.get(vendorId);
    }
    public Worker getWorkerById(String workerId) {
        return mWorkersMap.get(workerId);
    }
    public Task getTaskById(String taskId) {
        return mTasksMap.get(taskId);
    }
    public Equipment getEquipmentById(String equipmentId) {
        return mEquipmentsMap.get(equipmentId);
    }
    public Factory getFactoryById(String factoryId) {
        return mFactoriesMap.get(factoryId);
    }
    public Tag getTagById(String tagId) {
        return mTagsMap.get(tagId);
    }


    public void addRecordToWorker(Worker worker, BaseData data) {
        if (worker == null || data == null) return;
        worker.records.add(data);
    }

    public void addRecordToTask(Task task, BaseData data) {
        if (task == null || data == null) return;
        task.records.add(data);
    }

    public String getLoginWorkerId() { // TODO
        return getRandomWorkerId();
    }

    // +++ only for test case
    private void generateFakeData() {
        final int managerCount = 5;
        final int factoryCount = 3;
        final int workerCount = 20;
        final int vendorCount = 3;
        final int taskCaseCount = 3;
        final int taskItemCount = 10;
        final int equipmentCount = 10;

        for (int i = 0 ; i < managerCount ; i++) {
            String managerId = generateDataId(DataType.MANAGER);
            mManagersMap.put(managerId, new Manager(managerId, managerId, 100));
        }

        for (int i = 1; i <= factoryCount; i++) {
            String factoryId = generateDataId(DataType.FACTIRY);
            Factory factory = new Factory(factoryId, factoryId);
            mFactoriesMap.put(factory.id, factory);

            for (int j = 1; j <= workerCount; j++) {
                String workerId = generateDataId(DataType.WORKER);
                Worker workItem = new Worker(mContext, workerId, workerId, "Title " + workerId);
                workItem.factoryId = factory.id;
                factory.workers.add(workItem);
                mWorkersMap.put(workItem.id, workItem);
            }
        }

        for (int i = 0; i < equipmentCount; i++) {
            String equipmentId = generateDataId(DataType.EQUIPMENT);
            Equipment equipment = new Equipment(equipmentId, equipmentId, getRandomFactoryId());
            equipment.purchaseDate = getRandomDate();
            equipment.records.add(new MaintenanceRecord("reason1", getRandomDate()));
            equipment.records.add(new MaintenanceRecord("reason2", getRandomDate()));
            mEquipmentsMap.put(equipment.id, equipment);
        }

        for (Factory factory : mFactoriesMap.values()) {
            for (Worker worker : factory.workers) {
                FileData file = (FileData) DataFactory.genData(worker.id, BaseData.TYPE.FILE);
                file.uploader = getRandomWorkerId();
                file.time = getRandomDate();
                file.fileName = "test.pdf";
                worker.records.add(file);
                HistoryData history1 = (HistoryData) DataFactory.genData(worker.id, BaseData.TYPE.HISTORY);
                history1.time = getRandomDate();
                history1.status = HistoryData.STATUS.WORK;
                worker.records.add(history1);
                HistoryData history2 = (HistoryData) DataFactory.genData(worker.id, BaseData.TYPE.HISTORY);
                history2.time = getRandomDate();
                history2.status = HistoryData.STATUS.OFF_WORK;
                worker.records.add(history2);
                PhotoData photo = (PhotoData) DataFactory.genData(worker.id, BaseData.TYPE.PHOTO);
                photo.time = getRandomDate();
                photo.uploader = getRandomWorkerId();
                photo.fileName = "test.png";
                photo.photo = mContext.getDrawable(R.drawable.drawer_equipment);
                worker.records.add(photo);
                RecordData record = (RecordData) DataFactory.genData(worker.id, BaseData.TYPE.RECORD);
                record.time = getRandomDate();
                record.reporter = getRandomWorkerId();
                record.description = "test description";
                worker.records.add(record);

                LeaveData leave1 = new LeaveData();
                leave1.date = getRandomDate();
                leave1.reason = "test reason";
                leave1.type = LeaveData.TYPE.PRIVATE;
                worker.leaveDatas.add(leave1);
                LeaveData leave2 = new LeaveData();
                leave2.date = getRandomDate();
                leave2.reason = "test reason";
                leave2.type = LeaveData.TYPE.SICK;
                worker.leaveDatas.add(leave2);
                LeaveData leave3 = new LeaveData();
                leave3.date = getRandomDate();
                leave3.reason = "test reason";
                leave3.type = LeaveData.TYPE.WORK;
                worker.leaveDatas.add(leave3);
            }
        }

        for (int i = 1; i <= vendorCount; i++) {
            String vendorId = generateDataId(DataType.VENDOR);
            Vendor vendor = new Vendor(vendorId, vendorId);
            mVendorsMap.put(vendor.id, vendor);

            for (int j = 1; j <= taskCaseCount; j++) {
                String taskCaseId = generateDataId(DataType.CASE);
                Case aCase = new Case(taskCaseId, taskCaseId);
                mCasesMap.put(aCase.id, aCase);
                aCase.vendorId = vendor.id;
                aCase.workerId = getRandomWorkerId();
                aCase.materialPurchasedDate = getRandomDate();
                aCase.deliveredDate = getRandomDate();
                aCase.layoutDeliveredDate = getRandomDate();
                aCase.managerId = getRandomManagerId();
                vendor.cases.add(aCase);
                for (int k = 1; k <= taskItemCount; k++) {
                    String taskId = generateDataId(DataType.TASK);
                    Task task = new Task(taskId, taskId);
//                    task.status = getRandomStatus();
//                    if (task.status != Task.Status.NOT_START) {
//                        task.startDate = getRandomDate();
//                        if (task.status == Task.Status.FINISH) {
//                            task.finishDate = getRandomDate();
//                        }
//                    }
                    task.caseId = aCase.id;
                    Warning w1 = new Warning("No power", Warning.Status.CLOSE);
                    Warning w2 = new Warning("No power", Warning.Status.CLOSE);
                    Warning w3 = new Warning("No resource", Warning.Status.OPEN);
                    Warning w4 = new Warning("No resource", Warning.Status.OPEN);
                    w1.taskId = task.id;
                    w2.taskId = task.id;
                    w3.taskId = task.id;
                    w4.taskId = task.id;
                    w1.managerId = getRandomManagerId();
                    w2.managerId = getRandomManagerId();
                    w3.managerId = getRandomManagerId();
                    w4.managerId = getRandomManagerId();
                    task.warnings.add(w1);
                    task.warnings.add(w2);
                    task.warnings.add(w3);
                    task.warnings.add(w4);
                    aCase.tasks.add(task);
                    mTasksMap.put(task.id, task);
                    task.equipmentId = getRandomEquipmentId();
                    task.workerId = getRandomWorkerId();
                    getWorkerById(task.workerId).currentTask = task;
                }
            }
        }
    }

    private Task.Status getRandomStatus() {
        Task.Status[] statuses = Task.Status.values();
        int idx = (int) (Math.random() * statuses.length);
        return statuses[idx];
    }

    private String getRandomFactoryId() {
        int num = (int) (Math.random() * mFactoriesMap.keySet().size());
        List<String> list = new ArrayList<>(mFactoriesMap.keySet());
        if (list.size() == 0) return "";
        return list.get(num);
    }

    private String getRandomWorkerId() {
        int num = (int) (Math.random() * mWorkersMap.keySet().size());
        List<String> list = new ArrayList<>(mWorkersMap.keySet());
        if (list.size() == 0) {
            return "";
        }
        return list.get(num);
    }

    private String getRandomManagerId() {
        int num = (int) (Math.random() * mManagersMap.keySet().size());
        List<String> list = new ArrayList<>(mManagersMap.keySet());
        if (list.size() == 0) {
            return "";
        }
        return list.get(num);
    }

    private String getRandomEquipmentId() {
        int num = (int) (Math.random() * mEquipmentsMap.keySet().size());
        List<String> list = new ArrayList<>(mEquipmentsMap.keySet());
        if (list.size() == 0) return "";
        return list.get(num);
    }

    private Date getRandomDate() {
        int year = randBetween(2014, 2015);
        int month = randBetween(0, Calendar.getInstance().get(Calendar.MONTH) - 1);
        GregorianCalendar gc = new GregorianCalendar(year, month, 1);
        int day = randBetween(1, gc.getActualMaximum(gc.DAY_OF_MONTH));
        gc.set(year, month, day);
        return gc.getTime();
    }

    private int randBetween(int start, int end) {
        return start + (int) Math.round(Math.random() * (end - start));
    }

    private String generateDataId(int dataType) {
        sDataIdCount++;
        String id = "";

        switch (dataType) {
            case DataType.EQUIPMENT:
                id = "equipment" + sDataIdCount;
                break;
            case DataType.FACTIRY:
                id = "factory" + sDataIdCount;
                break;
            case DataType.MANAGER:
                id = "manager" + sDataIdCount;
                break;
            case DataType.TASK:
                id = "task" + sDataIdCount;
                break;
            case DataType.CASE:
                id = "taskcase" + sDataIdCount;
                break;
            case DataType.VENDOR:
                id = "vendor" + sDataIdCount;
                break;
            case DataType.WARNING:
                id = "warning" + sDataIdCount;
                break;
            case DataType.WORKER:
                id = "worker" + sDataIdCount;
                break;
        }

        return id;
    }
    // --- only for test case
}
