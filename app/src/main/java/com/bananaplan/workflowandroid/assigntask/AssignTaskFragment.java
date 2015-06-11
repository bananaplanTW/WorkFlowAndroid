package com.bananaplan.workflowandroid.assigntask;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.bananaplan.workflowandroid.R;

import java.util.ArrayList;
import java.util.List;


/**
 * Fragment to assign tasks to workers
 *
 * @author Danny Lin
 * @since 2015.05.30
 */
public class AssignTaskFragment extends Fragment implements ViewPager.OnPageChangeListener {

    private static final String TAG_WORKER = "tag_worker";
    private static final int MAX_WORKER_COUNT_IN_PAGE = 9;

    private Activity mActivity;
    private View mFragmentView;
    private FragmentManager mFragmentManager;

    private Spinner mFactorySpinner;
    private Spinner mCaseSpinner;
    private ArrayAdapter mFactorySpinnerAdapter;
    private ArrayAdapter mTaskSpinnerAdapter;

    private ArrayList<WorkerFragment> mWorkerFragmentList;
    private ViewPager mWorkerViewPager;
    private WorkerViewPagerAdapter mWorkerViewPagerAdapter;
    private int mMaxWorkerCountInPage;

    private RecyclerView mTaskList;
    private LinearLayoutManager mLinearLayoutManager;
    private TaskListAdapter mTaskListAdapter;

    private String[] mFactoryDatas = {"Factory 1", "Factory 2", "Factory 3"};
    private String[] mCaseDatas = {"Case 1", "Case 2", "Case 3"};
    private List<TaskItem> mTaskDatas = new ArrayList<TaskItem>();


    private class WorkerViewPagerAdapter extends PagerAdapter {

        private FragmentManager mFragmentManager;
        private FragmentTransaction mCurTransaction = null;
        private int mAdapterSize = 0;


        public WorkerViewPagerAdapter(FragmentManager fm, int size) {
            mFragmentManager = fm;
            mAdapterSize = size;
        }

        public void setAdapterSize(int size) {
            mAdapterSize = size;
        }

        /**
         * Show fragment Fragments will be instantiated near the current
         * fragment. Ex: Current = Fragment2 => Fragment1 and Fragment3 will be
         * instantiated.
         */
        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            if (mCurTransaction == null) {
                mCurTransaction = mFragmentManager.beginTransaction();
            }

            Fragment f = getFragment(position);
            mCurTransaction.show(f);

            return f;
        }

