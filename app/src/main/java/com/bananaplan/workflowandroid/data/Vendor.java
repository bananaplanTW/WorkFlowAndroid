package com.bananaplan.workflowandroid.data;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by Ben on 2015/7/22.
 */
public class Vendor extends IdData {

    public String address;
    public String phone;

    public List<String> caseIds;
    public ArrayList<Case> cases;


    public Vendor(String id, String name, String address, String phone, List<String> caseIds) {
        this.id = id;
        this.name = name;
        this.address = address;
        this.phone = phone;
        this.caseIds = caseIds;
    }

    public Vendor(String id, String name) {
        this(id, name, new ArrayList<Case>());
    }

    public Vendor(String id, String name, ArrayList<Case> cases) {
        this.id = id;
        this.name = name;
        this.cases = cases;
    }
}
