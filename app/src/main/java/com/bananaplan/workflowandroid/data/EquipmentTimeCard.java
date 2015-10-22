package com.bananaplan.workflowandroid.data;

/**
 * Created by daz on 10/22/15.
 */
public class EquipmentTimeCard {
    public String id;
    public String equipmentId;
    public long startDate;
    public long endDate;
    // [TODO] should use universal timecard status constant
    public CaseTimeCard.STATUS status;
    public long createdDate;
    public long updatedDate;

    public EquipmentTimeCard(String id, String equipmentId, long startDate,
                          long endDate, CaseTimeCard.STATUS status, long createdDate, long updatedDate) {
        this.id = id;
        this.equipmentId = equipmentId;
        this.startDate = startDate;
        this.endDate = endDate;
        this.status = status;
        this.createdDate = createdDate;
        this.updatedDate = updatedDate;
    }
}
