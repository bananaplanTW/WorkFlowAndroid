package com.bananaplan.workflowandroid.drawermenu;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bananaplan.workflowandroid.R;
import com.bananaplan.workflowandroid.utility.ExpandableLayout;
import com.bananaplan.workflowandroid.utility.ExpandableLayout.OnExpandCollapseListener;

import java.util.ArrayList;

/**
 * @author Danny Lin
 * @since 2015/8/8.
 */
public class DrawerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements View.OnClickListener {

    private static final String TAG = "DrawerAdapter";

    private Context mContext;
    private ArrayList<DrawerItem> mDrawerDatas = new ArrayList<DrawerItem>();

    private ExpandableLayout mLastExpandedGroup = null;

    private OnExpandCollapseListener mOnExpandCollapseListener = new OnExpandCollapseListener() {
        @Override
        public void onExpand(ExpandableLayout v) {
            if (mLastExpandedGroup == null) {
                mLastExpandedGroup = v;
                return;
            }
            if (mLastExpandedGroup == v) return;
            mLastExpandedGroup.hide();
            mLastExpandedGroup = v;
        }

        @Override
        public void onCollapse(ExpandableLayout v) {

        }
    };

    private static final class DrawerItemIndex {
        public static final int SIZE = 6;
        public static final int SETTING = 0;
        public static final int MAIN = 1;
        public static final int ASSIGN_TASK = 2;
        public static final int CASE = 3;
        public static final int WORKER = 4;
        public static final int EQUIPMENT = 5;
    }

    private final class SettingViewHolder extends RecyclerView.ViewHolder {

        public ImageView avatar;
        public TextView text;
        public ImageView settingIcon;

        public SettingViewHolder(View v) {
            super(v);
            avatar = (ImageView) v.findViewById(R.id.avatar);
            text = (TextView) v.findViewById(R.id.drawer_setting_text);
            settingIcon = (ImageView) v.findViewById(R.id.drawer_setting);
        }
    }

    private class BaseViewHolder extends RecyclerView.ViewHolder {

        public View view;
        public TextView text;
        public ImageView licon;

        public BaseViewHolder(View v) {
            super(v);
            view = v;
            text = (TextView) v.findViewById(R.id.drawer_item_text);
            licon = (ImageView) v.findViewById(R.id.left_icon);
        }
    }

    private final class LRIconViewHolder extends BaseViewHolder {

        public ImageView ricon;

        public LRIconViewHolder(View v) {
            super(v);
            ricon = (ImageView) v.findViewById(R.id.right_icon);
        }
    }

    private final class InfoCountViewHolder extends BaseViewHolder {

        public TextView number;


        public InfoCountViewHolder(View v) {
            super(v);
            number = (TextView) v.findViewById(R.id.drawer_item_info_count);
        }
    }

    private final class GroupViewHolder extends BaseViewHolder {

        public ImageView ricon;
        public ViewGroup subItemsContainer;


        public GroupViewHolder(View v) {
            super(v);
            ((ExpandableLayout) v).setOnExpandCollapseListener(mOnExpandCollapseListener);
            ricon = (ImageView) v.findViewById(R.id.right_icon);
            subItemsContainer = (ViewGroup) v.findViewById(R.id.drawer_subitems_container);
        }
    }


    public DrawerAdapter(Context context) {
        mContext = context;
        setupDatas();
    }

