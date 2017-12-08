package com.example.q_thjen.mmessage.Activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;

import com.example.q_thjen.mmessage.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.sdsmdg.tastytoast.TastyToast;

import java.util.HashMap;

public class RegisterActivity extends AppCompatActivity {

    private TextInputLayout et_nameCreate, et_accountCreate, et_passCreate;
    private Button bt_createAccount;
    private Toolbar tbar_register;

    private FirebaseAuth auth;
    private DatabaseReference dataRef;

    /** progress dialog create account **/
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        bt_createAccount = findViewById(R.id.bt_createDone);
        et_accountCreate = findViewById(R.id.et_accountCreate);
        et_nameCreate = findViewById(R.id.et_nameCreate);
        et_passCreate = findViewById(R.id.et_passCreate);
        tbar_register = findViewById(R.id.tbar_register);

        auth = FirebaseAuth.getInstance();

        progressDialog = new ProgressDialog(RegisterActivity.this);

        setSupportActionBar(tbar_register);
        getSupportActionBar().setTitle(getResources().getString(R.string.Create));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        bt_createAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String name = et_nameCreate.getEditText().getText().toString().trim();
                String email = et_accountCreate.getEditText().getText().toString().trim();
                String pass = et_passCreate.getEditText().getText().toString().trim();

                if ( !TextUtils.isEmpty(name) || !TextUtils.isEmpty(email) || !TextUtils.isEmpty(pass)) {

                    progressDialog.setTitle(getResources().getString(R.string.registeruser));
                    progressDialog.setMessage(getResources().getString(R.string.progressRegister));
                    progressDialog.setCanceledOnTouchOutside(false);
                    progressDialog.show();

                    RegisterAccount(name, email, pass);
                } else {

                    progressDialog.hide();
                    TastyToast.makeText(RegisterActivity.this, getResources().getString(R.string.pleaseEnter), TastyToast.LENGTH_SHORT, TastyToast.WARNING);
                }

            }
        });

    }

    private void RegisterAccount(final String name, String email, String pass ) {

        auth.createUserWithEmailAndPassword(email, pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {

                if ( task.isSuccessful() ) {
                /** add info user to firebase **/
                    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                    String uid = user.getUid();

                    dataRef = FirebaseDatabase.getInstance().getReference();

                    HashMap<String, String> map = new HashMap<>();
                    map.put("name", name);
                    map.put("status", "user status");
                    map.put("image", "user image");
                    map.put("thumb_image", "default");

                    dataRef.child("Users").child(uid).setValue(map).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {

                            if ( task.isSuccessful() ) {

                                progressDialog.dismiss();
                                Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(intent);
                                finish();

                            }

                        }
                    });

                } else {

                    progressDialog.hide();
                    TastyToast.makeText(RegisterActivity.this, getResources().getString(R.string.someError), TastyToast.LENGTH_SHORT, TastyToast.ERROR);
                }

            }
        });

    }

}
