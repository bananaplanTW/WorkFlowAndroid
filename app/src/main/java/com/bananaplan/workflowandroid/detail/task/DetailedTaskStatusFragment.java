package com.bananaplan.workflowandroid.detail.task;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
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
import com.bananaplan.workflowandroid.data.IdData;
import com.bananaplan.workflowandroid.data.Task;
import com.bananaplan.workflowandroid.data.Worker;
import com.bananaplan.workflowandroid.data.WorkingData;
import com.bananaplan.workflowandroid.data.activity.ActivityDataStore;
import com.bananaplan.workflowandroid.data.activity.EmployeeActivityTypeInterpreter;
import com.bananaplan.workflowandroid.data.activity.TaskActivityTypeInterpreter;
import com.bananaplan.workflowandroid.data.activity.actions.LeaveAFileCommentToTaskCommand;
import com.bananaplan.workflowandroid.data.activity.actions.LeaveAPhotoCommentToTaskCommand;
import com.bananaplan.workflowandroid.data.activity.actions.LeaveATextCommentToTaskCommand;
import com.bananaplan.workflowandroid.data.dataobserver.DataObserver;
import com.bananaplan.workflowandroid.data.worker.status.BaseData;
import com.bananaplan.workflowandroid.data.worker.status.DataFactory;
import com.bananaplan.workflowandroid.data.worker.status.FileData;
import com.bananaplan.workflowandroid.data.worker.status.HistoryData;
import com.bananaplan.workflowandroid.data.worker.status.PhotoData;
import com.bananaplan.workflowandroid.data.worker.status.RecordData;
import com.bananaplan.workflowandroid.utility.DisplayImageActivity;
import com.bananaplan.workflowandroid.utility.OvTabFragmentBase;
import com.bananaplan.workflowandroid.utility.Utils;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;


/**
 * Created by Ben on 2015/8/14.
 */
