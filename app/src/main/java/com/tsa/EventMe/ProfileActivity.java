package com.tsa.EventMe;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.appcompat.widget.Toolbar;

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
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;


public class ProfileActivity extends AppCompatActivity {
    public static final String APP_PREFERENCES = "myprefs";
    public static int NUMBER_OF_ALL_EVENTS = 0;

    private ImageView personImage;

    private String name;
    private String phone;
    private String email;
    private String status;
    private Toolbar toolbar;

    private TextView userName;
    private TextView userStatus;
    private TextView userEmail;
    private TextView userPhone;
    private TextView userAllEventsCount;

    private ImageView editImage;
    private EditText editText;
    private int CHOOSE_IMAGE_CODE = 111;
    private AlertDialog dialog;

    private SwitchCompat postSwitch;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        toolbar = (Toolbar) findViewById(R.id.toolbar);

        toolbar.setTitle("Person name");
        toolbar.setTitleTextColor(getResources().getColor(R.color.white_text));

        setSupportActionBar(toolbar);
        ActionBar actionBar = (ActionBar) getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        personImage = (ImageView) findViewById(R.id.person_image);
        personImage.setImageResource(R.drawable.defaultimage);

        userStatus = findViewById(R.id.user_status);
        userEmail = findViewById(R.id.user_email);
        userPhone = findViewById(R.id.user_phone);
        postSwitch = findViewById(R.id.post_switch);
        userAllEventsCount = findViewById(R.id.user_number_of_events);



        // editText = findViewById(R.id.edit_text);

