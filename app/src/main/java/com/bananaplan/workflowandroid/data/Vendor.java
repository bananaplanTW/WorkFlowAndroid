package com.bananaplan.workflowandroid.data;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by Ben on 2015/7/22.
 */
public class Vendor extends IdData {

    public String address;
    public String phone;

    public List<String> caseIds = new ArrayList<>();
    private List<Case> cases = new ArrayList<>();


    public Vendor(String id, String name, String address, String phone, List<String> caseIds, long lastUpdatedTime) {
        this.id = id;
        this.name = name;
        this.address = address;
        this.phone = phone;
        this.caseIds = caseIds;
        this.lastUpdatedTime = lastUpdatedTime;
    }

    public Vendor(String id, String name) {
        this(id, name, new ArrayList<Case>());
    }

    public Vendor(String id, String name, ArrayList<Case> cases) {
        this.id = id;
        this.name = name;
        this.cases = cases;
    }

    public void update(Vendor vendor) {
        this.name = vendor.name;
        this.address = vendor.address;
        this.phone = vendor.phone;
        this.caseIds = vendor.caseIds;
        this.lastUpdatedTime = vendor.lastUpdatedTime;
    }

    public void setCases(List<Case> cases) {
        List<String> caseIds = new ArrayList<>();
        for (Case aCase : cases) {
            caseIds.add(aCase.id);
        }

        this.caseIds = caseIds;
        this.cases = cases;
    }

    public void addCase(Case aCase) {
        if (!caseIds.contains(aCase.id)) {
            caseIds.add(aCase.id);
        }

        if (!cases.contains(aCase)) {
            cases.add(aCase);
        }
    }

    public List<Case> getCases() {
        return cases;
    }

    public void addAllCases(List<Case> cases) {
        List<String> caseIds = new ArrayList<>();
        for (Case aCase : cases) {
            caseIds.add(aCase.id);
        }

        caseIds.addAll(caseIds);
        cases.addAll(cases);
    }

    public void clearCases() {
        caseIds.clear();
        cases.clear();
    }

    public void removeCase(Case aCase) {
        caseIds.remove(aCase.id);
        cases.remove(aCase);
    }
}
