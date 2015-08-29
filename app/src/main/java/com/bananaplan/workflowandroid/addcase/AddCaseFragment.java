package com.bananaplan.workflowandroid.addcase;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Spinner;

import com.bananaplan.workflowandroid.R;
import com.bananaplan.workflowandroid.utility.GridSpanSizeLookup;
import com.bananaplan.workflowandroid.utility.data.IconSpinnerAdapter;

import java.util.ArrayList;


/**
 * @author Danny Lin
 * @since 2015/8/22.
 */
public class AddCaseFragment extends Fragment {

    private Activity mMainActivity;
    private View mFragmentView;

    private Spinner mModuleSpinner;
    private ModuleSpinnerAdapter mModuleSpinnerAdapter;
    private ArrayList<String> mModuleDatas = new ArrayList<String>();

    private RecyclerView mAddTaskGridView;
    private GridLayoutManager mGridLayoutManager;
    private AddTaskAdapter mAddTaskAdapter;

    private int mAddTaskGridViewSpanCount = 0;


    private class ModuleSpinnerAdapter extends IconSpinnerAdapter<String> {

        private Context mContext;

        public ModuleSpinnerAdapter(Context context, int resource, ArrayList<String> datas) {
            super(context, resource, datas);
            mContext = context;
        }

        @Override
        public String getSpinnerViewDisplayString(int position) {
            return (String) getItem(position);
        }

        @Override
        public int getSpinnerIconResourceId() {
            return R.drawable.ic_work_black;
        }

        @Override
        public String getDropdownSpinnerViewDisplayString(int position) {
            return (String) getItem(position);
        }

        @Override
        public boolean isDropdownSelectedIconVisible(int position) {
            return false;
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mMainActivity = activity;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_add_case, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initialize();
    }

    private void initialize() {
        findViews();

        setupDatas();
        setupModuleSpinner();
        setupAddCaseGridView();
    }

    private void findViews() {
        mFragmentView = getView();
        mAddTaskGridViewSpanCount = mMainActivity.getResources().getInteger(R.integer.add_case_grid_view_span_count);

        mModuleSpinner = (Spinner) mFragmentView.findViewById(R.id.module_spinner);
        mAddTaskGridView = (RecyclerView) mFragmentView.findViewById(R.id.add_task_grid_view);
    }

    private void setupDatas() {
        mModuleDatas.add("Module 1");
        mModuleDatas.add("Module 2");
    }

    private void setupModuleSpinner() {
        mModuleSpinnerAdapter = new ModuleSpinnerAdapter(mMainActivity, R.layout.icon_spinner_item, mModuleDatas);
        mModuleSpinner.setAdapter(mModuleSpinnerAdapter);
    }

    private void setupAddCaseGridView() {
        mGridLayoutManager = new GridLayoutManager(mMainActivity, mAddTaskGridViewSpanCount);
        mGridLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mGridLayoutManager.setSpanSizeLookup(new GridSpanSizeLookup(mGridLayoutManager));

        mAddTaskAdapter = new AddTaskAdapter(mMainActivity, mAddTaskGridViewSpanCount);

        mAddTaskGridView.setLayoutManager(mGridLayoutManager);
        mAddTaskGridView.addItemDecoration(new AddTaskItemDecoration(mMainActivity, mAddTaskGridViewSpanCount));
        mAddTaskGridView.setAdapter(mAddTaskAdapter);
    }
}
