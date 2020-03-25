package com.tsa.EventMe;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Map;

public class SignupActivity extends AppCompatActivity {

    private EditText inputEmail, inputPassword;
    private Button btnSignup, btnSignin;
    private ProgressBar progressBar;
    private FirebaseAuth auth;

    private GoogleSignInClient mGoogleSignInClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.AppTheme);

        super.onCreate(savedInstanceState);

        auth = FirebaseAuth.getInstance();
        if (auth.getCurrentUser() != null) {
            startActivity(new Intent(this, MainActivity.class));
        }

        setContentView(R.layout.testik);

        inputEmail = (EditText) findViewById(R.id.email);
        inputPassword = (EditText) findViewById(R.id.password);
        btnSignup = (Button) findViewById(R.id.sign_up_button);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);

        btnSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String email = inputEmail.getText().toString();
                String password = inputPassword.getText().toString();

                if (!checkEmailAndPassword(email, password)) {
                    return;
                }

                progressBar.setVisibility(View.VISIBLE);

                auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(SignupActivity.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Toast.makeText(SignupActivity.this, "createUserWithEmail:onComplete:" + task.isSuccessful(), Toast.LENGTH_SHORT).show();
                        progressBar.setVisibility(View.GONE);

                        if (!task.isSuccessful()) {
                            Toast.makeText(SignupActivity.this, "Authentication failed." + task.getException(),
                                    Toast.LENGTH_SHORT).show();
                        } else {
                            Intent intent = new Intent(SignupActivity.this, MainActivity.class);
                            intent.putExtra("email", inputEmail.getText().toString());
                            startActivity(intent);
                            //finish();
                        }
                    }
                });
            }
        });



        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);


    }



    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == 101) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account);
            } catch (ApiException e) {
                // Google Sign In failed, update UI appropriately
                Log.w("asd", "Google sign in failed", e);
                // ...
            }
        }
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount account) {

        AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(), null);
        auth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d("asd", "signInWithCredential:success");
                            FirebaseUser user = auth.getCurrentUser();

                            Intent i = new Intent( getApplicationContext(), MainActivity.class);

                            i.putExtra("email", user.getEmail());

                            startActivity(i);


                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w("asd", "signInWithCredential:failure", task.getException());
//                            Snackbar.make(findViewById(R.id.main_layout), "Authentication Failed.", Snackbar.LENGTH_SHORT).show();
//                            updateUI(null);
                        }

                        // ...
                    }
                });
    }


    public void signIn(View view) {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, 101);
    }


    public void login(View view) {
        startActivity(new Intent(SignupActivity.this, LoginActivity.class));

    }


    @Override
    protected void onResume() {
        super.onResume();
        progressBar.setVisibility(View.GONE);
    }

    public void logOut(View view) {
        auth.signOut();
    }



    //Create test data and push it to Firebase Database
    public void generateData(View view) {
//        String image1 = "https://www.google.com/url?sa=i&url=https%3A%2F%2Fwww.forbes.com%2Fsites%2Fmarkgreene%2F2019%2F10%2F11%2Fhow-to-buy-a-house-with-10000%2F&psig=AOvVaw21g7PCnoitEJ4uGWGesgSt&ust=1581243678106000&source=images&cd=vfe&ved=0CAIQjRxqFwoTCLCNmfDdwecCFQAAAAAdAAAAABAD";
//        String image2 = "https://www.google.com/url?sa=i&url=http%3A%2F%2Fmediabitch.ru%2Fevent-details%2F&psig=AOvVaw1HpesCLwac2NcLOuTAL8FA&ust=1581246229426000&source=images&cd=vfe&ved=0CAIQjRxqFwoTCJDEjLHnwecCFQAAAAAdAAAAABAJ";
//
//        Event event1 = new Event(auth.getCurrentUser().getEmail(),"Party in John's house", "Really big night party at John", image1, "Nijniy Novgorod", new GregorianCalendar(2020,2, 7));
//        Event event2 = new Event(auth.getCurrentUser().getEmail(),"Music concert", "All favourites here! Lets go!", image2, "Nijniy Novgorod", new GregorianCalendar(2020,2, 17));
//
//
//        final FirebaseDatabase database = FirebaseDatabase.getInstance();
//
//        DatabaseReference ref = database.getReference();
//        DatabaseReference usersRef = ref.child("events");
//
//        //Need to move from the main thread‼️
//        usersRef.push().setValue(event1);
//        usersRef.push().setValue(event2);

    }

    private boolean checkEmailAndPassword(String email, String password) {
        if (TextUtils.isEmpty(email)) {
            Toast.makeText(getApplicationContext(), "Please enter email", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (TextUtils.isEmpty(password)) {
            Toast.makeText(getApplicationContext(), "Please enter password", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (password.length() < 6) {
            Toast.makeText(getApplicationContext(), "Password too short, enter minimum 6 characters!", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (!email.contains("@")) {
            Toast.makeText(getApplicationContext(), "Email incorrect", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }
}
