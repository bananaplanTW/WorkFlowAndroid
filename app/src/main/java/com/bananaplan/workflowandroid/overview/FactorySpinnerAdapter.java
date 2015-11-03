package com.bananaplan.workflowandroid.overview;

import android.content.Context;

import com.bananaplan.workflowandroid.R;
import com.bananaplan.workflowandroid.data.Factory;
import com.bananaplan.workflowandroid.utility.data.IconSpinnerAdapter;

import java.util.List;


/**
 * Created by Ben on 2015/9/3.
 */
public class FactorySpinnerAdapter extends IconSpinnerAdapter<Factory> {

    public FactorySpinnerAdapter(Context context, List<Factory> objects) {
        super(context, 0, objects);
    }

    public FactorySpinnerAdapter(Context context, List<Factory> objects, OnItemSelectedCallback callback) {
        super(context, 0, objects, callback);
    }

    @Override
    public Factory getItem(int position) {
        return (Factory) super.getItem(position);
    }

    @Override
    public String getDropdownSpinnerViewDisplayString(int position) {
        return getItem(position).name;
    }

    @Override
    public String getSpinnerViewDisplayString(int position) {
        return getItem(position).name;
    }

    @Override
    public int getSpinnerIconResourceId() {
        return R.drawable.ic_business_black;
    }
}
