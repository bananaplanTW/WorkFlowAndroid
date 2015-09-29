package com.bananaplan.workflowandroid.data.server;

import android.content.Context;
import android.util.Log;

import com.bananaplan.workflowandroid.data.Case;
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
import java.util.concurrent.ExecutionException;


/**
 * Utility for loading data from server
 *
 * @author Danny Lin
 * @since 2015/9/23.
 */
public class LoadDataUtils {

    private static final String TAG = "LoadDataUtils";

    private static final class WorkingDataUrl {
        public static final String WORKERS = "http://bp-workflow.cloudapp.net:3000/api/employees";
        public static final String CASES = "http://bp-workflow.cloudapp.net:3000/api/cases";
        public static final String FACTORIES = "http://bp-workflow.cloudapp.net:3000/api/groups";
        public static final String TASKS_BY_CASE = "http://bp-workflow.cloudapp.net:3000/api/tasks?caseId=";
        public static final String WORKERS_BY_FACTORY = "http://bp-workflow.cloudapp.net:3000/api/group/employees?groupId=";
//        public static final String WORKERS = "http://10.1.1.28:3000/api/employees";
//        public static final String CASES = "http://10.1.1.28:3000/api/cases";
//        public static final String TASKS_BY_CASE = "http://10.1.1.28:3000/api/tasks?caseId=";
    }

    public static void loadCases(Context context) {
        try {
            String caseJsonListString = RestfulUtils.getJsonStringFromUrl(WorkingDataUrl.CASES);
            JSONArray caseJsonList = new JSONObject(caseJsonListString).getJSONArray("result");

            for (int i = 0; i < caseJsonList.length(); i++) {
                JSONObject caseJson = caseJsonList.getJSONObject(i);
                JSONObject caseVendor = caseJson.getJSONObject("client");
                JSONObject caseManager = caseJson.getJSONObject("lead");
                JSONArray caseTags = caseJson.getJSONArray("tags");

                addCaseToWorkingData(context, caseJson);
                addVendorToWorkingData(context, caseVendor);
                addManagerToWorkingData(context, caseManager);
                for (int j = 0 ; j < caseTags.length() ; j++) {
                    addTagToWorkingData(context, caseTags.getJSONObject(j));
                }
            }

        } catch (JSONException e) {
            Log.e(TAG, "Exception in loadCases()");
            e.printStackTrace();
        }
    }
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
            String taskJsonString = RestfulUtils.getJsonStringFromUrl(getTasksByCaseUrl(caseId));
            JSONArray taskJsonList = new JSONObject(taskJsonString).getJSONArray("result");
            List<Task> newCaseTasks = new ArrayList<>();

            //Log.d(TAG, "Case  " + WorkingData.getInstance(context).getCaseById(caseId).name + " tasks :");
            for (int i = 0 ; i < taskJsonList.length() ; i++) {
                JSONObject taskJson = taskJsonList.getJSONObject(i);
                JSONArray taskWarnings = taskJson.getJSONArray("taskExceptions");

                String id = taskJson.getString("_id");
                String taskCaseId = taskJson.getString("caseId");
                String workerId = getStringFromJson(taskJson, "employeeId");
                String equipmentId = getStringFromJson(taskJson, "resourceId");
                String name = taskJson.getString("name");
                Task.Status status = Task.convertStringToStatus(taskJson.getString("status"));
                long expectedTime = taskJson.getLong("expectedTime");
                long spentTime = taskJson.getLong("spentTime");
                Date startDate = getDateFromJson(taskJson, "startDate");
                Date endDate = getDateFromJson(taskJson, "endDate");
                Date assignDate = getDateFromJson(taskJson, "dispatchedDate");

                // TODO: Warning list
//                List<Warning> warnings = new ArrayList<>();
//                for (int j = 0 ; j < taskWarnings.length() ; j++) {
//                    warnings.add();
//                }


                // TODO: Sub tasks list

                Task newTask = new Task(
                        id,
                        name,
                        taskCaseId,
                        workerId,
                        equipmentId,
                        status,
                        expectedTime,
                        spentTime,
                        assignDate,
                        startDate,
                        endDate,
                        new ArrayList<Warning>());

                newCaseTasks.add(newTask);

                WorkingData.getInstance(context).addTask(newTask);

                //Log.d(TAG, "Task " + i + " " + newTask.name);
            }

