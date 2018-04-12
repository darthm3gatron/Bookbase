package com.bookbase.app.mainscreen;

import android.content.Context;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.bookbase.app.R;
import com.bookbase.app.aboutScreen.AboutFragment;
import com.bookbase.app.library.BooksFragment;
import com.bookbase.app.library.viewBook.ViewBookFragment;
import com.bookbase.app.model.api.BooksApiCallback;
import com.bookbase.app.model.api.GoogleBooksApi;
import com.bookbase.app.model.entity.Book;
import com.bookbase.app.reports.ReportFragment;
import com.bookbase.app.settings.SettingsFragment;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Response;

public class HomeScreen extends AppCompatActivity implements
        BooksFragment.OnFragmentInteractionListener,
        ReportFragment.OnFragmentInteractionListener,
        SettingsFragment.OnFragmentInteractionListener,
        AboutFragment.OnFragmentInteractionListener,
        ViewBookFragment.OnFragmentInteractionListener {

    private static Context context;

    @BindView(R.id.navigation_drawer) DrawerLayout drawer;
    @BindView(R.id.toolbar) Toolbar toolbar;
    @BindView(R.id.toolbar_title) TextView toolbarTitle;
    @BindView(R.id.nav_view) NavigationView navDrawer;

    private ActionBarDrawerToggle drawerToggle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        context = getApplicationContext();
        
        // Setup toolbar and nav drawer.
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        drawerToggle = setUpDrawerToggle();
        setupDrawerContent(navDrawer);
        navDrawer.inflateHeaderView(R.layout.nav_header);

        drawer.addDrawerListener(drawerToggle);
        selectDrawerItem(navDrawer.getMenu().getItem(0));

    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState){
        super.onPostCreate(savedInstanceState);
        drawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig){
        super.onConfigurationChanged(newConfig);
        drawerToggle.onConfigurationChanged(newConfig);
    }


    private void setupDrawerContent(NavigationView navView){
        navView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                        selectDrawerItem(item);
                        return true;
                    }
                });
    }

    public void selectDrawerItem(MenuItem item){

        // Create fragment class and specify which fragment is instantiated based on edit_book_menu_options option.
        Fragment fragment = null;
        Class fragmentClass;

        switch(item.getItemId()){
            case R.id.nav_books:
                fragmentClass = BooksFragment.class;
                break;
            case R.id.nav_settings:
                fragmentClass = SettingsFragment.class;
                break;
            case R.id.nav_about:
                fragmentClass = AboutFragment.class;
                break;
            default:
                fragmentClass = BooksFragment.class;
        }

        try{
            fragment = (Fragment) fragmentClass.newInstance();
        } catch(InstantiationException e1){
            e1.printStackTrace();
        } catch(IllegalAccessException e2){
            e2.printStackTrace();;
        }

        // Insert fragment.
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .setCustomAnimations(R.anim.fade_in, R.anim.fade_out)
                .replace(R.id.content_frame, fragment)
                .addToBackStack(null)
                .commit();

        // Update UI to indicate currently selected item.
        item.setChecked(true);
        setTitle(item.getTitle());
        drawer.closeDrawers();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        return drawerToggle.onOptionsItemSelected(item) || super.onOptionsItemSelected(item);
    }

    private ActionBarDrawerToggle setUpDrawerToggle(){
        return new ActionBarDrawerToggle(this, drawer, toolbar,
                R.string.drawer_open, R.string.drawer_close);
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    @Override
    public void setTitle(CharSequence title) {
        toolbarTitle.setText(title);
    }

    public static Context getContext(){
        return context;
    }
}