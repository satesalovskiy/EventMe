package com.tsa.EventMe;


import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.ListFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.List;


public class AllEventsFragment extends Fragment {
    private View EventsView;
    private RecyclerView myEventList;
    private DatabaseReference EVENTSRef;
    private Query eventsRef;
    private FirebaseAuth auth;
    private String currentUserId;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        EventsView =  inflater.inflate(R.layout.test, container, false);

        myEventList = (RecyclerView) EventsView.findViewById(R.id.events_list);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);

        //=======
        layoutManager.setReverseLayout(true);
        layoutManager.setStackFromEnd(true);
        //=======

        myEventList.setLayoutManager(layoutManager);


        auth = FirebaseAuth.getInstance();
        currentUserId = auth.getUid();

        //Как то так мб?
        eventsRef = FirebaseDatabase.getInstance().getReference().child("events");


        EVENTSRef = FirebaseDatabase.getInstance().getReference().child("events");


        return EventsView;
    }


    @Override
    public void onStart() {
        super.onStart();

        FirebaseRecyclerOptions option = new FirebaseRecyclerOptions.Builder<Event>()
                .setQuery(eventsRef, Event.class)
                .build();


        final FirebaseRecyclerAdapter<Event, EventsViewHolder> adapter =
                new FirebaseRecyclerAdapter<Event, EventsViewHolder>(option) {


                    @Override
                    protected void onBindViewHolder(@NonNull final EventsViewHolder holder, int position, @NonNull Event model) {

                        String evetnsIDs = getRef(position).getKey();


                        EVENTSRef.child(evetnsIDs).addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {


                                final String eventImage = dataSnapshot.child("image").getValue().toString();
                                final String eventTopic = dataSnapshot.child("topic").getValue().toString();
                                final String eventDate = dataSnapshot.child("day").getValue().toString() +"."+
                                        dataSnapshot.child("month").getValue().toString() + "."+
                                        dataSnapshot.child("year").getValue().toString();

                                final String eventDay = dataSnapshot.child("day").getValue().toString();
                                final String eventMonth = dataSnapshot.child("month").getValue().toString();
                                final String eventYear = dataSnapshot.child("year").getValue().toString();

                                holder.eventTopic.setText(eventTopic);
                                holder.eventDate.setText(eventDate);
                                dataSnapshot.getChildrenCount();


                                Picasso.get()
                                        .load(Uri.parse(eventImage))
                                        .placeholder(R.drawable.defaultimage)
                                        .fit()
                                        .centerInside()
                                        .into(holder.eventImage)
                                        ;


                                final String description = dataSnapshot.child("description").getValue().toString();
                                final String eventID = dataSnapshot.getRef().getKey();


                                holder.itemView.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        Toast.makeText(getContext(), eventID, Toast.LENGTH_SHORT).show();

                                        Intent detailsIntent = new Intent(getContext(), DetailsActivity.class );
                                        detailsIntent.putExtra("event_photo", eventImage);
                                        detailsIntent.putExtra("event_topic", eventTopic);
                                        detailsIntent.putExtra("event_day", eventDay);
                                        detailsIntent.putExtra("event_month", eventMonth);
                                        detailsIntent.putExtra("event_year", eventYear);

                                        detailsIntent.putExtra("event_description", description);
                                        detailsIntent.putExtra("event_ref", eventID);

                                        startActivity(detailsIntent);
                                    }
                                });

                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });
                    }

                    @NonNull
                    @Override
                    public EventsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item, parent, false);
                        EventsViewHolder viewHolder = new EventsViewHolder(view);
                        return  viewHolder;
                    }
                };

        myEventList.setAdapter(adapter);

        adapter.startListening();

    }

    public static class EventsViewHolder extends RecyclerView.ViewHolder {

        TextView eventDate, eventTopic;
        ImageView eventImage;



        public EventsViewHolder(@NonNull View itemView) {
            super(itemView);

            eventDate = (TextView) itemView.findViewById(R.id.eventDate);
            eventTopic = (TextView) itemView.findViewById(R.id.eventTopic);
            eventImage = (ImageView) itemView.findViewById(R.id.eventImage);


        }
    }
}
