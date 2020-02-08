package com.tsa.EventMe;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.Bundle;


public class ProfileActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Person name");
        setSupportActionBar(toolbar);
        ActionBar actionBar = (ActionBar) getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

    }
}
