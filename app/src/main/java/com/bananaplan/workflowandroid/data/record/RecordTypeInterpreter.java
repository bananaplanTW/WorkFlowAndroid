package com.bananaplan.workflowandroid.data.record;

/**
 * Created by daz on 10/10/15.
 */
public class RecordTypeInterpreter {
    public static String getTranslation(String type) {
        switch (type) {
            case "checkIn":
                return "打卡上班";
            case "checkOut":
                return "打卡下班";
            case "becomeWIP":
                return "忙碌中";
            case "becomePause":
                return "暫停中";
            case "becomeResume":
                return "復工";
            case "becomeOverwork":
                return "加班中";
            case "becomeStop":
                return "下班";
            case "becomePending":
                return "閒置中";
            case "becomeOff":
                return "休假";


            case "dispatchTask":
                return "分派這項工作：";
            case "startTask":
                return "開始這項工作：";
            case "suspendTask":
                return "中斷這項工作：";
            case "completeTask":
                return "完成這項工作：";
            case "unloadTask":
                return "解除這項工作：";
            case "passReviewTask":
                return "工作通過檢驗：";
            case "failReviewTask":
                return "工作沒通過檢驗：";
            case "createTaskException":
                return "工作發生警訊：";
            case "completeTaskException":
                return "工作解除警訊：";


            default:
                return type;
        }

    }
}
