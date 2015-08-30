package com.bananaplan.workflowandroid.workeroverview;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Filter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Toast;

import com.bananaplan.workflowandroid.R;
import com.bananaplan.workflowandroid.assigntask.workers.WorkerItem;
import com.bananaplan.workflowandroid.main.WorkingData;
import com.bananaplan.workflowandroid.utility.OvTabFragmentBase;
import com.bananaplan.workflowandroid.utility.Utils;
import com.bananaplan.workflowandroid.workeroverview.data.status.BaseData;
import com.bananaplan.workflowandroid.workeroverview.data.status.FileData;
import com.bananaplan.workflowandroid.workeroverview.data.status.HistoryData;
import com.bananaplan.workflowandroid.workeroverview.data.status.PhotoData;
import com.bananaplan.workflowandroid.workeroverview.data.status.RecordData;

import java.util.ArrayList;

/**
 * Created by Ben on 2015/8/14.
 */
public class WorkerStatusFragment extends OvTabFragmentBase implements View.OnClickListener, OvTabFragmentBase.WorkerOvCallBack {

    private static final ArrayList<TabInfo> mTabInfos = new ArrayList<>(5);
    private static class TAB_TAG {
        private final static String ALL = "worker_status_tab_tag_all";
        private final static String RECORD = "worker_status_tab_tag_record";
        private final static String FILE = "worker_status_tab_tag_file";
        private final static String PHOTO = "worker_status_tab_tag_photo";
        private final static String HISTORY = "worker_status_tab_tag_history";
    }

    private EditText mRecordEditText;
    private TabHost mTabHost;
    private TextView mScore;
    private ListView mListView;
    private DataAdapter mAdapter;

    static {
        mTabInfos.add(new TabInfo(BaseData.TYPE.ALL.ordinal(), TAB_TAG.ALL, R.id.tab_all, R.id.worker_status_list));
        mTabInfos.add(new TabInfo(BaseData.TYPE.RECORD.ordinal(), TAB_TAG.RECORD, R.id.tab_record, R.id.worker_status_list));
        mTabInfos.add(new TabInfo(BaseData.TYPE.FILE.ordinal(), TAB_TAG.FILE, R.id.tab_file, R.id.worker_status_list));
        mTabInfos.add(new TabInfo(BaseData.TYPE.PHOTO.ordinal(), TAB_TAG.PHOTO, R.id.tab_photo, R.id.worker_status_list));
        mTabInfos.add(new TabInfo(BaseData.TYPE.HISTORY.ordinal(), TAB_TAG.HISTORY, R.id.tab_history, R.id.worker_status_list));
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
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mRecordEditText = (EditText) getActivity().findViewById(R.id.et_record);
        getActivity().findViewById(R.id.upload).setOnClickListener(this);
        getActivity().findViewById(R.id.record).setOnClickListener(this);
        getActivity().findViewById(R.id.capture).setOnClickListener(this);
        getActivity().findViewById(R.id.score_plus).setOnClickListener(this);
        getActivity().findViewById(R.id.score_minus).setOnClickListener(this);
        mTabHost = (TabHost) getActivity().findViewById(R.id.worker_ov_right_pane_status_tab_host);
        setupTabHost();
        mScore = (TextView) getActivity().findViewById(R.id.score);
        onWorkerSelected(getSelectedWorker());
    }

