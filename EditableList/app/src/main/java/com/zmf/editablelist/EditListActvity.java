package com.zmf.editablelist;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;

import com.zmf.editablelist.helper.OnStartDragListener;
import com.zmf.editablelist.helper.SimpleItemTouchHelperCallback;

import java.util.ArrayList;
import java.util.HashMap;

import de.greenrobot.event.EventBus;

/**
 * Created by zimengfang on 16/4/19.
 */
public class EditListActvity extends Activity implements OnStartDragListener {
    private Button delete;
    private RecyclerView recyclerView;
    private CheckBox checkbox;
    private TextView selected;

    private ItemTouchHelper mItemTouchHelper;
    private EditListAdapter adapter;
    ArrayList<DataEntity> list;
    private Intent intent;
    private EventBus event;
    private boolean isChange = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);
        recyclerView = (RecyclerView) findViewById(R.id.recyclerview);
        intent = getIntent();
        event = EventBus.getDefault();
        event.register(this);
        list = intent.getParcelableArrayListExtra("stocks");

        recyclerView = (RecyclerView) findViewById(R.id.recyclerview);
        checkbox = (CheckBox) findViewById(R.id.checkbox);
        delete = (Button) findViewById(R.id.delete);
        selected = (TextView) findViewById(R.id.selected);


        adapter = new EditListAdapter(list, this, event);

        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        ItemTouchHelper.Callback callback = new SimpleItemTouchHelperCallback(adapter);
        mItemTouchHelper = new ItemTouchHelper(callback);
        mItemTouchHelper.attachToRecyclerView(recyclerView);


        checkbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                try {
                    HashMap<Integer, Boolean> map = new HashMap<Integer, Boolean>();
                    int count = 0;
                    for (int i = 0, p = list.size(); i < p; i++) {
                        if (isChecked) {
                            map.put(i, true);
                            count++;
                        } else {
                            if (!isChange) {
                                map.put(i, false);
                                count = 0;
                            } else {
                                map = adapter.getMap();
                                count = map.size();
                            }
                        }
                    }
                    selected.setText("已选" + count + "项");
                    adapter.setMap(map);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                HashMap<Integer, Boolean> map = adapter.getMap();
                ArrayList<DataEntity> selectedList = new ArrayList<DataEntity>();
                StringBuilder securityIds = new StringBuilder();
                for (int i = 0; i < list.size(); i++) {
                    if (map.get(i)) {
                        selectedList.add(list.get(i));
                    }
                }
                if (selectedList.size() <= 0) {
                    Toast.makeText(EditListActvity.this,"请选择要删除的股票",Toast.LENGTH_LONG).show();
                    return;
                }

                list.removeAll(selectedList);

                for (int i = 0, p = list.size(); i < p; i++) {
                    map.put(i, false);
                }
                adapter.setMap(map);
                adapter.notifyDataSetChanged();


                StringBuilder stocks = new StringBuilder();
                DataEntity entity;
                for (int i = 0, p = list.size(); i < p; i++) {
                    entity = list.get(i);
                    if (i == 0) {
                        stocks.append(entity.getSecshortname()).append("&").append(entity.getSecid()).append("&").append(entity.getTicker());
                    } else {
                        stocks.append("," + entity.getSecshortname()).append("&").append(entity.getSecid()).append("&").append(entity.getTicker());
                    }
                }
                selected.setText("已选0项");
                SharedPreferences preferences = EditListActvity.this.getSharedPreferences(
                        "prefers", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = preferences.edit();
                editor.putString(Constant.SELECTED, stocks.toString());
                editor.commit();
            }
        });

        findViewById(R.id.finish).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                intent.putParcelableArrayListExtra("stocks", list);
                setResult(Constant.REQUEST_CODE, intent);
                finish();
            }
        });

    }

    @Override
    public void onStartDrag(RecyclerView.ViewHolder viewHolder) {
        mItemTouchHelper.startDrag(viewHolder);
    }

    public void onEventMainThread(SeletedEvent event) {
        int size = event.getSize();
        if (size < list.size()) {
            isChange = true;
            checkbox.setChecked(false);
        } else {
            checkbox.setChecked(true);
            isChange = false;
        }
        selected.setText("已选" + size + "项");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        event.unregister(this);
    }

}
