package com.citylifeapps.actionsendgridview;

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
public class GridAdapter extends BaseAdapter
{
    private final List<IntentShare> couponShareList;

    public GridAdapter(List<IntentShare> couponShareList) {
        super();

        this.couponShareList=couponShareList;

    }

    @Override
    public int getCount() {
        return couponShareList.size();
    }

    @Override
    public Object getItem(int position) {
        return couponShareList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    private class ViewHolder {
        //ImageView icon;
        //AutofitTextView label;
        TextView label;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder holder=new ViewHolder();

        if (convertView == null) {

            final LayoutInflater mInflater = LayoutInflater.from(parent.getContext());
            convertView=   mInflater.inflate(R.layout.gridview_item, parent, false);

            holder.label=(TextView) convertView.findViewById(R.id.intent_label);

            convertView.setTag(holder);
        } else {
            holder.label = (TextView) convertView.getTag();
        }

        holder.label.setTag(convertView);

        final float scale = parent.getContext().getResources().getDisplayMetrics().density;
        int padding = (int) (10 * scale + 0.5f);

        holder.label.setMaxLines(2);
        holder.label.setGravity(Gravity.CENTER);
        holder.label.setPadding(0,padding,0,0);


        IntentShare intentShare=couponShareList.get(position);

        if (intentShare!=null) {
            holder.label.setText(intentShare.getLabel());
            holder.label.setCompoundDrawablesWithIntrinsicBounds(null, intentShare.getBitmap(), null, null);
        }

        return convertView;
    }

}