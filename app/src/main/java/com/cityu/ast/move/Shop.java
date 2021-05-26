package com.cityu.ast.move;

import android.accounts.Account;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.LocalBroadcastManager;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

public class Shop extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    private String displayName, email;
    private Double credits;
    TextView usernameDisplay;
    TextView emailDisplay;
    TextView creditsDisplay;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shop);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        mMessageReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String message = intent.getStringExtra("message");
                final int movieID = intent.getIntExtra("movieID", 0);
                Snackbar mySnackbar = Snackbar.make(findViewById(R.id.drawer_layout), message, Snackbar.LENGTH_LONG);
                mySnackbar.setActionTextColor(Color.rgb(198, 179, 255));
                mySnackbar.setAction("View Now", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent in = new Intent(Shop.this, SingleMovie.class);
                        in.putExtra("ID", movieID);
                        startActivity(in);
                    }
                });
                mySnackbar.show();
            }
        };

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        Intent myIntent = getIntent();
        displayName = Session.getName();
        email = Session.getEmail();
        credits = Session.getCredits();

        View header = navigationView.getHeaderView(0);
        usernameDisplay = (TextView) header.findViewById(R.id.textView_UsernameDisplay);
        emailDisplay = (TextView) header.findViewById(R.id.textView_UserEmailDisplay);
        creditsDisplay = (TextView) header.findViewById(R.id.textView_UserCredits);
        usernameDisplay.setText(displayName);
        emailDisplay.setText(email);
        creditsDisplay.setText("$  " + credits);

        if (savedInstanceState == null) {
            navigationView.getMenu().getItem(0).setChecked(true);
            onNavigationItemSelected(navigationView.getMenu().getItem(0));
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        displayName = Session.getName();
        email = Session.getEmail();
        credits = Session.getCredits();
        usernameDisplay.setText(displayName);
        emailDisplay.setText(email);
        creditsDisplay.setText("$  " + credits);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.shop, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        Fragment fragment = null;

        if (id == R.id.nav_home) {
            fragment = new Home();
        } else if (id == R.id.nav_movies) {
            fragment = new Movie();
        } else if (id == R.id.nav_tv) {
            fragment = new Movie();
            Bundle bundle = new Bundle();
            bundle.putString("type", "tv");
            fragment.setArguments(bundle);
        } else if (id == R.id.nav_search) {
            fragment = new Search();
        } else if (id == R.id.nav_shopping_cart) {
            fragment = new ShoppingCart();
        } else if (id == R.id.nav_watchlist) {
            fragment = new Watchlist();
        } else if (id == R.id.nav_purchase_history) {
            fragment = new PurchaseHistory();
        } else if (id == R.id.nav_account) {
            Intent in = new Intent(Shop.this, Settings.class);
            startActivity(in);
            return true;
        }
        if (fragment != null) {
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.content_frame, fragment);
            ft.commit();
        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private BroadcastReceiver mMessageReceiver = null;

    @Override
    public void onResume() {
        super.onResume();
        LocalBroadcastManager.getInstance(this).registerReceiver(mMessageReceiver, new IntentFilter("EVENT_SNACKBAR"));
    }
}
