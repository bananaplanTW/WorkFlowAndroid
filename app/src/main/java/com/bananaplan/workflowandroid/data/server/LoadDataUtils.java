package com.bananaplan.workflowandroid.data.server;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import com.bananaplan.workflowandroid.data.Case;
import com.bananaplan.workflowandroid.data.Equipment;
import com.bananaplan.workflowandroid.data.Factory;
import com.bananaplan.workflowandroid.data.Manager;
import com.bananaplan.workflowandroid.data.Tag;
import com.bananaplan.workflowandroid.data.Task;
import com.bananaplan.workflowandroid.data.Vendor;
import com.bananaplan.workflowandroid.data.Warning;
import com.bananaplan.workflowandroid.data.Worker;
import com.bananaplan.workflowandroid.data.WorkingData;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;


/**
 * Utility for loading data from server
 *
 * @author Danny Lin
 * @since 2015/9/23.
 */
public class LoadDataUtils {

    private static final String TAG = "LoadDataUtils";

    private static final class WorkingDataUrl {
//        public static final String WORKERS = "http://bp-workflow.cloudapp.net:3000/api/employees";
//        public static final String CASES = "http://bp-workflow.cloudapp.net:3000/api/cases";
//        public static final String FACTORIES = "http://bp-workflow.cloudapp.net:3000/api/groups";
//        public static final String TASKS_BY_CASE = "http://bp-workflow.cloudapp.net:3000/api/tasks?caseId=";
//        public static final String TASKS_BY_WORKER = "http://bp-workflow.cloudapp.net:3000/api/employee/tasks?employeeId=";
//        public static final String WORKERS_BY_FACTORY = "http://bp-workflow.cloudapp.net:3000/api/group/employees?groupId=";
        public static final String WORKERS = "http://128.199.198.169:3000/api/employees";
        public static final String CASES = "http://128.199.198.169:3000/api/cases";
        public static final String FACTORIES = "http://128.199.198.169:3000/api/groups";
        public static final String TASKS_BY_CASE = "http://128.199.198.169:3000/api/tasks?caseId=";
        public static final String TASKS_BY_WORKER = "http://128.199.198.169:3000/api/employee/tasks?employeeId=";
        public static final String WORKERS_BY_FACTORY = "http://128.199.198.169:3000/api/group/employees?groupId=";
    }


    /**
     * Load all cases data from server, not include tasks data.
     * We only load task id of each task in the case.
     *
     * @param context
     */
    public static void loadCases(Context context) {
        try {
            String caseJsonListString = RestfulUtils.getJsonStringFromUrl(WorkingDataUrl.CASES);
            JSONArray caseJsonList = new JSONObject(caseJsonListString).getJSONArray("result");

            for (int i = 0; i < caseJsonList.length(); i++) {
                JSONObject caseJson = caseJsonList.getJSONObject(i);
                addCaseToWorkingData(context, caseJson);
            }

        } catch (JSONException e) {
            Log.e(TAG, "Exception in loadCases()");
            e.printStackTrace();
        }
    }

    /**
     * Load all factories data from server, not include workers data.
     * We only load worker id of each worker in the factory.
     *
     * @param context
     */
    public static void loadFactories(Context context) {
        try {
            String factoryJsonListString = RestfulUtils.getJsonStringFromUrl(WorkingDataUrl.FACTORIES);
            JSONArray factoryJsonList = new JSONObject(factoryJsonListString).getJSONArray("result");

            for (int i = 0 ; i < factoryJsonList.length() ; i++) {
                JSONObject factoryJson = factoryJsonList.getJSONObject(i);
                addFactoryToWorkingData(context, factoryJson);
            }
        } catch (JSONException e) {
            Log.e(TAG, "Exception in loadFactories()");
            e.printStackTrace();
        }
    }


