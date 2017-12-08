package com.example.q_thjen.mmessage.Activity;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.ContextThemeWrapper;
import android.view.View;
import android.widget.TextView;

import com.example.q_thjen.mmessage.Model.User;
import com.example.q_thjen.mmessage.R;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class UsersActivity extends AppCompatActivity {

    private Toolbar tbar_user;
    private RecyclerView recyclerView;

    private DatabaseReference dataRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_users);

        BeginMethod();

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

    }

    private void BeginMethod() {

        tbar_user = findViewById(R.id.tbar_user);
        setSupportActionBar(tbar_user);
        getSupportActionBar().setTitle("Users");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        recyclerView = findViewById(R.id.recyclerView_user);
        dataRef = FirebaseDatabase.getInstance().getReference().child("Users");

    }

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseRecyclerAdapter<User, UsersViewHolder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<User, UsersViewHolder>(

                User.class,
                R.layout.user_layout,
                UsersViewHolder.class,
                dataRef
        ) {
            @Override
            protected void populateViewHolder(UsersViewHolder usersViewHolder, User user, int position) {

                usersViewHolder.SetName(user.getName());
                usersViewHolder.SetUserStatus(user.getStatus());
                usersViewHolder.SetImageUser(user.getThumb_image(), getApplicationContext());

                final String uid = getRef(position).getKey();

                usersViewHolder.myView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        Intent intent = new Intent(UsersActivity.this, ProfileUsersActivity.class);
                        intent.putExtra("userUid", uid);
                        startActivity(intent);
                    }
                });
            }
        };

        recyclerView.setAdapter(firebaseRecyclerAdapter);

    }

    public static class UsersViewHolder extends RecyclerView.ViewHolder {

        View myView;

        public UsersViewHolder(View itemView) {
            super(itemView);

            myView = itemView;
        }

        public void SetName(String name) {

            TextView tvName = myView.findViewById(R.id.tv_nameuser);
            tvName.setText(name);
        }

        public void SetUserStatus(String status) {

            TextView tvStatus = myView.findViewById(R.id.tv_statususer);
            tvStatus.setText(status);
        }

        public void SetImageUser(String link, Context context) {

            CircleImageView civUser = myView.findViewById(R.id.civ_user);
            Picasso.with(context).load(link).placeholder(R.drawable.user).into(civUser);
        }

    }

}
