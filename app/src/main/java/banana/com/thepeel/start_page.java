package banana.com.thepeel;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.google.android.gms.maps.GoogleMap;

import banana.com.thepeel.fragments.map_page_fragment;
import banana.com.thepeel.fragments.profile_page_fragment;
import banana.com.thepeel.fragments.settings_page_fragment;
import banana.com.thepeel.fragments.statistics_page_fragment;

public class start_page extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_start_page);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setBackgroundColor(Color.rgb(100, 170, 100));

        setTitle("The Peel");
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
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
        getMenuInflater().inflate(R.menu.start_page_option_menu, menu);
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

        }else if(id == R.id.map_view_option_terrain){
            map_page_fragment.change_map_type(GoogleMap.MAP_TYPE_TERRAIN);
        }else if(id == R.id.map_view_option_satellite){
            map_page_fragment.change_map_type(GoogleMap.MAP_TYPE_SATELLITE);
        }else if(id == R.id.map_view_option_normal){
            map_page_fragment.change_map_type(GoogleMap.MAP_TYPE_NORMAL);
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        android.support.v4.app.Fragment fragment = null;
        Class fragmentClass = null;
        FragmentManager fm = getSupportFragmentManager();

        if (id == R.id.nav_maps) {
            fragmentClass = map_page_fragment.class;
        } else if (id == R.id.nav_profile) {
            fragmentClass = profile_page_fragment.class;
        } else if (id == R.id.nav_statistics) {
            fragmentClass = statistics_page_fragment.class;
        }else if(id == R.id.nav_settings){
            fragmentClass = settings_page_fragment.class;
        }

        try{
            fragment = (android.support.v4.app.Fragment)fragmentClass.newInstance();
        }catch (Exception x){
            x.printStackTrace();
        }

        fm.beginTransaction().replace(R.id.content_start_page, fragment).commit();
        setTitle(item.getTitle());
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
