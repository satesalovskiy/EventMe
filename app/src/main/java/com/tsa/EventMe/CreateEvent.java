package com.tsa.EventMe;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomappbar.BottomAppBar;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Map;

import static com.tsa.EventMe.ProfileActivity.APP_PREFERENCES;

public class CreateEvent extends AppCompatActivity {

    private String topic;
    private String description;
    private String location;
    private String imageUrl;
    private String userImageUrl;
    private EditText createTopic;
    private EditText createDescription;
    private EditText createLocation;
    private ImageView creatingImage;
    private Calendar date;
    private FirebaseAuth auth;
    private FirebaseDatabase database;
    private DatabaseReference ref;
    private StorageReference mStorageRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_event);

        BottomAppBar bottomAppBar = findViewById(R.id.bottom_app_bar);
        setSupportActionBar(bottomAppBar);

        date = new GregorianCalendar();
        date = Calendar.getInstance();

        createTopic = findViewById(R.id.createEventTopic);
        createDescription = findViewById(R.id.createEventDescription);
        creatingImage = findViewById(R.id.creatingImage);
        createLocation = findViewById(R.id.createEventLocation);

        mStorageRef = FirebaseStorage.getInstance().getReference();

        auth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_create_event, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.load_image:
                chooseImage();
                return true;
            case R.id.choose_date:
                chooseDate();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void chooseDate() {
        new DatePickerDialog(CreateEvent.this, datePickerListener,
                date.get(Calendar.YEAR),
                date.get(Calendar.MONTH),
                date.get(Calendar.DAY_OF_MONTH)).show();
    }

    public void chooseImage() {
        Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
        photoPickerIntent.setType("image/*");
        startActivityForResult(photoPickerIntent, 1);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent imageReturnedIntent) {
        super.onActivityResult(requestCode, resultCode, imageReturnedIntent);

        switch (requestCode) {
            case 1:
                if (resultCode == RESULT_OK) {
                    final Uri selectedImage = imageReturnedIntent.getData();

                    String name = generateRandomNameForImage();

                    final StorageReference imagesRef = mStorageRef.child("images/" + name + ".jpg");
                    UploadTask uploadTask = imagesRef.putFile(selectedImage);
                    Task<Uri> urlTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                        @Override
                        public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                            if (!task.isSuccessful()) {
                                throw task.getException();
                            }
                            return imagesRef.getDownloadUrl();
                        }
                    }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                        @Override
                        public void onComplete(@NonNull Task<Uri> task) {
                            if (task.isSuccessful()) {
                                Uri downloadUri = task.getResult();
                                if (downloadUri != null) {
                                    String photoStringLink = downloadUri.toString();
                                    setImageUrl(photoStringLink);
                                }
                            }
                        }
                    });
                }
        }
    }

    private void setImageUrl(String url) {
        imageUrl = url;
        Picasso.get()
                .load(url)
                .placeholder(R.drawable.defaultimage)
                .fit()
                .centerCrop()
                .into(creatingImage);
    }

    private String generateRandomNameForImage() {
        String symbols = "qwertyuiopasdfghjklzxcvbnm";
        StringBuilder randString = new StringBuilder();

        int count = 10 + (int) (Math.random() * 30);

        for (int i = 0; i < count; i++)
            randString.append(symbols.charAt((int) (Math.random() * symbols.length())));

        return randString.toString();
    }

    public void createEvent(View view) {
        if (createTopic.getText().toString().isEmpty() || createDescription.getText().toString().isEmpty() || imageUrl.isEmpty() || createLocation.getText().toString().isEmpty()) {
            Toast.makeText(CreateEvent.this, "Please define topic, description, location and image", Toast.LENGTH_SHORT).show();
            return;
        }

        topic = createTopic.getText().toString();
        description = createDescription.getText().toString();
        location = createLocation.getText().toString();

        if (auth.getCurrentUser().getPhotoUrl() != null) {
            userImageUrl = auth.getCurrentUser().getPhotoUrl().toString();
        } else {
            userImageUrl = "https://vk.com/im?peers=103103918_83744687&sel=44403965&z=photo44403965_457242394%2Fmail1152840";
        }

        checkIsTheSameTopicHasAlreadyExists(topic);
    }

    private void pushEvent(String userImageUrl, String uid, String email, String topic, String description, String imageUrl, String location, Calendar date) {

        Event event = new Event(
                userImageUrl,
                auth.getCurrentUser().getUid(),
                auth.getCurrentUser().getEmail(),
                topic,
                description,
                imageUrl,
                location,
                date);

        ref = database.getReference().child("users").child(auth.getCurrentUser().getUid()).child("events");
        DatabaseReference allEvents = database.getReference().child("events");
        ref.push().setValue(event);
        allEvents.push().setValue(event);

        sendPushNotification(email, topic, imageUrl);


        createTopic.setText("");
        createDescription.setText("");
        createLocation.setText("");
        date.clear();
        imageUrl = "";
        creatingImage.setImageResource(0);

        showSnackBar("Event created");

    }

    private void dialogNewTopic() {
        LayoutInflater inflater = this.getLayoutInflater();
        final View view1 = inflater.inflate(R.layout.new_topic, null);
        final EditText newTopic = view1.findViewById(R.id.edit_text_new_topic);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Event with the same topic has already exists. Enter new name in form below")
                .setView(view1)
                .setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if (newTopic.getText() != null) {

                            if (newTopic.getText().toString().equals(topic)) {
                                Toast.makeText(getApplicationContext(), "Enter correct topic!", Toast.LENGTH_SHORT).show();
                                dialogInterface.dismiss();
                            } else {
                                checkIsTheSameTopicHasAlreadyExists(newTopic.getText().toString());
                            }
                        }
                    }
                })
                .setNegativeButton("Back", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                });

        builder.create();
        builder.show();
    }

    private void checkIsTheSameTopicHasAlreadyExists(String topic2) {
        final String ij = topic2;
        DatabaseReference EVENTSRef = FirebaseDatabase.getInstance().getReference().child("events");
        Query mQuery = EVENTSRef.orderByChild("topic").equalTo(topic2);
        mQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    dialogNewTopic();
                    return;
                }
                pushEvent(userImageUrl, auth.getCurrentUser().getUid(), auth.getCurrentUser().getEmail(), ij, description, imageUrl, location, date);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void sendPushNotification(String email, String s, String image) {
        try {

            RequestQueue queue = Volley.newRequestQueue(this);

            String url = "https://fcm.googleapis.com/fcm/send";

            JSONObject data = new JSONObject();

            data.put("title", "User " + email + " has created a new event!");
            data.put("description", s);
            data.put("image", image);
            data.put("useremail", email);




            JSONObject notification_data = new JSONObject();
            notification_data.put("data", data);

            notification_data.put("to", "/topics/Notifications");

            JsonObjectRequest request = new JsonObjectRequest(url, notification_data, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {

                }
            }) {
                @Override
                public Map<String, String> getHeaders() {
                    String api_key_header_value = "Key=AAAAj4yNrKs:APA91bHwM8NIrDRY9pGFJSS1yFsidaCYicaVd0Zg0bLxk6AYE1qdlbYYYmS3JKgMx3OVyAtCltgLWzAM7SKKXbSVFFx9mPfUdILL5cA4W6uGbaVvXjoGurWryzF4l_GGYYYwgQXhkCyJ";
                    Map<String, String> headers = new HashMap<>();
                    headers.put("Content-Type", "application/json");
                    headers.put("Authorization", api_key_header_value);
                    return headers;
                }
            };

            queue.add(request);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void showSnackBar(String s) {
        Snackbar snackbar = Snackbar.make(findViewById(R.id.coordinator2), s, Snackbar.LENGTH_LONG);
        snackbar.setAction("Ok", new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(CreateEvent.this, MainActivity.class));
            }
        });
        snackbar.show();
    }

    DatePickerDialog.OnDateSetListener datePickerListener = new DatePickerDialog.OnDateSetListener() {
        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
            date.set(Calendar.YEAR, year);
            date.set(Calendar.MONTH, monthOfYear + 1);
            date.set(Calendar.DAY_OF_MONTH, dayOfMonth);
        }
    };

}