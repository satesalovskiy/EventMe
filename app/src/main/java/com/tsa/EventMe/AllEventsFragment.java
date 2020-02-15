package com.tsa.EventMe;


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
import android.widget.ListView;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.List;


public class AllEventsFragment extends Fragment {
    private View EventsView;
    private RecyclerView myEventList;
    private DatabaseReference eventsRef, EVENTSRef;
    private FirebaseAuth auth;
    private String currentUserId;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        EventsView =  inflater.inflate(R.layout.test, container, false);

        myEventList = (RecyclerView) EventsView.findViewById(R.id.events_list);
        myEventList.setLayoutManager(new LinearLayoutManager(getContext()));


        auth = FirebaseAuth.getInstance();
        currentUserId = auth.getUid();


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


        FirebaseRecyclerAdapter<Event, EventsViewHolder> adapter =
                new FirebaseRecyclerAdapter<Event, EventsViewHolder>(option) {

                    @Override
                    protected void onBindViewHolder(@NonNull final EventsViewHolder holder, int position, @NonNull Event model) {

                        String evetnsIDs = getRef(position).getKey();

                        EVENTSRef.child(evetnsIDs).addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                String eventImage = dataSnapshot.child("image").getValue().toString();
                                String eventTopic = dataSnapshot.child("topic").getValue().toString();
                                String eventDate = dataSnapshot.child("day").getValue().toString() +
                                        dataSnapshot.child("month").getValue().toString() +
                                        dataSnapshot.child("year").getValue().toString();

                                holder.eventTopic.setText(eventTopic);
                                holder.eventDate.setText(eventDate);

                                //Растягивать до определенного размера в Picasso
                                Picasso.get()
                                        .load(Uri.parse(eventImage))
                                        .placeholder(R.drawable.defaultimage)
                                        .fit()
                                        .centerCrop()
                                        .into(holder.eventImage)
                                        ;

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
