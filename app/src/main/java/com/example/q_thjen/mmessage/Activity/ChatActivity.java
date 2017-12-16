package com.example.q_thjen.mmessage.Activity;

import android.content.Context;
import android.content.Intent;
import android.os.Message;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.q_thjen.mmessage.Adapter.MyMessageAdapter;
import com.example.q_thjen.mmessage.Model.MyMessage;
import com.example.q_thjen.mmessage.R;
import com.example.q_thjen.mmessage.Utils.GetTimeAgo;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChatActivity extends AppCompatActivity {

    private String mUserUid;

    /** view activity **/
    private Toolbar tbar_chat;
    private ImageView mIv_sendChat, mIv_addImageChat;
    private EditText mEt_chat;
    private RecyclerView mRecyclerChat;
    private SwipeRefreshLayout mRefreshLL;

    private List<MyMessage> mListMessage = new ArrayList<>();
    private LinearLayoutManager mLinearLayoutManager;
    private MyMessageAdapter mAdapter;

    /** view custom toolbar **/
    private TextView tv_nameTbar, tv_statusTbar;
    private CircleImageView civ_tbar;

    private DatabaseReference mDataRef;
    private FirebaseAuth mAuth;

    private String mCurrentUserId;

    private static final int TOTAL_ITEMS_TO_LOAD = 10;
    private int mCurrentPage = 1;

    private int itemPos = 0;

    private String mLastKey = "";

    private int GALLERY_PICKER = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        tbar_chat = findViewById(R.id.tbar_chat);
        mIv_sendChat = findViewById(R.id.iv_sendChat);
        mIv_addImageChat = findViewById(R.id.iv_addImageChat);
        mEt_chat = findViewById(R.id.et_chat);
        mRecyclerChat = findViewById(R.id.recycler_chat);
        mRefreshLL = findViewById(R.id.swipeRefreshLayoutChat);

        mDataRef = FirebaseDatabase.getInstance().getReference();

        mAuth = FirebaseAuth.getInstance();
        mCurrentUserId = mAuth.getCurrentUser().getUid();

        mUserUid = getIntent().getExtras().getString("userUid");

        String userName = getIntent().getExtras().getString("userName");

        /** set adapter **/
        mAdapter = new MyMessageAdapter(mListMessage);

        mLinearLayoutManager = new LinearLayoutManager(this);
        mRecyclerChat.setHasFixedSize(true);
        mRecyclerChat.setLayoutManager(mLinearLayoutManager);

        mRecyclerChat.setAdapter(mAdapter);

        loadMessage();

        setSupportActionBar(tbar_chat);
        /** TODO: custom actionbar **/
        ActionBar actionBar = getSupportActionBar();

        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowCustomEnabled(true);

        LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View actionBarView = inflater.inflate(R.layout.custom_toolbar, null);
        actionBar.setCustomView(actionBarView);

        /** custom toolbar **/
        civ_tbar = findViewById(R.id.civ_tbar);
        tv_statusTbar = findViewById(R.id.tv_statusTbarChat);
        tv_nameTbar = findViewById(R.id.tv_nameTbarChat);

        tv_nameTbar.setText(userName);

        mDataRef.child("Users").child(mUserUid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                String online = dataSnapshot.child("online").getValue().toString();
                String thumb = dataSnapshot.child("image").getValue().toString();

                if (online.equals("true")) {
                    tv_statusTbar.setText("Online");
                } else {

                    GetTimeAgo getTimeAgo = new GetTimeAgo();

                    long lastTime = Long.parseLong(online);

                    String lastSeenTime = getTimeAgo.getTimeAgo(lastTime, getApplicationContext());

                    tv_statusTbar.setText(lastSeenTime);
                }

                Picasso.with(ChatActivity.this).load(thumb).placeholder(R.drawable.user).into(civ_tbar);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        mDataRef.child("Chat").child(mCurrentUserId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                if ( !dataSnapshot.hasChild(mUserUid)) {

                    Map chatAddMap = new HashMap();
                    chatAddMap.put("seen", false);
                    chatAddMap.put("timestamp", ServerValue.TIMESTAMP);

                    Map chatUserMap = new HashMap();
                    chatUserMap.put("Chat/" + mCurrentUserId + "/" + mUserUid, chatAddMap);
                    chatUserMap.put("Chat/" + mUserUid + "/" + mCurrentUserId, chatAddMap);

                    mDataRef.updateChildren(chatUserMap, new DatabaseReference.CompletionListener() {
                        @Override
                        public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {

                            if ( databaseError != null ) {

                            }

                        }
                    });

                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        mIv_sendChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendMessage();
                mEt_chat.setText("");
            }
        });

        mRefreshLL.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {

                mCurrentPage++;

                itemPos = 0;

                loadMoreMessage();

            }
        });

        mIv_addImageChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "SELECT IMAGE"), GALLERY_PICKER);

            }
        });

    }

    private void loadMoreMessage() {

        DatabaseReference messageRef = mDataRef.child("message").child(mCurrentUserId).child(mUserUid);

        Query query = messageRef.orderByKey().endAt(mLastKey).limitToLast(10);

        query.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {

                MyMessage myMessage = dataSnapshot.getValue(MyMessage.class);

                mListMessage.add( itemPos++, myMessage);

                if (itemPos == 1) {

                    String messageKey = dataSnapshot.getKey();

                    mLastKey = messageKey;
                }

                mAdapter.notifyDataSetChanged();

                mRefreshLL.setRefreshing(false);

                mLinearLayoutManager.scrollToPositionWithOffset(10,0);

            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    private void loadMessage() {

        DatabaseReference messageRef = mDataRef.child("message").child(mCurrentUserId).child(mUserUid);

        Query query = messageRef.limitToLast(mCurrentPage * TOTAL_ITEMS_TO_LOAD);

        query.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {

                MyMessage myMessage = dataSnapshot.getValue(MyMessage.class);

                itemPos++;

                if (itemPos == 1) {

                    String messageKey = dataSnapshot.getKey();

                    mLastKey = messageKey;
                }

                mListMessage.add(myMessage);
                mAdapter.notifyDataSetChanged();

                mRecyclerChat.scrollToPosition(mListMessage.size() - 1);

                mRefreshLL.setRefreshing(false);

            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    private void sendMessage() {

        String message = mEt_chat.getText().toString();
        if (!TextUtils.isEmpty(message)) {

            String current_user = "message/" + mCurrentUserId + "/" + mUserUid;
            String chat_user = "message/" + mUserUid + "/" + mCurrentUserId;

            DatabaseReference user_message_push = mDataRef.child("message")
                    .child(mCurrentUserId).child(mUserUid).push();

            String push_id = user_message_push.getKey();

            Map messageMap = new HashMap();
            messageMap.put("message", message);
            messageMap.put("seen", false);
            messageMap.put("type", "text");
            messageMap.put("time", ServerValue.TIMESTAMP);
            messageMap.put("from", mCurrentUserId);

            Map messageUserMap = new HashMap();
            messageUserMap.put(current_user + "/" + push_id, messageMap);
            messageUserMap.put(chat_user + "/" + push_id, messageMap);

            mDataRef.updateChildren(messageUserMap, new DatabaseReference.CompletionListener() {
                @Override
                public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {

                    if ( databaseError != null) {

                    }
                }
            });

        }

    }

}
