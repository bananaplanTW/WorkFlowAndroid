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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import com.bananaplan.workflowandroid.R;
import com.bananaplan.workflowandroid.assigntask.tasks.TaskCase;
import com.bananaplan.workflowandroid.assigntask.tasks.TaskItem;
import com.bananaplan.workflowandroid.assigntask.tasks.TaskListAdapter;
import com.bananaplan.workflowandroid.assigntask.workers.WorkerFragment;

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

    private TextView mPersonInCharge;
    private TextView mUncompletedTaskTime;
    private TextView mUndergoingTaskTime;
    private TextView mUndergoingWorkerCount;

    private String[] mFactoryDatas = {"武林廠", "豐原廠", "桃園廠"};
    private String[] mCaseDatas = {"案件ㄧ", "案件二", "案件三"};
    private List<TaskCase> mTaskCases = new ArrayList<TaskCase>();


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

        // When DB is created, this part needs to be done after the loader has already loaded data.
        createWorkerFragments(10);
        createTaskCaseData();

        initTaskList();
        initFactorySpinner();
        initCaseSpinner();
        initWorkerViewPager();
    }

    private void findViews() {
        mFactorySpinner = (Spinner) mFragmentView.findViewById(R.id.factory_spinner);
        mCaseSpinner = (Spinner) mFragmentView.findViewById(R.id.task_spinner);
        mWorkerViewPager = (ViewPager) mFragmentView.findViewById(R.id.worker_viewpager);
        mTaskList = (RecyclerView) mFragmentView.findViewById(R.id.task_list_view);
        mPersonInCharge = (TextView) mFragmentView.findViewById(R.id.person_in_charge);
        mUncompletedTaskTime = (TextView) mFragmentView.findViewById(R.id.uncompleted_task_time);
        mUndergoingTaskTime = (TextView) mFragmentView.findViewById(R.id.undergoing_task_time);
        mUndergoingWorkerCount = (TextView) mFragmentView.findViewById(R.id.undergoing_worker_count);
    }

    private void initFactorySpinner() {
        mFactorySpinnerAdapter = new ArrayAdapter(mActivity, R.layout.factory_spinner_item, mFactoryDatas);
        mFactorySpinnerAdapter.setDropDownViewResource(android.R.layout.simple_list_item_1);
        mFactorySpinner.setAdapter(mFactorySpinnerAdapter);
    }

    private void initCaseSpinner() {
        mTaskSpinnerAdapter = new ArrayAdapter(mActivity, R.layout.task_spinner_item, mCaseDatas);
        mTaskSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_list_item_1);
        mCaseSpinner.setAdapter(mTaskSpinnerAdapter);
        mCaseSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                mPersonInCharge.setText("負責人：" + mTaskCases.get(position).personInCharge);
                mUncompletedTaskTime.setText(mTaskCases.get(position).uncompletedTaskTime);
                mUndergoingTaskTime.setText(mTaskCases.get(position).undergoingTaskTime);
                mUndergoingWorkerCount.setText(String.valueOf(mTaskCases.get(position).undergoingWorkerCount));
                mTaskListAdapter.setTaskDatas(mTaskCases.get(position).taskDatas);
                mTaskListAdapter.notifyDataSetChanged();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private void initWorkerViewPager() {
        mWorkerViewPagerAdapter = new WorkerViewPagerAdapter(mFragmentManager, mWorkerFragmentList.size());
        mWorkerViewPager.setAdapter(mWorkerViewPagerAdapter);
        mWorkerViewPager.setOnPageChangeListener(this);
    }

    private void createTaskCaseData() {
        List<TaskItem> case1 = new ArrayList<TaskItem>();
        List<TaskItem> case2 = new ArrayList<TaskItem>();
        List<TaskItem> case3 = new ArrayList<TaskItem>();

        case1.add(new TaskItem("外面鑽孔", "鑽孔機械A", TaskItem.Status.COMPLETED, "11:00:00"));
        case1.add(new TaskItem("外面鑽孔", "鑽孔機械A", TaskItem.Status.OVERTIME, "11:00:00"));
        case1.add(new TaskItem("外面鑽孔", "鑽孔機械A", TaskItem.Status.UNDERGOING, "11:00:00"));
        case1.add(new TaskItem("外面鑽孔", "鑽孔機械A", TaskItem.Status.UNDERGOING, "11:00:00"));
        case1.add(new TaskItem("外面鑽孔", "鑽孔機械A", TaskItem.Status.UNDERGOING, "11:00:00"));
        case1.add(new TaskItem("外面鑽孔", "鑽孔機械A", TaskItem.Status.OVERTIME, "11:00:00"));
        case1.add(new TaskItem("外面鑽孔", "鑽孔機械A", TaskItem.Status.COMPLETED, "11:00:00"));
        case1.add(new TaskItem("外面鑽孔", "鑽孔機械A", TaskItem.Status.OVERTIME, "11:00:00"));
        case1.add(new TaskItem("外面鑽孔", "鑽孔機械A", TaskItem.Status.COMPLETED, "11:00:00"));

        case2.add(new TaskItem("Hand", "鑽孔機械A", TaskItem.Status.COMPLETED, "9:13:00"));
        case2.add(new TaskItem("Head", "鑽孔機械A", TaskItem.Status.OVERTIME, "2:13:00"));
        case2.add(new TaskItem("Body", "鑽孔機械A", TaskItem.Status.UNDERGOING, "9:18:00"));
        case2.add(new TaskItem("Leg", "鑽孔機械A", TaskItem.Status.COMPLETED, "5:13:00"));

        case3.add(new TaskItem("X", "Z", TaskItem.Status.UNDERGOING, "00:00:00"));

        mTaskCases.add(new TaskCase("Tony", "8:00:12", "35:04:55", 3, case1));
        mTaskCases.add(new TaskCase("Thor", "6:11:10", "5:04:55", 6, case2));
        mTaskCases.add(new TaskCase("BBB", "4:32:11", "00:04:55", 5, case3));
    }

    private void initTaskList() {
        mTaskListAdapter = new TaskListAdapter(mActivity);
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
