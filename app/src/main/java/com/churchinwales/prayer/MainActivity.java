package com.churchinwales.prayer;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;

import android.view.Menu;


import com.google.android.material.navigation.NavigationView;

import androidx.core.view.GravityCompat;

import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import static java.lang.String.valueOf;


public class MainActivity extends AppCompatActivity implements
    NavigationView.OnNavigationItemSelectedListener
{

    private AppBarConfiguration mAppBarConfiguration;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Context app_Context = getApplicationContext();
        //Loading Dialogue Start Here
        ResourceLoader.unzipFromAssets(app_Context,"Prayer.zip","");
        ResourceLoader.unzipFromAssets(app_Context,"WelBeiblNet.zip",app_Context.getFilesDir().getPath()+"/JSWORD");

        //setContentView(R.layout.fragment_prayer);

        setContentView(R.layout.activity_main);
        //setContentView(R.layout.fragment_cardview);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //spinner = (ProgressBar) findViewById(R.id.ProgressBar1);
        //spinner.setVisibility(View.GONE);
        /*
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
         */

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.fragment_cardview, R.id.fragment_Prayer, R.id.id_EveningPrayer, R.id.fragment_Lectionary)
                .setOpenableLayout(drawer)
                .build();
        //NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        //NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_container);


        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment);
        NavController navController = navHostFragment.getNavController();
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);
        navigationView.setNavigationItemSelectedListener(this);
        //(onNavigationItemSelected())



    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }

    public boolean onNavigationItemSelected(MenuItem item) {

        int id = item.getItemId();

        Toolbar mToolbar = (Toolbar) findViewById(R.id.toolbar);

        Log.v("TAG", "On Navigation Item Selected");
        Bundle args = new Bundle();

        int destination=R.id.fragment_cardview;

        /*
        if (id == R.id.homeFragment) {
          //fragment = findViewById(R.id.homeFragment);
           destination = R.id.homeFragment;
        }*/

        if (id == R.id.fragment_cardview) {
            //fragment = findViewById(R.id.homeFragment);
            destination = R.id.fragment_cardview;
            mToolbar.setTitle(R.string.app_Welcome);
        }

        if (id == R.id.fragment_Prayer) {

            args.putString("Type","MorningPrayer");
            destination=R.id.fragment_Prayer;
            mToolbar.setTitle(getString(R.string.app_MorningPrayer));
        }

        if (id == R.id.id_EveningPrayer) {
            args.putString("Type","EveningPrayer");
            destination=R.id.fragment_Prayer;
            mToolbar.setTitle(getString(R.string.app_EveningPrayer));
        }

        if(id== R.id.nav_Lectionary) {
            destination = R.id.fragment_Lectionary;
        }
        if(id == R.id.fragment_oremus){
            destination = R.id.fragment_oremus;
        }

        if(id == R.id.fragment_JSWORD) {
            destination = R.id.fragment_JSWORD;
        }


        Navigation.findNavController(this,R.id.nav_host_fragment).navigate(destination,args);


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);

        if(id == R.id.id_EveningPrayer) {
            mToolbar.setTitle(getString(R.string.app_EveningPrayer));
        }

        if(id == R.id.fragment_Prayer) {
            mToolbar.setTitle(getString(R.string.app_MorningPrayer));
        }


        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        AppDebug.log("Menu",valueOf(id));
        if (id == R.id.action_settings) {
            Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
            startActivity(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

}