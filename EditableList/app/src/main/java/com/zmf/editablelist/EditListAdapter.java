/*
 * Copyright (C) 2015 Paul Burke
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.zmf.editablelist;

import android.graphics.Color;
import android.support.v4.view.MotionEventCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.zmf.editablelist.helper.ItemTouchHelperAdapter;
import com.zmf.editablelist.helper.ItemTouchHelperViewHolder;
import com.zmf.editablelist.helper.OnStartDragListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import de.greenrobot.event.EventBus;

/**
 * Simple RecyclerView.Adapter that implements {@link ItemTouchHelperAdapter} to respond to move and
 * dismiss events from a {@link android.support.v7.widget.helper.ItemTouchHelper}.
 *
 * @author Paul Burke (ipaulpro)
 */
public class EditListAdapter extends RecyclerView.Adapter<EditListAdapter.ItemViewHolder>
        implements ItemTouchHelperAdapter {

    private List<DataEntity> mItems;
    private List<DataEntity> selected;

    private final OnStartDragListener mDragStartListener;
    public HashMap<Integer, Boolean> map;
    private EventBus eventBus;

    public EditListAdapter(List<DataEntity> mItems, OnStartDragListener dragStartListener, EventBus eventBus) {
        mDragStartListener = dragStartListener;
        this.mItems = mItems;
        this.eventBus = eventBus;
        map = new HashMap<>();
        selected = new ArrayList<>();
        init();
    }

    private void init() {
        if (null == mItems || mItems.size() <= 0) {
            return;
        }
        for (int i = 0, p = mItems.size(); i < p; i++) {
            map.put(i, false);
        }
    }

    @Override
    public ItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_edit_item, parent, false);
        ItemViewHolder itemViewHolder = new ItemViewHolder(view);
        return itemViewHolder;
    }

    @Override
    public void onBindViewHolder(final ItemViewHolder holder, final int position) {
        if (null == mItems || mItems.size() <= 0) {
            return;
        }
        holder.stockName.setText(mItems.get(position).getSecshortname());
        holder.stockCode.setText(mItems.get(position).getTicker());

        // Start a drag whenever the handle view it touched
        holder.handleView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (MotionEventCompat.getActionMasked(event) == MotionEvent.ACTION_DOWN) {
                    mDragStartListener.onStartDrag(holder);
                }
                return false;
            }
        });
        holder.up.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (MotionEventCompat.getActionMasked(motionEvent) == MotionEvent.ACTION_DOWN) {
                    DataEntity stock = mItems.remove(position);
                    mItems.add(0, stock);
                    init();
                    eventBus.post(new SeletedEvent(selected(map)));
                    notifyDataSetChanged();
                }
                return false;
            }
        });

        holder.checkBox.setTag(new Integer(position));//设置tag 否则划回来时选中消失

        if (map != null) {
            ((ItemViewHolder) holder).checkBox.setChecked((map.get(position)));
        } else {
            ((ItemViewHolder) holder).checkBox.setChecked(false);
        }
        //cb 的选中事件
//        onchecked(holder, position);

        holder.checkBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int mFlags = (Integer) view.getTag();
                if (map != null) {
                    if (map.get(position)) {
                        map.put(position, false);
                        eventBus.post(new SeletedEvent(selected(map)));
                    } else {
                        map.put(mFlags, Boolean.TRUE);
                        eventBus.post(new SeletedEvent(selected(map)));
                    }
                }
            }
        });
    }

    private int selected(HashMap<Integer, Boolean> map) {
        int size = 0;
        for (Integer key : map.keySet()) {
            if(map.get(key)){
                size++;
            }
        }
        return size;
    }
    @Override
    public void onItemDismiss(int position) {
        mItems.remove(position);
        notifyItemRemoved(position);
    }

    @Override
    public boolean onItemMove(int fromPosition, int toPosition) {
        Collections.swap(mItems, fromPosition, toPosition);
        notifyItemMoved(fromPosition, toPosition);
//        init();
//        notifyDataSetChanged();
        return true;
    }

    @Override
    public int getItemCount() {
        return mItems == null? 0 :mItems.size();
    }


    /**
     * Simple example of a view holder that implements {@link ItemTouchHelperViewHolder} and has a
     * "handle" view that initiates a drag event when touched.
     */
    public static class ItemViewHolder extends RecyclerView.ViewHolder implements
            ItemTouchHelperViewHolder {

        public final CheckBox checkBox;
        public final TextView stockName;
        public final TextView stockCode;
        public final ImageView handleView;
        public final ImageView up;

        public ItemViewHolder(View itemView) {
            super(itemView);
            checkBox = (CheckBox) itemView.findViewById(R.id.checkbox);
            stockName = (TextView) itemView.findViewById(R.id.stock_name);
            stockCode = (TextView) itemView.findViewById(R.id.stock_code);
            handleView = (ImageView) itemView.findViewById(R.id.handle);
            up = (ImageView) itemView.findViewById(R.id.up);
        }

        @Override
        public void onItemSelected() {
            itemView.setBackgroundColor(Color.LTGRAY);
        }

        @Override
        public void onItemClear() {
            itemView.setBackgroundColor(0);
        }
    }

    public HashMap<Integer, Boolean> getMap() {
        return map;
    }

    public void setMap(HashMap<Integer, Boolean> map) {
        this.map = map;
        notifyDataSetChanged();
    }
}
