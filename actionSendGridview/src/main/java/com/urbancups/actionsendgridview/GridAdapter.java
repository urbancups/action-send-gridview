package com.urbancups.actionsendgridview;

import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

/**
 * Created by Yonatan Moskovich on 07/09/2014
 */
public class GridAdapter extends BaseAdapter {
    private List<IntentShare> couponShareList;
    private final float scale;

    public GridAdapter(float scale) {
        super();
        this.scale=scale;
    }

    public void setAdapterList(List<IntentShare> couponShareList) {
        this.couponShareList=couponShareList;
    }

    @Override
    public int getCount() {
        if (couponShareList!=null) {
            return couponShareList.size();
        } else {
            return 0;
        }
    }

    @Override
    public Object getItem(int position) {
        return couponShareList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        TextView intentPlaceholder;

        if (convertView == null) {

            final LayoutInflater mInflater = LayoutInflater.from(parent.getContext());
            convertView = mInflater.inflate(R.layout.gridview_item, parent, false);

            intentPlaceholder = (TextView) convertView.findViewById(R.id.intent_label);

            convertView.setTag(intentPlaceholder);
        } else {
            intentPlaceholder = (TextView) convertView.getTag();
        }

        intentPlaceholder.setTag(convertView);

        int padding = (int) (10 * scale + 0.5f);

        intentPlaceholder.setMaxLines(2);
        intentPlaceholder.setGravity(Gravity.CENTER);
        intentPlaceholder.setPadding(0, padding, 0, 0);

        IntentShare intentShare = couponShareList.get(position);

        if (intentShare != null) {
            intentPlaceholder.setText(intentShare.getLabel());
            intentPlaceholder.setCompoundDrawablesWithIntrinsicBounds(null, intentShare.getBitmap(), null, null);
        }

        return convertView;
    }

}