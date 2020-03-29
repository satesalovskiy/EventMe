package com.tsa.EventMe;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
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

import de.hdodenhof.circleimageview.CircleImageView;

import static com.tsa.EventMe.ProfileActivity.APP_PREFERENCES;

public class DetailsActivity extends AppCompatActivity {

    private DatabaseReference EVENTSRef;


    private String receivedTopic;
    private String receivedDescription;
    private String receivedCreatorPhoto = null;

    private FirebaseAuth auth;
    private Calendar date;
    private DatabaseReference ref;


    private FirebaseDatabase database;


    private Button switchCompat;


    private String test;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);

        createNotificationChanel();

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Details");
        toolbar.setTitleTextColor(getResources().getColor(R.color.white_text));
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        String receivedImage = getIntent().getExtras().getString("event_photo");
        String receivedDay = getIntent().getExtras().getString("event_day");
        String receivedMonth = getIntent().getExtras().getString("event_month");
        String receivedYear = getIntent().getExtras().getString("event_year");
        String receivedCreatorEmail = getIntent().getExtras().getString("event_creator_email");

        if (getIntent().getExtras().containsKey("event_creator_photo")) {
            receivedCreatorPhoto = getIntent().getExtras().getString("event_creator_photo");
        }

        receivedTopic = getIntent().getExtras().getString("event_topic");
        String recievedRef = getIntent().getExtras().getString("event_ref");
        receivedDescription = getIntent().getExtras().getString("event_description");

        date = new GregorianCalendar();
        date = Calendar.getInstance();
        date.set(Calendar.YEAR, Integer.parseInt(receivedYear));
        date.set(Calendar.MONTH, Integer.parseInt(receivedMonth));
        date.set(Calendar.DAY_OF_MONTH, Integer.parseInt(receivedDay));

        ImageView details_image = findViewById(R.id.details_image);
        TextView details_topic = findViewById(R.id.details_topic);
        TextView details_date = findViewById(R.id.details_date);
        TextView details_description = findViewById(R.id.details_description);
        TextView eventCreatorEmail = findViewById(R.id.eventCreatorEmail);
        CircleImageView eventCreatorPhoto = findViewById(R.id.eventCreatorPhoto);

        switchCompat = findViewById(R.id.remind_switch);
        setSwitchListener();

        Picasso.get()
                .load(Uri.parse(receivedImage))
                .placeholder(R.drawable.qwe)
                .fit()
                .centerInside()
                .into(details_image);

        details_topic.setText(receivedTopic);
        String datee = receivedDay + "-" + receivedMonth + "-" + receivedYear;
        details_date.setText(datee);
        details_description.setText(receivedDescription);
        eventCreatorEmail.setText(receivedCreatorEmail);

        if (receivedCreatorPhoto != null) {
            Picasso.get()
                    .load(Uri.parse(receivedCreatorPhoto))
                    .placeholder(R.drawable.defaultimage)
                    .fit()
                    .centerInside()
                    .into(eventCreatorPhoto);
        }
        EVENTSRef = FirebaseDatabase.getInstance().getReference().child("events").child(recievedRef);

        auth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
    }


    private void setSwitchListener() {
        switchCompat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Remind();
            }
        });
    }

    private void Remind() {

        Toast.makeText(this, "You will get a notification", Toast.LENGTH_SHORT).show();

        Intent intent = new Intent(this, ReminderBroadcast.class);
        intent.putExtra("Topic", receivedTopic);
        intent.putExtra("Description", receivedDescription);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, intent, 0);
        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);

        Date date12 = date.getTime();
        alarmManager.set(AlarmManager.RTC_WAKEUP, 10000, pendingIntent);
    }

    private void createNotificationChanel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
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
                showAskDialog();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void showAskDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Add to My Events?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        positiveAnswer();
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                });

        builder.create();
        builder.show();
    }

    private void positiveAnswer() {
        addToFavorite();
    }

    private void addToFavorite() {
        EVENTSRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if (dataSnapshot.child("topic").getValue() != null) {
                    test = ")))";
                }

                test = dataSnapshot.child("topic").toString();

                date.set(Calendar.YEAR, Integer.parseInt(dataSnapshot.child("year").getValue().toString()));
                date.set(Calendar.MONTH, Integer.parseInt(dataSnapshot.child("month").getValue().toString()));
                date.set(Calendar.DAY_OF_MONTH, Integer.parseInt(dataSnapshot.child("day").getValue().toString()));

                String userImageUrl;
                if (auth.getCurrentUser().getPhotoUrl() != null) {
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
    }
}
