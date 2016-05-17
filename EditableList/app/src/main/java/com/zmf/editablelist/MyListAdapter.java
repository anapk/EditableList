package com.zmf.editablelist;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

/**
 * Created by zimengfang on 16/3/27.
 */
public class MyListAdapter extends BaseAdapter {
    private List<DataEntity> mList;
    private Context context;
    private LayoutInflater mInflater;

    public MyListAdapter(List<DataEntity> mList, Context context) {
        this.mList = mList;
        this.context = context;
        mInflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return mList == null ? 0 : mList.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final ViewHolder holder;
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.activity_main_item, null);
            holder = new ViewHolder();
            holder.code = (TextView) convertView.findViewById(R.id.stock_code);
            holder.name = (TextView) convertView.findViewById(R.id.stock_name);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        holder.name.setText(mList.get(position).getSecshortname());
        holder.code.setText(mList.get(position).getTicker());

        return convertView;
    }

    static class ViewHolder {
        TextView name;
        TextView code;

    }
}