            WorkingData.getInstance(context).getCaseById(caseId).tasks = newCaseTasks;

        } catch (JSONException e) {
            Log.e(TAG, "Exception in loadTasksByCase()");
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
                Worker newWorker = addWorkerToWorkingData(context, workerJson);

                if (newWorker != null) {
                    newWorkers.add(newWorker);
                }
            }

            WorkingData.getInstance(context).getFactoryById(factoryId).workers = newWorkers;

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    private static void addCaseToWorkingData(Context context, JSONObject caseJson) {
        try {
            String caseId = caseJson.getString("_id");
            long lastUpdatedTime = caseJson.getLong("updatedAt");
            boolean workingDataHasCase = WorkingData.getInstance(context).hasCase(caseId);

            if (workingDataHasCase &&
                    WorkingData.getInstance(context).getCaseById(caseId).lastUpdatedTime >= lastUpdatedTime) {
                return;
            }

            if (workingDataHasCase) {
                WorkingData.getInstance(context).getCaseById(caseId).update(retrieveCaseFromJson(caseJson));
//                Log.d(TAG, "Update case " + caseJson.getString("name"));
//                Log.d(TAG, "Local lastUpdatedTime =  " + WorkingData.getInstance(context).getCaseById(caseId).lastUpdatedTime);
//                Log.d(TAG, "Server lastUpdatedTime =  " + lastUpdatedTime);
            } else {
                WorkingData.getInstance(context).addCase(retrieveCaseFromJson(caseJson));
//                Log.d(TAG, "Add new case " + caseJson.getString("name"));
            }
        } catch (JSONException e) {
            Log.e(TAG, "Exception in addCaseToWorkingData()");
            e.printStackTrace();
        }
    }
    private static void addTaskToWorkingData(Context context, JSONObject taskJson) {
        // TODO: addTaskToWorkingData
    }
    private static void addFactoryToWorkingData(Context context, JSONObject factoryJson) {
        try {
            String factoryId = factoryJson.getString("_id");
            long lastUpdatedTime = factoryJson.getLong("updatedAt");
            boolean workingDataHasFactory = WorkingData.getInstance(context).hasFactory(factoryId);

            if (workingDataHasFactory &&
                    WorkingData.getInstance(context).getFactoryById(factoryId).lastUpdatedTime >= lastUpdatedTime) {
                return;
            }

            if (workingDataHasFactory) {
                WorkingData.getInstance(context).getFactoryById(factoryId).update(retrieveFactoryFromJson(context, factoryJson));
            } else {
                WorkingData.getInstance(context).addFactory(retrieveFactoryFromJson(context, factoryJson));
            }
            //Log.d(TAG, "Add factory " + factoryJson.getString("name"));

        } catch (JSONException e) {
            Log.e(TAG, "Exception in addFactoryToWorkingData()");
            e.printStackTrace();
        }
    }
    private static Worker addWorkerToWorkingData(Context context, JSONObject workerJson) {
        Worker worker = null;

        try {
            String workerId = workerJson.getString("_id");
            long lastUpdatedTime = workerJson.getLong("updatedAt");
            boolean workingDataHasWorker = WorkingData.getInstance(context).hasWorker(workerId);

            if (workingDataHasWorker &&
                    WorkingData.getInstance(context).getWorkerById(workerId).lastUpdatedTime >= lastUpdatedTime) {
                worker = WorkingData.getInstance(context).getWorkerById(workerId);
            }

            worker = retrieveWorkerFromJson(workerJson);
            if (workingDataHasWorker) {
                WorkingData.getInstance(context).getWorkerById(workerId).update(worker);
            } else {
                WorkingData.getInstance(context).addWorker(worker);
            }
            //Log.d(TAG, "Add worker " + workerJson.getString("username"));

        } catch (JSONException e) {
            Log.e(TAG, "Exception in addWorkerToWorkingData()");
            e.printStackTrace();
        }

        return worker;
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
                WorkingData.getInstance(context).getVendorById(vendorId).update(retrieveVendorFromJson(vendorJson));
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
            boolean workingDataHasManager = WorkingData.getInstance(context).hasManager(managerId);

            if (workingDataHasManager &&
                WorkingData.getInstance(context).getManagerById(managerId).lastUpdatedTime >= lastUpdatedTime) {
                return;
            }

            if (workingDataHasManager) {
                WorkingData.getInstance(context).getManagerById(managerId).update(retrieveManagerFromJson(managerJson));
            } else {
                WorkingData.getInstance(context).addManager(retrieveManagerFromJson(managerJson));
            }
        } catch (JSONException e) {
            Log.e(TAG, "Exception in addManagerToWorkingData()");
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
                WorkingData.getInstance(context).getTagById(tagId).update(retrieveTagFromJson(tagJson));
            } else {
                WorkingData.getInstance(context).addTag(retrieveTagFromJson(tagJson));
            }
        } catch (JSONException e) {
            Log.e(TAG, "Exception in addTagToWorkingData()");
            e.printStackTrace();
        }
    }


    private static Case retrieveCaseFromJson(JSONObject caseJson) {
        try {
            JSONObject caseIndustrialForm = caseJson.getJSONObject("industrialForm");
            JSONArray caseMovableMoldSize = caseIndustrialForm.getJSONArray("movableMoldSize");
            JSONArray caseFixedMoldSize = caseIndustrialForm.getJSONArray("fixedMoldSize");
            JSONArray caseSupportBlockMoldSize = caseIndustrialForm.getJSONArray("supportBlockMoldSize");
            JSONArray caseTags = caseJson.getJSONArray("tags");
            JSONArray caseWorkerIds = caseJson.getJSONArray("employeeIdList");

            String id = caseJson.getString("_id");
            String name = caseJson.getString("name");
            String vendorId = caseJson.getJSONObject("client").getString("_id");
            String managerId = caseJson.getJSONObject("lead").getString("_id");
            String description = getStringFromJson(caseJson, "details");

            long lastUpdatedTime = caseJson.getLong("updatedAt");

            Date deliveredDate = getDateFromJson(caseJson, "willDeliverAt");
            Date materialPurchasedDate = getDateFromJson(caseIndustrialForm, "materialPurchasedAt");
            Date layoutDeliveredDate = getDateFromJson(caseIndustrialForm, "layoutDeliveredAt");

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

            int plateCount = caseIndustrialForm.getInt("plateCount");
            int supportBlockCount = caseIndustrialForm.getInt("supportBlockCount");

            List<Tag> tags = new ArrayList<>();
            for (int j = 0 ; j < caseTags.length() ; j++) {
                JSONObject caseTag = caseTags.getJSONObject(j);
                tags.add(new Tag(caseTag.getString("_id"), caseTag.getString("name"), caseTag.getLong("updatedAt")));
            }

            List<String> workerIds = new ArrayList<>();
            for (int j = 0 ; j < caseWorkerIds.length() ; j++) {
                workerIds.add(caseWorkerIds.getString(j));
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
    private static Worker retrieveWorkerFromJson(JSONObject workerJson) {
        try {
            JSONObject paymentJson = workerJson.getJSONObject("paymentClassification");

            String id = workerJson.getString("_id");
            String name = workerJson.getJSONObject("profile").getString("name");
            String factoryId = getStringFromJson(workerJson, "groupId");
            String address = getStringFromJson(workerJson, "address");
            String phone = getStringFromJson(workerJson, "phone");
            int score = workerJson.getInt("score");
            long lastUpdatedTime = workerJson.getLong("updatedAt");
            boolean isOvertime = workerJson.getBoolean("overwork");

            Worker.Status status = Worker.convertStringToStatus(workerJson.getString("status"));

            Worker.PaymentClassification payment =
                    new Worker.PaymentClassification(paymentJson.getString("type"),
                                                     paymentJson.getDouble("base"),
                                                     paymentJson.getDouble("hourlyPayment"),
                                                     paymentJson.getDouble("overtimeBase"));

            return new Worker(
                    id,
                    name,
                    factoryId,
                    address,
                    phone,score,
                    isOvertime,
                    status,
                    payment,
                    lastUpdatedTime);

        } catch (JSONException e) {
            Log.e(TAG, "Exception in retrieveWorkerFromJson()");
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


    private static String getTasksByCaseUrl(String caseId) {
        return WorkingDataUrl.TASKS_BY_CASE + caseId;
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
}
