package com.example.q_thjen.mmessage.Activity;

import android.content.Intent;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.example.q_thjen.mmessage.Adapter.ViewPaperAdapter;
import com.example.q_thjen.mmessage.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {

    private FirebaseAuth auth;

    private Toolbar tbar_main;
    private ViewPaperAdapter adapter;
    private ViewPager viewPager;
    private TabLayout tabLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tbar_main = (Toolbar) findViewById(R.id.tbar_main);
        setSupportActionBar(tbar_main);
        getSupportActionBar().setTitle(getResources().getString(R.string.home));

        viewPager = findViewById(R.id.viewpp_main);
        tabLayout = findViewById(R.id.tablayout_main);

        auth = FirebaseAuth.getInstance();

        /** set adapter **/
        adapter = new ViewPaperAdapter(getSupportFragmentManager(), getResources().getString(R.string.tabRequest),
                getResources().getString(R.string.tabChat), getResources().getString(R.string.tabFriend));
        viewPager.setAdapter(adapter);

        /** set viewpager into tab layout **/
        tabLayout.setupWithViewPager(viewPager);

    }

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseUser currentUser = auth.getCurrentUser();
        if ( currentUser == null ) {
            StartToStart();
        }

    }

    private void StartToStart() {

        Intent intent = new Intent(MainActivity.this, StartActivity.class);
        startActivity(intent);
        finish();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.menu, menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch ( item.getItemId() ) {

            case R.id.mn_acsetting:
                Intent intent = new Intent(MainActivity.this, SettingActivity.class);
                startActivity(intent);
                break;
            case R.id.mn_alluser:
                Intent intent1 = new Intent(MainActivity.this, UsersActivity.class);
                startActivity(intent1);
                break;
            case R.id.mn_logout:
                FirebaseAuth.getInstance().signOut();
                StartToStart();
                break;

        }

        return super.onOptionsItemSelected(item);
    }

}
