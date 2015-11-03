package com.bananaplan.workflowandroid.overview.caseoverview;

import android.app.Activity;
import android.content.Context;
import android.text.TextUtils;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.Filterable;

import com.bananaplan.workflowandroid.data.Case;
import com.bananaplan.workflowandroid.data.Vendor;
import com.bananaplan.workflowandroid.data.WorkingData;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by Ben on 2015/10/24.
 */
public abstract class CaseOverviewListAdapterBase extends ArrayAdapter<Case> implements Filterable {

    private Activity mActivity;

    protected List<Case> mOrigCases;
    protected List<Case> mFilteredCases;
    protected CustomFilter mFilter;

    protected int mPositionSelected;


    public CaseOverviewListAdapterBase(Context context, List<Case> cases) {
        super(context, 0, cases);
        if (context instanceof Activity) {
            mActivity = (Activity) context;
        }
        mOrigCases = cases;
        mFilteredCases = new ArrayList<>(cases);
        mFilter = new CustomFilter();
    }

    public abstract Vendor getSelectedVendor();

    @Override
    public Case getItem(int position) {
        return mFilteredCases.get(position);
    }

    @Override
    public int getCount() {
        return mFilteredCases.size();
    }

    @Override
    public Filter getFilter() {
        return mFilter;
    }

    public void setPositionSelected(int position) {
        mPositionSelected = position;
    }

    public int getPositionSelected() {
        return mPositionSelected;
    }

    private class CustomFilter extends Filter {

        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            constraint = constraint.toString().toLowerCase();
            FilterResults result = new FilterResults();
            List<Case> filterResult = new ArrayList<>();
            Vendor selectedVendor = getSelectedVendor();

            if (selectedVendor != null) {
                for (Case aCase : mOrigCases) {
                    boolean matchText = TextUtils.isEmpty(constraint) || (aCase.name.toLowerCase().contains(constraint) ||
                                    WorkingData.getInstance(mActivity).getVendorById(aCase.vendorId).name.toLowerCase().contains(constraint));

                    if (matchText) {
                        filterResult.add(aCase);
                    }
                }
            }

            result.values = filterResult;
            result.count = filterResult.size();

            return result;
        }

        @SuppressWarnings("unchecked")
        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            mFilteredCases = (ArrayList<Case>) results.values;
            notifyDataSetChanged();
        }
    }

    public void updateDataSet(List<Case> dataSet) {
        mOrigCases = dataSet;
        mFilteredCases = dataSet;
        notifyDataSetChanged();
    }
}
