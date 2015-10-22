package com.bananaplan.workflowandroid.assigntask;

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
import android.widget.ProgressBar;
import android.widget.Spinner;

import com.bananaplan.workflowandroid.R;
import com.bananaplan.workflowandroid.assigntask.workers.WorkerGridViewAdapter;
import com.bananaplan.workflowandroid.data.Case;
import com.bananaplan.workflowandroid.assigntask.tasks.CaseCardDecoration;
import com.bananaplan.workflowandroid.assigntask.tasks.CaseAdapter;
import com.bananaplan.workflowandroid.assigntask.tasks.CaseOnTouchListener;
import com.bananaplan.workflowandroid.data.loading.LoadingDataTask;
import com.bananaplan.workflowandroid.data.dataobserver.DataObserver;
import com.bananaplan.workflowandroid.utility.GridSpanSizeLookup;
import com.bananaplan.workflowandroid.data.Factory;
import com.bananaplan.workflowandroid.assigntask.workers.WorkerFragment;
import com.bananaplan.workflowandroid.utility.Utils;
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
 *
 * TODO: Need to handle no-data situation
 *
 */
public class AssignTaskFragment extends Fragment implements
        ViewPager.OnPageChangeListener, WorkerGridViewAdapter.OnRefreshCaseListener,
        LoadingDataTask.OnFinishLoadingDataListener, DataObserver {

    private static final String TAG = "AssignTaskFragment";

    private static final String KEY_FACTORY_SPINNER_POSITION = "key_factory_spinner_position";

    private Context mContext;
    private FragmentManager mFragmentManager;

    private View mMainView;
    private ProgressBar mProgressBar;

    private Spinner mCaseSpinner;
    private CaseSpinnerAdapter mCaseSpinnerAdapter;
    private List<String> mCaseTitles = new ArrayList<>();
    private int mSelectedCasePosition = 0;
    private boolean mIsCaseSpinnerInitialized = false;

    private Spinner mFactorySpinner;
    private IconSpinnerAdapter mFactorySpinnerAdapter;
    private List<String> mFactorySpinnerDatas = new ArrayList<>();
    private boolean mIsFactorySpinnerFirstCalled = true;

    private List<WorkerFragment> mWorkerPageList = new ArrayList<>();
    private ViewPager mWorkerPager;
    private WorkerPagerAdapter mWorkerPagerAdapter;
    private int mMaxWorkerCountInPage;
    private int mPreviousPagerIndex = 0;
    private ViewGroup mWorkerPagerIndicatorContainer;

    private RecyclerView mCaseView;
    private GridLayoutManager mGridLayoutManager;
    private CaseAdapter mCaseAdapter;
    private CaseOnTouchListener mCaseOnTouchListener;


    private class CaseSpinnerAdapter extends IconSpinnerAdapter<String> {
        public CaseSpinnerAdapter(Context context, int resource, List<String> datas) {
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
        public boolean isDropdownSelectedIconVisible(int position) {
            return mSelectedCasePosition == position;
        }

        @Override
        public String getDropdownSpinnerViewDisplayString(int position) {
            return (String) getItem(position);
        }
    }

    private class FactorySpinnerAdapter extends IconSpinnerAdapter<String> {
        public FactorySpinnerAdapter(Context context, int resource, List<String> datas) {
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

        private List<WorkerFragment> mWorkerPageData = new ArrayList<>();

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
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
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
    public void onStart() {
        super.onStart();
        WorkingData.getInstance(mContext).registerDataObserver(this);
        updateData();
    }

    @Override
    public void onStop() {
        super.onStop();
        WorkingData.getInstance(mContext).removeDataObserver(this);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(KEY_FACTORY_SPINNER_POSITION, mFactorySpinner.getSelectedItemPosition());
    }

    private void initialize() {
        mFragmentManager = getFragmentManager();
        mMaxWorkerCountInPage = WorkerFragment.MAX_WORKER_COUNT_IN_PAGE; // Need to get count according to the device size.
        findViews();
        showViews();

        //new LoadingDataTask(mContext, this).execute();
    }

    private void findViews() {
        mMainView = getView().findViewById(R.id.main_view);
        mProgressBar = (ProgressBar) getView().findViewById(R.id.progress_bar);
        mCaseSpinner = (Spinner) getView().findViewById(R.id.case_spinner);
        mFactorySpinner = (Spinner) getView().findViewById(R.id.factory_spinner);
        mWorkerPager = (ViewPager) getView().findViewById(R.id.worker_pager);
        mWorkerPagerIndicatorContainer = (ViewGroup) getView().findViewById(R.id.worker_pager_indicator_container);
        mCaseView = (RecyclerView) getView().findViewById(R.id.task_case_view);
    }

    private void showViews() {
        getCaseSpinnerTitles();
        getFactorySpinnerTitles();
        initCaseSpinner();
        initFactorySpinner();
        initCaseView();
        initWorkerPager();
        Utils.replaceProgressBarWhenLoadingFinished(mContext, mMainView, mProgressBar);
    }

    private void getCaseSpinnerTitles() {
        for (Case aCase : WorkingData.getInstance(mContext).getCases()) {
            mCaseTitles.add(aCase.name);
        }
    }

    private void getFactorySpinnerTitles() {
        for (Factory factory : WorkingData.getInstance(mContext).getFactories()) {
            mFactorySpinnerDatas.add(factory.name);
        }
    }

    private void initCaseSpinner() {
        mCaseSpinnerAdapter = new CaseSpinnerAdapter(mContext, R.layout.icon_spinner_item, mCaseTitles);
        mCaseSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_list_item_1);
        mCaseSpinner.setAdapter(mCaseSpinnerAdapter);
        mCaseSpinner.setSelection(mSelectedCasePosition);
        mCaseSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                // Avoid the first call of onItemSelected() when the spinner is initialized.
                if (!mIsCaseSpinnerInitialized) {
                    mIsCaseSpinnerInitialized = true;
                    return;
                }

                mSelectedCasePosition = position;
                mCaseAdapter.swapCase(WorkingData.getInstance(mContext).getCases().get(position));
                mCaseView.scrollToPosition(0);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    // TODO: Need to handle rotation
    private void initFactorySpinner() {
        mFactorySpinnerAdapter = new FactorySpinnerAdapter(mContext, R.layout.factory_spinner_item, mFactorySpinnerDatas);
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
                createWorkerPages(WorkingData.getInstance(mContext).getFactories().get(position).workers);
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

    private void initCaseView() {
        mCaseAdapter = new CaseAdapter(mContext, WorkingData.getInstance(mContext).getCases().get(0));
        mCaseOnTouchListener = new CaseOnTouchListener(mCaseView);

        int caseSpanCount = mContext.getResources().getInteger(R.integer.task_case_column_count);
        mGridLayoutManager =
                new GridLayoutManager(mContext, caseSpanCount);
        mGridLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mGridLayoutManager.setSpanSizeLookup(new GridSpanSizeLookup(mGridLayoutManager));

        mCaseView.setLayoutManager(mGridLayoutManager);
        mCaseView.addItemDecoration(new CaseCardDecoration(mContext, caseSpanCount));
        mCaseView.setOnTouchListener(mCaseOnTouchListener);
        mCaseView.setAdapter(mCaseAdapter);
    }

    // TODO: Need to handle rotation
    private void initWorkerPager() {
        mWorkerPagerAdapter = new WorkerPagerAdapter(mFragmentManager);
        createWorkerPages(WorkingData.getInstance(mContext).getFactories().get(0).workers);
        initWorkerPagerIndicator();
        mWorkerPagerAdapter.setWorkerPages(mWorkerPageList);
        mWorkerPager.setAdapter(mWorkerPagerAdapter);
        mWorkerPager.setOnPageChangeListener(this);
    }

    private void createWorkerPages(List<Worker> workerDatas) {
        if (workerDatas == null || workerDatas.size() < 0) return;

        addWorkerPage();

        int fragmentIndex = 0;
        for (int i = 0 ; i < workerDatas.size() ; i++) {
            if (mMaxWorkerCountInPage == mWorkerPageList.get(fragmentIndex).getDataSize()) {
                fragmentIndex++;
                addWorkerPage();
            }
            mWorkerPageList.get(fragmentIndex).addWorker(workerDatas.get(i));  // After DB is created, add worker information
        }
    }

    private void addWorkerPage() {
        WorkerFragment workerFragment = new WorkerFragment();
        workerFragment.setOnRefreshCaseListener(this);
        mWorkerPageList.add(workerFragment);

        View indicator = LayoutInflater.from(mContext).inflate(
                R.layout.worker_pager_indicator, mWorkerPagerIndicatorContainer, false);
        mWorkerPagerIndicatorContainer.addView(indicator);
    }

    private void initWorkerPagerIndicator() {
        if (mWorkerPagerIndicatorContainer.getChildAt(0) == null) return;
        mWorkerPagerIndicatorContainer.getChildAt(0).setSelected(true);
        mPreviousPagerIndex = 0;
    }

    private void clearWorkers() {
        mWorkerPageList.clear();
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
    public void onRefreshCase() {
        mCaseAdapter.notifyDataSetChanged();
    }

    @Override
    public void onFinishLoadingData() {
        //showViews();
    }

    @Override
    public void onFailLoadingData(boolean isFailCausedByInternet) {

    }

    @Override
    public void updateData() {
        mCaseAdapter.notifyDataSetChanged();

        for (WorkerFragment workerFragment : mWorkerPageList) {
            workerFragment.notifyDataSetChanged();
        }
    }
}
