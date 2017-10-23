package de.hdm.project.billtracker;

import android.content.Intent;
import android.graphics.Camera;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {

            Fragment activeFragment = null;
            switch (item.getItemId()) {
                case R.id.navigation_chart:
                    activeFragment = ChartFragment.newInstance();
                    //item.setIcon(R.color.colorAccent);
                    item.setChecked(false);
                    break;
                case R.id.navigation_camera:
                    activeFragment = CameraFragment.newInstance();
                    break;
                case R.id.navigation_folder:
                    activeFragment = FolderFragment.newInstance();
                    break;
            }
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.container, activeFragment);
            transaction.commit();
            return true;
        }

    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        // set navItem to cameraFragment
        navigation.getMenu().getItem(1).setChecked(true);
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        //transaction.replace(R.id.container, IntroFragment.newInstance());
        transaction.replace(R.id.container, CameraFragment.newInstance());
        transaction.commit();
    }
}