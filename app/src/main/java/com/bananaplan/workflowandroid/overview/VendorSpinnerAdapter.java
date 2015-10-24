package com.bananaplan.workflowandroid.overview;

import android.content.Context;

import com.bananaplan.workflowandroid.R;
import com.bananaplan.workflowandroid.data.Vendor;
import com.bananaplan.workflowandroid.utility.data.IconSpinnerAdapter;

import java.util.ArrayList;

/**
 * Created by Ben on 2015/10/24.
 */
public class VendorSpinnerAdapter extends IconSpinnerAdapter<Vendor> {

    public VendorSpinnerAdapter(Context context, ArrayList<Vendor> objects,
                                IconSpinnerAdapter.OnItemSelectedCallback callback) {
        super(context, 0, objects, callback);
    }

    @Override
    public Vendor getItem(int position) {
        return (Vendor) super.getItem(position);
    }

    @Override
    public String getSpinnerViewDisplayString(int position) {
        return getItem(position).name;
    }

    @Override
    public int getSpinnerIconResourceId() {
        return R.drawable.ic_work_black;
    }

    @Override
    public String getDropdownSpinnerViewDisplayString(int position) {
        return getItem(position).name;
    }
}