    private void setupDatas() {
        for (int i = 0 ; i < DrawerItemIndex.SIZE ; i++) {
            DrawerItem item = null;
            String[] subItemTitles = null;
            int[] subItemClickIds = null;
            DrawerSubItem[] subItems = null;


            switch (i) {
                case DrawerItemIndex.SETTING:
                    item = new DrawerItem(null, "Danny");
                    break;

                case DrawerItemIndex.MAIN:
                    item = new DrawerItem(R.id.drawer_main,
                                          mContext.getString(R.string.drawer_main),
                                          mContext.getDrawable(R.drawable.drawer_info),
                                          30);
                    break;

                case DrawerItemIndex.ASSIGN_TASK:
                    item = new DrawerItem(R.id.drawer_assign_task,
                                          mContext.getString(R.string.drawer_assign_task),
                                          mContext.getDrawable(R.drawable.drawer_dispatch));
                    break;

                case DrawerItemIndex.CASE:
                    subItemTitles = new String[] {mContext.getString(R.string.drawer_case_overview),
                                                  mContext.getString(R.string.drawer_add_case)};
                    subItemClickIds = new int[] {R.id.drawer_case_overview,
                                                 R.id.drawer_add_case};
                    subItems = new DrawerSubItem[] {new DrawerSubItem(subItemTitles[0], subItemClickIds[0]),
                                                    new DrawerSubItem(subItemTitles[1], subItemClickIds[1])};

                    item = new DrawerItem(mContext.getString(R.string.drawer_case),
                                          mContext.getDrawable(R.drawable.drawer_case),
                                          mContext.getDrawable(R.drawable.drawer_more),
                                          subItems);
                    break;

                case DrawerItemIndex.WORKER:
                    subItemTitles = new String[] {mContext.getString(R.string.drawer_worker_overview),
                                                  mContext.getString(R.string.drawer_add_worker)};
                    subItemClickIds = new int[] {R.id.drawer_worker_overview,
                                                 R.id.drawer_add_worker};
                    subItems = new DrawerSubItem[] {new DrawerSubItem(subItemTitles[0], subItemClickIds[0]),
                                                    new DrawerSubItem(subItemTitles[1], subItemClickIds[1])};

                    item = new DrawerItem(mContext.getString(R.string.drawer_worker),
                                          mContext.getDrawable(R.drawable.drawer_worker),
                                          mContext.getDrawable(R.drawable.drawer_more),
                                          subItems);
                    break;

                case DrawerItemIndex.EQUIPMENT:
                    subItemTitles = new String[] {mContext.getString(R.string.drawer_equipment_overview),
                                                  mContext.getString(R.string.drawer_add_equipment)};
                    subItemClickIds = new int[] {R.id.drawer_equipment_overview,
                                                 R.id.drawer_add_equipment};
                    subItems = new DrawerSubItem[] {new DrawerSubItem(subItemTitles[0], subItemClickIds[0]),
                                                    new DrawerSubItem(subItemTitles[1], subItemClickIds[1])};

                    item = new DrawerItem(mContext.getString(R.string.drawer_equipment),
                                          mContext.getDrawable(R.drawable.drawer_equipment),
                                          mContext.getDrawable(R.drawable.drawer_more),
                                          subItems);
                    break;
            }

            mDrawerDatas.add(item);
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder viewHolder = null;
        View v = null;

        switch (viewType) {
            case DrawerItem.LayoutTemplate.SETTING:
                v = LayoutInflater.from(mContext).inflate(R.layout.drawer_item_setting, parent, false);
                return new SettingViewHolder(v);

            case DrawerItem.LayoutTemplate.LR_ICON:
                v = LayoutInflater.from(mContext).inflate(R.layout.drawer_item_lr_icon, parent, false);
                return new LRIconViewHolder(v);

            case DrawerItem.LayoutTemplate.INFO_COUNT:
                v = LayoutInflater.from(mContext).inflate(R.layout.drawer_item_info_count, parent, false);
                return new InfoCountViewHolder(v);

            case DrawerItem.LayoutTemplate.GROUP:
                v = LayoutInflater.from(mContext).inflate(R.layout.drawer_item_group, parent, false);
                return new GroupViewHolder(v);
        }

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        DrawerItem.Type type = mDrawerDatas.get(position).type;

        if (DrawerItem.Type.SETTING == type) {
            onBindSettingViewHolder((SettingViewHolder) holder, position);
        } else {
            BaseViewHolder baseViewHolder = (BaseViewHolder) holder;
            baseViewHolder.text.setText(mDrawerDatas.get(position).text);
            baseViewHolder.licon.setImageDrawable(mDrawerDatas.get(position).leftIcon);

            switch (mDrawerDatas.get(position).type) {
                case INFO_COUNT:
                    onBindInfoCountViewHolder((InfoCountViewHolder) baseViewHolder, position);
                    break;
                case NORMAL:
                    onBindNormalViewHolder((LRIconViewHolder) baseViewHolder, position);
                    break;
                case GROUP:
                    onBindGroupViewHolder((GroupViewHolder) baseViewHolder, position);
                    break;
            }
        }
    }

    private void onBindSettingViewHolder(SettingViewHolder holder, int position) {
        if (mDrawerDatas.get(position).leftIcon != null) {
            holder.avatar.setImageDrawable(mDrawerDatas.get(position).leftIcon);
        }
        holder.text.setText(mDrawerDatas.get(position).text);
        holder.settingIcon.setOnClickListener(this);
    }

    private void onBindInfoCountViewHolder(InfoCountViewHolder holder, int position) {
        int number = mDrawerDatas.get(position).infoCount;
        holder.number.setTextColor(mContext.getResources().getColor(R.color.drawer_item_info_count_text_color));
        if (number != 0) {
            holder.number.setText(String.valueOf(number));
        }
        if (mDrawerDatas.get(position).clickId != -1) {
            holder.view.setId(mDrawerDatas.get(position).clickId);
            holder.view.setOnClickListener(this);
        }
    }

    private void onBindNormalViewHolder(LRIconViewHolder holder, int position) {
        holder.ricon.setVisibility(View.GONE);
        if (mDrawerDatas.get(position).clickId != -1) {
            holder.view.setId(mDrawerDatas.get(position).clickId);
            holder.view.setOnClickListener(this);
        }
    }

    private void onBindGroupViewHolder(GroupViewHolder holder, int position) {
        if (mDrawerDatas.get(position).subItems != null) {
            int length = mDrawerDatas.get(position).subItems.length;

            for (int i = 0 ; i < length ; i++) {
                View view = LayoutInflater.from(mContext).
                        inflate(R.layout.drawer_subitem, holder.subItemsContainer, false);
                TextView textView = (TextView) view.findViewById(R.id.drawer_subitem);
                textView.setText(mDrawerDatas.get(position).subItems[i].text);

                int clickId = mDrawerDatas.get(position).subItems[i].clickId;
                if (clickId != -1) {
                    view.setId(clickId);
                    view.setOnClickListener(this);
                }

                holder.subItemsContainer.addView(view);
                if (i != length - 1) {
                    holder.subItemsContainer.addView(LayoutInflater.from(mContext).
                            inflate(R.layout.drawer_divider, holder.subItemsContainer, false));
                }
            }
        }
    }

    @Override
    public int getItemViewType(int position) {
        return mDrawerDatas.get(position).layoutTemplate;
    }

    @Override
    public int getItemCount() {
        return mDrawerDatas.size();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.drawer_setting:
                Toast.makeText(mContext, "Settings", Toast.LENGTH_SHORT).show();
                break;
            case R.id.drawer_main:
                Toast.makeText(mContext, "Main", Toast.LENGTH_SHORT).show();
                break;
            case R.id.drawer_assign_task:
                Toast.makeText(mContext, "AssignTask", Toast.LENGTH_SHORT).show();
                break;
            case R.id.drawer_case_overview:
                Toast.makeText(mContext, "CaseOverview", Toast.LENGTH_SHORT).show();
                break;
            case R.id.drawer_add_case:
                Toast.makeText(mContext, "AddCase", Toast.LENGTH_SHORT).show();
                break;
            case R.id.drawer_worker_overview:
                Toast.makeText(mContext, "WorkerOverview", Toast.LENGTH_SHORT).show();
                break;
            case R.id.drawer_add_worker:
                Toast.makeText(mContext, "AddWorker", Toast.LENGTH_SHORT).show();
                break;
            case R.id.drawer_equipment_overview:
                Toast.makeText(mContext, "EquipmentOverview", Toast.LENGTH_SHORT).show();
                break;
            case R.id.drawer_add_equipment:
                Toast.makeText(mContext, "AddEquipment", Toast.LENGTH_SHORT).show();
                break;
        }
    }
}
