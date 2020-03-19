package com.tsa.EventMe;


import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.ListFragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

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


public class MyEventsFragment extends Fragment {
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
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(), 2);
        gridLayoutManager.setReverseLayout(true);
       // gridLayoutManager.setStackFromEnd(true);
        int spanCount = 3; // 3 columns
        int spacing = 50; // 50px
        myEventList.addItemDecoration(new GridSpacingItemDecoration(spanCount, spacing, true));
        myEventList.setLayoutManager(gridLayoutManager);
        auth = FirebaseAuth.getInstance();
        currentUserId = auth.getUid();
        eventsRef = FirebaseDatabase.getInstance().getReference().child("users").child(currentUserId).child("events");
        EVENTSRef = FirebaseDatabase.getInstance().getReference().child("users").child(currentUserId).child("events");
        return EventsView;
    }


    @Override
    public void onStart() {
        super.onStart();

        FirebaseRecyclerOptions option = new FirebaseRecyclerOptions.Builder<Event>()
                .setQuery(eventsRef, Event.class)
                .build();


        final FirebaseRecyclerAdapter<Event, AllEventsFragment.EventsViewHolder> adapter =
                new FirebaseRecyclerAdapter<Event, AllEventsFragment.EventsViewHolder>(option) {


                    @Override
                    protected void onBindViewHolder(@NonNull final AllEventsFragment.EventsViewHolder holder, int position, @NonNull Event model) {

                        String evetnsIDs = getRef(position).getKey();

                        EVENTSRef.child(evetnsIDs).addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {


                                final String eventImage = dataSnapshot.child("image").getValue().toString();
                                final String eventTopic = dataSnapshot.child("topic").getValue().toString();
                                final String eventDate = dataSnapshot.child("day").getValue().toString() +"."+
                                        dataSnapshot.child("month").getValue().toString() + "."+
                                        dataSnapshot.child("year").getValue().toString();

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


                                holder.itemView.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        Intent detailsIntent = new Intent(getContext(), DetailsActivity.class );
                                        detailsIntent.putExtra("event_photo", eventImage);
                                        detailsIntent.putExtra("event_topic", eventTopic);
                                        detailsIntent.putExtra("event_date", eventDate);
                                        detailsIntent.putExtra("event_description", description);
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
                    public AllEventsFragment.EventsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item, parent, false);
                        AllEventsFragment.EventsViewHolder viewHolder = new AllEventsFragment.EventsViewHolder(view);
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
