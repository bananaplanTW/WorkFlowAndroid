package com.bananaplan.workflowandroid.utility.data;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.bananaplan.workflowandroid.R;
import com.bananaplan.workflowandroid.data.IdData;
import com.bananaplan.workflowandroid.data.Warning;

import java.util.List;

/**
 * Created by daz on 10/28/15.
 */
public class TextSpinnerAdapter<T> extends ArrayAdapter {

    private class ItemViewHolder {
        public TextView spinnerText;
        public ItemViewHolder (View v) {
            spinnerText = (TextView) v.findViewById(R.id.spinner_text);
        }
    }


    private LayoutInflater mLayoutInflater;


    public TextSpinnerAdapter(Context context, int resource, List<T> objects) {
        super(context, resource, objects);
        mLayoutInflater = LayoutInflater.from(context);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ItemViewHolder holder;

        if (convertView == null) {
            convertView = mLayoutInflater.inflate(R.layout.text_spinner_item, parent, false);
            holder = new ItemViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (ItemViewHolder) convertView.getTag();
        }

        holder.spinnerText.setText( ((IdData) getItem(position)).name );
        return convertView;
    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        ItemViewHolder holder;

        if (convertView == null) {
            convertView = mLayoutInflater.inflate(R.layout.text_spinner_item, null);
            holder = new ItemViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (ItemViewHolder) convertView.getTag();
        }
        holder.spinnerText.setText( ((IdData) getItem(position)).name );
        return convertView;
    }
}