    public static void loadTasksByCase(Context context, String caseId) {
        if (!WorkingData.getInstance(context).hasCase(caseId)) return;

        try {
            String taskJsonListString = RestfulUtils.getJsonStringFromUrl(getTasksByCaseUrl(caseId));
            JSONArray taskJsonList = new JSONObject(taskJsonListString).getJSONArray("result");
            List<Task> newCaseTasks = new ArrayList<>();

            for (int i = 0 ; i < taskJsonList.length() ; i++) {
                JSONObject taskJson = taskJsonList.getJSONObject(i);
                String taskId = taskJson.getString("_id");

                addTaskToWorkingData(context, taskJson);
                if (WorkingData.getInstance(context).hasTask(taskId)) {
                    newCaseTasks.add(WorkingData.getInstance(context).getTaskById(taskId));
                }
            }

            WorkingData.getInstance(context).getCaseById(caseId).tasks = newCaseTasks;

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    public static void loadWorkersByFactory(Context context, String factoryId) {
        if (!WorkingData.getInstance(context).hasFactory(factoryId)) return;

        try {
            String workerJsonString = RestfulUtils.getJsonStringFromUrl(getWorkersByFactoryUrl(factoryId));
            JSONArray workerJsonList = new JSONObject(workerJsonString).getJSONArray("result");
            List<Worker> newWorkers = new ArrayList<>();

            for (int i = 0 ; i < workerJsonList.length() ; i++) {
                JSONObject workerJson = workerJsonList.getJSONObject(i);
                String workerId = workerJson.getString("_id");

                addWorkerToWorkingData(context, workerJson);
                if (WorkingData.getInstance(context).hasWorker(workerId)) {
                    newWorkers.add(WorkingData.getInstance(context).getWorkerById(workerId));
                }
            }

            WorkingData.getInstance(context).getFactoryById(factoryId).workers = newWorkers;

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    public static void loadTasksByWorker(Context context, String workerId) {
        if (!WorkingData.getInstance(context).hasWorker(workerId)) return;

        try {
            String taskJsonString = RestfulUtils.getJsonStringFromUrl(getTasksByWorkerUrl(workerId));
            JSONObject taskJson = new JSONObject(taskJsonString).getJSONObject("result");
            JSONObject wipTaskJson = taskJson.getJSONObject("WIPTask");
            String wipTaskId = wipTaskJson.getString("_id");
            JSONArray scheduledTaskJsonList = taskJson.getJSONArray("scheduledTasks");
            List<Task> scheduledTasks = new ArrayList<>();

            addTaskToWorkingData(context, wipTaskJson);

            for (int i = 0 ; i < scheduledTaskJsonList.length() ; i++) {
                JSONObject scheduledTaskJson = scheduledTaskJsonList.getJSONObject(i);
                String scheduledTaskId = scheduledTaskJson.getString("_id");
                addTaskToWorkingData(context, scheduledTaskJson);

                if (WorkingData.getInstance(context).hasTask(scheduledTaskId)) {
                    scheduledTasks.add(WorkingData.getInstance(context).getTaskById(scheduledTaskId));
                }
            }

            if (WorkingData.getInstance(context).hasTask(wipTaskId)) {
                WorkingData.getInstance(context).getWorkerById(workerId).wipTask
                        = WorkingData.getInstance(context).getTaskById(wipTaskId);
            }
            WorkingData.getInstance(context).getWorkerById(workerId).scheduledTasks = scheduledTasks;

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    private static void addCaseToWorkingData(Context context, JSONObject caseJson) {
        try {
            String caseId = caseJson.getString("_id");
            long lastUpdatedTime = caseJson.getLong("updatedAt");
            boolean hasCase = WorkingData.getInstance(context).hasCase(caseId);

            if (hasCase &&
                    WorkingData.getInstance(context).getCaseById(caseId).lastUpdatedTime >= lastUpdatedTime) {
                return;
            }

            if (hasCase) {
                WorkingData.getInstance(context).updateCase(caseId, retrieveCaseFromJson(context, caseJson));
            } else {
                WorkingData.getInstance(context).addCase(retrieveCaseFromJson(context, caseJson));
            }
        } catch (JSONException e) {
            Log.e(TAG, "Exception in addCaseToWorkingData()");
            e.printStackTrace();
        }
    }
    private static void addTaskToWorkingData(Context context, JSONObject taskJson) {
        try {
            String taskId = taskJson.getString("_id");
            long lastUpdatedTime = taskJson.getLong("updatedAt");
            boolean workingDataHasTask = WorkingData.getInstance(context).hasTask(taskId);

            if (workingDataHasTask &&
                    WorkingData.getInstance(context).getTaskById(taskId).lastUpdatedTime >= lastUpdatedTime) {
                return;
            }

            if (workingDataHasTask) {
                WorkingData.getInstance(context).updateTask(taskId, retrieveTaskFromJson(context, taskJson));
            } else {
                WorkingData.getInstance(context).addTask(retrieveTaskFromJson(context, taskJson));
            }
        } catch (JSONException e) {
            Log.e(TAG, "Exception in addTaskToWorkingData()");
            e.printStackTrace();
        }
    }
    private static void addFactoryToWorkingData(Context context, JSONObject factoryJson) {
        try {
            String factoryId = factoryJson.getString("_id");
            long lastUpdatedTime = factoryJson.getLong("updatedAt");
            boolean hasFactory = WorkingData.getInstance(context).hasFactory(factoryId);

            if (hasFactory &&
                    WorkingData.getInstance(context).getFactoryById(factoryId).lastUpdatedTime >= lastUpdatedTime) {
                return;
            }

            if (hasFactory) {
                WorkingData.getInstance(context).updateFactory(factoryId, retrieveFactoryFromJson(context, factoryJson));
            } else {
                WorkingData.getInstance(context).addFactory(retrieveFactoryFromJson(context, factoryJson));
            }
        } catch (JSONException e) {
            Log.e(TAG, "Exception in addFactoryToWorkingData()");
            e.printStackTrace();
        }
    }
    private static void addWorkerToWorkingData(Context context, JSONObject workerJson) {
        try {
            String workerId = workerJson.getString("_id");
            long lastUpdatedTime = workerJson.getLong("updatedAt");
            boolean workingDataHasWorker = WorkingData.getInstance(context).hasWorker(workerId);

            if (workingDataHasWorker &&
                    WorkingData.getInstance(context).getWorkerById(workerId).lastUpdatedTime >= lastUpdatedTime) {
                return;
            }

            if (workingDataHasWorker) {
                WorkingData.getInstance(context).getWorkerById(workerId).update(retrieveWorkerFromJson(context, workerJson));
            } else {
                WorkingData.getInstance(context).addWorker(retrieveWorkerFromJson(context, workerJson));
            }

        } catch (JSONException e) {
            Log.e(TAG, "Exception in addWorkerToWorkingData()");
            e.printStackTrace();
        }
    }
    private static void addVendorToWorkingData(Context context, JSONObject vendorJson) {
        try {
            String vendorId = vendorJson.getString("_id");
            long lastUpdatedTime = vendorJson.getLong("updatedAt");
            boolean workingDataHasVendor = WorkingData.getInstance(context).hasVendor(vendorId);

            if (workingDataHasVendor &&
                    WorkingData.getInstance(context).getVendorById(vendorId).lastUpdatedTime >= lastUpdatedTime) {
                return;
            }

            if (workingDataHasVendor) {
                WorkingData.getInstance(context).updateVendor(vendorId, retrieveVendorFromJson(vendorJson));
            } else {
                WorkingData.getInstance(context).addVendor(retrieveVendorFromJson(vendorJson));
            }
        } catch (JSONException e) {
            Log.e(TAG, "Exception in addVendorToWorkingData()");
            e.printStackTrace();
        }
    }
    private static void addManagerToWorkingData(Context context, JSONObject managerJson) {
        try {
            String managerId = managerJson.getString("_id");
            long lastUpdatedTime = managerJson.getLong("updatedAt");
            boolean hasManager = WorkingData.getInstance(context).hasManager(managerId);

            if (hasManager &&
                    WorkingData.getInstance(context).getManagerById(managerId).lastUpdatedTime >= lastUpdatedTime) {
                return;
            }

            if (hasManager) {
                WorkingData.getInstance(context).updateManager(managerId, retrieveManagerFromJson(managerJson));
            } else {
                WorkingData.getInstance(context).addManager(retrieveManagerFromJson(managerJson));
            }
        } catch (JSONException e) {
            Log.e(TAG, "Exception in addManagerToWorkingData()");
            e.printStackTrace();
        }
    }
    private static void addWarningToWorkingData(Context context, JSONObject warningJson) {
        try {
            String warningId = warningJson.getString("_id");
            long lastUpdatedTime = warningJson.getLong("updatedAt");
            boolean workingDataHasWarning = WorkingData.getInstance(context).hasWarning(warningId);

            if (workingDataHasWarning &&
                    WorkingData.getInstance(context).getWarningById(warningId).lastUpdatedTime >= lastUpdatedTime) {
                return;
            }

            if (workingDataHasWarning) {
                WorkingData.getInstance(context).updateWarning(warningId, retrieveWarningFromJson(warningJson));
            } else {
                WorkingData.getInstance(context).addWarning(retrieveWarningFromJson(warningJson));
            }
        } catch (JSONException e) {
            Log.e(TAG, "Exception in addWarningToWorkingData()");
            e.printStackTrace();
        }
    }
    private static void addTagToWorkingData(Context context, JSONObject tagJson) {
        try {
            String tagId = tagJson.getString("_id");
            long lastUpdatedTime = tagJson.getLong("updatedAt");
            boolean workingDataHasTag = WorkingData.getInstance(context).hasTag(tagId);

            if (workingDataHasTag &&
                    WorkingData.getInstance(context).getTagById(tagId).lastUpdatedTime >= lastUpdatedTime) {
                return;
            }

            if (workingDataHasTag) {
                WorkingData.getInstance(context).updateTag(tagId, retrieveTagFromJson(tagJson));
            } else {
                WorkingData.getInstance(context).addTag(retrieveTagFromJson(tagJson));
            }
        } catch (JSONException e) {
            Log.e(TAG, "Exception in addTagToWorkingData()");
            e.printStackTrace();
        }
    }
    private static void addEquipmentToWorkingData(Context context, JSONObject equipmentJson) {
        try {
            String equipmentId = equipmentJson.getString("_id");
            long lastUpdatedTime = equipmentJson.getLong("updatedAt");
            boolean hasEquipment = WorkingData.getInstance(context).hasEquipment(equipmentId);

            if (hasEquipment &&
                    WorkingData.getInstance(context).getEquipmentById(equipmentId).lastUpdatedTime >= lastUpdatedTime) {
                return;
            }

            if (hasEquipment) {
                WorkingData.getInstance(context).updateEquipment(equipmentId, retrieveEquipmentFromJson(equipmentJson));
            } else {
                WorkingData.getInstance(context).addEquipment(retrieveEquipmentFromJson(equipmentJson));
            }
        } catch (JSONException e) {
            Log.e(TAG, "Exception in addEquipmentToWorkingData()");
            e.printStackTrace();
        }
    }


    private static Case retrieveCaseFromJson(Context context, JSONObject caseJson) {
        try {
            JSONObject caseIndustrialForm = caseJson.getJSONObject("industrialForm");
            JSONObject caseVendor = caseJson.getJSONObject("client");
            JSONObject caseManager = caseJson.getJSONObject("lead");
            JSONArray caseMovableMoldSize = caseIndustrialForm.getJSONArray("movableMoldSize");
            JSONArray caseFixedMoldSize = caseIndustrialForm.getJSONArray("fixedMoldSize");
            JSONArray caseSupportBlockMoldSize = caseIndustrialForm.getJSONArray("supportBlockMoldSize");
            JSONArray caseTagListJson = caseJson.getJSONArray("tags");
            JSONArray caseWorkerIds = caseJson.getJSONArray("employeeIdList");

            String id = caseJson.getString("_id");
            String name = caseJson.getString("name");
            String description = getStringFromJson(caseJson, "details");

            addVendorToWorkingData(context, caseVendor);
            String vendorId = caseVendor.getString("_id");

            addManagerToWorkingData(context, caseManager);
            String managerId = caseManager.getString("_id");

            int plateCount = caseIndustrialForm.getInt("plateCount");
            int supportBlockCount = caseIndustrialForm.getInt("supportBlockCount");
            long lastUpdatedTime = caseJson.getLong("updatedAt");

            double[] movableMoldSize = {
                    caseMovableMoldSize.getDouble(0),
                    caseMovableMoldSize.getDouble(1),
                    caseMovableMoldSize.getDouble(2),
                    caseIndustrialForm.getDouble("movableMoldWeight")
            };
            double[] fixedMoldSize = {
                    caseFixedMoldSize.getDouble(0),
                    caseFixedMoldSize.getDouble(1),
                    caseFixedMoldSize.getDouble(2),
                    caseIndustrialForm.getDouble("fixedMoldWeight")
            };
            double[] supportBlockMoldSize = {
                    caseSupportBlockMoldSize.getDouble(0),
                    caseSupportBlockMoldSize.getDouble(1),
                    caseSupportBlockMoldSize.getDouble(2),
                    caseIndustrialForm.getDouble("supportBlockMoldWeight")
            };

            Date deliveredDate = getDateFromJson(caseJson, "willDeliverAt");
            Date materialPurchasedDate = getDateFromJson(caseIndustrialForm, "materialPurchasedAt");
            Date layoutDeliveredDate = getDateFromJson(caseIndustrialForm, "layoutDeliveredAt");

            List<Tag> tags = new ArrayList<>();
            for (int j = 0 ; j < caseTagListJson.length() ; j++) {
                JSONObject caseTag = caseTagListJson.getJSONObject(j);
                String tagId = caseTag.getString("_id");

                addTagToWorkingData(context, caseTag);
                tags.add(WorkingData.getInstance(context).getTagById(tagId));
            }

            List<String> workerIds = new ArrayList<>();
            for (int w = 0 ; w < caseWorkerIds.length() ; w++) {
                workerIds.add(caseWorkerIds.getString(w));
            }

//            Log.d(TAG, "Case id = " + id);
//            Log.d(TAG, "Case name = " + name);
//            Log.d(TAG, "Case description = " + description);
//            Log.d(TAG, "Case vendorId = " + vendorId);
//            Log.d(TAG, "Case managerId = " + managerId);
//            Log.d(TAG, "Case deliveredDate = " + deliveredDate.getTime());
//            Log.d(TAG, "Case materialPurchasedDate = " + materialPurchasedDate.getTime());
//            Log.d(TAG, "Case layoutDeliveredDate = " + layoutDeliveredDate.getTime());
//            Log.d(TAG, "Case movableMoldSize = " + description);
//            Log.d(TAG, "Case movableMoldSize = "
//                    + movableMoldSize[0] + " " + movableMoldSize[1] + " " + movableMoldSize[2] + " " + movableMoldSize[3]);
//            Log.d(TAG, "Case fixedMoldSize = "
//                    + fixedMoldSize[0] + " " + fixedMoldSize[1] + " " + fixedMoldSize[2] + " " + fixedMoldSize[3]);
//            Log.d(TAG, "Case supportBlockMoldSize = "
//                    + supportBlockMoldSize[0] + " " + supportBlockMoldSize[1] + " "
//                    + supportBlockMoldSize[2] + " " + supportBlockMoldSize[3]);
//            Log.d(TAG, "Case plateCount = " + plateCount);
//            Log.d(TAG, "Case supportBlockCount = " + supportBlockCount);
//            Log.d(TAG, "Case tags = " + tags);
//            Log.d(TAG, "Case workerIds = " + workerIds);
//            Log.d(TAG, "Case lastUpdatedTime = " + lastUpdatedTime);


            return new Case(
                    id,
                    name,
                    description,
                    vendorId,
                    managerId,
                    deliveredDate,
                    materialPurchasedDate,
                    layoutDeliveredDate,
                    new Case.Size(movableMoldSize),
                    new Case.Size(fixedMoldSize),
                    new Case.Size(supportBlockMoldSize),
                    plateCount,
                    supportBlockCount,
                    tags,
                    workerIds,
                    lastUpdatedTime);

        } catch (JSONException e) {
            Log.e(TAG, "Exception in retrieveCaseFromJson()");
            e.printStackTrace();
        }

        return null;
    }
    private static Factory retrieveFactoryFromJson(Context context, JSONObject factoryJson) {
        try {
            JSONArray managerJsonList = factoryJson.getJSONArray("managerList");

            String id = factoryJson.getString("_id");
            String name = factoryJson.getString("name");
            long lastUpdatedTime = factoryJson.getLong("updatedAt");

            List<Manager> managers = new ArrayList<>();
            for (int m = 0 ; m < managerJsonList.length() ; m++) {
                JSONObject managerJson = managerJsonList.getJSONObject(m);
                String managerId = managerJson.getString("_id");

                addManagerToWorkingData(context, managerJson);
                managers.add(WorkingData.getInstance(context).getManagerById(managerId));
            }

            return new Factory(id, name, managers, lastUpdatedTime);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return null;
    }
    private static Worker retrieveWorkerFromJson(Context context, JSONObject workerJson) {
        try {
            JSONObject paymentJson = workerJson.getJSONObject("paymentClassification");
            JSONObject equipmentJson = getJsonObjectFromJson(workerJson, "resource");
            JSONArray scheduledTaskJsonList = workerJson.getJSONArray("scheduledTaskIds");

            String id = workerJson.getString("_id");
            String name = workerJson.getJSONObject("profile").getString("name");
            String factoryId = getStringFromJson(workerJson, "groupId");
            String equipmentId = "";
            String wipTaskId = getStringFromJson(workerJson, "WIPTaskId");
            String address = getStringFromJson(workerJson, "address");
            String phone = getStringFromJson(workerJson, "phone");

            if (equipmentJson != null) {
                equipmentId = equipmentJson.getString("_id");
                addEquipmentToWorkingData(context, equipmentJson);
            }

            int score = workerJson.getInt("score");
            long lastUpdatedTime = workerJson.getLong("updatedAt");
            boolean isOvertime = workerJson.getBoolean("overwork");

            Worker.Status status = Worker.convertStringToStatus(workerJson.getString("status"));

            Worker.PaymentClassification payment =
                    new Worker.PaymentClassification(paymentJson.getString("type"),
                                                     paymentJson.getDouble("base"),
                                                     paymentJson.getDouble("hourlyPayment"),
                                                     paymentJson.getDouble("overtimeBase"));

            List<String> scheduledTaskIds = new ArrayList<>();
            for (int st = 0 ; st < scheduledTaskJsonList.length() ; st++) {
                scheduledTaskIds.add(scheduledTaskJsonList.getString(st));
            }

            return new Worker(
                    id,
                    name,
                    factoryId,
                    wipTaskId,
                    address,
                    phone,score,
                    isOvertime,
                    status,
                    payment,
                    scheduledTaskIds,
                    lastUpdatedTime);

        } catch (JSONException e) {
            Log.e(TAG, "Exception in retrieveWorkerFromJson()");
            e.printStackTrace();
        }

        return null;
    }
    private static Task retrieveTaskFromJson(Context context, JSONObject taskJson) {
        try {
            JSONArray warningJsonList = taskJson.getJSONArray("taskExceptions");
            JSONObject equipmentJson = getJsonObjectFromJson(taskJson, "resource");

            String id = taskJson.getString("_id");
            String name = taskJson.getString("name");
            String caseId = taskJson.getString("caseId");
            String workerId = getStringFromJson(taskJson, "employeeId");
            String equipmentId = "";

            if (equipmentJson != null) {
                equipmentId = equipmentJson.getString("_id");
                addEquipmentToWorkingData(context, equipmentJson);
            }

            Task.Status status = Task.convertStringToStatus(taskJson.getString("status"));

            long expectedTime = taskJson.getLong("expectedTime");
            long spentTime = taskJson.getLong("spentTime");
            long lastUpdatedTime = taskJson.getLong("updatedAt");

            Date startDate = getDateFromJson(taskJson, "startDate");
            Date endDate = getDateFromJson(taskJson, "endDate");
            Date assignDate = getDateFromJson(taskJson, "dispatchedDate");

            List<Warning> warnings = new ArrayList<>();
            for (int w = 0 ; w < warningJsonList.length() ; w++) {
                JSONObject warningJson = warningJsonList.getJSONObject(w);
                String warningId = warningJson.getString("_id");

                addWarningToWorkingData(context, warningJson);
                warnings.add(WorkingData.getInstance(context).getWarningById(warningId));
            }

            // TODO: Sub task

            return new Task(
                    id,
                    name,
                    caseId,
                    workerId,
                    equipmentId,
                    status,
                    assignDate,
                    startDate,
                    endDate,
                    warnings,
                    expectedTime,
                    spentTime,
                    lastUpdatedTime);

        } catch (JSONException e) {
            Log.e(TAG, "Exception in retrieveTaskFromJson()");
            e.printStackTrace();
        }

        return null;
    }
    private static Vendor retrieveVendorFromJson(JSONObject vendorJson) {
        try {
            JSONArray caseIdsJson = vendorJson.getJSONArray("caseIds");

            String id = vendorJson.getString("_id");
            String name = vendorJson.getString("name");
            String address = getStringFromJson(vendorJson, "address");
            String phone = getStringFromJson(vendorJson, "phone");
            long lastUpdatedTime = vendorJson.getLong("updatedAt");

            List<String> caseIds = new ArrayList<>();
            for (int i = 0 ; i < caseIdsJson.length() ; i++) {
                caseIds.add(caseIdsJson.getString(i));
            }

            return new Vendor(id, name, address, phone, caseIds, lastUpdatedTime);
        } catch (JSONException e) {
            Log.e(TAG, "Exception in retrieveVendorFromJson()");
            e.printStackTrace();
        }

        return null;
    }
    private static Manager retrieveManagerFromJson(JSONObject managerJson) {
        try {
            String id = managerJson.getString("_id");
            String name = managerJson.getJSONObject("profile").getString("name");
            long lastUpdatedTime = managerJson.getLong("updatedAt");

            return new Manager(id, name, lastUpdatedTime);
        } catch (JSONException e) {
            Log.e(TAG, "Exception in retrieveManagerFromJson()");
            e.printStackTrace();
        }

        return null;
    }
    private static Tag retrieveTagFromJson(JSONObject tagJson) {
        try {
            String id = tagJson.getString("_id");
            String name = tagJson.getString("name");
            long lastUpdatedTime = tagJson.getLong("updatedAt");

            return new Tag(id, name, lastUpdatedTime);
        } catch (JSONException e) {
            Log.e(TAG, "Exception in retrieveTagFromJson()");
            e.printStackTrace();
        }

        return null;
    }
    private static Warning retrieveWarningFromJson(JSONObject warningJson) {
        try {
            String id = warningJson.getString("_id");
            String name = warningJson.getString("name");
            String caseId = warningJson.getString("caseId");
            String taskId = warningJson.getString("taskId");
            String workerId = warningJson.getString("employeeId");

            long spentTime = warningJson.getLong("spentTime");
            long lastUpdatedTime = warningJson.getLong("updatedAt");

            Warning.Status status = Warning.convertStringToStatus(warningJson.getString("status"));

            return new Warning(
                    id,
                    name,
                    caseId,
                    taskId,
                    workerId,
                    status,
                    spentTime,
                    lastUpdatedTime);

        } catch (JSONException e) {
            Log.e(TAG, "Exception in retrieveTagFromJson()");
            e.printStackTrace();
        }

        return null;
    }
    private static Equipment retrieveEquipmentFromJson(JSONObject equipmentJson) {
        try {
            String id = equipmentJson.getString("_id");
            String name = equipmentJson.getString("name");
            String description = getStringFromJson(equipmentJson, "details");
            String factoryId = equipmentJson.getString("workingGroupId");

            long lastUpdatedTime = equipmentJson.getLong("updatedAt");
            Date purchasedDate = getDateFromJson(equipmentJson, "purchasedDate");

            Equipment.Status status = Equipment.convertStringToStatus(equipmentJson.getString("status"));

            return new Equipment(
                    id,
                    name,
                    description,
                    factoryId,
                    status,
                    purchasedDate,
                    lastUpdatedTime);

        } catch (JSONException e) {
            Log.e(TAG, "Exception in retrieveTagFromJson()");
            e.printStackTrace();
        }

        return null;
    }


    private static String getTasksByCaseUrl(String caseId) {
        return WorkingDataUrl.TASKS_BY_CASE + caseId;
    }
    private static String getTasksByWorkerUrl(String workerId) {
        return WorkingDataUrl.TASKS_BY_WORKER + workerId;
    }
    private static String getWorkersByFactoryUrl(String factoryId) {
        return WorkingDataUrl.WORKERS_BY_FACTORY + factoryId;
    }


    private static Date getDateFromJson(JSONObject jsonObject, String key) throws JSONException {
        return jsonObject.has(key) ? new Date(jsonObject.getLong(key)) : null;
    }
    private static String getStringFromJson(JSONObject jsonObject, String key) throws JSONException {
        return jsonObject.has(key) ? jsonObject.getString(key) : "";
    }
    private static long getLongFromJson(JSONObject jsonObject, String key) throws JSONException {
        return jsonObject.has(key) ? jsonObject.getLong(key) : -1L;
    }
    private static JSONObject getJsonObjectFromJson(JSONObject jsonObject, String key) throws JSONException {
        return jsonObject.has(key) ? jsonObject.getJSONObject(key) : null;
    }
}