    private void setupTabHost() {
        mTabHost.setup();
        getActivity().getLayoutInflater().inflate(R.layout.fragment_worker_ov_status_tabs_container, mTabHost.getTabContentView(), true);
        for (TabInfo info : mTabInfos) {
            mTabHost.addTab(mTabHost.newTabSpec(info.tag).setIndicator(info.tag).setContent(info.contentResId));
            updateTabIndicatorView(info.tag, info.tabResId);
        }
        mListView = (ListView) getActivity().findViewById(R.id.worker_status_list);
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
            case TAB_TAG.RECORD:
                textResId = R.string.worker_ov_tab_record;
                iconResId = R.drawable.selector_tab_record;
                break;
            case TAB_TAG.FILE:
                textResId = R.string.worker_ov_tab_file;
                iconResId = R.drawable.selector_tab_file;
                break;
            case TAB_TAG.PHOTO:
                textResId = R.string.worker_ov_tab_photo;
                iconResId = R.drawable.selector_tab_photo;
                break;
            case TAB_TAG.HISTORY:
                textResId = R.string.worker_ov_tab_history;
                iconResId = R.drawable.selector_tab_history;
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
            case R.id.score_plus:
                scoreWorker(true);
                break;
            case R.id.score_minus:
                scoreWorker(false);
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

    private void scoreWorker(boolean plus) {
        final WorkerItem worker = getSelectedWorker();
        if (plus) {
            WorkingData.getInstance(getActivity()).getWorkerItemById(getSelectedWorker().id).score++;
        } else {
            WorkingData.getInstance(getActivity()).getWorkerItemById(getSelectedWorker().id).score--;
        }
        mScore.setText(String.valueOf(WorkingData.getInstance(getActivity()).getWorkerItemById(worker.id).score));
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
        mAdapter.getFilter().filter("");
    }

    @Override
    public void onWorkerSelected(WorkerItem worker) {
        mScore.setText(String.valueOf(WorkingData.getInstance(getActivity()).getWorkerItemById(worker.id).score));
        if (mAdapter == null) {
            mAdapter = new DataAdapter(worker.records);
            mListView.setAdapter(mAdapter);
            if (mListView.getVisibility() != View.VISIBLE) {
                mListView.setVisibility(View.VISIBLE);
            }
        } else {
            mAdapter.clear();
            mAdapter.addAll(worker.records);
            mAdapter.notifyDataSetChanged();
        }
        onTabSelected(R.id.tab_all);
    }

    @Override
    public Object getCallBack() {
        return this;
    }

    private class DataAdapter extends ArrayAdapter<BaseData> {
        private ArrayList<BaseData> mOrigData;
        private ArrayList<BaseData> mFilteredData;
        private CustomFilter mFilter;

        public DataAdapter(ArrayList<BaseData> data) {
            super(getActivity(), 0, data);
            mOrigData = data;
            mFilteredData = new ArrayList<>(data);
            mFilter = new CustomFilter();
        }

        @Override
        public int getCount() {
            return mFilteredData.size();
        }

        @Override
        public BaseData getItem(int position) {
            return mFilteredData.get(position);
        }

        private class ViewHolder {
            ImageView avatar;
            ImageView photo;
            TextView name;
            TextView description;
            TextView status;
            TextView download;
            TextView time;

            public ViewHolder(View view) {
                avatar = (ImageView) view.findViewById(R.id.avatar);
                photo = (ImageView) view.findViewById(R.id.photo);
                name = (TextView) view.findViewById(R.id.name);
                description = (TextView) view.findViewById(R.id.description);
                status = (TextView) view.findViewById(R.id.status);
                download = (TextView) view.findViewById(R.id.download);
                time = (TextView) view.findViewById(R.id.time);
            }
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder;
            if (convertView == null) {
                convertView = getActivity().getLayoutInflater().inflate(R.layout.worker_record, parent, false);
                holder = new ViewHolder(convertView);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            BaseData data = getItem(position);
            WorkerItem worker;
            int nameVisibility = View.GONE;
            int descriptionVisibility = View.GONE;
            int statusVisibility = View.GONE;
            int downloadVisibility = View.GONE;
            int photoVisibility = View.GONE;
            switch (data.type) {
                case RECORD:
                    if (data instanceof RecordData) {
                        worker = WorkingData.getInstance(getActivity()).getWorkerItemById(((RecordData) data).reporter);
                        holder.avatar.setImageDrawable(worker.getAvator());
                        holder.name.setText(worker.name);
                        holder.description.setText(((RecordData) data).description);
                        nameVisibility = View.VISIBLE;
                        descriptionVisibility = View.VISIBLE;
                    }
                    break;
                case FILE:
                    if (data instanceof FileData) {
                        worker = WorkingData.getInstance(getActivity()).getWorkerItemById(((FileData) data).uploader);
                        String statusTxt = (worker != null ? worker.name + " " : "") +
                                getResources().getString(R.string.worker_ov_tab_status_upload) +
                                (TextUtils.isEmpty(((FileData) data).fileName) ? "" : " " + ((FileData) data).fileName);
                        holder.avatar.setImageDrawable(getResources().getDrawable(R.drawable.ic_insert_drive_file, null));
                        holder.status.setText(statusTxt);
                        holder.download.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Toast.makeText(getActivity(), "download file", Toast.LENGTH_SHORT).show();
                            }
                        });
                        statusVisibility = View.VISIBLE;
                        downloadVisibility = View.VISIBLE;
                    }
                    break;
                case PHOTO:
                    if (data instanceof PhotoData) {
                        worker = WorkingData.getInstance(getActivity()).getWorkerItemById(((PhotoData) data).uploader);
                        holder.status.setText(worker.name + ((PhotoData) data).fileName);
                        holder.photo.setImageDrawable(((PhotoData) data).photo);
                        holder.avatar.setImageDrawable(getResources().getDrawable(R.drawable.ic_photo, null));
                        statusVisibility = View.VISIBLE;
                        photoVisibility = View.VISIBLE;
                    }
                    break;
                case HISTORY:
                    if (data instanceof HistoryData) {
                        worker = getSelectedWorker();
                        holder.status.setText(worker.name + " " +
                                (((HistoryData) data).status == HistoryData.STATUS.WORK ?
                                        getResources().getString(R.string.worker_ov_tab_status_work) :
                                        getResources().getString(R.string.worker_ov_tab_status_off_work)));
                        holder.avatar.setImageDrawable(getResources().getDrawable(R.drawable.ic_person, null));
                        statusVisibility = View.VISIBLE;
                    }
                    break;
            }
            holder.name.setVisibility(nameVisibility);
            holder.description.setVisibility(descriptionVisibility);
            holder.status.setVisibility(statusVisibility);
            holder.download.setVisibility(downloadVisibility);
            holder.photo.setVisibility(photoVisibility);
            holder.time.setText(Utils.timestamp2Date(data.time, Utils.DATE_FORMAT_YMD_HM_AMPM));
            return convertView;
        }

        @Override
        public Filter getFilter() {
            return mFilter;
        }

        private class CustomFilter extends Filter {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                FilterResults result = new FilterResults();
                ArrayList<BaseData> filterResult = new ArrayList<>();
                for (BaseData data : mOrigData) {
                    if (mTabHost.getCurrentTab() == data.type.ordinal() ||
                            mTabHost.getCurrentTab() == BaseData.TYPE.ALL.ordinal()) {
                        filterResult.add(data);
                    }
                }
                result.values = filterResult;
                result.count = filterResult.size();
                return result;
            }

            @SuppressWarnings("unchecked")
            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                mFilteredData.clear();
                mFilteredData.addAll((ArrayList<BaseData>) results.values);
                notifyDataSetChanged();
            }
        }
    }
}
