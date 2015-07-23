package com.bananaplan.workflowandroid.assigntask;

import android.app.Activity;
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
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.bananaplan.workflowandroid.R;
import com.bananaplan.workflowandroid.assigntask.tasks.TaskCase;
import com.bananaplan.workflowandroid.assigntask.tasks.TaskItem;
import com.bananaplan.workflowandroid.assigntask.tasks.TaskCaseAdapter;
import com.bananaplan.workflowandroid.assigntask.tasks.TaskCaseOnTouchListener;
import com.bananaplan.workflowandroid.assigntask.tasks.TaskCaseSpanSizeLookup;
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
public class AssignTaskFragment extends Fragment implements
        ViewPager.OnPageChangeListener, TaskCaseAdapter.OnSelectTaskCaseListener {

    private static final String TAG = "AssignTaskFragment";
    private static final String KEY_FACTORY_SPINNER_POSITION = "key_factory_spinner_position";

    private Activity mActivity;
    private View mFragmentView;
    private FragmentManager mFragmentManager;

    private Spinner mFactorySpinner;
    private ArrayAdapter mFactorySpinnerAdapter;

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

        // TODO: When DB is created, this part needs to be done after the loader has already loaded data.
        createTaskCaseDatas();
        createWorkerDatas();

        initTaskList();
        initFactorySpinner();
        initWorkerPager(savedInstanceState);
    }

    private void findViews() {
        mFactorySpinner = (Spinner) mFragmentView.findViewById(R.id.factory_spinner);
        mWorkerPager = (ViewPager) mFragmentView.findViewById(R.id.worker_pager);
        mWorkerPagerIndicatorContainer = (ViewGroup) mFragmentView.findViewById(R.id.worker_pager_indicator_container);
        mTaskCaseView = (RecyclerView) mFragmentView.findViewById(R.id.task_case_view);
    }

    private void initTaskList() {
        mTaskCaseAdapter = new TaskCaseAdapter(mActivity);
        mTaskCaseAdapter.initTaskCaseDatas(mCaseSpinnerDatas, mTaskCaseDatas.get(0));
        mTaskCaseAdapter.setOnSelectTaskCaseListener(this);

        mTaskCaseOnTouchListener = new TaskCaseOnTouchListener(mTaskCaseView);

        mGridLayoutManager =
                new GridLayoutManager(mActivity, mActivity.getResources().getInteger(R.integer.task_list_column_count));
        mGridLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mGridLayoutManager.setSpanSizeLookup(new TaskCaseSpanSizeLookup(mGridLayoutManager));

        mTaskCaseView.setLayoutManager(mGridLayoutManager);
        mTaskCaseView.setOnTouchListener(mTaskCaseOnTouchListener);
        mTaskCaseView.setAdapter(mTaskCaseAdapter);
    }

    // TODO: Need to handle rotation
    private void initFactorySpinner() {
        mFactorySpinnerAdapter = new ArrayAdapter(mActivity, R.layout.factory_spinner_item, mFactorySpinnerDatas);
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
                switch (position) {
                    case 0:
                        createWorkerPages(mFactoryDatas.get(0).workerItems);
                        break;
                    case 1:
                        createWorkerPages(mFactoryDatas.get(1).workerItems);
                        break;
                    case 2:
                        createWorkerPages(mFactoryDatas.get(2).workerItems);
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

    // TODO: Need to handle rotation
    private void initWorkerPager(Bundle savedInstanceState) {
        mWorkerPagerAdapter = new WorkerPagerAdapter(mFragmentManager);
        createWorkerPages(mFactoryDatas.get(savedInstanceState == null ?
                0 : savedInstanceState.getInt(KEY_FACTORY_SPINNER_POSITION, 0)).workerItems);
        initWorkerPagerIndicator();
        mWorkerPagerAdapter.setWorkerPages(mWorkerPageList);
        mWorkerPager.setAdapter(mWorkerPagerAdapter);
        mWorkerPager.setOnPageChangeListener(this);
    }

    private void createTaskCaseDatas() {
        List<TaskItem> case1 = new ArrayList<TaskItem>();
        List<TaskItem> case2 = new ArrayList<TaskItem>();
        List<TaskItem> case3 = new ArrayList<TaskItem>();

        case1.add(new TaskItem(1, "外面鑽孔"));
        case1.add(new TaskItem(2, "外面鑽孔"));
        case1.add(new TaskItem(3, "外面鑽孔"));
        case1.add(new TaskItem(4, "外面鑽孔"));
        case1.add(new TaskItem(5, "外面鑽孔"));
        case1.add(new TaskItem(6, "外面鑽孔"));
        case1.add(new TaskItem(7, "外面鑽孔"));
        case1.add(new TaskItem(8, "外面鑽孔"));
        case1.add(new TaskItem(9, "外面鑽孔"));
        case1.add(new TaskItem(10, "外面鑽孔"));

        case2.add(new TaskItem(11, "外面鑽孔"));
        case2.add(new TaskItem(12, "外面鑽孔"));
        case2.add(new TaskItem(13, "外面鑽孔"));
        case2.add(new TaskItem(14, "外面鑽孔"));

        case3.add(new TaskItem(15, "外面鑽孔"));

        mTaskCaseDatas.add(new TaskCase(1, "TaskCase1", case1));
        mTaskCaseDatas.add(new TaskCase(2, "TaskCase2", case2));
        mTaskCaseDatas.add(new TaskCase(3, "TaskCase3", case3));
    }

    private void createWorkerDatas() {
        ArrayList<WorkerItem> workerDatas1 = new ArrayList<WorkerItem>();
        ArrayList<WorkerItem> workerDatas2 = new ArrayList<WorkerItem>();
        ArrayList<WorkerItem> workerDatas3 = new ArrayList<WorkerItem>();

        workerDatas1.add(new WorkerItem(1, "王1", "工頭"));
        workerDatas1.add(new WorkerItem(2, "王1", "工頭"));
        workerDatas1.add(new WorkerItem(3, "王1", "工頭"));
        workerDatas1.add(new WorkerItem(4, "王1", "工頭"));
        workerDatas1.add(new WorkerItem(5, "王1", "工頭"));
        workerDatas1.add(new WorkerItem(6, "王1", "工頭"));
        workerDatas1.add(new WorkerItem(7, "王1", "工頭"));
        workerDatas1.add(new WorkerItem(8, "王1", "工頭"));
        workerDatas1.add(new WorkerItem(9, "王1", "工頭"));
        workerDatas1.add(new WorkerItem(10, "王1", "工頭"));
        workerDatas1.add(new WorkerItem(11, "王1", "工頭"));
        workerDatas1.add(new WorkerItem(12, "王1", "工頭"));
        workerDatas1.add(new WorkerItem(13, "王1", "工頭"));
        workerDatas1.add(new WorkerItem(14, "王1", "工頭"));
        workerDatas1.add(new WorkerItem(15, "王1", "工頭"));
        workerDatas1.add(new WorkerItem(16, "王1", "工頭"));
        workerDatas1.add(new WorkerItem(17, "王1", "工頭"));
        workerDatas1.add(new WorkerItem(18, "王1", "工頭"));
        workerDatas1.add(new WorkerItem(19, "王1", "工頭"));
        workerDatas1.add(new WorkerItem(20, "王1", "工頭"));

        workerDatas2.add(new WorkerItem(21, "陳1", "工頭"));
        workerDatas2.add(new WorkerItem(22, "陳1", "工頭"));
        workerDatas2.add(new WorkerItem(23, "陳1", "工頭"));
        workerDatas2.add(new WorkerItem(24, "陳1", "工頭"));
        workerDatas2.add(new WorkerItem(25, "陳1", "工頭"));

        workerDatas3.add(new WorkerItem(26, "黃1", "工頭"));
        workerDatas3.add(new WorkerItem(27, "黃1", "工頭"));
        workerDatas3.add(new WorkerItem(28, "黃1", "工頭"));
        workerDatas3.add(new WorkerItem(29, "黃1", "工頭"));
        workerDatas3.add(new WorkerItem(30, "黃1", "工頭"));
        workerDatas3.add(new WorkerItem(31, "黃1", "工頭"));
        workerDatas3.add(new WorkerItem(32, "黃1", "工頭"));
        workerDatas3.add(new WorkerItem(33, "黃1", "工頭"));
        workerDatas3.add(new WorkerItem(34, "黃1", "工頭"));
        workerDatas3.add(new WorkerItem(35, "黃1", "工頭"));

        mFactoryDatas.add(new Factory(1, "武林場", workerDatas1));
        mFactoryDatas.add(new Factory(2, "豐原場", workerDatas2));
        mFactoryDatas.add(new Factory(3, "桃園場", workerDatas3));
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

    @Override
    public void onSelectTaskCase(int position) {
        mTaskCaseAdapter.swapTaskCase(mTaskCaseDatas.get(position));
        mTaskCaseAdapter.notifyDataSetChanged();
    }
}
