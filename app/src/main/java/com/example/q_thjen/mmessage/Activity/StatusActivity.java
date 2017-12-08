package com.example.q_thjen.mmessage.Activity;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;

import com.example.q_thjen.mmessage.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.sdsmdg.tastytoast.TastyToast;

public class StatusActivity extends AppCompatActivity {

    private FirebaseUser user;
    private DatabaseReference dataRef;

    private TextInputLayout et_stUpdate;
    private Toolbar tbar_stUpdate;
    private Button bt_stUpdate;

    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_status);

        et_stUpdate = findViewById(R.id.et_statusUpdate);
        tbar_stUpdate = findViewById(R.id.tbar_status);
        bt_stUpdate = findViewById(R.id.bt_stUpdate);

        setSupportActionBar(tbar_stUpdate);
        getSupportActionBar().setTitle(getRes(R.string.acStatus));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        user = FirebaseAuth.getInstance().getCurrentUser();
        String uid = user.getUid();
        dataRef = FirebaseDatabase.getInstance().getReference().child("Users").child(uid);

        String statusget = getIntent().getExtras().getString("mstatus");
        et_stUpdate.getEditText().setText(statusget);

        bt_stUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                progressDialog = new ProgressDialog(StatusActivity.this);
                progressDialog.setTitle(getRes(R.string.svChange));
                progressDialog.setMessage(getRes(R.string.waitSvStatus));
                progressDialog.setCanceledOnTouchOutside(false);
                progressDialog.show();

                String status = et_stUpdate.getEditText().getText().toString().trim();

                dataRef.child("status").setValue(status).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {

                        if ( task.isSuccessful() ) {
                            progressDialog.dismiss();
                        } else {
                            progressDialog.hide();
                            TastyToast.makeText(StatusActivity.this, getRes(R.string.someErrorSv), TastyToast.LENGTH_SHORT, TastyToast.ERROR);
                        }

                    }
                });

            }
        });

    }

    private String getRes(int numberString) {

        return getResources().getString(numberString);
    }

}
