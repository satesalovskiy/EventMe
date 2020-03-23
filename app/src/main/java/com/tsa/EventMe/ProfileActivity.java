package com.tsa.EventMe;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;


public class ProfileActivity extends AppCompatActivity {
    public static final String APP_PREFERENCES = "myprefs";

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

    private ImageView editImage;
    private EditText editText;
    private int CHOOSE_IMAGE_CODE = 111;
    private AlertDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        toolbar = (Toolbar) findViewById(R.id.toolbar);

        toolbar.setTitle("Person name");

        setSupportActionBar(toolbar);
        ActionBar actionBar = (ActionBar) getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        personImage = (ImageView) findViewById(R.id.person_image);
        personImage.setImageResource(R.drawable.defaultimage);

        userStatus = findViewById(R.id.user_status);
        userEmail = findViewById(R.id.user_email);
        userPhone = findViewById(R.id.user_phone);


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


        SharedPreferences sharedPreferences = getSharedPreferences(APP_PREFERENCES, 0);
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
    }

    public void EditUserInfo(View view) {

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
                        ConfirmChanges(name, status, email, phone);
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

    private void ConfirmChanges(String name, String status, String email, String phone) {

        if(!name.isEmpty()){
            toolbar.setTitle(name);
            WriteInPrefs("newUserName", name);
        }
        if(!status.isEmpty()){
            userStatus.setText(status);
            WriteInPrefs("newUserStatus", status);
        }
        if(!email.isEmpty()){
            userEmail.setText(email);
            WriteInPrefs("newUserEmail", email);
        }
        if(!phone.isEmpty()){
            userPhone.setText(phone);
            WriteInPrefs("newUserPhone", phone);
        }

    }

    private void WriteInPrefs(String key, String value) {
        SharedPreferences sharedPreferences = this.getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(key, value);
        editor.apply();
    }


    public void ChangeImage() {
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
                //startActivity(new Intent(this, ProfileSettings.class));
                ChangeImage();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
