package com.example.q_thjen.mmessage.Fragment;


import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.q_thjen.mmessage.Activity.ChatActivity;
import com.example.q_thjen.mmessage.Activity.ProfileUsersActivity;
import com.example.q_thjen.mmessage.Activity.UsersActivity;
import com.example.q_thjen.mmessage.Model.Friends;
import com.example.q_thjen.mmessage.R;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * A simple {@link Fragment} subclass.
 */
public class FriendsFragment extends Fragment {

    private RecyclerView mRecyclerFriends;

    private DatabaseReference mFriendsRef;
    private FirebaseAuth mAuth;
    private DatabaseReference mUserRef;

    private String mCurrent_uid;
    private View mMainView;

    public FriendsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mMainView = inflater.inflate(R.layout.fragment_friends, container, false);

        mRecyclerFriends = mMainView.findViewById(R.id.recycler_friends);
        mAuth = FirebaseAuth.getInstance();

        mCurrent_uid = mAuth.getCurrentUser().getUid();

        mFriendsRef = FirebaseDatabase.getInstance().getReference().child("Friends").child(mCurrent_uid);
        mUserRef = FirebaseDatabase.getInstance().getReference().child("Users");
        mUserRef.keepSynced(true);

        mRecyclerFriends.setHasFixedSize(true);
        mRecyclerFriends.setLayoutManager(new LinearLayoutManager(getContext()));

        return mMainView;
    }

    @Override
    public void onStart() {
        super.onStart();

        FirebaseRecyclerAdapter<Friends, FriendsViewHolder> friendsAdapter = new FirebaseRecyclerAdapter<Friends, FriendsViewHolder>(

                Friends.class,
                R.layout.friends_layout,
                FriendsViewHolder.class,
                mFriendsRef

        ) {
            @Override
            protected void populateViewHolder(final FriendsViewHolder viewHolder, Friends friends, int position) {

                viewHolder.setDate(friends.getDate());
                final String list_uid = getRef(position).getKey();

                mUserRef.child(list_uid).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        final String userName = dataSnapshot.child("name").getValue().toString();
                        String userThumb = dataSnapshot.child("thumb_image").getValue().toString();

                        if (dataSnapshot.hasChild("online")) {
                            String statusUser = dataSnapshot.child("online").getValue().toString();
                            viewHolder.getStatus(statusUser);
                        }
                        viewHolder.setName(userName);
                        viewHolder.setThumb(userThumb, getContext());

                        viewHolder.mView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {

                                CharSequence option[] = new CharSequence[]{"Open profile", "Send message"};
                                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                                builder.setTitle("Select options");
                                builder.setItems(option, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {

                                        switch ( which ) {

                                            case 0:
                                                Intent intent = new Intent(getContext(), ProfileUsersActivity.class);
                                                intent.putExtra("userUid", list_uid);
                                                startActivity(intent);
                                                break;

                                            case 1:
                                                Intent intent1 = new Intent(getContext(), ChatActivity.class);
                                                intent1.putExtra("userUid", list_uid);
                                                intent1.putExtra("userName", userName);
                                                startActivity(intent1);
                                                break;

                                        }

                                    }
                                });

                                builder.show();

                            }
                        });

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

            }
        };

        mRecyclerFriends.setAdapter(friendsAdapter);

    }

    public static class FriendsViewHolder extends RecyclerView.ViewHolder {

        View mView;

        public FriendsViewHolder(View itemView) {
            super(itemView);

            mView = itemView;
        }

        public void setDate(String date) {

            TextView userName = mView.findViewById(R.id.tv_statusFriendsLayout);
            userName.setText(date);

        }

        public void setName(String name) {

            TextView tvName = mView.findViewById(R.id.tv_nameFriendsLayout);
            tvName.setText(name);

        }

        public void setThumb(String thumb, Context context) {

            CircleImageView circleThumb = mView.findViewById(R.id.civ_friendsLayout);
            Picasso.with(context).load(thumb).placeholder(R.drawable.user).into(circleThumb);

        }

        public void getStatus(String onlineStatus) {

            ImageView ivStatus = mView.findViewById(R.id.civ_online);
            if ( onlineStatus.equals("true") ) {
                ivStatus.setVisibility(View.VISIBLE);
            } else {
                ivStatus.setVisibility(View.INVISIBLE);
            }

        }

    }

}
