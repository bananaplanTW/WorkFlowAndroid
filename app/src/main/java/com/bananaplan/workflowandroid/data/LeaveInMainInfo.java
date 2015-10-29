package com.bananaplan.workflowandroid.data;

import android.content.Context;
import android.content.res.Resources;

import com.bananaplan.workflowandroid.R;

/**
 * @author Danny Lin
 * @since 2015/9/22.
 */
public class LeaveInMainInfo extends IdData {

    // DEFAULT無請假類別
    // PERSONAL事假, MEDICAL病假, ANNUAL特休假, OFFICIAL公假,
    // FUNERAL喪假, MATERNITY產假, PATERNITY陪產假, MENSTRUATION生理假, COMPENSATORY補假
    public enum Type {
        DEFAULT, PERSONAL, MEDICAL, ANNUAL, OFFICIAL, FUNERAL, MATERNITY, PATERNITY, MENSTRUATION, COMPENSATORY
    }

    public String workerId;
    public String description;

    public Type type;

    public long from = 0L;
    public long to = 0L;


    public LeaveInMainInfo(String id, String workerId, Type type, long from, long to, String description, long lastUpdatedTime) {
        this.id = id;
        this.workerId = workerId;
        this.type = type;
        this.from = from;
        this.to = to;
        this.description = description;
        this.lastUpdatedTime = lastUpdatedTime;
    }

    public void update(LeaveInMainInfo leaveInMainInfo) {
        this.workerId = leaveInMainInfo.workerId;
        this.type = leaveInMainInfo.type;
        this.from = leaveInMainInfo.from;
        this.to = leaveInMainInfo.to;
        this.description = leaveInMainInfo.description;
        this.lastUpdatedTime = leaveInMainInfo.lastUpdatedTime;
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

    public static String getLeaveString(Context context, Type type) {
        String r = "";
        Resources resources = context.getResources();

        switch (type) {
            case PERSONAL:
                r = resources.getString(R.string.leave_type_personal);
                break;

            case MEDICAL:
                r = resources.getString(R.string.leave_type_medical);
                break;

            case ANNUAL:
                r = resources.getString(R.string.leave_type_annual);
                break;

            case OFFICIAL:
                r = resources.getString(R.string.leave_type_official);
                break;

            case FUNERAL:
                r = resources.getString(R.string.leave_type_funeral);
                break;

            case MATERNITY:
                r = resources.getString(R.string.leave_type_maternity);
                break;

            case PATERNITY:
                r = resources.getString(R.string.leave_type_paternity);
                break;

            case MENSTRUATION:
                r = resources.getString(R.string.leave_type_menstruation);
                break;

            case COMPENSATORY:
                r = resources.getString(R.string.leave_type_compensatory);
                break;


            default:
                r = resources.getString(R.string.leave_type_default);
                break;
        }

        return r;
    }
}
