package com.bananaplan.workflowandroid.data.activity;

/**
 * Created by daz on 10/11/15.
 */
public class TaskActivityTypeInterpreter {
    public static String getTranslation(String type) {
        switch (type) {
            case "start":
                return "更新工作狀態：開始";
            case "suspend":
                return "更新工作狀態：中斷";
            case "complete":
                return "更新工作狀態：完成";
            case "pause":
                return "更新工作狀態：暫停";
            case "resume":
                return "更新工作狀態：復原";
            case "pass":
                return "通過檢驗";
            case "fail":
                return "沒通過檢驗";
            case "create_exception":
                return "新增工作警訊：";
            case "complete_exception":
                return "解除工作警訊：";
            case "dispatch":
                return "把工作發派給：";

            default:
                return type;
        }

    }
}
