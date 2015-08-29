package com.bananaplan.workflowandroid.utility.view;

import android.content.Context;
import android.widget.Spinner;

import com.bananaplan.workflowandroid.R;
import com.bananaplan.workflowandroid.data.Factory;
import com.bananaplan.workflowandroid.utility.data.IconSpinnerAdapter;

import java.util.ArrayList;


/**
 * Created by Ben on 2015/9/3.
 */
public class FactorySpinnerAdapter extends IconSpinnerAdapter<Factory> {

    private Spinner mSpinner;

    public FactorySpinnerAdapter(Context context, ArrayList<Factory> objects, Spinner spinner) {
        super(context, 0, objects);
        mSpinner = spinner;
    }

    @Override
    public Factory getItem(int position) {
        return (Factory) super.getItem(position);
    }

    @Override
    public long getItemId(int position) {
        return getItem(position).id;
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
    public boolean isDropdownSelectedIconVisible(int position) {
        return position == mSpinner.getSelectedItemPosition();
    }

    @Override
    public int getSpinnerIconResourceId() {
        return R.drawable.ic_business_black;
    }
}
