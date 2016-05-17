package com.zmf.editablelist;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    private ArrayList<DataEntity> localList = new ArrayList<>();
    private ListView mListView;
    private MyListAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mListView = (ListView) findViewById(R.id.list_stocks);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        for (int i = 0; i < 10; i++) {
            DataEntity model = new DataEntity();
            model.setSecshortname("平安股份" + i);
            model.setTicker("00000" + i);
            model.setSecid("00000" + i + ".XSE");
            localList.add(model);
        }
        mAdapter = new MyListAdapter(localList,MainActivity.this);
        mListView.setAdapter(mAdapter);

        findViewById(R.id.edit).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, EditListActvity.class);
                intent.putParcelableArrayListExtra("stocks", localList);
                startActivityForResult(intent, Constant.REQUEST_CODE);
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.i("stocks", "onActivityResult()");
        switch (resultCode) { //resultCode为回传的标记
            case Constant.REQUEST_CODE:
                try {
                    localList.clear();
                    localList = data.getParcelableArrayListExtra("stocks");
                    mAdapter.notifyDataSetChanged();
                    StringBuilder stocks = new StringBuilder();
                    DataEntity entity;
                    for (int i = 0, p = localList.size(); i < p; i++) {
                        entity = localList.get(i);
                        if (i == 0) {
                            stocks.append(entity.getSecshortname()).append("&").append(entity.getSecid()).append("&").append(entity.getTicker());
                        } else {
                            stocks.append("," + entity.getSecshortname()).append("&").append(entity.getSecid()).append("&").append(entity.getTicker());
                        }
                    }
                    SharedPreferences preferences = MainActivity.this.getSharedPreferences(
                            "prefers", Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = preferences.edit();
                    editor.putString(Constant.SELECTED, stocks.toString());
                    editor.commit();
                    Log.i("stocks", "onActivityResult() " + stocks.toString());
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            default:
                break;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
