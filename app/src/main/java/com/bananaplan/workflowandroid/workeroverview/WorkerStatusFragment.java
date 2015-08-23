package com.bananaplan.workflowandroid.workeroverview;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TabHost;
import android.widget.TextView;

import com.bananaplan.workflowandroid.R;
import com.bananaplan.workflowandroid.assigntask.workers.WorkerItem;
import com.bananaplan.workflowandroid.utility.OvTabFragmentBase;

import java.util.ArrayList;

/**
 * Created by Ben on 2015/8/14.
 */
public class WorkerStatusFragment extends OvTabFragmentBase implements View.OnClickListener, OvTabFragmentBase.WorkerOvCallBack {

    private static class TAB_TAG {
        private final static String ALL = "worker_status_tab_tag_all";
        private final static String RECORD = "worker_status_tab_tag_record";
        private final static String FILE = "worker_status_tab_tag_file";
        private final static String PHOTO = "worker_status_tab_tag_photo";
        private final static String HISTORY = "worker_status_tab_tag_history";
    }

    private EditText mRecordEditText;
    private TabHost mTabHost;
    private static final ArrayList<TabInfo> mTabInfos = new ArrayList<>(5);

    static {
        mTabInfos.add(new TabInfo(0, TAB_TAG.ALL, R.id.tab_all, R.id.tab_content_all));
        mTabInfos.add(new TabInfo(1, TAB_TAG.RECORD, R.id.tab_record, R.id.tab_content_record));
        mTabInfos.add(new TabInfo(2, TAB_TAG.FILE, R.id.tab_file, R.id.tab_content_file));
        mTabInfos.add(new TabInfo(3, TAB_TAG.PHOTO, R.id.tab_photo, R.id.tab_content_photo));
        mTabInfos.add(new TabInfo(4, TAB_TAG.HISTORY, R.id.tab_history, R.id.tab_content_history));
    }

    private static final class TabInfo {
        int idx;
        String tag;
        int tabResId;
        int contentResId;

        public TabInfo(int idx, String tag, int resId, int contentResId) {
            this.idx = idx;
            this.tag = tag;
            this.tabResId = resId;
            this.contentResId = contentResId;
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return getActivity().getLayoutInflater().inflate(R.layout.fragment_worker_ov_status, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mRecordEditText = (EditText) getActivity().findViewById(R.id.et_record);
        getActivity().findViewById(R.id.upload).setOnClickListener(this);
        getActivity().findViewById(R.id.record).setOnClickListener(this);
        getActivity().findViewById(R.id.capture).setOnClickListener(this);
        mTabHost = (TabHost) getActivity().findViewById(R.id.worker_ov_right_pane_status_tab_host);
        setupTabHost();
    }

    private void setupTabHost() {
        mTabHost.setup();
        getActivity().getLayoutInflater().inflate(R.layout.fragment_worker_ov_status_tabs, mTabHost.getTabContentView(), true);
        for (TabInfo info : mTabInfos) {
            mTabHost.addTab(mTabHost.newTabSpec(info.tag).setIndicator(info.tag).setContent(info.contentResId));
            updateTabIndicatorView(info.tag, info.tabResId);
        }
        onTabSelected(R.id.tab_all);
    }

    private void updateTabIndicatorView(final String tag, final int rootId) {
        View view = getActivity().findViewById(rootId);
        view.setOnClickListener(this);
        int iconResId = -1;
        int textResId = -1;
        switch (tag) {
            case TAB_TAG.ALL:
                textResId = R.string.worker_ov_tab_all;
                iconResId = R.drawable.selector_tab_all;
                break;
            case TAB_TAG.HISTORY:
                textResId = R.string.worker_ov_tab_history;
                iconResId = R.drawable.selector_tab_history;
                break;
            case TAB_TAG.FILE:
                textResId = R.string.worker_ov_tab_file;
                iconResId = R.drawable.selector_tab_file;
                break;
            case TAB_TAG.PHOTO:
                textResId = R.string.worker_ov_tab_photo;
                iconResId = R.drawable.selector_tab_photo;
                break;
            case TAB_TAG.RECORD:
                textResId = R.string.worker_ov_tab_record;
                iconResId = R.drawable.selector_tab_record;
                break;
        }
        if (textResId != -1) {
            ((TextView) view.findViewById(R.id.text)).setText(getResources().getString(textResId));
        }
        if (iconResId != -1) {
            ((ImageView) view.findViewById(R.id.icon)).setImageDrawable(getResources().getDrawable(iconResId, null));
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.upload:
                break;
            case R.id.record:
                break;
            case R.id.capture:
                break;
            case R.id.tab_all:
            case R.id.tab_record:
            case R.id.tab_file:
            case R.id.tab_photo:
            case R.id.tab_history:
                onTabSelected(v.getId());
                break;
            default:
                break;
        }
    }

    private void onTabSelected(int id) {
        for (int i = 0; i < mTabInfos.size(); i++) {
            TabInfo info = mTabInfos.get(i);
            if (id == info.tabResId) {
                mTabHost.setCurrentTab(info.idx);
                getActivity().findViewById(info.tabResId).setSelected(true);
            } else {
                getActivity().findViewById(info.tabResId).setSelected(false);
            }
        }
    }

    @Override
    public void onWorkerSelected(WorkerItem worker) {

    }

    @Override
    public Object getCallBack() {
        return this;
    }
}
