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
            default:
                return type;
        }
    }
}