public class DetailedTaskStatusFragment extends OvTabFragmentBase implements View.OnClickListener, DataObserver,
        SwipeRefreshLayout.OnRefreshListener {

    private static final ArrayList<TabInfo> mTabInfos = new ArrayList<>(5);

    private static class TAB_TAG {
        private final static String ALL = "worker_status_tab_tag_all";
        private final static String RECORD = "worker_status_tab_tag_record";
        private final static String FILE = "worker_status_tab_tag_file";
        private final static String PHOTO = "worker_status_tab_tag_photo";
        private final static String HISTORY = "worker_status_tab_tag_history";
    }

    private static final int REQUEST_IMAGE_CAPTURE = 10001;
    private static final int REQUEST_PICK_FILE = 10002;

    private Context mContext;

    private Task mTask;

    private EditText mRecordEditText;
    private TabHost mTabHost;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private ListView mListView;
    private DataAdapter mAdapter;

    private String mCurrentPhotoPath = null;
    private String mCurrentFilePath = null;
    private String mCommentText = null;

    static {
        mTabInfos.add(new TabInfo(0, TAB_TAG.ALL, R.id.tab_all, R.id.worker_status_list));
        mTabInfos.add(new TabInfo(1, TAB_TAG.RECORD, R.id.tab_record, R.id.worker_status_list));
        mTabInfos.add(new TabInfo(2, TAB_TAG.FILE, R.id.tab_file, R.id.worker_status_list));
        mTabInfos.add(new TabInfo(3, TAB_TAG.PHOTO, R.id.tab_photo, R.id.worker_status_list));
        mTabInfos.add(new TabInfo(4, TAB_TAG.HISTORY, R.id.tab_history, R.id.worker_status_list));
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
        return getActivity().getLayoutInflater().inflate(R.layout.fragment_detailed_task_status, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initialize();
        setupTaskLogRecords();
        setupViews();
    }

    private void setupTaskLogRecords() {
        ArrayList<BaseData> records = new ArrayList<>();
        ActivityDataStore instance = ActivityDataStore.getInstance(mContext);;

        if (instance.hasTaskActivitiesCacheWithTaskId(mTask.id)) {
            records = instance.getTaskActivities(mTask.id);
        } else {
            instance.loadTaskActivities(mTask.id, 15);
            instance.registerDataObserver(this);
        }

        Collections.sort(records, new Comparator<BaseData>() {
            @Override
            public int compare(BaseData lhs, BaseData rhs) {
                return rhs.time.compareTo(lhs.time);
            }
        });

        updateListData(records);
    }

    private void initialize() {
        mContext = getContext();
        mTask = WorkingData.getInstance(mContext).getTaskById(getArguments().getString(DetailedTaskActivity.EXTRA_TASK_ID));
        findViews();
        setupTabHost();
        setupTabs();
    }

    private void findViews() {
        mRecordEditText = (EditText) getView().findViewById(R.id.et_record);
        mTabHost = (TabHost) getView().findViewById(R.id.worker_ov_right_pane_status_tab_host);
    }

    private void setupTabs() {
        getView().findViewById(R.id.upload).setOnClickListener(this);
        getView().findViewById(R.id.record).setOnClickListener(this);
        getView().findViewById(R.id.capture).setOnClickListener(this);
    }

    private void setupViews() {
        ((TextView) getActivity().findViewById(R.id.status_string)).setText(getString(R.string.status_string_detailed_task));
    }

    private void setupTabHost() {
        mTabHost.setup();
        getActivity().getLayoutInflater().inflate(R.layout.fragment_worker_ov_status_tabs_container, mTabHost.getTabContentView(), true);
        for (TabInfo info : mTabInfos) {
            mTabHost.addTab(mTabHost.newTabSpec(info.tag).setIndicator(info.tag).setContent(info.contentResId));
            updateTabIndicatorView(info.tag, info.tabResId);
        }
        mListView = (ListView) getActivity().findViewById(R.id.worker_status_list);

        mSwipeRefreshLayout = (SwipeRefreshLayout) getActivity().findViewById(R.id.worker_status_list_container);
        mSwipeRefreshLayout.setOnRefreshListener(this);
        mSwipeRefreshLayout.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);
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
                textResId = R.string.ov_tab_record;
                iconResId = R.drawable.selector_tab_record;
                break;
            case TAB_TAG.FILE:
                textResId = R.string.ov_tab_file;
                iconResId = R.drawable.selector_tab_file;
                break;
            case TAB_TAG.PHOTO:
                textResId = R.string.ov_tab_photo;
                iconResId = R.drawable.selector_tab_photo;
                break;
            case TAB_TAG.HISTORY:
                textResId = R.string.ov_tab_history;
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
                pickupFile();
                break;
            case R.id.record:
                recordText();
                break;
            case R.id.capture:
                capturePhoto();
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

    @Override
    public void onRefresh() {
        ActivityDataStore instance = ActivityDataStore.getInstance(mContext);

        instance.loadTaskActivities(mTask.id, 15);
        instance.registerDataObserver(this);
    }

    private void recordText() {
        if (TextUtils.isEmpty(mRecordEditText.getText())) return;

        String managerId = WorkingData.getUserId();
        RecordData record = (RecordData) DataFactory.genData(managerId, BaseData.TYPE.RECORD);
        record.time = Calendar.getInstance().getTime();
        record.reporter = WorkingData.getUserId();
        record.description = mRecordEditText.getText().toString();
        addRecord(getSelectedWorker(), record);

        mCommentText = mRecordEditText.getText().toString();
        syncingTextActivity();

        mRecordEditText.setText("");

        Utils.hideSoftKeyboard(getActivity());

        Toast.makeText(mContext,
                String.format(mContext.getString(R.string.status_record_completed), mContext.getString(R.string.ov_message)),
                Toast.LENGTH_SHORT).show();
    }

    private void pickupFile() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("*/*");
        intent.addCategory(Intent.CATEGORY_OPENABLE);

        try {
            startActivityForResult(Intent.createChooser(intent, "Select a File to Upload"),
                    REQUEST_PICK_FILE);
        } catch (android.content.ActivityNotFoundException ex) {
            Toast.makeText(getActivity(), "Please install a File Manager.", Toast.LENGTH_SHORT).show();
            ex.printStackTrace();
        }
    }

    private void capturePhoto() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getActivity().getPackageManager()) == null) {
            Toast.makeText(getActivity(), "No Activity to handle ACTION_IMAGE_CAPTURE intent", Toast.LENGTH_SHORT).show();
        }
        File photoFile = null;
        try {
            photoFile = createImageFile();
        } catch (IOException ex) {
            Toast.makeText(getActivity(), "Create image file failed", Toast.LENGTH_SHORT).show();
            ex.printStackTrace();
        }
        // Continue only if the File was successfully created
        if (photoFile != null) {
            takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(photoFile));
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }
    }

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        File storageDir = Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES);
        File image = new File(storageDir + "/" + timeStamp + ".jpg");
        if (!image.createNewFile()) throw new IOException();
        // Save a file: path for use with ACTION_VIEW intents
        mCurrentPhotoPath = "file:" + image.getAbsolutePath();
        return image;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != Activity.RESULT_OK) return;
        switch (requestCode) {
            case REQUEST_IMAGE_CAPTURE:
                onPhotoCaptured();
                break;
            case REQUEST_PICK_FILE:
                onFilePicked(data);
                break;
            default:
                break;
        }
    }

    private void onFilePicked(Intent intent) {
        Uri uri = intent.getData();
        String path = null;
        try {
            path = Utils.getPath(getActivity(), uri);
        } catch (URISyntaxException e) {
            Toast.makeText(getActivity(), "File attach failed", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
        if (TextUtils.isEmpty(path)) return;

        String ownerId = WorkingData.getUserId();
        FileData file = (FileData) DataFactory.genData(ownerId, BaseData.TYPE.FILE);

        file.uploader = ownerId;
        file.time = Calendar.getInstance().getTime();
        file.fileName = path.substring(path.lastIndexOf('/') + 1);
        file.filePath = uri;
        addRecord(getSelectedWorker(), file);

        mCurrentFilePath = path;
        syncingFileActivity();
        //onItemSelected(getSelectedWorker()); // force notify adapter data changed
    }

    private void onPhotoCaptured() {
        // compress photo
        File photoFile = new File(mCurrentPhotoPath.replace("file:", ""));
        Bitmap bitmap = Utils.scaleBitmap(getActivity(), photoFile.getAbsolutePath());
        if (bitmap == null) return;

        String ownerId = WorkingData.getUserId();
        PhotoData photo = (PhotoData) DataFactory.genData(ownerId, BaseData.TYPE.PHOTO);

        photo.time = Calendar.getInstance().getTime();
        photo.uploader = ownerId;
        photo.fileName = mCurrentPhotoPath.substring(mCurrentPhotoPath.lastIndexOf('/') + 1);
        photo.photo = new BitmapDrawable(getResources(), bitmap);
        photo.filePath = Uri.parse(mCurrentPhotoPath);
        addRecord(getSelectedWorker(), photo);

        scanPhotoToGallery();
        syncingPhotoActivity();
        //onItemSelected(getSelectedWorker()); // force notify adapter data changed
    }

    private void addRecord(Worker worker, BaseData data) {
        ActivityDataStore instance = ActivityDataStore.getInstance(mContext);
        instance.addTaskActivity(mTask.id, data);
    }

    private void scanPhotoToGallery() {
        if (!TextUtils.isEmpty(mCurrentPhotoPath)) { // trigger media scanner
            Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
            File f = new File(mCurrentPhotoPath);
            Uri contentUri = Uri.fromFile(f);
            mediaScanIntent.setData(contentUri);
            getActivity().sendBroadcast(mediaScanIntent);
        }
    }

    private void syncingPhotoActivity() {
        String realPath = mCurrentPhotoPath.substring(mCurrentPhotoPath.indexOf(':') + 1);

        LeaveAPhotoCommentToTaskCommand leaveAPhotoCommentToTaskCommand =
                new LeaveAPhotoCommentToTaskCommand(mContext, mTask.id, realPath);
        leaveAPhotoCommentToTaskCommand.execute();

        mCurrentPhotoPath = null;
    }

    private void syncingFileActivity() {
        if (Utils.isImage(mCurrentFilePath)) {
            LeaveAPhotoCommentToTaskCommand leaveAPhotoCommentToTaskCommand =
                    new LeaveAPhotoCommentToTaskCommand(mContext, mTask.id, mCurrentFilePath);
            leaveAPhotoCommentToTaskCommand.execute();
        } else {
            LeaveAFileCommentToTaskCommand leaveAFileCommentToTaskCommand =
                    new LeaveAFileCommentToTaskCommand(mContext, mTask.id, mCurrentFilePath);
            leaveAFileCommentToTaskCommand.execute();
        }

        mCurrentFilePath = null;
    }

    private void syncingTextActivity() {
        LeaveATextCommentToTaskCommand leaveATextCommentToTaskCommand =
                new LeaveATextCommentToTaskCommand(mContext, mTask.id, mCommentText);
        leaveATextCommentToTaskCommand.execute();

        mCommentText = null;
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

    /**
     * will update data when the record data has been fetch
     */
    @Override
    public void updateData() {
        ActivityDataStore instance = ActivityDataStore.getInstance(mContext);
        instance.removeDataObserver(this);

        ArrayList<BaseData> records = null;

        if (instance.hasTaskActivitiesCacheWithTaskId(mTask.id)) {
            records = instance.getTaskActivities(mTask.id);
        }

        if (records == null) {
            return;
        }

        updateListData(records);
        mSwipeRefreshLayout.setRefreshing(false);
        onTabSelected(R.id.tab_all);
    }

    private void updateListData(ArrayList<BaseData> records) {
        mAdapter = new DataAdapter(records);
        mListView.setAdapter(mAdapter);
        if (mListView.getVisibility() != View.VISIBLE) {
            mListView.setVisibility(View.VISIBLE);
        }

        mAdapter.notifyDataSetChanged();
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
            View view;
            ImageView avatar;
            ImageView photo;
            TextView name;
            TextView description;
            TextView status;
            TextView download;
            TextView time;

            public ViewHolder(View view) {
                this.view = view;
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
            IdData user;
            String description = "";
            int nameVisibility = View.GONE;
            int descriptionVisibility = View.GONE;
            int statusVisibility = View.GONE;
            int downloadVisibility = View.GONE;
            int photoVisibility = View.GONE;
            switch (data.type) {
                case RECORD:
                    if (data instanceof RecordData) {
                        RecordData recordData = (RecordData) data;
                        user = WorkingData.getInstance(getActivity()).getUserById(recordData.reporter);
                        // [TODO] should use manager's avatar
                        holder.avatar.setImageDrawable(getResources().getDrawable(R.drawable.ic_person, null));
                        holder.name.setText(user.name);
                        holder.description.setText(recordData.description);
                        nameVisibility = View.VISIBLE;
                        descriptionVisibility = View.VISIBLE;
                    }
                    break;
                case FILE:
                    if (data instanceof FileData) {
                        final FileData fileData = (FileData) data;

                        user = WorkingData.getInstance(getActivity()).getUserById(fileData.uploader);
                        String statusTxt = (user != null ? user.name + " " : "") +
                                getResources().getString(R.string.worker_ov_tab_status_upload) +
                                (TextUtils.isEmpty(fileData.fileName) ? "" : " " + fileData.fileName);
                        holder.avatar.setImageDrawable(getResources().getDrawable(R.drawable.ic_insert_drive_file, null));
                        holder.status.setText(statusTxt);
                        holder.download.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Utils.downloadFile(getActivity(), fileData.filePath.toString(), fileData.fileName);
                            }
                        });
                        statusVisibility = View.VISIBLE;
                        downloadVisibility = View.VISIBLE;
                    }
                    break;
                case PHOTO:
                    if (data instanceof PhotoData) {
                        final PhotoData photoData = (PhotoData) data;
                        user = WorkingData.getInstance(getActivity()).getUserById(photoData.uploader);
                        holder.status.setText((user != null ? user.name + " " : "") +
                                getResources().getString(R.string.worker_ov_tab_status_capture) +
                                (TextUtils.isEmpty(photoData.fileName) ? "" : " " + photoData.fileName));
                        holder.photo.setImageDrawable(photoData.photo);
                        if (Uri.EMPTY != photoData.filePath) {
                            holder.view.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    startActivity(DisplayImageActivity.
                                            launchDisplayImageActivity(mContext,
                                                    photoData.fileName, photoData.filePath.toString()));
                                }
                            });
                        }
                        holder.name.setText(user.name);
                        holder.avatar.setImageDrawable(getResources().getDrawable(R.drawable.ic_photo, null));
                        statusVisibility = View.VISIBLE;
                        photoVisibility = View.VISIBLE;
                    }
                    break;
                case HISTORY:
                    if (data instanceof HistoryData) {
                        HistoryData historyData = (HistoryData) data;
                        if (data.category == BaseData.CATEGORY.WORKER) {
                            user = WorkingData.getInstance(getActivity()).getUserById(historyData.workerId);
                            // [TODO] should let user to have avatar
                            holder.avatar.setImageDrawable(getResources().getDrawable(R.drawable.ic_person, null));
                            holder.name.setText(user.name);
                            // [TODO] should use String resource to perform multiple languages.
                            description = EmployeeActivityTypeInterpreter.getTranslation(mContext, historyData.tag) + historyData.description;
                        } else if (data.category == BaseData.CATEGORY.TASK) {
                            user = WorkingData.getInstance(getActivity()).getUserById(historyData.workerId);
                            // [TODO] should let user to have avatar
                            holder.avatar.setImageDrawable(getResources().getDrawable(R.drawable.ic_person, null));
                            holder.name.setText(user.name);
                            // [TODO] should use String resource to perform multiple languages.
                            description = TaskActivityTypeInterpreter.getTranslation(mContext, historyData.tag) + historyData.description;
                        }
                        holder.description.setText(description);
                        nameVisibility = View.VISIBLE;
                        descriptionVisibility = View.VISIBLE;
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
                    if (mTabHost.getCurrentTab() == 0 ||
                            (mTabHost.getCurrentTab() - 1) == data.type.ordinal()) {
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
