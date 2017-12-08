package com.example.q_thjen.mmessage.Activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.example.q_thjen.mmessage.R;

public class StartActivity extends AppCompatActivity {

    private Button bt_create;
    private Button bt_sign;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);

        bt_create = (Button) findViewById(R.id.bt_create);
        bt_sign = (Button) findViewById(R.id.bt_sign);

        bt_create.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(StartActivity.this, RegisterActivity.class);
                startActivity(intent);

            }
        });

        bt_sign.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(StartActivity.this, LoginActivity.class);
                startActivity(intent);

            }
        });

    }

}
