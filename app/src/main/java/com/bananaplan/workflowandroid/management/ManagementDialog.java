package com.bananaplan.workflowandroid.management;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.bananaplan.workflowandroid.R;
import com.bananaplan.workflowandroid.data.IdData;
import com.bananaplan.workflowandroid.data.WorkingData;
import com.bananaplan.workflowandroid.utility.Utils;
import com.bananaplan.workflowandroid.utility.view.DividerItemDecoration;

import java.util.ArrayList;
import java.util.List;


public class ManagementDialog extends AppCompatActivity implements View.OnClickListener {

    private static final String EXTRA_MANAGEMENT_TYPE = "extra_management_type";

    public static final class ManagementType {
        public static final int EQUIPMENT = 0;
        public static final int FACTORY = 1;
        public static final int MANAGER_PIC = 2;
        public static final int WORKER_PIC = 3;
        public static final int VENDOR = 4;
    }

    public class ManagementItem {

        public boolean isShowDeleteButton = false;
        public IdData idData;

        public ManagementItem(IdData data) {
            this.idData = data;
        }
    }

    private ActionBar mActionBar;
    private TextView mActionBarTitleTextView;

    private ViewGroup mAddItemContainer;

    private RecyclerView mManagementList;
    private LinearLayoutManager mLinearLayoutManager;
    private ManagementListAdapter mManagementListAdapter;
    private List<ManagementItem> mManagementDataSet = new ArrayList<ManagementItem>();

    private EditText mAddEditText;
    private Button mAddButton;

    private Animation mAddItemContainerSlideOutAnim;
    private Animation mAddItemContainerSlideInAnim;
    private Animation mDeleteButtonFadeInAnim;
    private Animation mDeleteButtonFadeOutAnim;

    private String mActionBarTitleType = "";
    private int mManagementType;

    private boolean mIsInEditState = false;


    private class ManagementListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

        private Context mContext;
        private List<ManagementItem> mDataSet;


        private class ItemViewHolder extends RecyclerView.ViewHolder {

            public TextView name;
            public ImageView deleteButton;


            public ItemViewHolder(View v) {
                super(v);
                findViews(v);
            }

            private void findViews(View v) {
                name = (TextView) v.findViewById(R.id.management_item_name);
                deleteButton = (ImageView) v.findViewById(R.id.management_item_delete_button);
            }
        }

        public ManagementListAdapter(Context context, List<ManagementItem> dataSet) {
            mContext = context;
            mDataSet = dataSet;
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new ItemViewHolder(LayoutInflater.from(mContext).inflate(R.layout.management_item, parent, false));
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            ItemViewHolder itemViewHolder = (ItemViewHolder) holder;
            itemViewHolder.name.setText(mDataSet.get(position).idData.name);
            itemViewHolder.deleteButton.setVisibility(mDataSet.get(position).isShowDeleteButton ? View.VISIBLE : View.GONE);
        }

