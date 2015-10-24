package com.bananaplan.workflowandroid.overview;

import android.app.Activity;
import android.content.Context;
import android.text.TextUtils;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.Filterable;

import com.bananaplan.workflowandroid.data.Case;
import com.bananaplan.workflowandroid.data.Vendor;
import com.bananaplan.workflowandroid.data.WorkingData;
import com.bananaplan.workflowandroid.utility.Utils;

import java.util.ArrayList;

/**
 * Created by Ben on 2015/10/24.
 */
public abstract class CaseAdapter extends ArrayAdapter<Case> implements Filterable {
    private Activity mActivity;
    protected ArrayList<Case> mOrigCases;
    protected ArrayList<Case> mFilteredCases;
    protected CustomFilter mFilter;
    protected int mPositionSelected;

    public CaseAdapter(Context context, ArrayList<Case> cases) {
        super(context, 0, cases);
        if (context instanceof Activity) {
            mActivity = (Activity) context;
        }
        mOrigCases = cases;
        mFilteredCases = new ArrayList<>(cases);
        mFilter = new CustomFilter();
    }

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

    private class CustomFilter extends Filter {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            constraint = constraint.toString().toLowerCase();
            FilterResults result = new FilterResults();
            ArrayList<Case> filterResult = new ArrayList<>();
            Vendor selectedVendor = getSelectedVendor();
            if (selectedVendor != null) {
                for (Case aCase : mOrigCases) {
                    boolean matchText = TextUtils.isEmpty(constraint) ?
                            true :
                            (aCase.name.toLowerCase().contains(constraint) ||
                                    WorkingData.getInstance(mActivity).getVendorById(aCase.vendorId).name.toLowerCase().contains(constraint));
                    boolean matchVendor = TextUtils.isEmpty(selectedVendor.id) ?
                            true :
                            Utils.isSameId(aCase.vendorId, selectedVendor.id);
                    if (matchText && matchVendor) {
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
            mFilteredCases.clear();
            mFilteredCases.addAll((ArrayList<Case>) results.values);
            notifyDataSetChanged();
        }
    }

    public abstract Vendor getSelectedVendor();
}
