package com.bananaplan.workflowandroid.assigntask;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
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
import com.bananaplan.workflowandroid.assigntask.workers.Factory;
import com.bananaplan.workflowandroid.assigntask.workers.WorkerFragment;
import com.bananaplan.workflowandroid.assigntask.workers.WorkerItem;
import com.bananaplan.workflowandroid.assigntask.workers.WorkerItem.WorkingStatus;

import java.util.ArrayList;
import java.util.List;


/**
 * Fragment to assign tasks to workers
 *
 * @author Danny Lin
 * @since 2015.05.30
 */
public class AssignTaskFragment extends Fragment implements ViewPager.OnPageChangeListener {

    private static final String TAG = "AssignTaskFragment";
    private static final String KEY_FACTORY_SPINNER_POSITION = "key_factory_spinner_position";

    private Activity mActivity;
    private View mFragmentView;
    private FragmentManager mFragmentManager;

    private Spinner mFactorySpinner;
    private Spinner mCaseSpinner;
    private ArrayAdapter mFactorySpinnerAdapter;
    private ArrayAdapter mTaskSpinnerAdapter;

    private List<WorkerFragment> mWorkerPageList;
    private ViewPager mWorkerPager;
    private WorkerPagerAdapter mWorkerPagerAdapter;
    private int mMaxWorkerCountInPage;
    private int mPreviousPagerIndex = 0;

    private ViewGroup mWorkerPagerIndicatorContainer;

    private RecyclerView mTaskList;
    private LinearLayoutManager mLinearLayoutManager;
    private TaskListAdapter mTaskListAdapter;

    private TextView mPersonInCharge;
    private TextView mUncompletedTaskTime;
    private TextView mUndergoingTaskTime;
    private TextView mUndergoingWorkerCount;

    private String[] mFactorySpinnerDatas = {"武林廠", "豐原廠", "桃園廠"};
    private String[] mCaseSpinnerDatas = {"案件ㄧ", "案件二", "案件三"};

    private List<TaskCase> mTaskCaseDatas = new ArrayList<TaskCase>();
    private List<Factory> mFactoryDatas = new ArrayList<Factory>();

    private boolean mIsFactorySpinnerFirstCalled = true;


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
        findViews();

        // When DB is created, this part needs to be done after the loader has already loaded data.
        createTaskCaseDatas();
        createWorkerDatas();

