package com.bananaplan.workflowandroid.assigntask;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Spinner;

import com.bananaplan.workflowandroid.R;
import com.bananaplan.workflowandroid.assigntask.workers.WorkerGridViewAdapter;
import com.bananaplan.workflowandroid.data.TaskCase;
import com.bananaplan.workflowandroid.assigntask.tasks.TaskCaseCardDecoration;
import com.bananaplan.workflowandroid.assigntask.tasks.TaskCaseAdapter;
import com.bananaplan.workflowandroid.assigntask.tasks.TaskCaseOnTouchListener;
import com.bananaplan.workflowandroid.utility.GridSpanSizeLookup;
import com.bananaplan.workflowandroid.data.Factory;
import com.bananaplan.workflowandroid.assigntask.workers.WorkerFragment;
import com.bananaplan.workflowandroid.utility.data.IconSpinnerAdapter;
import com.bananaplan.workflowandroid.data.Worker;
import com.bananaplan.workflowandroid.data.WorkingData;

import java.util.ArrayList;
import java.util.List;


/**
 * Fragment to assign tasks to workers
 *
 * @author Danny Lin
 * @since 2015.05.30
 */
public class AssignTaskFragment extends Fragment implements
        ViewPager.OnPageChangeListener, TaskCaseAdapter.OnSelectTaskCaseListener,
        WorkerGridViewAdapter.OnRefreshTaskCaseListener {

    private static final String TAG = "AssignTaskFragment";
    private static final String KEY_FACTORY_SPINNER_POSITION = "key_factory_spinner_position";

    private Activity mActivity;
    private View mFragmentView;
    private FragmentManager mFragmentManager;

    private Spinner mFactorySpinner;
    private IconSpinnerAdapter mFactorySpinnerAdapter;
    private ArrayList<String> mFactorySpinnerDatas = new ArrayList<String>();

    private List<WorkerFragment> mWorkerPageList;
    private ViewPager mWorkerPager;
    private WorkerPagerAdapter mWorkerPagerAdapter;
    private int mMaxWorkerCountInPage;
    private int mPreviousPagerIndex = 0;
    private ViewGroup mWorkerPagerIndicatorContainer;

    private RecyclerView mTaskCaseView;
    private GridLayoutManager mGridLayoutManager;
    private TaskCaseAdapter mTaskCaseAdapter;
    private TaskCaseOnTouchListener mTaskCaseOnTouchListener;
    private ArrayList<String> mTaskCaseSpinnerDatas = new ArrayList<String>();

    private WorkingData mWorkingData;

    private boolean mIsFactorySpinnerFirstCalled = true;


    private class FactorySpinnerAdapter extends IconSpinnerAdapter<String> {
        public FactorySpinnerAdapter(Context context, int resource, ArrayList<String> datas) {
            super(context, resource, datas);
        }

        @Override
        public String getSpinnerViewDisplayString(int position) {
            return (String) getItem(position);
        }

        @Override
        public int getSpinnerIconResourceId() {
            return R.drawable.case_spinner_icon;
        }

        @Override
        public String getDropdownSpinnerViewDisplayString(int position) {
            return (String) getItem(position);
        }

        @Override
        public boolean isDropdownSelectedIconVisible(int position) {
            return mFactorySpinner.getSelectedItemPosition() == position;
        }
    }

    private class WorkerPagerAdapter extends FragmentStatePagerAdapter {

        private List<WorkerFragment> mWorkerPageData = new ArrayList<WorkerFragment>();

        public WorkerPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        public void setWorkerPages(List<WorkerFragment> workerPages) {
            mWorkerPageData = workerPages;
        }

        @Override
        public Fragment getItem(int position) {
            return mWorkerPageData.get(position);
        }

        @Override
        public int getCount() {
            return mWorkerPageData.size();
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mActivity = activity;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_assign_task, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initialize(savedInstanceState);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(KEY_FACTORY_SPINNER_POSITION, mFactorySpinner.getSelectedItemPosition());
    }

    private void initialize(Bundle savedInstanceState) {
        mFragmentView = getView();
        mFragmentManager = getFragmentManager();
        mMaxWorkerCountInPage = WorkerFragment.MAX_WORKER_COUNT_IN_PAGE; // Need to get count according to the device size.
        mWorkingData = WorkingData.getInstance(mActivity);

        // TODO: When DB is created, this part needs to be done after the loader has already loaded data.
        getFactorySpinnerTitles();
        getTaskCaseSpinnerTitles();

        findViews();
        initTaskCaseView();
        initFactorySpinner();
        initWorkerPager(savedInstanceState);
    }

    private void getFactorySpinnerTitles() {
        for (Factory factory : mWorkingData.getFactories()) {
            mFactorySpinnerDatas.add(factory.name);
        }
    }

    private void getTaskCaseSpinnerTitles() {
        for (TaskCase taskCase : mWorkingData.getTaskCases()) {
            mTaskCaseSpinnerDatas.add(taskCase.name);
        }
    }

    private void findViews() {
        mFactorySpinner = (Spinner) mFragmentView.findViewById(R.id.factory_spinner);
        mWorkerPager = (ViewPager) mFragmentView.findViewById(R.id.worker_pager);
        mWorkerPagerIndicatorContainer = (ViewGroup) mFragmentView.findViewById(R.id.worker_pager_indicator_container);
        mTaskCaseView = (RecyclerView) mFragmentView.findViewById(R.id.task_case_view);
    }

    private void initTaskCaseView() {
        mTaskCaseAdapter = new TaskCaseAdapter(mActivity);
        mTaskCaseAdapter.initTaskCaseDatas(mTaskCaseSpinnerDatas, mWorkingData.getTaskCases().get(0));
        mTaskCaseAdapter.setOnSelectTaskCaseListener(this);

        mTaskCaseOnTouchListener = new TaskCaseOnTouchListener(mTaskCaseView);

        mGridLayoutManager =
                new GridLayoutManager(mActivity, mActivity.getResources().getInteger(R.integer.task_case_column_count));
        mGridLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mGridLayoutManager.setSpanSizeLookup(new GridSpanSizeLookup(mGridLayoutManager));

        mTaskCaseView.setLayoutManager(mGridLayoutManager);
        mTaskCaseView.addItemDecoration(new TaskCaseCardDecoration(mActivity));
        mTaskCaseView.setOnTouchListener(mTaskCaseOnTouchListener);
        mTaskCaseView.setAdapter(mTaskCaseAdapter);
    }

    // TODO: Need to handle rotation
    private void initFactorySpinner() {
        mFactorySpinnerAdapter = new FactorySpinnerAdapter(mActivity, R.layout.factory_spinner_item, mFactorySpinnerDatas);
        mFactorySpinnerAdapter.setDropDownViewResource(android.R.layout.simple_list_item_1);
        mFactorySpinner.setAdapter(mFactorySpinnerAdapter);
        mFactorySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (mIsFactorySpinnerFirstCalled) {
                    mIsFactorySpinnerFirstCalled = false;
                    return;
                }
                clearWorkers();
                createWorkerPages(mWorkingData.getFactories().get(position).workers);
                initWorkerPagerIndicator();
                mWorkerPagerAdapter.setWorkerPages(mWorkerPageList);
                mWorkerPager.setAdapter(mWorkerPagerAdapter);
                mWorkerPager.setCurrentItem(0);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    // TODO: Need to handle rotation
    private void initWorkerPager(Bundle savedInstanceState) {
        mWorkerPagerAdapter = new WorkerPagerAdapter(mFragmentManager);
        createWorkerPages(mWorkingData.getFactories().get(savedInstanceState == null ?
                0 : savedInstanceState.getInt(KEY_FACTORY_SPINNER_POSITION, 0)).workers);
        initWorkerPagerIndicator();
        mWorkerPagerAdapter.setWorkerPages(mWorkerPageList);
        mWorkerPager.setAdapter(mWorkerPagerAdapter);
        mWorkerPager.setOnPageChangeListener(this);
    }

    private void createWorkerPages(List<Worker> workerDatas) {
        int workerCount = workerDatas.size();
        if (workerCount <= 0) return;

        int fragmentIndex = 0;
        if (mWorkerPageList == null) {
            mWorkerPageList = new ArrayList<WorkerFragment>();
        }
        addWorkerPage();
        for (int i = 0 ; i < workerCount ; i++) {
            if (mMaxWorkerCountInPage == mWorkerPageList.get(fragmentIndex).getWorkerDatas().size()) {
                fragmentIndex++;
                addWorkerPage();
            }
            mWorkerPageList.get(fragmentIndex).getWorkerDatas().add(workerDatas.get(i));  // After DB is created, add worker information
        }
    }

    private void addWorkerPage() {
        WorkerFragment workerFragment = new WorkerFragment();
        workerFragment.setOnRefreshTaskCaseListener(this);
        mWorkerPageList.add(workerFragment);

        View indicator = LayoutInflater.from(mActivity).inflate(
                R.layout.worker_pager_indicator, mWorkerPagerIndicatorContainer, false);
        mWorkerPagerIndicatorContainer.addView(indicator);
    }

    private void initWorkerPagerIndicator() {
        mWorkerPagerIndicatorContainer.getChildAt(0).setSelected(true);
        mPreviousPagerIndex = 0;
    }

    private void clearWorkers() {
        mWorkerPageList = null;
        mWorkerPager.removeAllViews();
        mWorkerPagerIndicatorContainer.removeAllViews();
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        mWorkerPagerIndicatorContainer.getChildAt(mPreviousPagerIndex).setSelected(false);
        mWorkerPagerIndicatorContainer.getChildAt(position).setSelected(true);
        mPreviousPagerIndex = position;
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    @Override
    public void onSelectTaskCase(int position) {
        mTaskCaseAdapter.swapTaskCase(mWorkingData.getTaskCases().get(position));
        mTaskCaseAdapter.notifyDataSetChanged();
    }

    @Override
    public void onRefreshTaskCase() {
        mTaskCaseAdapter.notifyDataSetChanged();
    }
}