        @Override
        public int getItemCount() {
            return mDataSet.size();
        }
    }

    public static Intent showManagementDialog(Context context, int managementType) {
        Intent intent = new Intent(context, ManagementDialog.class);
        intent.putExtra(EXTRA_MANAGEMENT_TYPE, managementType);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_management);
        initialize(getIntent());
    }

    private void initialize(Intent intent) {
        mManagementType = intent.getIntExtra(EXTRA_MANAGEMENT_TYPE, ManagementType.WORKER_PIC);
        findViews();
        setupWindowSize();
        setupDatasAccordingToManagementType();
        setupActionBar();
        setupAnimations();
        setupAddItemContainer();
        setupManagementListDataSet();
        setupManagementList();
    }

    private void findViews() {
        mActionBarTitleTextView = (TextView) findViewById(R.id.management_actionbar_title);
        mAddItemContainer = (ViewGroup) findViewById(R.id.add_item_container);
        mManagementList = (RecyclerView) findViewById(R.id.management_list);
        mAddEditText = (EditText) findViewById(R.id.management_add_edit_text);
        mAddButton = (Button) findViewById(R.id.management_add_button);
    }

    private void setupWindowSize() {
        WindowManager.LayoutParams params = getWindow().getAttributes();
        int screenSize = getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK;

        if (screenSize == Configuration.SCREENLAYOUT_SIZE_LARGE ||
                screenSize == Configuration.SCREENLAYOUT_SIZE_XLARGE) {
            params.width = getResources().getDimensionPixelSize(R.dimen.management_dialog_window_width);
            params.height = getResources().getDimensionPixelSize(R.dimen.management_dialog_window_height);
        } else {
            params.width = WindowManager.LayoutParams.MATCH_PARENT;
            params.height = WindowManager.LayoutParams.MATCH_PARENT;
        }

        getWindow().setAttributes(params);
    }

    private void setupDatasAccordingToManagementType() {
        switch (mManagementType) {
            case ManagementType.EQUIPMENT:
                mActionBarTitleType = getString(R.string.management_equipment_text);
                mManagementDataSet = convertToManagementDataSet(WorkingData.getInstance(this).getEquipments());
                break;
            case ManagementType.FACTORY:
                mActionBarTitleType = getString(R.string.management_factory_text);
                mManagementDataSet = convertToManagementDataSet(WorkingData.getInstance(this).getFactories());
                break;
            case ManagementType.MANAGER_PIC:
                mActionBarTitleType = getString(R.string.management_manager_pic_text);
                mManagementDataSet = convertToManagementDataSet(WorkingData.getInstance(this).getManagers());
                break;
            case ManagementType.WORKER_PIC:
                mActionBarTitleType = getString(R.string.management_worker_pic_text);
                mManagementDataSet = convertToManagementDataSet(WorkingData.getInstance(this).getWorkers());
                break;
            case ManagementType.VENDOR:
                mActionBarTitleType = getString(R.string.management_vendor_text);
                mManagementDataSet = convertToManagementDataSet(WorkingData.getInstance(this).getVendors());
                break;
        }
    }

    private List<ManagementItem> convertToManagementDataSet(ArrayList<? extends IdData> dataSet) {
        List<ManagementItem> managementDataSet = new ArrayList<ManagementItem>();
        for (IdData id : dataSet) {
            managementDataSet.add(new ManagementItem(id));
        }

        return managementDataSet;
    }

    private void setupActionBar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        mActionBar = getSupportActionBar();

        if (mActionBar != null) {
            mActionBar.setDisplayShowTitleEnabled(false);
            mActionBarTitleTextView.setText(
                    String.format(getString(R.string.management_actionbar_title_text), mActionBarTitleType));
        }
    }

    private void setupAnimations() {
        mAddItemContainerSlideOutAnim = AnimationUtils.loadAnimation(this, R.anim.management_dialog_add_item_container_slide_out);
        mAddItemContainerSlideOutAnim.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                showDeleteButton();
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                mAddEditText.setText("");
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

        mAddItemContainerSlideInAnim = AnimationUtils.loadAnimation(this, R.anim.management_dialog_add_item_container_slide_in);
        mAddItemContainerSlideInAnim.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                showDeleteButton();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

        mDeleteButtonFadeInAnim = AnimationUtils.loadAnimation(this, R.anim.management_dialog_delete_button_fade_in);
        mDeleteButtonFadeInAnim.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                mManagementListAdapter.notifyDataSetChanged();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

        mDeleteButtonFadeOutAnim = AnimationUtils.loadAnimation(this, R.anim.management_dialog_delete_button_fade_out);
        mDeleteButtonFadeOutAnim.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                mManagementListAdapter.notifyDataSetChanged();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
    }

    private void setupAddItemContainer() {
        mAddEditText.setHint(String.format(getString(R.string.management_add_edit_text_hint), mActionBarTitleType));
        mAddButton.setOnClickListener(this);
    }

    private void setupManagementListDataSet() {

    }

    private void setupManagementList() {
        mLinearLayoutManager = new LinearLayoutManager(this);
        mLinearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mManagementListAdapter = new ManagementListAdapter(this, mManagementDataSet);

        mManagementList.setLayoutManager(mLinearLayoutManager);
        mManagementList.addItemDecoration(new DividerItemDecoration(getResources().getDrawable(R.drawable.drawer_divider),
                false, true));
        mManagementList.setAdapter(mManagementListAdapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_management, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        MenuItem editMenu = menu.findItem(R.id.edit);
        MenuItem dismissMenu = menu.findItem(R.id.dismiss);

        editMenu.setVisible(!mIsInEditState);
        dismissMenu.setVisible(mIsInEditState);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id) {
            case R.id.dismiss:
                Utils.hideSoftKeyboard(this);
                mAddItemContainer.startAnimation(mAddItemContainerSlideOutAnim);
                mAddItemContainer.setVisibility(View.GONE);
                mIsInEditState = false;
                invalidateOptionsMenu();

                return true;

            case R.id.edit:
                mAddItemContainer.startAnimation(mAddItemContainerSlideInAnim);
                mAddItemContainer.setVisibility(View.VISIBLE);
                mIsInEditState = true;
                invalidateOptionsMenu();

                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void showDeleteButton() {
        // Apply animation on visible items in RecylerView
        for (int i = 0 ; i < mLinearLayoutManager.getItemCount() ; i++) {
            mManagementDataSet.get(i).isShowDeleteButton = mIsInEditState;
            View listItem = mManagementList.getChildAt(i);
            if (listItem != null) {
                ImageView deleteButton = (ImageView) listItem.findViewById(R.id.management_item_delete_button);
                deleteButton.startAnimation(mIsInEditState ? mDeleteButtonFadeInAnim : mDeleteButtonFadeOutAnim);
                deleteButton.setVisibility(mIsInEditState ? View.VISIBLE : View.GONE);
            }
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.management_add_button:
                break;
        }
    }
}