        /**
         * We override this method to just hide the fragment instead of
         * destroying it.
         */
        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            if (mCurTransaction == null) {
                mCurTransaction = mFragmentManager.beginTransaction();
            }
            mCurTransaction.hide((Fragment) object);
        }

        @Override
        public void finishUpdate(ViewGroup container) {
            if (mCurTransaction != null) {
                mCurTransaction.commit();
                mCurTransaction = null;
            }
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return ((Fragment) object).getView() == view;
        }

        @Override
        public int getCount() {
            return mAdapterSize;
        }

        private Fragment getFragment(int position) {
            return mWorkerFragmentList.get(position);
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
        initialize();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        clearWorkers();
    }

    private void initialize() {
        mFragmentView = getView();
        mFragmentManager = getFragmentManager();
        mMaxWorkerCountInPage = MAX_WORKER_COUNT_IN_PAGE; // Need to get count according to the device size.
        findViews();
        initFactorySpinner();
        initTaskSpinner();
        createWorkerFragments(10); // When DB is created, this part needs to be done after the loader has already loaded data.
        initWorkerViewPager();
        initTaskListData();
        initTaskList();  // When DB is created, this part needs to be done after the loader has already loaded data.
    }

    private void findViews() {
        mFactorySpinner = (Spinner) mFragmentView.findViewById(R.id.factory_spinner);
        mCaseSpinner = (Spinner) mFragmentView.findViewById(R.id.task_spinner);
        mWorkerViewPager = (ViewPager) mFragmentView.findViewById(R.id.worker_viewpager);
        mTaskList = (RecyclerView) mFragmentView.findViewById(R.id.task_list_view);
    }

    private void initFactorySpinner() {
        mFactorySpinnerAdapter = new ArrayAdapter(mActivity, R.layout.factory_spinner_item, mFactoryDatas);
        mFactorySpinnerAdapter.setDropDownViewResource(android.R.layout.simple_list_item_1);
        mFactorySpinner.setAdapter(mFactorySpinnerAdapter);
    }

    private void initTaskSpinner() {
        mTaskSpinnerAdapter = new ArrayAdapter(mActivity, R.layout.task_spinner_item, mCaseDatas);
        mTaskSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_list_item_1);
        mCaseSpinner.setAdapter(mTaskSpinnerAdapter);
    }

    private void initWorkerViewPager() {
        mWorkerViewPagerAdapter = new WorkerViewPagerAdapter(mFragmentManager, mWorkerFragmentList.size());
        mWorkerViewPager.setAdapter(mWorkerViewPagerAdapter);
        mWorkerViewPager.setOnPageChangeListener(this);
    }

    private void initTaskListData() {
        mTaskDatas.add(new TaskItem("外面鑽孔", "鑽孔機械A", TaskItem.Status.COMPLETED, "11:00:00"));
        mTaskDatas.add(new TaskItem("外面鑽孔", "鑽孔機械A", TaskItem.Status.OVERTIME, "11:00:00"));
        mTaskDatas.add(new TaskItem("外面鑽孔", "鑽孔機械A", TaskItem.Status.UNDERGOING, "11:00:00"));
        mTaskDatas.add(new TaskItem("外面鑽孔", "鑽孔機械A", TaskItem.Status.UNDERGOING, "11:00:00"));
        mTaskDatas.add(new TaskItem("外面鑽孔", "鑽孔機械A", TaskItem.Status.UNDERGOING, "11:00:00"));
        mTaskDatas.add(new TaskItem("外面鑽孔", "鑽孔機械A", TaskItem.Status.OVERTIME, "11:00:00"));
        mTaskDatas.add(new TaskItem("外面鑽孔", "鑽孔機械A", TaskItem.Status.COMPLETED, "11:00:00"));
        mTaskDatas.add(new TaskItem("外面鑽孔", "鑽孔機械A", TaskItem.Status.OVERTIME, "11:00:00"));
        mTaskDatas.add(new TaskItem("外面鑽孔", "鑽孔機械A", TaskItem.Status.COMPLETED, "11:00:00"));
    }

    private void initTaskList() {
        mTaskListAdapter = new TaskListAdapter(mActivity, mTaskDatas);
        mLinearLayoutManager = new LinearLayoutManager(mActivity);
        mLinearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        mTaskList.setLayoutManager(mLinearLayoutManager);
        mTaskList.setAdapter(mTaskListAdapter);
    }

    private void createWorkerFragments(int workerCount) {
        if (workerCount <= 0) return;

        int fragmentIndex = 0;
        if (mWorkerFragmentList == null) {
            mWorkerFragmentList = new ArrayList<WorkerFragment>();
        }
        addWorkerFragment(TAG_WORKER + fragmentIndex);
        for (int i = 0 ; i < workerCount ; i++) {
            if (MAX_WORKER_COUNT_IN_PAGE == mWorkerFragmentList.get(fragmentIndex).getWorkerDatas().size()) {  // Need to refactory
                fragmentIndex++;
                addWorkerFragment(TAG_WORKER + fragmentIndex);
            }
            mWorkerFragmentList.get(fragmentIndex).getWorkerDatas().add("Worker " + i);  // After DB is created, add worker information
        }
    }

    private void addWorkerFragment(String tag) {
        WorkerFragment workerFragment = (WorkerFragment) mFragmentManager.findFragmentByTag(tag);
        if (workerFragment == null) {
            workerFragment = new WorkerFragment();
            FragmentTransaction transaction = mFragmentManager.beginTransaction();
            transaction.add(R.id.worker_viewpager, workerFragment, tag);
            transaction.hide(workerFragment);
            transaction.commit();
        }
        mWorkerFragmentList.add(workerFragment);
    }

    private void clearWorkers() {
        mWorkerFragmentList = null;
        mWorkerViewPager.removeAllViews();
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {

    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }
}
