package com.bananaplan.workflowandroid.utility;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bananaplan.workflowandroid.R;

import java.util.ArrayList;
import java.util.Objects;


/**
 * Template of spinner style
 * The spinner style is: Icon + Content + Arrow
 *
 * You can set Icon you want by calling setSpinnerIcon() BEFORE binding this adapter to adapter view.
 *
 * @author Danny Lin
 * @since 2015/7/24.
 */
public abstract class IconSpinnerAdapter<T> extends ArrayAdapter {

    private LayoutInflater mLayoutInflater;

    private class ItemViewHolder {

        public ImageView spinnerIcon;
        public TextView spinnerText;


        public ItemViewHolder(View v) {
            spinnerIcon = (ImageView) v.findViewById(R.id.spinner_icon);
            spinnerText = (TextView) v.findViewById(R.id.spinner_text);
            spinnerIcon.setImageResource(getSpinnerIconResourceId());
        }
    }

    public IconSpinnerAdapter(Context context, int resource, ArrayList<T> objects) {
        super(context, resource, objects);
        mLayoutInflater = LayoutInflater.from(context);
    }

    public IconSpinnerAdapter(Context context, int resource, T[] objects) {
        super(context, resource, objects);
        mLayoutInflater = LayoutInflater.from(context);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        ItemViewHolder holder;

        if (view == null) {
            view = mLayoutInflater.inflate(R.layout.icon_spinner_item, parent, false);
            holder = new ItemViewHolder(view);
            view.setTag(holder);
        } else {
            holder = (ItemViewHolder) view.getTag();
        }

        holder.spinnerText.setText(getSpinnerViewDisplayString(position));

        return view;
    }

    public abstract String getSpinnerViewDisplayString(int position);
    public abstract int getSpinnerIconResourceId();

    public LayoutInflater getLayoutInflater() {
        return mLayoutInflater;
    }
}
