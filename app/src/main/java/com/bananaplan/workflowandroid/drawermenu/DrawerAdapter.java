package com.bananaplan.workflowandroid.drawermenu;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bananaplan.workflowandroid.R;
import com.bananaplan.workflowandroid.data.Manager;
import com.bananaplan.workflowandroid.data.WorkingData;
import com.bananaplan.workflowandroid.utility.view.ExpandableLayout;
import com.bananaplan.workflowandroid.utility.view.ExpandableLayout.OnExpandCollapseListener;

import java.util.ArrayList;

/**
 * Adatper for drawer
 *
 * @author Danny Lin
 * @since 2015/8/8.
 */
public class DrawerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements View.OnClickListener {

    private static final String TAG = "DrawerAdapter";

    private Context mContext;
    private ArrayList<DrawerItem> mDrawerItemDatas = new ArrayList<DrawerItem>();

    private OnClickDrawerItemListener mOnClickDrawerItemListener;

    private ExpandableLayout mLastExpandedGroup = null;
    private View mLastSelectedItem = null;
    private View mLastSelectedGroupHeaderItem = null;

    private String mManagerName;

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
        public static final int SIZE = 7;
        public static final int SETTING = 0;
        public static final int INFO = 1;
        public static final int ASSIGN_TASK = 2;
        public static final int CASE = 3;
        public static final int WORKER = 4;
        public static final int EQUIPMENT = 5;
        public static final int WARNING = 6;
    }

    private class BaseViewHolder extends RecyclerView.ViewHolder {

        public View view;
        public TextView text;
        public ImageView licon;

        public BaseViewHolder(View v) {
            super(v);
            view = v;
            licon = (ImageView) v.findViewById(R.id.left_icon);
            text = (TextView) v.findViewById(R.id.drawer_item_text);
        }
    }

    private final class SettingViewHolder extends BaseViewHolder {

        public ImageView settingIcon;

        public SettingViewHolder(View v) {
            super(v);
            settingIcon = (ImageView) v.findViewById(R.id.drawer_setting_button);
        }
    }

    private final class InfoViewHolder extends BaseViewHolder {

        public TextView number;


        public InfoViewHolder(View v) {
            super(v);
            number = (TextView) v.findViewById(R.id.drawer_item_info_count);
        }
    }

    private final class GroupViewHolder extends BaseViewHolder {

        public ViewGroup subItemsContainer;


        public GroupViewHolder(View v) {
            super(v);
            subItemsContainer = (ViewGroup) v.findViewById(R.id.drawer_subitems_container);
        }
    }


    public DrawerAdapter(Context context, OnClickDrawerItemListener listener) {
        mContext = context;
        mOnClickDrawerItemListener = listener;
        Manager manager = WorkingData.getInstance(mContext).getManagerById(WorkingData.getUserId());
        if (manager != null) {
            mManagerName = manager.name;
        }
        setupDrawerItemDatas();
    }

    private void setupDrawerItemDatas() {
        for (int i = 0 ; i < DrawerItemIndex.SIZE ; i++) {
            DrawerItem item = null;

            switch (i) {
                case DrawerItemIndex.SETTING:
                    item = DrawerItem.generateSettingItem(null, mManagerName);
                    break;

                case DrawerItemIndex.INFO:
                    item = DrawerItem.generateInfoItem(R.id.drawer_info, 30);
                    break;

                case DrawerItemIndex.ASSIGN_TASK:
                    item = DrawerItem.generateNormalItem(R.id.drawer_assign_task,
                                                         mContext.getString(R.string.drawer_assign_task),
                                                         mContext.getDrawable(R.drawable.selector_drawer_assign_task_icon));
                    break;

                case DrawerItemIndex.CASE:
                    item = DrawerItem.generateNormalItem(R.id.drawer_case_overview,
                                                         mContext.getString(R.string.drawer_case_overview),
                                                         mContext.getDrawable(R.drawable.selector_drawer_case_icon));
//                    item = DrawerItem.generateGroupItem(mContext.getString(R.string.drawer_case),
//                                                        mContext.getDrawable(R.drawable.selector_drawer_case_icon),
//                                                        generateDrawerSubItems(i));
                    break;

                case DrawerItemIndex.WORKER:
                    item = DrawerItem.generateNormalItem(R.id.drawer_worker_overview,
                                                         mContext.getString(R.string.drawer_worker_overview),
                                                         mContext.getDrawable(R.drawable.selector_drawer_worker_icon));
//                    item = DrawerItem.generateGroupItem(mContext.getString(R.string.drawer_worker),
//                                                        mContext.getDrawable(R.drawable.selector_drawer_worker_icon),
//                                                        generateDrawerSubItems(i));
                    break;

                case DrawerItemIndex.EQUIPMENT:
                    item = DrawerItem.generateNormalItem(R.id.drawer_equipment_overview,
                                                         mContext.getString(R.string.drawer_equipment_overview),
                                                         mContext.getDrawable(R.drawable.selector_drawer_equipment_icon));
//                    item = DrawerItem.generateGroupItem(mContext.getString(R.string.drawer_equipment),
//                                                        mContext.getDrawable(R.drawable.selector_drawer_equipment_icon),
//                                                        generateDrawerSubItems(i));
                    break;

                case DrawerItemIndex.WARNING:
                    item = DrawerItem.generateNormalItem(R.id.drawer_warning,
                                                         mContext.getString(R.string.drawer_warning),
                                                         mContext.getDrawable(R.drawable.selector_drawer_warning_icon));
                    break;
            }

            mDrawerItemDatas.add(item);
        }
    }

    private DrawerSubItem[] generateDrawerSubItems(int drawerIndex) {
        String[] subItemTitles = null;
        int[] subItemClickIds = null;
        DrawerSubItem[] subItems = null;

        switch (drawerIndex) {
            case DrawerItemIndex.CASE:
                subItemTitles = new String[] {mContext.getString(R.string.drawer_case_overview),
                                              mContext.getString(R.string.drawer_add_case)};
                subItemClickIds = new int[] {R.id.drawer_case_overview,
                                             R.id.drawer_add_case};
                subItems = new DrawerSubItem[] {new DrawerSubItem(subItemTitles[0], subItemClickIds[0]),
                                                new DrawerSubItem(subItemTitles[1], subItemClickIds[1])};
                break;

            case DrawerItemIndex.WORKER:
                subItemTitles = new String[] {mContext.getString(R.string.drawer_worker_overview),
                                              mContext.getString(R.string.drawer_add_worker)};
                subItemClickIds = new int[] {R.id.drawer_worker_overview,
                                             R.id.drawer_add_worker};
                subItems = new DrawerSubItem[] {new DrawerSubItem(subItemTitles[0], subItemClickIds[0]),
                                                new DrawerSubItem(subItemTitles[1], subItemClickIds[1])};
                break;

            case DrawerItemIndex.EQUIPMENT:
                subItemTitles = new String[] {mContext.getString(R.string.drawer_equipment_overview),
                                              mContext.getString(R.string.drawer_add_equipment)};
                subItemClickIds = new int[] {R.id.drawer_equipment_overview,
                                             R.id.drawer_add_equipment};
                subItems = new DrawerSubItem[] {new DrawerSubItem(subItemTitles[0], subItemClickIds[0]),
                                                new DrawerSubItem(subItemTitles[1], subItemClickIds[1])};
                break;
        }

        return subItems;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder viewHolder = null;
        View v = null;

        switch (viewType) {
            case DrawerItem.LayoutTemplate.SETTING:
                v = LayoutInflater.from(mContext).inflate(R.layout.drawer_item_setting, parent, false);
                return new SettingViewHolder(v);

            case DrawerItem.LayoutTemplate.INFO:
                v = LayoutInflater.from(mContext).inflate(R.layout.drawer_item_info, parent, false);
                return new InfoViewHolder(v);

            case DrawerItem.LayoutTemplate.NORMAL:
                v = LayoutInflater.from(mContext).inflate(R.layout.drawer_item_normal, parent, false);
                return new BaseViewHolder(v);

            case DrawerItem.LayoutTemplate.GROUP:
                v = LayoutInflater.from(mContext).inflate(R.layout.drawer_item_group, parent, false);
                return new GroupViewHolder(v);
        }

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        switch (mDrawerItemDatas.get(position).layoutTemplate) {
            case DrawerItem.LayoutTemplate.SETTING:
                onBindSettingViewHolder((SettingViewHolder) holder, position);
                break;
            case DrawerItem.LayoutTemplate.INFO:
                onBindInfoViewHolder((InfoViewHolder) holder, position);
                break;
            case DrawerItem.LayoutTemplate.NORMAL:
                onBindNormalViewHolder((BaseViewHolder) holder, position);
                break;
            case DrawerItem.LayoutTemplate.GROUP:
                onBindGroupViewHolder((GroupViewHolder) holder, position);
                break;
        }
    }

    private void onBindSettingViewHolder(SettingViewHolder holder, int position) {
        if (mDrawerItemDatas.get(position).leftIcon != null) {
            holder.licon.setImageDrawable(mDrawerItemDatas.get(position).leftIcon);
        }
        holder.text.setText(mDrawerItemDatas.get(position).text);
        holder.settingIcon.setOnClickListener(this);
    }

    private void onBindInfoViewHolder(InfoViewHolder holder, int position) {
        // Default drawer item when launch app
        setSelectedItem(holder.view, false);

        int number = mDrawerItemDatas.get(position).infoCount;

        if (number != 0) {
            holder.number.setText(String.valueOf(number));
        }
        if (mDrawerItemDatas.get(position).clickId != -1) {
            holder.view.setId(mDrawerItemDatas.get(position).clickId);
            holder.view.setOnClickListener(this);
        }
    }

    private void onBindNormalViewHolder(BaseViewHolder holder, int position) {
        holder.licon.setImageDrawable(mDrawerItemDatas.get(position).leftIcon);
        holder.text.setText(mDrawerItemDatas.get(position).text);

        if (mDrawerItemDatas.get(position).clickId != -1) {
            holder.view.setId(mDrawerItemDatas.get(position).clickId);
            holder.view.setOnClickListener(this);
        }
    }

    private void onBindGroupViewHolder(GroupViewHolder holder, int position) {
        holder.licon.setImageDrawable(mDrawerItemDatas.get(position).leftIcon);
        holder.text.setText(mDrawerItemDatas.get(position).text);

        ((ExpandableLayout) holder.view).setOnExpandCollapseListener(mOnExpandCollapseListener);

        // Subitems
        if (mDrawerItemDatas.get(position).subItems != null) {
            int length = mDrawerItemDatas.get(position).subItems.length;

            for (int i = 0 ; i < length ; i++) {
                View view = LayoutInflater.from(mContext).
                        inflate(R.layout.drawer_subitem, holder.subItemsContainer, false);
                view.setTag(holder.view);
                TextView textView = (TextView) view.findViewById(R.id.drawer_subitem);
                textView.setText(mDrawerItemDatas.get(position).subItems[i].text);

                int clickId = mDrawerItemDatas.get(position).subItems[i].clickId;
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
        return mDrawerItemDatas.get(position).layoutTemplate;
    }

    @Override
    public int getItemCount() {
        return mDrawerItemDatas.size();
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id != R.id.drawer_setting_button) {
            setSelectedItem(v, false);
//            if (id == R.id.drawer_info || id == R.id.drawer_assign_task || id == R.id.drawer_warning) {
//                setSelectedItem(v, false);
//            } else {
//                // For group item
//                setSelectedItem(v, true);
//            }
        }

        mOnClickDrawerItemListener.onClickDrawerItem(id);
    }

    private void setSelectedItem(View v, boolean isSubitem) {
        if (mLastSelectedGroupHeaderItem != null) {
            mLastSelectedGroupHeaderItem.setSelected(false);
        }
        if (mLastSelectedItem != null) {
            mLastSelectedItem.setSelected(false);
        }

        if (isSubitem) {
            ViewGroup header = ((ExpandableLayout) v.getTag()).getHeaderLayout();
            header.setSelected(true);
            mLastSelectedGroupHeaderItem = header;
        }
        v.setSelected(true);
        mLastSelectedItem = v;
    }
}
