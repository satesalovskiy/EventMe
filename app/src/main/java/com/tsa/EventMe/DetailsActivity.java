package com.tsa.EventMe;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.Calendar;
import java.util.GregorianCalendar;

public class DetailsActivity extends AppCompatActivity {

    private DatabaseReference EVENTSRef;



    private String recievedTopic;
    private String recievedDescription;
    private String recievedDate;
    private String recievedImage;
    private String recievedRef;
    private FirebaseAuth auth;
    private Calendar date;
    DatabaseReference ref;



    private ImageView details_image;
    private TextView details_topic;
    private TextView details_description;
    private TextView details_date;
    private ImageView star;
    FirebaseDatabase database;



    String test;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);


        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);


        recievedImage = getIntent().getExtras().getString("event_photo");
        recievedDate = getIntent().getExtras().getString("event_date");
        recievedTopic = getIntent().getExtras().getString("event_topic");
        recievedRef = getIntent().getExtras().getString("event_ref");
        toolbar.setTitle("Details");
        setSupportActionBar(toolbar);
        ActionBar actionBar = (ActionBar) getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        recievedDescription = getIntent().getExtras().getString("event_description");

        date = new GregorianCalendar();
        date = Calendar.getInstance();

        details_image = findViewById(R.id.details_image);
        details_topic = findViewById(R.id.details_topic);
        details_date = findViewById(R.id.details_date);
        details_description = findViewById(R.id.details_description);
        star = findViewById(R.id.star);
        //Toast.makeText(this, getIntent().getExtras().getString("event_topic"), Toast.LENGTH_SHORT).show();

        Picasso.get()
                .load(Uri.parse(recievedImage))
                .placeholder(R.drawable.defaultimage)
                .fit()
                .centerInside()
                .into(details_image);
        details_topic.setText(recievedTopic);
        details_date.setText(recievedDate);
        details_description.setText(recievedDescription);


        EVENTSRef = FirebaseDatabase.getInstance().getReference().child("events").child(recievedRef);
        auth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();



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
                //Toast.makeText(this, "Subscribed", Toast.LENGTH_SHORT).show();
                //star.setVisibility(View.VISIBLE);

                EVENTSRef.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                            if(dataSnapshot.child("topic").getValue() != null) {
                                test = "Jopa";
                            }

                            test = dataSnapshot.child("topic").toString();

                            //Log.d("MYTAG", dataSnapshot.child("topic").getValue().toString());

                        date.set(Calendar.YEAR, Integer.parseInt(dataSnapshot.child("year").getValue().toString()));
                        date.set(Calendar.MONTH, Integer.parseInt(dataSnapshot.child("month").getValue().toString()));
                        date.set(Calendar.DAY_OF_MONTH, Integer.parseInt(dataSnapshot.child("day").getValue().toString()));

                        Event event = new Event(
                                auth.getCurrentUser().getUid(),
                                auth.getCurrentUser().getEmail(),
                                dataSnapshot.child("topic").getValue().toString(),
                                dataSnapshot.child("description").getValue().toString(),
                                dataSnapshot.child("image").getValue().toString(),
                                dataSnapshot.child("location").getValue().toString(),
                                date);

                        ref = database.getReference().child("users").child(auth.getCurrentUser().getUid()).child("events");
                        ref.push().setValue(event);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

                //Toast.makeText(this, test, Toast.LENGTH_SHORT).show();
//                if(test != null){
//                    Log.d("MYTAG", test);
//
//                }




                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
