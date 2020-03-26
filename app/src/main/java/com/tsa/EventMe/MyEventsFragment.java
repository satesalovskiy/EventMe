package com.tsa.EventMe;


import android.app.AlertDialog;
import android.content.DialogInterface;
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
import android.widget.Toast;


import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;


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
        gridLayoutManager.setReverseLayout(false);
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

                                if(dataSnapshot.child("topic").getValue() != null) {


                                   // final Object eventCreatorPhoto = dataSnapshot.child("userImage").getValue();
                                   // final String eventCreatorEmail = dataSnapshot.child("userEmail").getValue().toString();
                                    final String eventImage = dataSnapshot.child("image").getValue().toString();
                                    final String eventTopic = dataSnapshot.child("topic").getValue().toString();

                                    final String eventDay = dataSnapshot.child("day").getValue().toString();
                                    final String eventMonth = dataSnapshot.child("month").getValue().toString();
                                    final String eventYear = dataSnapshot.child("year").getValue().toString();


                                    holder.eventTopic.setText(eventTopic);
                                   //holder.eventCreatorEmail.setText(eventCreatorEmail);


                                    dataSnapshot.getChildrenCount();

//                                    if(eventCreatorPhoto == null) {
//                                        Picasso.get()
//                                                .load(R.drawable.defaultimage)
//                                                .placeholder(R.drawable.defaultimage)
//                                                .fit()
//                                                .centerInside()
//                                                .into(holder.eventCreatorPhoto);
//                                    } else {
//                                        Picasso.get()
//                                                .load(Uri.parse(eventCreatorPhoto.toString()))
//                                                .placeholder(R.drawable.defaultimage)
//                                                .fit()
//                                                .centerInside()
//                                                .into(holder.eventCreatorPhoto);
//                                    }


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
                                        public void onItemLongClick (View view, int position) {

                                            String currentTitle = getItem(position).getTopic();
                                            String currentImage = getItem(position).getImage();
                                            showDeleteDataDialog(currentTitle, currentImage);


                                        }
                                    });


                                    holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                                        @Override
                                        public boolean onLongClick(View view) {

                                            showDeleteDataDialog(eventTopic, eventImage);
                                            return true;
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
                    public AllEventsFragment.EventsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.one_more_list_item, parent, false);
                        AllEventsFragment.EventsViewHolder viewHolder = new AllEventsFragment.EventsViewHolder(view);
                        return  viewHolder;
                    }
                };

        myEventList.setAdapter(adapter);

        adapter.startListening();

    }


    private void showDeleteDataDialog(final String currentTitle, final String currentImage) {

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Delete");
        builder.setMessage("Are you sure to delete this post?");
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

                Query mQuery = EVENTSRef.orderByChild("topic").equalTo(currentTitle);
                mQuery.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for(DataSnapshot ds: dataSnapshot.getChildren()) {
                            ds.getRef().removeValue();
                        }
                        Toast.makeText(getContext(), "Post deleted", Toast.LENGTH_SHORT).show();

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

                Query mQuery2 = FirebaseDatabase.getInstance().getReference().child("events").orderByChild("topic").equalTo(currentTitle);
                mQuery2.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for(DataSnapshot ds: dataSnapshot.getChildren()) {
                            ds.getRef().removeValue();
                        }
                        Toast.makeText(getContext(), "Post deleted", Toast.LENGTH_SHORT).show();

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });


                StorageReference mPictureRef = FirebaseStorage.getInstance().getReferenceFromUrl(currentImage);
                mPictureRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {

                    }
                });

            }
        });
        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });


        builder.create().show();

    }

    public static class EventsViewHolder extends RecyclerView.ViewHolder {

        TextView eventTopic, eventCreatorEmail;
        ImageView eventImage;
        CircleImageView eventCreatorPhoto;

        public EventsViewHolder(@NonNull View itemView) {
            super(itemView);
            eventTopic = (TextView) itemView.findViewById(R.id.eventTopic);
            eventImage = (ImageView) itemView.findViewById(R.id.eventImage);
            //eventCreatorEmail = itemView.findViewById(R.id.eventCreatorEmail);
            //eventCreatorPhoto = itemView.findViewById(R.id.eventCreatorPhoto);
        }
    }

}
