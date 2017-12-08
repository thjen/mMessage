package com.example.q_thjen.mmessage.Activity;

import android.app.ProgressDialog;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.q_thjen.mmessage.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.sdsmdg.tastytoast.TastyToast;
import com.squareup.picasso.Picasso;

public class ProfileUsersActivity extends AppCompatActivity {

    private TextView mTv_nameProfile, mTv_statusProfile, mTv_total;
    private Button mBt_sendRequest, mBt_declineRequest;
    private ImageView mIv_profile;

    private DatabaseReference mDataRefUsers;
    private DatabaseReference mDataFriendRequest;
    private FirebaseUser mCurrentUser;

    private String mCurrenState;

    private ProgressDialog mProgress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_users);

        FindView();
        final String profileUid = getIntent().getExtras().getString("userUid");

        mDataRefUsers = FirebaseDatabase.getInstance().getReference().child("Users").child(profileUid);
        mDataFriendRequest = FirebaseDatabase.getInstance().getReference().child("Friend_request");
        mCurrentUser = FirebaseAuth.getInstance().getCurrentUser();

        mCurrenState = "not_friends";

        mProgress = new ProgressDialog(this);
        mProgress.setTitle("Loading user data");
        mProgress.setMessage("Please wait while we load the user data");
        mProgress.setCanceledOnTouchOutside(false);
        mProgress.show();

        mDataRefUsers.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                String name = dataSnapshot.child("name").getValue().toString();
                String image = dataSnapshot.child("image").getValue().toString();
                String status = dataSnapshot.child("status").getValue().toString();

                mTv_nameProfile.setText(name);
                mTv_statusProfile.setText(status);

                Picasso.with(ProfileUsersActivity.this).load(image).placeholder(R.drawable.userprofile).into(mIv_profile);

                /** TODO: xác nhận bạn bè khi được gửi lời mời kết bạn với user khác **/
                mDataFriendRequest.child(mCurrentUser.getUid())
                        .addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {

                                if ( dataSnapshot.hasChild(profileUid)) {

                                    String req_type = dataSnapshot.child(profileUid).child("request_type").getValue().toString();

                                    if ( req_type.equals("sent")) {

                                        mCurrenState = "req_sent";
                                        mBt_sendRequest.setText("Cancel sent request");

                                    } else if ( req_type.equals("received")) {

                                        mCurrenState = "req_received";
                                        mBt_sendRequest.setText("Accept friend request");

                                    }

                                }

                                mProgress.dismiss();

                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });

        mBt_sendRequest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                mBt_sendRequest.setEnabled(false);

                /** TODO: Gửi lời mời kết bạn nếu chưa gửi lời mời kết bạn với user này **/
                if ( mCurrenState.equals("not_friends")) {

                    mDataFriendRequest.child(mCurrentUser.getUid()).child(profileUid).child("request_type")
                            .setValue("sent").addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {

                            if (task.isSuccessful()) {

                                mDataFriendRequest.child(profileUid).child(mCurrentUser.getUid()).child("request_type")
                                        .setValue("received").addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {

                                        mBt_sendRequest.setEnabled(true);
                                        mCurrenState = "req_sent";
                                        mBt_sendRequest.setText("Cancel friend request");

                                        TastyToast.makeText(ProfileUsersActivity.this, "Request sent successfully", TastyToast.LENGTH_SHORT, TastyToast.SUCCESS);
                                    }
                                });

                            } else {
                                TastyToast.makeText(ProfileUsersActivity.this, "Failed sending request", TastyToast.LENGTH_SHORT, TastyToast.ERROR);
                            }

                        }
                    });

                }

                /** TODO: Hủy lời mời kết bạn với user này **/
                if ( mCurrenState.equals("req_sent")) {

                    mDataFriendRequest.child(mCurrentUser.getUid()).child(profileUid).removeValue()
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {

                                    mDataFriendRequest.child(profileUid).child(mCurrentUser.getUid()).removeValue()
                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void aVoid) {

                                                    mBt_sendRequest.setEnabled(true);
                                                    mCurrenState = "not_friends";
                                                    mBt_sendRequest.setText("Send friend request");

                                                    TastyToast.makeText(ProfileUsersActivity.this, "Cancel sent successfully", TastyToast.LENGTH_SHORT, TastyToast.SUCCESS);

                                                }
                                            });

                                }
                            });

                }

            }
        });

    }

    private void FindView() {

        mTv_nameProfile = findViewById(R.id.tv_nameProfile);
        mTv_statusProfile = findViewById(R.id.tv_statusProfile);
        mTv_total = findViewById(R.id.tv_totalFriend);
        mBt_sendRequest = findViewById(R.id.bt_sendRequest);
        mIv_profile = findViewById(R.id.iv_userProfile);
        mBt_declineRequest = findViewById(R.id.bt_declineRequest);

    }

}
