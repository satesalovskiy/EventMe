package com.tsa.EventMe;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TableLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.GregorianCalendar;

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
        if (getIntent().getExtras() != null) {
            String possibleEmail = getIntent().getExtras().getString("email");
            if (possibleEmail != null) {
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
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        SectionsPagerAdapter sectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());
        ViewPager viewPager = (ViewPager) findViewById(R.id.pager);
        viewPager.setAdapter(sectionsPagerAdapter);
        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);


    }


    //Create test data and push it to Firebase Database
    public void generateData() {
        String image1 = "https://www.google.com/url?sa=i&url=https%3A%2F%2Fwww.forbes.com%2Fsites%2Fmarkgreene%2F2019%2F10%2F11%2Fhow-to-buy-a-house-with-10000%2F&psig=AOvVaw21g7PCnoitEJ4uGWGesgSt&ust=1581243678106000&source=images&cd=vfe&ved=0CAIQjRxqFwoTCLCNmfDdwecCFQAAAAAdAAAAABAD";
        String image2 = "https://www.google.com/url?sa=i&url=http%3A%2F%2Fmediabitch.ru%2Fevent-details%2F&psig=AOvVaw1HpesCLwac2NcLOuTAL8FA&ust=1581246229426000&source=images&cd=vfe&ved=0CAIQjRxqFwoTCJDEjLHnwecCFQAAAAAdAAAAABAJ";

        Event event1 = new Event(auth.getCurrentUser().getUid(), auth.getCurrentUser().getEmail(), "Party in John's house", "Really big night party at John", image1, "Nijniy Novgorod", new GregorianCalendar(2020, 2, 7));
        Event event2 = new Event(auth.getCurrentUser().getUid(), auth.getCurrentUser().getEmail(), "Music concert", "All favourites here! Lets go!", image2, "Nijniy Novgorod", new GregorianCalendar(2020, 2, 17));

        database = FirebaseDatabase.getInstance();

        ref = database.getReference().child("users").child(auth.getCurrentUser().getUid()).child("events");
        DatabaseReference allEvents = database.getReference().child("events");
        ref.push().setValue(event1);
        ref.push().setValue(event2);
        allEvents.push().setValue(event1);
        allEvents.push().setValue(event2);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_account:
                startActivity(new Intent(this, ProfileActivity.class));
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }



    private class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }
        @Override
        public int getCount() {
            return 2;
        }
        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    return new AllEventsFragment();
                case 1:
                    return new MyEventsFragment();
            }
            return null;
        }
        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return getResources().getText(R.string.all_tab);
                case 1:
                    return getResources().getText(R.string.my_tab);

            }
            return null;
        }
    }


    public void onClickFloatingButton(View view) {
        startActivity(new Intent(this, CreateEvent.class));
    }

}
