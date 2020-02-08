package com.tsa.EventMe;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    FirebaseAuth auth;
    FirebaseDatabase database;
    DatabaseReference ref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        auth = FirebaseAuth.getInstance();
        Toast.makeText(this, auth.getUid(), Toast.LENGTH_LONG).show();
        if(getIntent().getExtras() != null) {
            String possibleEmail = getIntent().getExtras().getString("email");
            if( possibleEmail!= null) {
                //Create new Person
                FirebaseDatabase database = FirebaseDatabase.getInstance();
                DatabaseReference ref = database.getReference();
                DatabaseReference usersRef = ref.child("users");
                //DatabaseReference user = usersRef.child(possibleEmail);
                Person newPerson = new Person(possibleEmail);
                usersRef.push().setValue(newPerson);
                auth = FirebaseAuth.getInstance();
                generateData();
            }
        }









    }


    //Create test data and push it to Firebase Database
    public void generateData() {
        String image1 = "https://www.google.com/url?sa=i&url=https%3A%2F%2Fwww.forbes.com%2Fsites%2Fmarkgreene%2F2019%2F10%2F11%2Fhow-to-buy-a-house-with-10000%2F&psig=AOvVaw21g7PCnoitEJ4uGWGesgSt&ust=1581243678106000&source=images&cd=vfe&ved=0CAIQjRxqFwoTCLCNmfDdwecCFQAAAAAdAAAAABAD";
        String image2 = "https://www.google.com/url?sa=i&url=http%3A%2F%2Fmediabitch.ru%2Fevent-details%2F&psig=AOvVaw1HpesCLwac2NcLOuTAL8FA&ust=1581246229426000&source=images&cd=vfe&ved=0CAIQjRxqFwoTCJDEjLHnwecCFQAAAAAdAAAAABAJ";

        Event event1 = new Event(auth.getCurrentUser().getUid(), auth.getCurrentUser().getEmail(),"Party in John's house", "Really big night party at John", image1, "Nijniy Novgorod", new GregorianCalendar(2020,2, 7));
        Event event2 = new Event(auth.getCurrentUser().getUid(), auth.getCurrentUser().getEmail(),"Music concert", "All favourites here! Lets go!", image2, "Nijniy Novgorod", new GregorianCalendar(2020,2, 17));

        database = FirebaseDatabase.getInstance();

        ref = database.getReference().child("users").child(auth.getCurrentUser().getUid()).child("events");
        DatabaseReference allEvents = database.getReference().child("events");
        ref.push().setValue(event1);
        ref.push().setValue(event2);
        allEvents.push().setValue(event1);
        allEvents.push().setValue(event2);

    }
}
