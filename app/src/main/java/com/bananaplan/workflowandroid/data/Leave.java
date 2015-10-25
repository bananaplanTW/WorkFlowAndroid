package com.bananaplan.workflowandroid.data;

/**
 * Created by logicmelody on 2015/9/22.
 */
public class Leave extends IdData {

    // DEFAULT無請假類別
    // PERSONAL事假, MEDICAL病假, ANNUAL特休假, OFFICIAL公假,
    // FUNERAL喪假, MATERNITY產假, PATERNITY陪產假, MENSTRUATION生理假, COMPENSATORY補假
    public enum Type {
        DEFAULT, PERSONAL, MEDICAL, ANNUAL, OFFICIAL, FUNERAL, MATERNITY, PATERNITY, MENSTRUATION, COMPENSATORY
    }

    public String workerId;
    public String description;

    public Type type;


    public Leave(String id, String workerId, Type type, String description, long lastUpdatedTime) {
        this.id = id;
        this.workerId = workerId;
        this.type = type;
        this.description = description;
        this.lastUpdatedTime = lastUpdatedTime;
    }

    public void update(Leave leave) {
        this.workerId = leave.workerId;
        this.type = leave.type;
        this.description = leave.description;
        this.lastUpdatedTime = leave.lastUpdatedTime;
    }

    public static Type convertStringToType(String type) {
        Type result = Type.DEFAULT;

        if ("personal".equals(type)) {
            result = Type.PERSONAL;

        } else if ("medical".equals(type)) {
            result = Type.MEDICAL;

        } else if ("annual".equals(type)) {
            result = Type.ANNUAL;

        } else if ("official".equals(type)) {
            result = Type.OFFICIAL;

        } else if ("funeral".equals(type)) {
            result = Type.FUNERAL;

        } else if ("maternity".equals(type)) {
            result = Type.MATERNITY;

        } else if ("paternity".equals(type)) {
            result = Type.PATERNITY;

        } else if ("menstruation".equals(type)) {
            result = Type.MENSTRUATION;

        } else if ("compensatory".equals(type)) {
            result = Type.COMPENSATORY;

        }

        return result;
    }
}
