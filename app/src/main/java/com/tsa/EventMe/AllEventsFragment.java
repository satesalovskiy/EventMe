package com.tsa.EventMe;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class AllEventsFragment extends Fragment {

    private DatabaseReference EVENTSRef;
    private Query eventsRefQuery;
    private RecyclerView myEventList;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        layoutManager.setReverseLayout(true);
        layoutManager.setStackFromEnd(true);

        View EventsView = inflater.inflate(R.layout.test, container, false);

        myEventList = EventsView.findViewById(R.id.events_list);
        myEventList.addItemDecoration(new SpacesItemDecoration(20));
        myEventList.setLayoutManager(layoutManager);

        eventsRefQuery = FirebaseDatabase.getInstance().getReference().child("events");
        EVENTSRef = FirebaseDatabase.getInstance().getReference().child("events");

        return EventsView;
    }


    @Override
    public void onStart() {
        super.onStart();

        FirebaseRecyclerOptions option = new FirebaseRecyclerOptions.Builder<Event>()
                .setQuery(eventsRefQuery, Event.class)
                .build();


        FirebaseRecyclerAdapter<Event, EventsViewHolder> adapter =
                new FirebaseRecyclerAdapter<Event, EventsViewHolder>(option) {

                    @Override
                    protected void onBindViewHolder(@NonNull final EventsViewHolder holder, final int position, @NonNull Event model) {

                        String eventsIDs = getRef(position).getKey();

                        EVENTSRef.child(eventsIDs).addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                                if (dataSnapshot.child("topic").getValue() != null) {

                                    final Object eventCreatorPhoto = dataSnapshot.child("userImage").getValue();
                                    final String eventCreatorEmail = dataSnapshot.child("userEmail").getValue().toString();
                                    final String eventImage = dataSnapshot.child("image").getValue().toString();
                                    final String eventTopic = dataSnapshot.child("topic").getValue().toString();
                                    final String eventDate = dataSnapshot.child("day").getValue().toString() + "." +
                                            dataSnapshot.child("month").getValue().toString() + "." +
                                            dataSnapshot.child("year").getValue().toString();
                                    final String eventDay = dataSnapshot.child("day").getValue().toString();
                                    final String eventMonth = dataSnapshot.child("month").getValue().toString();
                                    final String eventYear = dataSnapshot.child("year").getValue().toString();
                                    final String description = dataSnapshot.child("description").getValue().toString();
                                    final String eventID = dataSnapshot.getRef().getKey();

                                    holder.eventTopic.setText(eventTopic);
                                    holder.eventDate.setText(eventDate);
                                    holder.eventCreatorEmail.setText(eventCreatorEmail);

                                    dataSnapshot.getChildrenCount();

                                    final String eventCreatorImage;
                                    if (eventCreatorPhoto == null) {
                                        Picasso.get()
                                                .load(R.drawable.defaultimage)
                                                .placeholder(R.drawable.defaultimage)
                                                .fit()
                                                .centerInside()
                                                .into(holder.eventCreatorPhoto);
                                        eventCreatorImage = null;
                                    } else {
                                        Picasso.get()
                                                .load(Uri.parse(eventCreatorPhoto.toString()))
                                                .placeholder(R.drawable.defaultimage)
                                                .fit()
                                                .centerInside()
                                                .into(holder.eventCreatorPhoto);
                                        eventCreatorImage = eventCreatorPhoto.toString();
                                    }

                                    Picasso.get()
                                            .load(Uri.parse(eventImage))
                                            .placeholder(R.drawable.qwe)
                                            .fit()
                                            .centerInside()
                                            .into(holder.eventImage);

                                    holder.itemView.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {
                                            Intent detailsIntent = new Intent(getContext(), DetailsActivity.class);
                                            detailsIntent.putExtra("event_photo", eventImage);
                                            detailsIntent.putExtra("event_topic", eventTopic);
                                            detailsIntent.putExtra("event_day", eventDay);
                                            detailsIntent.putExtra("event_month", eventMonth);
                                            detailsIntent.putExtra("event_year", eventYear);
                                            detailsIntent.putExtra("event_creator_email", eventCreatorEmail);
                                            detailsIntent.putExtra("event_description", description);
                                            detailsIntent.putExtra("event_ref", eventID);

                                            if (eventCreatorPhoto != null) {
                                                detailsIntent.putExtra("event_creator_photo", eventCreatorImage);
                                            }

                                            startActivity(detailsIntent);
                                        }
                                    });
                                }
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
                        return viewHolder;
                    }
                };

        myEventList.setAdapter(adapter);
        adapter.startListening();
    }

    static class EventsViewHolder extends RecyclerView.ViewHolder {

        TextView eventDate, eventTopic, eventCreatorEmail;
        ImageView eventImage;
        CircleImageView eventCreatorPhoto;

        EventsViewHolder(@NonNull View itemView) {
            super(itemView);

            eventDate = itemView.findViewById(R.id.eventDate);
            eventTopic = itemView.findViewById(R.id.eventTopic);
            eventImage = itemView.findViewById(R.id.eventImage);
            eventCreatorEmail = itemView.findViewById(R.id.eventCreatorEmail);
            eventCreatorPhoto = itemView.findViewById(R.id.eventCreatorPhoto);
        }
    }
}
