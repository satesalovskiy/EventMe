package com.tsa.EventMe;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.appcompat.widget.Toolbar;

import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
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

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;

public class DetailsActivity extends AppCompatActivity {

    private DatabaseReference EVENTSRef;



    private String recievedTopic;
    private String recievedDescription;
    private String recievedDay;
    private String recievedMonth;
    private String recievedYear;

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

    private Button switchCompat;
    private SharedPreferences sharedPreferences;




    String test;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);


        createNotificationChanel();


        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);


        recievedImage = getIntent().getExtras().getString("event_photo");

        recievedDay = getIntent().getExtras().getString("event_day");
        recievedMonth = getIntent().getExtras().getString("event_month");
        recievedYear = getIntent().getExtras().getString("event_year");



        recievedTopic = getIntent().getExtras().getString("event_topic");
        recievedRef = getIntent().getExtras().getString("event_ref");



        toolbar.setTitle("Details");
        setSupportActionBar(toolbar);
        ActionBar actionBar = (ActionBar) getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        recievedDescription = getIntent().getExtras().getString("event_description");

        date = new GregorianCalendar();
        date = Calendar.getInstance();


        date.set(Calendar.YEAR, Integer.parseInt(recievedYear));
        date.set(Calendar.MONTH, Integer.parseInt(recievedMonth));
        date.set(Calendar.DAY_OF_MONTH, Integer.parseInt(recievedDay));

        details_image = findViewById(R.id.details_image);
        details_topic = findViewById(R.id.details_topic);
        details_date = findViewById(R.id.details_date);
        details_description = findViewById(R.id.details_description);
        star = findViewById(R.id.star);

        sharedPreferences = getSharedPreferences(ProfileActivity.APP_PREFERENCES, MODE_PRIVATE);

        switchCompat = findViewById(R.id.remind_switch);
        setSwitchListener();
        //Toast.makeText(this, getIntent().getExtras().getString("event_topic"), Toast.LENGTH_SHORT).show();

        Picasso.get()
                .load(Uri.parse(recievedImage))
                .placeholder(R.drawable.defaultimage)
                .fit()
                .centerInside()
                .into(details_image);
        details_topic.setText(recievedTopic);
        String datee = recievedDay + "-" + recievedMonth + "-" + recievedYear;
        details_date.setText(datee);
        details_description.setText(recievedDescription);


        EVENTSRef = FirebaseDatabase.getInstance().getReference().child("events").child(recievedRef);

        auth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();



    }


    private void setSwitchListener() {

//        boolean isRemindReq = sharedPreferences.getBoolean("Remind", false);
//
//        if(isRemindReq) {
//            switchCompat.setChecked(true);
//        } else {
//            switchCompat.setChecked(false);
//        }

        switchCompat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Remind();
            }
        });

//        switchCompat.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
//            @Override
//            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
//
//                SharedPreferences.Editor edit = sharedPreferences.edit();
//                edit.putBoolean("Remind", b);
//                edit.apply();
//
//
//                if(b){
//                    Remind();
//                } else {
//                    DeleteRemind();
//                }
//            }
//        });
    }


    private void Remind() {

        Toast.makeText(this, "You will get a notification", Toast.LENGTH_SHORT).show();

        String myFormat = "dd/MM/yy" ;
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat , Locale. getDefault ()) ;

        Intent intent = new Intent(this, ReminderBroadcast.class);
        intent.putExtra("Topic", recievedTopic);
        intent.putExtra("Description", recievedDescription);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, intent, 0);
        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);

        Date date12 = date.getTime();
        alarmManager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, date12.getTime(), pendingIntent);
    }

    private void DeleteRemind() {

    }

    private void createNotificationChanel() {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            CharSequence name = "MyReminderChanel";
            String description = "Chanel";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel("remindMe", name, importance);
            channel.setDescription(description);

            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel);


        }
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
                                test = ")))";
                            }

                            test = dataSnapshot.child("topic").toString();

                            //Log.d("MYTAG", dataSnapshot.child("topic").getValue().toString());

                        date.set(Calendar.YEAR, Integer.parseInt(dataSnapshot.child("year").getValue().toString()));
                        date.set(Calendar.MONTH, Integer.parseInt(dataSnapshot.child("month").getValue().toString()));
                        date.set(Calendar.DAY_OF_MONTH, Integer.parseInt(dataSnapshot.child("day").getValue().toString()));

                        String userImageUrl;
                        if(auth.getCurrentUser().getPhotoUrl() != null) {
                            userImageUrl = auth.getCurrentUser().getPhotoUrl().toString();
                        } else {
                            userImageUrl = "https://vk.com/im?peers=103103918_83744687&sel=44403965&z=photo44403965_457242394%2Fmail1152840";
                        }


                        Event event = new Event(
                                userImageUrl,
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