        initTaskList();
        initFactorySpinner();
        initCaseSpinner();
        initWorkerPager(savedInstanceState);
    }

    private void findViews() {
        mFactorySpinner = (Spinner) mFragmentView.findViewById(R.id.factory_spinner);
        mCaseSpinner = (Spinner) mFragmentView.findViewById(R.id.task_spinner);
        mWorkerPager = (ViewPager) mFragmentView.findViewById(R.id.worker_pager);
        mWorkerPagerIndicatorContainer = (ViewGroup) mFragmentView.findViewById(R.id.worker_pager_indicator_container);
        mTaskList = (RecyclerView) mFragmentView.findViewById(R.id.task_list_view);
        mPersonInCharge = (TextView) mFragmentView.findViewById(R.id.person_in_charge);
        mUncompletedTaskTime = (TextView) mFragmentView.findViewById(R.id.uncompleted_task_time);
        mUndergoingTaskTime = (TextView) mFragmentView.findViewById(R.id.undergoing_task_time);
        mUndergoingWorkerCount = (TextView) mFragmentView.findViewById(R.id.undergoing_worker_count);
    }

    // TODO: Need to handle rotation
    private void initFactorySpinner() {
        Log.d(TAG, "Init factory spinner");
        mFactorySpinnerAdapter = new ArrayAdapter(mActivity, R.layout.factory_spinner_item, mFactorySpinnerDatas);
        mFactorySpinnerAdapter.setDropDownViewResource(android.R.layout.simple_list_item_1);
        mFactorySpinner.setAdapter(mFactorySpinnerAdapter);
        mFactorySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (mIsFactorySpinnerFirstCalled) {
                    Log.d(TAG, "Factory Spinner First Called");
                    mIsFactorySpinnerFirstCalled = false;
                    return;
                }
                Log.d(TAG, "Factory spinner select " + position);
                clearWorkers();
                switch(position) {
                    case 0:
                        createWorkerPages(mFactoryDatas.get(0).workerDatas);
                        break;
                    case 1:
                        createWorkerPages(mFactoryDatas.get(1).workerDatas);
                        break;
                    case 2:
                        createWorkerPages(mFactoryDatas.get(2).workerDatas);
                        break;
                }
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

    private void initCaseSpinner() {
        mTaskSpinnerAdapter = new ArrayAdapter(mActivity, R.layout.task_spinner_item, mCaseSpinnerDatas);
        mTaskSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_list_item_1);
        mCaseSpinner.setAdapter(mTaskSpinnerAdapter);
        mCaseSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                mPersonInCharge.setText("負責人：" + mTaskCaseDatas.get(position).personInCharge);
                mUncompletedTaskTime.setText(mTaskCaseDatas.get(position).uncompletedTaskTime);
                mUndergoingTaskTime.setText(mTaskCaseDatas.get(position).undergoingTaskTime);
                mUndergoingWorkerCount.setText(String.valueOf(mTaskCaseDatas.get(position).undergoingWorkerCount));
                mTaskListAdapter.setTaskDatas(mTaskCaseDatas.get(position).taskDatas);
                mTaskListAdapter.notifyDataSetChanged();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    // TODO: Need to handle rotation
    private void initWorkerPager(Bundle savedInstanceState) {
        Log.d(TAG, "initWorkerPager");
        mWorkerPagerAdapter = new WorkerPagerAdapter(mFragmentManager);
        createWorkerPages(mFactoryDatas.get(savedInstanceState == null ?
                0 : savedInstanceState.getInt(KEY_FACTORY_SPINNER_POSITION, 0)).workerDatas);
        initWorkerPagerIndicator();
        mWorkerPagerAdapter.setWorkerPages(mWorkerPageList);
        mWorkerPager.setAdapter(mWorkerPagerAdapter);
        mWorkerPager.setOnPageChangeListener(this);
    }

    private void createTaskCaseDatas() {
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

        mTaskCaseDatas.add(new TaskCase("Tony", "8:00:12", "35:04:55", 3, case1));
        mTaskCaseDatas.add(new TaskCase("Thor", "6:11:10", "5:04:55", 6, case2));
        mTaskCaseDatas.add(new TaskCase("BBB", "4:32:11", "00:04:55", 5, case3));
    }

    private void createWorkerDatas() {
        List<WorkerItem> workerDatas1 = new ArrayList<WorkerItem>();
        List<WorkerItem> workerDatas2 = new ArrayList<WorkerItem>();
        List<WorkerItem> workerDatas3 = new ArrayList<WorkerItem>();

        workerDatas1.add(new WorkerItem("王1", "工頭", "外面鑽孔 孔3", WorkingStatus.NORMAL, "6:33:11"));
        workerDatas1.add(new WorkerItem("王1", "工頭", "外面鑽孔 孔3", WorkingStatus.NORMAL, "6:33:11"));
        workerDatas1.add(new WorkerItem("王1", "工頭", "外面鑽孔 孔3", WorkingStatus.NORMAL, "6:33:11"));
        workerDatas1.add(new WorkerItem("王1", "工頭", "外面鑽孔 孔3", WorkingStatus.NORMAL, "6:33:11"));
        workerDatas1.add(new WorkerItem("王1", "工頭", "外面鑽孔 孔3", WorkingStatus.NORMAL, "1:33:11"));
        workerDatas1.add(new WorkerItem("王1", "工頭", "外面鑽孔 孔3", WorkingStatus.NORMAL, "1:33:11"));
        workerDatas1.add(new WorkerItem("王1", "工頭", "外面鑽孔 孔3", WorkingStatus.NORMAL, "6:33:11"));
        workerDatas1.add(new WorkerItem("王1", "工頭", "外面鑽孔 孔3", WorkingStatus.NORMAL, "1:33:11"));
        workerDatas1.add(new WorkerItem("王1", "工頭", "外面鑽孔 孔3", WorkingStatus.NORMAL, "1:33:11"));
        workerDatas1.add(new WorkerItem("王1", "工頭", "外面鑽孔 孔3", WorkingStatus.NORMAL, "1:33:11"));
        workerDatas1.add(new WorkerItem("王1", "工頭", "外面鑽孔 孔3", WorkingStatus.NORMAL, "6:33:11"));
        workerDatas1.add(new WorkerItem("王1", "工頭", "外面鑽孔 孔3", WorkingStatus.NORMAL, "6:33:11"));
        workerDatas1.add(new WorkerItem("王1", "工頭", "外面鑽孔 孔3", WorkingStatus.NORMAL, "6:33:11"));
        workerDatas1.add(new WorkerItem("王1", "工頭", "外面鑽孔 孔3", WorkingStatus.NORMAL, "1:33:11"));
        workerDatas1.add(new WorkerItem("王1", "工頭", "外面鑽孔 孔3", WorkingStatus.NORMAL, "1:33:11"));
        workerDatas1.add(new WorkerItem("王1", "工頭", "外面鑽孔 孔3", WorkingStatus.NORMAL, "1:33:11"));
        workerDatas1.add(new WorkerItem("王1", "工頭", "外面鑽孔 孔3", WorkingStatus.NORMAL, "1:33:11"));
        workerDatas1.add(new WorkerItem("王1", "工頭", "外面鑽孔 孔3", WorkingStatus.NORMAL, "1:33:11"));
        workerDatas1.add(new WorkerItem("王1", "工頭", "外面鑽孔 孔3", WorkingStatus.NORMAL, "1:33:11"));
        workerDatas1.add(new WorkerItem("王1", "工頭", "外面鑽孔 孔3", WorkingStatus.NORMAL, "6:33:11"));

        workerDatas2.add(new WorkerItem("陳1", "工頭", "外面鑽孔 孔3", WorkingStatus.NORMAL, "6:33:11"));
        workerDatas2.add(new WorkerItem("陳1", "工頭", "外面鑽孔 孔3", WorkingStatus.NORMAL, "1:33:11"));
        workerDatas2.add(new WorkerItem("陳1", "工頭", "外面鑽孔 孔3", WorkingStatus.NORMAL, "6:33:11"));
        workerDatas2.add(new WorkerItem("陳1", "工頭", "外面鑽孔 孔3", WorkingStatus.NORMAL, "6:33:11"));
        workerDatas2.add(new WorkerItem("陳1", "工頭", "外面鑽孔 孔3", WorkingStatus.NORMAL, "1:33:11"));

        workerDatas3.add(new WorkerItem("黃1", "工頭", "外面鑽孔 孔3", WorkingStatus.NORMAL, "1:33:11"));
        workerDatas3.add(new WorkerItem("黃1", "工頭", "外面鑽孔 孔3", WorkingStatus.NORMAL, "1:33:11"));
        workerDatas3.add(new WorkerItem("黃1", "工頭", "外面鑽孔 孔3", WorkingStatus.NORMAL, "1:33:11"));
        workerDatas3.add(new WorkerItem("黃1", "工頭", "外面鑽孔 孔3", WorkingStatus.NORMAL, "1:33:11"));
        workerDatas3.add(new WorkerItem("黃1", "工頭", "外面鑽孔 孔3", WorkingStatus.NORMAL, "1:33:11"));
        workerDatas3.add(new WorkerItem("黃1", "工頭", "外面鑽孔 孔3", WorkingStatus.NORMAL, "1:33:11"));
        workerDatas3.add(new WorkerItem("黃1", "工頭", "外面鑽孔 孔3", WorkingStatus.NORMAL, "6:33:11"));
        workerDatas3.add(new WorkerItem("黃1", "工頭", "外面鑽孔 孔3", WorkingStatus.NORMAL, "1:33:11"));
        workerDatas3.add(new WorkerItem("黃1", "工頭", "外面鑽孔 孔3", WorkingStatus.NORMAL, "1:33:11"));
        workerDatas3.add(new WorkerItem("黃1", "工頭", "外面鑽孔 孔3", WorkingStatus.NORMAL, "1:33:11"));

        mFactoryDatas.add(new Factory(workerDatas1));
        mFactoryDatas.add(new Factory(workerDatas2));
        mFactoryDatas.add(new Factory(workerDatas3));
    }

    private void initTaskList() {
        mTaskListAdapter = new TaskListAdapter(mActivity);
        mLinearLayoutManager = new LinearLayoutManager(mActivity);
        mLinearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        mTaskList.setLayoutManager(mLinearLayoutManager);
        mTaskList.setAdapter(mTaskListAdapter);
    }

    private void createWorkerPages(List<WorkerItem> workerDatas) {
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
        mWorkerPageList.add(new WorkerFragment());

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
}
