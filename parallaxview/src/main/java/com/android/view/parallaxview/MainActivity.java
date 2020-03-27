package com.android.view.parallaxview;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;

public class MainActivity extends AppCompatActivity {

    protected AliInfoListView aliInfoListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        aliInfoListView = findViewById(R.id.ali_list);


        View headView = LayoutInflater.from(this).inflate(R.layout.headview, null);
        aliInfoListView.addHeaderView(headView);
        aliInfoListView.setHeaderView(headView);

        ArrayAdapter arrayAdapter = new ArrayAdapter(
                this, android.R.layout.simple_list_item_1,
                new String[]{


                        "https://github.com/HashWaney",
                        "https://github.com/HashWaney",
                        "https://github.com/HashWaney",
                        "https://github.com/HashWaney",
                        "https://github.com/HashWaney",
                        "https://github.com/HashWaney",
                        "https://github.com/HashWaney"


                }


        );

        aliInfoListView.setAdapter(arrayAdapter);

    }
}
