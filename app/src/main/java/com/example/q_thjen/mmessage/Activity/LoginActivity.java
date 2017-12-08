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
import com.sdsmdg.tastytoast.TastyToast;

public class LoginActivity extends AppCompatActivity {

    private TextInputLayout et_emailLogin, et_passLogin;
    private Button bt_sign;
    private Toolbar tbar_login;

    private FirebaseAuth auth;

    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        et_emailLogin = findViewById(R.id.et_accountSign);
        et_passLogin = findViewById(R.id.et_passSign);
        bt_sign = findViewById(R.id.bt_LoginDone);
        tbar_login = findViewById(R.id.tbar_login);

        auth = FirebaseAuth.getInstance();

        progressDialog = new ProgressDialog(LoginActivity.this);

        setSupportActionBar(tbar_login);
        getSupportActionBar().setTitle(getResources().getString(R.string.signin));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        bt_sign.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String email = et_emailLogin.getEditText().getText().toString().trim();
                String pass = et_passLogin.getEditText().getText().toString().trim();

                if ( !TextUtils.isEmpty(email) || !TextUtils.isEmpty(pass) ) {
                /** show dialog when login **/
                    progressDialog.setTitle(getResources().getString(R.string.logging));
                    progressDialog.setMessage(getResources().getString(R.string.progressLogin));
                    progressDialog.setCanceledOnTouchOutside(false);
                    progressDialog.show();

                    SignIn(email, pass);
                } else {
                    progressDialog.hide();
                    TastyToast.makeText(LoginActivity.this, getResources().getString(R.string.pleaseEnter), TastyToast.LENGTH_SHORT, TastyToast.WARNING);
                }

            }
        });

    }

    private void SignIn(String email ,String pass ) {

        auth.signInWithEmailAndPassword(email, pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {

                if ( task.isSuccessful() ) {

                    progressDialog.dismiss();
                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                    /** add flag để xóa start activty nếu đăng nhập thành công sẽ xóa màn hình đăng nhập và tạo tài khoản **/
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish();

                } else {
                    progressDialog.hide();
                    TastyToast.makeText(LoginActivity.this, getResources().getString(R.string.someError), TastyToast.LENGTH_SHORT, TastyToast.ERROR);
                }

            }
        });

    }

}
