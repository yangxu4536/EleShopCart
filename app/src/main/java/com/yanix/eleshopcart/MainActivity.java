package com.yanix.eleshopcart;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {
    private GouWuCheLayout gouwucheLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        gouwucheLayout = (GouWuCheLayout) findViewById(R.id.dragLayout);
        findViewById(R.id.tv_gouwuche).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (gouwucheLayout.isBottom()) {
                    gouwucheLayout.maximize();
                } else {
                    gouwucheLayout.minimize();
                }
            }
        });

        ListView listView = (ListView) findViewById(R.id.listView);
        ListView listView2 = (ListView) findViewById(R.id.desc);
        listView.setAdapter(new MyAdapter());
        listView2.setAdapter(new MyAdapter());
    }

    class MyAdapter extends BaseAdapter {
        @Override
        public int getCount() {
            return 50;
        }

        @Override
        public String getItem(int i) {
            return "object" + i;
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(int i, View rView, ViewGroup viewGroup) {
            View view = rView;
            if (view == null) {
                view = getLayoutInflater().inflate(android.R.layout.simple_list_item_1, viewGroup, false);
            }
            ((TextView) view.findViewById(android.R.id.text1)).setText(getItem(i));
            return view;
        }
    }

    @Override
    public void onBackPressed() {
        if (!gouwucheLayout.isBottom()) {
            gouwucheLayout.minimize();
            return;
        }
        super.onBackPressed();
    }
}