        // editImage = findViewById(R.id.change_image_button);
//
//        editText.setFocusable(View.FOCUSABLE);
//        editText.setFocusableInTouchMode(true);
//        editText.setInputType(InputType.TYPE_NULL);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            if (user.getPhotoUrl() != null) {
                Picasso.get()
                        .load(user.getPhotoUrl())
                        .placeholder(R.drawable.defaultimage)
                        .fit()
                        .centerCrop()
                        .into(personImage);
            }
        }


        sharedPreferences = getSharedPreferences(APP_PREFERENCES, 0);

        setSwitchListener();

        if(sharedPreferences.contains("newUserName")) {
            toolbar.setTitle(sharedPreferences.getString("newUserName", ""));
        }
        if(sharedPreferences.contains("newUserEmail")) {
            userEmail.setText(sharedPreferences.getString("newUserEmail", ""));
        }
        if(sharedPreferences.contains("newUserStatus")) {
            userStatus.setText(sharedPreferences.getString("newUserStatus", ""));
        }
        if(sharedPreferences.contains("newUserPhone")) {
            userPhone.setText(sharedPreferences.getString("newUserPhone", ""));
        }
        if(sharedPreferences.contains("totalNumberOfEvents")) {

            int a = sharedPreferences.getInt("totalNumberOfEvents", 0);
            String b = ""+a;
            userAllEventsCount.setText(b);
        }
    }



    private void setSwitchListener() {

        boolean isPostEnabled = sharedPreferences.getBoolean("Notifications", false);

        if(isPostEnabled) {
            postSwitch.setChecked(true);
        } else {
            postSwitch.setChecked(false);
        }


        postSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {

                SharedPreferences.Editor edit = sharedPreferences.edit();
                edit.putBoolean("Notifications", b);
                edit.apply();


                if(b){
                    subscribePostNotifications();
                } else {
                    unsubscribePostNotifications();
                }
            }
        });
    }

    private void unsubscribePostNotifications() {

        FirebaseMessaging.getInstance().unsubscribeFromTopic("Notifications")
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        String msg = "You will not receive post notifications";

                        if(!task.isSuccessful()) {
                            msg = "UnSubscription failed";
                        }

                        Toast.makeText(ProfileActivity.this, msg, Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void subscribePostNotifications() {

        FirebaseMessaging.getInstance().subscribeToTopic("Notifications")
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        String msg = "You will receive post notifications";

                        if(!task.isSuccessful()) {
                            msg = "Subscription failed";
                        }

                        Toast.makeText(ProfileActivity.this, msg, Toast.LENGTH_SHORT).show();
                    }
                });
        
    }

    public void editUserInfo(View view) {

        AlertDialog.Builder  builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();
        final View view1 = inflater.inflate(R.layout.edit_user_profile_info, null);

        builder.setView(view1)
                .setPositiveButton(R.string.editTextPositiveButton, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                        EditText editTextName = view1.findViewById(R.id.edit_text_name);
                        EditText editTextStatus = view1.findViewById(R.id.edit_text_status);
                        EditText editTextEmail = view1.findViewById(R.id.edit_text_email);
                        EditText editTextPhone = view1.findViewById(R.id.edit_text_phone);

                        String name = editTextName.getText().toString();
                        String status = editTextStatus.getText().toString();
                        String email = editTextEmail.getText().toString();
                        String phone = editTextPhone.getText().toString();
                        confirmChanges(name, status, email, phone);
                    }
                })
                .setNegativeButton(R.string.editTextNegativeButton, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.cancel();
                    }
                });

        dialog = builder.create();
        dialog.show();
    }

    private void confirmChanges(String name, String status, String email, String phone) {
        if(!name.isEmpty()){
            toolbar.setTitle(name);
            writeInPrefs("newUserName", name);
        }
        if(!status.isEmpty()){
            userStatus.setText(status);
            writeInPrefs("newUserStatus", status);
        }
        if(!email.isEmpty()){
            userEmail.setText(email);
            writeInPrefs("newUserEmail", email);
        }
        if(!phone.isEmpty()){
            userPhone.setText(phone);
            writeInPrefs("newUserPhone", phone);
        }
        Toast.makeText(this, "Some changes will be available after re-enter", Toast.LENGTH_SHORT).show();
    }

    private void writeInPrefs(String key, String value) {
        SharedPreferences sharedPreferences = this.getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(key, value);
        editor.apply();
    }


    public void changeImage() {
        Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
        photoPickerIntent.setType("image/*");
        startActivityForResult(photoPickerIntent, CHOOSE_IMAGE_CODE);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CHOOSE_IMAGE_CODE) {
            switch (resultCode) {
                case RESULT_OK:

                    Uri uri = data.getData();
                    Picasso.get()
                            .load(uri)
                            .placeholder(R.drawable.defaultimage)
                            .fit()
                            .centerCrop()
                            .into(personImage);

                    handleUpload(uri);
            }
        }


    }

    private void handleUpload(Uri uri) {

        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();

        final StorageReference storageReference = FirebaseStorage.getInstance().getReference()
                .child("profileImages")
                .child(uid + ".jpg");

        storageReference.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                getDownloadUrl(storageReference);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

            }
        });


    }


    private void getDownloadUrl(StorageReference reference) {
        reference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                setUserProfileUrl(uri);
            }
        });
    }

    private void setUserProfileUrl(Uri uri) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        UserProfileChangeRequest request = new UserProfileChangeRequest.Builder()
                .setPhotoUri(uri)
                .build();

        user.updateProfile(request)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(ProfileActivity.this, "Image changed successfully", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_profile_options, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.profile_settings:
                //ChangeImage();

                showOptionsDialog();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void showOptionsDialog() {
        final String[] options = {"Edit profile info", "Pick new photo", "Log out"};

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Chose option")
                .setItems(options, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                       switch (which){
                           case 0:
                               editUserInfo(null);
                               break;
                           case 1:
                               changeImage();
                               break;
                           case 2:
                               logOut();
                       }
                    }
                })
        .setIcon(R.drawable.baseline_brush_black_48);

         builder.create();
         builder.show();
    }

    private void logOut() {
        FirebaseAuth.getInstance().signOut();
        SharedPreferences sharedPreferences = this.getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.remove("newUserName");
        editor.remove("newUserEmail");
        editor.remove("newUserPhone");
        editor.remove("newUserStatus");
        editor.remove("totalNumberOfEvents");
        editor.apply();

        startActivity(new Intent(this, SignupActivity.class));
    }
}
