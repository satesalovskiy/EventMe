package com.tsa.EventMe;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

public class DetailsActivity extends AppCompatActivity {


    private String recievedTopic;
    private String recievedDescription;
    private String recievedDate;
    private String recievedImage;

    private ImageView details_image;
    private TextView details_topic;
    private TextView details_description;
    private TextView details_date;
    private ImageView star;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);


        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);


        recievedImage = getIntent().getExtras().getString("event_photo");
        recievedDate = getIntent().getExtras().getString("event_date");
        recievedTopic = getIntent().getExtras().getString("event_topic");
        toolbar.setTitle("Details");
        setSupportActionBar(toolbar);
        ActionBar actionBar = (ActionBar) getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        recievedDescription = getIntent().getExtras().getString("event_description");



        details_image = findViewById(R.id.details_image);
        details_topic = findViewById(R.id.details_topic);
        details_date = findViewById(R.id.details_date);
        details_description = findViewById(R.id.details_description);
        star = findViewById(R.id.star);
        Toast.makeText(this, getIntent().getExtras().getString("event_topic"), Toast.LENGTH_SHORT).show();

        Picasso.get()
                .load(Uri.parse(recievedImage))
                .placeholder(R.drawable.defaultimage)
                .fit()
                .centerInside()
                .into(details_image);
        details_topic.setText(recievedTopic);
        details_date.setText(recievedDate);
        details_description.setText(recievedDescription);

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_details, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.details_subscribe:



               // startActivity(new Intent(this, ProfileSettings.class));
                Toast.makeText(this, "Subscribed", Toast.LENGTH_SHORT).show();
                //star.setVisibility(View.VISIBLE);




                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
