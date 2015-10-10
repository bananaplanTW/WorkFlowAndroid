package com.bananaplan.workflowandroid.overview;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.style.StyleSpan;
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
import com.bananaplan.workflowandroid.data.Worker;
import com.bananaplan.workflowandroid.data.WorkingData;
import com.bananaplan.workflowandroid.data.dataobserver.DataObserver;
import com.bananaplan.workflowandroid.data.activity.ActivityDataStore;
import com.bananaplan.workflowandroid.data.activity.EmployeeActivityTypeInterpreter;
import com.bananaplan.workflowandroid.data.worker.status.DataFactory;
import com.bananaplan.workflowandroid.detail.DetailedWorkerActivity;
import com.bananaplan.workflowandroid.overview.workeroverview.WorkerOverviewFragment;
import com.bananaplan.workflowandroid.utility.OvTabFragmentBase;
import com.bananaplan.workflowandroid.utility.Utils;
import com.bananaplan.workflowandroid.data.worker.status.BaseData;
import com.bananaplan.workflowandroid.data.worker.status.FileData;
import com.bananaplan.workflowandroid.data.worker.status.HistoryData;
import com.bananaplan.workflowandroid.data.worker.status.PhotoData;
import com.bananaplan.workflowandroid.data.worker.status.RecordData;

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
public class StatusFragment extends OvTabFragmentBase implements View.OnClickListener,
        OvTabFragmentBase.OvCallBack, DataObserver {

    private static final ArrayList<TabInfo> mTabInfos = new ArrayList<>(5);

    private static class TAB_TAG {
        private final static String ALL = "worker_status_tab_tag_all";
        private final static String RECORD = "worker_status_tab_tag_record";
        private final static String FILE = "worker_status_tab_tag_file";
        private final static String PHOTO = "worker_status_tab_tag_photo";
        private final static String HISTORY = "worker_status_tab_tag_history";
    }

    public static final String FROM = "from";
    private static class CONTENT_SHOW {
        private final static int TASK_STATUS = 1;
        private final static int WORKER_STATUS = 2;
    }

    private static final int REQUEST_IMAGE_CAPTURE = 10001;
    private static final int REQUEST_PICK_FILE = 10002;

    private Worker mWorker;
    private EditText mRecordEditText;
    private TabHost mTabHost;
    private TextView mScore;
    private ListView mListView;
    private DataAdapter mAdapter;

    private String mCurrentPhotoPath = null;
    private int mContentShow;

    static {
        //mTabInfos.add(new TabInfo(BaseData.TYPE.ALL.ordinal(), TAB_TAG.ALL, R.id.tab_all, R.id.worker_status_list));
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
        String from = getArguments() != null ? getArguments().getString(FROM) : "";

        if (from.equals(WorkerOverviewFragment.class.getSimpleName())) {
            mContentShow = CONTENT_SHOW.WORKER_STATUS;
        } else if (from.equals(DetailedWorkerActivity.class.getSimpleName())) {
            mContentShow = CONTENT_SHOW.TASK_STATUS;
        }

        mRecordEditText = (EditText) getActivity().findViewById(R.id.et_record);
        getActivity().findViewById(R.id.upload).setOnClickListener(this);
        getActivity().findViewById(R.id.record).setOnClickListener(this);
        getActivity().findViewById(R.id.capture).setOnClickListener(this);
        getActivity().findViewById(R.id.score_plus).setOnClickListener(this);
        getActivity().findViewById(R.id.score_minus).setOnClickListener(this);
        mTabHost = (TabHost) getActivity().findViewById(R.id.worker_ov_right_pane_status_tab_host);
        setupTabHost();
        mScore = (TextView) getActivity().findViewById(R.id.score);
        onItemSelected(getSelectedWorker());
        String temp = null;
        switch (mContentShow) {
            case CONTENT_SHOW.WORKER_STATUS:
                temp = getString(R.string.status_string_worker, getSelectedWorker().name);
                break;
            case CONTENT_SHOW.TASK_STATUS:
                temp = getString(R.string.status_string_task,
                        getSelectedWorker().getWipTask() != null ? getSelectedWorker().getWipTask().name: "");
                break;
            default:
                break;
        }
        if (!TextUtils.isEmpty(temp)) {
            SpannableStringBuilder statusStringBuilder = new SpannableStringBuilder(temp);
            StyleSpan span = new StyleSpan(Typeface.BOLD);
            statusStringBuilder.setSpan(span, temp.indexOf(" ") + 1, temp.lastIndexOf(" "), Spannable.SPAN_INCLUSIVE_INCLUSIVE);
            if (statusStringBuilder != null) {
                ((TextView) getActivity().findViewById(R.id.status_string)).setText(statusStringBuilder);
            }
        }
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
                pickupFile();
                break;
            case R.id.record:
                recordText();
                break;
            case R.id.capture:
                capturePhoto();
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

    private void recordText() {
        if (TextUtils.isEmpty(mRecordEditText.getText())) return;
        RecordData record = (RecordData) DataFactory.genData(getSelectedWorker().id, BaseData.TYPE.RECORD);
        record.time = Calendar.getInstance().getTime();
        record.reporter = WorkingData.getInstance(getActivity()).getLoginWorkerId();
        record.description = mRecordEditText.getText().toString();
        addRecord(getSelectedWorker(), record);
        onItemSelected(getSelectedWorker()); // force notify adapter data changed
        mRecordEditText.setText("");
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
        File image = new File(storageDir + "/" + timeStamp + ".png");
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
        FileData file = (FileData) DataFactory.genData(getSelectedWorker().id, BaseData.TYPE.FILE);
        file.uploader = WorkingData.getInstance(getActivity()).getLoginWorkerId();
        file.time = Calendar.getInstance().getTime();
        file.fileName = path.substring(path.lastIndexOf('/') + 1);
        file.filePath = uri;
        addRecord(getSelectedWorker(), file);
        onItemSelected(getSelectedWorker()); // force notify adapter data changed
    }

    private void onPhotoCaptured() {
        // compress photo
        File photoFile = new File(mCurrentPhotoPath.replace("file:", ""));
        int targetW = (int) getResources().getDimension(R.dimen.photo_thumbnail_max_width);
        int targetH = (int) getResources().getDimension(R.dimen.photo_thumbnail_max_height);
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        bmOptions.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(photoFile.getAbsolutePath(), bmOptions);
        int photoW = bmOptions.outWidth;
        int photoH = bmOptions.outHeight;
        int scaleFactor = Math.min(photoW / targetW, photoH / targetH);
        bmOptions.inJustDecodeBounds = false;
        bmOptions.inSampleSize = scaleFactor < 1 ? 1 : scaleFactor;
        bmOptions.inPurgeable = true;
        Bitmap bitmap = BitmapFactory.decodeFile(photoFile.getAbsolutePath(), bmOptions);

        PhotoData photo = (PhotoData) DataFactory.genData(getSelectedWorker().id, BaseData.TYPE.PHOTO);
        photo.time = Calendar.getInstance().getTime();
        photo.uploader = WorkingData.getInstance(getActivity()).getLoginWorkerId();
        photo.fileName = mCurrentPhotoPath.substring(mCurrentPhotoPath.lastIndexOf('/') + 1);
        photo.photo = new BitmapDrawable(getResources(), bitmap);
        photo.filePath = Uri.parse(mCurrentPhotoPath);
        addRecord(getSelectedWorker(), photo);
        scanPhotoToGallery();
        onItemSelected(getSelectedWorker()); // force notify adapter data changed
    }

    private void addRecord(Worker worker, BaseData data) {
        if (mContentShow == CONTENT_SHOW.WORKER_STATUS) {
            WorkingData.getInstance(getActivity()).addRecordToWorker(worker, data);
        } else {
            if (worker.getWipTask() != null) {
                WorkingData.getInstance(getActivity()).addRecordToTask(worker.getWipTask(), data);
            }
        }
    }

    private void scanPhotoToGallery() {
        if (!TextUtils.isEmpty(mCurrentPhotoPath)) { // trigger media scanner
            Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
            File f = new File(mCurrentPhotoPath);
            Uri contentUri = Uri.fromFile(f);
            mediaScanIntent.setData(contentUri);
            getActivity().sendBroadcast(mediaScanIntent);
            mCurrentPhotoPath = null;
        }
    }

    private void scoreWorker(boolean plus) {
        final Worker worker = getSelectedWorker();
        if (plus) {
            WorkingData.getInstance(getActivity()).getWorkerById(getSelectedWorker().id).score++;
        } else {
            WorkingData.getInstance(getActivity()).getWorkerById(getSelectedWorker().id).score--;
        }
        mScore.setText(String.valueOf(WorkingData.getInstance(getActivity()).getWorkerById(worker.id).score));
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
    public void onItemSelected(Object item) {
        Worker worker = (Worker) item;
        if (worker == null) return;

        mWorker = worker;

        mScore.setText(String.valueOf(WorkingData.getInstance(getActivity()).getWorkerById(worker.id).score));
        ArrayList<BaseData> records = new ArrayList<>();
        switch (mContentShow) {
            case CONTENT_SHOW.WORKER_STATUS:
                ActivityDataStore instance = ActivityDataStore.getInstance(getContext());
                if (instance.hasWorkerActivitiesCacheWithWorkerId(worker.id)) {
                    records = instance.getWorkerActivities(worker.id);
                } else {
                    instance.loadWorkerActivities(worker.id, 15);
                    instance.registerDataObserver(this);
                }
                break;
            case CONTENT_SHOW.TASK_STATUS:
                if (worker.getWipTask() != null) {
                    records = new ArrayList<>(worker.getWipTask().records);
                } else {
                    records = new ArrayList<>();
                }
                break;
        }
        Collections.sort(records, new Comparator<BaseData>() {
            @Override
            public int compare(BaseData lhs, BaseData rhs) {
                return rhs.time.compareTo(lhs.time);
            }
        });
        if (mAdapter == null) {
            mAdapter = new DataAdapter(records);
            mListView.setAdapter(mAdapter);
            if (mListView.getVisibility() != View.VISIBLE) {
                mListView.setVisibility(View.VISIBLE);
            }
        } else {
            mAdapter.clear();
            mAdapter.addAll(records);
        }
        mAdapter.notifyDataSetChanged();
        onTabSelected(R.id.tab_all);
    }

    /**
     * will update data when the record data has been fetch
     */
    @Override
    public void updateData() {
        ActivityDataStore instance = ActivityDataStore.getInstance(getContext());
        instance.removeDataObserver(this);

        ArrayList<BaseData> records = instance.getWorkerActivities(mWorker.id);
        if (records == null) {
            return;
        }

        // [TODO] should move into a single function
        if (mAdapter == null) {
            mAdapter = new DataAdapter(records);
            mListView.setAdapter(mAdapter);
            if (mListView.getVisibility() != View.VISIBLE) {
                mListView.setVisibility(View.VISIBLE);
            }
        } else {
            mAdapter.clear();
            mAdapter.addAll(records);
        }
        mAdapter.notifyDataSetChanged();
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
            Worker worker;
            String description;
            int nameVisibility = View.GONE;
            int descriptionVisibility = View.GONE;
            int statusVisibility = View.GONE;
            int downloadVisibility = View.GONE;
            int photoVisibility = View.GONE;
            switch (data.type) {
                case RECORD:
                    if (data instanceof RecordData) {
                        RecordData recordData = (RecordData) data;
                        worker = WorkingData.getInstance(getActivity()).getWorkerById(recordData.workerId);
                        // [TODO] should use String resource to perform multiple languages.
                        description = EmployeeActivityTypeInterpreter.getTranslation(recordData.tag) + recordData.description;
                        holder.avatar.setImageDrawable(worker.getAvator());
                        holder.name.setText(worker.name);
                        holder.description.setText(description);
                        nameVisibility = View.VISIBLE;
                        descriptionVisibility = View.VISIBLE;
                    }
                    break;
                case FILE:
                    if (data instanceof FileData) {
                        worker = WorkingData.getInstance(getActivity()).getWorkerById(((FileData) data).uploader);
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
                        final PhotoData photoData = (PhotoData) data;
                        worker = WorkingData.getInstance(getActivity()).getWorkerById(photoData.uploader);
                        holder.status.setText((worker != null ? worker.name + " " : "") +
                                getResources().getString(R.string.worker_ov_tab_status_capture) +
                                (TextUtils.isEmpty(photoData.fileName) ? "" : " " + photoData.fileName));
                        holder.photo.setImageDrawable(photoData.photo);
                        if (Uri.EMPTY != photoData.filePath) {
                            holder.photo.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Intent intent = new Intent();
                                    intent.setAction(android.content.Intent.ACTION_VIEW);
                                    File file = new File(photoData.filePath.getPath());
                                    intent.setDataAndType(Uri.fromFile(file), "image/*");
                                    startActivity(intent);
                                }
                            });
                        }
                        holder.avatar.setImageDrawable(getResources().getDrawable(R.drawable.ic_photo, null));
                        statusVisibility = View.VISIBLE;
                        photoVisibility = View.VISIBLE;
                    }
                    break;
                case HISTORY:
                    if (data instanceof HistoryData) {
                        HistoryData historyData = (HistoryData) data;
                        worker = WorkingData.getInstance(getActivity()).getWorkerById(historyData.workerId);
                        // [TODO] should use String resource to perform multiple languages.
                        description = EmployeeActivityTypeInterpreter.getTranslation(historyData.tag) + historyData.description;
                        holder.avatar.setImageDrawable(worker.getAvator());
                        holder.name.setText(worker.name);
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
                    if (mTabHost.getCurrentTab() == data.type.ordinal()) {
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
