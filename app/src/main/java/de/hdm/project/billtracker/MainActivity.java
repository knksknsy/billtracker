package de.hdm.project.billtracker;

import android.app.FragmentManager;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;

public class MainActivity extends AppCompatActivity {

    private void navigationView() {

        BottomNavigationView bottomNavigationView = (BottomNavigationView) findViewById(R.id.navigation);
        if (bottomNavigationView != null) {

            // Default item at startup == camera(1)
            Menu menu = bottomNavigationView.getMenu();
            activeFragment(menu.getItem(1));

            // Set action to perform when any navigation item is selected.
            bottomNavigationView.setOnNavigationItemSelectedListener(
                new BottomNavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                    activeFragment(item);
                    return false;
                    }
                }
            );
        }
    }

    /**
     * Switch navigation item when clicked.
     *
     * @param item Item that is active.
     */
    protected void activeFragment(MenuItem item) {

        item.setChecked(true);
        switch (item.getItemId()) {
            case R.id.navigation_chart:
                pushFragment(new ChartFragment());
                break;
            case R.id.navigation_camera:
                pushFragment(new CameraFragment());
                break;
            case R.id.navigation_content:
                pushFragment(new ContentFragment());
                break;
        }
    }

    /**
     * Method to push any fragment into given id.
     *
     * @param fragment An instance of Fragment to show into the given id.
     */
    protected void pushFragment(Fragment fragment) {
        if (fragment == null)
            return;

        FragmentManager fragmentManager = getFragmentManager();
        if (fragmentManager != null) {
            FragmentTransaction transaction = getFragmentManager().beginTransaction();
            if (transaction != null) {
                transaction.replace(R.id.container, fragment);
                transaction.commit();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        navigationView();

        // TODO If user is already logged in, skip LoginActivity
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
    }
}